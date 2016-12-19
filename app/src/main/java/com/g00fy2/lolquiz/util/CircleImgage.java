package com.g00fy2.lolquiz.util;

/**
 * Created by thoma on 23.11.2016.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.squareup.picasso.Transformation;

// hardware accelerated transformation of bitmaps to rounded one
public class CircleImgage implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        int size = source.getWidth();

        // if image is non square first create square version
        if (size != source.getHeight()) {
            int sizeX = size;
            int sizeY = source.getHeight();
            size = Math.min(sizeY, sizeX);

            // calcuate offset for the longer side the other will be 0
            sizeX = (sizeX - size) / 2;
            sizeY = (sizeY - size) / 2;

            // create new square bitmap
            Bitmap squareSource = Bitmap.createBitmap(source, sizeX, sizeY, size, size);

            // recycle old source and set new squareSource as source for further processing
            source.recycle();
            source = squareSource;
        }

        // create new bitmap to process
        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(source,BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        source.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}