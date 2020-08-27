package ch.fridge.activities.recipe;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import ch.fridge.R;
import ch.fridge.domain.RecipeOverview;
import ch.fridge.activities.helpers.ItemClickListener;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.MyViewHolder> {

    private List<RecipeOverview> recipes;
    private List<MyViewHolder> viewHolders;
    private ItemClickListener clickListener;
    private final Context context;

    public RecipeOverview getRecipe(int position) {
        return recipes.get(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView textName;
        private TextView textCookingTime;
        private RatingBar ratingBar;
        private CardView cardView;

        public MyViewHolder(View view) {
            super(view);

            image = view.findViewById(R.id.recipe_image);
            textName = view.findViewById(R.id.recipe_text);
            textCookingTime = view.findViewById(R.id.recipe_cookingtime);
            ratingBar = view.findViewById(R.id.recipe_rating);
            cardView = view.findViewById(R.id.cardView);
        }
    }

    public RecipeAdapter(Context context) {
        this.viewHolders = new ArrayList<>();
        this.recipes = new ArrayList<>();
        this.context = context;
    }

    public void clearRecipes() {
        recipes.clear();
        notifyDataSetChanged();
    }

    public void setRecipes(List<RecipeOverview> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recipe_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        viewHolders.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecipeAdapter.MyViewHolder holder, int position) {
        RecipeOverview recipe = recipes.get(position);
        if (clickListener != null) {
            holder.cardView.setOnClickListener(view -> clickListener.onClick(view, position));
        }

        holder.textName.setText(recipe.getName());
        holder.textCookingTime.setText(recipe.getCookingTimeDisplay());

        Picasso.Builder builder = new Picasso.Builder(context);
        builder.downloader(new OkHttp3Downloader(context));

        if(recipe.hasImageUrl()) {
            builder.build().load(recipe.getImageUrl())
                    .placeholder(R.drawable.fork_and_knife)
                    .error(R.drawable.fork_and_knife)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.fork_and_knife);
        }

        holder.ratingBar.setRating(recipe.getRating());
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }
}