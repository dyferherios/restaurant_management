package test;

import dao.entity.*;
import dao.operations.DishCrudOperations;
import dao.operations.DishOrderCrudOperations;
import dao.operations.OrderCrudOperations;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {
    DishCrudOperations dishCrudOperations = new DishCrudOperations();
    OrderCrudOperations orderCrudOperations = new OrderCrudOperations();
    DishOrderCrudOperations dishOrderCrudOperations = new DishOrderCrudOperations();
    OrderCrudOperations subject = new OrderCrudOperations();

//    @Test
//    void get_actual_status_OK(){
//        Order order = subject.findById(3L);
//        assertNotNull(order);
//        Status actualStatus = order.getActualStatus();
//        assertEquals(Status.CREATED, actualStatus);
//    }
//
//    @Test
//    void get_total_amount_OK(){
//        Order order = subject.findById(3L);
//        assertNotNull(order);
//        assertEquals(30000.0, order.getTotalAmount());
//    }
//
//    @Test
//    void get_preparation_time_OK(){
//        Order order = subject.findById(3L);
//        assertNotNull(order);
//        //assertEquals(Duration.parse("PT0S"), order.getPreparationTime());
//    }
//
//    @Test
//    void get_dish_order_OK(){
//        Order order = subject.findById(3L);
//        assertNotNull(order);
//        HashMap<String, List<Status>> dishStatus = new HashMap<>();
//        dishStatus.put("Hot dog", List.of(Status.CREATED));
//        HashMap<String, List<Status>> dishStatusOrder = new HashMap<>();
//        String hotDog = order.getOneDish(order.getDishesOrder(), "Hot dog");
//        dishStatusOrder.put(hotDog, List.of(Status.CREATED));
//        assertEquals(dishStatus, dishStatusOrder);
//    }
//
//    @Test
//    void get_order_By_Id_OK(){
//        Order order = subject.findById(3L);
//        System.out.println(order.toString());
//        assertNotNull(order);
//        assertEquals(Status.CREATED, order.getOrderStatus().getLast().getStatus());
//    }

    @Test
    void save_all_order_OK(){
        Order newOrder = new Order();
        newOrder.setId(1L);
        newOrder.setReferences("order");
        newOrder.setCreationDate(Date.from(Instant.now()));

        OrderStatus newOrderStatus = new OrderStatus();
        newOrderStatus.setStatusDate(Instant.now());
        newOrderStatus.setStatus(Status.INPROGRESS);
        newOrderStatus.setOrder(newOrder);
        newOrder.addOrderStatus(newOrderStatus);

        DishOrder newDishOrder = new DishOrder();
        newDishOrder.setDish(dishCrudOperations.findById(1L));
        newDishOrder.setQuantity(4);
        newOrder.addDishOrder(newDishOrder);



        subject.saveAll(List.of(newOrder, newOrder));



//        System.out.println(orderCrudOperations.mapOrderStatus(newOrder.getId()));
//      List<DishOrder> dishOrders = orderCrudOperations.findById(4L).getDishesOrder();
        //orderCrudOperations.saveDishOrderStatus(dishOrders);

        assertNotNull(subject.findById(2L));
    }


}
