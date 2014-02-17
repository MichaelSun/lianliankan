package com.tinygame.lianliankan.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.util.Log;
import com.tinygame.lianliankan.config.Config;
import com.xstd.llk.R;

public class SoundEffectUtils {
    private static final String TAG = "SoundEffectUtils";

    private Context mContext;
    private SoundPool mSoundNotifySoundPool;
    private static SoundEffectUtils gSoundEffectUtils = new SoundEffectUtils();
    private int mClickPlayID;
    private int mConnectPlayID;
    private MediaPlayer mMediaPlayer;
    
    public static SoundEffectUtils getInstance() {
        return gSoundEffectUtils;
    }
    
    public void init(Context context) {
        mContext = context;
        mSoundNotifySoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        
        mSoundNotifySoundPool.unload(R.raw.click);
        mSoundNotifySoundPool.unload(R.raw.disappear);

        mClickPlayID = mSoundNotifySoundPool.load(mContext, R.raw.click, 0);
        mConnectPlayID = mSoundNotifySoundPool.load(mContext, R.raw.disappear, 0);
    }
    
    public void playClickSound() {
        if (Config.SOUND_DEBUG) {
            return;
        }
        
        if (mSoundNotifySoundPool != null) {
            mSoundNotifySoundPool.play(mClickPlayID, (float) 0.3, (float) 0.5, 0, 0, 1);
        }
    }
    
    public void playDisapperSound() {
        if (Config.SOUND_DEBUG) {
            return;
        }
        
        if (mSoundNotifySoundPool != null) {
            mSoundNotifySoundPool.play(mConnectPlayID, (float) 0.5, (float) 0.5, 0, 0, 1);
        }
    }
    
    public void playReadySound() {
//        if (Config.SOUND_DEBUG) {
//            return;
//        }
//        if (mSoundNotifySoundPool != null) {
//            mSoundNotifySoundPool.play(mReadyGoID, (float) 0.1, (float) 0.3, 0, 0, 1);
//        }
    }
    
    public void playSpeedSound() {
//        if (Config.SOUND_DEBUG) {
//            return;
//        }
//
//        if (mMediaPlayer != null) {
//            mMediaPlayer.stop();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//        }
//
//        if (mContext == null) {
//            return;
//        }
//
//        mMediaPlayer = new MediaPlayer();
//        mMediaPlayer = MediaPlayer.create(mContext, R.raw.game_classic_back);
//        try {
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mMediaPlayer.setLooping(true);
//            mMediaPlayer.prepare();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//            }
//        });
//
//        try {
//            mMediaPlayer.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//        }
    }
    
    public void stopSpeedSound() {
        if (Config.SOUND_DEBUG) {
            return;
        }
        
        LOGD("[[stopSpeedSound]]");
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
    
    public void playMenuSound() {
//        if (Config.SOUND_DEBUG) {
//            return;
//        }
//
//        if (mMediaPlayer != null) {
//            mMediaPlayer.stop();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//        }
//
//        if (mContext == null) {
//            return;
//        }
//
//        mMediaPlayer = new MediaPlayer();
//        mMediaPlayer = MediaPlayer.create(mContext, R.raw.game_main_back);
//        try {
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mMediaPlayer.setLooping(true);
//            mMediaPlayer.prepare();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//            }
//        });
//
//        try {
//            mMediaPlayer.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//        }
    }
    
    public void stopMenuSound() {
        if (Config.SOUND_DEBUG) {
            return;
        }
        
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
