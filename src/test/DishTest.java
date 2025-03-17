package test;

import dao.entity.Dish;
import dao.operations.DishCrudOperations;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DishTest {
    DishCrudOperations subject = new DishCrudOperations();

    @Test
    public void get_total_ingredients_cost_OK() {
        Dish hotDog = subject.findById(1L);

        assertEquals(5500.0, hotDog.getTotalIngredientsCost());
    }
}