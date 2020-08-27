package ch.fridge.activities.recipe;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.Toast;

import ch.fridge.R;
import ch.fridge.activities.BaseActivity;
import ch.fridge.data.RecipeService;
import ch.fridge.data.RetrofitClient;
import ch.fridge.domain.RecipeOverview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeGeneratorActivity extends BaseActivity {
    private RecipeService recipeService;
    private List<String> ingredients;
    private List<String> availableIngredients;
    private RecipeAdapter recipeAdapter;
    private Button buttonIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_generator);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setToolbar(toolbar);

        RecyclerView recipeView = findViewById(R.id.recipe_list);
        recipeAdapter = new RecipeAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RecipeGeneratorActivity.this);
        recipeView.setLayoutManager(layoutManager);
        recipeView.setAdapter(recipeAdapter);
        recipeAdapter.notifyDataSetChanged();
        recipeAdapter.setClickListener((view, position) -> {
            RecipeOverview recipe = recipeAdapter.getRecipe(position);
            Intent intent = new Intent(getApplicationContext(), RecipeDetailsActivity.class);
            intent.putExtra("RECIPE_ID", recipe.getId());
            startActivity(intent);
        });

        availableIngredients = getLabelsText().stream().sorted().collect(Collectors.toList());

        recipeService = RetrofitClient.getRetrofitInstance(getApplicationContext())
                .create(RecipeService.class);
        ingredients = getUniqueIngredients();
        loadRecipes(ingredients);

        buttonIngredients = findViewById(R.id.recipe_generator_button_ingredients);
        buttonIngredients.setOnClickListener(view -> {
            List<Boolean> checkedItems = availableIngredients.stream()
                    .map(x -> ingredients.contains(x))
                    .collect(Collectors.toList());
            boolean[] checkedItemsArray = toPrimitveArray(checkedItems);

            final CharSequence[] availableIngredientsArray = availableIngredients
                    .toArray(new CharSequence[availableIngredients.size()]);

            final List<String> selectedIngredients = new ArrayList<>(ingredients);
            AlertDialog dialog = new AlertDialog.Builder(RecipeGeneratorActivity.this)
                    .setTitle(R.string.recipe_ingredients)
                    .setMultiChoiceItems(availableIngredientsArray, checkedItemsArray,
                            (dialogInterface, which, isChecked) -> {
                                String ingredient = availableIngredients.get(which);
                                if (isChecked) {
                                    selectedIngredients.add(ingredient);
                                } else if (selectedIngredients.contains(ingredient)) {
                                    selectedIngredients.remove(ingredient);
                                }
                            })
                    .setPositiveButton(R.string.ok, (dialogInterface, which) -> {
                        ingredients = selectedIngredients;
                        loadRecipes(ingredients);
                        updateButtonIngredientsText();
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .setNeutralButton(R.string.recipe_ingredients_clear_all, (dialogInterface, which) -> {
                        ingredients.clear();
                        recipeAdapter.clearRecipes();
                        updateButtonIngredientsText();
                    })
                    .setCancelable(false)
                    .setIcon(R.drawable.boiler_pan_icon)
                    .create();
            dialog.show();
        });
        updateButtonIngredientsText();
    }

    private void updateButtonIngredientsText() {
        int countIngredients = ingredients.size();
        String text = countIngredients + " Zutat" +
                (countIngredients > 1 || countIngredients == 0 ? "en" : "");
        buttonIngredients.setText(text);
    }

    private static boolean[] toPrimitveArray(List<Boolean> booleans) {
        boolean[] booleanArray = new boolean[booleans.size()];
        for (int i = 0; i < booleans.size(); i++) {
            booleanArray[i] = booleans.get(i);
        }

        return booleanArray;
    }

    private void loadRecipes(List<String> uniqueIngredients) {
        if (uniqueIngredients.size() <= 0) {
            recipeAdapter.clearRecipes();
            Toast.makeText(RecipeGeneratorActivity.this, R.string.recipe_no_ingredients_selected, Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.recipe_load));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        Call<List<RecipeOverview>> call = recipeService.getRecipes(uniqueIngredients);
        call.enqueue(new Callback<List<RecipeOverview>>() {
            @Override
            public void onResponse(Call<List<RecipeOverview>> call, Response<List<RecipeOverview>> response) {
                if (response.code() == 200) {
                    List<RecipeOverview> recipes = response.body();
                    recipeAdapter.setRecipes(recipes);
                } else {
                    Toast.makeText(RecipeGeneratorActivity.this, R.string.recipe_load_error, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<RecipeOverview>> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(RecipeGeneratorActivity.this, R.string.recipe_load_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<String> getUniqueIngredients() {
        Serializable detectedLabelsSerializable = getIntent().getSerializableExtra("UNIQUE_DETECTIONS");
        if (detectedLabelsSerializable != null) {
            return (ArrayList<String>) detectedLabelsSerializable;
        }

        return new ArrayList<>();
    }

}
