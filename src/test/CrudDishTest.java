package test;

import dao.DishDao;
import dao.mapper.UnitMapper;
import db.DataSource;
import entity.Dish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CrudDishTest implements CrudRestaurantManagementTest<Dish>{

    private DishDao dishDao;

    @BeforeEach
    public void setUp() {
        // Initialize BookDao, connexion, et Unitmapper
        DataSource dataSource = new DataSource();
        UnitMapper unitMapper = new UnitMapper();
        dishDao = new DishDao();
    }

    @Override
    public void findAll_ok() {
        List<Dish> dishes = dishDao.findAll(1, 1);
        assertNotNull(dishes);
        assertFalse(dishes.isEmpty());
        assertEquals(1, dishes.size());
        assertTrue(dishes.stream().anyMatch(e-> "hot dog".equals(e.getName())));
    }

    public void getIngredientCostOfOneDish(){
        Dish dish = dishDao.findByName("hot dog");
        assertNotNull(dish);
        assertEquals(5500, dish.getIngredientCost());
    }

    @Override
    public Dish findByName_ok(String DishName) {
        return null;
    }

    @Override
    public Dish save_ok(Dish dish) {
        return null;
    }
}
