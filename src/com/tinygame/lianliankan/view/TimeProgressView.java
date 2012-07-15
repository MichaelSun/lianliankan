package com.tinygame.lianliankan.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.tinygame.lianliankan.utils.ThemeManager;

public class TimeProgressView extends View {
    private static final String TAG = "TimeProgressView";
    private static final boolean DEBUG = false;
    
    public interface TimeProgressListener {
        void onTimeCostFinish();
        
        void onContinueClick();
        
        void onContinueClickDismiss();
    }
    
    private static final int PADDING_TOP = 30;
    private static final int BACKGROUND_TOP_PADDING = 17;
    private static final int BACKGROUND_HEIGHT = 840;
    private static final int CONTINUE_TOUCH_DELAY = 1500;
    private static final int PROGRESS_ICON_CHANGED_DELAY = 500;
    
    private Bitmap mProgressBt;
    private Bitmap mProgressBg;
    private Context mContext;
    private Paint mPaint = new Paint();
    private int mTotalTime;
    private long mStartTime;
    private long mEffectTime;
    private boolean mProgressing;
    private TimeProgressListener mTimeProgressListener;
    private long mPreDismissTouch;
    private int mProgressLeave;
    private long mStopProgressTime;
    private int mCurTimeProgreeIconIndex;
    private ArrayList<Bitmap> mTimeProgressList;
    private int mTimeProgressIconWidth;
    private int mTimeProgressIconHeight;
    private long mPreDrawTime;
    
    private long mCurStopTime;
    private boolean mHasStop;
    
    public TimeProgressView(Context context) {
        super(context);
        init(context);
    }

    public TimeProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setTimeProgressListener(TimeProgressListener l) {
        mTimeProgressListener = l;
    }
    
    public void increaseTime(int time) {
        mStartTime += time;
        mEffectTime += time;
        
        this.invalidate();
    }
    
    public void setTotalTime(int time) {
        mTotalTime = time;
        mProgressing = false;
        mStartTime = 0;
        mEffectTime = 0;
        mPreDismissTouch = 0;
        mStopProgressTime = 0;
        mHasStop = false;
        
        this.invalidate();
    }
    
    public void startProgress() {
        mStartTime = System.currentTimeMillis();
        mEffectTime = mStartTime;
        mPreDismissTouch = 0;
        mStopProgressTime = 0;
        mProgressing = true;
        mHasStop = false;
        
        this.invalidate();
    }
    
    public void reset() {
        mStartTime = 0;
        mEffectTime = 0;
        mProgressing = false;
        mCurTimeProgreeIconIndex = 0;
        mPreDrawTime = 0;
        mProgressLeave = 0;
        mPreDismissTouch = 0;
        mStopProgressTime = 0;
        mHasStop = false;
        
        this.invalidate();
    }
    
    public void resume() {
        long time = System.currentTimeMillis() - mCurStopTime;
        if (mStartTime != 0) {
            mStartTime += time;
        }
        if (mEffectTime != 0) {
            mEffectTime += time;
        }
        
        mPreDismissTouch = 0;
        mStopProgressTime = 0;
        mCurStopTime = 0;
        mHasStop = false;
        
        this.invalidate();
    }
    
    public void stop() {
        mHasStop = true;
        mCurStopTime = System.currentTimeMillis();
        
        this.invalidate();
    }
    
    public int getCurCostTime() {
        if (mStartTime != 0) {
            return (int) (System.currentTimeMillis() - mStartTime) / 1000;
        }
        
        return 0;
    }

