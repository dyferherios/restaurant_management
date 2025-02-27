package dao;

import dao.mapper.Criteria;
import dao.mapper.UnitMapper;
import db.DataSource;
import entity.Dish;
import entity.Ingredient;
import entity.Unit;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.sql.Connection;

public class DishDao implements CrudRestaurantManagement<Dish>{

    DataSource dataSource = new DataSource();
    Connection connection = dataSource.getConnection();

    private final UnitMapper unitMapper = new UnitMapper();

//    @Override
//    public List<Dish> findAll(int page, int pageSize, List<Criteria> criterias) {
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
//                    dishes = mapDishFromResultSet(resultSet);
//                }
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return dishes;
//    }
//
//    private List<Dish> mapDishFromResultSet(ResultSet rs) throws SQLException {
//        List<Dish> dishes = new ArrayList<>();
//        while (rs.next()) {
//            Dish dish = new Dish();
//            dish.setId(rs.getString("id"));
//            dish.setName(rs.getString("name"));
//            dish.setUnitPrice(rs.getDouble("unit_price"));
//            dish.setIngredients(findAllIngredientInsideADish(rs.getString("id")));
//            dish.setProductPrice(dish.getIngredientCost());
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
//        while (rs.next()) {
//            Ingredient ingredient = new Ingredient(
//                    rs.getString("id"),
//                    rs.getString("name"),
//                    rs.getObject("last_modification_date", Timestamp.class).toLocalDateTime(),
//                    rs.getInt("unit_price"),
//                    rs.getDouble("quantity"),
//                    unitMapper.mapFromResultSet(rs.getString("unit")));
//            ingredients.add(ingredient);
//        }
//        return ingredients;
//    }

