package com.tinygame.lianliankan.pay;

import android.app.Activity;
import android.app.ProgressDialog;
import com.skymobi.free.FreePayment;
import com.skymobi.free.OnFetchPayChannelsListener;
import com.skymobi.free.PayChannel;
import com.skymobi.sdkproxy.SdkProxy;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-11-11
 * Time: AM10:14
 * To change this template use File | Settings | File Templates.
 */
public class Pay implements OnFetchPayChannelsListener {

    private static Pay PAY = null;

    private ProgressDialog pDialog;

    private PayChannel[] channelData;

    public static Pay getInstance() {
        if (PAY == null) {
            PAY = new Pay();
        }

        return PAY;
    }

    private Pay() {
    }

    public PayChannel[] getChannelData() {
        return channelData;
    }

    public void init(Activity a) {
        //初始化SDK
        SdkProxy.init(a);
        SdkProxy.onCreate(a);
        //检查更新
        SdkProxy.checkUpdate(a);
        pDialog = ProgressDialog.show(a, null, "正在获取支付通道...");
        FreePayment.fetchPayChannels(this);

        pDialog.show();
    }

    @Override
    public void onFetchPayChannelSuccess(PayChannel[] channels) {
        pDialog.dismiss();

        channelData = channels;
    }

    @Override
    public void onFetchPayChannelFailure(int code, String msg) {
        pDialog.dismiss();
    }
}
