package com.tinygame.lianliankan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.view.animation.*;
import android.view.animation.Animation.AnimationListener;
import android.widget.TextView;
import com.tinygame.lianliankan.config.Config;
import com.tinygame.lianliankan.config.Env;
import com.tinygame.lianliankan.db.DatabaseOperator;
import com.tinygame.lianliankan.db.DatabaseOperator.LevelInfo;
import com.tinygame.lianliankan.engine.Chart;
import com.tinygame.lianliankan.engine.FillContent;
import com.tinygame.lianliankan.engine.Hint;
import com.tinygame.lianliankan.engine.Tile;
import com.tinygame.lianliankan.utils.PointsManager;
import com.tinygame.lianliankan.utils.SoundEffectUtils;
import com.tinygame.lianliankan.utils.ThemeManager;
import com.tinygame.lianliankan.view.ContinueClickView;
import com.tinygame.lianliankan.view.LevelView;
import com.tinygame.lianliankan.view.LevelView.LevelChangedListener;
import com.tinygame.lianliankan.view.LinkLinkSurfaceView;
import com.tinygame.lianliankan.view.LinkLinkSurfaceView.LLViewActionListener;
import com.tinygame.lianliankan.view.TimeProgressView;
import com.tinygame.lianliankan.view.TimeProgressView.TimeProgressListener;

