package ch.fridge.domain;

import android.graphics.Bitmap;

import ch.fridge.detection.BitmapProxy;

import java.io.Serializable;

public class DetectedLabel implements Serializable {
    private Label label;
    private BitmapProxy imageProxy;

    public DetectedLabel(Label label, Bitmap image) {
        this.label = label;
        this.imageProxy = new BitmapProxy(image);
    }

    public Label getLabel() {
        return label;
    }

    public Bitmap getImage() {
        return imageProxy.getBitmap();
    }
}
