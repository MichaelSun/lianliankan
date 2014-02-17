package com.tinygame.lianliankan.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.tinygame.lianliankan.LinkLinkApplication;
import com.xstd.llk.R;

public class JPSplashView extends RelativeLayout {
    
    public interface SplashDispalyListener {
        void onAnimationFinished();
    }
    
    private ImageView mImageBackTop;
    private ImageView mImageBackMid;
    private ImageView mImageBackBottom;
    private ImageView mImageLight;
    private ImageView mImageLine;
    private ImageView mImageLogo;
    private SplashDispalyListener mSplashDispalyListener;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

        }

    };

    public JPSplashView(Context context) {
        super(context);

        initView(context);
    }

    public JPSplashView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView(context);
    }
    
    public void setSplashDispalyListener(SplashDispalyListener l) {
        mSplashDispalyListener = l;
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.splash, null);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT
                                                    , LayoutParams.FILL_PARENT);
        this.addView(view, param);

        // mImageBackTop = (ImageView)
        // view.findViewById(R.id.splashview_back_top);
        // mImageBackMid = (ImageView)
        // view.findViewById(R.id.splashview_back_mid);
        // mImageBackBottom = (ImageView)
        // view.findViewById(R.id.splashview_back_bottom);
        // mImageLight = (ImageView) view.findViewById(R.id.splashview_light);
        // mImageLine = (ImageView) view.findViewById(R.id.splashview_line);
        mImageLogo = (ImageView) view.findViewById(R.id.logo);

        // mImageLight.setVisibility(GONE);
        // mImageLine.setVisibility(GONE);
        // mImageLogo.setVisibility(GONE);

        // updateBitmap(mImageBackTop);
        updateBitmap(mImageLogo);
    }

    private void updateBitmap(ImageView imageview) {
        try {
            ViewGroup.LayoutParams param = (ViewGroup.LayoutParams) imageview.getLayoutParams();
            if (param != null) {
                BitmapDrawable drawable = (BitmapDrawable) imageview.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                int oriW = bitmap.getWidth();
                int oriH = bitmap.getHeight();
                int dstW = LinkLinkApplication.SCREEN_WIDTH;
                int dstH = oriH * dstW / oriW;
                param.width = dstW;
                param.height = dstH;
                imageview.setLayoutParams(param);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseRes() {
        // mImageBackTop.setImageDrawable(null);
        // mImageBackMid.setImageDrawable(null);
        // mImageBackBottom.setImageDrawable(null);
        // mImageLight.setImageDrawable(null);
        // mImageLine.setImageDrawable(null);
        mImageLogo.setImageDrawable(null);
    }

    public void startWork() {
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // FadeIn(mImageLight);
                // FadeIn(mImageLine);
                FadeIn(mImageLogo);
            }

        }, 0);
    }

    // 是否开始旋转出
    private boolean bRotate = false;

    private synchronized boolean isRotateOut() {
        return bRotate;
    }

    private synchronized void setRotateOut(boolean b) {
        bRotate = b;
    }

    private void FadeIn(ImageView iv) {
        Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1500);
        fadeIn.setFillAfter(true);
        fadeIn.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!isRotateOut()) {
                    setRotateOut(true);
                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            rorateOut();
                        }

                    }, 300);
                }
            }
        });
        iv.startAnimation(fadeIn);
    }

    private void rorateOut() {
        JPRotate3DAnimation animation = new JPRotate3DAnimation(0, -90, 0, 0, 0, this.getHeight() / 2);
        animation.setDuration(900);
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                JPSplashView.this.setVisibility(View.GONE);
                releaseRes();
                
                if (mSplashDispalyListener != null) {
                    mSplashDispalyListener.onAnimationFinished();
                }
            }
        });
        this.startAnimation(animation);
    }

}
