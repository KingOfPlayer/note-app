package com.note_app.noteservice.repository.jdbc;

import org.springframework.jdbc.core.RowMapper;

import com.note_app.noteservice.entities.Category;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class CategoryRowMapper implements RowMapper<Category> {

    @Override
    public Category mapRow(ResultSet rs, int rowNum) throws SQLException {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setUserId(rs.getString("user_id"));
        category.setName(rs.getString("name"));
        category.setColor(rs.getString("color"));
        Timestamp created = rs.getTimestamp("created_at");
        Timestamp updated = rs.getTimestamp("updated_at");
        category.setCreatedAt(created != null ? created.toLocalDateTime() : null);
        category.setUpdatedAt(updated != null ? updated.toLocalDateTime() : null);
        return category;
    }
}
