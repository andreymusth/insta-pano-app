package com.tzkt.andrey.instapano.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.preference.PreferenceManager;

import com.tzkt.andrey.instapano.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by andrey on 27/01/2018.
 */

public final class BitmapUtils {

    private static final String FILE_PROVIDER_AUTHORITY = "com.tzkt.andrey.fileprovider";
    public static Bitmap[] imgs;
    public static ArrayList<Uri> uris;

    public static void splitBitmap(Bitmap realBitmap,
                                       Bitmap scaledBitmap,
                                       PointF leftTopEdge,
                                       PointF leftBottomEdge,
                                       PointF rightTopEdge,
                                       float leftEdge,
                                       float topEdge,
                                       int partsQuantity) {

        imgs = new Bitmap[partsQuantity];

        float ratio = (float) realBitmap.getWidth() / scaledBitmap.getWidth();

        int width = (int) ((rightTopEdge.x - leftTopEdge.x) / partsQuantity * ratio);
        int height = (int) ((leftBottomEdge.y - leftTopEdge.y) * ratio);
        int y = (int) ((leftTopEdge.y - topEdge) * ratio);

        if (height > realBitmap.getHeight()) {
            height = realBitmap.getHeight();
        }

        for (int i = 0; i < imgs.length ; i++) {

            int currentX = (int) ((leftTopEdge.x - leftEdge) * ratio) + width * i;
            int currentWidth = width;

//            if (currentX + currentWidth > rightTopEdge.x * ratio) {
//                currentWidth = currentWidth - currentX - width;
//            }

            imgs[i] = Bitmap.createBitmap(realBitmap,
                    currentX,
                    y,
                    currentWidth,
                    height);
        }
    }

    public static Bitmap getScaledImage(Bitmap realImage, float maxImageSize, boolean stretchHeight) {

        float ratio = stretchHeight ? maxImageSize / realImage.getHeight() : maxImageSize / realImage.getWidth();

        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        return Bitmap.createScaledBitmap(realImage, width, height, false);
    }

    public static ArrayList<Uri> saveBitmaps(Context c) {

        // getting output format from shared preferences

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String outputFormat = getFormat(c);

        uris = new ArrayList<>();

        for (int i = 0; i < imgs.length ; i++) {
            uris.add(saveImage(c, imgs[i], timeStamp, i, outputFormat));
        }

        return uris;

    }

    public static File createTempImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalCacheDir();

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    private static void galleryAddPic(Context context, String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }


    /**
     * Helper method for saving the image.
     *
     * @param context The application context.
     * @param image   The image to be saved.
     */
    private static Uri saveImage(Context context, Bitmap image, String timeStamp, int imageNumber, String outputFormat) {

        String savedImagePath = null;

        // Create the new file in the external storage

        String imageFileName = "IP_" + timeStamp + "_" + imageNumber + "." + outputFormat;
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Insta pano");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }

        Bitmap.CompressFormat format = outputFormat.equals("jpg") ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG;

        // Save the new Bitmap
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(format, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Add the image to the system gallery
            galleryAddPic(context, savedImagePath);
        }

        return Uri.parse(savedImagePath);
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

//    /**
//     * Helper method for sharing an images.
//     *
//     * @param context The image context.
//     */
//    public static ArrayList<Uri> shareImages(Context context) {
//
//        ArrayList<Uri> uris = new ArrayList<>();
//
//        for (Bitmap img: imgs) {
//            uris.add(getImageUri(context, img));
//        }
//
//        return uris;
//
//    }

//    private static Uri getImageUri(Context c, Bitmap image) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//
//        String outputFormat = getFormat(c);
//        Bitmap.CompressFormat format = outputFormat.equals("jpg") ? Bitmap.CompressFormat.JPEG : Bitmap.CompressFormat.PNG;
//
//        image.compress(format, 100, bytes);
//
////        String path = MediaStore.Images.Media.insertImage(c.getContentResolver(), image, "Title", null);
//        String path = saveImage(c, ima)
//        return Uri.parse(path);
//    }

    private static String getFormat(Context c) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        return sharedPreferences.getString(c.getString(R.string.pref_format_key), c.getString(R.string.format_jpg_value));
    }
}
