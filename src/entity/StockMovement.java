package entity;

import java.time.LocalDateTime;

public class StockMovement {
    private MovementType type;
    private double quantity;
    private Unit unit;
    private LocalDateTime date;

    public StockMovement(MovementType type, double quantity, Unit unit, LocalDateTime date) {
        this.type = type;
        this.quantity = quantity;
        this.unit = unit;
        this.date = date;
    }


    // Getters
    public MovementType getType() {
        return type;
    }

    public double getQuantity() {
        return quantity;
    }

    public Unit getUnit() {
        return unit;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "StockMovement{" +
                ", type=" + type +
                ", quantity=" + quantity +
                ", unit=" + unit +
                ", date=" + date +
                '}';
    }
}
