package test;

import java.util.List;

public interface CrudRestaurantManagementTest<T> {
    void findAll_ok();
    void findByName_ok();
    void save_ok();
}
