package generator;

import pojo.Order;

import java.util.List;

public class OrderGenerator {

    public static Order generateOrderByIngredients(List<String> ingredients){
        return new Order(ingredients);
    }

    public static Order generateEmptyOrder(){
        return new Order();
    }
}
