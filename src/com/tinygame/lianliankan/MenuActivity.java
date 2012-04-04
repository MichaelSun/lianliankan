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
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobclick.android.MobclickAgent;
import com.tinygame.lianliankan.config.Config;
import com.tinygame.lianliankan.utils.SoundEffectUtils;
import com.tinygame.lianliankan.utils.Utils;
import com.wiyun.game.WiGame;

public class MenuActivity extends Activity {

    private ImageView mSoundImageView;
    private View mClassicModeView;
    
    private AnimationSet mAnimationset;
    
    private static final int ENTRY_GAME = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ENTRY_GAME:
                Intent mainviewIntent = new Intent();
                mainviewIntent.setClass(getApplicationContext(), LevelActivity.class);
                startActivity(mainviewIntent);
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
        SettingManager.getInstance().init(getApplicationContext());
        this.setContentView(R.layout.menu_view);
        MobclickAgent.onError(this);
        
        mAnimationset = new AnimationSet(true);
//        Animation a = new TranslateAnimation(0.0f, 0.0f, 0.0f, 20.0f);
//        a.setDuration(150);
//        a.setInterpolator(this, android.R.anim.decelerate_interpolator);
//        mAnimationset.addAnimation(a);
        
        Animation a = new AlphaAnimation(1.0f, 0.3f);
        a.setDuration(1000);
        a.setStartOffset(0);
        mAnimationset.addAnimation(a);
        
//        Animation b = new TranslateAnimation(0.0f, 0.0f, 20.0f, -400.0f);
//        Animation b = new TranslateAnimation(0.0f, 0.0f, 0.0f, -150.0f);
//        b.setDuration(1000);
//        b.setStartOffset(0);
//        b.setInterpolator(this, android.R.anim.accelerate_interpolator);
//        mAnimationset.addAnimation(b);
        mAnimationset.setAnimationListener(new AnimationListener() {
            
            @Override
            public void onAnimationEnd(Animation animation) {
                mClassicModeView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });
        
        initView();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        mClassicModeView.setVisibility(View.VISIBLE);
        
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
        mClassicModeView = findViewById(R.id.classic);
        
        View sorceBt = findViewById(R.id.sorcebt);
        sorceBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WiGame.openLeaderboard(Config.WIGAME_SORCE_KEY);
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            }
        });
        
        View wigame = findViewById(R.id.wigame);
        wigame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WiGame.startUI();
            }
        });
        
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
                mHandler.sendEmptyMessageDelayed(ENTRY_GAME, 200);
                mClassicModeView.startAnimation(mAnimationset);
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
