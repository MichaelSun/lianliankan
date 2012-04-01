package com.tinygame.lianliankan.utils;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.tinygame.lianliankan.utils.ImageSplitUtils.BitmapSiplited;


public class ThemeManager {

    public static final int NO_IMAGE = -1;
    public int mCurrentImageCount;
    
    private static ThemeManager gThemeManager = new ThemeManager();
    private Drawable[] mResArray;
    private Context mContext;
    private HashMap<Integer, Bitmap> mNumberMap;
    private String mCurCategory;
    private ArrayList<Bitmap> mBgList = new ArrayList<Bitmap>();
    private ArrayList<SoftReference<Bitmap>> mBoomCacheList;
    private ArrayList<Bitmap> mLevelBtList;
    private ArrayList<Bitmap> mTimeProgressBtList;
    private ArrayList<Bitmap> mContinueNumberBtList;

    public static ThemeManager getInstance() {
        if (gThemeManager == null) {
            gThemeManager = new ThemeManager(); 
        }
        
        return gThemeManager;
    }
    
    public void init(Context context) {
        mContext = context;
        ImageSplitUtils.getInstance().init(mContext);
    }
    
    public void unload() {
        if (mBoomCacheList != null) {
            for (int i = 0; i < mBoomCacheList.size(); ++i) {
                if (mBoomCacheList.get(i).get() != null &&
                        !mBoomCacheList.get(i).get().isRecycled()) {
                    mBoomCacheList.get(i).get().recycle();
                }
            }
            
            mBoomCacheList.clear();
            mBoomCacheList = null;
        }
        
        if (mBgList != null) {
            for (int i = 0; i < mBgList.size(); ++i) {
                if (mBgList.get(i) != null && !mBgList.get(i).isRecycled()) {
                    mBgList.get(i).recycle();
                }
            }
            
            mBgList.clear();
            mBgList = null;
        }
        
        if (mNumberMap != null) {
            for (Bitmap bt : mNumberMap.values()) {
                if (bt != null && !bt.isRecycled()) {
                    bt.recycle();
                }
            }
            
            mNumberMap.clear();
            mNumberMap = null;
        }
        
        if (mLevelBtList != null) {
            for (Bitmap bt : mLevelBtList) {
                if (bt != null && !bt.isRecycled()) {
                    bt.recycle();
                }
            }
            
            mLevelBtList.clear();
            mLevelBtList = null;
        }
        
        if (mTimeProgressBtList != null) {
            for (Bitmap bt : mTimeProgressBtList) {
                if (bt != null && !bt.isRecycled()) {
                    bt.recycle();
                }
            }
            
            mTimeProgressBtList.clear();
            mTimeProgressBtList = null;
        }
        
        if (mContinueNumberBtList != null) {
            for (Bitmap bt : mContinueNumberBtList) {
                if (bt != null && !bt.isRecycled()) {
                    bt.recycle();
                }
            }
            
            mContinueNumberBtList.clear();
            mContinueNumberBtList = null;
        }
    }
    
    public int getCurrentImageCount() {
        return mCurrentImageCount;
    }
    
    public int getCurrentImageWidth() {
        return ImageSplitUtils.getInstance().getCurrentImageWidth();
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
    
    public ArrayList<Bitmap> getContinueNumberList() {
        if (mContinueNumberBtList == null || (mContinueNumberBtList != null && mContinueNumberBtList.size() == 0)) {
            mContinueNumberBtList = ImageSplitUtils.getInstance().getContinueClickBtList();
        }
        
        return mContinueNumberBtList;
    }
    
    public ArrayList<Bitmap> getTimeProgressList() {
        if (mLevelBtList == null || (mLevelBtList != null && mLevelBtList.size() == 0)) {
            mLevelBtList = ImageSplitUtils.getInstance().getTimeProgressBtList();
        }
        
        return mLevelBtList;
    }
    
    public ArrayList<Bitmap> getLevelNumberList() {
        if (mTimeProgressBtList == null || (mTimeProgressBtList != null && mTimeProgressBtList.size() == 0)) {
            mTimeProgressBtList = ImageSplitUtils.getInstance().getLevelNumberBtList();
        }
        
        return mTimeProgressBtList;
    }
   
    public Bitmap getTimeNumberBtByNumber(int number) {
        if (mNumberMap == null || (mNumberMap != null && mNumberMap.size() == 0)) {
            mNumberMap = new HashMap<Integer, Bitmap>();
            ArrayList<Bitmap> ret = ImageSplitUtils.getInstance().getTimeNumberBtList();
            for (int i = 0; i < 10; ++i) {
                mNumberMap.put(i, ret.get(i));
            }
        }
        
        return mNumberMap.get(number);
    }
    
    public ArrayList<SoftReference<Bitmap>> getBoomList() {
        if (mBoomCacheList == null) {
            mBoomCacheList = new ArrayList<SoftReference<Bitmap>>();
            ArrayList<Bitmap> btList = ImageSplitUtils.getInstance().getBoomBtList();
            if (btList != null && btList.size() > 0) {
                for (Bitmap bt : btList) {
                    mBoomCacheList.add(new SoftReference<Bitmap>(bt));
                }
            }
        }
        
        return mBoomCacheList;
    }
    
    public ArrayList<Bitmap> getBgList() {
        if (mBgList == null || (mBgList != null && mBgList.size() == 0)) {
            mBgList = new ArrayList<Bitmap>();
            
            mBgList.add(AssetsImageLoader.loadBitmapFromAsset(mContext, "background/game_bg"));
            mBgList.add(AssetsImageLoader.loadBitmapFromAsset(mContext, "background/game_bg1"));
            mBgList.add(AssetsImageLoader.loadBitmapFromAsset(mContext, "background/game_bg2"));
            mBgList.add(AssetsImageLoader.loadBitmapFromAsset(mContext, "background/game_bg3"));
            mBgList.add(AssetsImageLoader.loadBitmapFromAsset(mContext, "background/game_bg4"));
        }
        
        return mBgList;
    }
    
    public void loadImageByCategary(String cate) {
        if (mCurCategory == null 
                || ((mCurCategory != null) 
                        && !mCurCategory.equals(cate))) {
            mCurCategory = cate;
            ImageSplitUtils.getInstance().clearCurrentRes();
            mResArray = null;
            ArrayList<BitmapSiplited> ret = ImageSplitUtils.getInstance()
                            .splitBitmapInAssests(mCurCategory);
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