    public void onDissmisTouch() {
        if (mPreDismissTouch == 0) {
            mPreDismissTouch = System.currentTimeMillis();
        } else {
            long curTime = System.currentTimeMillis();
            long cost = curTime - mPreDismissTouch;
            if (cost <= CONTINUE_TOUCH_DELAY) {
                if (mStopProgressTime == 0) {
                    mStopProgressTime = curTime;
                }
                mPreDismissTouch = curTime;
                if (mTimeProgressListener != null) {
                    mTimeProgressListener.onContinueClick();
                }
            } else {
                if (mStopProgressTime != 0) {
                    mEffectTime += (curTime - mStopProgressTime);
                    mStopProgressTime = 0;
                }
                mPreDismissTouch = curTime;
                if (mTimeProgressListener != null) {
                    mTimeProgressListener.onContinueClickDismiss();
                }
            }
        }
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
//        if (mHasStop) {
//            return;
//        }
        
        int width = getWidth();
        int height = getHeight();
        int topStart = PADDING_TOP;
        long curTime = System.currentTimeMillis();
        
        if (!mHasStop && mPreDismissTouch != 0) {
            long cost = curTime - mPreDismissTouch;
            if (cost > CONTINUE_TOUCH_DELAY) {
                if (mStopProgressTime != 0) {
                    mEffectTime += (curTime - mStopProgressTime);
                    mStopProgressTime = 0;
                }
                mPreDismissTouch = 0;
                if (mTimeProgressListener != null) {
                    mTimeProgressListener.onContinueClickDismiss();
                }
            }
        }
        
        int progressBgWidth = mProgressBg.getWidth();
        int progressBtWidth = mProgressBt.getWidth();
        
        int bgX = (width - progressBgWidth) / 2;
        int btX = (width - progressBtWidth) / 2;
        
        int curTopPadding = (int) ((((float) height) / BACKGROUND_HEIGHT) * BACKGROUND_TOP_PADDING);
        
        Rect src = new Rect(0, 0, progressBgWidth, mProgressBg.getHeight());
        Rect dest = new Rect(bgX, topStart, bgX + progressBgWidth, height);
        canvas.drawBitmap(mProgressBg, src, dest, mPaint);

        int progressLeft = topStart;
        if (mProgressLeave == 0) {
            mProgressLeave = topStart;
        }
        
        if (!mHasStop && mStopProgressTime == 0) {
            if (mProgressing) {
                if (curTime > mEffectTime && mTotalTime > 0) {
                    if ((curTime - mEffectTime) <= mTotalTime * 1000) {
                        float usedTime = (((float) (curTime - mEffectTime)) / 1000) / mTotalTime;
                        progressLeft = (int) (usedTime * (height - curTopPadding * 2 - topStart));
                        mProgressLeave = progressLeft;
                    } else {
                        if (mTimeProgressListener != null) {
                            mTimeProgressListener.onTimeCostFinish();
                            mProgressing = false;
                        }
                    }
                }
            }
        } else {
            progressLeft = mProgressLeave;
        }
        
        if (mProgressing) {
            canvas.save();
            Rect btClip = new Rect(0, progressLeft + curTopPadding + topStart, width, height);
            canvas.clipRect(btClip);
            Rect btSrc = new Rect(0, 0, progressBtWidth, mProgressBt.getHeight());
            Rect btDest = new Rect(btX, topStart, btX + progressBtWidth, height);
            canvas.drawBitmap(mProgressBt, btSrc, btDest, mPaint);
            
            canvas.restore();
            
            if (mCurTimeProgreeIconIndex < mTimeProgressList.size()) {
                Bitmap drawIcon = mTimeProgressList.get(mCurTimeProgreeIconIndex);
                if (drawIcon != null && !drawIcon.isRecycled()) {
                    Rect iconSrc = new Rect(0, 0, mTimeProgressIconWidth, mTimeProgressIconHeight);
                    Rect iconDest = new Rect(bgX, progressLeft + curTopPadding + topStart - 15
                                    , progressBgWidth + bgX, progressLeft + curTopPadding + topStart + 15);
                    canvas.drawBitmap(drawIcon, iconSrc, iconDest, mPaint);
                }
                
                if (mPreDrawTime == 0) {
                    mPreDrawTime = System.currentTimeMillis();
                }
                
                long time = System.currentTimeMillis();
                if ((time - mPreDrawTime) >= PROGRESS_ICON_CHANGED_DELAY) {
                    mCurTimeProgreeIconIndex++;
                    if (mCurTimeProgreeIconIndex >= mTimeProgressList.size()) {
                        mCurTimeProgreeIconIndex = 0;
                    }
                    mPreDrawTime = System.currentTimeMillis();
                }
            }
        } else {
            Rect btSrc = new Rect(0, 0, progressBtWidth, mProgressBt.getHeight());
            Rect btDest = new Rect(btX, topStart, btX + progressBtWidth, height);
            canvas.drawBitmap(mProgressBt, btSrc, btDest, mPaint);
            
            mCurTimeProgreeIconIndex = 0;
            if (mCurTimeProgreeIconIndex < mTimeProgressList.size()) {
                Bitmap drawIcon = mTimeProgressList.get(mCurTimeProgreeIconIndex);
                if (drawIcon != null && !drawIcon.isRecycled()) {
                    Rect iconSrc = new Rect(0, 0, mTimeProgressIconWidth, mTimeProgressIconHeight);
                    Rect iconDest = new Rect(bgX, topStart + curTopPadding - 15
                                        , progressBgWidth + bgX, topStart + curTopPadding + 15);
                    canvas.drawBitmap(drawIcon, iconSrc, iconDest, mPaint);
                }
            }
        }
        
        if (!mHasStop && mProgressing) {
            int time = (int) ((curTime - mEffectTime) / 1000);
            int left = mTotalTime >= time ? (mTotalTime - time) : 0;
            ArrayList<Bitmap> numbers = getTimeNumberBt(left);
            if (numbers.size() > 0) {
                int numberWidth = numbers.get(0).getWidth();
                int numberHeight = numbers.get(0).getHeight();
                int totalNumberWidth = numberWidth * numbers.size();
                float startX = ((float) (width - totalNumberWidth)) / 2;
                int startY = (PADDING_TOP - numberHeight) / 2;
                Bitmap bt = null;
                for (int i = 0; i < numbers.size(); ++i) {
                    bt = numbers.get(i);
                    if (bt != null && !bt.isRecycled()) {
                        canvas.drawBitmap(numbers.get(i)
                                        , startX + (i * numberWidth)
                                        , startY
                                        , mPaint);
                    }
                }
            }
        } 
        
        if (mProgressing) {
            this.invalidate();
        }
        
        super.onDraw(canvas);
    }
    
