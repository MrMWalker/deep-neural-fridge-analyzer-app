package ch.fridge.activities.recipe;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import ch.fridge.R;

import java.util.ArrayList;
import java.util.List;


public class StepsFragment extends android.support.v4.app.Fragment {
    public static final String Title = "Zubereitung";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_steps, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ArrayAdapter<String> stepsAdapter = new ArrayAdapter<>(this.getContext(), R.layout.simple_row, getSteps());
        ListView stepsList = getView().findViewById(R.id.steps_list);
        stepsList.setAdapter(stepsAdapter);
        stepsAdapter.notifyDataSetChanged();

        TextView textCookingTime = getView().findViewById(R.id.steps_cookingtime_text);
        textCookingTime.setText(String.format("Dauer: %s", getCookingTime()));

        super.onViewCreated(view, savedInstanceState);
    }

    private String getCookingTime() {
        return getArguments().getString("COOKING_TIME");
    }

    private List<String> getSteps() {
        Bundle arguments = getArguments();
        List<String> steps = arguments.getStringArrayList("STEPS");
        if (steps != null) {
            return steps;
        }
        return new ArrayList<>();
    }
}
