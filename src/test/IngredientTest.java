package test;

import dao.entity.*;
import dao.operations.IngredientCrudOperations;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class IngredientTest {
    IngredientCrudOperations subject = new IngredientCrudOperations();

    @Test
    void oeuf_get_available_quantity_OK() {
        Ingredient oeuf = subject.findById(1L);

        assertEquals(55.0, oeuf.getAvailableQuantity());
    }

    @Test
    void pain_get_available_quantity_OK() {
        Ingredient pain = subject.findById(4L);

        assertEquals(5.0, pain.getAvailableQuantity());
    }

    @Test
    void save_all_OK() {
        Ingredient newIngredient = new Ingredient(5L, "riz", List.of(), List.of());
        Price price = new Price(6L, newIngredient, 3.5,LocalDate.now());
        StockMovement stockMovement = new StockMovement(12L, newIngredient, 10000.0, Unit.G ,StockMovementType.IN, Instant.parse("2025-03-20T12:13:00Z"));
        newIngredient.setPrices(List.of(price));
        newIngredient.setStockMovements(List.of(stockMovement));
        List<Ingredient> ingredientAdded = subject.saveAll(List.of(newIngredient));
        assertNotNull(ingredientAdded);

        assertTrue(ingredientAdded.stream().allMatch(ingredient -> "riz".equals(ingredient.getName())));

    }
}
