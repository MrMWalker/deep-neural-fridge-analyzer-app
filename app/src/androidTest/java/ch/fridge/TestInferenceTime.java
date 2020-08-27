package ch.fridge;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import ch.fridge.detection.Detector;
import ch.fridge.detection.DetectorFactory;
import ch.fridge.detection.DetectorType;
import ch.fridge.domain.Detection;
import ch.fridge.domain.Label;
import ch.fridge.utilities.ImageLoader;
import ch.fridge.utilities.Logger;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class TestInferenceTime {
    @Test
    public void test_rfcnDetector_recognizeImage_returnsDetections() throws Exception {
        // Arrange
        Logger logger = new Logger();
        Context context = InstrumentationRegistry.getTargetContext();
        List<Label> labels = DetectorFactory.getLabels(context.getAssets());
        Detector detector = DetectorFactory.createDetector(context, new Logger(), DetectorType.RFCN, labels);
        ImageLoader imageLoader = new ImageLoader(context.getAssets());
        List<Long>inferenceTimes = new ArrayList<>();

        String testDir = "test";
        String[] testPaths = context.getAssets().list(testDir);
        for(String testPath : testPaths) {
            String fullPath = testDir + "/" + testPath;
            Bitmap image = imageLoader.loadBitmapFromAsset(fullPath);
            long startTime= System.currentTimeMillis();
            List<Detection> detections = detector.detectImage(image);
            long endTime= System.currentTimeMillis();
            inferenceTimes.add(endTime-startTime);
            logger.d("one image done: "+ (endTime-startTime));
        }

        // Assert
        double avg = inferenceTimes.stream().mapToLong(x -> x).average().getAsDouble();
        logger.d("**************************************************************");
        logger.d("***********FINAL MEAN INFERENCE TIME: " + avg + " *************");
        logger.d("**************************************************************");
    }

}
