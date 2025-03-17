package test;

import dao.entity.Ingredient;
import dao.operations.IngredientCrudOperations;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IngredientTest {
    IngredientCrudOperations subject = new IngredientCrudOperations();

    @Test
    void oeuf_get_available_quantity_OK() {
        Ingredient oeuf = subject.findById(1L);

        assertEquals(80.0, oeuf.getAvailableQuantity());
    }

    @Test
    void pain_get_available_quantity_OK() {
        Ingredient pain = subject.findById(4L);

        assertEquals(30.0, pain.getAvailableQuantity());
    }
}
