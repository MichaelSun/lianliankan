package com.tinygame.lianliankan;

import java.util.ArrayList;

import android.util.Log;

import com.tinygame.lianliankan.Categary_diff_selector.Diff;
import com.tinygame.lianliankan.config.Config;

public class Endless_Category_Diff_Selector {
    private static final String TAG = "Categary_diff_selector";
    
    public static class Diff {
        public String diff;
        public int time;
        public int hint;
        public int rerange;
        public int level;
        public int alignMode;
        
        public Diff(String diff, int time, int hint, int rerange, int alignMode) {
            this.diff = diff;
            this.time = time;
            this.hint = hint;
            this.rerange = rerange;
            this.alignMode = alignMode;
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
    private static Endless_Category_Diff_Selector gCategary_diff_selector = new Endless_Category_Diff_Selector();

    private int mCurrentCategary;
    private int mCurrentDiff;
    
    public static Endless_Category_Diff_Selector getInstance() {
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
    
    public int getCurretntDiffAlignMode() {
        if (mCurrentDiff < mDiffArryList.size()) {
            return mDiffArryList.get(mCurrentDiff).alignMode;
        }
        
        return -1;
    }
    
    public String getCurrentDiff() {
        if (mCurrentDiff < mDiffArryList.size()) {
            return mDiffArryList.get(mCurrentDiff).diff;
        } else {
            mCurrentDiff--;
            return mDiffArryList.get(mCurrentDiff).diff;
        }
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
    
    public int getCurrentCategoryLevel() {
        return mCurrentCategary;
    }
    
    public int getCurrentDiffLevel() {
        return mCurrentDiff + 1;
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
        return mCateSmallArryList.size();
    }
    
    public void saveCurretInfo() {
        SettingManager.getInstance().setEndlessLastCategory(mCurrentCategary);
        SettingManager.getInstance().setEndlessLastDiff(mCurrentDiff);
    }
    
    public void clearInfo() {
        mCurrentCategary = 0;
        mCurrentDiff = 0;
        SettingManager.getInstance().setEndlessLastCategory(mCurrentCategary);
        SettingManager.getInstance().setEndlessLastDiff(mCurrentDiff);
        gCategary_diff_selector = null;
        gCategary_diff_selector = new Endless_Category_Diff_Selector();
    }
    
    private Endless_Category_Diff_Selector() {
        mCurrentCategary = SettingManager.getInstance().getEndlessLastCategory();
        mCurrentDiff = SettingManager.getInstance().getEndlessLastDiff();
        
        //config the image icon for draw
        mCateSmallArryList.add("level/1_85.png");
        
        mCateSmallTwoArryList.add("level/1_85.png");
        
        mCateMeduimArryList.add("level/1_85.png");
        
        mCateLargeArryList.add("level/1_85.png");
        
        
        //set the level diffcult
        mDiffArryList.add(new Diff("6x6", 15, 1, 1, -1));
        mDiffArryList.add(new Diff("6x8", 18, 1, 1, -1));
        mDiffArryList.add(new Diff("6x8", 18, 1, 1, -1));
        
        mDiffArryList.add(new Diff("6x6", 17, 1, 1, Config.ALIGN_LEFT));
        mDiffArryList.add(new Diff("6x8", 22, 1, 1, Config.ALIGN_RIGHT));
        mDiffArryList.add(new Diff("6x8", 22, 1, 1, Config.ALIGN_TOP));
        mDiffArryList.add(new Diff("6x8", 22, 1, 1, Config.ALIGN_BOTTOM));
        
        mDiffArryList.add(new Diff("6x9", 18, 2, 1, Config.ALIGN_RIGHT));
        mDiffArryList.add(new Diff("6x10", 19, 2, 1, Config.ALIGN_RIGHT));
        mDiffArryList.add(new Diff("7x8", 20, 2, 1, Config.ALIGN_RIGHT));
        mDiffArryList.add(new Diff("7x10", 22, 2, 1, Config.ALIGN_RIGHT));
        mDiffArryList.add(new Diff("8x8", 20, 3, 1, Config.ALIGN_RIGHT));
        
        mDiffArryList.add(new Diff("8x9", 30, 4, 2, Config.ALIGN_LEFT));
        mDiffArryList.add(new Diff("8x10", 34, 4, 2, Config.ALIGN_LEFT));
        mDiffArryList.add(new Diff("8x11", 36, 4, 2, Config.ALIGN_LEFT));
        mDiffArryList.add(new Diff("8x12", 36, 4, 2, Config.ALIGN_LEFT));
        mDiffArryList.add(new Diff("8x12", 38, 4, 2, Config.ALIGN_LEFT));
        
        mDiffArryList.add(new Diff("8x10", 38, 4, 2, Config.ALIGN_LEFT));
        mDiffArryList.add(new Diff("8x11", 40, 4, 2, Config.ALIGN_RIGHT));
        mDiffArryList.add(new Diff("8x12", 42, 4, 2, Config.ALIGN_TOP));
        mDiffArryList.add(new Diff("8x12", 44, 4, 2, Config.ALIGN_BOTTOM));
        
        mDiffArryList.add(new Diff("9x8", 45, 4, 2, Config.ALIGN_BOTTOM));
        mDiffArryList.add(new Diff("9x10", 50, 5, 3, Config.ALIGN_BOTTOM));
        mDiffArryList.add(new Diff("9x12", 55, 5, 3, Config.ALIGN_BOTTOM));
        
        mDiffArryList.add(new Diff("10x10", 65, 5, 3, Config.ALIGN_TOP));
        mDiffArryList.add(new Diff("10x11", 75, 5, 3, Config.ALIGN_TOP));
        mDiffArryList.add(new Diff("10x12", 80, 5, 3, Config.ALIGN_TOP));
        mDiffArryList.add(new Diff("10x14", 90, 5, 3, Config.ALIGN_TOP));
        
        mDiffArryList.add(new Diff("10x10", 65, 5, 3, Config.ALIGN_RIGHT));
        mDiffArryList.add(new Diff("10x11", 75, 5, 3, Config.ALIGN_RIGHT));
        mDiffArryList.add(new Diff("10x12", 80, 5, 3, Config.ALIGN_RIGHT));
        mDiffArryList.add(new Diff("10x14", 90, 5, 3, Config.ALIGN_RIGHT));
        
        mDiffArryList.add(new Diff("10x10", 73, 5, 3, Config.ALIGN_LEFT));
        mDiffArryList.add(new Diff("10x11", 83, 5, 3, Config.ALIGN_RIGHT));
        mDiffArryList.add(new Diff("10x12", 90, 5, 3, Config.ALIGN_TOP));
        mDiffArryList.add(new Diff("10x14", 100, 5, 3, Config.ALIGN_BOTTOM));
    }
    
    private void LOGD(String msg) {
        if (com.tinygame.lianliankan.config.Config.DEBUG) {
            Log.d(TAG, msg);
        }
    }
}
