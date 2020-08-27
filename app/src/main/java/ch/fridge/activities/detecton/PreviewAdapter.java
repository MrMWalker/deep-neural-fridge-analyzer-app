package ch.fridge.activities.detecton;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import ch.fridge.R;
import ch.fridge.activities.helpers.ItemClickListener;
import ch.fridge.activities.helpers.ItemLongClickListener;
import ch.fridge.domain.DetectedImage;

import java.util.ArrayList;
import java.util.List;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.MyViewHolder> {

    private List<DetectedImage> detectedImages;
    private List<MyViewHolder> viewHolders;
    private ItemClickListener clickListener;
    private ItemLongClickListener longClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView preview;

        public MyViewHolder(View view) {
            super(view);

            preview = view.findViewById(R.id.main_preview_image);
        }
    }

    public PreviewAdapter(List<DetectedImage> detectedImages) {
        this.detectedImages = detectedImages;
        this.viewHolders = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.preview_image_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(itemView);
        viewHolders.add(viewHolder);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PreviewAdapter.MyViewHolder holder, int position) {
        DetectedImage detectedImage = detectedImages.get(position);
        holder.preview.setImageBitmap(detectedImage.getImage());
        if (clickListener != null) {
            holder.preview.setOnClickListener(view -> clickListener.onClick(view, position));
        }
        if (longClickListener != null) {
            holder.preview.setOnLongClickListener(view -> longClickListener.onLongClick(view, position));
        }
        highlightItem(position);
    }

    @Override
    public int getItemCount() {
        return detectedImages.size();
    }

    public void setClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLongClickListener(ItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public void highlightItem(int position) {
        viewHolders.forEach(x -> x.preview.setBackgroundColor(Color.WHITE));
        MyViewHolder viewHolder = viewHolders.get(position);
        viewHolder.preview.setBackgroundColor(Color.GRAY);
    }

    public void addItem(DetectedImage detectedImage) {
        detectedImages.add(detectedImage);
        notifyItemInserted(detectedImages.size());
    }

    public void removeItem(int position) {
        detectedImages.remove(position);
        notifyItemRemoved(position);
    }

}