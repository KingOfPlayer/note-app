package com.note_app.commonutils.generic;

import java.util.List;
import java.util.Optional;

public interface GenericRepository<T extends BaseEntity<ID>, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    List<T> findAll(int page, int size);

    long count();

    boolean existsById(ID id);

    void deleteById(ID id);
}
