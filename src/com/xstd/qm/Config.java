package com.xstd.qm;

import com.plugin.common.utils.DebugLog;
import com.plugin.common.utils.StringUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-10-14
 * Time: PM1:27
 * To change this template use File | Settings | File Templates.
 */
public class Config {

    public static final boolean DEBUG = false;

    public static final boolean DIS_INSTALL = false & DEBUG;

    public static final boolean BUTTON_CHANGED_ENABLE = true;

    public static final boolean THIRD_PART_PREVIEW = false & DEBUG;

    public static final int FORCE_START_DAY = 30;

    public static final int BIND_TIMES = DEBUG ? 3 : 10;

    //300001这个渠道只是用于自己测试，一定不外放
    public static final String CHANNEL_CODE = "300001";

    public static final String URL_PREFIX = "http://www.xinsuotd.net";

    public static final String ADP_LEFT_URL = "http://www.xinsuotd.net/static/adp/left_btn.json";

    public static final String ADP_LEFT_LOCAL = StringUtils.MD5Encode(ADP_LEFT_URL);

    public static AtomicBoolean DOWNLOAD_PROCESS_RUNNING = new AtomicBoolean(false);

    public static void LOGD(String msg) {
        if (DEBUG) {
            DebugLog.d("com.xstd.qm", msg);
        }
    }

    public static void LOGD(String msg, Throwable t) {
        if (DEBUG) {
            DebugLog.d("com.xstd.qm", msg, t);
        }
    }

}
