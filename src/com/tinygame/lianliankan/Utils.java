package com.tinygame.lianliankan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

public class Utils {

    public static Drawable getPressDrawable(Context context, Bitmap src) {
        if (src == null) {
            return null;
        }
        
        Paint p = new Paint();
        p.setColor(0xffFFD700);
        Bitmap bitmap = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(src.extractAlpha(), 0, 0, p);

        StateListDrawable sld = new StateListDrawable();
        sld.addState(new int[] { android.R.attr.state_pressed }, new BitmapDrawable(bitmap));

        return sld;
    }
}
