package com.note_app.noteservice.repository.jdbc;

import com.note_app.noteservice.entities.Note;
import com.note_app.noteservice.repository.INoteRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcNoteRepository implements INoteRepository {

    private final JdbcTemplate jdbc;
    private final RowMapper<Note> rowMapper = new NoteRowMapper();

    public JdbcNoteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Note save(Note entity) {
        if (entity.getId() == null) {
            return insert(entity);
        }
        return update(entity);
    }

    private Note insert(Note note) {
        String sql = "INSERT INTO notes (user_id, category_id, title, content, color, is_pinned, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        LocalDateTime now = LocalDateTime.now();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, note.getUserId());
            if (note.getCategoryId() == null) ps.setNull(2, java.sql.Types.BIGINT);
            else ps.setLong(2, note.getCategoryId());
            ps.setString(3, note.getTitle());
            ps.setString(4, note.getContent());
            ps.setString(5, note.getColor());
            ps.setBoolean(6, note.isPinned());
            ps.setTimestamp(7, Timestamp.valueOf(now));
            ps.setTimestamp(8, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);
        if (keyHolder.getKeys() != null) {
            Object idVal = keyHolder.getKeys().get("id");
            if (idVal instanceof Number n) {
                note.setId(n.longValue());
            }
        } else {
            Number key = keyHolder.getKey();
            if (key != null) {
                note.setId(key.longValue());
            }
        }
        note.setCreatedAt(now);
        note.setUpdatedAt(now);
        return note;
    }

    private Note update(Note note) {
        LocalDateTime now = LocalDateTime.now();
        String sql = "UPDATE notes SET category_id = ?, title = ?, content = ?, color = ?, is_pinned = ?, updated_at = ? WHERE id = ?";
        jdbc.update(sql,
                note.getCategoryId(),
                note.getTitle(),
                note.getContent(),
                note.getColor(),
                note.isPinned(),
                Timestamp.valueOf(now),
                note.getId());
        note.setUpdatedAt(now);
        return note;
    }

    @Override
    public Optional<Note> findById(Long id) {
        try {
            Note note = jdbc.queryForObject("SELECT * FROM notes WHERE id = ?", rowMapper, id);
            return Optional.ofNullable(note);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Note> findAll() {
        return jdbc.query("SELECT * FROM notes ORDER BY is_pinned DESC, updated_at DESC", rowMapper);
    }

    @Override
    public List<Note> findAll(int page, int size) {
        return jdbc.query(
                "SELECT * FROM notes ORDER BY is_pinned DESC, updated_at DESC LIMIT ? OFFSET ?",
                rowMapper, size, page * size);
    }

    @Override
    public long count() {
        Long count = jdbc.queryForObject("SELECT COUNT(*) FROM notes", Long.class);
        return count != null ? count : 0L;
    }

    @Override
    public boolean existsById(Long id) {
        Integer found = jdbc.queryForObject("SELECT COUNT(*) FROM notes WHERE id = ?", Integer.class, id);
        return found != null && found > 0;
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("DELETE FROM notes WHERE id = ?", id);
    }

    @Override
    public List<Note> findByUserId(String userId, int page, int size) {
        return jdbc.query(
                "SELECT * FROM notes WHERE user_id = ? ORDER BY is_pinned DESC, updated_at DESC LIMIT ? OFFSET ?",
                rowMapper, userId, size, page * size);
    }

    @Override
    public long countByUserId(String userId) {
        Long count = jdbc.queryForObject("SELECT COUNT(*) FROM notes WHERE user_id = ?", Long.class, userId);
        return count != null ? count : 0L;
    }

    @Override
    public List<Note> findByUserIdAndCategoryId(String userId, Long categoryId) {
        return jdbc.query(
                "SELECT * FROM notes WHERE user_id = ? AND category_id = ? ORDER BY updated_at DESC",
                rowMapper, userId, categoryId);
    }

    @Override
    public List<Note> findByUserIdAndPinned(String userId, boolean pinned) {
        return jdbc.query(
                "SELECT * FROM notes WHERE user_id = ? AND is_pinned = ? ORDER BY updated_at DESC",
                rowMapper, userId, pinned);
    }

    @Override
    public List<Note> searchByTitle(String userId, String keyword) {
        String like = "%" + keyword.toLowerCase() + "%";
        return jdbc.query(
                "SELECT * FROM notes WHERE user_id = ? AND LOWER(title) LIKE ? ORDER BY updated_at DESC",
                rowMapper, userId, like);
    }

    @Override
    public List<Note> searchByContent(String userId, String keyword) {
        String like = "%" + keyword.toLowerCase() + "%";
        return jdbc.query(
                "SELECT * FROM notes WHERE user_id = ? AND LOWER(content) LIKE ? ORDER BY updated_at DESC",
                rowMapper, userId, like);
    }

    @Override
    public List<Note> searchByTitleOrContent(String userId, String keyword) {
        String like = "%" + keyword.toLowerCase() + "%";
        return jdbc.query(
                "SELECT * FROM notes WHERE user_id = ? AND (LOWER(title) LIKE ? OR LOWER(content) LIKE ?) ORDER BY updated_at DESC",
                rowMapper, userId, like, like);
    }
}
