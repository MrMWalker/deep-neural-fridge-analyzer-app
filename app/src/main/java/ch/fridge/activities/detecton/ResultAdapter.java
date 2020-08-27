package ch.fridge.activities.detecton;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ch.fridge.R;
import ch.fridge.activities.helpers.ItemClickListener;
import ch.fridge.activities.helpers.ItemLongClickListener;
import ch.fridge.domain.DetectedLabel;

import java.util.ArrayList;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.MyViewHolder> {

    private List<DetectedLabel> detectedLabels;
    private List<MyViewHolder> viewHolders;
    private ItemClickListener clickListener;
    private ItemLongClickListener longClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView text;

        public MyViewHolder(View view) {
            super(view);

            image = view.findViewById(R.id.result_image);
            image.setBackgroundColor(Color.GRAY);
            text = view.findViewById(R.id.result_text);
        }
    }

    public ResultAdapter(List<DetectedLabel> detectedLabels) {
        this.detectedLabels = detectedLabels;
        this.viewHolders = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.result_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        viewHolders.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ResultAdapter.MyViewHolder holder, int position) {
        DetectedLabel detectedLabel = detectedLabels.get(position);
        holder.image.setImageBitmap(detectedLabel.getImage());
        holder.text.setText(detectedLabel.getLabel().getDisplayText());
        if (clickListener != null) {
            holder.image.setOnClickListener(view -> clickListener.onClick(view, position));
        }
        if (longClickListener != null) {
            holder.image.setOnLongClickListener(view -> longClickListener.onLongClick(view, position));
        }
    }

    @Override
    public int getItemCount() {
        return detectedLabels.size();
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLongClickListener(ItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }
}