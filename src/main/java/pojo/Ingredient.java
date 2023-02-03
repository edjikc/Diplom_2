package pojo;

import com.google.gson.annotations.SerializedName;

public class Ingredient {
    @SerializedName("_id")
    private String id;

    public Ingredient(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id='" + id + '\'' +
                '}';
    }
}
