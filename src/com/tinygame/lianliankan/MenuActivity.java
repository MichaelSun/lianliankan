package com.tinygame.lianliankan;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;

import com.tinygame.lianliankan.pay.Pay;
import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;
import net.youmi.android.offers.PointsManager;
import net.youmi.android.spot.SpotManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
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

import com.mobclick.android.MobclickAgent;
import com.renren.mobile.rmsdk.component.share.ShareActivity;
import com.renren.mobile.rmsdk.core.RMConnectCenter;
import com.renren.mobile.rmsdk.core.RMConnectCenter.AuthVerifyListener;
import com.tinygame.lianliankan.config.Config;
import com.tinygame.lianliankan.pageIndactor.TitlePageIndicatorEx1;
import com.tinygame.lianliankan.pageIndactor.TitleProvider;
import com.tinygame.lianliankan.utils.SoundEffectUtils;
import com.tinygame.lianliankan.utils.Utils;
import com.wiyun.game.WiGame;

public class MenuActivity extends Activity {

    private ImageView mSoundImageView;
//    private View mClassicModeView;
    
    private LayoutInflater mLayoutInflater;
//    private Gallery mGallery;
    private ViewPager mViewPager;
    
    private AnimationSet mAnimationset;
    
    private int mCurrentIndex;
    private Dialog mDownloadDialog;
    
    private RMConnectCenter mRMCenter;
    
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
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,    
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SettingManager.getInstance().init(getApplicationContext());
        this.setContentView(R.layout.menu_view);
        MobclickAgent.onError(this);
        
        mRMCenter = RMConnectCenter.getInstance(this);
        mRMCenter.setClientInfo(Config.RR_API_KEY, Config.RR_SECRET_KEY, Config.RR_APP_ID);
        mRMCenter.setAuthVerifyListener(new AuthVerifyListener() {
            @Override
            public void onAuthVerifySuccess() {
                Toast.makeText(getApplicationContext(), getString(R.string.login_success), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAuthVerifyFailed() {
                Toast.makeText(getApplicationContext(), getString(R.string.login_failed), Toast.LENGTH_LONG).show();
            }
        });
        mRMCenter.initFromLauncher(this);
        
        View renren = findViewById(R.id.renren_logo);
        renren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getWindow().getDecorView();
                Bitmap bmp = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                        android.graphics.Bitmap.Config.ARGB_8888);
                view.draw(new Canvas(bmp));
                String fileName = "my_screen_shot_upload.png";
                String path = getCacheDir().getAbsolutePath();
                File file = new File(path + "/" + fileName);
                try {
                    FileOutputStream os = new FileOutputStream(file);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bmp.compress(CompressFormat.JPEG, 100, bos);
                    byte[] data = bos.toByteArray();
                    os.write(data);
                    bos.close();
                    os.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                bmp.recycle();
                bmp = null;
                ShareActivity.share(MenuActivity.this, file, getString(R.string.share_tips));
            }
        });
        
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
        initYoumi();
        
        int dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        int lastOpenDay = SettingManager.getInstance().getLastOpenTime();
        if (lastOpenDay == 0 || (lastOpenDay != 0 && lastOpenDay != dayOfYear)) {
            Toast.makeText(this, getString(R.string.score_awards_tips), Toast.LENGTH_LONG).show();
            PointsManager.getInstance(this.getApplicationContext()).awardPoints(30);
            Toast.makeText(getApplicationContext(), R.string.awardTips, Toast.LENGTH_LONG).show();
        }
        SettingManager.getInstance().setLastOpenTime(dayOfYear);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        SettingManager.getInstance().init(getApplicationContext());
        
        boolean soundOpen = SettingManager.getInstance().getSoundOpen();
        if (soundOpen) {
            SoundEffectUtils.getInstance().playMenuSound();        
        }
        
        SpotManager.getInstance(this).loadSpotAds();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        Animation alpha = new AlphaAnimation(0.3f, 1.0f);
        alpha.setDuration(1300);
//        mClassicModeView.startAnimation(alpha);
        
        MobclickAgent.onResume(this);
        MobclickAgent.onEvent(this, Config.ACTION_START_LAUNCH, "MenuActiviy_resume");
    }
    
    @Override
    public void onPause() {
        super.onPause();
        
        MobclickAgent.onEvent(this, Config.ACTION_START_LAUNCH, "MenuActiviy_pause");
        MobclickAgent.onPause(this);
    }
    
    @Override
    public void onStop() {
        super.onStop();
        boolean soundOpen = SettingManager.getInstance().getSoundOpen();
        if (soundOpen) {
            SoundEffectUtils.getInstance().stopMenuSound();
        }
    }
    
    private void initYoumi() {
        AdManager.getInstance(this.getApplicationContext()).init(Config.YOUMI_APP_ID, Config.YOUMI_APP_SECRET_KEY, false);
        OffersManager.getInstance(this.getApplicationContext()).onAppLaunch();
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
        sorceBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mCurrentIndex) {
                case 0:
                    WiGame.openLeaderboard(Config.WIGAME_SORCE_KEY);
                    break;
                case 1:
                    WiGame.openLeaderboard(Config.WIGAME_ENDLESS_KEY);
                    break;
                }
                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
            }
        });
        
        View wigame = findViewById(R.id.wigame);
        wigame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WiGame.startUI();
            }
        });
        
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
                            int point = PointsManager.getInstance(getApplicationContext()).queryPoints();
                            if (!Config.DEBUG_CLOSE_APP_DOWNLOAD && point < Config.ENDLESS_POINT) {
                                showCountDownloadDialog(point);
                            } else {
                                PointsManager.getInstance(getApplicationContext()).spendPoints(Config.ENDLESS_POINT);
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
                                OffersManager.getInstance(getApplicationContext()).showOffersWall();
                                MobclickAgent.onEvent(MenuActivity.this, Config.ACTION_OFFER_LABEL);
                            }
                }).setNegativeButton(R.string.btn_cancel
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                MobclickAgent.onEvent(MenuActivity.this, Config.ACTION_OFFER_CANCEL_LABEL);
                                overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
                            }
                        }).create();
        mDownloadDialog.setCancelable(false);
        mDownloadDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mRMCenter.onActivityResult(requestCode, resultCode, data);
    }
}
