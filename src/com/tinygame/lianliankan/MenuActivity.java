package com.tinygame.lianliankan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.tinygame.lianliankan.utils.SoundEffectUtils;

public class MenuActivity extends Activity {

    private static final int ENTRY_GAME = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case ENTRY_GAME:
                Intent mainviewIntent = new Intent();
                mainviewIntent.setClass(getApplicationContext(), LinkLink.class);
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
        initView();
    }
    
    @Override
    public void onStart() {
        super.onStart();
        SoundEffectUtils.getInstance().playMenuSound();        
    }
    
    @Override
    public void onStop() {
        super.onStop();
        SoundEffectUtils.getInstance().stopMenuSound();
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
    }
}
