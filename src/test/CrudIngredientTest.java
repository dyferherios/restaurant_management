package test;

import entity.Ingredient;

import java.util.List;

public class CrudIngredientTest implements CrudRestaurantManagementTest<Ingredient>{
    @Override
    public void findAll_ok() {

    }

    @Override
    public Ingredient findByName_ok(String IngredientName) {
        return null;
    }

    @Override
    public Ingredient save_ok(Ingredient ingredient) {
        return null;
    }
}
