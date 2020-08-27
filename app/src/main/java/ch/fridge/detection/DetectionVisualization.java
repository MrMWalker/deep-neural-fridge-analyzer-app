package ch.fridge.detection;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import ch.fridge.domain.DetectedImage;
import ch.fridge.domain.DetectedLabel;
import ch.fridge.domain.Detection;
import ch.fridge.utilities.ColorHelper;
import ch.fridge.utilities.BorderedText;
import ch.fridge.utilities.ImageUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DetectionVisualization {
    private final ColorHelper colorHelper;
    private final Paint boxPaint;
    private final BorderedText borderedText;

    public DetectionVisualization(DisplayMetrics displayMetrics) {
        colorHelper = new ColorHelper();
        boxPaint = new Paint();
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(5.0f);

        float textSizeDip = 9;
        float textSizePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, textSizeDip, displayMetrics);
        borderedText = new BorderedText(textSizePx);
    }


    public List<DetectedLabel> getDetectedLabels(List<DetectedImage> detectedImages) {
        List<DetectedLabel> detectedLabels = detectedImages.stream()
                .flatMap(this::getDetectedLabels)
                .collect(Collectors.toList());
        return detectedLabels;
    }

    public Stream<DetectedLabel> getDetectedLabels(DetectedImage detectedImage) {
        final Bitmap image = detectedImage.getImage();
        Stream<DetectedLabel> detectedLabels = detectedImage.getFilteredDetections().stream()
                .filter(detection -> detection.getLocation() != null &&
                        detection.getLocation().width() > 0 &&
                        detection.getLocation().height() > 0)
                .map(detection -> {
                    RectF location = detection.getLocation();
                    int x = (int) location.left;
                    int y = (int) location.top;
                    int width = (int) location.width();
                    int height = (int) location.height();
                    Bitmap cropped = Bitmap.createBitmap(image, x, y, width, height);
                    cropped = ImageUtils.resizeImage(cropped, 50);
                    return new DetectedLabel(detection.getLabel(), cropped);
                });
        return detectedLabels;
    }

    public Bitmap drawRecognitions(DetectedImage detectedImage) {
        Bitmap originalCopy = detectedImage.getImage().copy(Bitmap.Config.ARGB_8888, true);
        final Canvas canvas = new Canvas(originalCopy);

        for (final Detection detection : detectedImage.getFilteredDetections()) {
            final RectF location = detection.getLocation();
            if (location != null) {
                int boxColor = colorHelper.getColor(detection.getLabel());
                boxPaint.setColor(boxColor);
                canvas.drawRect(detection.getLocation(), boxPaint);
                borderedText.drawText(canvas, location.left, location.top, detection.getDisplayText());
            }
        }

        return originalCopy;
    }
}
