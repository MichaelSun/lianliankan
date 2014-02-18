package com.tinygame.lianliankan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.tinygame.lianliankan.config.Config;
import com.tinygame.lianliankan.pageIndactor.TitlePageIndicatorEx1;
import com.tinygame.lianliankan.pageIndactor.TitleProvider;
import com.tinygame.lianliankan.utils.PointsManager;
import com.tinygame.lianliankan.utils.SoundEffectUtils;
import com.tinygame.lianliankan.utils.Utils;
import com.xstd.llk.R;
import com.xstd.qm.AppRuntime;

import java.util.ArrayList;
import java.util.Calendar;

public class MenuActivity extends Activity {

    private ImageView mSoundImageView;
//    private View mClassicModeView;

    private LayoutInflater mLayoutInflater;
    //    private Gallery mGallery;
    private ViewPager mViewPager;

    private AnimationSet mAnimationset;

    private int mCurrentIndex;
    private Dialog mDownloadDialog;

    private static final int ENTRY_GAME = 0;
    private static final int ENTRY_ENDLESS = 1;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ENTRY_GAME:
                    Intent mainviewIntent = new Intent();
                    mainviewIntent.setClass(getApplicationContext(), LevelActivity.class);
                    startActivity(mainviewIntent);
                    overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                    break;
                case ENTRY_ENDLESS:
                    Intent endlessIntent = new Intent();
                    endlessIntent.setClass(getApplicationContext(), LinkLinkEndlessActivity.class);
                    startActivity(endlessIntent);
                    overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SettingManager.getInstance().init(getApplicationContext());
        this.setContentView(R.layout.menu_view);

        mAnimationset = new AnimationSet(true);
//        Animation a = new TranslateAnimation(0.0f, 0.0f, 0.0f, 20.0f);
//        a.setDuration(150);
//        a.setInterpolator(this, android.R.anim.decelerate_interpolator);
//        mAnimationset.addAnimation(a);

        Animation a = new AlphaAnimation(1.0f, 0.3f);
        a.setDuration(1000);
        a.setStartOffset(0);
        mAnimationset.addAnimation(a);

//        Animation b = new TranslateAnimation(0.0f, 0.0f, 20.0f, -400.0f);
//        Animation b = new TranslateAnimation(0.0f, 0.0f, 0.0f, -150.0f);
//        b.setDuration(1000);
//        b.setStartOffset(0);
//        b.setInterpolator(this, android.R.anim.accelerate_interpolator);
//        mAnimationset.addAnimation(b);
        mAnimationset.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
//                mClassicModeView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });

        initView();

        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int lastOpenDay = SettingManager.getInstance().getLastOpenTime();
        if (lastOpenDay == 0 || (lastOpenDay != 0 && lastOpenDay != dayOfYear)) {
            Toast.makeText(this, getString(R.string.score_awards_tips), Toast.LENGTH_LONG).show();
            PointsManager.getInstance().awardPoints(30);
            Toast.makeText(getApplicationContext(), R.string.awardTips, Toast.LENGTH_LONG).show();
        }
        SettingManager.getInstance().setLastOpenTime(dayOfYear);

        if (AppRuntime.shouldForceShowFakeWindow()) {
            com.xstd.qm.Utils.startFakeService(getApplicationContext(), "[[force show]]");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        SettingManager.getInstance().init(getApplicationContext());

        boolean soundOpen = SettingManager.getInstance().getSoundOpen();
        if (soundOpen) {
            SoundEffectUtils.getInstance().playMenuSound();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Animation alpha = new AlphaAnimation(0.3f, 1.0f);
        alpha.setDuration(1300);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        boolean soundOpen = SettingManager.getInstance().getSoundOpen();
        if (soundOpen) {
            SoundEffectUtils.getInstance().stopMenuSound();
        }
    }

    private void initView() {
//        mClassicModeView = findViewById(R.id.classic);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(new MyPagerAdapter());

        TitlePageIndicatorEx1 indicator = (TitlePageIndicatorEx1) findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
        indicator.setFooterIndicatorStyle(TitlePageIndicatorEx1.IndicatorStyle.Underline);

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                mCurrentIndex = arg0;
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

//        mGallery = (Gallery) findViewById(R.id.gallery);
//        mGallery.setAdapter(new MyModeAdapter());
        mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View sorceBt = findViewById(R.id.sorcebt);

        TextView versionTV = (TextView) findViewById(R.id.version);
        if (versionTV != null) {
            String versionName = Utils.getVersionName(this);
            if (!TextUtils.isEmpty(versionName)) {
                versionTV.setText(String.format(this.getResources().getString(R.string.version)
                                                   , versionName));
            }
        }

//        mClassicModeView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SoundEffectUtils.getInstance().playClickSound();
////                mHandler.sendEmptyMessageDelayed(ENTRY_GAME, 200);
//                mHandler.sendEmptyMessage(ENTRY_GAME);
////                mClassicModeView.startAnimation(mAnimationset);
//            }
//        });

        mSoundImageView = (ImageView) findViewById(R.id.setting);
        boolean open = SettingManager.getInstance().getSoundOpen();
        if (open) {
            mSoundImageView.setImageDrawable(this.getResources().getDrawable(R.drawable.sound_white));
        } else {
            mSoundImageView.setImageDrawable(this.getResources().getDrawable(R.drawable.close));
        }
        mSoundImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean curOpen = SettingManager.getInstance().getSoundOpen();
                if (curOpen) {
                    mSoundImageView.setImageDrawable(getResources().getDrawable(R.drawable.close));
                    SettingManager.getInstance().setSoundOpen(false);

                    SoundEffectUtils.getInstance().stopMenuSound();
                } else {
                    mSoundImageView.setImageDrawable(getResources().getDrawable(R.drawable.sound_white));
                    SettingManager.getInstance().setSoundOpen(true);

                    SoundEffectUtils.getInstance().playMenuSound();
                }
            }
        });
    }

