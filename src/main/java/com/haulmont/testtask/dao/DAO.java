package com.haulmont.testtask.dao;


import com.haulmont.testtask.entity.Entity;
import java.util.List;


public interface DAO<T extends Entity> {

    T persist(T object) throws DAOException;
    List<T> getAll() throws DAOException;
    T getByPrimaryKey(Long key) throws DAOException;
    void update(T object) throws DAOException;
    void delete(T object) throws DAOException;

}
