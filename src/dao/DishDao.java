package dao;

import dao.mapper.Criteria;
import dao.mapper.UnitMapper;
import db.DataSource;
import entity.Dish;
import entity.Ingredient;
import entity.Unit;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.sql.Connection;
import java.util.stream.Collectors;

public class DishDao implements CrudRestaurantManagement<Dish>{

    DataSource dataSource = new DataSource();
    Connection connection = dataSource.getConnection();

    private final UnitMapper unitMapper = new UnitMapper();

    @Override
//    public List<Dish> findAll(int page, int pageSize, LocalDateTime dateTime) {
//        List<Dish> dishes = new ArrayList<>();
//        if(page < 1){
//            throw new IllegalArgumentException("Page must be greater than 0 but actual page is " + page);
//        }
//        if(connection!=null){
//            try {
//                String select = "select id, name, unit_price from dish limit ? offset ?";
//                PreparedStatement preparedStatement = connection.prepareStatement(select);
//                preparedStatement.setInt(1, pageSize);
//                preparedStatement.setInt(2, pageSize * (page - 1));
//                try(ResultSet resultSet = preparedStatement.executeQuery()){
//                    dishes = mapDishFromResultSet(resultSet, dateTime);
//                }
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return dishes;
//    }
//
//    private List<Dish> mapDishFromResultSet(ResultSet rs, LocalDateTime dateTime) throws SQLException {
//        List<Dish> dishes = new ArrayList<>();
//        while (rs.next()) {
//            Dish dish = new Dish();
//            dish.setId(rs.getString("id"));
//            dish.setName(rs.getString("name"));
//            dish.setUnitPrice(rs.getDouble("unit_price"));
//            dish.setIngredients(findAllIngredientInsideADish(rs.getString("id")));
//            dish.setProductPrice(dish.getIngredientCost(dateTime));
//            dishes.add(dish);
//        }
//        return dishes;
//    }
//
//    public List<Ingredient> findAllIngredientInsideADish(String idDish){
//        List<Ingredient> ingredients = new ArrayList<>();
//        if(connection!=null){
//            String select ="select ingredient.name, ingredient_cost.id,  ingredient_cost.unit_price, ingredient_cost.unit, ingredient_cost.last_modification_date, dish_ingredient.quantity from ingredient "+
//            "join ingredient_cost on ingredient.id=ingredient_cost.id join dish_ingredient on ingredient.id=dish_ingredient.ingredient_id where dish_ingredient.dish_id=?;";
//            try{
//                PreparedStatement preparedStatement = connection.prepareStatement(select);
//                preparedStatement.setString(1, idDish);
//                try (ResultSet resultSet = preparedStatement.executeQuery()){
//                    ingredients = mapIngredientFromResultSet(resultSet);
//                }
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return ingredients;
//    }
//
//    private List<Ingredient> mapIngredientFromResultSet(ResultSet rs) throws SQLException {
//        List<Ingredient> ingredients = new ArrayList<>();
//
//        while (rs.next()) {
//            List<LocalDateTime> modificationDates = new ArrayList<>();
//            Array sqlArrayDate = rs.getArray("last_modification_date");
//            if (sqlArrayDate != null) {
//                Timestamp[] timestamps = (Timestamp[]) sqlArrayDate.getArray();
//                for (Timestamp timestamp : timestamps) {
//                    modificationDates.add(timestamp.toLocalDateTime());
//                }
//            }
//
//            List<Double> unitPrices = new ArrayList<>();
//            Array sqlArrayPrice = rs.getArray("unit_price");
//            if (sqlArrayPrice != null) {
//                Double[] prices = (Double[]) sqlArrayPrice.getArray();
//                unitPrices = Arrays.asList(prices);
//            }
//
//            Ingredient ingredient = new Ingredient(
//                    rs.getString("id"),
//                    rs.getString("name"),
//                    List.of(modificationDates.getLast()),
//                    List.of(unitPrices.getLast()),
//                    rs.getDouble("quantity"),
//                    unitMapper.mapFromResultSet(rs.getString("unit"))
//            );
//            ingredients.add(ingredient);
//        }
//        return ingredients;
//    }

