package com.xstd.plugin.api;

import com.plugin.internet.core.annotations.RequiredParam;
import com.plugin.internet.core.annotations.RestMethodUrl;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-11-19
 * Time: PM3:41
 * To change this template use File | Settings | File Templates.
 */

@RestMethodUrl("test")
public class DomainRequest extends PMRequestBase<DomainResponse> {

    @RequiredParam("method")
    private String mehtod;

    public DomainRequest(String mehtod) {
        this.mehtod = mehtod;
    }
}
