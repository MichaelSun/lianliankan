package com.tinygame.lianliankan;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.tinygame.lianliankan.utils.ImageSplitUtils;
import com.tinygame.lianliankan.utils.Utils;
import com.tinygame.lianliankan.utils.ImageSplitUtils.BitmapSiplited;

public class ThemeManager {

    public static final int NO_IMAGE = -1;
    public int mCurrentImageCount;
    
    private static ThemeManager gThemeManager = new ThemeManager();
    private Drawable[] mResArray;
    private Context mContext;
    private HashMap<Integer, Bitmap> mNumberMap;
    private String mCurCategory;

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
   
    public Bitmap getTimeNumberBtByNumber(int number) {
        if (mNumberMap == null) {
            mNumberMap = new HashMap<Integer, Bitmap>();
            ArrayList<Bitmap> ret = ImageSplitUtils.getInstance().getTimeNumberBtList();
            for (int i = 0; i < 10; ++i) {
                mNumberMap.put(i, ret.get(i));
            }
        }
        
        return mNumberMap.get(number);
    }
    
    public void loadImageByCategary(String cate) {
        if (mCurCategory == null 
                || ((mCurCategory != null) 
                        && !mCurCategory.equals(cate))) {
            mCurCategory = cate;
            ImageSplitUtils.getInstance().clearCurrentRes();
            mResArray = null;
            ArrayList<BitmapSiplited> ret = ImageSplitUtils.getInstance().splitBitmapInAssests(mCurCategory);
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

}
