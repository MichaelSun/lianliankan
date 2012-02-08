package com.tinygame.lianliankan.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class DatabaseOperator {
    private static final String TAG = "DatabaseOperator";
    private static final boolean DEBUG = true;
    
    public static class LevelInfo {
        public int category;
        public int level;
        public int count;
        public int continueCount;
        public int max;
        public int cost;
        
        public LevelInfo() {
            category = -1;
            level = -1;
        }

        @Override
        public String toString() {
            return "LevelInfo [category=" + category + ", level=" + level + ", count=" + count + ", continueCount="
                    + continueCount + ", max=" + max + ", cost=" + cost + "]";
        }
        
    }
    
    private static DatabaseOperator gDatabaseOperator;
    private static Object mObj = new Object();
    
    private InternalDatabaseProxy mDBProxy;
    private boolean mInit;

    public static DatabaseOperator getInstance() {
        if (gDatabaseOperator == null) {
            synchronized (mObj) {
                if (gDatabaseOperator == null) {
                    gDatabaseOperator = new DatabaseOperator();
                }
            }
        }
        
        return gDatabaseOperator;
    }
    
    public void init(Context context) {
        if (!mInit) {
            mDBProxy = InternalDatabaseProxy.getDBInstance(context);
            mInit = true;
        }
    }
    
    public LevelInfo getLevelInfo(int category, int level) {
        LevelInfo ret = new LevelInfo();
        if (category >= 0 && level >= 0) {
            ret.category = category;
            ret.level = level;
            Cursor c = null;
            try {
                String selection = DataBaseConfig.INTEGRAL_TABLE_CATEGORY + " =? AND " + DataBaseConfig.INTEGRAL_TABLE_LEVEL
                                        + " =?";
                String[] selectionArgs = new String[] { String.valueOf(category), String.valueOf(level) };
                c = mDBProxy.query(DataBaseConfig.INTEGRAL_TABLE_NAME, selection, selectionArgs, null);
                
                LOGD("[[getLevelInfo]] selection = " + selection
                        + " selectionArgs = " + selectionArgs);
                
                if (c != null) {
                    LOGD("[[getLevelInfo]] cate = " + category + " level = " + level + " c != null >>>>>>>");
                    if (c.moveToFirst()) {
                        ret.count = Integer.valueOf(c.getString(c.getColumnIndex(DataBaseConfig.INTEGRAL_TABLE_COUNT)));
                        ret.continueCount = Integer.valueOf(c.getString(c.getColumnIndex(DataBaseConfig.INTEGRAL_TABLE_CONTINUE)));
                        ret.max = Integer.valueOf(c.getString(c.getColumnIndex(DataBaseConfig.INTEGRAL_TABLE_MAX_CONTINUE)));
                        ret.cost = Integer.valueOf(c.getString(c.getColumnIndex(DataBaseConfig.INTEGRAL_TABLE_COST)));
                        LOGD("[[getLevelInfo]] cate = " + category + " level = " + level + " c move to next success"
                                + " ret = " + ret.toString());
                    }
                }
                
                return ret;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
        
        return ret;
    }
    
    public boolean insertCategoryAndLevelIntergral(LevelInfo info) {
        if (info != null) {
            return insertCategoryAndLevelIntergral(info.category, info.level, info.count, info.continueCount, info.max, info.cost);
        }
        
        return false;
    }
    
    public boolean insertCategoryAndLevelIntergral(int category, int level, int count, int countinue, int max, int cost) {
        if (category >= 0 && level >= 0) {
            Cursor c = null;
            try {
                String selection = DataBaseConfig.INTEGRAL_TABLE_CATEGORY + " =? AND "
                                        + DataBaseConfig.INTEGRAL_TABLE_LEVEL + " =?";
                String[] selectionArgs = new String[]{ String.valueOf(category), String.valueOf(level) };
                
                c = mDBProxy.query(DataBaseConfig.INTEGRAL_TABLE_NAME, selection, selectionArgs, null);
                
                ContentValues values = new ContentValues();
                values.put(DataBaseConfig.INTEGRAL_TABLE_CATEGORY, String.valueOf(category));
                values.put(DataBaseConfig.INTEGRAL_TABLE_LEVEL, String.valueOf(level));
                if (count != -1) {
                    values.put(DataBaseConfig.INTEGRAL_TABLE_COUNT, String.valueOf(count));
                }
                if (countinue != -1) {
                    values.put(DataBaseConfig.INTEGRAL_TABLE_CONTINUE, String.valueOf(countinue));
                }
                if (max != -1) {
                    values.put(DataBaseConfig.INTEGRAL_TABLE_MAX_CONTINUE, String.valueOf(max));
                }
                if (cost != -1) {
                    values.put(DataBaseConfig.INTEGRAL_TABLE_COST, String.valueOf(cost));
                }
                if (c == null || (c != null && !c.moveToFirst())) {
                    LOGD("[[insertCategoryAndLevelIntergral]] c == null, value = " + values.toString());
                    mDBProxy.insert(DataBaseConfig.INTEGRAL_TABLE_NAME, values);
                } else {
                    LOGD("[[insertCategoryAndLevelIntergral]] c != null update, value = " + values.toString());
                    mDBProxy.update(DataBaseConfig.INTEGRAL_TABLE_NAME, values, selection, selectionArgs);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null) {
                    c.close();
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    
    private DatabaseOperator() {
    }
    
    private void LOGD(String text) {
        if (DEBUG) {
            Log.d(TAG, text);
        }
    }
}
