package test;

import dao.entity.Dish;
import dao.entity.DishOrder;
import dao.entity.Order;
import dao.entity.Status;
import dao.operations.OrderCrudOperations;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {
    OrderCrudOperations subject = new OrderCrudOperations();

    @Test
    void get_actual_status_OK(){
        Order order = subject.findById(1L);
        assertNotNull(order);
        assertEquals(Status.CONFIRMED, order.getActualStatus());
    }

    @Test
    void get_total_amount_OK(){
        Order order = subject.findById(1L);
        assertNotNull(order);
        assertEquals(30000.0, order.getTotalAmount());
    }

    @Test
    void get_preparation_time_OK(){
        Order order = subject.findById(1L);
        assertNotNull(order);
        assertEquals(Duration.parse("PT15M"), order.getPreparationTime());
    }

    @Test
    void get_dish_order_OK(){
        Order order = subject.findById(1L);
        assertNotNull(order);
        HashMap<String, List<Status>> dishStatus = new HashMap<>();
        dishStatus.put("Hot dog", List.of(Status.CREATED,Status.CONFIRMED));
        HashMap<String, List<Status>> dishStatusOrder = new HashMap<>();
        String hotDog = order.getOneDish(order.getDishesOrder(), "Hot dog");
        dishStatusOrder.put(hotDog, List.of(Status.CREATED,Status.CONFIRMED));
        assertEquals(dishStatus, dishStatusOrder);
    }
}
