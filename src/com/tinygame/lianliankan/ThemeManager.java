package com.tinygame.lianliankan;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.tinygame.lianliankan.utils.ImageSplitUtils;
import com.tinygame.lianliankan.utils.ImageSplitUtils.BitmapSiplited;

public class ThemeManager {

    public static final int NO_IMAGE = -1;
    public int mCurrentImageCount;
    
    private static ThemeManager gThemeManager = new ThemeManager();
    private Drawable[] mResArray;
    private Context mContext;

    public static ThemeManager getInstance() {
        return gThemeManager;
    }
    
    public void init(Context context) {
        mContext = context;
    }
    
    public int getCurrentImageCount() {
        return mCurrentImageCount;
    }
    
    public Drawable getImage(int id) {
        if (id == NO_IMAGE) {
            return null;
        }
        
        if (id < mResArray.length) {
            return mResArray[id];
        }
        
        return null;
    }
    
    public void loadImageByCategary(String cate) {
        String imagePath = cate;
        
        ImageSplitUtils.getInstance().clearCurrentRes();
        mResArray = null;
        ArrayList<BitmapSiplited> ret = ImageSplitUtils.getInstance().splitBitmapInAssests(imagePath);
        if (ret.size() > 0) {
            if (mResArray == null) {
                mResArray = new Drawable[ret.size()];
            }
            for (BitmapSiplited bts : ret) {
                mResArray[bts.id] = new BitmapDrawable(bts.bitmap);
            }
            mCurrentImageCount = ret.size();
        }
    }

}
