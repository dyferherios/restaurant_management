package entity;

import java.nio.channels.FileLock;
import java.time.LocalDateTime;
import java.util.*;

public class Ingredient {
    private String id;
    private String name;
    private List<LocalDateTime> lastModificationDate = new ArrayList<>();
    private List<Double> unitPrice = new ArrayList<>();
    private List<StockMovement> stockMovements;
    private double quantity;
    private Unit unit;

    public Ingredient(){};

    public Ingredient(String id, String  name, List<LocalDateTime>  lastModificationDate, List<Double> unitPrice, double quantity, Unit unit){
        this.id = id;
        this.name = name;
        this.lastModificationDate = lastModificationDate;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.unit = unit;
    }

    public List<StockMovement> getStockMovements() {
        return stockMovements;
    }

    public void setStockMovements(List<StockMovement> stockMovements) {
        this.stockMovements = stockMovements;
    }

    public double getAvailableQuantity(LocalDateTime date) {
        List<StockMovement> stockMovementsTmp = this.getStockMovements().stream()
                .filter(m -> m.getDate().isBefore(date) || m.getDate().isEqual(date)).toList();
        double sum = 0.0;
        for (StockMovement stockMovement : stockMovementsTmp) {
            if (stockMovement.getType() == MovementType.IN) {
                sum += stockMovement.getQuantity();
            } else {
                sum -= stockMovement.getQuantity();
            }
        }
        return sum;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getQuantity() {
        return quantity;
    }

    public List<LocalDateTime> getLastModificationDate() {
        return lastModificationDate;
    }

    public void setLastModificationDate(List<LocalDateTime> lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public void setUnitPrice(List<Double> unitPrice) {
           this.unitPrice = unitPrice;
    }

    public List<Double> getUnitPrice() {
        return unitPrice;
    }
    public Unit getUnit() {
        return unit;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "Ingredient { id = " + id +
                ", name = " + name +
                ", lastModificationDate = " + lastModificationDate +
                ", unitPrice = " + unitPrice +
                ", quantity = " + quantity +
                ", unit = " + unit + "}";
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Ingredient ingredient = (Ingredient) o;

        return Objects.equals(id, ingredient.id) && Objects.equals(name, ingredient.name) && Objects.equals(unitPrice, ingredient.unitPrice) && Objects.equals(lastModificationDate, ingredient.lastModificationDate) && Objects.equals(unit, ingredient.unit);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id, name, lastModificationDate, unitPrice, unit);
    }
}