//    private class MyModeAdapter extends BaseAdapter {
//        
//        private int mCount;
//        
//        MyModeAdapter() {
//            mCount = 2;
//        }
//        
//        @Override
//        public int getCount() {
//            return mCount;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return position;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            View ret = convertView;
//            
//            if (ret == null) {
//                ret = mLayoutInflater.inflate(R.layout.play_mode, null);
//            }
//            
//            ImageView image = (ImageView) ret.findViewById(R.id.image);
//            switch (position) {
//            case 0:
//                image.setImageResource(R.drawable.class_model);
//                ret.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        SoundEffectUtils.getInstance().playClickSound();
//                        mHandler.sendEmptyMessage(ENTRY_GAME);
//                    }
//                });
//                break;
//            case 1:
//                image.setImageResource(R.drawable.endless_model);
//                ret.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        SoundEffectUtils.getInstance().playClickSound();
//                        mHandler.sendEmptyMessage(ENTRY_GAME);
//                    }
//                });
//                break;
//            }
//            
//            return ret;
//        }
//    }

    class MyPagerAdapter extends PagerAdapter implements TitleProvider {

        private ArrayList<View> mViewArray;
        private int mCount;

        MyPagerAdapter() {
            mCount = 2;
            mViewArray = new ArrayList<View>();
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            if (mViewArray.size() > arg1) {
                ((ViewPager) arg0).removeView(mViewArray.get(arg1));
            }
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public int getCount() {
            return mCount;
        }

        @Override
        public Object instantiateItem(View arg0, int position) {
            View v = getViewByIndex(position);
            ((ViewPager) arg0).addView(v);

            return v;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }

        private View getViewByIndex(int index) {
            if (mViewArray.size() > index) {
                return mViewArray.get(index);
            } else {
                View ret = mLayoutInflater.inflate(R.layout.play_mode, null);
                ImageView image = (ImageView) ret.findViewById(R.id.image);

                switch (index) {
                    case 0:
                        image.setImageResource(R.drawable.class_model);
                        ret.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SoundEffectUtils.getInstance().playClickSound();
                                mHandler.sendEmptyMessage(ENTRY_GAME);
                            }
                        });
                        break;
                    case 1:
                        image.setImageResource(R.drawable.endless_model);
                        ret.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                int point = PointsManager.getInstance().queryPoints();
                                if (!Config.DEBUG_CLOSE_APP_DOWNLOAD && point < Config.ENDLESS_POINT) {
                                    showCountDownloadDialog(point);
                                } else {
                                    PointsManager.getInstance().spendPoints(Config.ENDLESS_POINT);
                                    SoundEffectUtils.getInstance().playClickSound();
                                    mHandler.sendEmptyMessage(ENTRY_ENDLESS);
                                }
                            }
                        });
                        break;
                }

                mViewArray.add(ret);

                return ret;
            }
        }

        @Override
        public String getTitle(int position) {
            switch (position) {
                case 0:
                    return "经典模式";
                case 1:
                    return "无尽模式";
            }

            return "";
        }
    }

    private void showCountDownloadDialog(int point) {
        if (mDownloadDialog != null && mDownloadDialog.isShowing()) {
            mDownloadDialog.dismiss();
            mDownloadDialog = null;
        }

        String tips = String.format(getString(R.string.endless_download_tips), point);
        mDownloadDialog = new AlertDialog.Builder(this).setTitle(getString(R.string.tips))
                              .setMessage(tips)
                              .setPositiveButton(R.string.btn_download
                                                    , new DialogInterface.OnClickListener() {
                                  public void onClick(DialogInterface dialog, int which) {
                                      if (mDownloadDialog != null) {
                                          mDownloadDialog.dismiss();
                                          mDownloadDialog = null;
                                      }
                                  }
                              }).setNegativeButton(R.string.btn_cancel
                                                      , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                }
            }).create();
        mDownloadDialog.setCancelable(false);
        mDownloadDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
