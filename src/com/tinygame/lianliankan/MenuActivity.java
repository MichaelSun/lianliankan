package com.tinygame.lianliankan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobclick.android.MobclickAgent;
import com.tinygame.lianliankan.config.Config;
import com.tinygame.lianliankan.utils.SoundEffectUtils;
import com.tinygame.lianliankan.utils.Utils;

public class MenuActivity extends Activity {

    private ImageView mSoundImageView;
    private View mClassicModeView;
    
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
        SettingManager.getInstance().init(getApplicationContext());
        this.setContentView(R.layout.menu_view);
        MobclickAgent.onError(this);
        initView();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        SettingManager.getInstance().init(getApplicationContext());
        
        boolean soundOpen = SettingManager.getInstance().getSoundOpen();
        if (soundOpen) {
            SoundEffectUtils.getInstance().playMenuSound();        
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        Animation alpha = new AlphaAnimation(0.3f, 1.0f);
        alpha.setDuration(1300);
        mClassicModeView.startAnimation(alpha);
        
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
//        Drawable classDrawable = this.getResources().getDrawable(R.drawable.classic_model);
//        Drawable bg = Utils.getPressDrawable(this, ((BitmapDrawable) classDrawable).getBitmap());
        
        mClassicModeView = findViewById(R.id.classic);
//        if (bg != null) {
//            classic.setBackgroundDrawable(bg);
//        }
        
        TextView versionTV = (TextView) findViewById(R.id.version);
        if (versionTV != null) {
            String versionName = Utils.getVersionName(this);
            if (!TextUtils.isEmpty(versionName)) {
                versionTV.setText(String.format(this.getResources().getString(R.string.version)
                                    , versionName));
            }
        }
        
        mClassicModeView.setOnClickListener(new View.OnClickListener() {
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
