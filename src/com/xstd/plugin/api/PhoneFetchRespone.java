package com.xstd.plugin.api;

import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

/**
 * Created by michael on 13-12-11.
 */
public class PhoneFetchRespone extends ResponseBase {

    @JsonProperty("pn")
    public String phone;

    public PhoneFetchRespone() {
    }

}
