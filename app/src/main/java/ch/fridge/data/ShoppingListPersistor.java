package ch.fridge.data;

import ch.fridge.domain.ShoppingItem;

import java.util.List;

public interface ShoppingListPersistor {
    List<ShoppingItem> getShoppingList();
    void saveShoppingList(List<ShoppingItem> shoppingList);
}

