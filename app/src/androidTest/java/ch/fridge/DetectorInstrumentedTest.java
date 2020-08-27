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

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DetectorInstrumentedTest {
    @Test
    public void test_detectorFactory_returnsCorrectDetector() throws Exception {
        // Arrange
        Context context = InstrumentationRegistry.getTargetContext();
        List<Label> labels = new ArrayList<>();

        // Act
        Detector detector = DetectorFactory.createDetector(context, new Logger(), DetectorType.SSD, labels);

        // Assert
        Assert.assertEquals(detector.getDetectorType(), DetectorType.SSD);
    }

    @Test
    public void test_ssdDetector_recognizeImage_returnsDetections() throws Exception {
        // Arrange
        Context context = InstrumentationRegistry.getTargetContext();
        List<Label> labels = DetectorFactory.getLabels(context.getAssets());
        Detector detector = DetectorFactory.createDetector(context, new Logger(), DetectorType.SSD, labels);
        ImageLoader imageLoader = new ImageLoader(context.getAssets());
        Bitmap image = imageLoader.getNextDemoImage();

        // Act
        List<Detection> detections = detector.detectImage(image);

        // Assert
        assertTrue(detections.size() > 0);
    }

    @Test
    public void test_rfcnDetector_recognizeImage_returnsDetections() throws Exception {
        // Arrange
        Context context = InstrumentationRegistry.getTargetContext();
        List<Label> labels = DetectorFactory.getLabels(context.getAssets());
        Detector detector = DetectorFactory.createDetector(context, new Logger(), DetectorType.RFCN, labels);
        ImageLoader imageLoader = new ImageLoader(context.getAssets());
        Bitmap image = imageLoader.getNextDemoImage();

        // Act
        List<Detection> detections = detector.detectImage(image);

        // Assert
        assertTrue(detections.size() > 0);
    }
}
