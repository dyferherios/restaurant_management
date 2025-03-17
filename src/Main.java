import dao.entity.Criteria.Criteria;
import dao.entity.Ingredient;
import dao.entity.Order;
import dao.entity.Price;
import dao.entity.StockMovement;
import dao.operations.*;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLOutput;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws SQLException {
//        DishCrudOperations dishCrudOperations = new DishCrudOperations();
//        System.out.println(dishCrudOperations.findById(1L).toString());

//        OrderCrudOperations orderCrudOperations = new OrderCrudOperations();
//        //orderCrudOperations.getAll(1, 10).forEach(order -> System.out.println(order.toString()));
//
//        System.out.println( Duration.between(Instant.parse("2025-02-25T07:00:00Z"), Instant.parse("2025-02-25T07:15:00Z")));

        IngredientCrudOperations ingredientCrudOperations = new IngredientCrudOperations();
//
//        ingredientCrudOperations.getAll(1, 10).forEach(ingredient -> System.out.println(ingredient.toString()));
        PriceCrudOperations priceCrudOperations = new PriceCrudOperations();
        List<Criteria> priceCriterias = List.of(
                new Criteria("date", LocalDate.of(2025,3,17),"<",  "AND"),
                new Criteria("date",LocalDate.of(2025,3,15), ">=", "AND"),
                new Criteria("amount", 1020.0, ">=", "AND")
        );
        Map<String, String> mapPrice = Map.of();
        List<Price> prices= priceCrudOperations.filterByIngredientIdAndCriteria(1L, priceCriterias, mapPrice);
        System.out.println(prices.toString());

        StockMovementCrudOperations stockMovementCrudOperations = new StockMovementCrudOperations();
        List<Criteria> stockMovementsCriterias = List.of(
                new Criteria("date", LocalDate.of(2025,3,17),"<=",  "AND"),
                new Criteria("date",LocalDate.of(2025,2,1), ">=", "AND"),
                new Criteria("movement_type", "IN", "ilike", "AND")
        );

        List<StockMovement> stockMovements = stockMovementCrudOperations.filterByIngredientIdByCriteria(1L, stockMovementsCriterias, mapPrice);
        System.out.println(stockMovements.toString());

//        List<Criteria> criterias = List.of(
//                new Criteria("name", "o", "ILIKE", "AND"),
//                new Criteria("required_quantity", 1000.0, ">=", "AND")
//        );

//        Map<String, String> sort = Map.of("i.name", "ASC");
//
//        List<Ingredient> result = ingredientCrudOperations.filterByCriteria(criterias, 1, 10, sort);
//        result.forEach(ingredient -> System.out.println(ingredient.toString()));

    }
}
//  
//gest : RASOLOARINAIVO Bodohasina
//        Bodohasina.RASOLOARINAIVO@bni.mg
//        ag : 02022 667 00