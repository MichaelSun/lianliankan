package com.tinygame.lianliankan;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.tinygame.lianliankan.config.Config;
import com.tinygame.lianliankan.db.DatabaseOperator;
import com.tinygame.lianliankan.db.DatabaseOperator.LevelInfo;
import com.wiyun.game.WiGame;

public class ResultActivity extends Activity {

    public static final String RESULT_TYPE = "result_type";
    public static final String COST_TIME = "cost_time";
    public static final String COUNT = "count";
    public static final String CONTINUE_COUNT = "continue_count";
    public static final String CATEGORY = "category";
    public static final String LEVEL = "level";
    
    public static final int SUCCESS_CONTENT = 0;
    public static final int FAILED_CONTENT = 1;
    
    public static final int RETURN_QUIT = 100;
    public static final int RETURN_RETRY = 101;
    public static final int RETURN_NEXT = 102;
    
    private int mResultType;
    
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        
        mResultType = getIntent().getIntExtra(RESULT_TYPE, SUCCESS_CONTENT);
        if (mResultType == SUCCESS_CONTENT) {
            setContentView(R.layout.win_view);
            
            String time = getIntent().getStringExtra(COST_TIME);
            String count = getIntent().getStringExtra(COUNT);
            String continue_count = getIntent().getStringExtra(CONTINUE_COUNT);
            int category = getIntent().getIntExtra(CATEGORY, 0);
            int level = getIntent().getIntExtra(LEVEL, 0);
            
            ArrayList<LevelInfo> infos = DatabaseOperator.getInstance()
                                            .getLevelInfoForCategory(category, level);
            int totalCount = 0;
            for (LevelInfo info : infos) {
                totalCount += info.count;
            }
            
            TextView contentTV = (TextView) findViewById(R.id.content);
            String showContent = String.format(getString(R.string.win_content)
                                                , time
                                                , count
                                                , totalCount
                                                , continue_count);
            int historyScore = SettingManager.getInstance().getHighScore();
            if (totalCount > historyScore) {
                SettingManager.getInstance().setHighScore(totalCount);
            }
            
            contentTV.setText(showContent);
            
            View retry = findViewById(R.id.retry);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RETURN_RETRY);
                    finish();
                }
            });
            
            View quit = findViewById(R.id.quit);
            quit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RETURN_QUIT);
                    finish();
                }
            });
            
            View next = findViewById(R.id.next);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RETURN_NEXT);
                    finish();
                }
            });
            
            WiGame.submitScore(Config.WIGAME_SORCE_KEY, totalCount, null, true);
            
        } else if (mResultType == FAILED_CONTENT) {
            setContentView(R.layout.lose_view);
            
            View retry = findViewById(R.id.retry);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RETURN_RETRY);
                    finish();
                }
            });
            
            View quit = findViewById(R.id.quit);
            quit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RETURN_QUIT);
                    finish();
                }
            });
        }
        
        View sorceBt = findViewById(R.id.sorcebt);
        sorceBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WiGame.openLeaderboard(Config.WIGAME_SORCE_KEY);
            }
        });
    }
    
    @Override
    public void onResume() {
        super.onResume();
        View tips = findViewById(R.id.tips_icon);
        Animation alpha = new AlphaAnimation(0.3f, 1.0f);
        alpha.setDuration(1300);
        tips.startAnimation(alpha);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //eat the key code
            return true;
        }
        
        return false;
    }
}
