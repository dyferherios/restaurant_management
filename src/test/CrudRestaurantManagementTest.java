package test;

import java.util.List;

public interface CrudRestaurantManagementTest<T> {
    void findAll_ok();
    T findByName_ok(String tName);
    T save_ok(T t);
}
