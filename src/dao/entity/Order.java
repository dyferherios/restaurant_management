package dao.entity;

import lombok.*;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Getter
@Setter
public class Order {
    private Long id;
    private String references;
    private Date creationDate;
    private Double amount;
    @Builder.Default
    private List<DishOrder> dishesOrder = new ArrayList<>();

    @Builder.Default
    private List<OrderStatus> orderStatus = new ArrayList<>();

    public void addDishOrder(DishOrder newDishOrder) {
        this.dishesOrder.add(newDishOrder);
        setDishesOrder(this.dishesOrder);
    }

    public void addOrderStatus(OrderStatus newOrderStatus){
        this.orderStatus.add(newOrderStatus);
        setOrderStatus(this.orderStatus);
    }


    public void setDishesOrder(List<DishOrder> dishesOrder){
        this.dishesOrder = dishesOrder;
        dishesOrder.forEach(dishOrder -> dishOrder.setOrder(this));
    }

    public void setOrderStatus(List<OrderStatus> orderStatus) {
        this.orderStatus = orderStatus;
        if (orderStatus != null) {
            orderStatus.forEach(status -> status.setOrder(this));
        }
    }

    public Status getActualStatus() {
        List<OrderStatus> statuses = getOrderStatus();
        return statuses.getLast().getStatus();
    }


    public HashMap<Dish, Status> getDishOrderWithActualStatus(){
        HashMap<Dish, Status> dishOrderStatus = new HashMap<>();
        this.dishesOrder.forEach(dishOrder -> dishOrderStatus.put(dishOrder.getDish(), dishOrder.getOrderStatus().getLast().getStatus()));
        return dishOrderStatus;
    }

    public double getTotalAmount(){
        AtomicReference<Double> totalAmount = new AtomicReference<>(0.0);
        this.dishesOrder.forEach(dishQuantity -> totalAmount.updateAndGet(v -> v + dishQuantity.getDish().getPrice() * dishQuantity.getQuantity()));
        return totalAmount.get();
    }

    public Duration getPreparationTime(){
        return Duration.between(orderStatus.getFirst().getStatusDate(), orderStatus.getLast().getStatusDate());
    }

    public String getOneDish(List<DishOrder> dishOrders, String dishName){
        return dishOrders.stream()
                .filter(dishOrder -> dishOrder.getDish() != null && dishOrder.getDish().getName().equalsIgnoreCase(dishName))
                .findFirst().get().getDish().getName();
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", references='" + references + '\'' +
                ", creationDate=" + creationDate +
                ", amount=" + amount +
                ", statusCount=" + (orderStatus != null ? orderStatus.size() : 0) +
                ", dishesCount=" + (dishesOrder != null ? dishesOrder.size() : 0) +
                '}';
    }

}
