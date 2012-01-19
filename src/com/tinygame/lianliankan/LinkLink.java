package com.tinygame.lianliankan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.tinygame.lianliankan.LLKView.LLViewActionListener;
import com.tinygame.lianliankan.config.Env;
import com.tinygame.lianliankan.engine.Chart;
import com.tinygame.lianliankan.engine.FillContent;
import com.tinygame.lianliankan.engine.Hint;
import com.tinygame.lianliankan.engine.Tile;
import com.tinygame.lianliankan.utils.ImageSplitUtils;
import com.tinygame.lianliankan.utils.SoundEffectUtils;
import com.tinygame.lianliankan.view.LevelView;
import com.tinygame.lianliankan.view.TimeProgressView;
import com.tinygame.lianliankan.view.TimeProgressView.TimeProgressListener;

public class LinkLink extends Activity implements LLViewActionListener, TimeProgressListener {
    private static final String TAG = "LinkLink";
    
    private LLKView mLLView;
    private View newGameButton, arrangeButton, hintButton;
    private View mNext;
    private TimeProgressView mTimeView;
    private LevelView mLevelView;
    private int mCurrentTimeProgress;
    private LayoutInflater mInflater;
    private Dialog mWinDialog;
    private Dialog mLoseDialog;
    
    private static final int PLAY_READY_SOUND = 0;
    private static final int PLAY_BACKGROUND_SOUND = 1;
    private static final int START_PROGRESS_TIME_VIEW = 2;
    private static final int RESET_PROGRESS_TIME_VIEW = 3;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case PLAY_READY_SOUND:
                SoundEffectUtils.getInstance().playReadySound();
                break;
            case PLAY_BACKGROUND_SOUND:
                SoundEffectUtils.getInstance().playSpeedSound();
                break;
            case START_PROGRESS_TIME_VIEW:
                mTimeView.setTotalTime(mCurrentTimeProgress);
                mTimeView.startProgress();
                break;
            case RESET_PROGRESS_TIME_VIEW:
                mTimeView.reset();
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
        SoundEffectUtils.getInstance().init(this);
        ImageSplitUtils.getInstance().init(this);
        ThemeManager.getInstance().init(this);
        Env.ICON_REGION_INIT = false;
        
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        resetContent();
        mHandler.sendEmptyMessageDelayed(PLAY_BACKGROUND_SOUND, 500);
    }

    @Override
    public void onStart() {
        super.onStart();
        reloadCurrentLevel();
    }

    public void resetContent() {
        setContentView(R.layout.main);
        mLLView = (LLKView) findViewById(R.id.llk);
        mLLView.setLLViewActionListener(this);
        
//        reloadCurrentLevel();

        newGameButton = findViewById(R.id.newGame);
        arrangeButton = findViewById(R.id.arrange);
        hintButton = findViewById(R.id.hint);
        mNext = findViewById(R.id.next);
        mTimeView = (TimeProgressView) findViewById(R.id.time);
        mTimeView.setTimeProgressListener(this);
        mLevelView = (LevelView) findViewById(R.id.level);
        mLevelView.setLevel(Categary_diff_selector.getInstance().getCurrentLevel());
        
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Env.ICON_REGION_INIT = false;
                reloadCurrentLevel();
            }
        });
        arrangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chart chart = mLLView.getChart();
                chart.reArrange();
                mLLView.invalidate();
            }
        });
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tile[] hint = new Hint(mLLView.getChart()).findHint();
                mLLView.showHint(hint);
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryUpdateDiffAndCategory();
            }
        });
    }
    
    @Override
    public void onStop() {
        super.onStop();
        LOGD("[[onStop]]");
        SoundEffectUtils.getInstance().stopSpeedSound();
        Categary_diff_selector.getInstance().saveCurretInfo();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        LOGD("[[onDestroy]]");
        mHandler.sendEmptyMessage(RESET_PROGRESS_TIME_VIEW);
        Categary_diff_selector.getInstance().saveCurretInfo();
    }

    @Override
    public void onNoHintToConnect() {
        Chart chart = mLLView.getChart();
        chart.reArrange();
        mLLView.invalidate();
    }

    @Override
    public void onFinishOnTime() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View showView = mInflater.inflate(R.layout.win_view, null);
        View next = showView.findViewById(R.id.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryUpdateDiffAndCategory();
                if (mWinDialog != null) {
                    mWinDialog.dismiss();
                    mWinDialog = null;
                }
            }
        });
        View quit = showView.findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWinDialog != null) {
                    mWinDialog.dismiss();
                    mWinDialog = null;
                }
                finish();
            }
        });
        
        builder.setView(showView);
        mWinDialog = builder.create();
        mWinDialog.show();
        mHandler.removeMessages(START_PROGRESS_TIME_VIEW);
        mHandler.sendEmptyMessage(RESET_PROGRESS_TIME_VIEW);
    }
    
    private void showFailedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View showView = mInflater.inflate(R.layout.lose_view, null);
        View next = showView.findViewById(R.id.retry);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Env.ICON_REGION_INIT = false;
                reloadCurrentLevel();
                if (mLoseDialog != null) {
                    mLoseDialog.dismiss();
                    mLoseDialog = null;
                }
            }
        });
        View quit = showView.findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoseDialog != null) {
                    mLoseDialog.dismiss();
                    mLoseDialog = null;
                }
                finish();
            }
        });
        
        builder.setView(showView);
        mLoseDialog = builder.create();
        mLoseDialog.show();
    }
    
    private void reloadCurrentLevel() {
        String diff = Categary_diff_selector.getInstance().getCurrentDiff();
        String cate = Categary_diff_selector.getInstance().getCurrentCategary();
        if (cate != null && diff != null) {
            ThemeManager.getInstance().loadImageByCategary(cate);
            Chart c = new Chart(FillContent.getRandomWithDiff(diff
                                , ThemeManager.getInstance().getCurrentImageCount() - 1));
            mLLView.setChart(c);
            mCurrentTimeProgress = Categary_diff_selector.getInstance().getCurrentTime();
            mHandler.sendEmptyMessageDelayed(PLAY_READY_SOUND, 200);
            mHandler.removeMessages(START_PROGRESS_TIME_VIEW);
            mHandler.sendEmptyMessage(RESET_PROGRESS_TIME_VIEW);
            mHandler.sendEmptyMessageDelayed(START_PROGRESS_TIME_VIEW, 1000);
            mLLView.invalidate();
        }
    }
    
    private void tryUpdateDiffAndCategory() {
        LOGD("tryUpdateDiffAndCategory >>>>>>>>>>");
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
            mLLView.setChart(c);
            mLLView.invalidate();
            mCurrentTimeProgress = Categary_diff_selector.getInstance().getCurrentTime();
            mHandler.sendEmptyMessageDelayed(PLAY_READY_SOUND, 200);
            mHandler.removeMessages(START_PROGRESS_TIME_VIEW);
            mHandler.sendEmptyMessage(RESET_PROGRESS_TIME_VIEW);
            mHandler.sendEmptyMessageDelayed(START_PROGRESS_TIME_VIEW, 1000);
            
            mLevelView.setLevel(Categary_diff_selector.getInstance().getCurrentLevel());
        }
    }
    
    @Override
    public void onTimeCostFinish() {
        showFailedDialog();
    }
    
    private void LOGD(String msg) {
        if (com.tinygame.lianliankan.config.Config.DEBUG) {
            Log.d(TAG, msg);
        }
    }
}