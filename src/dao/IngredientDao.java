package dao;

import dao.mapper.Criteria;
import dao.mapper.UnitMapper;
import db.DataSource;
import entity.Ingredient;
import entity.MovementType;
import entity.StockMovement;
import entity.Unit;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IngredientDao implements CrudRestaurantManagement<Ingredient> {
    DataSource dataSource = new DataSource();
    Connection connection = dataSource.getConnection();
    private final UnitMapper unitMapper;

    public IngredientDao() {
        this.unitMapper = new UnitMapper();
    }


    public List<Ingredient> findAllIngredient(int page, int pageSize) {
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

    private List<Ingredient> mapIngredientFromResultSet(ResultSet resultSet) throws SQLException {
        List<Ingredient> ingredients = new ArrayList<>();
        while (resultSet.next()) {
            Array lastModificationDatesArray = resultSet.getArray("last_modification_date");
            Array unitPriceArray = resultSet.getArray("unit_price");
            Timestamp[] timestamps = (Timestamp[]) lastModificationDatesArray.getArray();
            List<LocalDateTime> lastModificationDates = Arrays.stream(timestamps)
                    .map(Timestamp::toLocalDateTime)
                    .collect(Collectors.toList());

            Double[] unitPricesArray = (Double[]) unitPriceArray.getArray();
            List<Double> unitPrices = Arrays.asList(unitPricesArray);
            Ingredient ingredient = new Ingredient();
            ingredient.setId(resultSet.getString("id"));
            ingredient.setName(resultSet.getString("name"));
            ingredient.setLastModificationDate(lastModificationDates);
            ingredient.setUnitPrice(unitPrices);
            ingredient.setUnit(Unit.valueOf(resultSet.getString("unit")));
            ingredient.setStockMovements(findByMovementOfOneIngredient(resultSet.getString("id")));
            ingredients.add(ingredient);
        }
        return ingredients;
    }

    public List<Ingredient> filterByCriteria(int page, int pageSize, List<Criteria> criterias) {
        List<Ingredient> ingredients = new ArrayList<>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT ic.id, i.name, MAX(ic.unit_price) AS unit_price, MAX(ic.last_modification_date) AS last_modification_date, ic.unit ");
        query.append("FROM ingredient i ");
        query.append("INNER JOIN ingredient_cost ic ON i.id = ic.id ");
        query.append("WHERE 1 = 1 ");

        List<Object> parameters = new ArrayList<>();
        List<String> orderByClauses = new ArrayList<>();
        boolean useGroupBy = false;

        for (Criteria criteria : criterias) {
            String column = criteria.getColumn();
            Object value = criteria.getValue();
            String operator = criteria.getOperator();
            String logicalOperator = criteria.getLogicalOperator().toUpperCase();
            String order = criteria.getOrder();

            if ("name".equalsIgnoreCase(column)) {
                query.append(" ").append(logicalOperator).append(" i.name ");
                if ("like".equalsIgnoreCase(operator)) {
                    query.append("ILIKE ?");
                    parameters.add("%" + value + "%");
                } else {
                    query.append(operator).append(" ?");
                    parameters.add(value);
                }
                if (order != null) {
                    orderByClauses.add("i.name " + order);
                }
            } else if ("last_modification_date".equalsIgnoreCase(column)) {
                query.append(" ").append(logicalOperator)
                        .append(" EXISTS (SELECT 1 FROM unnest(ic.last_modification_date) AS last_modification_date WHERE last_modification_date ")
                        .append(operator).append(" ?) ");
                parameters.add(value);
                if (order != null) {
                    orderByClauses.add("MAX(ic.last_modification_date) " + order);
                    useGroupBy = true;
                }
            } else if ("unit_price".equalsIgnoreCase(column)) {
                query.append(" ").append(logicalOperator)
                        .append(" EXISTS (SELECT 1 FROM unnest(ic.unit_price) AS unit_price WHERE unit_price ")
                        .append(operator).append(" ?) ");
                parameters.add(value);
                if (order != null) {
                    orderByClauses.add("MAX(ic.unit_price) " + order);
                    useGroupBy = true;
                }
            }
        }

        if (useGroupBy) {
            query.append(" GROUP BY ic.id, i.name, ic.unit ");
        }

        if (!orderByClauses.isEmpty()) {
            query.append(" ORDER BY ");
            query.append(String.join(", ", orderByClauses));
        } else {
            query.append(" ORDER BY i.name ASC");
        }

        query.append(" LIMIT ? OFFSET ?");
        parameters.add(pageSize);
        parameters.add((page - 1) * pageSize);
        try (PreparedStatement stmt = connection.prepareStatement(query.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }
            ResultSet resultSet = stmt.executeQuery();
            ingredients = mapIngredientFromResultSet(resultSet);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'exécution de la requête SQL", e);
        }

        return ingredients;
    }

    @Override
    public List<Ingredient> findAll(int page, int pageSize, LocalDateTime dateTime) {
        return List.of();
    }

    @Override
    public Ingredient findByName(String ingredientName) {
        Ingredient ingredient = new Ingredient();
        if (connection != null) {
            String select = "SELECT ingredient_cost.id, ingredient.name, ingredient_cost.unit_price, ingredient_cost.unit, ingredient_cost.last_modification_date " +
                    "FROM ingredient " +
                    "INNER JOIN ingredient_cost ON ingredient.id = ingredient_cost.id " +
                    "WHERE ingredient.name = ?;";

            try {
                PreparedStatement preparedStatement = connection.prepareStatement(select);
                preparedStatement.setString(1, ingredientName);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    Array lastModificationDatesArray = resultSet.getArray("last_modification_date");
                    Array unitPriceArray = resultSet.getArray("unit_price");

                    Timestamp[] timestamps = (Timestamp[]) lastModificationDatesArray.getArray();
                    List<LocalDateTime> lastModificationDates = Arrays.stream(timestamps)
                            .map(Timestamp::toLocalDateTime)
                            .collect(Collectors.toList());

                    Double[] unitPricesArray = (Double[]) unitPriceArray.getArray();
                    List<Double> unitPrices = Arrays.asList(unitPricesArray);
                    ingredient.setId(resultSet.getString("id"));
                    ingredient.setName(resultSet.getString("name"));
                    ingredient.setLastModificationDate(lastModificationDates);
                    ingredient.setUnitPrice(unitPrices);
                    Unit.valueOf(resultSet.getString("unit")) ;
                    ingredient.setStockMovements(findByMovementOfOneIngredient(resultSet.getString("id")));
                }
            } catch (Exception e) {
                throw new RuntimeException("Error executing query", e);
            }
        }
        return ingredient;
    }

    public List<StockMovement> findByMovementOfOneIngredient(String ingredientId) {
        List<StockMovement> movements = new ArrayList<>();
        if (connection != null) {
            String sql = "SELECT movement_type, quantity, unit, movement_date FROM stock_movement WHERE ingredient_id = ? ORDER BY movement_date";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, ingredientId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    MovementType type = MovementType.valueOf(rs.getString("movement_type"));
                    double quantity = rs.getDouble("quantity");
                    Unit unit = Unit.valueOf(rs.getString("unit"));
                    LocalDateTime date = rs.getTimestamp("movement_date").toLocalDateTime();

                    movements.add(new StockMovement(type, quantity, unit, date));
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error fetching stock movements", e);
            }
        }
        return movements;
    }

    @Override
    public Ingredient save(Ingredient newIngredient) {
        if (connection != null) {
            String upSertQuery = "INSERT INTO ingredient_cost (id, unit, last_modification_date, unit_price) " +
                    "VALUES (?, ?::unit, ARRAY[?]::timestamp[] || '{}', ARRAY[?] || '{}') " +
                    "ON CONFLICT (id) DO UPDATE " +
                    "SET last_modification_date = ARRAY_APPEND(ingredient_cost.last_modification_date, ?), " +
                    "unit_price = ARRAY_APPEND(ingredient_cost.unit_price, ?), " +
                    "unit = EXCLUDED.unit;";

            try (PreparedStatement preparedStatement = connection.prepareStatement(upSertQuery)) {
                Timestamp currentTimestamp = Timestamp.valueOf(LocalDateTime.now());
                double unitPrice = newIngredient.getUnitPrice().getFirst();
                String unit = newIngredient.getUnit().name();
                preparedStatement.setString(1, newIngredient.getId());
                preparedStatement.setString(2, unit);
                preparedStatement.setArray(3, connection.createArrayOf("timestamp", new Timestamp[]{currentTimestamp}));  // Tableau de timestamps
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
