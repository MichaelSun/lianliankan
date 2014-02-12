package com.tinygame.lianliankan.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by michael on 14-2-12.
 */
public class PointsManager {

    private static PointsManager gPointsManager = new PointsManager();
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public static PointsManager getInstance() {
        return gPointsManager;
    }

    public void init(Context context) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditor = mSharedPreferences.edit();
    }

    public int queryPoints() {
        return mSharedPreferences.getInt("point", 0);
    }

    public void awardPoints(int point) {
        int curPoint = queryPoints();
        mEditor.putInt("point", curPoint + point).commit();
    }

    public void spendPoints(int point) {
        int curPoint = queryPoints();
        curPoint = curPoint - point;
        curPoint = curPoint > 0 ? curPoint : 0;
        mEditor.putInt("point", curPoint).commit();
    }

}
