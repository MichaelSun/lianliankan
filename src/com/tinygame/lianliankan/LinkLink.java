package com.tinygame.lianliankan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.tinygame.lianliankan.LLKView.LLViewActionListener;
import com.tinygame.lianliankan.config.Env;
import com.tinygame.lianliankan.engine.Chart;
import com.tinygame.lianliankan.engine.FillContent;
import com.tinygame.lianliankan.engine.Hint;
import com.tinygame.lianliankan.engine.Tile;
import com.tinygame.lianliankan.utils.ImageSplitUtils;
import com.tinygame.lianliankan.utils.SoundEffectUtils;

public class LinkLink extends Activity implements LLViewActionListener {
    private static final String TAG = "LinkLink";
    
    LLKView llk;
    private View newGameButton, arrangeButton, hintButton;
    private View mNext;
    
    private static final int PLAY_READY_SOUND = 0;
    private static final int PLAY_BACKGROUND_SOUND = 1;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case PLAY_READY_SOUND:
                SoundEffectUtils.getInstance().stopSpeedSound();
                SoundEffectUtils.getInstance().playReadySound();
                mHandler.sendEmptyMessageDelayed(PLAY_BACKGROUND_SOUND, 50);
                break;
            case PLAY_BACKGROUND_SOUND:
                SoundEffectUtils.getInstance().playSpeedSound();
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
        SoundEffectUtils.getInstance().init(this);
        ImageSplitUtils.getInstance().init(this);
        ThemeManager.getInstance().init(this);
        resetContent();
    }

    public void resetContent() {
        setContentView(R.layout.main);
        llk = (LLKView) findViewById(R.id.llk);
        llk.setLLViewActionListener(this);
        
        String diff = Categary_diff_selector.getInstance().getCurrentDiff();
        String cate = Categary_diff_selector.getInstance().getCurrentCategary();
        if (cate != null && diff != null) {
            ThemeManager.getInstance().loadImageByCategary(cate);
            Chart c = new Chart(FillContent.getRandomWithDiff(diff
                                , ThemeManager.getInstance().getCurrentImageCount() - 1));
            llk.setChart(c);
            mHandler.sendEmptyMessageDelayed(PLAY_READY_SOUND, 200);
        }

        newGameButton = findViewById(R.id.newGame);
        arrangeButton = findViewById(R.id.arrange);
        hintButton = findViewById(R.id.hint);
//        mNext = findViewById(R.id.next);
        
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetContent();
            }
        });
        arrangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chart chart = llk.getChart();
                chart.reArrange();
                llk.invalidate();
            }
        });
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tile[] hint = new Hint(llk.getChart()).findHint();
                llk.showHint(hint);
            }
        });
//        mNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tryUpdateDiffAndCategory();
//            }
//        });
    }
    
    @Override
    public void onStop() {
        super.onStop();
        LOGD("[[onStop]]");
        SoundEffectUtils.getInstance().stopSpeedSound();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        LOGD("[[onDestroy]]");
    }

    @Override
    public void onNoHintToConnect() {
        Chart chart = llk.getChart();
        chart.reArrange();
        llk.invalidate();
    }

    @Override
    public void onFinishOnTime() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("You win!!!");
        builder.setMessage("click button for new game");
        builder.setCancelable(false);
        builder.setNegativeButton(R.string.btn_new_game, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tryUpdateDiffAndCategory();
            }
        });
        builder.create().show();

    }
    
    private void tryUpdateDiffAndCategory() {
        String diff = Categary_diff_selector.getInstance().updateDiff();
        String cate = Categary_diff_selector.getInstance().getCurrentCategary();
        if (diff == null) {
            cate = Categary_diff_selector.getInstance().updateCategory();
            if (cate != null) {
                Categary_diff_selector.getInstance().restDiff();
                diff = Categary_diff_selector.getInstance().getCurrentDiff();
            }
        }
        if (diff != null && cate != null) {
            Env.ICON_REGION_INIT = false;
            ThemeManager.getInstance().loadImageByCategary(cate);
            Chart c = new Chart(FillContent.getRandomWithDiff(diff
                                , ThemeManager.getInstance().getCurrentImageCount() - 1));
            llk.setChart(c);
            llk.invalidate();
            
            mHandler.sendEmptyMessageDelayed(PLAY_READY_SOUND, 200);
        }
    }
    
    private void LOGD(String msg) {
        if (com.tinygame.lianliankan.config.Config.DEBUG) {
            Log.d(TAG, msg);
        }
    }
}