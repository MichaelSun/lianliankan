package com.tinygame.lianliankan;

import com.tinygame.lianliankan.utils.SoundEffectUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

public class SplashActivity extends Activity {

    private static final int START_MAIN_VIEW = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case START_MAIN_VIEW:
                Intent mainviewIntent = new Intent();
                mainviewIntent.setClass(getApplicationContext(), MenuActivity.class);
                startActivity(mainviewIntent);
                finish();
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
        
        this.setContentView(R.layout.splash);
        StartTask task = new StartTask();
        task.execute("");
    }
    
    private class StartTask extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String...params) {
            try {
                SoundEffectUtils.getInstance().init(SplashActivity.this.getApplicationContext());
                SettingManager.getInstance().init(getApplicationContext());
                Thread.sleep(1200);
            } catch (Exception e) {
            }
            
            return 0;
        }
        
        protected void onPostExecute(Integer result) {
            mHandler.sendEmptyMessage(START_MAIN_VIEW);
        }
    }
}
