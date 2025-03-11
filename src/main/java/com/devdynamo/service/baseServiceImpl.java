package com.devdynamo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public class baseServiceImpl<T, ID> implements baseService<T, ID> {

    @Autowired
    protected final JpaRepository<T, ID> repository;

    public baseServiceImpl(JpaRepository<T,ID> jpaRepository){
        this.repository = jpaRepository;
    }
    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<T> getById(ID id) {
        return repository.findById(id);
    }

    @Override
    public List<T> getAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Override
    public T update(T t) {
        return save(t);
    }


}