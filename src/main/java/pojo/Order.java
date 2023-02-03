package pojo;

import java.util.List;
import java.util.Objects;

public class Order {
    private List<String> ingredients;
    private Long number;
    private String name;

    public Order(List<String > ingredients) {
        this.ingredients = ingredients;
    }

    public Order() {
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String > ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(ingredients, order.ingredients) && Objects.equals(number, order.number) && Objects.equals(name, order.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredients, number, name);
    }

    @Override
    public String toString() {
        return "Order{" +
                "ingredients=" + ingredients +
                ", number=" + number +
                ", name='" + name + '\'' +
                '}';
    }
}
