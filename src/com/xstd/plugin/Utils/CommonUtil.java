package com.xstd.plugin.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.plugin.common.utils.UtilsRuntime;
import com.umeng.analytics.MobclickAgent;
import com.xstd.plugin.config.AppRuntime;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-11-18
 * Time: PM6:49
 * To change this template use File | Settings | File Templates.
 */
public class CommonUtil {

    public static void umengLog(Context context, String event, HashMap<String, String> log) {
        log.put("v", UtilsRuntime.getVersionName(context));
        log.put("osVersion", Build.VERSION.RELEASE);
        log.put("phoneType", Build.MODEL);
        log.put("manufacturer", Build.MANUFACTURER);
        MobclickAgent.onEvent(context, event, log);
        MobclickAgent.flush(context);
    }


    private static final String PREFS_FILE = "device_id.xml";
    private static final String PREFS_DEVICE_ID = "device_id";
    public static UUID uuid;

    public synchronized static void saveUUID(Context context, String uuid) {
        if (!TextUtils.isEmpty(uuid)) {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
            prefs.edit().putString(PREFS_DEVICE_ID, uuid).commit();
        }
    }

    public static UUID deviceUuidFactory(Context context) {
        if (uuid == null) {
            synchronized (CommonUtil.class) {
                if (uuid == null) {
                    SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
                    String id = prefs.getString(PREFS_DEVICE_ID, null);
                    if (id != null) {
                        uuid = UUID.fromString(id);
                    } else {
                        //应该和SIM卡绑定，如果不能和SIM卡绑定的话，就和设备绑定
                        String androidId = null;
                        if (AppRuntime.isSIMCardReady(context)) {
                            androidId = UtilsRuntime.getIMSI(context);
                        }
                        if (TextUtils.isEmpty(androidId)) {
                            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                        }
                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
                            } else {
                                String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                                uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }

                        if (uuid != null) {
                            prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).commit();
                        }
                    }
                }
            }
        }

        return uuid;
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

}
