package com.khafizov.ferrum.utilities;
import android.graphics.Bitmap;
import android.util.Base64;
import java.io.ByteArrayOutputStream;

public class ImageEncoder {
    public String encodeImage(Bitmap bitmap, int requiredWidth, int requiredHeight) {
        try {if (bitmap.getWidth() <= requiredWidth && bitmap.getHeight() <= requiredHeight) {
                return encodeBase64(bitmap);}
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, requiredWidth, requiredHeight, false);
            return encodeBase64(resizedBitmap);} catch (Exception e) {
            e.printStackTrace();return null;}}
    private String encodeBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return android.util.Base64.encodeToString(imageBytes, Base64.DEFAULT);}}
