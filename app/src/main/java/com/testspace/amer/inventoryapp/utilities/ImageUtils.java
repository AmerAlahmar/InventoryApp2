package com.testspace.amer.inventoryapp.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {
    private static final String TAG = ImageUtils.class.getSimpleName();

    public static byte[] fromBitmapToBlob(Bitmap imgBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imgBitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        try {
            outputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "fromBitmapToBlob: Couldn't close stream, ", e);
        }
        return outputStream.toByteArray();
    }

    public static Bitmap fromBlobToBitmap(byte[] imgBlob) {
        return BitmapFactory.decodeByteArray(imgBlob, 0, imgBlob.length);
    }
}
