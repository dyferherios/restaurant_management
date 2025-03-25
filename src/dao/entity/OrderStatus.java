package dao.entity;

import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Getter
@Setter
public class OrderStatus {
    Long id;
    Instant statusDate;
    Status status;
    Order order;

    @Override
    public String toString() {
        return "OrderStatus{" +
                "id=" + id +
                ", status=" + status +
                '}';
    }

}
