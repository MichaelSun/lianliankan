package com.tinygame.lianliankan.view;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.tinygame.lianliankan.R;
import com.tinygame.lianliankan.ThemeManager;
import com.tinygame.lianliankan.config.Env;
import com.tinygame.lianliankan.engine.BlankRoute;
import com.tinygame.lianliankan.engine.Chart;
import com.tinygame.lianliankan.engine.ConnectiveInfo;
import com.tinygame.lianliankan.engine.Direction;
import com.tinygame.lianliankan.engine.DirectionPath;
import com.tinygame.lianliankan.engine.Hint;
import com.tinygame.lianliankan.engine.Tile;
import com.tinygame.lianliankan.utils.AssetsImageLoader;
import com.tinygame.lianliankan.utils.ImageSplitUtils;
import com.tinygame.lianliankan.utils.SoundEffectUtils;

public class LinkLinkSurfaceView extends SurfaceView implements Callback {
    private static final String TAG = "LinkLinkSurfaceView";

    public static interface LLViewActionListener {
        void onNoHintToConnect();
        void onFinishOnTime();
        void onDismissTouch();
    }
    
    private static final int PADDING_LEFT = 3;
    private static final int REFRESH_DELAY = 300;
    
    private Context mContext;
    private Paint mPaintHint;
    private Paint mPaintPic;
    private Paint mPaintSelect;
    private Paint mPaintPath;
    private Paint mPaintDismissing;
    private int mStartX;
    private int mStartY;
    private Drawable mBackgroundDrawable;
    private Bitmap mLightHBt;
    private Bitmap mLightVBt;
    private Drawable mSelectorDrawable;
    private Drawable mHintDrawable;
    
    private SurfaceHolder mHolder;
    
    private LLViewActionListener mLLViewActionListener;
    
    private Chart mChart;
    private LinkedList<BlankRoute> mRoutes = new LinkedList<BlankRoute>();
    private Tile[] mHint;
    private Tile mSelectTileCur;
    private Tile mSelectTileOne;
    private Tile mSelectTileTwo;
    
    private boolean mCurRoundClipTile;
    private boolean mTileDataChanged = true;
    private boolean mShowTouch;
    private boolean mShowHint;
    private boolean mForceRefresh;
    private int mTileDataChangedCount;
    
    private DrawTread mDrawTread;
    
    public Runnable mRefreshRunnable = new Runnable() {
        public void run() {
            try {
                mTileDataChanged = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public LinkLinkSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public LinkLinkSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LOGD("[[surfaceChanged]] <<<<<<<< >>>>>>>>");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LOGD("[[surfaceCreated]] <<<<<<<< >>>>>>>>");
        if (mDrawTread != null) {
            mDrawTread.mRunning = false;
            mDrawTread = null;
        }
        
        mDrawTread = new DrawTread();
        mDrawTread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LOGD("[[surfaceDestroyed]] <<<<<<<< >>>>>>>>");
        if (mDrawTread != null) {
            mDrawTread.mRunning = false;
            mDrawTread = null;
        }
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!Env.ICON_REGION_INIT || mChart == null) {
            return true;
        }
        int xPicIndex = (int) ((event.getX() - mStartX) / Env.ICON_WIDTH);
        int yPicIndex = (int) ((event.getY() - mStartY) / Env.ICON_WIDTH);
        if (xPicIndex >= mChart.xSize || xPicIndex < 0 || yPicIndex >= mChart.ySize || yPicIndex < 0) {
            return false;
        }
        LOGD("[[onTouch]] click = (x, " + event.getX() + " y " + event.getY()
                + " ) and index = (x, " + xPicIndex + " y " + yPicIndex + ") >>>>>>>>");
        
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            LOGD("[[onTouchEvent::Action_down]] >>>>>>>>>>");
            Tile touchTile = mChart.getTile(xPicIndex, yPicIndex);
            if (touchTile.isBlank() == false) {
                mHint = null;
                SoundEffectUtils.getInstance().playClickSound();
                handlerTileSelect(touchTile);
            }
            break;
        case MotionEvent.ACTION_UP:
//            this.postDelayed(mRefreshRunnable, REFRESH_DELAY);
            break;
        }
        return true;
    }

    public void setLLViewActionListener(LLViewActionListener l) {
        mLLViewActionListener = l;
    }
    
    public Chart getChart() {
        return mChart;
    }
    
    public void setChart(Chart chart) {
        mChart = chart;
    }
    
    public void showHint(Tile[] hint) {
        mHint = hint;
        mShowHint = true;
    }
    
