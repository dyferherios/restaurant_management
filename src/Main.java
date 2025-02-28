import dao.DishDao;
//import dao.IngredientDao;
import dao.IngredientDao;
import dao.mapper.Criteria;
import db.DataSource;
import entity.Dish;
import entity.Ingredient;
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
        //ingredientDao.findAll(1, 5);

        List<Criteria> criterias = new ArrayList<>();
        criterias.add(new Criteria("name", "%e%", "ILIKE", "AND", null));  // Filtrer par nom avec un LIKE
        criterias.add(new Criteria("unit_price", 1200, ">=", "AND", null));  // Filtrer par prix (dernière valeur du tableau >= 1000)
        criterias.add(new Criteria("last_modification_date", LocalDateTime.of(2025, 2, 25, 0, 0), ">", "AND", null));  // Filtrer par date (dernière valeur du tableau > date donnée)

        List<Ingredient> filteredIngredients = ingredientDao.filterByCriteria(0, 10, criterias);
        filteredIngredients.forEach(e -> System.out.println(e.toString()));

        Ingredient newEgg = new Ingredient();
        newEgg.setId("I003");  // ID unique pour l'ingrédient
        newEgg.setUnit(Unit.U);  // Assurez-vous d'utiliser l'unité correcte
        newEgg.setUnitPrice(1400.0);  // Utilisez le prix de 1400
        newEgg.setLastModificationDate(LocalDateTime.now());  // Date de modification actuelle

        Ingredient savedIngredient = ingredientDao.save(newEgg);

        //ingredientDao.findAll(1, 10).forEach(e -> System.out.println(e.toString()));
        //dishDao.findAllIngredientInsideADish(1).forEach(e -> System.out.println(e.toString()));
        //dishDao.findAll(1, 2).forEach(e -> System.out.println(e.toString()));
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