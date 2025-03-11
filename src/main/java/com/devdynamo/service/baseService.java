package com.devdynamo.service;

import java.util.List;
import java.util.Optional;

public interface baseService<T, Long> {

    T save(T entity);

    Optional<T> getById(Long id);

    List<T> getAll();

    void deleteById(Long id);

    T update(T t);
}