package ch.fridge.domain;

import android.graphics.Bitmap;

import java.io.Serializable;

public class RecipeOverview implements Serializable {
    private String id;
    private String name;
    private int cookingTime;
    private String imageUrl;
    private float rating;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public String getCookingTimeDisplay() {
        if(cookingTime == 0) {
            return "";
        }
        return String.format("%s min", cookingTime);
    }

    public boolean hasImageUrl() {
        return imageUrl != null && !imageUrl.isEmpty();
    }
}
