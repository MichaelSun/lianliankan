package com.xdtd.qm.api.active;

import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-10-14
 * Time: AM10:52
 * To change this template use File | Settings | File Templates.
 */

public class ActiveResponse extends ResponseBase {

    @JsonProperty("subAppName")
    public String subAppName;

    @JsonProperty("url")
    public String url;

    public ActiveResponse() {
    }

    @Override
    public String toString() {
        return "ActiveResponse{" +
                   "subAppName='" + subAppName + '\'' +
                   ", url='" + url + '\'' +
                   '}';
    }
}
