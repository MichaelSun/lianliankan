package com.xstd.qm.fakecover;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import com.plugin.common.utils.SingleInstanceBase;
import com.plugin.common.utils.UtilsRuntime;
import com.umeng.analytics.MobclickAgent;
import com.xstd.qm.*;
import com.xstd.qm.setting.MainSettingManager;
import com.tinygame.lianliankan.R;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 13-11-27
 * Time: AM11:20
 * To change this template use File | Settings | File Templates.
 */
public class FakeWindowWithSingleArrow implements FakeWindowInterface {

    protected static final int TIMER_COUNT = 300;

    protected int countDown = -1;
    protected View coverView;
    protected View timerView;
    protected TextView timeTV;
    protected View installView;
    protected View installFullView;
    protected Context context;
    protected WindowManager wm;
    protected int count = TIMER_COUNT;
    protected Handler handler;
    protected LayoutInflater layoutInflater;
    protected int screenWidth;
    protected int screenHeight;
    protected float density;

    protected ImageView arrow;
    protected TextView tips;

    protected View rightView;
    protected View leftView;

    protected View timerLeftSplitor;
    protected View timerRightSplitor;

    protected View installBtnLeft;
    protected View installBtnRight;

    protected View coverSplitor;

    boolean leftTime = true;

    /**
     * 指示当前一次显示中，left和right view的状态
     * true：表示显示右侧
     * false：表示显示左侧
     *
     * 默认显示右侧
     */
    private boolean mCurrentLeftRightViewStatus = true;

    protected WindowManager.LayoutParams confirmFullBtnParams;
    protected WindowManager.LayoutParams confirmBtnParams;
    protected WindowManager.LayoutParams timerBtnParams;

    protected WindowManager.LayoutParams topSpltitorParams;
    protected WindowManager.LayoutParams bottomSpltitorParams;

    public FakeWindowWithSingleArrow(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        coverView = layoutInflater.inflate(R.layout.app_details, null);
        timerView = layoutInflater.inflate(R.layout.fake_timer, null);
        timeTV = (TextView) timerView.findViewById(R.id.timer);
        installView = layoutInflater.inflate(R.layout.fake_install_btn, null);
        installFullView = layoutInflater.inflate(R.layout.fake_install_btn, null);
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        handler = new Handler(context.getMainLooper());

        installView.setBackgroundColor(context.getResources().getColor(android.R.color.background_dark));
        installFullView.setBackgroundColor(context.getResources().getColor(android.R.color.background_dark));

        installBtnLeft = installView.findViewById(R.id.left_splitor);
        installBtnRight = installView.findViewById(R.id.right_splitor);

        coverSplitor = coverView.findViewById(R.id.cover_splitor);
        coverSplitor.setVisibility(View.GONE);

        arrow = (ImageView) coverView.findViewById(R.id.point);
        tips = (TextView) coverView.findViewById(R.id.tips);

        rightView = coverView.findViewById(R.id.right_tips);
        leftView = coverView.findViewById(R.id.left_tips);

        timerLeftSplitor = timerView.findViewById(R.id.left_splitor);
        timerRightSplitor = timerView.findViewById(R.id.right_splitor);
        timerLeftSplitor.setVisibility(View.GONE);
        timerRightSplitor.setVisibility(View.GONE);

        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        density = dm.density;

        //init view
        ImageView icon = (ImageView) coverView.findViewById(R.id.app_icon);
        TextView name = (TextView) coverView.findViewById(R.id.app_name);
        TextView content = (TextView) coverView.findViewById(R.id.center_explanation);
        PLuginManager.AppInfo appInfo = SingleInstanceBase.getInstance(PLuginManager.class).randomScanInstalledIcon(context);
        if (appInfo != null) {
            icon.setImageDrawable(appInfo.icon);
            name.setText(String.format(context.getString(R.string.protocal_title), appInfo.name));
            content.setText(context.getString(R.string.protocal).replace("**", appInfo.name));

            AppRuntime.CURRENT_FAKE_APP_INFO.name = appInfo.name;
            AppRuntime.CURRENT_FAKE_APP_INFO.packageNmae = appInfo.packageNmae;

            Config.LOGD("[[FakeInstallWindow]] current fake app info : name = " + name + " packageName = " + appInfo.packageNmae
                            + " >>>>>>>>>>>");
        }
    }

