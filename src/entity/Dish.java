package entity;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

public class Dish{
    private String id;
    private String name;
    private double unitPrice;
    private List<Ingredient> ingredients;
    private double productPrice;

    public Dish(){};

    public Dish (String name, double unitPrice, List<Ingredient> ingredients, double productPrice){
        this.name = name;
        this.unitPrice = unitPrice;
        this.productPrice = productPrice;
        this.ingredients = ingredients;
    }

    public Dish(String id,String name, double unitPrice, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.ingredients = ingredients;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public double getIngredientCost(LocalDateTime dateTime) {
        return ingredients.stream()
                .mapToDouble(e -> {
                    double unitPrice1 = getPriceAtDate(e.getUnitPrice(), e.getLastModificationDate(), dateTime);
                    return unitPrice1 * e.getQuantity();
                })
                .sum();
    }


    private Double getPriceAtDate(List<Double> unitPrices, List<LocalDateTime> lastModificationDates, LocalDateTime dateTime) {
        if (unitPrices.isEmpty() || lastModificationDates.isEmpty()) return 0.0;
        double lastValidPrice = unitPrices.getFirst();
        for (int i = 0; i < lastModificationDates.size(); i++) {
            if (!dateTime.isBefore(lastModificationDates.get(i))) {
                lastValidPrice = unitPrices.get(i);
            }
        }
        return lastValidPrice;
    }

    public Double getGrossMargin(LocalDateTime requestedDate){
        return unitPrice - this.getIngredientCost(requestedDate!=null?requestedDate : LocalDateTime.now());
    }


    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    @Override
    public String toString() {
        return "Dish { id = " + id +
                ", name = " + name +
                ", unitPrice = " + unitPrice +
                ", ingredients = " + ingredients +
                ", productPrice = " + productPrice + "}";
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Dish dish = (Dish) o;

        return Objects.equals(id, dish.id) && Objects.equals(name, dish.name) && Objects.equals(unitPrice, dish.unitPrice) && Objects.equals(ingredients, dish.ingredients);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id, name, unitPrice, ingredients);
    }

}