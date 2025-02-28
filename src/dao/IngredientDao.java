package dao;

import dao.mapper.Criteria;
import dao.mapper.UnitMapper;
import db.DataSource;
import entity.Ingredient;
import entity.Unit;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

            // Récupération du dernier prix connu (au lieu de rs.getArray("unit_price"))
            double lastUnitPrice = rs.getDouble("last_unit_price");
            ingredient.setUnitPrice(lastUnitPrice);

            // Récupération de la dernière date de modification
            Timestamp lastModificationTs = rs.getTimestamp("last_modification_date");
            if (lastModificationTs != null) {
                ingredient.setLastModificationDate(lastModificationTs.toLocalDateTime());
            }

            ingredients.add(ingredient);
        }
        return ingredients;
    }


    public List<Ingredient> filterByCriteria(int page, int pageSize, List<Criteria> criterias) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT ic.id, i.name, ");
        sql.append("ic.unit_price[array_length(ic.unit_price, 1)] AS last_unit_price, ");
        sql.append("ic.unit, ic.last_modification_date[array_length(ic.last_modification_date, 1)] AS last_modification_date ");
        sql.append("FROM ingredient i ");
        sql.append("INNER JOIN ingredient_cost ic ON i.id = ic.id ");

        if (criterias != null && !criterias.isEmpty()) {
            sql.append("WHERE ");
            for (int i = 0; i < criterias.size(); i++) {
                Criteria criteria = criterias.get(i);
                String column = criteria.getColumn();
                Object value = criteria.getValue();
                String operator = criteria.getOperator();
                String logicalOperator = criteria.getLogicalOperator();

                if (column.equals("unit_price")) {
                    column = "ic.unit_price[array_length(ic.unit_price, 1)]";
                } else if (column.equals("last_modification_date")) {
                    column = "ic.last_modification_date[array_length(ic.last_modification_date, 1)]";
                }
                if (value instanceof String) {
                    value = "'" + value + "'";
                } else if (value instanceof LocalDateTime) {
                    value = "'" + ((LocalDateTime) value).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "'";
                }
                sql.append(column).append(" ").append(operator).append(" ").append(value);
                if (i < criterias.size() - 1) {
                    sql.append(" ").append(logicalOperator).append(" ");
                }
            }
        }
        sql.append(" ORDER BY last_modification_date DESC");
        sql.append(" LIMIT ").append(pageSize).append(" OFFSET ").append(page * pageSize);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            ResultSet rs = stmt.executeQuery();
            return mapIngredientFromResultSet(rs);
        }
    }




    @Override
    public Ingredient findByName(String ingredientId) {
//        if(connection!=null){
//            String select = "select ingredient_cost.id, ingredient.name, ingredient_cost.unit_price, ingredient_cost.unit, ingredient_cost.last_modification_date from ingredient" +
//                    " inner join ingredient_cost on ingredient.id=ingredient_cost.id where ingredient.id=?;";
//            try{
//                PreparedStatement preparedStatement = connection.prepareStatement(select);
//                preparedStatement.setInt(1, Integer.parseInt(ingredientId));
//                ResultSet resultSet = preparedStatement.executeQuery();
//                if (resultSet.next()){
//                    Array lastModificationDates = resultSet.getArray("last_modification_date");
//                    return new Ingredient(
//                        resultSet.getString("id"),
//                        resultSet.getString("name"),
//                        resultSet.getObject("last_modification_date", LocalDateTime.class),
//                        resultSet.getInt("unit_price"),
//                        unitMapper.mapFromResultSet(resultSet.getString("unit"))
//                    );
//                }
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
//        }
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
                double unitPrice = newIngredient.getUnitPrice().getFirst();
                Unit unit = unitMapper.mapFromResultSet(newIngredient.getUnit().name());
                preparedStatement.setString(1, newIngredient.getId());
                preparedStatement.setString(2, String.valueOf(unit));
                preparedStatement.setTimestamp(3, currentTimestamp);
                preparedStatement.setDouble(4, unitPrice);
                preparedStatement.setTimestamp(5, currentTimestamp);
                preparedStatement.setDouble(6, unitPrice);
                System.out.println(preparedStatement);
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
