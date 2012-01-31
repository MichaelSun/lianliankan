package com.tinygame.lianliankan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.mobclick.android.MobclickAgent;
import com.mobclick.android.ReportPolicy;
import com.tinygame.lianliankan.config.Config;
import com.tinygame.lianliankan.utils.SoundEffectUtils;

public class MenuActivity extends Activity {

    private ImageView mSoundImageView;
    
    private static final int ENTRY_GAME = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ENTRY_GAME:
                Intent mainviewIntent = new Intent();
//                mainviewIntent.setClass(getApplicationContext(), LinkLink.class);
                mainviewIntent.setClass(getApplicationContext(), LevelActivity.class);
                startActivity(mainviewIntent);
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
        
        this.setContentView(R.layout.menu_view);
        MobclickAgent.onError(this);
        initView();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        boolean soundOpen = SettingManager.getInstance().getSoundOpen();
        if (soundOpen) {
            SoundEffectUtils.getInstance().playMenuSound();        
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        MobclickAgent.onEvent(this, Config.ACTION_START_LAUNCH, "MenuActiviy_resume");
    }
    
    @Override
    public void onPause() {
        super.onPause();
        
        MobclickAgent.onEvent(this, Config.ACTION_START_LAUNCH, "MenuActiviy_pause");
        MobclickAgent.onPause(this);
    }
    
    @Override
    public void onStop() {
        super.onStop();
        boolean soundOpen = SettingManager.getInstance().getSoundOpen();
        if (soundOpen) {
            SoundEffectUtils.getInstance().stopMenuSound();
        }
    }
    
    private void initView() {
        View classic = findViewById(R.id.classic);
        classic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoundEffectUtils.getInstance().playClickSound();
                mHandler.sendEmptyMessageDelayed(ENTRY_GAME, 100);
            }
        });
        
        mSoundImageView = (ImageView) findViewById(R.id.setting);
        boolean open = SettingManager.getInstance().getSoundOpen();
        if (open) {
            mSoundImageView.setImageDrawable(this.getResources().getDrawable(R.drawable.sound_white));
        } else {
            mSoundImageView.setImageDrawable(this.getResources().getDrawable(R.drawable.close));
        }
        mSoundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean curOpen = SettingManager.getInstance().getSoundOpen();
                if (curOpen) {
                    mSoundImageView.setImageDrawable(getResources().getDrawable(R.drawable.close));
                    SettingManager.getInstance().setSoundOpen(false);
                    
                    SoundEffectUtils.getInstance().stopMenuSound();
                } else {
                    mSoundImageView.setImageDrawable(getResources().getDrawable(R.drawable.sound_white));
                    SettingManager.getInstance().setSoundOpen(true);
                    
                    SoundEffectUtils.getInstance().playMenuSound();
                }
            }
        });
    }
}
