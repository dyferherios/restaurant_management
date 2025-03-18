package dao.operations;

import dao.entity.Criteria;
import dao.entity.DishOrder;
import dao.entity.Status;
import db.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DishOrderCrudOperations implements CrudOperations<DishOrder> {
    private final DataSource dataSource = new DataSource();
    private final DishCrudOperations dishCrudOperations = new DishCrudOperations();
    @Override
    public List<DishOrder> getAll(int page, int size) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<DishOrder> getAllDishInsideAnOrder(Long orderId){
        List<DishOrder> dishOrders = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select id, id_dish, quantity from dish_order where id_order = ?")){
            preparedStatement.setLong(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                DishOrder dishOrder = new DishOrder();
                Long dishId = resultSet.getLong("id_dish");
                dishOrder.setId(resultSet.getLong("id"));
                dishOrder.setDish(dishCrudOperations.findById(dishId));
                dishOrder.setDishStatus(mapStatusOfOneDish(orderId, dishId));
                dishOrder.setStatusDate(mapProcessDateOfOneDish(orderId, dishId));
                dishOrder.setQuantity(resultSet.getDouble("quantity"));
                dishOrders.add(dishOrder);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dishOrders;
    }

    public List<Status> mapStatusOfOneDish(Long orderId, Long dishId){
        List<Status> dishStatus = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
                "select os.status, os.creation_date from dish_order" +
                        " join dish_order_status as dos on dish_order.id = dos.id_dish_order" +
                        " join order_status os on os.id=dos.id_order_status" +
                        " join orders as o on o.id=dish_order.id_order" +
                        " where o.id = ? and dish_order.id = ?")){
            preparedStatement.setLong(1, orderId);
            preparedStatement.setLong(2, dishId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                dishStatus.add(Status.valueOf(resultSet.getString("status")));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return dishStatus;
    }

    public List<Instant> mapProcessDateOfOneDish(Long orderId, Long dishId){
        List<Instant> processDate = new ArrayList<>();
        try(Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "select os.status, os.creation_date from dish_order" +
                            " join dish_order_status as dos on dish_order.id = dos.id_dish_order" +
                            " join order_status os on os.id=dos.id_order_status" +
                            " join orders as o on o.id=dish_order.id_order" +
                            " where o.id = ? and dish_order.id = ?")){
            preparedStatement.setLong(1, orderId);
            preparedStatement.setLong(2, dishId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                processDate.add(resultSet.getTimestamp("creation_date").toInstant());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return processDate;
    }


    @Override
    public DishOrder findById(Long id) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<DishOrder> filterByCriteria(List<Criteria> criterias, int page, int size, Map<String, String> sort) {
        return List.of();
    }

    @Override
    public List<DishOrder> saveAll(List<DishOrder> entities) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
