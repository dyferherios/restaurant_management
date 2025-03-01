package test;

import dao.DishDao;
import dao.mapper.UnitMapper;
import db.DataSource;
import entity.Dish;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CrudDishTest implements CrudRestaurantManagementTest<Dish>{

    private DishDao dishDao;

    @BeforeEach
    public void setUp() {
        // Initialize DishDao, connexion, et Unitmapper
        DataSource dataSource = new DataSource();
        UnitMapper unitMapper = new UnitMapper();
        dishDao = new DishDao();
    }

    @Override
    @Test
    public void findAll_ok() {
        List<Dish> dishes = dishDao.findAll(1, 1, LocalDateTime.of(2025, 2, 28, 0, 0));
        assertNotNull(dishes);
        assertFalse(dishes.isEmpty());
        assertEquals(1, dishes.size());
        assertTrue(dishes.stream().anyMatch(e-> "hot dog".equals(e.getName())));
    }

    @Test
    public void getIngredientCostOfOneDish(){
        Dish dish = dishDao.findByName("hot dog");
        assertNotNull(dish);
        assertEquals(5500.0, dish.getIngredientCost(LocalDateTime.now()));
    }

    @Test
    public void getGrossMind_Ok(){
        Dish dish = dishDao.findByName("hot dog");
        assertNotNull(dish);
        assertEquals(9500, dish.getGrossMargin(LocalDateTime.now()));
    }

    @Override
    public void findByName_ok() {

    }

    @Override
    public void save_ok() {

    }
}