    public List<Dish> findAll(int page, int pageSize, LocalDateTime dateTime) {
        List<Dish> dishes = new ArrayList<>();
        if(page < 1){
            throw new IllegalArgumentException("Page must be greater than 0 but actual page is " + page);
        }
        if(connection != null){
            try {
                String select = "select id, name, unit_price from dish limit ? offset ?";
                PreparedStatement preparedStatement = connection.prepareStatement(select);
                preparedStatement.setInt(1, pageSize);
                preparedStatement.setInt(2, pageSize * (page - 1));
                try(ResultSet resultSet = preparedStatement.executeQuery()){
                    dishes = mapDishFromResultSet(resultSet, dateTime);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return dishes;
    }

    private List<Dish> mapDishFromResultSet(ResultSet rs, LocalDateTime dateTime) throws SQLException {
        List<Dish> dishes = new ArrayList<>();
        while (rs.next()) {
            Dish dish = new Dish();
            dish.setId(rs.getString("id"));
            dish.setName(rs.getString("name"));
            dish.setUnitPrice(rs.getDouble("unit_price"));
            dish.setIngredients(findAllIngredientInsideADish(rs.getString("id"), dateTime));
            dish.setProductPrice(dish.getIngredientCost(dateTime));
            dishes.add(dish);
        }
        return dishes;
    }

    public List<Ingredient> findAllIngredientInsideADish(String idDish, LocalDateTime dateRequested) {
        List<Ingredient> ingredients = new ArrayList<>();
        if(connection != null){
            String select = "SELECT i.id, i.name, ic.unit_price, ic.unit, ic.last_modification_date, di.quantity " +
                    "FROM ingredient i " +
                    "JOIN ingredient_cost ic ON i.id = ic.id " +
                    "JOIN dish_ingredient di ON i.id = di.ingredient_id " +
                    "WHERE di.dish_id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(select);
                preparedStatement.setString(1, idDish);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    ingredients = mapIngredientFromResultSet(resultSet, dateRequested);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return ingredients;
    }

    private List<Ingredient> mapIngredientFromResultSet(ResultSet rs, LocalDateTime dateRequested) throws SQLException {
        List<Ingredient> ingredients = new ArrayList<>();

        while (rs.next()) {
            java.sql.Array priceArray = rs.getArray("unit_price");
            Double[] priceValues = (Double[]) priceArray.getArray();
            List<Double> unitPrices = Arrays.asList(priceValues);

            java.sql.Array dateArray = rs.getArray("last_modification_date");
            Timestamp[] timestampValues = (Timestamp[]) dateArray.getArray();
            List<LocalDateTime> lastModificationDates = Arrays.stream(timestampValues)
                    .map(Timestamp::toLocalDateTime)
                    .toList();

            Ingredient ingredient = new Ingredient(
                    rs.getString("id"),
                    rs.getString("name"),
                    lastModificationDates,
                    unitPrices,
                    rs.getDouble("quantity"),
                    unitMapper.mapFromResultSet(rs.getString("unit"))
            );
            ingredients.add(ingredient);
        }
        return ingredients;
    }



    @Override
    public Dish findByName(String dishName) {
        Dish dish = new Dish();
        if(connection!=null){
            try {
                String select = "select id, name, unit_price from dish where name = ?;";
                PreparedStatement preparedStatement = connection.prepareStatement(select);
                preparedStatement.setString(1, dishName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    dish.setId(resultSet.getString("id"));
                    dish.setName(resultSet.getString("name"));
                    dish.setUnitPrice(resultSet.getDouble("unit_price"));
                    dish.setIngredients(findAllIngredientInsideADish(resultSet.getString("id"), LocalDateTime.now()));
                    dish.setProductPrice(dish.getIngredientCost(LocalDateTime.now()));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return dish;
    }

    @Override
    public Dish save(Dish dish) {
        if (connection != null) {
            try {
                String query = """
                INSERT INTO dish (id, name, unit_price)
                VALUES (?, ?, ?)
                ON CONFLICT (name) DO UPDATE
                SET id = EXCLUDED.id, name = EXCLUDED.name, unit_price = EXCLUDED.unit_price
                RETURNING id
            """;

                try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                    preparedStatement.setString(1, dish.getId());
                    preparedStatement.setString(2, dish.getName());
                    preparedStatement.setDouble(3, dish.getUnitPrice());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        String dishId = resultSet.getString("id");
                        dish.setId(dishId);
                       if (dish.getIngredients() != null && !dish.getIngredients().isEmpty()) {
                            saveDishIngredients(dishId, dish.getIngredients());
                       }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return dish;
    }

    private void saveDishIngredients(String dishId, List<Ingredient> newIngredients) throws SQLException {
        String selectQuery = "SELECT ingredient_id FROM dish_ingredient WHERE dish_id = ?";
        Set<String> existingIngredients = new HashSet<>();

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, dishId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                existingIngredients.add(resultSet.getString("ingredient_id"));
            }
        }

        List<Ingredient> ingredientsToUpdate = newIngredients.stream()
                .filter(ingredient -> existingIngredients.contains(ingredient.getId()))
                .toList();

        List<Ingredient> ingredientsToAdd = newIngredients.stream()
                .filter(ingredient -> !existingIngredients.contains(ingredient.getId()))
                .toList();

        updateIngredientsInDish(dishId, ingredientsToUpdate);
        addIngredientsToDish(dishId, ingredientsToAdd);
    }

    private void updateIngredientsInDish(String dishId, List<Ingredient> ingredientsToUpdate) throws SQLException {
        String upsertQuery = """
        INSERT INTO dish_ingredient (dish_id, ingredient_id, quantity)
        VALUES (?, ?, ?)
        ON CONFLICT (dish_id, ingredient_id) DO UPDATE
        SET quantity = EXCLUDED.quantity where dish_id = ? and ingredient_id=?
    """;

        try (PreparedStatement updateStatement = connection.prepareStatement(upsertQuery)) {
            for (Ingredient ingredient : ingredientsToUpdate) {
                updateStatement.setString(1, dishId);
                updateStatement.setString(2, ingredient.getId());
                updateStatement.setDouble(3, ingredient.getQuantity());
                updateStatement.setString(4, dishId);
                updateStatement.setString(5, ingredient.getId());
                updateStatement.addBatch();
            }
            updateStatement.executeBatch();
        }
    }

    public void addIngredientsToDish(String dishId, List<Ingredient> ingredientsToAdd) throws SQLException {
        String insertQuery = "INSERT INTO dish_ingredient (dish_id, ingredient_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
            for (Ingredient ingredient : ingredientsToAdd) {
                insertStmt.setString(1, dishId);
                insertStmt.setString(2, ingredient.getId());
                insertStmt.setDouble(3, ingredient.getQuantity());
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        }
    }

    public void removeIngredientsFromDish(String dishId, List<Ingredient> ingredientsToRemove) throws SQLException {
        String deleteQuery = "DELETE FROM dish_ingredient WHERE dish_id = ? AND ingredient_id = ?";

        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
            ingredientsToRemove.forEach(ingredient -> {
                try {
                    deleteStmt.setString(1, dishId);
                    deleteStmt.setString(2, ingredient.getId());
                    deleteStmt.addBatch();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            deleteStmt.executeBatch();
        }
    }

    @Override
    public void delete(String dishId) {
        if(connection!=null){
            String delete = "DELETE FROM dish WHERE id= ?";
            try(PreparedStatement deleteStmt = connection.prepareStatement(delete)){
                deleteStmt.setString(1, dishId);
                deleteStmt.executeQuery();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
