package dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Order {
    private Long id;
    private String references;
    private Date creationDate;
    private Double amount;
    private List<DishOrder> dishesOrder;
    private List<Status> orderStatus;

    public Status getActualStatus(){
        return this.orderStatus.getLast();
    }

    public HashMap<Dish, Status> getDishOrderWithActualStatus(){
        HashMap<Dish, Status> dishOrderStatus = new HashMap<>();
        this.dishesOrder.forEach(dishOrder -> dishOrderStatus.put(dishOrder.getDish(), dishOrder.getDishStatus().getLast()));
        return dishOrderStatus;
    }

    public double getTotalAmount(){
        AtomicReference<Double> totalAmount = new AtomicReference<>(0.0);
        this.dishesOrder.forEach(dishQuantity -> totalAmount.updateAndGet(v -> v + dishQuantity.getDish().getPrice() * dishQuantity.getQuantity()));
        return totalAmount.get();
    }

    public Duration getPreparationTime(){
        return Duration.between(dishesOrder.getLast().getStatusDate().getFirst(), dishesOrder.getLast().getStatusDate().getLast());
    }

    public String getOneDish(List<DishOrder> dishOrders, String dishName){
        return dishOrders.stream()
                .filter(dishOrder -> dishOrder.getDish() != null && dishOrder.getDish().getName().equalsIgnoreCase(dishName))
                .findFirst().get().getDish().getName();
    }
}
