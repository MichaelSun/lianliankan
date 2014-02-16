package com.xstd.plugin.api;

import android.os.Bundle;
import android.text.TextUtils;
import com.plugin.internet.core.NetWorkException;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

/**
 * Created by michael on 13-12-28.
 */

@RestMethodUrl("http://www.xinsuotd.net/gais/")
public class MainActiveRequest extends PMRequestBase<MainActiveResponse> {

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

    @RequiredParam("method")
    private String method;

    private String uniqueNumber;

    @RequiredParam("extra")
    private String extra;

    public MainActiveRequest(String appVersion, String imei, String imsi, String channelCode, String phoneNumber, String unique
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
