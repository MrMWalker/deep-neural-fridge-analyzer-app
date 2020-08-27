package ch.fridge.activities.detecton;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jsibbold.zoomage.ZoomageView;

import ch.fridge.R;
import ch.fridge.activities.BaseActivity;
import ch.fridge.detection.DetectionVisualization;
import ch.fridge.detection.Detector;
import ch.fridge.detection.DetectorFactory;
import ch.fridge.domain.DetectedImage;
import ch.fridge.utilities.ImageLoader;
import ch.fridge.utilities.ImageUtils;
import ch.fridge.domain.DetectedLabel;
import ch.fridge.domain.Detection;
import ch.fridge.detection.DetectorType;
import ch.fridge.utilities.CameraUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private static final int ImageMaxDimension = 1024;
    private static final int CameraRequest = 1888;
    private ImageLoader imageLoader;

    private Detector detector;
    private Uri lastImageUri; //Uri to capture image
    private List<DetectedImage> detectedImages = new ArrayList<>();
    private DetectedImage lastDetectedImage;
    private DetectionVisualization visualization;

    private ZoomageView resultImage;
    private SeekBar seekBarConfidence;
    private TextView labelConfidence;
    private FloatingActionButton buttonFinish;
    private PreviewAdapter previewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        visualization = new DetectionVisualization(getApplicationContext().getResources().getDisplayMetrics());
        imageLoader = new ImageLoader(getAssets());

        setSupportActionBar(findViewById(R.id.toolbar));
        resultImage = findViewById(R.id.main_image_result);
        seekBarConfidence = findViewById(R.id.main_seekbar);
        seekBarConfidence.setVisibility(View.INVISIBLE);
        labelConfidence = findViewById(R.id.main_label_confidence);
        buttonFinish = findViewById(R.id.main_button_finish);

        displayInstructionImage();
        setMinimumConfidence(0.5f);
        disableDeathOnFileUriExposure();

        FloatingActionButton buttonImage = findViewById(R.id.main_button_take_image);
        buttonImage.setOnClickListener(view -> {
            lastImageUri = CameraUtils.getOutputMediaFileUri(getApplicationContext()); //get lastImageUri from CameraUtils
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, lastImageUri); //Send lastImageUri with intent
            startActivityForResult(cameraIntent, CameraRequest);
        });

        buttonFinish.setOnClickListener(view -> {
            List<DetectedLabel> detectedLabels = visualization.getDetectedLabels(detectedImages);
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.putExtra("DETECTED_LABELS", new ArrayList<>(detectedLabels));
            startActivity(intent);
        });

        seekBarConfidence.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
                if (value == 0) {
                    // Minimum = 1
                    setMinimumConfidence(0.01f);
                }
                setConfidencteText();
                if (lastDetectedImage != null) {
                    lastDetectedImage.setMinimumConfidence(getMinimumConfidence());
                    displayImage(lastDetectedImage);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        RecyclerView listPreview = findViewById(R.id.main_list_preview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        listPreview.setLayoutManager(layoutManager);
        previewAdapter = new PreviewAdapter(detectedImages);
        previewAdapter.setClickListener((view, position) -> {
            DetectedImage detectedImage = detectedImages.get(position);
            displayImage(detectedImage);
            setMinimumConfidence(detectedImage.getMinimumConfidence());
            previewAdapter.highlightItem(position);
        });
        previewAdapter.setLongClickListener((view, position) -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.main_delete_image_confirm_title)
                    .setMessage(R.string.main_delete_image_confirm)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.confirm_yes, (dialog, whichButton) -> {
                        previewAdapter.removeItem(position);
                        if (detectedImages.size() > 0) {
                            displayImage(detectedImages.stream().findFirst().get());
                        } else {
                            displayInstructionImage();
                        }
                        Toast.makeText(MainActivity.this, R.string.main_delete_image_success,
                                Toast.LENGTH_SHORT).show();

                    })
                    .setNegativeButton(R.string.confirm_no, null).show();
            return true;
        });
        listPreview.setAdapter(previewAdapter);
    }

    private float getMinimumConfidence() {
        return seekBarConfidence.getProgress() / 100f;
    }

    private void setMinimumConfidence(float minimumConfidence) {
        seekBarConfidence.setProgress((int) (minimumConfidence * 100));
        setConfidencteText();
    }

    private void setConfidencteText() {
        int value = seekBarConfidence.getProgress();
        String confidenceText = "Confidence: " + value + " %";
        labelConfidence.setText(confidenceText);
    }

    private void displayInstructionImage() {
        resultImage.setEnabled(false);
        resultImage.setImageResource(R.drawable.instruction);
        seekBarConfidence.setVisibility(View.INVISIBLE);
        buttonFinish.setVisibility(View.INVISIBLE);
        labelConfidence.setVisibility(View.INVISIBLE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CameraRequest && resultCode == Activity.RESULT_OK && lastImageUri != null) {
            String imagePath = lastImageUri.getPath();
            Bitmap image = CameraUtils.convertImagePathToBitmap(imagePath);
            image = ImageUtils.resizeImage(image, ImageMaxDimension);
            int rotation = CameraUtils.getRotation(imagePath);
            if (rotation != 0) {
                image = ImageUtils.rotateImage(image, rotation);
            }

            new DetectionTask(this).execute(image);
            // Delete image
            new File(imagePath).delete();

        } else {
            Toast.makeText(getApplicationContext(), R.string.main_take_image_error, Toast.LENGTH_LONG).show();
        }
    }

    private void displayImage(DetectedImage detectedImage) {
        lastDetectedImage = detectedImage;
        Bitmap image = visualization.drawRecognitions(detectedImage);
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = (width * image.getHeight()) / image.getWidth();
        Bitmap fullResult = Bitmap.createScaledBitmap(image, width, height, true);
        resultImage.setImageBitmap(fullResult);
        resultImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        resultImage.setEnabled(true);
    }

    private DetectorType getDetectorType() {
        boolean useSSD = preferences.getBoolean("model_switch", false);
        return useSSD ? DetectorType.SSD : DetectorType.RFCN;
    }

    private void detectDemoImage() {
        Bitmap image = imageLoader.getNextDemoImage();
        new DetectionTask(this).execute(image);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDemo:
                detectDemoImage();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class DetectionTask extends AsyncTask<Bitmap, Void, DetectedImage> {
        private ProgressDialog dialog;

        public DetectionTask(MainActivity activity) {
            dialog = new ProgressDialog(activity);
        }

        @Override
        protected void onPostExecute(DetectedImage detectedImage) {
            displayImage(detectedImage);
            seekBarConfidence.setVisibility(View.VISIBLE);
            buttonFinish.setVisibility(View.VISIBLE);
            labelConfidence.setVisibility(View.VISIBLE);

            previewAdapter.addItem(detectedImage);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        protected DetectedImage doInBackground(Bitmap... params) {
            Bitmap image = params[0];

            if (detector == null || getDetectorType() != detector.getDetectorType()) {
                // Detector has not been initialized yet or the detectorType has been changed in the settings.
                detector = DetectorFactory.createDetector(getApplicationContext(), Logger, getDetectorType(), getLabels());
            }

            List<Detection> detections = detector.detectImage(image);
            DetectedImage detectedImage = new DetectedImage(image, detections, getMinimumConfidence());
            return detectedImage;
        }

        @Override
        protected void onPreExecute() {
            String message = getString(R.string.main_loading_message);
            if (getDetectorType() == DetectorType.SSD) {
                message += getString(R.string.compatbility_mode);
            }
            dialog.setMessage(message);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }
}

