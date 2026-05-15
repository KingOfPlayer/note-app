package com.note_app.noteservice.repository.jdbc;

import com.note_app.noteservice.entities.Category;
import com.note_app.noteservice.repository.ICategoryRepository;
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
public class JdbcCategoryRepository implements ICategoryRepository {

    private final JdbcTemplate jdbc;
    private final RowMapper<Category> rowMapper = new CategoryRowMapper();

    public JdbcCategoryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Category save(Category entity) {
        if (entity.getId() == null) {
            return insert(entity);
        }
        return update(entity);
    }

    private Category insert(Category category) {
        LocalDateTime now = LocalDateTime.now();
        String sql = "INSERT INTO categories (user_id, name, color, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, category.getUserId());
            ps.setString(2, category.getName());
            ps.setString(3, category.getColor());
            ps.setTimestamp(4, Timestamp.valueOf(now));
            ps.setTimestamp(5, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);
        if (keyHolder.getKeys() != null) {
            Object idVal = keyHolder.getKeys().get("id");
            if (idVal instanceof Number n) {
                category.setId(n.longValue());
            }
        } else {
            Number key = keyHolder.getKey();
            if (key != null) {
                category.setId(key.longValue());
            }
        }
        category.setCreatedAt(now);
        category.setUpdatedAt(now);
        return category;
    }

    private Category update(Category category) {
        LocalDateTime now = LocalDateTime.now();
        jdbc.update("UPDATE categories SET name = ?, color = ?, updated_at = ? WHERE id = ?",
                category.getName(), category.getColor(), Timestamp.valueOf(now), category.getId());
        category.setUpdatedAt(now);
        return category;
    }

    @Override
    public Optional<Category> findById(Long id) {
        try {
            Category category = jdbc.queryForObject("SELECT * FROM categories WHERE id = ?", rowMapper, id);
            return Optional.ofNullable(category);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Category> findAll() {
        return jdbc.query("SELECT * FROM categories ORDER BY name ASC", rowMapper);
    }

    @Override
    public List<Category> findAll(int page, int size) {
        return jdbc.query("SELECT * FROM categories ORDER BY name ASC LIMIT ? OFFSET ?",
                rowMapper, size, page * size);
    }

    @Override
    public long count() {
        Long count = jdbc.queryForObject("SELECT COUNT(*) FROM categories", Long.class);
        return count != null ? count : 0L;
    }

    @Override
    public boolean existsById(Long id) {
        Integer found = jdbc.queryForObject("SELECT COUNT(*) FROM categories WHERE id = ?", Integer.class, id);
        return found != null && found > 0;
    }

    @Override
    public void deleteById(Long id) {
        jdbc.update("DELETE FROM categories WHERE id = ?", id);
    }

    @Override
    public List<Category> findByUserId(String userId) {
        return jdbc.query("SELECT * FROM categories WHERE user_id = ? ORDER BY name ASC",
                rowMapper, userId);
    }

    @Override
    public Optional<Category> findByUserIdAndName(String userId, String name) {
        try {
            Category category = jdbc.queryForObject(
                    "SELECT * FROM categories WHERE user_id = ? AND name = ?",
                    rowMapper, userId, name);
            return Optional.ofNullable(category);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
