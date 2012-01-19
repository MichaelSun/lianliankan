package com.tinygame.lianliankan;

import java.util.ArrayList;

public class Categary_diff_selector {
    
    private static class Diff {
        public String diff;
        public int time;
        
        public Diff(String diff, int time) {
            this.diff = diff;
            this.time = time;
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
                    return mCateSmallArryList.get(mCurrentCategary);
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
    
    public String updateDiff() {
        mCurrentDiff++;
        return this.getCurrentDiff();
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
        
        mDiffArryList.add(new Diff("5x6", 30));
        mDiffArryList.add(new Diff("5x6", 30));
        mDiffArryList.add(new Diff("5x8", 34));
        mDiffArryList.add(new Diff("5x10", 34));
        
        mDiffArryList.add(new Diff("6x8", 34));
        mDiffArryList.add(new Diff("6x10", 38));
        mDiffArryList.add(new Diff("6x11", 45));
        mDiffArryList.add(new Diff("6x12", 48));
        
        mDiffArryList.add(new Diff("8x10", 55));
        mDiffArryList.add(new Diff("8x12", 57));
        mDiffArryList.add(new Diff("8x14", 60));
        mDiffArryList.add(new Diff("8x15", 60));
        
        mDiffArryList.add(new Diff("10x10", 64));
        mDiffArryList.add(new Diff("10x12", 68));
        mDiffArryList.add(new Diff("10x14", 70));
        mDiffArryList.add(new Diff("10x16", 80));
    }
}
