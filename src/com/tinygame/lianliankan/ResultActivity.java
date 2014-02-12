package com.tinygame.lianliankan;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import com.tinygame.lianliankan.config.Config;
import com.tinygame.lianliankan.db.DatabaseOperator;
import com.tinygame.lianliankan.db.DatabaseOperator.LevelInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class ResultActivity extends Activity {

    public static final String RESULT_TYPE = "result_type";
    public static final String COST_TIME = "cost_time";
    public static final String COUNT = "count";
    public static final String CONTINUE_COUNT = "continue_count";
    public static final String CATEGORY = "category";
    public static final String LEVEL = "level";

    public static final String GAME_MODE = "mode";

    public static final int SUCCESS_CONTENT = 0;
    public static final int FAILED_CONTENT = 1;

    public static final int RETURN_QUIT = 100;
    public static final int RETURN_RETRY = 101;
    public static final int RETURN_NEXT = 102;

    private int mResultType;
    private int mGameMode;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

        mGameMode = getIntent().getIntExtra(GAME_MODE, Config.NORMAL_MODE);
        mResultType = getIntent().getIntExtra(RESULT_TYPE, SUCCESS_CONTENT);
        if (mResultType == SUCCESS_CONTENT) {
            setContentView(R.layout.win_view);

            initShareToRenRen();

            String time = getIntent().getStringExtra(COST_TIME);
            String count = getIntent().getStringExtra(COUNT);
            String continue_count = getIntent().getStringExtra(CONTINUE_COUNT);
            int category = getIntent().getIntExtra(CATEGORY, 0);
            int level = getIntent().getIntExtra(LEVEL, 0);
            int totalCount = 0;
            boolean shouldUpateSorce = false;

            if (mGameMode == Config.NORMAL_MODE) {
                ArrayList<LevelInfo> infos = DatabaseOperator.getInstance()
                                                 .getLevelInfoForCategory(category, level);
                for (LevelInfo info : infos) {
                    totalCount += info.count;
                }

                TextView contentTV = (TextView) findViewById(R.id.content);
                String showContent = String.format(getString(R.string.win_content)
                                                      , time
                                                      , count
                                                      , totalCount
                                                      , continue_count);
                int historyScore = SettingManager.getInstance().getHighScore();
                if (totalCount > historyScore) {
                    SettingManager.getInstance().setHighScore(totalCount);
                    shouldUpateSorce = true;
                }

                contentTV.setText(showContent);
            } else if (mGameMode == Config.ENDLESS_MODE) {
                LevelInfo info = DatabaseOperator.getInstance().getEndlessInfo(category, level);
                totalCount = info.count;
                TextView contentTV = (TextView) findViewById(R.id.content);
                String showContent = String.format(getString(R.string.endless_win_content)
                                                      , time
                                                      , totalCount
                                                      , continue_count);
                int historyScore = SettingManager.getInstance().getEndlessHighScore();
                if (totalCount > historyScore) {
                    SettingManager.getInstance().setEndlessHighScore(totalCount);
                    shouldUpateSorce = true;
                } else {
                    ImageView tipsIcon = (ImageView) findViewById(R.id.tips_icon);
                    tipsIcon.setBackgroundResource(R.drawable.lost);
                }

                contentTV.setText(showContent);
            }

            View retry = findViewById(R.id.retry);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RETURN_RETRY);
                    finish();
                }
            });

            View quit = findViewById(R.id.quit);
            quit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RETURN_QUIT);
                    finish();
                }
            });

            View next = findViewById(R.id.next);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RETURN_NEXT);
                    finish();
                }
            });

            if (shouldUpateSorce) {
                if (mGameMode == Config.ENDLESS_MODE) {
                } else if (mGameMode == Config.NORMAL_MODE) {
                }
            }

        } else if (mResultType == FAILED_CONTENT) {
            setContentView(R.layout.lose_view);

            initShareToRenRen();

            View retry = findViewById(R.id.retry);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RETURN_RETRY);
                    finish();
                }
            });

            View quit = findViewById(R.id.quit);
            quit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RETURN_QUIT);
                    finish();
                }
            });
        }

        View sorceBt = findViewById(R.id.sorcebt);
        sorceBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGameMode == Config.ENDLESS_MODE) {
                } else if (mGameMode == Config.NORMAL_MODE) {
                }
            }
        });

        if (mGameMode == Config.ENDLESS_MODE) {
            View next = findViewById(R.id.next);
            next.setVisibility(View.GONE);
        }
    }

    private void initShareToRenRen() {
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
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        View tips = findViewById(R.id.tips_icon);
        Animation alpha = new AlphaAnimation(0.3f, 1.0f);
        alpha.setDuration(1300);
        tips.startAnimation(alpha);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //eat the key code
            return true;
        }

        return false;
    }
}
