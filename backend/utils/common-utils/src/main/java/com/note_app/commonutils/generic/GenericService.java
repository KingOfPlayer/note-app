package com.note_app.commonutils.generic;

import java.util.List;

public interface GenericService<T extends BaseEntity<ID>, ID> {

    T create(T entity);

    T update(ID id, T entity);

    T getById(ID id);

    List<T> getAll();

    PageResponse<T> getPage(int page, int size);

    void delete(ID id);
}
