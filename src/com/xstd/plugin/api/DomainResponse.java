package com.xstd.plugin.api;

import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-11-19
 * Time: PM3:33
 * To change this template use File | Settings | File Templates.
 */
public class DomainResponse extends ResponseBase {

    @JsonProperty("spDomainList")
    public String[] domainList;

    public DomainResponse() {
    }

    @Override
    public String toString() {
        return "DomainResponse{" +
                   "domainList=" + Arrays.toString(domainList) +
                   '}';
    }
}
