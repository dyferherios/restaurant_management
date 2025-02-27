import dao.DishDao;
//import dao.IngredientDao;
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
        //IngredientDao ingredientDao = new IngredientDao();
        //ingredientDao.findById(String.valueOf(1));
        //dishDao.findAllIngredientInsideADish(1).forEach(e -> System.out.println(e.toString()));
        dishDao.findAll(1, 2).forEach(e -> System.out.println(e.toString()));
        System.out.println(dishDao.findByName("hot dog"));

//        List<Ingredient> ingredients = new ArrayList<>();
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