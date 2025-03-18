package test;

import dao.entity.Dish;
import dao.operations.DishCrudOperations;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DishTest {
    DishCrudOperations subject = new DishCrudOperations();

    @Test
    public void get_total_ingredients_cost_OK() {
        Dish hotDog = subject.findById(1L);

        //assertEquals(5500.0, hotDog.getTotalIngredientsCost());
        assertEquals(5700.0, hotDog.getTotalIngredientsCost());
    }

    @Test
    public void get_available_quantity_OK(){
        Dish dish = subject.findById(1L);
        assertNotNull(dish);
        assertEquals(5.0, dish.getAvailableQuantity());
    }

    @Test
    public void get_available_quantity_At_OK(){
        Dish dish = subject.findById(1L);
        assertNotNull(dish);
        assertEquals(30.0, dish.getAvailableQuantityAt(Instant.parse("2025-02-28T00:00:00Z")));
    }
}