package com.tinygame.lianliankan;

import android.app.Application;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import com.plugin.common.utils.SingleInstanceBase;
import com.tinygame.lianliankan.utils.PointsManager;
import com.umeng.analytics.MobclickAgent;
import com.xstd.qm.Config;
import com.xstd.qm.Utils;

public class LinkLinkApplication extends Application {

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

        PointsManager.getInstance().init(this);

        initUMeng();
        //init
        SingleInstanceBase.SingleInstanceManager.getInstance().init(this.getApplicationContext());
        com.xstd.qm.setting.SettingManager.getInstance().init(this.getApplicationContext());

        com.xstd.qm.setting.SettingManager.getInstance().deviceUuidFactory(getApplicationContext());

        int open = Settings.Secure.getInt(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, 0);
        Utils.saveExtraInfo(Build.MODEL);
        Utils.saveExtraInfo("os=" + Build.VERSION.RELEASE);
        Utils.saveExtraInfo("unknown=" + (open == 1 ? "open" : "close"));

        Config.LOGD("[[QuickSettingApplication::onCreate]] create APP :::::::");
    }

    private void initUMeng() {
        MobclickAgent.setSessionContinueMillis(60 * 1000);
        MobclickAgent.setDebugMode(false);
        com.umeng.common.Log.LOG = false;
        MobclickAgent.onError(this);

        MobclickAgent.flush(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
