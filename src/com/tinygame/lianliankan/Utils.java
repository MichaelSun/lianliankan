package com.tinygame.lianliankan;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
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
