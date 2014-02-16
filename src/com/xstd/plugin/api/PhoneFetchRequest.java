package com.xstd.plugin.api;

import com.plugin.internet.core.annotations.HttpMethod;
import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

/**
 * Created by michael on 13-12-11.
 */

@HttpMethod("GET")
@RestMethodUrl("test")
public class PhoneFetchRequest extends PMRequestBase<PhoneFetchRespone> {

    @RequiredParam("method")
    private String mehtod;

    public PhoneFetchRequest(String method) {
        this.mehtod = method;
    }

}
