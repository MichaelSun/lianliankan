package com.tinygame.lianliankan;

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
import android.view.SurfaceView;

import com.tinygame.lianliankan.config.Env;
import com.tinygame.lianliankan.engine.BlankRoute;
import com.tinygame.lianliankan.engine.Chart;
import com.tinygame.lianliankan.engine.ConnectiveInfo;
import com.tinygame.lianliankan.engine.Direction;
import com.tinygame.lianliankan.engine.DirectionPath;
import com.tinygame.lianliankan.engine.Hint;
import com.tinygame.lianliankan.engine.Tile;
import com.tinygame.lianliankan.utils.ImageSplitUtils;
import com.tinygame.lianliankan.utils.SoundEffectUtils;

public class LLKView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "LLKView";
    
    public static interface LLViewActionListener {
        void onNoHintToConnect();
        void onFinishOnTime();
    }

    private int span = 5;
    private int xStart;
    private int yStart;

    private Tile selectTile;
    private Chart chart;
    private LLViewActionListener mLLViewActionListener;
    private BoomThread mBoomThread;
    private Bitmap mBufferBt;
    private Tile mSelectTileOne;
    private Tile mSelectTileTwo;

    public LLKView(Context context) {
        super(context);
        init();
    }

    public LLKView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public void setLLViewActionListener(LLViewActionListener l) {
        mLLViewActionListener = l;
    }
    
    private void init() {
        this.setBackgroundColor(0x00000000);
        this.getHolder().addCallback(this);
    }
    
    public Chart getChart() {
        return chart;
    }

    Paint paintForHint = new Paint();
    {
        paintForHint.setColor(Color.BLUE);
        paintForHint.setStrokeWidth(2);
        paintForHint.setStyle(Paint.Style.STROKE);
    }
    Paint paintForPic = new Paint();
    Paint paintForSelect = new Paint();
    {
        paintForSelect.setColor(Color.RED);
        paintForSelect.setStrokeWidth(2);
        paintForSelect.setStyle(Paint.Style.STROKE);
    }
    Paint paintForPath = new Paint();
    {
        paintForPath.setColor(Color.RED);
        paintForPath.setStrokeWidth(3);
    }
    Paint paintForDismissing = new Paint();
    {
        paintForDismissing.setAlpha(80);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

//        if (mBufferBt == null) {
//            mBufferBt = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        }

//        mBufferBt.eraseColor(0xff000000);
//        Canvas canvas = new Canvas(mBufferBt);
        LOGD("[[LLKView::onDraw]] entry into  >>>>>>> width = " + width + " height = " + height);
//        canvas.drawColor(Color.BLACK);

        if (!Env.ICON_REGION_INIT) {
            int sideLength = Math.min(width, height) - 2 * span;
            Env.ICON_REGION_INIT = true;
            Env.ICON_WIDTH = sideLength / chart.xSize;
            LOGD("[[onDraw]] sideLength = "+ sideLength + " icon size = " + chart.xSize 
                    + " icon width = " + Env.ICON_WIDTH + " >>>>>>>>>>>>>>>>>");
            int imageWidth = ImageSplitUtils.getInstance().getCurrentImageWidth();
            if (imageWidth < Env.ICON_WIDTH) {
                Env.ICON_WIDTH = imageWidth;
                xStart = (width - (chart.xSize * imageWidth)) / 2;
                yStart = (height - (chart.ySize * imageWidth)) / 2;
            } else {
                xStart = span;
                yStart = span;
            }
        }

        for (int yIndex = 0; yIndex < chart.ySize; yIndex++) {
            for (int xIndex = 0; xIndex < chart.xSize; xIndex++) {
                try {
                    Tile tileTemp = chart.getTile(xIndex, yIndex);
                    if (tileTemp != mSelectTileTwo && tileTemp != mSelectTileOne) {
                        Drawable drawable = ThemeManager.getInstance().getImage(chart.getTile(xIndex, yIndex).getImageIndex());
                        if (null != drawable) {
                            drawable.setBounds(xStart + xIndex * Env.ICON_WIDTH
                                        , yStart + yIndex * Env.ICON_WIDTH
                                        , xStart + (xIndex + 1) * Env.ICON_WIDTH
                                        , yStart + (yIndex + 1) * Env.ICON_WIDTH);
                            drawable.draw(canvas);
                        }
                    }
                } catch (Exception e) {
                }
            }
        }

        if (null != selectTile) {
            canvas.drawRect(xStart + (selectTile.x) * Env.ICON_WIDTH
                        , yStart + (selectTile.y) * Env.ICON_WIDTH
                        , xStart + (selectTile.x + 1) * Env.ICON_WIDTH
                        , yStart + (selectTile.y + 1) * Env.ICON_WIDTH
                        , paintForSelect);
        }
        
        if (mSelectTileTwo != null && mSelectTileOne != null) {
            ArrayList<Bitmap> ret = ImageSplitUtils.getInstance().getBoomBtList();
            if (ret.size() > 3) {
                Bitmap bt = ret.get(2);
                Rect src = new Rect(0, 0, bt.getWidth(), bt.getHeight());
                Rect dest = new Rect(xStart + (mSelectTileOne.x) * Env.ICON_WIDTH
                                    , yStart + (mSelectTileOne.y) * Env.ICON_WIDTH
                                    , xStart + (mSelectTileOne.x + 1) * Env.ICON_WIDTH
                                    , yStart + (mSelectTileOne.y + 1) * Env.ICON_WIDTH);
                canvas.drawBitmap(bt, src, dest, paintForPic);
                
                Rect destTwo = new Rect(xStart + (mSelectTileTwo.x) * Env.ICON_WIDTH
                                    , yStart + (mSelectTileTwo.y) * Env.ICON_WIDTH
                                    , xStart + (mSelectTileTwo.x + 1) * Env.ICON_WIDTH
                                    , yStart + (mSelectTileTwo.y + 1) * Env.ICON_WIDTH);
                canvas.drawBitmap(bt, src, destTwo, paintForPic);
            }
            
            mSelectTileTwo = null;
            mSelectTileOne = null;
        }
        
        for (BlankRoute eachRoute : routes) {
            for (DirectionPath each : eachRoute.getpath()) {
                Tile eachTile = each.getTile();
                int xPoint = xStart + (eachTile.x) * Env.ICON_WIDTH + Env.ICON_WIDTH / 2;
                int yPoint = yStart + (eachTile.y) * Env.ICON_WIDTH + Env.ICON_WIDTH / 2;

                for (Direction eachDirection : each.getDirection()) {
                    canvas.drawLine(xPoint, yPoint, xPoint + eachDirection.padding(true, Env.ICON_WIDTH),
                            yPoint + eachDirection.padding(false, Env.ICON_WIDTH), paintForPath);
                }
            }
        }

        if (null != hint) {
            boolean blank = false;

            for (Tile tile : hint) {
                if (tile.isBlank()) {
                    blank = true;
                }
            }
            if (blank == false) {
                for (Tile tile : hint) {
                    canvas.drawRect(xStart + tile.x * Env.ICON_WIDTH
                                , yStart + tile.y * Env.ICON_WIDTH
                                , xStart + (tile.x + 1) * Env.ICON_WIDTH
                                , yStart + (tile.y + 1) * Env.ICON_WIDTH
                                , paintForSelect);
                }
            }
        }
        
//        canvasReal.drawBitmap(mBufferBt
//                        , new Rect(0, 0, mBufferBt.getWidth(), mBufferBt.getHeight())
//                        , new Rect(0, 0, width, height)
//                        , paintForPic);
        
        LOGD("[[LLKView::onDraw]] leva <<<<<<< width = " + width + " height = " + height);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!Env.ICON_REGION_INIT) {
            return true;
        }
        int xPicIndex = (int) ((event.getX() - xStart) / Env.ICON_WIDTH);
        int yPicIndex = (int) ((event.getY() - yStart) / Env.ICON_WIDTH);
        if (xPicIndex >= chart.xSize || xPicIndex < 0 || yPicIndex >= chart.ySize || yPicIndex < 0) {
            return false;
        }
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            LOGD("[[onTouchEvent::Action_down]] >>>>>>>>>>");
            Tile touchTile = chart.getTile(xPicIndex, yPicIndex);
            if (touchTile.isBlank() == false) {
                SoundEffectUtils.getInstance().playClickSound();
                handlerTileSelect(touchTile);
            }
            break;
        case MotionEvent.ACTION_UP:
            break;
        }
        return true;
    }

    public void setChart(Chart chart) {
        this.chart = chart;
    }

    private LinkedList<BlankRoute> routes = new LinkedList<BlankRoute>();

    public Runnable hintRunable = new Runnable() {
        public void run() {
            try {
                hint = null;
                LLKView.this.postInvalidate();
            } catch (Exception ex) {
                Log.e("cosina1985", ex.toString());
            }
        }
    };

    public Runnable runable = new Runnable() {
        public void run() {
            try {
                BlankRoute blankRoute = routes.removeFirst();
                blankRoute.start.dismiss();
                blankRoute.end.dismiss();
                if (blankRoute.start == selectTile || blankRoute.end == selectTile) {
                    selectTile = null;
                }
                if (chart.isAllBlank()) {
                    if (mLLViewActionListener != null) {
                        mLLViewActionListener.onFinishOnTime();
                    }
                } else {
                    LLKView.this.postInvalidate();
                    Tile[] hint = new Hint(getChart()).findHint();
                    if (hint == null && mLLViewActionListener != null) {
                        mLLViewActionListener.onNoHintToConnect();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    private void handlerTileSelect(Tile newTile) {
        if (selectTile == null) {
            selectTile = newTile;
        } else {
            if (selectTile == newTile) {
                return;
            } else {
                if (selectTile.getImageIndex() == newTile.getImageIndex()) {
                    ConnectiveInfo ci = chart.connectvie(selectTile, newTile);
                    if (ci.getResult()) {
                        SoundEffectUtils.getInstance().playDisapperSound();
//                        mBoomThread.updateSelectTitle(selectTile, newTile);
                        mSelectTileOne = selectTile;
                        mSelectTileTwo = newTile;
                        selectTile = null;
                        routes.add(ci.getRoute().dismissing());
                        LLKView.this.postDelayed(runable, 150);
                    } else {
                        selectTile = newTile;
                    }
                } else {
                    selectTile = newTile;
                }
            }

        }
        invalidate();
    }

    private Tile[] hint;

    public void showHint(Tile[] hint) {
        this.hint = hint;
        LLKView.this.postDelayed(hintRunable, 1000);
        invalidate();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        LOGD("[[surfaceChanged]] width = " + width + " height = " + height + " >>>>>>>>>>");
//        if (mBufferBt != null) {
//            if (mBufferBt.getWidth() != width || mBufferBt.getHeight() != height) {
//                mBufferBt.recycle();
//                mBufferBt = null;
//            }
//        }
        
//        mBufferBt = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        LOGD("[[surfaceCreated]] >>>>>>>>>>>");
//        if (mBoomThread != null) {
//            mBoomThread.mRunning = false;
//            mBoomThread = null;
//        }
        
//        mBoomThread = new BoomThread();
//        mBoomThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        LOGD("[[surfaceCreated]] >>>>>>>>>>>");
//        if (mBoomThread != null) {
//            mBoomThread.mRunning = false;
//            mBoomThread = null;
//        }
    }
    
    class BoomThread extends Thread {
        private boolean mRunning;
        private Tile mSelectTileOne;
        private Tile mSelectTileTwo;
        
        BoomThread() {
            mRunning = true;
        }
        
        public void updateSelectTitle(Tile one, Tile two) {
            LOGD("[[updateSelectTitle]] >>>>>>>>");
            mSelectTileOne = one;
            mSelectTileTwo = two;
        }
        
        public void run() {
            while (mRunning) {
                if (mSelectTileOne != null && mSelectTileTwo != null) {
                    ArrayList<Bitmap> ret = ImageSplitUtils.getInstance().getBoomBtList();
                    SurfaceHolder myholder = LLKView.this.getHolder();
                    Paint paint = new Paint();
                    LOGD(">>>>> draw boom >>>>>>>");
                    for (Bitmap bt : ret) {
                        LOGD(">>>>> draw boom for bt = " + bt + ">>>>>>>");
                        Canvas canvas = myholder.lockCanvas();
                        try {
                            synchronized (myholder) {
                                    Rect src = new Rect(0, 0, bt.getWidth(), bt.getHeight());
                                    Rect dest = new Rect(xStart + (mSelectTileOne.x) * Env.ICON_WIDTH
                                                        , yStart + (mSelectTileOne.y) * Env.ICON_WIDTH
                                                        , xStart + (mSelectTileOne.x + 1) * Env.ICON_WIDTH
                                                        , yStart + (mSelectTileOne.y + 1) * Env.ICON_WIDTH);
                                    canvas.drawBitmap(bt, src, dest, paint);
                                    
                                    Rect destTwo = new Rect(xStart + (mSelectTileTwo.x) * Env.ICON_WIDTH
                                                        , yStart + (mSelectTileTwo.y) * Env.ICON_WIDTH
                                                        , xStart + (mSelectTileTwo.x + 1) * Env.ICON_WIDTH
                                                        , yStart + (mSelectTileTwo.y + 1) * Env.ICON_WIDTH);
                                    canvas.drawBitmap(bt, src, destTwo, paint);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (canvas != null) {
                                myholder.unlockCanvasAndPost(canvas);
                            }
                        }
                        
                        try {
                            Thread.sleep(50);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    LOGD(">>>>> draw boom finish  >>>>>>>");
                    
                    Canvas canvas = myholder.lockCanvas();
                    try {
                        if (canvas != null) {
                            
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (canvas != null) {
                            myholder.unlockCanvasAndPost(canvas);
                        }
                    }
                    
                }
                mSelectTileOne = null;
                mSelectTileTwo = null;
                
                try {
                    Thread.sleep(500);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void LOGD(String msg) {
        if (com.tinygame.lianliankan.config.Config.DEBUG) {
            Log.d(TAG, msg);
        }
    }
}
