package ch.fridge.detection;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Trace;

import ch.fridge.domain.Detection;
import ch.fridge.domain.Label;
import ch.fridge.utilities.Logger;

import org.tensorflow.Graph;
import org.tensorflow.Operation;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public abstract class BaseDetector  {
    protected static final ch.fridge.utilities.Logger Logger = new Logger();
    // Only return this many results.
    public static final int MAX_RESULTS = 300;

    // Config values.
    private String inputName;

    // Pre-allocated buffers.
    private List<Label> labels;
    private float[] outputLocations;
    private float[] outputScores;
    private float[] outputClasses;
    private float[] outputNumDetections;
    private String[] outputNames;

    private boolean logStats = false;

    private TensorFlowInferenceInterface inferenceInterface;


    public BaseDetector(AssetManager assetManager, String modelFilename, List<Label> labels) throws IOException {
        this.labels = new ArrayList<>(labels);
        // Add background label
        this.labels.add(0, new Label(0, "Background", "-", "-"));
        Logger.w("Loaded " + labels.size() + " labels.");

        inferenceInterface = new TensorFlowInferenceInterface(assetManager, modelFilename);

        final Graph graph = inferenceInterface.graph();

        inputName = "image_tensor";
        // The inputName node has a shape of [N, H, W, C], where
        // N is the batch size
        // H = W are the height and width
        // C is the number of channels (3 for our purposes - RGB)
        final Operation inputOp = graph.operation(inputName);
        if (inputOp == null) {
            throw new RuntimeException("Failed to find input Node '" + inputName + "'");
        }
        // The outputScoresName node has a shape of [N, NumLocations], where N
        // is the batch size.
        final Operation outputOp1 = graph.operation("detection_scores");
        if (outputOp1 == null) {
            throw new RuntimeException("Failed to find output Node 'detection_scores'");
        }
        final Operation outputOp2 = graph.operation("detection_boxes");
        if (outputOp2 == null) {
            throw new RuntimeException("Failed to find output Node 'detection_boxes'");
        }
        final Operation outputOp3 = graph.operation("detection_classes");
        if (outputOp3 == null) {
            throw new RuntimeException("Failed to find output Node 'detection_classes'");
        }

        // Pre-allocate buffers.
        outputNames = new String[]{
                "detection_boxes", "detection_scores",
                "detection_classes", "num_detections"};

        outputScores = new float[MAX_RESULTS];
        outputLocations = new float[MAX_RESULTS * 4];
        outputClasses = new float[MAX_RESULTS];
        outputNumDetections = new float[1];
    }

    protected List<Detection> inferDetection(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();

        // Log this method so that it can be analyzed with systrace.
        Trace.beginSection("detectImage");

        Trace.beginSection("preprocessBitmap");
        // Preprocess the image data from 0-255 int to normalized float based
        // on the provided parameters.
        int[] intValues = new int[width * height];
        byte[] byteValues = new byte[width * height * 3];

        image.getPixels(intValues, 0, width, 0, 0, width, height);

        for (int i = 0; i < intValues.length; ++i) {
            byteValues[i * 3 + 2] = (byte) (intValues[i] & 0xFF);
            byteValues[i * 3 + 1] = (byte) ((intValues[i] >> 8) & 0xFF);
            byteValues[i * 3 + 0] = (byte) ((intValues[i] >> 16) & 0xFF);
        }
        Trace.endSection(); // preprocessBitmap

        // Copy the input data into TensorFlow.
        Trace.beginSection("feed");
        inferenceInterface.feed(inputName, byteValues, 1, height, width, 3);
        Trace.endSection();

        // Run the inference call.
        Trace.beginSection("run");
        inferenceInterface.run(outputNames, logStats);
        Trace.endSection();

        // Copy the output Tensor back into the output array.
        Trace.beginSection("fetch");
        outputLocations = new float[MAX_RESULTS * 4];
        outputScores = new float[MAX_RESULTS];
        outputClasses = new float[MAX_RESULTS];
        outputNumDetections = new float[1];
        inferenceInterface.fetch(outputNames[0], outputLocations);
        inferenceInterface.fetch(outputNames[1], outputScores);
        inferenceInterface.fetch(outputNames[2], outputClasses);
        inferenceInterface.fetch(outputNames[3], outputNumDetections);
        Trace.endSection();

        // Find the best detections.
        final PriorityQueue<Detection> queue =
                new PriorityQueue<>(
                        1,
                        (lhs, rhs) -> {
                            // Intentionally reversed to put high confidence at the head of the queue.
                            return Float.compare(rhs.getConfidence(), lhs.getConfidence());
                        });

        // Scale them back to the input size.
        for (int i = 0; i < outputScores.length; ++i) {
            final RectF detection =
                    new RectF(
                            outputLocations[4 * i + 1] * width,
                            outputLocations[4 * i] * height,
                            outputLocations[4 * i + 3] * width,
                            outputLocations[4 * i + 2] * height);
            queue.add(
                    new Detection("" + i, labels.get((int) outputClasses[i]), outputScores[i], detection));
        }

        final ArrayList<Detection> detections = new ArrayList<>();
        for (int i = 0; i < Math.min(queue.size(), MAX_RESULTS); ++i) {
            detections.add(queue.poll());
        }
        Trace.endSection();
        return detections;
    }

    public void enableStatLogging(final boolean logStats) {
        this.logStats = logStats;
    }

    public String getStatString() {
        return inferenceInterface.getStatString();
    }

    public void close() {
        inferenceInterface.close();
    }
}
