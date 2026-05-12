package com.note_app.commonutils.generic;

import com.note_app.commonutils.exception.ErrorMessages;
import com.note_app.commonutils.exception.NotFoundException;

import java.util.List;

public abstract class AbstractCrudService<T extends BaseEntity<ID>, ID> implements GenericService<T, ID> {

    protected final GenericRepository<T, ID> repository;

    protected AbstractCrudService(GenericRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public T create(T entity) {
        beforeCreate(entity);
        T saved = repository.save(entity);
        afterCreate(saved);
        return saved;
    }

    @Override
    public T update(ID id, T entity) {
        T existing = getById(id);
        entity.setId(existing.getId());
        beforeUpdate(existing, entity);
        return repository.save(entity);
    }

    @Override
    public T getById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.withId(entityName() + " bulunamadi", id)));
    }

    @Override
    public List<T> getAll() {
        return repository.findAll();
    }

    @Override
    public PageResponse<T> getPage(int page, int size) {
        if (page < 0) page = 0;
        if (size <= 0 || size > 100) size = 20;
        List<T> items = repository.findAll(page, size);
        long total = repository.count();
        return new PageResponse<>(items, page, size, total);
    }

    @Override
    public void delete(ID id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException(ErrorMessages.withId(entityName() + " bulunamadi", id));
        }
        repository.deleteById(id);
    }

    protected abstract String entityName();

    protected void beforeCreate(T entity) {
    }

    protected void afterCreate(T entity) {
    }

    protected void beforeUpdate(T existing, T incoming) {
    }
}
