package com.tinygame.lianliankan;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class ResultActivity extends Activity {

    public static final String RESULT_TYPE = "result_type";
    public static final String COST_TIME = "cost_time";
    public static final String COUNT = "count";
    public static final String CONTINUE_COUNT = "continue_count";
    
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
            TextView contentTV = (TextView) findViewById(R.id.content);
            String showContent = String.format(getString(R.string.win_content), time, count, continue_count);
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
