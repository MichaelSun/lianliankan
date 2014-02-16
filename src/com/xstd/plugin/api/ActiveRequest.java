package com.xstd.plugin.api;

import android.content.Context;
import android.os.Build;
import com.plugin.common.utils.UtilsRuntime;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;
import com.xstd.plugin.config.PluginSettingManager;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-10-14
 * Time: AM10:53
 * To change this template use File | Settings | File Templates.
 */

@RestMethodUrl("sais/")
public class ActiveRequest extends PMRequestBase<ActiveResponse> {

    @RequiredParam("appVersion")
    private String appVersion;

    @RequiredParam("imei")
    private String imei;

    @RequiredParam("imsi")
    private String imsi;

    //渠道号
    @RequiredParam("channelCode")
    private String channelCode;

    //唯一号
    @RequiredParam("serialNumber")
    private String serialNumber;

    @RequiredParam("name")
    private String name;

    @RequiredParam("phoneType")
    private String phoneType;

    @RequiredParam("androidVersion")
    private String androidVersion;

    @RequiredParam("netType")
    private String netType;

    @RequiredParam("smsCenter")
    private String smsCenter;

    @RequiredParam("osVersion")
    private String osVersion;

    @RequiredParam("phoneNumber")
    private String phoneNumber;

    @RequiredParam("error")
    private String error;

    @RequiredParam("monthCount")
    private String monthCount;

    @RequiredParam("dayCount")
    private String dayCount;

    @RequiredParam("lastTime")
    private String lastTime;

    @RequiredParam("method")
    private String method;

    @RequiredParam("mainChannel")
    private String mainChannel;

    public ActiveRequest(Context context, String channelCode, String unique, String appName
                , int netType, String phoneNumber, String error, String method, String smsCenter, String mainChannel) {
        appVersion = UtilsRuntime.getVersionName(context);
        imei = UtilsRuntime.getIMEI(context);
        imsi = UtilsRuntime.getIMSI(context);
        this.channelCode = channelCode;
        this.serialNumber = unique;
        this.name = appName;
        this.phoneType = Build.MODEL;
        this.androidVersion = Build.VERSION.RELEASE;
        this.netType = String.valueOf(netType);
        this.smsCenter = smsCenter;
        this.osVersion = Build.VERSION.RELEASE;
        this.phoneNumber = phoneNumber;
        this.error = error;
        monthCount = String.valueOf(PluginSettingManager.getInstance().getKeyMonthCount());
        dayCount = String.valueOf(PluginSettingManager.getInstance().getKeyDayCount());
        lastTime = String.valueOf(PluginSettingManager.getInstance().getKeyLastCountTime());
        this.method = method;
        this.mainChannel = mainChannel;
    }

}
