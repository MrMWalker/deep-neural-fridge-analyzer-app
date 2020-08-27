package ch.fridge.data;

import android.content.Context;

import ch.fridge.domain.ShoppingItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListFilePersistor implements ShoppingListPersistor {
    private final Context context;
    private static final String FileName = "shoppingList.json";

    public ShoppingListFilePersistor(Context context) {
        this.context = context;
    }

    @Override
    public List<ShoppingItem> getShoppingList() {

        String shoppingListString;
        try {
            FileInputStream fis = context.openFileInput(FileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            shoppingListString = sb.toString();
        } catch (IOException ioException) {
            return new ArrayList<>();
        }

        List<ShoppingItem> shoppingList;
        try {
            JSONObject shoppingListJson = new JSONObject(shoppingListString);
            shoppingList = convertJsonToShoppingList(shoppingListJson);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return shoppingList;
    }

    @Override
    public void saveShoppingList(List<ShoppingItem> shoppingList) {
        JSONObject shoppingListJson;
        try {
            shoppingListJson = convertShoppingListToJsonObject(shoppingList);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        String jsonString = shoppingListJson.toString();
        try {
            FileOutputStream fos = context.openFileOutput(FileName, Context.MODE_PRIVATE);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
        } catch (IOException ioException) {
            throw new RuntimeException("Could not save Shopping List", ioException);
        }
    }

    private List<ShoppingItem> convertJsonToShoppingList(JSONObject shoppingListJson) throws JSONException {
        List<ShoppingItem> shoppingList = new ArrayList<>();
        JSONArray shoppingItems = shoppingListJson.getJSONArray("ShoppingList");
        for (int i = 0; i < shoppingItems.length(); i++) {
            JSONObject jsonObject = shoppingItems.getJSONObject(i);
            ShoppingItem item = convertJsonObjectToShoppingItem(jsonObject);
            shoppingList.add(item);
        }

        return shoppingList;
    }

    private ShoppingItem convertJsonObjectToShoppingItem(JSONObject itemJson) throws JSONException {
        ShoppingItem item = new ShoppingItem();
        item.setName(itemJson.getString("name"));
        item.setChecked(itemJson.getBoolean("isChecked"));
        return item;
    }

    private JSONObject convertShoppingListToJsonObject(List<ShoppingItem> shoppingList) throws JSONException {
        JSONObject obj = new JSONObject();
        JSONArray shoppingItemsJson = new JSONArray();
        for (ShoppingItem item : shoppingList) {
            JSONObject shoppingItemJson = new JSONObject();
            shoppingItemJson.put("isChecked", item.isChecked());
            shoppingItemJson.put("name", item.getName());
            shoppingItemsJson.put(shoppingItemJson);
        }

        obj.put("ShoppingList", shoppingItemsJson);
        return obj;
    }
}
