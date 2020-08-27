package ch.fridge.data;

import ch.fridge.domain.Recipe;
import ch.fridge.domain.RecipeOverview;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RecipeService {
    @POST("/recipes")
    Call<List<RecipeOverview>> getRecipes(@Body List<String> ingredients);
    @GET("/recipe/{id}")
    Call<Recipe> getRecipe(@Path("id") String recipeId);
}

