package com.xstd.qm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.plugin.common.utils.SingleInstanceBase;
import com.xstd.qm.*;
import com.xstd.qm.setting.MainSettingManager;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-10-17
 * Time: AM9:50
 * To change this template use File | Settings | File Templates.
 */
public class PackageAddBrc extends BroadcastReceiver {

    public void onReceive(final Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            MainSettingManager.getInstance().init(context);
            if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                String packageName = intent.getDataString().substring(8);
                if (Config.DEBUG) {
                    Config.LOGD("<<PackageAddBrc::onReceive>> package name for ADD is : " + packageName);
                }

                if (SingleInstanceBase.getInstance(PLuginManager.class).scanPluginInstalled()) {
                    AppRuntime.PLUGIN_INSTALLED = true;
                    MainSettingManager.getInstance().setKeyPluginInstalled(true);
                    if (UtilOperator.fake != null) {
                        UtilOperator.fake.setCountDown(10);
                    }

                    if (!MainSettingManager.getInstance().getNotifyPluginInstallSuccess()) {
                        MainSettingManager.getInstance().setNotifyPluginInstallSuccess(false);
                        Utils.saveExtraInfo("子程序已安装");
                        Utils.notifyServiceInfo(context);
                    }

                    MainSettingManager.getInstance().setHasInstallPlugin(true);

                    //子程序已经安装，那么我们就将绑定的次数设置成5次以上
                    MainSettingManager.getInstance().setPluginAppTime(Config.BIND_TIMES + 1);
                } else {
                    MainSettingManager.getInstance().setKeyPluginInstalled(false);
                    AppRuntime.PLUGIN_INSTALLED = false;
                }
            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                if (!SingleInstanceBase.getInstance(PLuginManager.class).scanPluginInstalled()) {
                    MainSettingManager.getInstance().setKeyPluginInstalled(false);
                    AppRuntime.PLUGIN_INSTALLED = false;
                }
            }
        }
    }

}
