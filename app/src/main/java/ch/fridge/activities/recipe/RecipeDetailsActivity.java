package ch.fridge.activities.recipe;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import ch.fridge.R;
import ch.fridge.activities.BaseActivity;
import ch.fridge.data.RecipeService;
import ch.fridge.data.RetrofitClient;
import ch.fridge.domain.Recipe;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeDetailsActivity extends BaseActivity {
    private RecipeService recipeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);
        Toolbar toolbar = findViewById(R.id.result_toolbar);
        setToolbar(toolbar);

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.recipe_load));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        String recipeId = getIntent().getStringExtra("RECIPE_ID");
        recipeService = RetrofitClient.getRetrofitInstance(getApplicationContext())
                .create(RecipeService.class);
        Call<Recipe> call = recipeService.getRecipe(recipeId);
        call.enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                if(response.code() == 200) {
                    showRecipe(response.body());
                } else {
                    Toast.makeText(RecipeDetailsActivity.this, R.string.recipedetails_load_error, Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(RecipeDetailsActivity.this, R.string.recipedetails_load_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setViewPager(Recipe recipe) {
        ViewPager pager = findViewById(R.id.recipe_pager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), recipe);
        pager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tab);
        tabLayout.setupWithViewPager(pager);
    }


    private void showRecipe(Recipe recipe) {
        ImageView imageRecipe = findViewById(R.id.recipe_image);
        if(recipe.hasLargeImageUrl()) {
            Picasso.Builder builder = new Picasso.Builder(this);
            builder.build().load(recipe.getImageLargeUrl())
                    .placeholder(R.drawable.fork_and_knife)
                    .error(R.drawable.fork_and_knife)
                    .into(imageRecipe);
        } else {
            imageRecipe.setImageResource(R.drawable.fork_and_knife);
        }
        setTitle(recipe.getName());

        RatingBar ratingBar = findViewById(R.id.recipe_ratingbar);
        ratingBar.setRating(recipe.getRating());

        setViewPager(recipe);
    }
}
