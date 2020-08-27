package ch.fridge.domain;

import android.graphics.Bitmap;

import java.util.List;
import java.util.stream.Collectors;

public class DetectedImage {
    private Bitmap image;
    private List<Detection> detections;
    private float minimumConfidence;

    public DetectedImage(Bitmap image, List<Detection> detections, float minimumConfidence) {
        this.image = image;
        this.detections = detections;
        this.minimumConfidence = minimumConfidence;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public float getMinimumConfidence() {
        return minimumConfidence;
    }

    public void setMinimumConfidence(float minimumConfidence) {
        this.minimumConfidence = minimumConfidence;
    }

    public List<Detection> getFilteredDetections() {
        return detections.stream().filter(x -> x.getConfidence() >= minimumConfidence).collect(Collectors.toList());
    }
}
