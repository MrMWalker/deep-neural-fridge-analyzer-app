package ch.fridge.activities.shoppinglist;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import ch.fridge.R;
import ch.fridge.activities.BaseActivity;
import ch.fridge.activities.helpers.SwipeToDeleteCallback;
import ch.fridge.data.ShoppingListFilePersistor;
import ch.fridge.data.ShoppingListPersistor;
import ch.fridge.domain.Label;
import ch.fridge.domain.ShoppingItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ShoppingListActivity extends BaseActivity {
    private List<ShoppingItem> shoppingList;
    private ShoppingListPersistor shoppingListPersistor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoppinglist);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setToolbar(toolbar);
        shoppingListPersistor = new ShoppingListFilePersistor(getApplicationContext());

        shoppingList = getShoppingList();
        RecyclerView shoppingListView = findViewById(R.id.shoppinglist_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        shoppingListView.setLayoutManager(layoutManager);
        final ShoppingListAdapter shoppingListAdapter = new ShoppingListAdapter(shoppingList);
        shoppingListAdapter.notifyDataSetChanged();
        shoppingListView.setAdapter(shoppingListAdapter);
        shoppingListAdapter.setClickListener((view, position) -> shoppingListAdapter.toggleChecked(position));

        SwipeToDeleteCallback swipeHandler = new SwipeToDeleteCallback(getApplicationContext()) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                shoppingListAdapter.removeAt(position);
                Toast.makeText(ShoppingListActivity.this, "The image has been successfully deleted.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHandler);
        itemTouchHelper.attachToRecyclerView(shoppingListView);

        ImageButton buttonAddItem = findViewById(R.id.shoppinglist_add_button);
        buttonAddItem.setOnClickListener(view -> {
            final AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_dropdown_item_1line, getLabelsText());
            autoCompleteTextView.setAdapter(adapter);
            AlertDialog dialog = new AlertDialog.Builder(ShoppingListActivity.this)
                    .setTitle(R.string.shoppinglist_title_add_item)
                    .setMessage(R.string.shoppinglist_add_item)
                    .setView(autoCompleteTextView)
                    .setPositiveButton(R.string.shoppinglist_add_item_confirm, (dialogInterface, i) -> {
                        String item = String.valueOf(autoCompleteTextView.getText());
                        shoppingListAdapter.addItem(new ShoppingItem(item, false));
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .create();
            dialog.show();
        });
    }

    private List<ShoppingItem> getShoppingList() {
        List<ShoppingItem> shoppingList;
        Serializable serializableDetections = getIntent().getSerializableExtra("UNIQUE_DETECTIONS");
        if (serializableDetections == null) {
            // Load last shopping list
            shoppingList = shoppingListPersistor.getShoppingList();

        } else {
            // Create shopping list from detections
            List<String> detections = (ArrayList<String>) serializableDetections;
            shoppingList = generateShoppingListFromDetections(detections);
            shoppingListPersistor.saveShoppingList(shoppingList);
        }

        return shoppingList;
    }

    private List<ShoppingItem> generateShoppingListFromDetections(List<String> detections) {
        Set<Integer> shoppingListPreferenceIds = preferences.getStringSet("pref_shoppinglist", new HashSet<>())
                .stream().map(Integer::parseInt).collect(Collectors.toSet());
        List<String> shoppingListPreferences = getLabels().stream().filter(x -> shoppingListPreferenceIds.contains(x.getId()))
                .map(Label::getDisplayText).collect(Collectors.toList());

        List<ShoppingItem> shoppingList = shoppingListPreferences.stream()
                .filter(x -> !detections.contains(x))
                .map(x -> new ShoppingItem(x, false))
                .collect(Collectors.toList());
        return shoppingList;
    }

    @Override
    protected void onPause() {
        shoppingListPersistor.saveShoppingList(shoppingList);
        super.onPause();
    }
}
