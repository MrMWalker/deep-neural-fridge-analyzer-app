package ch.fridge.utilities;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

public class ImageLoader {
    private final AssetManager assetManager;
    private int lastImage;
    private String[] demoImages = new String[]{
            "images/train4.jpg",
            "images/test1.jpg",
    };

    public ImageLoader(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    private String getNextImageUrl() {
        lastImage = lastImage % demoImages.length;
        String imageUrl = demoImages[lastImage];
        lastImage++;
        return imageUrl;
    }

    public Bitmap getNextDemoImage() {
        String imageUrl = getNextImageUrl();
        return loadBitmapFromAsset(imageUrl);
    }

    public Bitmap loadBitmapFromAsset(String filePath) {
        InputStream istr;
        Bitmap bitmap;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bitmap;
    }
}
