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

    @Test
    void get_actual_status_OK(){
        Order order = subject.findById(1L);
        assertNotNull(order);
        Status actualStatus = order.getActualStatus();
        assertEquals(Status.CONFIRMED, actualStatus);
    }

    @Test
    void get_total_amount_OK(){
        Order order = subject.findById(1L);
        assertNotNull(order);
        assertEquals(15000.0*4, order.getTotalAmount());
    }

    @Test
    void get_preparation_time_OK(){
        Order order = subject.findById(1L);
        assertNotNull(order);
        //assertEquals(Duration.parse("PT0S"), order.getPreparationTime());
    }

    @Test
    void get_dish_order_OK(){
        Order order = subject.findById(1L);
        assertNotNull(order);
        assertNotNull(order.getDishesOrder());
        assertTrue(order.getDishesOrder().stream().anyMatch(dishOrder -> dishOrder.getDish().getName().contains("dog")));
    }

    @Test
    void get_order_By_Id_OK(){
        Order order = subject.findById(1L);
        assertNotNull(order);
        assertEquals("Hot dog", order.getOneDish(order.getDishesOrder(), "Hot dog"));
    }

    @Test
    void save_all_order_OK(){
        Order newOrder = new Order();
        newOrder.setId(1L);
        newOrder.setReferences("order");
        newOrder.setCreationDate(Date.from(Instant.now()));

        OrderStatus newOrderStatus = new OrderStatus();
        newOrderStatus.setStatusDate(Instant.now());
        newOrderStatus.setStatus(Status.CREATED);
        newOrder.addOrderStatus(newOrderStatus);


        DishOrder newDishOrder = new DishOrder();
        newDishOrder.setDish(dishCrudOperations.findById(1L));
        newDishOrder.setQuantity(4);
        newOrder.addDishOrder(newDishOrder);

        OrderStatus newOrderStatus1 = new OrderStatus();
        newOrderStatus1.setStatusDate(Instant.now());
        newOrderStatus1.setStatus(Status.CONFIRMED);
        newOrder.addOrderStatus(newOrderStatus1);

        subject.saveAll(List.of(newOrder));

        assertNotNull(subject.findById(1L));
    }


}
