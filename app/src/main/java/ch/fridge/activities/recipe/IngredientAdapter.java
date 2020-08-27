package ch.fridge.activities.recipe;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ch.fridge.R;
import ch.fridge.domain.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.MyViewHolder> {
    private final List<Ingredient> ingredients;
    private final List<MyViewHolder> viewHolders;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView ingredient;
        private TextView amount;

        public MyViewHolder(View view) {
            super(view);

            ingredient = view.findViewById(R.id.ingredient_text);
            amount = view.findViewById(R.id.ingredient_amount);
        }
    }

    public IngredientAdapter(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        this.viewHolders = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        viewHolders.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(IngredientAdapter.MyViewHolder holder, int position) {
        Ingredient ingredient = ingredients.get(position);
        holder.ingredient.setText(ingredient.getName());
        holder.amount.setText(ingredient.getAmountWithUnit());
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }
}