    public void forceRefresh() {
        LOGD("[[forceRefresh]] <<<<>>>> <<<<>>>>>");
        mForceRefresh = true;    
    }
    
    private void handlerTileSelect(Tile newTile) {
        if (mSelectTileCur == null) {
            mSelectTileCur = newTile;
        } else {
            if (mSelectTileCur == newTile) {
                return;
            } else {
                if (mSelectTileCur.getImageIndex() == newTile.getImageIndex()) {
                    ConnectiveInfo ci = mChart.connectvie(mSelectTileCur, newTile);
                    if (ci.getResult()) {
                        SoundEffectUtils.getInstance().playDisapperSound();
                        
                        if (mLLViewActionListener != null) {
                            mLLViewActionListener.onDismissTouch();
                        }
                        
                        mSelectTileOne = mSelectTileCur;
                        mSelectTileTwo = newTile;
                        mSelectTileCur = null;
                        mRoutes.add(ci.getRoute().dismissing());
                        synchronized (LinkLinkSurfaceView.this) {
                            mTileDataChangedCount++;
                            mTileDataChanged = true;
                        }
                        return ;
                    } else {
                        mSelectTileCur = newTile;
                    }
                } else {
                    mSelectTileCur = newTile;
                }
            }
        }
        mShowTouch = true;
    }
    
