package com.example.backend_vkr.domain.repositories;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void deleteById(ID id);

    <S extends T> List<S> saveAll(Iterable<S> entities);

    void flush();
    void deleteAll(Iterable<? extends T> entities);
    void delete(T entity);

    boolean existsById(ID id);

    long count();
}