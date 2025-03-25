package dao.operations;

import dao.entity.*;
import db.DataSource;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class OrderCrudOperations implements CrudOperations<Order> {
    private final DataSource dataSource = new DataSource();
    private final DishOrderCrudOperations dishOrderCrudOperations = new DishOrderCrudOperations();

   @Override
    public List<Order> getAll(int page, int size) {
        List<Order> orders;
        try(Connection connection = dataSource.getConnection();
            Statement statement=connection.createStatement()){
            ResultSet resultSet = statement.executeQuery("select id, order_references, creation_date from orders");
            orders = mapOrderFromResultSet(resultSet);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return orders;
    }

    @Override
    public Order findById(Long orderId) {
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
                order.setDishesOrder(dishOrderCrudOperations.getAllDishInsideAnOrder(orderId));
                order.setAmount(order.getTotalAmount());
                order.setOrderStatus(mapOrderStatus(orderId));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return order;
    }

    @Override
    public List<Order> filterByCriteria(List<Criteria> criterias, int page, int size, Map<String, String> sort) {
        throw new UnsupportedOperationException("Not supported yet");
    }

    @Override
    public List<Order> saveAll(List<Order> entities) {
        List<Order> orders = new ArrayList<>();
        try(Connection connection = dataSource.getConnection()){
            PreparedStatement statement = connection.prepareStatement("insert into orders (id, order_references, creation_date) values(?,?,?) " +
                    "on conflict (id) do update set order_references = excluded.order_references " +
                    "returning id, order_references, creation_date;", Statement.RETURN_GENERATED_KEYS
            );
            entities.forEach(orderToSave -> {
                    try {
                        Long orderId = orderToSave.getId();
                        if(orderId!=null){
                            statement.setLong(1, orderId);
                        }else{
                            statement.setLong(1, Types.INTEGER);
                        }
                        statement.setString(2, orderToSave.getReferences());
                        statement.setObject(3, orderToSave.getCreationDate(), Types.DATE);
                        statement.addBatch();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
            });
            statement.executeBatch();
            ResultSet resultSet = statement.getGeneratedKeys();
            
            int index = 0;
            while (resultSet.next()) {
                Order order = new Order();
                order.setId(resultSet.getLong("id"));
                order.setReferences(resultSet.getString("order_references"));
                order.setCreationDate(Date.from(resultSet.getTimestamp("creation_date").toInstant()));

                if (index < entities.size() && entities.get(index).getOrderStatus() != null) {
                    order.setOrderStatus(new ArrayList<>(entities.get(index).getOrderStatus()));
                }

                orders.add(order);
                index++;
            }

            for(Order order: entities){
                if(order.getOrderStatus()!=null  && !order.getOrderStatus().isEmpty()){
                    saveAllOrderStatus(order.getOrderStatus().getLast());
                    dishOrderCrudOperations.saveAll(order.getDishesOrder());
                }
                if(!order.getDishesOrder().isEmpty()){
                    order.getDishesOrder().forEach(this::saveDishOrderStatus);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return orders;
    }

    public List<Order> mapOrderFromResultSet(ResultSet resultSet) throws SQLException {
        List<Order> orders = new ArrayList<>();
        while(resultSet.next()){
            Order order = new Order();
            Long orderId = resultSet.getLong("id");
            order.setId(orderId);
            order.setReferences(resultSet.getString("order_references"));
            order.setCreationDate(resultSet.getDate("creation_date"));
            List<DishOrder> dishOrders = dishOrderCrudOperations.getAllDishInsideAnOrder(orderId);
            order.setDishesOrder(dishOrders);
            order.setAmount(order.getTotalAmount());
            orders.add(order);
        }
        return orders;
    }

    public List<OrderStatus> mapOrderStatus(Long orderId){
        List<OrderStatus> orderStatuses = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "select dos.id, dos.id_order_status, os.id_order, os.status, os.creation_date from dish_order_status dos " +
                    "join order_status os on dos.id_order_status=os.id where os.id_order=?")){
            statement.setLong(1, orderId);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                OrderStatus  orderStatus = new OrderStatus();
                orderStatus.setId(resultSet.getLong("id"));
                orderStatus.setStatus(Status.valueOf(resultSet.getString("status")));
                orderStatus.setStatusDate(resultSet.getTimestamp("creation_date").toInstant());
                orderStatuses.add(orderStatus);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return orderStatuses;
    }

    public void saveAllOrderStatus(OrderStatus orderStatus) throws SQLException {
        if (Status.CONFIRMED.equals(orderStatus.getStatus())) {
            validateDishAvailability(orderStatus);
        }

        String sql = buildUpsertQuery(orderStatus.getId() != null);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int paramIndex = 1;

            if (orderStatus.getId() != null) {
                statement.setLong(paramIndex++, orderStatus.getId());
            }

            statement.setLong(paramIndex++, orderStatus.getOrder().getId());
            statement.setString(paramIndex++, orderStatus.getStatus().name());
            statement.setTimestamp(paramIndex, Timestamp.from(orderStatus.getStatusDate()));

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet resultSet = statement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        orderStatus.setId(resultSet.getLong("id"));
                        orderStatus.setStatusDate(resultSet.getTimestamp("creation_date").toInstant());
                        if (orderStatus.getOrder().getId() == null) {
                            orderStatus.setOrder(findById(resultSet.getLong("id_order")));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving order status", e);
        }
    }
    private void validateDishAvailability(OrderStatus orderStatus) {
        if (orderStatus.getOrder().getDishesOrder() == null || orderStatus.getOrder().getDishesOrder().isEmpty()) {
            return;
        }

        orderStatus.getOrder().getDishesOrder().forEach(dishOrder -> {
            if (dishOrder.getDish().getAvailableQuantity() < dishOrder.getQuantity()) {
                throw new RuntimeException("Stock not enough for this dish'" + dishOrder.getDish().getName() +
                        "'. Available : " + dishOrder.getDish().getAvailableQuantity() +
                        ", Required : " + dishOrder.getQuantity());
            }
        });
    }
    private String buildUpsertQuery(boolean hasId) {
        return "INSERT INTO order_status(" +
                (hasId ? "id, " : "") +
                "id_order, status, creation_date) " +
                "VALUES (" + (hasId ? "?, " : "") + "?, ?::order_status_process, ?) " +
                "ON CONFLICT (id_order, status) " +
                "DO UPDATE SET creation_date = EXCLUDED.creation_date " +
                "RETURNING id, id_order, status, creation_date";
    }

    public void saveDishOrderStatus(DishOrder dishOrder){
        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(
                    "insert into dish_order_status(id_dish_order, id_order_status) values(?,?) " +
                        "on conflict (id_dish_order, id_order_status) " +
                        "do nothing returning id, id_dish_order, id_order_status")){
                try {
                    statement.setLong(1, dishOrder.getId());
                    statement.setLong(2, dishOrder.getOrder().getOrderStatus().getLast().getId());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            statement.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
