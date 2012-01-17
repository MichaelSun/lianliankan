package com.tinygame.lianliankan.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

import com.tinygame.lianliankan.config.Config;

public class ImageSplitUtils {

    private static final String TAG = "ImageSplitUtils";
    
    public static class BitmapSiplited {
        public Bitmap bitmap;
        public int id;
    }
    
    private static ImageSplitUtils gImageSplitUtils = new ImageSplitUtils();
    private Context mContext;
    private ArrayList<BitmapSiplited> mCurrentRes;
    
    public static ImageSplitUtils getInstance() {
        return gImageSplitUtils;
    }

    public void init(Context context) {
        mContext = context;
    }
    
    public void clearCurrentRes() {
        if (mCurrentRes != null) {
            for (BitmapSiplited bt : mCurrentRes) {
                if (bt != null && !bt.bitmap.isRecycled()) {
                    bt.bitmap.recycle();
                }
            }
            mCurrentRes.clear();
        }
    }
    
    public ArrayList<BitmapSiplited> splitBitmapInAssests(String path) {
        mCurrentRes = new ArrayList<BitmapSiplited>();
        Bitmap src = loadBitmapFromAsset(mContext, path);
        if (path.endsWith(".png")) {
            path = path.substring(0, path.length() - 4);
        }
        int pos = path.lastIndexOf("_");
        if (pos != -1) {
            int icon_width = Integer.valueOf(path.substring(pos + 1));
            int src_width = src.getWidth();
            int src_height = src.getHeight();
            
            BitmapSiplited bts = null;
            int x_length  = src_width / icon_width;
            int y_length  = src_height / icon_width;
            int index = 0;
            for (int x = 0; x < x_length; ++x) {
                for (int y = 0; y < y_length; ++y) {
                    try {
                        bts = new BitmapSiplited();
                        bts.bitmap = Bitmap.createBitmap(src
                                                , x * icon_width 
                                                , y * icon_width
                                                , icon_width
                                                , icon_width);
                        bts.id = index;
                        index++;
                        mCurrentRes.add(bts);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return mCurrentRes;
    }
    
    private Bitmap loadBitmapFromAsset (Context context, String resName) {
        if (resName == null) {
            throw new RuntimeException("resName MUST not be NULL");
        }
        
        if (!resName.endsWith(".png")) {
            resName = resName + ".png";
        }
            
        InputStream is = null;
        try {
            is = context.getAssets().open(resName);
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inDensity = 160;
            Rect rect = new Rect();
            Bitmap bitmap = BitmapFactory.decodeStream(is, rect, opt);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    private void LOGD(String msg) {
        if (Config.DEBUG) {
            Log.d(TAG, msg);
        }
    }
}
