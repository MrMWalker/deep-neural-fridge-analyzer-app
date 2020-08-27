package ch.fridge.detection;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import ch.fridge.domain.Detection;
import ch.fridge.domain.Label;

import java.io.IOException;
import java.util.List;

public class RFCNDetector extends BaseDetector implements Detector {

    public RFCNDetector(AssetManager assetManager, String modelFilename, List<Label> labels) throws IOException {
        super(assetManager, modelFilename, labels);
    }

    @Override
    public DetectorType getDetectorType() {
        return DetectorType.RFCN;
    }

    @Override
    public List<Detection> detectImage(Bitmap image) {
        List<Detection> detections = inferDetection(image);
        return detections;
    }
}
