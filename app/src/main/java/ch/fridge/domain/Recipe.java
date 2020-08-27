package ch.fridge.domain;

import java.io.Serializable;
import java.util.List;

public class Recipe extends RecipeOverview implements Serializable{
    private List<Ingredient> ingredients;
    private int persons;
    private List<String> steps;
    private String imageLargeUrl;

    public String getImageLargeUrl() {
        return imageLargeUrl;
    }

    public void setImageLargeUrl(String imageLargeUrl) {
        this.imageLargeUrl = imageLargeUrl;
    }

    public int getPersons() {
        return persons;
    }

    public void setPersons(int persons) {
        this.persons = persons;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getSteps() {
        return steps;
    }

    public void setSteps(List<String> steps) {
        this.steps = steps;
    }

    public boolean hasLargeImageUrl() {
        return imageLargeUrl != null && !imageLargeUrl.isEmpty();
    }
}
