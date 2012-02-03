package com.tinygame.lianliankan;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.tinygame.lianliankan.screen.ScrollScreen;
import com.tinygame.lianliankan.screen.ScrollScreen.OnScreenChangeListener;
import com.tinygame.lianliankan.screen.ScrollScreen.ScreenContentFactory;
import com.tinygame.lianliankan.screen.ScrollScreenActivity;
import com.tinygame.lianliankan.utils.Utils;

public class LevelActivity extends ScrollScreenActivity
                implements ScreenContentFactory, OnScreenChangeListener {

    private class EachLevelInfo {
        int startPoint;
        boolean hasFinished;
    }

    private static final int SCREEN_COUNT = 5;
    
    private ScrollScreen mScrollScreen;
    private LayoutInflater mInflater;
    private int mCurCategory;
    private GridView[] mGridViewList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SettingManager.getInstance().init(getApplicationContext());
//        this.setContentView(R.layout.level_list);
        
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mGridViewList = new GridView[SCREEN_COUNT];
        
        mScrollScreen = getScrollScreen();
        mScrollScreen.addScreen(SCREEN_COUNT, this);
        mScrollScreen.setOnScreenChangedListener(this);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        SettingManager.getInstance().init(getApplicationContext());        
        
        if (mCurCategory < mGridViewList.length) {
            GridView gview = mGridViewList[mCurCategory];
            if (gview != null) {
                ArrayList<EachLevelInfo> ret = resetListData();
                InfoAdapter adapter = new InfoAdapter(this, R.layout.level_list_item, ret);
                gview.setAdapter(adapter);
            }
        }
    }
    
    @Override
    public void onScreenChanged(int index) {
        mCurCategory = index;
    }
    
    @Override
    public View createScreenContent(int index) {
        View ret = mInflater.inflate(R.layout.level_list, null);
        initView(ret, index);
        
        return ret;
    }
    
    private ArrayList<EachLevelInfo> resetListData() {
        int level = Categary_diff_selector.getInstance().getDiffLevels().size();
        int curLevel = SettingManager.getInstance().getOpenLevelByCategory(mCurCategory);
        ArrayList<EachLevelInfo> levelPointList = new ArrayList<EachLevelInfo>();
        // init each level point
        for (int i = 0; i < level; ++i) {
            EachLevelInfo info = new EachLevelInfo();
            if (i < curLevel) {
                info.hasFinished = true;
            }
            info.startPoint = 1;
            levelPointList.add(info);
        }
        
        return levelPointList;
    }
    
    private void initView(View view, int category) {
        int level = Categary_diff_selector.getInstance().getDiffLevels().size();
        int curLevel = SettingManager.getInstance().getOpenLevelByCategory(category);

        ArrayList<EachLevelInfo> levelPointList = new ArrayList<EachLevelInfo>();
        // init each level point
        for (int i = 0; i < level; ++i) {
            EachLevelInfo info = new EachLevelInfo();
            if (i < curLevel) {
                info.hasFinished = true;
            }
            info.startPoint = 1;
            levelPointList.add(info);
        }

        GridView gridView = (GridView) view.findViewById(R.id.level_grid);
        InfoAdapter adapter = new InfoAdapter(this, R.layout.level_list_item, levelPointList);
        gridView.setAdapter(adapter);
        
        mGridViewList[category] = gridView;
        
        Drawable egg = this.getResources().getDrawable(R.drawable.egg);
        Drawable bg = Utils.getPressDrawable(this, ((BitmapDrawable) egg).getBitmap());
        if (gridView != null && bg != null) {
            gridView.setSelector(bg);
        }
        
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view == null) {
                    return;
                }
                
                View lock = view.findViewById(R.id.lock_icon);
                if (lock.getVisibility() == View.GONE) {
                
                    Categary_diff_selector.getInstance().updateLevelInfo(position + 1, mCurCategory);
                    Intent mainviewIntent = new Intent();
                    mainviewIntent.setClass(getApplicationContext(), LinkLink.class);
                    startActivity(mainviewIntent);
                }
            }
        });
    }

    class InfoAdapter extends ArrayAdapter<EachLevelInfo> {
        private int mResourceID;
        private Context mContext;
        private LayoutInflater mInflater;

        InfoAdapter(Context context, int resourceId, ArrayList<EachLevelInfo> data) {
            super(context, resourceId, data);
            mResourceID = resourceId;
            mContext = context;
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View ret = convertView;
            if (ret == null) {
                ret = mInflater.inflate(mResourceID, null);
            }

            EachLevelInfo info = this.getItem(position);
            if (info.hasFinished) {
                View lock = ret.findViewById(R.id.lock_icon);
                lock.setVisibility(View.GONE);
                
//                View egg = ret.findViewById(R.id.egg_point);
//                egg.setVisibility(View.VISIBLE);
//                View eggTwo = ret.findViewById(R.id.egg_point_two);
//                eggTwo.setVisibility(View.VISIBLE);
//                View eggThree = ret.findViewById(R.id.egg_point_three);
//                eggThree.setVisibility(View.VISIBLE);
            } else {
                View lock = ret.findViewById(R.id.lock_icon);
                lock.setVisibility(View.VISIBLE);

//                View egg = ret.findViewById(R.id.egg_point);
//                egg.setVisibility(View.GONE);
//                View eggTwo = ret.findViewById(R.id.egg_point_two);
//                eggTwo.setVisibility(View.GONE);
//                View eggThree = ret.findViewById(R.id.egg_point_three);
//                eggThree.setVisibility(View.GONE);
            }
            
            TextView levelTV = (TextView) ret.findViewById(R.id.level_icon);
            levelTV.setText(String.valueOf(position + 1));

            return ret;
        }
    }
}
