package ch.fridge.activities.detecton;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import ch.fridge.R;
import ch.fridge.activities.BaseActivity;
import ch.fridge.activities.recipe.RecipeGeneratorActivity;
import ch.fridge.activities.shoppinglist.ShoppingListActivity;
import ch.fridge.domain.DetectedLabel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ResultActivity extends BaseActivity {

    private List<DetectedLabel> detectedLabels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setToolbar(toolbar);

        detectedLabels = getDetectedLabels();
        detectedLabels.sort(Comparator.comparing(detectedLabel -> detectedLabel.getLabel().getDisplayText()));

        RecyclerView resultsView = findViewById(R.id.result_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        resultsView.setLayoutManager(layoutManager);
        ResultAdapter resultAdapter = new ResultAdapter(detectedLabels);
        resultAdapter.notifyDataSetChanged();
        resultsView.setAdapter(resultAdapter);

        FloatingActionButton buttonShoppingList = findViewById(R.id.result_shoppinglist_button);
        buttonShoppingList.setOnClickListener(view -> {
            List<String> uniqueDetections = getUniqueDetections();
            Intent intent = new Intent(getApplicationContext(), ShoppingListActivity.class);
            intent.putExtra("UNIQUE_DETECTIONS", new ArrayList<>(uniqueDetections));
            startActivity(intent);
        });

        FloatingActionButton buttonRecipeGenerator = findViewById(R.id.result_recipe_button);
        buttonRecipeGenerator.setOnClickListener(view -> {
            List<String> uniqueDetections = getUniqueDetections();
            Intent intent = new Intent(getApplicationContext(), RecipeGeneratorActivity.class);
            intent.putExtra("UNIQUE_DETECTIONS", new ArrayList<>(uniqueDetections));
            startActivity(intent);
        });
    }

    private List<String> getUniqueDetections() {
        return detectedLabels.stream().map(x -> x.getLabel().getDisplayText())
                .distinct().collect(Collectors.toList());
    }

    private List<DetectedLabel> getDetectedLabels() {
        Serializable detectedLabelsSerializable = getIntent().getSerializableExtra("DETECTED_LABELS");
        if (detectedLabelsSerializable != null) {
            return (ArrayList<DetectedLabel>) detectedLabelsSerializable;
        }

        return new ArrayList<>();
    }
}
