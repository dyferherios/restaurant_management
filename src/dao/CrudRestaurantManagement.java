package dao;

import dao.mapper.Criteria;

import java.sql.SQLException;
import java.util.List;

public interface CrudRestaurantManagement<T> {
    List<T> findAll(int page, int pageSize);
    T findByName(String tName);
    T save(T t);
    void delete(String Tid);
}
