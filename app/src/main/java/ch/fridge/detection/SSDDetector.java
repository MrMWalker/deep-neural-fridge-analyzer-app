package ch.fridge.detection;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;

import ch.fridge.domain.Detection;
import ch.fridge.domain.Label;
import ch.fridge.utilities.ImageUtils;

import java.io.IOException;
import java.util.List;

public class SSDDetector extends BaseDetector implements Detector {
    private final int inputSize;

    public SSDDetector(AssetManager assetManager, String modelFilename, List<Label> labels, int inputSize) throws IOException {
        super(assetManager, modelFilename, labels);
        this.inputSize = inputSize;
    }

    @Override
    public DetectorType getDetectorType() {
        return DetectorType.SSD;
    }

    @Override
    public List<Detection> detectImage(final Bitmap image) {
        // Preprocess image
        Matrix frameToCropTransform = ImageUtils.getTransformationMatrix(
                image.getWidth(), image.getHeight(),
                inputSize, inputSize,
                0, false);

        Matrix cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        Bitmap scaledImage = Bitmap.createScaledBitmap(
                image, inputSize, inputSize, false);

        // Run detection call
        List<Detection> detections = inferDetection(scaledImage);

        // Rescale detections
        for (final Detection detection : detections) {
            final RectF location = detection.getLocation();
            cropToFrameTransform.mapRect(location);
            detection.setLocation(location);
        }

        return detections;
    }
}

