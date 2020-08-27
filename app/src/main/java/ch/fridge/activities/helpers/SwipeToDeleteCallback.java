package ch.fridge.activities.helpers;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import ch.fridge.R;


public abstract class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    private ColorDrawable backgroundColor = new ColorDrawable();
    private Drawable swipeIcon;

    public SwipeToDeleteCallback(Context context) {
        super(0, ItemTouchHelper.LEFT);
        swipeIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_white_24dp);
        backgroundColor.setColor(Color.RED);
    }

    public SwipeToDeleteCallback(@NonNull Drawable swipeIcon, @ColorInt int swipeBackgroundColor) {
        super(0, ItemTouchHelper.LEFT);
        this.swipeIcon = swipeIcon;
        backgroundColor.setColor(swipeBackgroundColor);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        int itemViewHeight = itemView.getBottom() - itemView.getTop();
        backgroundColor.setBounds(
                itemView.getRight() + ((int) dX),
                itemView.getTop(),
                itemView.getRight(),
                itemView.getBottom()
        );
        backgroundColor.draw(c);

        int top = itemView.getTop() + (itemViewHeight - swipeIcon.getIntrinsicHeight()) / 2;
        int deleteIconMargin = (itemViewHeight - swipeIcon.getIntrinsicHeight()) / 2;
        int left = itemView.getRight() - deleteIconMargin - swipeIcon.getIntrinsicWidth();
        int right = itemView.getRight() - deleteIconMargin;
        int bottom = top + swipeIcon.getIntrinsicHeight();

        swipeIcon.setBounds(left, top, right, bottom);
        swipeIcon.draw(c);

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }
}