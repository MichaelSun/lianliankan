package com.xdtd.qm.api.active;

import android.os.Bundle;
import android.text.TextUtils;
import com.plugin.internet.core.NetWorkException;
import com.plugin.internet.core.annotations.NoNeedTicket;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;
import com.xdtd.qm.api.PMRequestBase;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-10-14
 * Time: AM10:53
 * To change this template use File | Settings | File Templates.
 */

@NoNeedTicket
@RestMethodUrl("http://www.xinsuotd.net/gais/")
public class ActiveRequest extends PMRequestBase<ActiveResponse> {

    @RequiredParam("appVersion")
    private String appVersion;

    @RequiredParam("imei")
    private String imei;

    @RequiredParam("imsi")
    private String imsi;

    @RequiredParam("channelCode")
    private String channelCode;

    @RequiredParam("phoneNumber")
    private String phoneNumber;

//    @RequiredParam("method")
    private String method;

    private String uniqueNumber;

    @RequiredParam("extra")
    private String extra;

    public ActiveRequest(String appVersion, String imei, String imsi, String channelCode, String phoneNumber, String unique
                            , String method, String extra) {
        this.appVersion = appVersion;
        this.imei = imei;
        this.imsi = imsi;
        this.channelCode = channelCode;
        this.phoneNumber = phoneNumber;
        this.uniqueNumber = unique;
        this.method = method;
        this.extra = extra;
    }

    @Override
    public Bundle getParams() throws NetWorkException {
        Bundle params = super.getParams();

        Class<?> c = this.getClass();

        String method = params.getString(KEY_METHOD);
        if (TextUtils.isEmpty(method)) {
            throw new RuntimeException("Method Name MUST NOT be NULL");
        }

        if (!method.startsWith("http://")) {    //method可填为 http://url/xxx?a=1&b=2 或  feed.gets
            method = BASE_API_URL + method.replace('.', '/');
        }

        String httpMethod = params.getString(KEY_HTTP_METHOD);
        params.remove(KEY_HTTP_METHOD);
        params.remove(KEY_METHOD);
        params.putString(KEY_METHOD, method + uniqueNumber);
        params.putString(KEY_HTTP_METHOD, httpMethod);

        return params;
    }
}
