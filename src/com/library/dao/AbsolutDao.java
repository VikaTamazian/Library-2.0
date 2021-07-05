package com.library.dao;

import java.util.List;
import java.util.Optional;

public interface AbsolutDao<K, E> {

    boolean delete(K value);

    E save(E entity);

    void update(E entity);

    Optional<E> findById(K value);

    List<E> findAll();

}