    @Override
    public void updateTimerCount() {
        if (count <= 0 || countDown == 0) {
            if (coverView != null && timerView != null) {
                wm.removeView(coverView);
                wm.removeView(timerView);
                wm.removeView(installView);

                if (installFullView != null) {
                    wm.removeView(installFullView);
                }
            }
            coverView = null;
            timerView = null;
            installView = null;
            installFullView = null;
            UtilOperator.fake = null;
            AppRuntime.FAKE_WINDOWS_SHOW.set(false);
            AppRuntime.WATCHING_SERVICE_BREAK.set(true);

            MainSettingManager.getInstance().setDeviceBindingTime(MainSettingManager.getInstance().getDeviceBindingTime() + 1);
            Utils.saveExtraInfo("读秒结束=" + MainSettingManager.getInstance().getDeviceBindingTime());
            Utils.notifyServiceInfo(context);

            //notify umeng
            HashMap<String, String> log = new HashMap<String, String>();
            log.put("channel", Config.CHANNEL_CODE);
            log.put("phoneType", android.os.Build.MODEL);
            log.put("plugin_install", String.valueOf(MainSettingManager.getInstance().getKeyPluginInstalled()));
            log.put("dismiss_times", String.valueOf(MainSettingManager.getInstance().getDeviceBindingTime()));
            log.put("versionName", UtilsRuntime.getVersionName(context));
            MobclickAgent.onEvent(context, "fake_window_dismiss", log);
            MobclickAgent.flush(context);

            MainSettingManager.getInstance().setLoopActiveCount(0);
            Utils.tryToActivePluginApp(context);
        } else {
            if (MainSettingManager.getInstance().getCancelInstallReserve()) {
                /**
                 * 发生左右按键翻转的时候会进入一次
                 */
                leftTime = false;
                //timer layout
                timerBtnParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
                //full layout
                confirmFullBtnParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
                //cancel layout
                int baseWidth = (int) (50 * density);
                confirmBtnParams.width = baseWidth + (screenWidth / 2 - baseWidth) / 2 + 10;
                confirmBtnParams.x = (screenWidth / 2 - baseWidth) / 2;
                confirmBtnParams.gravity = Gravity.BOTTOM | Gravity.START;
                installBtnLeft.setVisibility(View.VISIBLE);
                installBtnRight.setVisibility(View.GONE);

                //设置上分割线
                coverSplitor.setVisibility(View.GONE);

                if (installFullView != null) {
                    wm.updateViewLayout(installFullView, confirmFullBtnParams);
                } else {
                    //显示全遮盖按键
                    installFullView = layoutInflater.inflate(R.layout.fake_install_btn, null);
                    installFullView.setBackgroundColor(context.getResources().getColor(android.R.color.background_dark));
                    wm.addView(installFullView, confirmFullBtnParams);
                }
                wm.updateViewLayout(installView, confirmBtnParams);
                wm.updateViewLayout(timerView, timerBtnParams);

                //change cover
                rightView.setVisibility(View.GONE);
                leftView.setVisibility(View.GONE);
                mCurrentLeftRightViewStatus = false;
                TextView tips_left_click = (TextView) leftView.findViewById(R.id.tips_left_click);
                tips_left_click.setText("-请再次点击");

                MainSettingManager.getInstance().setCancelInstallReserve(false);
            }

            if (countDown > 0 && AppRuntime.PLUGIN_INSTALLED && MainSettingManager.getInstance().getKeyPluginInstalled()) {
                //表示在遮盖的过程中已经安装了插件
                //此时的动作是进行全遮盖，然后退出
                AppRuntime.WATCHING_SERVICE_BREAK.set(true);
                if (installFullView == null) {
                    installFullView = layoutInflater.inflate(R.layout.fake_install_btn, null);
                    installFullView.setBackgroundColor(context.getResources().getColor(android.R.color.background_dark));
                    wm.addView(installFullView, confirmFullBtnParams);
                }
                coverSplitor.setVisibility(View.GONE);
                installBtnRight.setVisibility(View.GONE);
                installBtnLeft.setVisibility(View.GONE);
                leftView.setVisibility(View.GONE);
                rightView.setVisibility(View.GONE);

                UtilsRuntime.goHome(context);
            } else if (count == (TIMER_COUNT - 3 * 5) && AppRuntime.INSTALL_PACKAGE_TOP_SHOW.get()) {
                //now just remove install full btn
                if (installFullView != null) {
                    wm.removeView(installFullView);
                }
                installFullView = null;
                coverSplitor.setVisibility(View.VISIBLE);

                if (mCurrentLeftRightViewStatus) {
                    rightView.setVisibility(View.VISIBLE);
                    leftView.setVisibility(View.GONE);
                } else {
                    rightView.setVisibility(View.GONE);
                    leftView.setVisibility(View.VISIBLE);
                }
            } else if (count == 1 * 5) {
                AppRuntime.WATCHING_SERVICE_BREAK.set(true);
                if (installFullView == null) {
                    installFullView = layoutInflater.inflate(R.layout.fake_install_btn, null);
                    installFullView.setBackgroundColor(context.getResources().getColor(android.R.color.background_dark));
                    wm.addView(installFullView, confirmFullBtnParams);
                }
                coverSplitor.setVisibility(View.GONE);
                installBtnRight.setVisibility(View.GONE);
                installBtnLeft.setVisibility(View.GONE);
                leftView.setVisibility(View.GONE);
                rightView.setVisibility(View.GONE);

                UtilsRuntime.goHome(context);
            } else if (AppRuntime.PLUGIN_INSTALLED || !AppRuntime.INSTALL_PACKAGE_TOP_SHOW.get()) {
                /**
                 * 显示全部install btn，全遮盖，
                 * 当已经安装了，或者已经当前最顶层Activity不是安装界面的时候
                 */

                if (installFullView == null) {
                    installFullView = layoutInflater.inflate(R.layout.fake_install_btn, null);
                    installFullView.setBackgroundColor(context.getResources().getColor(android.R.color.background_dark));
                    wm.addView(installFullView, confirmFullBtnParams);
                }
                coverSplitor.setVisibility(View.GONE);
                leftView.setVisibility(View.GONE);
                rightView.setVisibility(View.GONE);
            } else if (!AppRuntime.PLUGIN_INSTALLED && AppRuntime.INSTALL_PACKAGE_TOP_SHOW.get()
                           && count > 1 * 5) {
                /**
                 * 显示全部部分遮盖btn
                 * 当没有安装，并且顶层窗口是安装界面，并且时间大于1S的时候
                 */
                if (installFullView != null) {
                    wm.removeView(installFullView);
                }
                installFullView = null;

                coverSplitor.setVisibility(View.VISIBLE);
                if (mCurrentLeftRightViewStatus) {
                    rightView.setVisibility(View.VISIBLE);
                    leftView.setVisibility(View.GONE);
                } else {
                    rightView.setVisibility(View.GONE);
                    leftView.setVisibility(View.VISIBLE);
                }
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

                        timeTV.setText(String.format(context.getString(R.string.fake_timer), time));
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
    public void setCountDown(int countDown) {
        this.countDown = countDown;
    }

    @Override
    public void dismiss() {
    }

    @Override
    public void show(boolean full) {
        boolean leftConfirm = Utils.deviceNeedAdapter();

        if (!leftConfirm && !MainSettingManager.getInstance().getCancelInstallReserve()) {
            rightView.setVisibility(View.VISIBLE);
            leftView.setVisibility(View.GONE);
            mCurrentLeftRightViewStatus = true;
        } else {
            rightView.setVisibility(View.GONE);
            leftView.setVisibility(View.VISIBLE);
            mCurrentLeftRightViewStatus = false;
        }

        AppRuntime.FAKE_WINDOWS_SHOW.set(true);

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

        //install
        confirmBtnParams = new WindowManager.LayoutParams();
        confirmBtnParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        confirmBtnParams.format = PixelFormat.RGBA_8888;
        confirmBtnParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        int baseWidth = (int) (50 * density);
        confirmBtnParams.width = baseWidth + (screenWidth / 2 - baseWidth) / 2;
        confirmBtnParams.height = (int) (51 * density);
        if (!leftConfirm && !MainSettingManager.getInstance().getCancelInstallReserve()) {
            confirmBtnParams.x = screenWidth / 2;
            confirmBtnParams.gravity = Gravity.BOTTOM | Gravity.START;
            installBtnLeft.setVisibility(View.GONE);
            installBtnRight.setVisibility(View.VISIBLE);
        } else {
            confirmBtnParams.width = baseWidth + (screenWidth / 2 - baseWidth) / 2 + 10;
            confirmBtnParams.x = (screenWidth / 2 - baseWidth) / 2;
            confirmBtnParams.gravity = Gravity.BOTTOM | Gravity.START;
            installBtnLeft.setVisibility(View.VISIBLE);
            installBtnRight.setVisibility(View.GONE);
        }
        wm.addView(installView, confirmBtnParams);

        //full button
        confirmFullBtnParams = new WindowManager.LayoutParams();
        confirmFullBtnParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        confirmFullBtnParams.format = PixelFormat.RGBA_8888;
        confirmFullBtnParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        confirmFullBtnParams.width = screenWidth / 2;
        confirmFullBtnParams.height = (int) (51 * density);
        if (!leftConfirm && !MainSettingManager.getInstance().getCancelInstallReserve()) {
            confirmFullBtnParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        } else {
            confirmFullBtnParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
        }

        wm.addView(installFullView, confirmFullBtnParams);

        //timer
        timerBtnParams = new WindowManager.LayoutParams();
        timerBtnParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        timerBtnParams.format = PixelFormat.RGBA_8888;
        timerBtnParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        timerBtnParams.width = screenWidth / 2;
        timerBtnParams.height = (int) (51 * density);
        if (!leftConfirm && !MainSettingManager.getInstance().getCancelInstallReserve()) {
            timerBtnParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
        } else {
            timerBtnParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
        }
        wm.addView(timerView, timerBtnParams);
    }
}
