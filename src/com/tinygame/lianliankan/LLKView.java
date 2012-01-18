package com.tinygame.lianliankan;

import java.util.LinkedList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

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

public class LLKView extends View {
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

        LOGD("[[onDraw]] width = " + width + " height = " + height);

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
                    Drawable drawable = ThemeManager.getInstance().getImage(chart.get(xIndex, yIndex).getImageIndex());
                    if (null != drawable) {
                        drawable.setBounds(xStart + xIndex * Env.ICON_WIDTH
                                    , yStart + yIndex * Env.ICON_WIDTH
                                    , xStart + (xIndex + 1) * Env.ICON_WIDTH
                                    , yStart + (yIndex + 1) * Env.ICON_WIDTH);
                        drawable.draw(canvas);
                    }
                } catch (Exception ex) {

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
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!Env.ICON_REGION_INIT) {
            return true;
        }
        int xPicIndex = (int) ((event.getX() - xStart) / Env.ICON_WIDTH);
        int yPicIndex = (int) ((event.getY() - yStart) / Env.ICON_WIDTH);
        if (xPicIndex >= chart.xSize || xPicIndex < 0 || yPicIndex >= chart.ySize || yPicIndex < 0) {
            return true;
        }
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            Tile touchTile = chart.get(xPicIndex, yPicIndex);
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
                    SoundEffectUtils.getInstance().playDisapperSound();
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

    private void LOGD(String msg) {
        if (com.tinygame.lianliankan.config.Config.DEBUG) {
            Log.d(TAG, msg);
        }
    }
}
