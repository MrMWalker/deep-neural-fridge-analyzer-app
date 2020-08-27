package ch.fridge.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import ch.fridge.R;
import ch.fridge.activities.detecton.MainActivity;
import ch.fridge.activities.recipe.RecipeGeneratorActivity;
import ch.fridge.activities.settings.SettingsActivity;
import ch.fridge.activities.shoppinglist.ShoppingListActivity;
import ch.fridge.detection.DetectorFactory;
import ch.fridge.domain.Label;
import ch.fridge.utilities.Logger;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseActivity extends AppCompatActivity {
    protected static final ch.fridge.utilities.Logger Logger = new Logger();
    protected SharedPreferences preferences;
    private List<Label> labels;

    protected void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(view -> {
            this.finish();
        });
    }

    protected List<Label> getLabels() {
        if (labels == null) {
            labels = DetectorFactory.getLabels(getAssets());
        }

        return labels;
    }

    protected List<String> getLabelsText() {
        return getLabels().stream().map(Label::getDisplayText).collect(Collectors.toList());
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSettings:
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            case R.id.menuShoppingList:
                Intent shoppingListIntent = new Intent(getApplicationContext(), ShoppingListActivity.class);
                startActivity(shoppingListIntent);
                return true;
            case R.id.menuRecipeGenerator:
                Intent recipeGeneratorIntent = new Intent(getApplicationContext(), RecipeGeneratorActivity.class);
                startActivity(recipeGeneratorIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        if(!(this instanceof MainActivity)) {
            menu.removeItem(R.id.menuDemo);
        }

        return true;
    }

    protected void disableDeathOnFileUriExposure() {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                Method method = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                method.invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
