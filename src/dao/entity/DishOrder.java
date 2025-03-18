package dao.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DishOrder {
    private Long id;
    private Dish dish;
    private double quantity;
    private List<Status> dishStatus;
    private List<Instant> statusDate;
}
