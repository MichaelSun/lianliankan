package com.tinygame.lianliankan.view;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.tinygame.lianliankan.R;
import com.tinygame.lianliankan.utils.ImageSplitUtils;

public class LevelView extends View {
    private static final String TAG = "LevelView";
    
    private static final int PADDING = 20;

    public interface LevelChangedListener {
        void onLevelChanged(int level);
    }
    
    private Context mContext;
    private HashMap<Integer, Bitmap> mNumberMap;
    private Drawable mLevelLogo;
    private int mCurrentLevel;
    private ArrayList<Integer> mLevelNumList;
    private Paint mPaint = new Paint();
    private Drawable mTopBgDrawable;
    private LevelChangedListener mLevelChangedListener;
    
    public LevelView(Context context) {
        super(context);
        init(context);
    }

    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public void setLevelChangedListener(LevelChangedListener l) {
        mLevelChangedListener = l;
    }
    
    public void setLevel(int level) {
        mCurrentLevel = level;
        mLevelNumList.clear();
        while (mCurrentLevel != 0) {
            mLevelNumList.add(0, mCurrentLevel % 10);
            mCurrentLevel = mCurrentLevel / 10;
        }
        
        if (mLevelChangedListener != null) {
            mLevelChangedListener.onLevelChanged(mCurrentLevel);
        }
        
        this.invalidate();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        
        mTopBgDrawable.setBounds(0, 0, width, height);
        mTopBgDrawable.draw(canvas);
        
        int logoWidth = mLevelLogo.getIntrinsicWidth();
        int numberWidth = mNumberMap.get(0).getWidth();
        int numberHeight = mNumberMap.get(0).getHeight();
        int totalWidth = logoWidth + PADDING + numberWidth * mLevelNumList.size();
        
        int startX = (width - totalWidth) / 2;
        int logStartY = (height - mLevelLogo.getIntrinsicHeight()) / 2;
        int numberStartY = (height - mNumberMap.get(0).getHeight()) / 2;
        
        mLevelLogo.setBounds(startX, logStartY
                        , startX + mLevelLogo.getIntrinsicWidth()
                        , logStartY + mLevelLogo.getIntrinsicHeight());
        mLevelLogo.draw(canvas);
        
        Rect src = new Rect(0, 0, numberWidth, numberHeight);
        Rect destTarget = null;
        for (int i = 0; i < mLevelNumList.size(); ++i) {
            destTarget = new Rect(startX + mLevelLogo.getIntrinsicWidth() + PADDING + numberWidth * i
                    , numberStartY
                    , numberWidth * (i + 1) + startX + mLevelLogo.getIntrinsicWidth() + PADDING
                    , numberStartY + numberHeight);
            canvas.drawBitmap(mNumberMap.get(mLevelNumList.get(i)), src, destTarget, mPaint);
        }
    }
    
    private void init(Context context) {
        mContext = context;
        mNumberMap = new HashMap<Integer, Bitmap>();
        ArrayList<Bitmap> number = ImageSplitUtils.getInstance().getLevelNumberBtList();
        if (number.size() > 0) {
            for (int i = 0; i < number.size(); ++i) {
                mNumberMap.put(i, number.get(i));
            }
        }
        mLevelLogo = mContext.getResources().getDrawable(R.drawable.level);
        mTopBgDrawable = mContext.getResources().getDrawable(R.drawable.top_bg);
        mLevelNumList = new ArrayList<Integer>();
    }
    
    private void LOGD(String msg) {
        if (com.tinygame.lianliankan.config.Config.DEBUG) {
            Log.d(TAG, msg);
        }
    }
}
