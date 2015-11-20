package ru.org.adons.mplace.edit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.File;

public final class ImageUtils {

    public static Bitmap decodeBitmapFromFile(String fileName, int reqWidth, int reqHeight) {
        // Get the size of the image
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, bmOptions);
        final int width = bmOptions.outWidth;
        final int height = bmOptions.outHeight;

        // Figure out which way needs to be reduced less
        int scaleFactor = 1;
        if ((reqWidth > 0) || (reqHeight > 0)) {
            scaleFactor = Math.min(width / reqWidth, height / reqHeight);
        }

        // Set bitmap options to scale the image and get Bitmap
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        return BitmapFactory.decodeFile(fileName, bmOptions);
    }

    public static void addImageToGallery(Context context, String filePath) {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(filePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

}
