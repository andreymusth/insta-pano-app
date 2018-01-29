package com.tzkt.andrey.instapano.utils;

import android.graphics.Bitmap;
import android.graphics.PointF;

import java.util.ArrayList;

/**
 * Created by andrey on 27/01/2018.
 */

public final class BitmapUtils {

    public static Bitmap[] imgs;

    public static Bitmap[] splitBitmap(Bitmap realBitmap,
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

        return imgs;
    }

    public static Bitmap getScaledImage(Bitmap realImage, float maxImageSize, boolean stretchHeight) {

        float ratio = stretchHeight ? maxImageSize / realImage.getHeight() : maxImageSize / realImage.getWidth();

        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        return Bitmap.createScaledBitmap(realImage, width, height, false);
    }
}
