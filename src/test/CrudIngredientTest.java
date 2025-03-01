package test;

import dao.IngredientDao;
import dao.StockMovementDao;
import dao.mapper.Criteria;
import entity.Ingredient;
import entity.MovementType;
import entity.StockMovement;
import entity.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CrudIngredientTest implements CrudRestaurantManagementTest<Ingredient>{
    private IngredientDao ingredientDao;
    private StockMovementDao stockMovementDao;

    @BeforeEach
    void setUp() {
        // Initialisation des ingrédients avec leur stock initial
        stockMovementDao = new StockMovementDao();
        ingredientDao = new IngredientDao();
    }

    @Override
    @Test
    public void findAll_ok() {
        List<Criteria> criterias = new ArrayList<>();
        criterias.add(new Criteria("name", "%e%", "ILIKE", "AND", "DESC"));
        criterias.add(new Criteria("last_modification_date", LocalDateTime.of(2025, 2, 25, 0, 0), ">", null, "DESC"));
        criterias.add(new Criteria("unit_price", 1000.0, "=", "AND", "ASC"));

        List<Ingredient> ingredients = ingredientDao.filterByCriteria(2, 1, criterias);
        assertEquals(1, ingredients.size());
        assertFalse(ingredients.isEmpty());
        assertTrue(ingredients.stream().anyMatch(e -> "brade".equals(e.getName())));
    }

    @Override
    @Test
    public void findByName_ok() {
        Ingredient ingredient = ingredientDao.findByName("egg");
        assertNotNull(ingredient);
        assertEquals("egg", ingredient.getName());
    }


    @Override
    public void save_ok() {

    }



    @Test
    void getAvailableQuantity_OK() {
        LocalDateTime today = LocalDateTime.now();
        Ingredient egg = ingredientDao.findByName("egg");
        assertNotNull(egg);
        assertEquals(80.0, egg.getAvailableQuantity(today));

        Ingredient brade = ingredientDao.findByName("brade");
        assertNotNull(brade);
        assertEquals(30.0, brade.getAvailableQuantity(today));

        Ingredient oil = ingredientDao.findByName("oil");
        assertNotNull(oil);
        assertEquals(20.0, oil.getAvailableQuantity(today));

        Ingredient saucisse = ingredientDao.findByName("saucisse");
        assertNotNull(saucisse);
        assertEquals(10000.0, saucisse.getAvailableQuantity(today));
    }

}