    private ArrayList<Bitmap> getTimeNumberBt(int time) {
        ArrayList<Bitmap> ret = new ArrayList<Bitmap>();
        int length = String.valueOf(time).length();
        for (int i = 0; i < length; ++i) {
            int data = time % 10;
            time = time / 10;
            ret.add(0, ThemeManager.getInstance().getTimeNumberBtByNumber(data));
        }
        
        return ret;
    }
    
    private void init(Context context) {
        mContext = context;
        mProgressBt = loadBitmapFromAsset(mContext, "image/time_bar.png");
        mProgressBg = loadBitmapFromAsset(mContext, "image/process_bg.png");
        mTimeProgressList = ThemeManager.getInstance().getTimeProgressList();
        if (mTimeProgressList.size() > 0) {
            mTimeProgressIconWidth = mTimeProgressList.get(0).getWidth();
            mTimeProgressIconHeight = mTimeProgressList.get(0).getHeight();
        }
    }
    
    private Bitmap loadBitmapFromAsset (Context context, String resName) {
        if (resName == null) {
            throw new RuntimeException("resName MUST not be NULL");
        }
        
        if (!resName.endsWith(".png")) {
            resName = resName + ".png";
        }
            
        InputStream is = null;
        try {
            is = context.getAssets().open(resName);
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inDensity = 160;
            Rect rect = new Rect();
            Bitmap bitmap = BitmapFactory.decodeStream(is, rect, opt);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    private void LOGD(String msg) {
        if (DEBUG) {
            Log.d(TAG, msg);
        }
    }
}
