package com.xstd.plugin.api;

import com.plugin.internet.core.ResponseBase;
import com.plugin.internet.core.json.JsonProperty;

/**
 * Created by michael on 13-12-28.
 */
public class MainActiveResponse extends ResponseBase {

    @JsonProperty("subAppName")
    public String subAppName;

    @JsonProperty("url")
    public String url;

    public MainActiveResponse() {
    }

    @Override
    public String toString() {
        return "ActiveResponse{" +
                   "subAppName='" + subAppName + '\'' +
                   ", url='" + url + '\'' +
                   '}';
    }
}
