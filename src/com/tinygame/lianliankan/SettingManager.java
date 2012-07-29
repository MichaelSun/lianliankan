package com.tinygame.lianliankan;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SettingManager {

    private static SettingManager gSettingManager = new SettingManager();
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    
    public static SettingManager getInstance() {
        return gSettingManager;
    }
    
    public int getLastDiff() {
        return mSharedPreferences.getInt(mContext.getString(R.string.perf_last_diff), 0);
    }
    
    public void setLastDiff(int diff) {
        mEditor.putInt(mContext.getString(R.string.perf_last_diff), diff);
        mEditor.commit();
    }
    
    public int getEndlessLastDiff() {
        return mSharedPreferences.getInt(mContext.getString(R.string.perf_last_diff_endless), 0);
    }
    
    public void setEndlessLastDiff(int diff) {
        mEditor.putInt(mContext.getString(R.string.perf_last_diff_endless), diff);
        mEditor.commit();
    }
    
    public int getLastCategory() {
        return mSharedPreferences.getInt(mContext.getString(R.string.perf_last_cate), 0);
    }
    
    public void setLastCategory(int category) {
        mEditor.putInt(mContext.getString(R.string.perf_last_cate), category);
        mEditor.commit();
    }
    
    public int getEndlessLastCategory() {
        return mSharedPreferences.getInt(mContext.getString(R.string.perf_last_cate_endless), 0);
    }
    
    public void setEndlessLastCategory(int category) {
        mEditor.putInt(mContext.getString(R.string.perf_last_cate_endless), category);
        mEditor.commit();
    }
    
    public boolean getSoundOpen() {
        return mSharedPreferences.getBoolean(mContext.getString(R.string.perf_sound_open), true);
    }
    
    public void setSoundOpen(boolean open) {
        mEditor.putBoolean(mContext.getString(R.string.perf_sound_open), open);
        mEditor.commit();        
    }
    
    public int getHighScore() {
        return mSharedPreferences.getInt(mContext.getString(R.string.perf_score), 0);
    }
    
    public void setHighScore(int score) {
        mEditor.putInt(mContext.getString(R.string.perf_score), score);
        mEditor.commit();   
    }
    
    public int getEndlessHighScore() {
        return mSharedPreferences.getInt(mContext.getString(R.string.perf_score_endless), 0);
    }
    
    public void setEndlessHighScore(int score) {
        mEditor.putInt(mContext.getString(R.string.perf_score_endless), score);
        mEditor.commit();   
    }
    
    public void setOpenLevel(int level) {
        mEditor.putInt(mContext.getString(R.string.perf_level_opened), level);
        mEditor.commit();
    }
    
    public int getOpenLevel() {
        return mSharedPreferences.getInt(mContext.getString(R.string.perf_level_opened), 1);
    }
    
    public void setOpenLevelWithCategory(int level, int category) {
        mEditor.putInt(mContext.getString(R.string.perf_level_opened) + String.valueOf(category), level);
        mEditor.commit();
    }
    
    public int getOpenLevelByCategory(int category) {
        return mSharedPreferences.getInt(mContext.getString(R.string.perf_level_opened) + String.valueOf(category), 1);
    }
    
    public void setEndlessOpenLevelWithCategory(int level, int category) {
        mEditor.putInt(mContext.getString(R.string.perf_level_endless) + String.valueOf(category)
                            , level);
        mEditor.commit();
    }
    
    public int getEndlessOpenLevelWithCategory(int category) {
        return mSharedPreferences.getInt(mContext.getString(R.string.perf_level_opened) + String.valueOf(category)
                            , 1);
    }
    
    public int getLastOpenTime() {
        return mSharedPreferences.getInt(mContext.getString(R.string.perf_last_open_day), 0);
    }
    
    public void setLastOpenTime(int day) {
        mEditor.putInt(mContext.getString(R.string.perf_last_open_day), day);
        mEditor.commit();
    }
    
    public void init(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSharedPreferences.edit();
    }
    
    private SettingManager() {
    }
}
