package ch.fridge.domain;

import java.io.Serializable;

public class Ingredient implements Serializable {
    private String name;
    private String unit;
    private String amount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmountWithUnit() {
        if(unit == null || unit.isEmpty()) {
            return amount != null ? amount : "";
        }

        if(amount == null || amount.isEmpty()) {
            return unit;
        }

        return String.format("%s %s", getAmount(), getUnit());
    }
}
