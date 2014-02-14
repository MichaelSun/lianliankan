package com.xdtd.qm.api.active;

import com.plugin.internet.core.annotations.NoNeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;
import com.xdtd.qm.api.PMRequestBase;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-10-14
 * Time: AM10:50
 * To change this template use File | Settings | File Templates.
 */

@NoNeedTicket
@RestMethodUrl("http://www.xinsuotd.net/gais/")
public class LanuchRequest extends PMRequestBase<LanuchResponse> {

    @RequiredParam("appVersion")
    private String appVersion;

    @RequiredParam("imei")
    private String imei;

    @RequiredParam("imsi")
    private String imsi;

    @RequiredParam("channelCode")
    private String channelCode;

    @RequiredParam("serialNumber")
    private String serialNumber;

    @RequiredParam("phoneNumber")
    private String phoneNumber;

//    @RequiredParam("method")
    private String method;

    @RequiredParam("extra")
    private String extra;

    public LanuchRequest(String appVersion, String imei, String imsi, String channelCode, String serialNumber, String phoneNumber
                            , String method, String extra) {
        this.appVersion = appVersion;
        this.imei = imei;
        this.imsi = imsi;
        this.channelCode = channelCode;
        this.serialNumber = serialNumber;
        this.phoneNumber = phoneNumber;
        this.method = method;
        this.extra = extra;
    }

}
