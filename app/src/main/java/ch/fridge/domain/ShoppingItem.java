package ch.fridge.domain;

import java.io.Serializable;

public class ShoppingItem implements Serializable {
    public ShoppingItem() {
    }

    public ShoppingItem(String name, boolean isChecked) {
        this.name = name;
        this.isChecked = isChecked;
    }

    private String name;
    private boolean isChecked;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void toggleChecked() {
        isChecked = !isChecked;
    }
}
