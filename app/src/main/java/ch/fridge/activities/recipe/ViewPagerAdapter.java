package ch.fridge.activities.recipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import ch.fridge.domain.Ingredient;
import ch.fridge.domain.Recipe;

import java.util.ArrayList;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private static final int TAB_COUNT = 2;
    private final Recipe recipe;

    public ViewPagerAdapter(FragmentManager fragmentManager, Recipe recipe) {
        super(fragmentManager);
        this.recipe = recipe;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle arguments =new Bundle();

        switch (position) {
            case 0:
                IngredientsFragment ingredients = new IngredientsFragment();
                arguments.putSerializable("INGREDIENTS", (ArrayList<Ingredient>)recipe.getIngredients());
                arguments.putInt("PERSONS", recipe.getPersons());
                ingredients.setArguments(arguments);
                return ingredients;
            case 1:
                StepsFragment steps = new StepsFragment();
                arguments.putStringArrayList("STEPS", (ArrayList<String>)recipe.getSteps());
                arguments.putString("COOKING_TIME", recipe.getCookingTimeDisplay());
                steps.setArguments(arguments);
                return steps;
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return IngredientsFragment.Title;

            case 1:
                return StepsFragment.Title;
        }
        return super.getPageTitle(position);
    }
}