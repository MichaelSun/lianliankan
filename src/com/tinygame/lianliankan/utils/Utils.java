package com.tinygame.lianliankan.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

public class Utils {

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx, int padding, int width) {
        int size = width;
        roundPx = size * roundPx / 60;

        Bitmap output = Bitmap.createBitmap(size + padding * 2, size + padding * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final RectF rectF = new RectF(padding, padding, size, size);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xfffffce7);
//        paint.setColor(0xff000000);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap
                    , new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight())
                    , new Rect(padding, padding, size, size)
                    , paint);
        return output;
    }
    
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

    public static String getVersionName(Context context) {
        try {
            String pckageName = context.getPackageName();
            PackageInfo pinfo = context.getPackageManager().getPackageInfo(pckageName,
                    PackageManager.GET_CONFIGURATIONS);

            return pinfo.versionName;
        } catch (NameNotFoundException e) {
        }
        
        return null;
    }
}
