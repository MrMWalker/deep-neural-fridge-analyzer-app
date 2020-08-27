package ch.fridge.activities.recipe;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.fridge.R;
import ch.fridge.domain.Ingredient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IngredientsFragment extends android.support.v4.app.Fragment {

    public static final String Title = "Zutaten";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ingredients, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        TextView textPersonCount = getView().findViewById(R.id.ingredient_personcount_text);
        textPersonCount.setText(String.format("Zutaten für %s personen", getPersons()));

        RecyclerView ingredientView = getView().findViewById(R.id.ingredient_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        ingredientView.setLayoutManager(layoutManager);
        IngredientAdapter ingredientAdapter = new IngredientAdapter(getIngredients());
        ingredientView.setAdapter(ingredientAdapter);
        ingredientAdapter.notifyDataSetChanged();

        super.onViewCreated(view, savedInstanceState);
    }

    private List<Ingredient> getIngredients() {
        Bundle arguments = getArguments();
        Serializable serializableIngredients = arguments.getSerializable("INGREDIENTS");
        if (serializableIngredients != null) {
            return (ArrayList<Ingredient>) serializableIngredients;
        }
        return new ArrayList<>();
    }

    private int getPersons() {
        Bundle arguments = getArguments();
        return arguments.getInt("PERSONS");
    }
}