    @Override
    public List<Dish> findAll(int page, int pageSize, List<Criteria> criterias) {
        List<Dish> dishes = new ArrayList<>();
        if (page < 1) {
            throw new IllegalArgumentException("Page must be greater than 0 but actual page is " + page);
        }

        if (connection != null) {
            try {
                StringBuilder query = new StringBuilder("SELECT id, name, unit_price FROM dish");
                List<Object> parameters = new ArrayList<>();

                // Gestion des critères
                if (criterias != null && !criterias.isEmpty()) {
                    query.append(" WHERE ");
                    List<String> conditions = new ArrayList<>();

                    for (Criteria criteria : criterias) {
                        switch (criteria.getColumn()) {
                            case "nameDish":
                                conditions.add("name LIKE ?");
                                parameters.add("%" + criteria.getValue() + "%");
                                break;
                            case "unit_price_dish":
                                conditions.add("unit_price <= ?");
                                parameters.add(criteria.getValue());
                                break;
                            default:
                                throw new IllegalArgumentException("Unknown criteria: " + criteria.getColumn());
                        }
                    }
                    query.append(String.join(" AND ", conditions));
                }

                query.append(" LIMIT ? OFFSET ?");
                parameters.add(pageSize);
                parameters.add(pageSize * (page - 1));

                PreparedStatement preparedStatement = connection.prepareStatement(query.toString());

                // Remplissage des paramètres
                for (int i = 0; i < parameters.size(); i++) {
                    preparedStatement.setObject(i + 1, parameters.get(i));
                }

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    dishes = mapDishFromResultSet(resultSet, criterias);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return dishes;
    }

    private List<Dish> mapDishFromResultSet(ResultSet rs, List<Criteria> criterias) throws SQLException {
        List<Dish> dishes = new ArrayList<>();
        while (rs.next()) {
            Dish dish = new Dish();
            dish.setId(rs.getString("id"));
            dish.setName(rs.getString("name"));
            dish.setUnitPrice(rs.getDouble("unit_price"));

            // Récupérer les ingrédients avec les critères filtrants
            List<Ingredient> ingredients = findAllIngredientInsideADish(rs.getString("id"), criterias);
            dish.setIngredients(ingredients);

            // Calculer le prix total en fonction des ingrédients récupérés
            dish.setProductPrice(dish.getIngredientCost());

            dishes.add(dish);
        }
        return dishes;
    }


    public List<Ingredient> findAllIngredientInsideADish(String idDish, List<Criteria> criterias) {
        List<Ingredient> ingredients = new ArrayList<>();
        if (connection != null) {
            // Construction de la requête avec des critères
            StringBuilder select = new StringBuilder("SELECT ingredient.name, ingredient_cost.id, ingredient_cost.unit_price, " +
                    "ingredient_cost.unit, ingredient_cost.last_modification_date, " +
                    "dish_ingredient.quantity " +
                    "FROM ingredient " +
                    "JOIN ingredient_cost ON ingredient.id = ingredient_cost.id " +
                    "JOIN dish_ingredient ON ingredient.id = dish_ingredient.ingredient_id " +
                    "WHERE dish_ingredient.dish_id = ? ");

            List<Object> parameters = new ArrayList<>();
            parameters.add(idDish);

            // Appliquer les critères de filtrage sur les ingrédients
            if (criterias != null && !criterias.isEmpty()) {
                for (Criteria criteria : criterias) {
                    switch (criteria.getColumn()) {
                        case "nameIngredient":
                            select.append("AND ingredient.name LIKE ? ");
                            parameters.add("%" + criteria.getValue() + "%");
                            break;
                        case "last_modification_date":
                            select.append("AND ingredient_cost.last_modification_date <= ? ");
                            parameters.add(criteria.getValue());
                            break;
                        default:
                            throw new IllegalArgumentException("Unknown criteria: " + criteria.getColumn());
                    }
                }
            }

            // Trier par date de modification et récupérer le dernier prix si aucune date n'est spécifiée
            select.append("ORDER BY ingredient_cost.last_modification_date DESC");

            try {
                PreparedStatement preparedStatement = connection.prepareStatement(select.toString());
                for (int i = 0; i < parameters.size(); i++) {
                    preparedStatement.setObject(i + 1, parameters.get(i));
                }

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    ingredients = mapIngredientFromResultSet(resultSet);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return ingredients;
    }


    private List<Ingredient> mapIngredientFromResultSet(ResultSet rs) throws SQLException {
        List<Ingredient> ingredients = new ArrayList<>();
        Map<String, Ingredient> ingredientMap = new HashMap<>();

        while (rs.next()) {
            String ingredientId = rs.getString("id");

            // Récupérer les informations de l'ingrédient
            String name = rs.getString("name");
            LocalDateTime lastModificationDate = rs.getObject("last_modification_date", Timestamp.class).toLocalDateTime();
            int unitPrice = rs.getInt("unit_price");
            double quantity = rs.getDouble("quantity");
            String unit = rs.getString("unit");

            // Vérifier si cet ingrédient existe déjà dans le Map
            if (ingredientMap.containsKey(ingredientId)) {
                Ingredient existingIngredient = ingredientMap.get(ingredientId);
                // Comparer les dates de modification et garder le plus récent
                if (lastModificationDate.isAfter(existingIngredient.getLastModificationDate())) {
                    // Mettre à jour l'ingrédient avec les nouvelles informations
                    existingIngredient.setUnitPrice(unitPrice);
                    existingIngredient.setQuantity(quantity);
                    existingIngredient.setUnit(Unit.valueOf(unit));
                    existingIngredient.setLastModificationDate(lastModificationDate);
                }
            } else {
                // Ajouter l'ingrédient si il n'est pas déjà dans la liste
                Ingredient ingredient = new Ingredient(ingredientId, name, lastModificationDate, unitPrice, quantity, unitMapper.mapFromResultSet(unit));
                ingredientMap.put(ingredientId, ingredient);
            }
        }

        // Ajouter tous les ingrédients dans la liste
        ingredients.addAll(ingredientMap.values());
        return ingredients;
    }




    @Override
    public Dish findByName(String dishName) {
        Dish dish = new Dish();
//        if(connection!=null){
//            try {
//                String select = "select id, name, unit_price from dish where name = ?;";
//                PreparedStatement preparedStatement = connection.prepareStatement(select);
//                preparedStatement.setString(1, dishName);
//                ResultSet resultSet = preparedStatement.executeQuery();
//                if(resultSet.next()){
//                    dish.setId(resultSet.getString("id"));
//                    dish.setName(resultSet.getString("name"));
//                    dish.setUnitPrice(resultSet.getDouble("unit_price"));
//                    dish.setIngredients(findAllIngredientInsideADish(resultSet.getString("id")));
//                    dish.setProductPrice(dish.getIngredientCost());
//                }
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//        }
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
        SET quantity = EXCLUDED.quantity
    """;

        try (PreparedStatement updateStatement = connection.prepareStatement(upsertQuery)) {
            for (Ingredient ingredient : ingredientsToUpdate) {
                updateStatement.setString(1, dishId);
                updateStatement.setString(2, ingredient.getId());
                updateStatement.setDouble(3, ingredient.getQuantity());
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
