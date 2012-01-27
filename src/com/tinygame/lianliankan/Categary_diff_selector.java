package com.tinygame.lianliankan;

import java.util.ArrayList;

import android.util.Log;

public class Categary_diff_selector {
    private static final String TAG = "Categary_diff_selector";
    
    public static class Diff {
        public String diff;
        public int time;
        public int hint;
        public int rerange;
        public int level;
        
        public Diff(String diff, int time, int hint, int rerange) {
            this.diff = diff;
            this.time = time;
            this.hint = hint;
            this.rerange = rerange;
        }
    }
    
    private ArrayList<Diff> mDiffArryList = new ArrayList<Diff>();
    private ArrayList<String> mCateSmallArryList = new ArrayList<String>();
    private ArrayList<String> mCateSmallTwoArryList = new ArrayList<String>();
    private ArrayList<String> mCateMeduimArryList = new ArrayList<String>();
    private ArrayList<String> mCateLargeArryList = new ArrayList<String>();
    
    private static final String LITTLE_WIDTH = "5";
    private static final String MEDUIM_WIDTH = "6";
    private static final String SMALL_WIDTH = "8";
    private static Categary_diff_selector gCategary_diff_selector = new Categary_diff_selector();

    private int mCurrentCategary;
    private int mCurrentDiff;
    
    public static Categary_diff_selector getInstance() {
        return gCategary_diff_selector;
    }
    
    public String getCurrentCategary() {
        if (mCurrentCategary < mCateSmallArryList.size()) {
            String diff = getCurrentDiff();
            if (diff != null) {
                if (diff.startsWith(LITTLE_WIDTH)) {
                    return mCateLargeArryList.get(mCurrentCategary);
                } else if (diff.startsWith(MEDUIM_WIDTH)) {
                    return mCateMeduimArryList.get(mCurrentCategary);
                } else if (diff.startsWith(SMALL_WIDTH)) {
                    return mCateSmallTwoArryList.get(mCurrentCategary);
                } else {
//                    return mCateSmallArryList.get(mCurrentCategary);
                    return mCateSmallTwoArryList.get(mCurrentCategary);
                }
            }
        }
        
        return null;
    }
    
    public String updateCategory() {
        mCurrentCategary++;
        mCurrentDiff = 0;
        return this.getCurrentCategary();
    }
    
    public String getCurrentDiff() {
        if (mCurrentDiff < mDiffArryList.size()) {
            return mDiffArryList.get(mCurrentDiff).diff;
        }
        
        return null;
    }
    
    public int getCurrentDiffArrange() {
        if (mCurrentDiff < mDiffArryList.size()) {
            return mDiffArryList.get(mCurrentDiff).rerange;
        }
        
        return 0;        
    }
    
    public int getCurrentDiffHint() {
        if (mCurrentDiff < mDiffArryList.size()) {
            return mDiffArryList.get(mCurrentDiff).hint;
        }
        
        return 0;        
    }
    
    public int getCurrentTime() {
        if (mCurrentDiff < mDiffArryList.size()) {
            return mDiffArryList.get(mCurrentDiff).time;
        }
        
        return -1;
    }
    
    public int getCurrentLevel() {
        if (getCurrentDiff() != null && getCurrentCategary() != null) {
            return 1 + mCurrentDiff + mDiffArryList.size() * mCurrentCategary;
        }
        
        return 0;
    }
    
    public void restDiff() {
        mCurrentDiff = 0;
    }
    
    public void resetCategory() {
        mCurrentCategary = 0;
    }
    
    public String updateDiff() {
        mCurrentDiff++;
        return this.getCurrentDiff();
    }
    
    public ArrayList<Diff> getDiffLevels() {
        return mDiffArryList;
    }
    
    public int getAllCategory() {
        return 5;
    }
    
    public void updateLevelInfo(int level) {
        int diffCount = mDiffArryList.size();
        int category = (level - 1) / diffCount;
        int diff = (level - 1) % diffCount;
        
        LOGD("[[updateLevelInfo]] level = " + level + " category = " + category
                + " diff = " + diff);
        
        mCurrentCategary = category;
        mCurrentDiff = diff;
    }
    
    public void saveCurretInfo() {
        SettingManager.getInstance().setLastCategory(mCurrentCategary);
        SettingManager.getInstance().setLastDiff(mCurrentDiff);
    }
    
    private Categary_diff_selector() {
        mCurrentCategary = SettingManager.getInstance().getLastCategory();
        mCurrentDiff = SettingManager.getInstance().getLastDiff();
        
        mCateSmallArryList.add("image/1_40.png");
        mCateSmallArryList.add("image/2_40.png");
        mCateSmallArryList.add("image/3_40.png");
        mCateSmallArryList.add("image/4_40.png");
        mCateSmallArryList.add("image/5_40.png");
        
        mCateSmallTwoArryList.add("image/1_48.png");
        mCateSmallTwoArryList.add("image/2_48.png");
        mCateSmallTwoArryList.add("image/3_48.png");
        mCateSmallTwoArryList.add("image/4_48.png");
        mCateSmallTwoArryList.add("image/5_48.png");
        
        mCateMeduimArryList.add("image/1_64.png");
        mCateMeduimArryList.add("image/2_64.png");
        mCateMeduimArryList.add("image/3_64.png");
        mCateMeduimArryList.add("image/4_64.png");
        mCateMeduimArryList.add("image/5_64.png");
        
        mCateLargeArryList.add("image/1_80.png");
        mCateLargeArryList.add("image/2_80.png");
        mCateLargeArryList.add("image/3_80.png");
        mCateLargeArryList.add("image/4_80.png");
        mCateLargeArryList.add("image/5_80.png");
        
        mDiffArryList.add(new Diff("5x6", 25, 1, 1));
        mDiffArryList.add(new Diff("5x6", 25, 1, 1));
        mDiffArryList.add(new Diff("5x8", 28, 1, 1));
        mDiffArryList.add(new Diff("5x8", 28, 1, 1));
        
        mDiffArryList.add(new Diff("6x6", 28, 2, 1));
        mDiffArryList.add(new Diff("6x7", 29, 2, 1));
        mDiffArryList.add(new Diff("6x8", 30, 2, 1));
        mDiffArryList.add(new Diff("6x9", 32, 2, 1));
        mDiffArryList.add(new Diff("6x10", 35, 3, 1));
        mDiffArryList.add(new Diff("6x11", 37, 4, 1));
        
        mDiffArryList.add(new Diff("8x7", 40, 4, 2));
        mDiffArryList.add(new Diff("8x8", 44, 4, 2));
        mDiffArryList.add(new Diff("8x19", 46, 4, 2));
        mDiffArryList.add(new Diff("8x10", 46, 4, 2));
        mDiffArryList.add(new Diff("8x11", 48, 4, 2));
        
        mDiffArryList.add(new Diff("9x8", 55, 4, 2));
        mDiffArryList.add(new Diff("9x10", 60, 5, 3));
        mDiffArryList.add(new Diff("9x12", 65, 5, 3));
        
        mDiffArryList.add(new Diff("10x10", 75, 5, 3));
        mDiffArryList.add(new Diff("10x11", 85, 5, 3));
        mDiffArryList.add(new Diff("10x12", 90, 5, 3));
        mDiffArryList.add(new Diff("10x14", 100, 5, 3));
    }
    
    private void LOGD(String msg) {
        if (com.tinygame.lianliankan.config.Config.DEBUG) {
            Log.d(TAG, msg);
        }
    }
}
