package dao;

import dao.mapper.UnitMapper;
import db.DataSource;
import entity.Ingredient;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IngredientDao implements CrudRestaurantManagement<Ingredient> {
    DataSource dataSource = new DataSource();
    Connection connection = dataSource.getConnection();
    private final UnitMapper unitMapper;

    public IngredientDao() {
        this.unitMapper = new UnitMapper();
    }

    @Override
    public List<Ingredient> findAll(int page, int pageSize) {
        List<Ingredient> ingredients = new ArrayList<>();
        if(connection!=null){
            String select = "select ingredient_cost.id, ingredient.name, ingredient_cost.unit_price, ingredient_cost.unit, ingredient_cost.last_modification_date from ingredient" +
                    " inner join ingredient_cost on ingredient.id=ingredient_cost.id;";
            try{
                Statement statement = connection.createStatement();
                try(ResultSet resultSet = statement.executeQuery(select)){
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
        while (rs.next()) {
            Ingredient ingredient = new Ingredient();
            ingredient.setId(rs.getString("id"));
            ingredient.setName(rs.getString("name"));
            ingredient.setUnit(unitMapper.mapFromResultSet(rs.getString("unit")));
            ingredient.setUnitPrice(rs.getDouble("unit_price"));
            ingredient.setLastModificationDate(rs.getObject("last_modification_date", LocalDateTime.class));
            ingredients.add(ingredient);
        }
        return ingredients;
    }

    @Override
    public Ingredient findByName(String ingredientId) {
        if(connection!=null){
            String select = "select ingredient_cost.id, ingredient.name, ingredient_cost.unit_price, ingredient_cost.unit, ingredient_cost.last_modification_date from ingredient" +
                    " inner join ingredient_cost on ingredient.id=ingredient_cost.id where ingredient.id=?;";
            try{
                PreparedStatement preparedStatement = connection.prepareStatement(select);
                preparedStatement.setInt(1, Integer.parseInt(ingredientId));
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()){
                    return new Ingredient(
                            resultSet.getString("id"),
                            resultSet.getString("name"),
                            resultSet.getObject("last_modification_date", LocalDateTime.class),
                            resultSet.getInt("unit_price"),
                            unitMapper.mapFromResultSet(resultSet.getString("unit"))
                    );
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public Ingredient save(Ingredient newIngredient) {
        if (connection != null) {
            String upSertQuery = "INSERT INTO ingredient_cost (id, unit, last_modification_date, unit_price) " +
                    "VALUES (?, ?, ARRAY[?] || '{}', ARRAY[?] || '{}') " +
                    "ON CONFLICT (id) DO UPDATE " +
                    "SET last_modification_date = ARRAY[?] || ingredient_cost.last_modification_date, " +
                    "unit_price = ARRAY[?] || ingredient_cost.unit_price, " +
                    "unit = EXCLUDED.unit;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(upSertQuery)) {
                Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
                double unitPrice = newIngredient.getUnitPrice();
                String unit = newIngredient.getUnit().name();
                preparedStatement.setString(1, newIngredient.getId());
                preparedStatement.setString(2, unit);
                preparedStatement.setTimestamp(3, currentTimestamp);
                preparedStatement.setDouble(4, unitPrice);
                preparedStatement.setTimestamp(5, currentTimestamp);
                preparedStatement.setDouble(6, unitPrice);
                preparedStatement.executeUpdate();
                return newIngredient;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public void delete(String ingredientId) {

    }
}
