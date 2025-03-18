package dao.operations;

import dao.entity.Criteria;
import dao.entity.DishOrder;
import dao.entity.Order;
import dao.entity.Status;
import db.DataSource;
import org.junit.platform.commons.function.Try;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderCrudOperations implements CrudOperations<Order> {
    private final DataSource dataSource = new DataSource();
    private final DishCrudOperations dishCrudOperations = new DishCrudOperations();
    private final DishOrderCrudOperations dishOrderCrudOperations = new DishOrderCrudOperations();
    @Override
    public List<Order> getAll(int page, int size) {
        List<Order> orders = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            Statement statement=connection.createStatement()){
            ResultSet resultSet = statement.executeQuery("select id, order_references, creation_date from orders");
            return mapOrderFromResultset(resultSet);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public List<Status> getAllStatusOfOrder(Long orderId) {
        List<Status> orderStatus = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("select status from order_status where id_order=?")){
            preparedStatement.setLong(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                orderStatus.add(Status.valueOf(resultSet.getString("status")));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return orderStatus;
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
                order.setOrderStatus(getAllStatusOfOrder(orderId));
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
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement("insert into order ")) {

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return List.of();
    }

    public List<Order> mapOrderFromResultset(ResultSet resultSet) throws SQLException {
        List<Order> orders = new ArrayList<>();
        while(resultSet.next()){
            Order order = new Order();
            Long orderId = resultSet.getLong("id");
            order.setId(orderId);
            order.setReferences(resultSet.getString("order_references"));
            order.setCreationDate(resultSet.getDate("creation_date"));
            order.setDishesOrder(dishOrderCrudOperations.getAllDishInsideAnOrder(orderId));
            order.setOrderStatus(getAllStatusOfOrder(orderId));
            orders.add(order);
        }
        return orders;
    }
}
