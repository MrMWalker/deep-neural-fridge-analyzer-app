package ch.fridge.detection;

import android.content.Context;
import android.content.res.AssetManager;

import ch.fridge.domain.Label;
import ch.fridge.utilities.LabelsLoader;
import ch.fridge.utilities.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetectorFactory {
    private static final Map<DetectorType, String> DetectorTypePaths;
    private static final String LabelsFile = "fridge_labels-69c.json";

    static {
        DetectorTypePaths = new HashMap<>();
        DetectorTypePaths.put(DetectorType.RFCN, "rfcn_69c_frozen_graph_run1_mAP5234_quantized.pb");
        DetectorTypePaths.put(DetectorType.SSD, "ssd_69c_frozen_graph_mAP2968_quantized.pb");
    }

    public static Detector createDetector(Context context, Logger logger, DetectorType detectorType, List<Label> labels) {
        String modelFileName = DetectorTypePaths.get(detectorType);
        try {
            if (detectorType == DetectorType.RFCN) {
                return new RFCNDetector(context.getAssets(), modelFileName, labels);

            } else if (detectorType == DetectorType.SSD) {
                return new SSDDetector(context.getAssets(), modelFileName, labels, 600);
            }
        } catch (IOException e) {
            logger.e("Exception initializing classifier!", e);
            throw new RuntimeException(e);
        }

        throw new RuntimeException("DetectorType is not supported!");
    }

    public static List<Label> getLabels(AssetManager assetManager) {
        return LabelsLoader.loadLabels(assetManager, LabelsFile);
    }
}