    private void doRoutes() {
        LOGD("[[doRoutes]] >>>>>> <<<<<<<");
        try {
            BlankRoute blankRoute = mRoutes.removeFirst();
            blankRoute.start.dismiss();
            blankRoute.end.dismiss();
            if (blankRoute.start == mSelectTileCur || blankRoute.end == mSelectTileCur) {
                mSelectTileCur = null;
            }
            if (mChart.isAllBlank()) {
                if (mLLViewActionListener != null) {
                    mLLViewActionListener.onFinishOnTime();
                }
            } else {
                Tile[] hint = new Hint(getChart()).findHint();
                if (hint == null && mLLViewActionListener != null) {
                    mLLViewActionListener.onNoHintToConnect();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private class DrawTread extends Thread {
        private boolean mRunning;
        
        DrawTread() {
            mRunning = true;
        }
        
        public void run() {
            while (mRunning) {
                if (mTileDataChanged || mShowHint 
                        || mForceRefresh
                        || mShowTouch) {
                    if (mShowHint || mForceRefresh || mShowTouch) {
                        Canvas canvas = mHolder.lockCanvas();
                        try {
                            if (canvas != null) {
                                onDrawFullView(canvas);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (canvas != null) {
                                mHolder.unlockCanvasAndPost(canvas);
                            }
                        }
                    }
                    
                    if (mSelectTileOne != null 
                            && mSelectTileTwo != null
                            && mTileDataChanged) {
                        onDrawBoom();
                        mSelectTileOne = null;
                        mSelectTileTwo = null;
                        
                        doRoutes();
                        Canvas canvas_temp = mHolder.lockCanvas();
                        try {
                            if (canvas_temp != null) {
                                onDrawFullView(canvas_temp);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (canvas_temp != null) {
                                mHolder.unlockCanvasAndPost(canvas_temp);
                            }
                        }
                    }
                    
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                    mShowTouch = false;
                    mForceRefresh = false;
                    mShowHint = false;
//                    mTileDataChanged = false;
                    synchronized (LinkLinkSurfaceView.this) {
                        if (mTileDataChangedCount > 0) {
                            mTileDataChangedCount--;
                        }
                        if (mTileDataChangedCount == 0) {
                            mTileDataChanged = false;
                        }
                    }
                }
            }
        }
    }
    
    private void init(Context context) {
        mContext = context;
        
        mPaintHint = new Paint();
        mPaintHint.setColor(Color.RED);
        mPaintHint.setStrokeWidth(4);
        mPaintHint.setStyle(Paint.Style.STROKE);
        
        mPaintSelect = new Paint();
        mPaintSelect.setColor(Color.RED);
        mPaintSelect.setStrokeWidth(3);
        mPaintSelect.setStyle(Paint.Style.STROKE);
        
        mPaintPath = new Paint();
        mPaintPath.setColor(Color.RED);
        mPaintPath.setStrokeWidth(6);
        mPaintPath.setAlpha(150);
        
        mPaintDismissing = new Paint();
        mPaintDismissing.setAlpha(80);
        
        mBackgroundDrawable = mContext.getResources().getDrawable(R.drawable.game_bg);
        mHintDrawable = mContext.getResources().getDrawable(R.drawable.hint_icon);
        mSelectorDrawable = mContext.getResources().getDrawable(R.drawable.selector);
        
        mLightHBt = AssetsImageLoader.loadBitmapFromAsset(mContext, "image/light_h");
        mLightVBt = AssetsImageLoader.loadBitmapFromAsset(mContext, "image/light_v");
        
        getHolder().addCallback(this);
        mHolder = getHolder();
    }
    
    private void onDrawBoom() {
        ArrayList<Bitmap> ret = ImageSplitUtils.getInstance().getBoomBtList();
        LOGD(">>>>> draw boom >>>>>>>");
        for (Bitmap bt : ret) {
            Canvas canvas = mHolder.lockCanvas();
            LOGD(">>>>> draw boom for bt = " + bt + ">>>>>>>");
            try {
                onDrawFullView(canvas);
                
                Rect src = new Rect(0, 0, bt.getWidth(), bt.getHeight());
                Rect dest = new Rect(mStartX + (mSelectTileOne.x) * Env.ICON_WIDTH
                                            , mStartY + (mSelectTileOne.y) * Env.ICON_WIDTH
                                            , mStartX + (mSelectTileOne.x + 1) * Env.ICON_WIDTH
                                            , mStartY + (mSelectTileOne.y + 1) * Env.ICON_WIDTH);
                canvas.drawBitmap(bt, src, dest, mPaintPic);
                        
                Rect destTwo = new Rect(mStartX + (mSelectTileTwo.x) * Env.ICON_WIDTH
                                            , mStartY + (mSelectTileTwo.y) * Env.ICON_WIDTH
                                            , mStartX + (mSelectTileTwo.x + 1) * Env.ICON_WIDTH
                                            , mStartY + (mSelectTileTwo.y + 1) * Env.ICON_WIDTH);
                canvas.drawBitmap(bt, src, destTwo, mPaintPic);
                        
                onDrawPathLine(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
            try {
                Thread.sleep(30);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        
        LOGD(">>>>> draw boom finish  >>>>>>>");
    }
    
    private void onDrawPathLine(Canvas canvas) {
        for (BlankRoute eachRoute : mRoutes) {
            for (DirectionPath each : eachRoute.getpath()) {
                Tile eachTile = each.getTile();
                int xPoint = mStartX + (eachTile.x) * Env.ICON_WIDTH + Env.ICON_WIDTH / 2;
                int yPoint = mStartY + (eachTile.y) * Env.ICON_WIDTH + Env.ICON_WIDTH / 2;

                for (Direction eachDirection : each.getDirection()) {
//                    int startX = xPoint;
//                    int startY = yPoint;
//                    int endX = xPoint + eachDirection.padding(true, Env.ICON_WIDTH);
//                    int endY = yPoint + eachDirection.padding(false, Env.ICON_WIDTH);
//                    LOGD("[[onDrawFullView]] draw link path, startX = " + startX
//                            + " startY = " + startY + " endX = " + endX + " endY = " + endY);
//                    if (startX == endX) {
//                        int w = mLightVBt.getWidth();
//                        Rect src = new Rect(0, 0, w, mLightVBt.getHeight());
//                        Rect dest = new Rect((startX - w / 2), startY
//                                                , (startX + w / 2), endY);
//                        canvas.drawBitmap(mLightVBt, src, dest, mPaintPic);
//                        canvas.drawRect(dest, mPaintPath);
//                        LOGD("[[onDrawFullView]] draw link path, drawable V : " + dest.toString());
//                    } else if (startY == endY) {
//                        int h = mLightHBt.getHeight();
//                        Rect src = new Rect(0, 0, mLightHBt.getWidth(), h);
//                        Rect dest = new Rect(startX, startY - h / 2
//                                                , endX, startY + h / 2);
//                        canvas.drawBitmap(mLightHBt, src, dest, mPaintPic);
//                        canvas.drawRect(dest, mPaintPath);
//                        LOGD("[[onDrawFullView]] draw link path, drawable H : " + dest.toString());
//                    }
                    
                    canvas.drawLine(xPoint, yPoint, xPoint + eachDirection.padding(true, Env.ICON_WIDTH),
                            yPoint + eachDirection.padding(false, Env.ICON_WIDTH), mPaintPath);
                }
            }
        }
    }
    
    private void onDrawFullView(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        mCurRoundClipTile = false;
//        if (mBufferBt == null) {
//            mBufferBt = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        }

//        mBufferBt.eraseColor(0xff000000);
//        Canvas canvas = new Canvas(mBufferBt);
        LOGD("[[onDrawFullView]] entry into  >>>>>>> width = " + width + " height = " + height);
        canvas.drawColor(Color.BLACK);
        mBackgroundDrawable.setBounds(0, 0, width, height);
        mBackgroundDrawable.draw(canvas);
        
        if (mChart == null) {
            LOGD("[[onDrawFullView]] return because chart invalid!");
            return;
        }

        if (!Env.ICON_REGION_INIT) {
            int sideLength = Math.min(width, height) - 2 * PADDING_LEFT;
            Env.ICON_REGION_INIT = true;
            Env.ICON_WIDTH = sideLength / mChart.xSize;
            LOGD("[[onDraw]] sideLength = "+ sideLength + " icon size = " + mChart.xSize 
                    + " icon width = " + Env.ICON_WIDTH + " >>>>>>>>>>>>>>>>>");
            int imageWidth = ImageSplitUtils.getInstance().getCurrentImageWidth();
            if (imageWidth < Env.ICON_WIDTH) {
                Env.ICON_WIDTH = imageWidth;
                mStartX = (width - (mChart.xSize * imageWidth)) / 2;
                mStartY = (height - (mChart.ySize * imageWidth)) / 2;
            } else {
                mStartX = PADDING_LEFT;
                mStartY = (height - (mChart.ySize * Env.ICON_WIDTH)) / 2;
            }
        }

        for (int yIndex = 0; yIndex < mChart.ySize; yIndex++) {
            for (int xIndex = 0; xIndex < mChart.xSize; xIndex++) {
                try {
                    Tile tileTemp = mChart.getTile(xIndex, yIndex);
                    Drawable drawable = ThemeManager.getInstance().getImage(mChart.getTile(xIndex, yIndex).getImageIndex());
                    if (tileTemp != mSelectTileTwo && tileTemp != mSelectTileOne && drawable != null) {
                        drawable.setBounds(mStartX + xIndex * Env.ICON_WIDTH
                                        , mStartY + yIndex * Env.ICON_WIDTH
                                        , mStartX + (xIndex + 1) * Env.ICON_WIDTH
                                        , mStartY + (yIndex + 1) * Env.ICON_WIDTH);
                        drawable.draw(canvas);
                    } else {
                        mCurRoundClipTile = true;
                    }
                } catch (Exception e) {
                }
            }
        }

        if (mSelectTileCur != null) {
//            canvas.drawRect(mStartX + (mSelectTileCur.x) * Env.ICON_WIDTH
//                        , mStartY + (mSelectTileCur.y) * Env.ICON_WIDTH
//                        , mStartX + (mSelectTileCur.x + 1) * Env.ICON_WIDTH
//                        , mStartY + (mSelectTileCur.y + 1) * Env.ICON_WIDTH
//                        , mPaintSelect);
            mSelectorDrawable.setBounds(mStartX + (mSelectTileCur.x) * Env.ICON_WIDTH
                        , mStartY + (mSelectTileCur.y) * Env.ICON_WIDTH
                        , mStartX + (mSelectTileCur.x + 1) * Env.ICON_WIDTH
                        , mStartY + (mSelectTileCur.y + 1) * Env.ICON_WIDTH);
            mSelectorDrawable.draw(canvas);
        }

        if (null != mHint) {
            boolean blank = false;

            for (Tile tile : mHint) {
                if (tile.isBlank()) {
                    blank = true;
                }
            }
            if (blank == false) {
                for (Tile tile : mHint) {
//                    canvas.drawRect(mStartX + tile.x * Env.ICON_WIDTH
//                                , mStartY + tile.y * Env.ICON_WIDTH
//                                , mStartX + (tile.x + 1) * Env.ICON_WIDTH
//                                , mStartY + (tile.y + 1) * Env.ICON_WIDTH
//                                , mPaintHint);
                    mHintDrawable.setBounds(mStartX + tile.x * Env.ICON_WIDTH
                                , mStartY + tile.y * Env.ICON_WIDTH
                                , mStartX + (tile.x + 1) * Env.ICON_WIDTH
                                , mStartY + (tile.y + 1) * Env.ICON_WIDTH);
                    mHintDrawable.draw(canvas);
                }
            }
        }
        
//        canvasReal.drawBitmap(mBufferBt
//                        , new Rect(0, 0, mBufferBt.getWidth(), mBufferBt.getHeight())
//                        , new Rect(0, 0, width, height)
//                        , paintForPic);
        
        LOGD("[[onDrawFullView]] leva <<<<<<< width = " + width + " height = " + height);
    }

    private void LOGD(String msg) {
        if (com.tinygame.lianliankan.config.Config.DEBUG) {
            Log.d(TAG, msg);
        }
    }
}
