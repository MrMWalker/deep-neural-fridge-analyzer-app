package ch.fridge.activities.shoppinglist;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import ch.fridge.R;
import ch.fridge.activities.helpers.ItemLongClickListener;
import ch.fridge.activities.helpers.ItemClickListener;
import ch.fridge.domain.ShoppingItem;

import java.util.ArrayList;
import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.MyViewHolder> {

    private List<ShoppingItem> shoppingList;
    private List<MyViewHolder> viewHolders;
    private ItemClickListener clickListener;
    private ItemLongClickListener longClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;

        public MyViewHolder(View view) {
            super(view);

            checkBox = view.findViewById(R.id.shoppinglist_checkbox);
        }
    }

    public ShoppingListAdapter(List<ShoppingItem> shoppingList) {
        this.shoppingList = shoppingList;
        this.viewHolders = new ArrayList<>();
    }

    public void removeAt(int position) {
        shoppingList.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(ShoppingItem shoppingItem) {
        shoppingList.add(shoppingItem);
        notifyItemInserted(shoppingList.size());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shoppinglist_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        viewHolders.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ShoppingListAdapter.MyViewHolder holder, int position) {
        ShoppingItem shoppingItem = shoppingList.get(position);
        holder.checkBox.setText(shoppingItem.getName());
        setChecked(holder.checkBox, shoppingItem.isChecked());
        if (clickListener != null) {
            holder.checkBox.setOnClickListener(view -> clickListener.onClick(view, position));
        }
        if (longClickListener != null) {
            holder.checkBox.setOnLongClickListener(view -> longClickListener.onLongClick(view, position));
        }
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    private void setChecked(CheckBox checkBox, boolean isChecked) {
        checkBox.setChecked(isChecked);
        if (isChecked) {
            checkBox.setPaintFlags(checkBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            checkBox.setPaintFlags(checkBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    public void setChecked(int position, boolean isChecked) {
        if (viewHolders.size() > position) {
            ShoppingListAdapter.MyViewHolder viewHolder = viewHolders.get(position);
            setChecked(viewHolder.checkBox, isChecked);
        }
    }

    public void toggleChecked(int position) {
        if (shoppingList.size() < position) {
            return;
        }
        ShoppingItem shoppingItem = shoppingList.get(position);
        shoppingItem.toggleChecked();
        setChecked(position, shoppingItem.isChecked());
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLongClickListener(ItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }
}