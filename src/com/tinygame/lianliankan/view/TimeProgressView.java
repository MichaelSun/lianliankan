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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.tinygame.lianliankan.R;
import com.tinygame.lianliankan.ThemeManager;
import com.tinygame.lianliankan.utils.ImageSplitUtils;

public class TimeProgressView extends View {
    private static final String TAG = "TimeProgressView";
    private static final boolean DEBUG = false;
    
    public interface TimeProgressListener {
        void onTimeCostFinish();
    }
    
    private static final int PADDING_TOP = 30;
    private static final int BACKGROUND_TOP_PADDING = 17;
    private static final int BACKGROUND_HEIGHT = 840;
    private static final int CONTINUE_TOUCH_DELAY = 1000;
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
    private Drawable mProgressIcon;
    private long mPreDismissTouch;
    private int mProgressLeave;
    private long mStopProgressTime;
    private int mCurTimeProgreeIconIndex;
    private ArrayList<Bitmap> mTimeProgressList;
    private int mTimeProgressIconWidth;
    private int mTimeProgressIconHeight;
    private long mPreDrawTime;
    
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
    
    public void setTotalTime(int time) {
        mTotalTime = time;
        mProgressing = false;
        mStartTime = 0;
        mEffectTime = 0;
        
        this.invalidate();
    }
    
    public void startProgress() {
        mProgressing = true;
        mStartTime = System.currentTimeMillis();
        mEffectTime = mStartTime;
        
        this.invalidate();
    }
    
    public void reset() {
        mStartTime = 0;
        mEffectTime = 0;
        mProgressing = false;
        mCurTimeProgreeIconIndex = 0;
        mPreDrawTime = 0;
        
        this.invalidate();
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
            } else {
                if (mStopProgressTime != 0) {
                    mEffectTime += (curTime - mStopProgressTime);
                    mStopProgressTime = 0;
                }
                mPreDismissTouch = curTime;
            }
        }
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int topStart = PADDING_TOP;
        long curTime = System.currentTimeMillis();
        
        if (mPreDismissTouch != 0) {
            long cost = curTime - mPreDismissTouch;
            if (cost > CONTINUE_TOUCH_DELAY) {
                if (mStopProgressTime != 0) {
                    mEffectTime += (curTime - mStopProgressTime);
                    mStopProgressTime = 0;
                }
                mPreDismissTouch = 0;
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
        
        if (mStopProgressTime == 0) {
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
//            int top = btClip.height() - mProgressBt.getHeight();
            Rect btDest = new Rect(btX, topStart, btX + progressBtWidth, height);
            canvas.drawBitmap(mProgressBt, btSrc, btDest, mPaint);
            
            canvas.restore();
//            mProgressIcon.setBounds(0, progressLeft + curTopPadding + topStart - 15
//                                , progressBgWidth, progressLeft + curTopPadding + topStart + 15);
//            mProgressIcon.draw(canvas);
            if (mCurTimeProgreeIconIndex < mTimeProgressList.size()) {
                Bitmap drawIcon = mTimeProgressList.get(mCurTimeProgreeIconIndex);
                Rect iconSrc = new Rect(0, 0, mTimeProgressIconWidth, mTimeProgressIconHeight);
                Rect iconDest = new Rect(0, progressLeft + curTopPadding + topStart - 15
                                    , progressBgWidth, progressLeft + curTopPadding + topStart + 15);
                canvas.drawBitmap(drawIcon, iconSrc, iconDest, mPaint);
                
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
            
//            mProgressIcon.setBounds(0, topStart + curTopPadding - 15
//                            , progressBgWidth, topStart + curTopPadding + 15);
//            mProgressIcon.draw(canvas);
            mCurTimeProgreeIconIndex = 0;
            if (mCurTimeProgreeIconIndex < mTimeProgressList.size()) {
                Bitmap drawIcon = mTimeProgressList.get(mCurTimeProgreeIconIndex);
                Rect iconSrc = new Rect(0, 0, mTimeProgressIconWidth, mTimeProgressIconHeight);
                Rect iconDest = new Rect(0, topStart + curTopPadding - 15
                                        , progressBgWidth, topStart + curTopPadding + 15);
                canvas.drawBitmap(drawIcon, iconSrc, iconDest, mPaint);
            }
        }
        
        if (mProgressing) {
            int time = (int) ((curTime - mEffectTime) / 1000);
            int left = mTotalTime >= time ? (mTotalTime - time) : 0;
            ArrayList<Bitmap> numbers = getTimeNumberBt(left);
            if (numbers.size() > 0) {
                int numberWidth = numbers.get(0).getWidth();
                int numberHeight = numbers.get(0).getHeight();
                int totalNumberWidth = numberWidth * numbers.size();
                float startX = ((float) (width - totalNumberWidth)) / 2;
                int startY = (PADDING_TOP - numberHeight) / 2;
                for (int i = 0; i < numbers.size(); ++i) {
                    canvas.drawBitmap(numbers.get(i)
                                        , startX + (i * numberWidth)
                                        , startY
                                        , mPaint);
                }
            }
        } 
        
        if (mProgressing) {
            this.invalidate();
        }
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
        mProgressIcon = mContext.getResources().getDrawable(R.drawable.progress_icon);
        mTimeProgressList = ImageSplitUtils.getInstance().getTimeProgressBtList();
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
