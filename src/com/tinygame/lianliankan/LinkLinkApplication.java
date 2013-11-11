package com.tinygame.lianliankan;

import android.app.Application;
import android.util.DisplayMetrics;
import com.skymobi.pay.app.PayApplication;

public class LinkLinkApplication extends PayApplication {

    /**
     * width of screen in pixels
     */
    public static int                   SCREEN_WIDTH    = 0;
    /**
     * height of screen in pixels
     */
    public static int                   SCREEN_HEIGHT   = 0;
    /**
     * this value used to transform pix to dip unit, pix = dp * SCREEN_DENSITY
     * screen density if screen size is 320*480 in pixels than SCREEN_DENSITY is 1.0f,
     * wile if screen size is 480*800 than SCREEN_DENSITY is 1.5f
     */
    public static float                 SCREEN_DENSITY  = 0F;    
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        SCREEN_WIDTH = dm.widthPixels;
        SCREEN_HEIGHT = dm.heightPixels;
        SCREEN_DENSITY =  dm.density;
    }
    
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
