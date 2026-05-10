package com.note_app.commonutils.generic;

import java.time.LocalDateTime;

public interface BaseEntity<ID> {

    ID getId();

    void setId(ID id);

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();
}
