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
import com.tinygame.lianliankan.utils.ImageSplitUtils;

public class ContinueClickView extends View {
    private static final String TAG = "ContinueClickView";

    private Context mContext;
    private Drawable mBattleDrawable;
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
        Bitmap battleBt = AssetsImageLoader.loadBitmapFromAsset(mContext, "image/batter");
        if (battleBt != null) {
            mBattleDrawable = new BitmapDrawable(battleBt);
        }
        mNumberList = ImageSplitUtils.getInstance().getContinueClickBtList();
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
        if (mBattleDrawable == null) {
            return;
        }
        
        int width = getWidth();
        int height = getHeight();
        
        int imageTotalWidth = mBattleDrawable.getIntrinsicWidth();
        for (Bitmap numBt : mNumberDrawList) {
            if (numBt != null) {
                imageTotalWidth += numBt.getWidth();
            }
        }
        
        int startX = (width - imageTotalWidth) / 2;
//        int startY = (height - mBattleDrawable.getIntrinsicHeight()) / 2;
        int startY = 0;
        
        mBattleDrawable.setBounds(startX, startY, mBattleDrawable.getIntrinsicWidth() + startX
                            , startY + mBattleDrawable.getIntrinsicHeight());
        mBattleDrawable.draw(canvas);
        
        int imageWidth = mNumberList.get(0).getWidth();
        int imageHeight = mNumberList.get(0).getHeight();
        
        startY = startY + mBattleDrawable.getIntrinsicHeight() - imageHeight;
        Rect src = new Rect(0, 0, imageWidth, imageHeight);
        for (int i = 0; i < mNumberDrawList.size(); ++i) {
            Rect dst = new Rect(mBattleDrawable.getIntrinsicWidth() + startX + (i * imageWidth)
                                , startY
                                , mBattleDrawable.getIntrinsicWidth() + startX + ((i + 1) * imageWidth)
                                , startY + imageHeight);
            canvas.drawBitmap(mNumberDrawList.get(i), src, dst, mPaint);
        }
    }
}