public class LinkLink extends Activity implements LLViewActionListener
                                                      , TimeProgressListener
                                                      , AnimationListener
                                                      , LevelChangedListener {
    private static final String TAG = "LinkLink";

    private LinkLinkSurfaceView mLLView;
    private View newGameButton, arrangeButton, hintButton;
    private View mNoMoreTipsView;
    private View mNoMoreTextView;
    private View mBottomRegion;
    private TextView mArrangeCount;
    private TextView mHintCount;
    private TextView mSorceTV;
    private View mNext;
    private View mStopView;
    private TimeProgressView mTimeView;
    private ContinueClickView mContinueClickView;
    private LevelView mLevelView;
    private int mCurrentTimeProgress;
    private LayoutInflater mInflater;
    private Dialog mWinDialog;
    private Dialog mLoseDialog;
    private Dialog mResetDialog;
    private Dialog mDownloadDialog;
    private Dialog mStopDialog;
    private int mCurDiffArrangeCount;
    private int mCurDiffHintCount;
    private boolean mFinishSuccessActivityShow;
    private boolean mAppDownloadShow;

    private boolean mStopDialogShow;

    private int mCountClick;
    private LevelInfo mLevelInfo;
    private AnimationSet mDispearAnimation;
    private AnimationSet mSorceAnimation;
    private AnimationSet mHitCountAnimation;
    private Animation mshakeAnimation;

    private AnimationSet mTitleDisplayAnimation;
    private AnimationSet mTimeProgressAnimation;
    private AnimationSet mBottomAnimation;
    private AnimationSet mStopAnimation;
    private AnimationSet mNewGameAnimation;

    private Context mContext;
    private boolean mNextLevel;

    private static final int PLAY_READY_SOUND = 0;
    private static final int PLAY_BACKGROUND_SOUND = 1;
    private static final int START_PROGRESS_TIME_VIEW = 2;
    private static final int RESET_PROGRESS_TIME_VIEW = 3;
    private static final int SHOW_FINISH_ONE_TIME = 4;
    private static final int SHOW_FAILED_DIALOG = 5;
    private static final int SHOW_NO_MORE_TIPS = 6;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PLAY_READY_SOUND: {
                    boolean soundOpen = SettingManager.getInstance().getSoundOpen();
                    if (soundOpen) {
                        SoundEffectUtils.getInstance().playReadySound();
                    }
                }
                break;
                case PLAY_BACKGROUND_SOUND:
                    boolean soundOpen = SettingManager.getInstance().getSoundOpen();
                    if (soundOpen) {
                        SoundEffectUtils.getInstance().playSpeedSound();
                    }
                    break;
                case START_PROGRESS_TIME_VIEW:
                    mTimeView.setTotalTime(mCurrentTimeProgress);
                    mTimeView.startProgress();
                    if (mStopDialogShow) {
                        mTimeView.stop();
                    }
                    break;
                case RESET_PROGRESS_TIME_VIEW:
                    mTimeView.reset();
                    break;
                case SHOW_FINISH_ONE_TIME:
                    showFinishDialog();
                    break;
                case SHOW_FAILED_DIALOG:
                    showFailedDialog();
                    break;
                case SHOW_NO_MORE_TIPS:
                    showNoMoreTipsView();
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mContext = this;
        SettingManager.getInstance().init(getApplicationContext());
        SoundEffectUtils.getInstance().init(this);
        ThemeManager.getInstance().init(this);
        Env.ICON_REGION_INIT = false;

        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mSorceAnimation = new AnimationSet(true);
        Animation s = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f);
        s.setDuration(200);
        this.mSorceAnimation.addAnimation(s);

        mTitleDisplayAnimation = new AnimationSet(true);
        Animation t = new TranslateAnimation(0.0f, 0.0f, -50.0f, 10.0f);
        t.setDuration(300);
        t.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.bounce_interpolator));
        mTitleDisplayAnimation.addAnimation(t);

        mTimeProgressAnimation = new AnimationSet(true);
        Animation p = new TranslateAnimation(-50.0f, 10.0f, 0.0f, 0.0f);
        p.setDuration(300);
        p.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.bounce_interpolator));
        mTimeProgressAnimation.addAnimation(p);

        mBottomAnimation = new AnimationSet(true);
        Animation p1 = new TranslateAnimation(0.0f, 0.0f, 50.0f, -10.0f);
        p1.setDuration(300);
        p1.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.bounce_interpolator));
        mBottomAnimation.addAnimation(p1);

        mStopAnimation = new AnimationSet(true);
        Animation stopT = new TranslateAnimation(-20.0f, 10.0f, -20.0f, 10.0f);
        stopT.setDuration(300);
        stopT.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.bounce_interpolator));
        mStopAnimation.addAnimation(stopT);

        mNewGameAnimation = new AnimationSet(true);
        Animation newGame = new TranslateAnimation(20.0f, -10.0f, -20.0f, 10.0f);
        newGame.setDuration(300);
        newGame.setInterpolator(AnimationUtils.loadInterpolator(this, android.R.anim.bounce_interpolator));
        mNewGameAnimation.addAnimation(newGame);

        mHitCountAnimation = new AnimationSet(true);
        Animation a1 = new TranslateAnimation(0.0f, 0.0f, 20.0f, 0.0f);
        a1.setDuration(800);
        mHitCountAnimation.addAnimation(a1);
        a1 = new AlphaAnimation(0.2f, 1.0f);
        a1.setDuration(800);
        mHitCountAnimation.addAnimation(a1);

        mDispearAnimation = new AnimationSet(true);
        Animation a = new TranslateAnimation(0.0f, 0.0f, 50.0f, 0.0f);
        a.setDuration(2000);
        mDispearAnimation.addAnimation(a);
        a = new AlphaAnimation(1.0f, 0.6f);
        a.setDuration(2000);
        mDispearAnimation.addAnimation(a);
        mDispearAnimation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mContinueClickView != null) {
                    mContinueClickView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

        });

        mshakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);

        resetContent();
    }

    @Override
    public void onStart() {
        super.onStart();
        LOGD("[[onStart]] >>>>>>>>> ");
        SettingManager.getInstance().init(getApplicationContext());
        mHandler.sendEmptyMessageDelayed(PLAY_BACKGROUND_SOUND, 500);

        mAppDownloadShow = false;
        checkAppPoint();

        if (!mNextLevel) {
            reloadCurrentLevel();
        }
        mNextLevel = false;

        if (mStopDialogShow && mStopDialog != null) {
            mStopDialog.dismiss();
            mStopDialog = null;
            mStopDialogShow = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        LOGD("[[onResume]] >>>>>>>>> ");
        mFinishSuccessActivityShow = false;

        mLevelView.startAnimation(mTitleDisplayAnimation);
        mTimeView.startAnimation(mTimeProgressAnimation);
        mBottomRegion.startAnimation(mBottomAnimation);
        mStopView.startAnimation(mStopAnimation);
        newGameButton.startAnimation(mNewGameAnimation);
    }

    @Override
    public void onPause() {
        super.onPause();
        LOGD("[[onPause]] >>>>>>>>> ");
        mFinishSuccessActivityShow = true;
    }

    private void checkAppPoint() {
        if (!Config.DEBUG_CLOSE_APP_DOWNLOAD) {
            int level = Categary_diff_selector.getInstance().getCurrentDiffLevel();
            int point = PointsManager.getInstance().queryPoints();
            if (point < Config.POINT_100 && level >= Config.APP_DOWNLOA_SHOW_LEVEL) {
                showCountDownloadDialog();
            } else if (point < Config.POINT_200 && level >= Config.APP_DOWNLOA_SHOW_LEVEL_TWO) {
//                YoumiPointsManager.spendPoints(this, Config.POINT_100);

                showCountDownloadDialog();
            } else if (point < Config.POINT_300 && level >= Config.APP_DOWNLOAD_SHOW_LEVEL_THREE) {
                showCountDownloadDialog();
            } else {
                if (mDownloadDialog != null) {
                    mDownloadDialog.dismiss();
                    mDownloadDialog = null;
                }
            }
        }
    }

    private void updateToolsCountView() {
        if (mArrangeCount != null) {
            mArrangeCount.setText(String.valueOf(mCurDiffArrangeCount));
        }
        if (mHintCount != null) {
            mHintCount.setText(String.valueOf(mCurDiffHintCount));
        }
    }

    private void updateToolsCount() {
//        mCurDiffArrangeCount = Categary_diff_selector.getInstance().getCurrentDiffArrange();
//        mCurDiffHintCount = Categary_diff_selector.getInstance().getCurrentDiffHint();
    }

    private void resetContent() {
        setContentView(R.layout.main);
        mLLView = (LinkLinkSurfaceView) findViewById(R.id.llk);
        mLLView.setLLViewActionListener(this);

//        reloadCurrentLevel();

        newGameButton = findViewById(R.id.newGame);
        arrangeButton = findViewById(R.id.arrange);
        hintButton = findViewById(R.id.hint);
        mNext = findViewById(R.id.next);
        mTimeView = (TimeProgressView) findViewById(R.id.time);
        mTimeView.setTimeProgressListener(this);
        mLevelView = (LevelView) findViewById(R.id.level);
        mLevelView.setLevelChangedListener(this);
        mLevelView.setLevel(Categary_diff_selector.getInstance().getCurrentDiffLevel());
        mArrangeCount = (TextView) findViewById(R.id.arrage_count);
        mHintCount = (TextView) findViewById(R.id.hint_count);

        mNoMoreTipsView = findViewById(R.id.no_more_tips);
        mNoMoreTextView = findViewById(R.id.no_more_text);

        mContinueClickView = (ContinueClickView) findViewById(R.id.continueclick);
        mSorceTV = (TextView) findViewById(R.id.sorce);
        mSorceTV.setText(String.format(getString(R.string.sorce), 0));

        mBottomRegion = findViewById(R.id.button_region);

        mStopView = findViewById(R.id.stop);
        if (mStopView != null) {
            mStopView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showStopDialog();
                }
            });
        }

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
                if (mCurDiffArrangeCount == 0) {
                } else {
                    Chart chart = mLLView.getChart();
                    chart.reArrange();

                    Tile[] hint = new Hint(chart).findHint();
                    if (hint == null) {
                        noMoreConnectChanged();
                    }

                    mLLView.forceRefresh();
                    mLLView.clearSelectOverlay();
                    if (mCurDiffArrangeCount > 0) {
                        mCurDiffArrangeCount--;
                    }
                    updateToolsCountView();

                    mLevelInfo.count -= Config.DISMISS_SORCE;
                    if (mLevelInfo.count < 0) {
                        mLevelInfo.count = 0;
                    }
                    mSorceTV.setText(String.format(getString(R.string.sorce), mLevelInfo.count));
                    mSorceTV.startAnimation(mSorceAnimation);
                }
            }
        });
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurDiffHintCount == 0) {
                } else {
                    Tile[] hint = new Hint(mLLView.getChart()).findHint();
                    mLLView.showHint(hint);

                    if (mCurDiffHintCount > 0) {
                        mCurDiffHintCount--;
                    }
                    updateToolsCountView();

                    mLevelInfo.count -= Config.DISMISS_SORCE / 2;
                    if (mLevelInfo.count < 0) {
                        mLevelInfo.count = 0;
                    }
                    mSorceTV.setText(String.format(getString(R.string.sorce), mLevelInfo.count));
                    mSorceTV.startAnimation(mSorceAnimation);
                }
            }
        });
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryUpdateDiffAndCategory();
            }
        });
        mNext.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        LOGD("[[onStop]] >>>>>>>");

        boolean soundOpen = SettingManager.getInstance().getSoundOpen();
        if (soundOpen) {
            SoundEffectUtils.getInstance().stopSpeedSound();
        }
        Categary_diff_selector.getInstance().saveCurretInfo();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LOGD("[[onDestroy]] >>>>>>>");

        if (mStopDialog != null) {
            mStopDialog.dismiss();
            mStopDialog = null;
        }

        mHandler.sendEmptyMessage(RESET_PROGRESS_TIME_VIEW);
        Categary_diff_selector.getInstance().saveCurretInfo();
    }

    @Override
    public void onAlignChart(Chart alignChart) {
        if (alignChart != null) {
            alignChart.dumpChart();

            mLLView.setChart(alignChart, false);
            mLLView.forceRefresh();
        }
    }

    @Override
    public void onNoHintToConnect() {
        noMoreConnectChanged();
    }

    @Override
    public void onDismissTouch(int imageIndex) {
        if (mTimeView != null) {
            mTimeView.onDissmisTouch();
        }

        this.mLevelInfo.count += Config.DISMISS_SORCE;
        mSorceTV.setText(String.format(getString(R.string.sorce), mLevelInfo.count));
//        mSorceTV.startAnimation(mSorceAnimation);
        mSorceTV.startAnimation(mHitCountAnimation);

        if (Categary_diff_selector.getInstance().getCurrentCategoryLevel() == 0) {
            switch (imageIndex) {
                case Config.IMAGE_SEARCH:
                    mCurDiffHintCount++;
                    updateToolsCountView();
                    //            mHintCount.startAnimation(mSorceAnimation);
                    mHintCount.startAnimation(mHitCountAnimation);
                    break;
                case Config.IMAGE_REARRANGE:
                    mCurDiffArrangeCount++;
                    updateToolsCountView();
                    //            mArrangeCount.startAnimation(mSorceAnimation);
                    mArrangeCount.startAnimation(mHitCountAnimation);
                    break;
                case Config.IMAGE_TIME:
                    mTimeView.increaseTime(1000);
                    mTimeView.startAnimation(mshakeAnimation);
                    break;
            }
        }
    }

    private void noMoreConnectChanged() {
        mTimeView.stop();
        mHandler.removeMessages(SHOW_NO_MORE_TIPS);
        Chart chart = mLLView.getChart();
        chart.reArrange();
        Tile[] hint = new Hint(chart).findHint();
        if (hint != null) {
            mHandler.sendEmptyMessage(SHOW_NO_MORE_TIPS);
        } else {
            noMoreConnectChanged();
        }
    }

    @Override
    public void onFinishOnTime() {
        mFinishSuccessActivityShow = true;
        mHandler.sendEmptyMessage(SHOW_FINISH_ONE_TIME);
    }

    @Override
    public void onLevelChanged(int level) {
        this.checkAppPoint();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finish();
            overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            return false;
        }
        return false;
    }

    private void showCountDownloadDialog() {
        if (mDownloadDialog != null && mDownloadDialog.isShowing()) {
            mDownloadDialog.dismiss();
            mDownloadDialog = null;
        }

        mDownloadDialog = new AlertDialog.Builder(this).setTitle(getString(R.string.tips))
                              .setMessage(getString(R.string.download_tips))
                              .setPositiveButton(R.string.btn_download
                                                    , new DialogInterface.OnClickListener() {
                                  public void onClick(DialogInterface dialog, int which) {
                                      if (mDownloadDialog != null) {
                                          mDownloadDialog.dismiss();
                                          mDownloadDialog = null;
                                      }
                                      mAppDownloadShow = false;
                                      LOGD("[[showCountDownloadDialog]] >>>>>>> show offers >>>>>>");
                                  }
                              }).setNegativeButton(R.string.btn_cancel
                                                      , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                }
            }).create();
        mDownloadDialog.setCancelable(false);
        mDownloadDialog.show();
        mAppDownloadShow = true;
    }

    private void showNoMoreTipsView() {
        if (mNoMoreTipsView.getVisibility() == View.GONE) {
            mNoMoreTipsView.setVisibility(View.VISIBLE);
            mNoMoreTextView.setVisibility(View.VISIBLE);

            Animation a = new TranslateAnimation(0.0f, 0.0f, -200.0f, 0.0f);
            a.setDuration(2000);
            a.setStartOffset(100);
//            a.setRepeatMode(Animation.RESTART);
//            a.setRepeatCount(Animation.INFINITE);
            a.setInterpolator(AnimationUtils.loadInterpolator(this,
                                                                 android.R.anim.bounce_interpolator));
            a.setAnimationListener(this);
            mNoMoreTipsView.startAnimation(a);
        }
    }

    private void showStopDialog() {
        mTimeView.stop();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View showView = mInflater.inflate(R.layout.stop_view, null);
        View retry = showView.findViewById(R.id.retry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Env.ICON_REGION_INIT = false;
                reloadCurrentLevel();
                if (mStopDialog != null) {
                    mStopDialog.dismiss();
                    mStopDialog = null;
                }
                mStopDialogShow = false;
            }
        });

        View quit = showView.findViewById(R.id.quit);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStopDialog != null) {
                    mStopDialog.dismiss();
                    mStopDialog = null;
                }
                mStopDialogShow = false;
                finish();
            }
        });

        View play = showView.findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStopDialog != null) {
                    mStopDialog.dismiss();
                    mStopDialog = null;
                }
                mStopDialogShow = false;
                mTimeView.resume();
            }
        });

        builder.setView(showView);
        mStopDialog = builder.create();
        mStopDialog.setCancelable(false);
        mStopDialog.show();
        mStopDialogShow = true;
    }

    private void showFinishDialog() {
        mHandler.removeMessages(START_PROGRESS_TIME_VIEW);
        mHandler.sendEmptyMessage(RESET_PROGRESS_TIME_VIEW);

        if (mLevelInfo != null) {
            mLevelInfo.cost = mTimeView.getCurCostTime();
        }

        DatabaseOperator.getInstance().insertCategoryAndLevelIntergral(mLevelInfo);
        Intent finishIntent = new Intent();
        finishIntent.setClass(this, ResultActivity.class);
        finishIntent.putExtra(ResultActivity.RESULT_TYPE, ResultActivity.SUCCESS_CONTENT);
        finishIntent.putExtra(ResultActivity.COST_TIME, String.valueOf(mLevelInfo.cost));
        finishIntent.putExtra(ResultActivity.COUNT, String.valueOf(mLevelInfo.count));
        finishIntent.putExtra(ResultActivity.CONTINUE_COUNT, String.valueOf(mLevelInfo.continueCount));
        finishIntent.putExtra(ResultActivity.CATEGORY, mLevelInfo.category);
        finishIntent.putExtra(ResultActivity.LEVEL, mLevelInfo.level);
        this.startActivityForResult(finishIntent, 100);
    }

    private void showFailedDialog() {
//        DatabaseOperator.getInstance().insertCategoryAndLevelIntergral(mLevelInfo);
        DatabaseOperator.getInstance().insertCategoryAndLevelIntergral(mLevelInfo.category
                                                                          , mLevelInfo.level
                                                                          , mLevelInfo.count
                                                                          , mLevelInfo.continueCount
                                                                          , mLevelInfo.max
                                                                          , -1);

        Intent finishIntent = new Intent();
        finishIntent.setClass(this, ResultActivity.class);
        finishIntent.putExtra(ResultActivity.RESULT_TYPE, ResultActivity.FAILED_CONTENT);
        this.startActivityForResult(finishIntent, 100);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View showView = mInflater.inflate(R.layout.lose_view, null);
//        View next = showView.findViewById(R.id.retry);
//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Env.ICON_REGION_INIT = false;
//                reloadCurrentLevel();
//                if (mLoseDialog != null) {
//                    mLoseDialog.dismiss();
//                    mLoseDialog = null;
//                }
//            }
//        });
//        View quit = showView.findViewById(R.id.quit);
//        quit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mLoseDialog != null) {
//                    mLoseDialog.dismiss();
//                    mLoseDialog = null;
//                }
//                finish();
//            }
//        });
//        
//        builder.setView(showView);
//        mLoseDialog = builder.create();
//        mLoseDialog.setCancelable(false);
//        mLoseDialog.show();
    }

    private void reloadCurrentLevel() {
        String diff = Categary_diff_selector.getInstance().getCurrentDiff();
        String cate = Categary_diff_selector.getInstance().getCurrentCategary();
        LOGD("[[reloadCurrentLevel]] diff = " + diff + " cate = " + cate + " >>>>>>>>>");
        if (cate != null && diff != null) {
            ThemeManager.getInstance().loadImageByCategary(cate);
            Chart c = new Chart(FillContent.getRandomWithDiff(diff
                                                                 , ThemeManager.getInstance().getCurrentImageCount() - 1));
            mLevelInfo = DatabaseOperator.getInstance().getLevelInfo(
                                                                        Categary_diff_selector.getInstance().getCurrentCategoryLevel()
                                                                        , Categary_diff_selector.getInstance().getCurrentDiffLevel() - 1);
            LOGD("[[reloadCurrentLevel]] levle info = " + mLevelInfo.toString());

            mLevelInfo.count = 0;
            mLevelInfo.continueCount = 0;
            mSorceTV.setText(String.format(getString(R.string.sorce), mLevelInfo.count));
            mCountClick = 0;
            mLLView.setChart(c, true);
            mLLView.setAlignMode(Categary_diff_selector.getInstance().getCurretntDiffAlignMode());
            mCurrentTimeProgress = Categary_diff_selector.getInstance().getCurrentTime();
            int curLevel = Categary_diff_selector.getInstance().getCurrentDiffLevel();
            mLevelView.setLevel(curLevel);

            mHandler.sendEmptyMessage(RESET_PROGRESS_TIME_VIEW);
            if (!mAppDownloadShow) {
                mHandler.sendEmptyMessageDelayed(PLAY_READY_SOUND, 200);
                mHandler.removeMessages(START_PROGRESS_TIME_VIEW);
                mHandler.sendEmptyMessageDelayed(START_PROGRESS_TIME_VIEW, 1000);
            }
            mLLView.forceRefresh();

            updateToolsCount();
            updateToolsCountView();
        } else {
            showResetGameDialog();
        }
    }

    private void showResetGameDialog() {
        LOGD("[[showResetGameDialog]] >>>>>>>>>>>>>>>");
        DatabaseOperator.getInstance().insertCategoryAndLevelIntergral(mLevelInfo);
        finish();
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        View showView = mInflater.inflate(R.layout.reset_game_view, null);
//        View next = showView.findViewById(R.id.retry);
//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainSettingManager.getInstance().setLastCategory(0);
//                MainSettingManager.getInstance().setLastDiff(0);
//                Categary_diff_selector.getInstance().restDiff();
//                Categary_diff_selector.getInstance().resetCategory();
//                Env.ICON_REGION_INIT = false;
//                reloadCurrentLevel();
//                if (mResetDialog != null) {
//                    mResetDialog.dismiss();
//                    mResetDialog = null;
//                }
//            }
//        });
//        View quit = showView.findViewById(R.id.quit);
//        quit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mResetDialog != null) {
//                    mResetDialog.dismiss();
//                    mResetDialog = null;
//                }
//                finish();
//            }
//        });
//        
//        builder.setView(showView);
//        mResetDialog = builder.create();
//        mResetDialog.setCancelable(false);
//        mResetDialog.show();
    }

    private void tryUpdateDiffAndCategory() {
        LOGD("[[tryUpdateDiffAndCategory]] levle info = " + mLevelInfo.toString());

//        DatabaseOperator.getInstance().insertCategoryAndLevelIntergral(mLevelInfo);

        String diff = Categary_diff_selector.getInstance().updateDiff();
        String cate = Categary_diff_selector.getInstance().getCurrentCategary();
        LOGD("tryUpdateDiffAndCategory >>>>> diff = " + diff + " cate = " + cate + " >>>>>>>");
        if (diff != null && cate != null) {
            Env.ICON_REGION_INIT = false;
            ThemeManager.getInstance().loadImageByCategary(cate);
            Chart c = new Chart(FillContent.getRandomWithDiff(diff
                                                                 , ThemeManager.getInstance().getCurrentImageCount() - 1));
            mLevelInfo = DatabaseOperator.getInstance().getLevelInfo(
                                                                        Categary_diff_selector.getInstance().getCurrentCategoryLevel()
                                                                        , Categary_diff_selector.getInstance().getCurrentDiffLevel() - 1);
            //reset the count to 0
            mLevelInfo.count = 0;
            mLevelInfo.continueCount = 0;
            mSorceTV.setText(String.format(getString(R.string.sorce), mLevelInfo.count));
            mCountClick = 0;
            mLLView.changeBackground();
            mLLView.setChart(c, true);
            mLLView.setAlignMode(Categary_diff_selector.getInstance().getCurretntDiffAlignMode());
            mLLView.forceRefresh();
            mCurrentTimeProgress = Categary_diff_selector.getInstance().getCurrentTime();
            mLevelView.setLevel(Categary_diff_selector.getInstance().getCurrentDiffLevel());

            mHandler.sendEmptyMessage(RESET_PROGRESS_TIME_VIEW);
            if (!mAppDownloadShow) {
                mHandler.sendEmptyMessageDelayed(PLAY_READY_SOUND, 200);
                mHandler.removeMessages(START_PROGRESS_TIME_VIEW);
                mHandler.sendEmptyMessageDelayed(START_PROGRESS_TIME_VIEW, 1000);
            }

            updateToolsCount();
            updateToolsCountView();

            int category = Categary_diff_selector.getInstance().getCurrentCategoryLevel();
            int openLevel = SettingManager.getInstance().getOpenLevelByCategory(category);
            int curLevel = Categary_diff_selector.getInstance().getCurrentDiffLevel();
            if (curLevel >= openLevel) {
                SettingManager.getInstance().setOpenLevelWithCategory(curLevel, category);
            }
        } else {
            showResetGameDialog();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            switch (resultCode) {
                case ResultActivity.RETURN_QUIT:
                    finish();
                    break;
                case ResultActivity.RETURN_RETRY:
                    Env.ICON_REGION_INIT = false;
                    reloadCurrentLevel();
                    break;
                case ResultActivity.RETURN_NEXT:
                    tryUpdateDiffAndCategory();
                    mNextLevel = true;
                    break;
            }
        }
    }


    @Override
    public void onTimeCostFinish() {
        if (!mFinishSuccessActivityShow) {
            mHandler.sendEmptyMessage(SHOW_FAILED_DIALOG);
        }
    }

    @Override
    public void onContinueClick() {
        mCountClick++;

        if (mCountClick > mLevelInfo.continueCount) {
            mLevelInfo.continueCount = mCountClick;
        }

        if (mCountClick > mLevelInfo.max) {
            mLevelInfo.max = mCountClick;
        }

        if (mContinueClickView.getVisibility() == View.GONE) {
            mContinueClickView.setVisibility(View.VISIBLE);
        }
        mContinueClickView.setContinueCount(mCountClick, mLevelInfo.max);
        mContinueClickView.startAnimation(mDispearAnimation);

        this.mLevelInfo.count += (Config.CONTINUE_DISMISS_SORCE - Config.DISMISS_SORCE);
        mSorceTV.setText(String.format(getString(R.string.sorce), mLevelInfo.count));
        mSorceTV.startAnimation(mSorceAnimation);
    }

    @Override
    public void onContinueClickDismiss() {
        mCountClick = 0;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        LOGD("[[onAnimationEnd]] >>>>>>>>>");
        if (mNoMoreTipsView != null) {
            mNoMoreTipsView.setVisibility(View.GONE);
        }
        if (mNoMoreTextView != null) {
            mNoMoreTextView.setVisibility(View.GONE);
        }

        mTimeView.resume();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onAnimationStart(Animation animation) {
//        mTimeView.stop();
    }

    private void LOGD(String msg) {
        if (com.tinygame.lianliankan.config.Config.DEBUG) {
            Log.d(TAG, msg);
        }
    }
}