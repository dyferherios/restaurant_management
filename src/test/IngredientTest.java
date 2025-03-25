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
        assertEquals(80.0, oeuf.getAvailableQuantityAt(Instant.parse("2025-02-20T12:00:00Z")));
    }

    @Test
    void pain_get_available_quantity_OK() {
        Ingredient pain = subject.findById(4L);

        assertEquals(30.0, pain.getAvailableQuantityAt(Instant.parse("2025-02-20T12:00:00Z")));
    }

    @Test
    void save_all_OK() {
        Ingredient newIngredient = new Ingredient(5L, "riz", List.of(), List.of());
        Price price = new Price(6L, newIngredient, 3.5,LocalDate.now());
        StockMovement stockMovement = new StockMovement(8L, newIngredient, 10000.0, Unit.G ,StockMovementType.IN, Instant.parse("2025-03-20T12:13:00Z"));
        newIngredient.setPrices(List.of(price));
        newIngredient.setStockMovements(List.of(stockMovement));
        List<Ingredient> ingredientAdded = subject.saveAll(List.of(newIngredient));
        assertNotNull(ingredientAdded);

        assertTrue(ingredientAdded.stream().allMatch(ingredient -> "riz".equals(ingredient.getName())));

        Ingredient newIngredient1 = new Ingredient(6L, "sel", List.of(), List.of());
        Price price1 = new Price(7L, newIngredient1, 2.5,LocalDate.now());
        StockMovement stockMovement1 = new StockMovement(9L, newIngredient1, 1000.0, Unit.G ,StockMovementType.IN, Instant.parse("2025-03-20T12:13:00Z"));
        newIngredient1.setPrices(List.of(price1));
        newIngredient1.setStockMovements(List.of(stockMovement1));
        List<Ingredient> ingredientAdded1 = subject.saveAll(List.of(newIngredient1));
        assertNotNull(ingredientAdded1);

        assertTrue(ingredientAdded1.stream().allMatch(ingredient -> "sel".equals(ingredient.getName())));

    }
}
