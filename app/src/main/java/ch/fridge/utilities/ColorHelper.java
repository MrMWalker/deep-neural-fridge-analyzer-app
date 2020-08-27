package ch.fridge.utilities;

import android.graphics.Color;

import ch.fridge.domain.Label;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ColorHelper {
    private Map<Label, Integer> colorMap = new HashMap<>();

    public int getColor(Label label) {
        if(colorMap.containsKey(label)) {
            return colorMap.get(label);
        }

        Random random = new Random();
        int color;
        do {
            color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        } while(colorMap.containsValue(color));

        colorMap.put(label, color);

        return color;
    }
}
