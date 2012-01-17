package com.tinygame.lianliankan.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.util.Log;

import com.tinygame.lianliankan.R;

public class SoundEffectUtils {
    private static final String TAG = "SoundEffectUtils";

    private Context mContext;
    private static SoundPool gSoundNotifySoundPool;
    private static SoundEffectUtils gSoundEffectUtils = new SoundEffectUtils();
    private int mClickPlayID;
    private int mConnectPlayID;
    private int mReadyGoID;
    private MediaPlayer mMediaPlayer;
    
    public static SoundEffectUtils getInstance() {
        return gSoundEffectUtils;
    }
    
    public void init(Context context) {
        mContext = context;
        gSoundNotifySoundPool = new SoundPool(3, AudioManager.STREAM_NOTIFICATION, 0);
        mClickPlayID = gSoundNotifySoundPool.load(mContext, R.raw.click, 0);
        mConnectPlayID = gSoundNotifySoundPool.load(mContext, R.raw.disappear, 0);
        mReadyGoID = gSoundNotifySoundPool.load(mContext, R.raw.ready_go, 0);
    }
    
    public void playClickSound() {
        gSoundNotifySoundPool.play(mClickPlayID, (float) 0.6, (float) 0.8, 0, 0, 1);
    }
    
    public void playDisapperSound() {
        gSoundNotifySoundPool.play(mConnectPlayID, (float) 0.3, (float) 0.5, 0, 0, 1);
    }
    
    public void playReadySound() {
        gSoundNotifySoundPool.play(mReadyGoID, (float) 0.1, (float) 0.3, 0, 0, 1);
    }
    
    public void playSpeedSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer = MediaPlayer.create(mContext, R.raw.game_classic_back);
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });

        try {
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
    
    public void stopSpeedSound() {
        LOGD("[[stopSpeedSound]]");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
    
    public void playMenuSound() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer = MediaPlayer.create(mContext, R.raw.game_main_back);
        try {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
            }
        });

        try {
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
    
    public void stopMenuSound() {
        LOGD("[[stopMenuSound]]");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
    
    
    private SoundEffectUtils() {
    }
    
    private void LOGD(String msg) {
        if (com.tinygame.lianliankan.config.Config.DEBUG) {
            Log.d(TAG, msg);
        }
    }
}
