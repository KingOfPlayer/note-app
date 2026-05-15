package com.note_app.noteservice.repository.jdbc;

import org.springframework.jdbc.core.RowMapper;

import com.note_app.noteservice.entities.Note;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class NoteRowMapper implements RowMapper<Note> {

    @Override
    public Note mapRow(ResultSet rs, int rowNum) throws SQLException {
        Note note = new Note();
        note.setId(rs.getLong("id"));
        note.setUserId(rs.getString("user_id"));
        long categoryId = rs.getLong("category_id");
        note.setCategoryId(rs.wasNull() ? null : categoryId);
        note.setTitle(rs.getString("title"));
        note.setContent(rs.getString("content"));
        note.setColor(rs.getString("color"));
        note.setPinned(rs.getBoolean("is_pinned"));
        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        note.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        note.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        return note;
    }
}
