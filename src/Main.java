import dao.DishDao;
//import dao.IngredientDao;
import dao.IngredientDao;
import dao.StockMovementDao;
import dao.mapper.Criteria;
import db.DataSource;
import entity.Dish;
import entity.Ingredient;
import entity.StockMovement;
import entity.Unit;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws SQLException {
        DataSource dataSource = new DataSource();
        dataSource.getConnection();
        DishDao dishDao = new DishDao();
        IngredientDao ingredientDao = new IngredientDao();
        StockMovementDao stockMovementDao = new StockMovementDao();
        //System.out.println(stockMovementDao.findByIngredientId("I003"));;
        //ingredientDao.findAll(1, 5).forEach(e -> System.out.println(e.toString()));

//        List<Criteria> criterias = new ArrayList<>();
//        criterias.add(new Criteria("name", "%e%", "ILIKE", "AND", "DESC"));
//        criterias.add(new Criteria("last_modification_date", LocalDateTime.of(2025, 2, 25, 0, 0), ">", null, "DESC"));
//        criterias.add(new Criteria("unit_price", 1000.0, "=", "AND", "ASC"));
//
//        List<Ingredient> ingredients = ingredientDao.filterByCriteria(2, 1, criterias);
//        ingredients.forEach(e -> System.out.println(e.toString()));

//        Ingredient newEgg = new Ingredient();
//        newEgg.setId("I003");  // ID unique pour l'ingrédient
//        newEgg.setUnit(Unit.U);  // Assurez-vous d'utiliser l'unité correcte
//        newEgg.setUnitPrice(1400.0);  // Utilisez le prix de 1400
//        newEgg.setLastModificationDate(LocalDateTime.now());  // Date de modification actuelle
//        Ingredient savedIngredient = ingredientDao.save(newEgg);  // Enregistrer l'ingrédient

        //dishDao.findAll(1, 10, LocalDateTime.now()).forEach(e -> System.out.println(e.toString()));
        //dishDao.findAllIngredientInsideADish(1).forEach(e -> System.out.println(e.toString()));
        // dishDao.findAll(1, 2).forEach(e -> System.out.println(e.toString()));
        //System.out.println(dishDao.findByName("hot dog"));

        //List<Ingredient> ingredients = new ArrayList<>();
//        Ingredient ingredient0 = new Ingredient("I003", "egg", LocalDateTime.now(), 1000, 1, Unit.U);
//        Ingredient ingredient1 = new Ingredient("I002", "oil", LocalDateTime.now(), 10000, 0.25, Unit.L);
//        ingredients.add(ingredient0);
//        ingredients.add(ingredient1);
        //Dish newDish = new Dish("D002","homelette", 15000, ingredients);
        //dishDao.save(newDish);
        //dishDao.delete("D002");
        //dishDao.findAll(1, 2).forEach(e -> System.out.println(e.toString()));

//        Ingredient newIngredient = new Ingredient(
//                "I005",  // ID de l'ingrédient
//                "Tomato", // Nom de l'ingrédient
//                LocalDateTime.now(), // Date de modification actuelle
//                500, // Prix par unité
//                Unit.U // Unité (Enum)
//        );
//
//        ingredientDao.save(newIngredient);

//        StockMovementDao stockMovementDao = new StockMovementDao();
//
//        // Exemple : récupérer le mouvement de stock pour l'ingrédient "Oeuf" (ID = "I001")
//            String ingredientId = "I001";
//            List<StockMovement> movements = stockMovementDao.findByIngredientId("I001");
//           movements.forEach(e -> System.out.println(e.toString()));
//
//            System.out.println(stockMovementDao.findByIngredientId("I001"));

    }
}

//Ingredient ingredient = new Ingredient(
//        rs.getString("id"),
//        rs.getString("name"),
//        rs.getObject("last_modification_date", Timestamp.class).toLocalDateTime(),
//        rs.getInt("unit_price"),
//        rs.getDouble("quantity"),

//
//gest : RASOLOARINAIVO Bodohasina
//        Bodohasina.RASOLOARINAIVO@bni.mg
//        ag : 02022 667 00