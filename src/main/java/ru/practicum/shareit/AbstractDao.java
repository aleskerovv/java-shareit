package ru.practicum.shareit;

import java.util.List;

public interface AbstractDao<T> {

    T getById(Long id);
    List<T> getAll();

    T create(T t);

    T update(T t, Long id);

    void deleteById(Long id);
}
