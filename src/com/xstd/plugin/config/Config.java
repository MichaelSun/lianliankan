package com.xstd.plugin.config;

import com.plugin.common.utils.DebugLog;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-10-20
 * Time: AM8:38
 * To change this template use File | Settings | File Templates.
 */
public class Config {

    public static final boolean DEBUG = com.xstd.qm.Config.DEBUG;

    public static final boolean DELETE_RECEIVED_MESSAGE = false && DEBUG;

    public static final boolean DEBUG_IF_GO_HOME = true && DEBUG;

    public static final int SMS_SEND_DELAY = 1;

    public static final long SMS_IMSI2PHONE_DELAY = ((long) 12) * 60 * 60 * 1000;

    public static final String DEFAULT_BASE_URL = "http://www.xinsuotd.com;http://www.ydssb.com";

    public static final long DELAY_ACTIVE_DO_MONKEY = ((long) 30) * 24 * 60 * 60 * 1000;

    /**
     * 200开始表示自有渠道
     */
    public static final String CHANNEL_CODE = com.xstd.qm.Config.CHANNEL_CODE;

    public static final String ACTIVE_RESPONSE_FILE = "response.data";

    public static final void LOGD(String msg) {
        if (DEBUG) {
            DebugLog.d("com.xstd.plugin", msg);
        }
    }

    public static final void LOGD(String msg, Throwable e) {
        if (DEBUG) {
            DebugLog.d("com.xstd.plugin", msg, e);
        }
    }
}
