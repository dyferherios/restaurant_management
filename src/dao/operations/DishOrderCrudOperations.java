package dao.operations;

import dao.entity.*;
import db.DataSource;

import java.sql.*;
import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class DishOrderCrudOperations implements CrudOperations<DishOrder> {
    private final DataSource dataSource = new DataSource();
    private final DishCrudOperations dishCrudOperations = new DishCrudOperations();
    private final StockMovementCrudOperations stockMovementCrudOperations = new StockMovementCrudOperations();


    @Override
    public List<DishOrder> getAll(int page, int size) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<DishOrder> getAllDishInsideAnOrder(Long orderId){
        List<DishOrder> dishOrders = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select d_o.id as dish_order_id, d_o.id_dish, d_o.quantity from dish_order d_o where id_order = ?")){
            preparedStatement.setLong(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                DishOrder dishOrder = new DishOrder();
                Long dishOrderId = resultSet.getLong("dish_order_id");
                Long dishId = resultSet.getLong("id_dish");
                dishOrder.setId(dishOrderId);
                dishOrder.setOrder(findOrderOfOneDishOrder(dishOrderId));
                dishOrder.setDish(dishCrudOperations.findById(dishId));
                dishOrder.setQuantity(resultSet.getDouble("quantity"));
                dishOrders.add(dishOrder);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dishOrders;
    }

    @Override
    public DishOrder findById(Long dishOrderId) {
        DishOrder dishOrder = new DishOrder();
        try(Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "select d_o.id, d_o.id_dish, d_o.id_order, d_o.quantity from dish_order d_o where d_o.id = ?")){
            statement.setLong(1, dishOrderId);
            try(ResultSet resultSet = statement.executeQuery()){
              if (resultSet.next()){
                  dishOrder.setId(dishOrderId);
                  dishOrder.setOrder(findOrderOfOneDishOrder(dishOrderId));
                  dishOrder.setDish(dishCrudOperations.findById(resultSet.getLong("id_dish")));
                  dishOrder.setQuantity(resultSet.getDouble("quantity"));
                  dishOrder.setOrderStatus(mapDishOrderStatus(dishOrderId));
              }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dishOrder;
    }


    public DishOrder findByIdOrderAndIdDish(Long idOrder, Long idDish) {
        DishOrder dishOrder = new DishOrder();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "select d_o.id, d_o.id_dish, d_o.id_order, d_o.quantity from dish_order d_o where d_o.id_order = ? and d_o.id_dish = ?")){
            statement.setLong(1, idOrder);
            statement.setLong(2, idDish);
            try(ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()){
                    Long dishOrderId = resultSet.getLong("id");
                    dishOrder.setId(dishOrderId);
                    dishOrder.setOrder(findOrderOfOneDishOrder(dishOrderId));
                    dishOrder.setDish(dishCrudOperations.findById(resultSet.getLong("id_dish")));
                    dishOrder.setQuantity(resultSet.getDouble("quantity"));
                    dishOrder.setOrderStatus(mapDishOrderStatus(dishOrderId));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dishOrder;
    }

    @Override
    public List<DishOrder> filterByCriteria(List<Criteria> criterias, int page, int size, Map<String, String> sort) {
        return List.of();
    }

    @Override
    public List<DishOrder> saveAll(List<DishOrder> entities) {
        if (entities == null || entities.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Boolean, List<DishOrder>> groupedEntities = entities.stream()
                .collect(Collectors.partitioningBy(e -> e.getId() != null));

        List<DishOrder> savedEntities = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {
            if (!groupedEntities.get(true).isEmpty()) {
                String sqlWithId = "INSERT INTO dish_order (id, id_order, id_dish, quantity) " +
                        "VALUES (?, ?, ?, ?) " +
                        "ON CONFLICT (id) DO UPDATE SET " +
                        "id_order = EXCLUDED.id_order, " +
                        "id_dish = EXCLUDED.id_dish, " +
                        "quantity = EXCLUDED.quantity " +
                        "RETURNING id, id_order, id_dish, quantity";

                savedEntities.addAll(executeBatch(conn, sqlWithId, groupedEntities.get(true), true));
            }

            if (!groupedEntities.get(false).isEmpty()) {
                String sqlWithoutId = "INSERT INTO dish_order (id_order, id_dish, quantity) " +
                        "VALUES (?, ?, ?) " +
                        "ON CONFLICT (id_order, id_dish) DO UPDATE SET " +
                        "quantity = EXCLUDED.quantity " +
                        "RETURNING id, id_order, id_dish, quantity";

                savedEntities.addAll(executeBatch(conn, sqlWithoutId, groupedEntities.get(false), false));
            }

            return savedEntities;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving DishOrders", e);
        }
    }

    private List<DishOrder> executeBatch(Connection conn, String sql, List<DishOrder> entities, boolean hasId)
            throws SQLException {

        List<DishOrder> savedEntities = new ArrayList<>();
        List<DishOrder> currentValues = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (DishOrder entity : entities) {
                DishOrder tmpDishOrder = this.findByIdOrderAndIdDish(entity.getOrder().getId(), entity.getDish().getId());
                if(tmpDishOrder!=null){
                    currentValues.add(tmpDishOrder);
                }
                int paramIndex = 1;

                if (hasId) {
                    stmt.setLong(paramIndex++, entity.getId());
                }

                stmt.setLong(paramIndex++, entity.getOrder().getId());
                stmt.setLong(paramIndex++, entity.getDish().getId());
                stmt.setDouble(paramIndex, entity.getQuantity());

                stmt.addBatch();
            }


            int[] affectedRows = stmt.executeBatch();

            if(affectedRows.length > 0){
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    while (rs.next()) {
                        DishOrder saved = new DishOrder();
                        saved.setId(rs.getLong("id"));
                        saved.setOrder(this.findByIdForDish(rs.getLong("id_order")));
                        saved.setDish(dishCrudOperations.findById(rs.getLong("id_dish")));
                        saved.setQuantity(rs.getDouble("quantity"));
                        savedEntities.add(saved);
                    }
                }
                Map<Long, Long> savedIds = new HashMap<>();
                for (DishOrder saved : savedEntities) {
                    savedIds.put(saved.getDish().getId(), saved.getId());
                }

                for (DishOrder dishOrder : entities) {
                    Long savedId = savedIds.get(dishOrder.getDish().getId());
                    if (savedId != null) {
                        dishOrder.setId(savedId);
                    }
                }

                addStockMovement(entities, currentValues);
            }
        }
        return savedEntities;
    }

    private void addStockMovement(List<DishOrder> dishOrders, List<DishOrder> currentValues) {
        Map<Long, DishOrder> currentValuesMap = currentValues.stream()
                .collect(Collectors.toMap(d -> d.getDish().getId(), d -> d));

        dishOrders.forEach(dishOrder -> {
            if (dishOrder.getDish() == null) {
                System.out.println("Dish is null for DishOrder ID: " + dishOrder.getId());
                return;
            }

            DishOrder current = currentValuesMap.get(dishOrder.getDish().getId());

            if (current == null) {
                double quantityDifference = Math.abs(dishOrder.getQuantity());
                dishOrder.getDish().getDishIngredients().forEach(dishIngredient -> {
                    double totalRequiredQuantity = dishIngredient.getRequiredQuantity() * quantityDifference;
                    StockMovementType movementType = dishOrder.getQuantity() > 0 ? StockMovementType.OUT : StockMovementType.IN;

                    StockMovement stockMovement = new StockMovement(
                            dishIngredient.getIngredient(),
                            totalRequiredQuantity,
                            dishIngredient.getUnit(),
                            movementType,
                            Instant.now()
                    );

                    // Enregistrer le mouvement de stock
                    stockMovementCrudOperations.saveAll(List.of(stockMovement));
                });
            } else {
                // Si le plat existe déjà dans currentValues, vérifier si la quantité a changé
                if (isQuantityChanged(dishOrder, current)) {
                    addStockMovementForChangedQuantity(dishOrder, current);
                }
            }
        });
    }

    private boolean isQuantityChanged(DishOrder dishOrder, DishOrder current) {
        return dishOrder.getQuantity() != current.getQuantity();
    }

    private void addStockMovementForChangedQuantity(DishOrder dishOrder, DishOrder current) {
        StockMovementType movementType = dishOrder.getQuantity() > current.getQuantity() ? StockMovementType.OUT : StockMovementType.IN;

        dishOrder.getDish().getDishIngredients().forEach(dishIngredient -> {
            double quantityDifference = Math.abs(dishOrder.getQuantity() - current.getQuantity());
            double totalRequiredQuantity = dishIngredient.getRequiredQuantity() * quantityDifference;

            StockMovement stockMovement = new StockMovement(
                    dishIngredient.getIngredient(),
                    totalRequiredQuantity,
                    dishIngredient.getUnit(),
                    movementType,
                    Instant.now()
            );

            // Enregistrer le mouvement de stock
            stockMovementCrudOperations.saveAll(List.of(stockMovement));
        });
    }



    private List<DishOrder> mapFromResultSet(ResultSet resultSet) throws SQLException {
        List<DishOrder> dishOrders = new ArrayList<>();
        while(resultSet.next()){
            DishOrder dishOrder = new DishOrder();
            Long dishOrderId = resultSet.getLong("id");
            dishOrder.setId(dishOrderId);
            dishOrder.setOrder(findOrderOfOneDishOrder(dishOrderId));
            dishOrder.setDish(dishCrudOperations.findById(resultSet.getLong("id_dish")));
            dishOrder.setQuantity(resultSet.getDouble("quantity"));
            dishOrder.setOrderStatus(mapDishOrderStatus(dishOrderId));
            dishOrders.add(dishOrder);
        }
        return dishOrders;
    }



    private Order findOrderOfOneDishOrder(Long dishOrderId){
        Order order = new Order();
        try(Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement("select d_o.id, o.id as id_order, o.order_references, o.creation_date  from dish_order d_o join" +
                " orders o on o.id = d_o.id_order where d_o.id=?")){
            statement.setLong(1, dishOrderId);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()){
                order.setId(resultSet.getLong("id_order"));
                order.setReferences(resultSet.getString("order_references"));
                order.setCreationDate(resultSet.getDate("creation_date"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return order;
    }

    private List<OrderStatus> mapDishOrderStatus(Long dishOrderId){
        List<OrderStatus> orderStatuses = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                "select dos.id, dos.id_dish_order, os.id_order, os.status, os.creation_date from dish_order_status dos join " +
                "order_status os on dos.id_order_status=os.id where dos.id_dish_order=?")){
            statement.setLong(1, dishOrderId);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setId(resultSet.getLong("id"));
                orderStatus.setStatus(Status.valueOf(resultSet.getString("status")));
                Date sqlDate = resultSet.getDate("creation_date");
                Instant creationInstant = sqlDate != null
                        ? sqlDate.toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
                        : null;

                orderStatus.setStatusDate(creationInstant);
                orderStatuses.add(orderStatus);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return orderStatuses;
    }

    public Order findByIdForDish(Long orderId) {
        Order order = new Order();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select id, order_references, creation_date from orders where id=?")){
            preparedStatement.setLong(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                order.setId(orderId);
                order.setReferences(resultSet.getString("order_references"));
                order.setCreationDate(resultSet.getDate("creation_date"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return order;
    }

}
