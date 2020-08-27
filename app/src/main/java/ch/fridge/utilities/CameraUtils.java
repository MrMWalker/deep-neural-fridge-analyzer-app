package ch.fridge.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraUtils {
    public static int getRotation(String imagePath) {
        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath);
        } catch (IOException e) {
            return 0;
        }
        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        }

        return 0;
    }

    //Get Uri Of captured Image
    public static Uri getOutputMediaFileUri(Context context) {
        File mediaStorageDir = new File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Camera");
        //If File is not present create directory
        if (!mediaStorageDir.exists()) {
            if (mediaStorageDir.mkdir())
                Log.e("Create Directory", "Main Directory Created : " + mediaStorageDir);
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());//Get Current timestamp
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");//create image path with system mill and image format
        return Uri.fromFile(mediaFile);

    }

    /*  Convert Captured image path into Bitmap to display over ImageView  */
    public static Bitmap convertImagePathToBitmap(String imagePath) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);//Decode image path
        return bitmap;
    }
}
