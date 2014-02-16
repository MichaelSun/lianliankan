package com.xstd.plugin.api;

import android.os.Bundle;
import android.text.TextUtils;
import com.plugin.internet.core.NetWorkException;
import com.plugin.internet.core.RequestBase;
import com.plugin.internet.core.RequestEntity;

public class PMRequestBase<T> extends RequestBase<T> {

    public static String BASE_API_URL = "null";

    protected static final String KEY_METHOD = "method";
    protected static final String KEY_HTTP_METHOD = "httpMethod";

    @Override
    public RequestEntity getRequestEntity() throws NetWorkException {
        RequestEntity entity = super.getRequestEntity();
        return entity;
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
        params.putString(KEY_METHOD, method);
        params.putString(KEY_HTTP_METHOD, httpMethod);

        return params;
    }

}
