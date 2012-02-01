package com.tinygame.lianliankan;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class LevelActivity extends Activity {

    private class EachLevelInfo {
        int startPoint;
        boolean hasFinished;
    }

    private GridView mGridView;
    private InfoAdapter mAdapter;
    private int mTotalLevel;
    private ArrayList<EachLevelInfo> mLevelPointList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.setContentView(R.layout.level_list);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        
        SettingManager.getInstance().init(this);
        initView();
    }
    
    private void initView() {
        mTotalLevel = Categary_diff_selector.getInstance().getDiffLevels().size()
                * Categary_diff_selector.getInstance().getAllCategory();
        int curLevel = SettingManager.getInstance().getOpenLevel();

        mLevelPointList = new ArrayList<EachLevelInfo>();
        // init each level point
        for (int i = 0; i < mTotalLevel; ++i) {
            EachLevelInfo info = new EachLevelInfo();
            if (i < curLevel) {
                info.hasFinished = true;
            }
            info.startPoint = 1;
            mLevelPointList.add(info);
        }

        mGridView = (GridView) findViewById(R.id.level_grid);
        mAdapter = new InfoAdapter(this, R.layout.level_list_item, mLevelPointList);
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EachLevelInfo info = mLevelPointList.get(position);
                if (!info.hasFinished) {
                    return;
                }
                
                Categary_diff_selector.getInstance().updateLevelInfo(position + 1);

                Intent mainviewIntent = new Intent();
                mainviewIntent.setClass(getApplicationContext(), LinkLink.class);
                startActivity(mainviewIntent);
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
                
                View egg = ret.findViewById(R.id.egg_point);
                egg.setVisibility(View.VISIBLE);
                View eggTwo = ret.findViewById(R.id.egg_point_two);
                eggTwo.setVisibility(View.VISIBLE);
                View eggThree = ret.findViewById(R.id.egg_point_three);
                eggThree.setVisibility(View.VISIBLE);
            } else {
                View lock = ret.findViewById(R.id.lock_icon);
                lock.setVisibility(View.VISIBLE);

                View egg = ret.findViewById(R.id.egg_point);
                egg.setVisibility(View.GONE);
                View eggTwo = ret.findViewById(R.id.egg_point_two);
                eggTwo.setVisibility(View.GONE);
                View eggThree = ret.findViewById(R.id.egg_point_three);
                eggThree.setVisibility(View.GONE);
            }
            
            TextView levelTV = (TextView) ret.findViewById(R.id.level_icon);
            levelTV.setText(String.valueOf(position + 1));

            return ret;
        }
    }
}
