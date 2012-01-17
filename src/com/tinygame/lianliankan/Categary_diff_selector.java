package com.tinygame.lianliankan;

import com.tinygame.lianliankan.config.Config;

public class Categary_diff_selector {

    private static Categary_diff_selector gCategary_diff_selector = new Categary_diff_selector();

    private int mCurrentCategary = 1;
    private int mCurrentDiff;
    
    public static Categary_diff_selector getInstance() {
        return gCategary_diff_selector;
    }
    
    public String getCurrentCategary() {
        switch (mCurrentCategary) {
        case 1:
            return "image/1_40.png";
        case 2:
            return "image/2_40.png";
        case 3:
            return "image/3_40.png";
        case 4:
            return "image/4_40.png";
        case 5:
            return "image/5_40.png";
            default:
                return null;
        }
    }
    
    public String updateCategory() {
        mCurrentCategary++;
        return this.getCurrentCategary();
    }
    
    public String getCurrentDiff() {
        switch (mCurrentDiff) {
        case 0:
            return Config.DIFF_ONE;
        case 1:
            return Config.DIFF_TWO;
        case 2:
            return Config.DIFF_THREE;
        case 3:
            return Config.DIFF_FOUR;
        case 4:
            return Config.DIFF_FIVE;
            default:
                return null;
        }
    }
    
    public void restDiff() {
        mCurrentDiff = 0;
    }
    
    public String updateDiff() {
        mCurrentDiff++;
        return this.getCurrentDiff();
    }
}
