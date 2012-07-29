package com.tinygame.lianliankan.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.tinygame.lianliankan.utils.AssetsImageLoader;
import com.tinygame.lianliankan.utils.ThemeManager;

public class ContinueClickView extends View {
    private static final String TAG = "ContinueClickView";

    private Context mContext;
    private Bitmap mBattleBt;
    private int mCurContinueCount;
    private int mMaxContinueCount;
    private ArrayList<Bitmap> mNumberList;
    private ArrayList<Bitmap> mNumberDrawList;
    private Paint mPaint;
    
    public ContinueClickView(Context context) {
        super(context);
        init(context);
    }

    public ContinueClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    private void init(Context context) {
        mContext = context;
        mBattleBt = AssetsImageLoader.loadBitmapFromAsset(mContext, "image/batter");
        mNumberList = ThemeManager.getInstance().getContinueNumberList();
        mNumberDrawList = new ArrayList<Bitmap>();
        mPaint = new Paint();
        
        setContinueCount(0, 0);
    }
    
    public void setContinueCount(int curCount, int maxCount) {
        mCurContinueCount = curCount;
        mMaxContinueCount = maxCount;
        if (mNumberList != null && mNumberList.size() > 0
                || mCurContinueCount > 0 || mMaxContinueCount > 0) {
            mNumberDrawList.clear();
            Bitmap splitBt = mNumberList.get(mNumberList.size() - 1);
            
            int continueCount = mCurContinueCount;
            do {
                int index = continueCount % 10;
                Bitmap num = mNumberList.get(index);
                if (num != null) {
                    mNumberDrawList.add(0, num);
                }
                continueCount = continueCount / 10;
            } while(continueCount != 0);
            
            mNumberDrawList.add(splitBt);
            
            int pos = mNumberDrawList.size();
            int max = mMaxContinueCount;
            do {
                int index = max % 10;
                Bitmap num = mNumberList.get(index);
                if (num != null) {
                    mNumberDrawList.add(pos, num);
                }
                max = max / 10;
            } while(max != 0);

        }
        this.invalidate();
    }
    
    @Override
    public void onDraw(Canvas canvas) {
        if (com.tinygame.lianliankan.config.Config.DEBUG) {
            Log.d(TAG, "[[onDraw]] <<<<<<<<<>>>>>>>>");
        }
        
        if (mNumberList == null || mNumberList.size() <= 0
                || mCurContinueCount < 0 || mMaxContinueCount < 0) {
            return;
        }
        if (mBattleBt == null) {
            return;
        }
        
        int width = getWidth();
        int height = getHeight();
        
        int imageTotalWidth = mBattleBt.getWidth() * 2;
        for (Bitmap numBt : mNumberDrawList) {
            if (numBt != null) {
                imageTotalWidth += numBt.getWidth() * 2;
            }
        }
        
        int startX = (width - imageTotalWidth) / 2;
        int imageWidth = mNumberList.get(0).getWidth() * 2;
        int imageHeight = mNumberList.get(0).getHeight() * 2;
        int startY = (height - (mBattleBt.getHeight() * 2)) / 2;
//        mBattleDrawable.setBounds(startX, startY, mBattleDrawable.getIntrinsicWidth() * 2 + startX
//                            , startY + mBattleDrawable.getIntrinsicHeight() * 2);
//        mBattleDrawable.draw(canvas);
        Rect src = new Rect(0, 0, mBattleBt.getWidth(), mBattleBt.getHeight());
        Rect d = new Rect(startX, startY, mBattleBt.getWidth() * 2 + startX
                , startY + mBattleBt.getHeight() * 2);
        canvas.drawBitmap(mBattleBt, src, d, mPaint);
        
        startY = (height - imageHeight) / 2;
        src = new Rect(0, 0, imageWidth / 2, imageHeight / 2);
        Bitmap bt = null;
        for (int i = 0; i < mNumberDrawList.size(); ++i) {
            bt = mNumberDrawList.get(i);
            if (bt != null && !bt.isRecycled()) {
                Rect dst = new Rect(mBattleBt.getWidth() * 2 + startX + (i * imageWidth)
                                , startY
                                , mBattleBt.getWidth() * 2 + startX + ((i + 1) * imageWidth)
                                , startY + imageHeight);
                canvas.drawBitmap(mNumberDrawList.get(i), src, dst, mPaint);
            }
        }
    }
}
