package ch.fridge.domain;

import java.io.Serializable;

public class Label implements Serializable {
    private int id;
    private String name;
    private String english;
    private String german;

    public Label(int id, String name, String english, String german) {
        this.id = id;
        this.name = name;
        this.english = english;
        this.german = german;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEnglish() {
        return english;
    }

    public String getGerman() {
        return german;
    }

    public String getDisplayText() {
        return german;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Label label = (Label) o;

        return id == label.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}


