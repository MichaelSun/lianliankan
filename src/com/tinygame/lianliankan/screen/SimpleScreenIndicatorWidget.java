/*
 * Copyright 2011 tfdroid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tinygame.lianliankan.screen;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.tinygame.lianliankan.R;
import com.tinygame.lianliankan.utils.AssetsImageLoader;
import com.tinygame.lianliankan.utils.Utils;

public class SimpleScreenIndicatorWidget extends LinearLayout implements ScrollScreen.ScreenIndicator {
	
    private ArrayList<Drawable> mIndicatorImageUnSelected;;
    private ArrayList<Drawable> mIndicatorImageSelected;;
    private float mDensity;
    private int mWidth;
    private ScrollScreen mScrollScreen;
//    private GradientDrawable mIndicatorBg;
    
    private static final int NO_SELECTOR_PADDING = 10;
    private static final int SELECTOR_PADDING = 1;
    
	public SimpleScreenIndicatorWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mDensity = context.getResources().getDisplayMetrics().density;
		mWidth = (int) (48 * mDensity);
//		mIndicatorBg = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
//                        new int[] { 0xfffffce7, 0x00000000 });
//		mIndicatorBg.setShape(GradientDrawable.RECTANGLE);
//		mIndicatorBg.setGradientType(GradientDrawable.RADIAL_GRADIENT);
//		mIndicatorBg.setGradientRadius((float)(Math.sqrt(2) * 60));
//		mIndicatorBg.setCornerRadii(new float[] { 6, 6, 6, 6,
//                6, 6, 6, 6 });
//		
		mIndicatorImageUnSelected = new ArrayList<Drawable>();
		mIndicatorImageUnSelected.add(getResources().getDrawable(R.drawable.shengdan));
		mIndicatorImageUnSelected.add(getResources().getDrawable(R.drawable.biaoqing));
		mIndicatorImageUnSelected.add(getResources().getDrawable(R.drawable.dongwu));
		mIndicatorImageUnSelected.add(getResources().getDrawable(R.drawable.katong));
		mIndicatorImageUnSelected.add(getResources().getDrawable(R.drawable.majiang));
		
		mIndicatorImageSelected = new ArrayList<Drawable>();
		mIndicatorImageSelected.add(getResources().getDrawable(R.drawable.shengdan_p));
		mIndicatorImageSelected.add(getResources().getDrawable(R.drawable.biaoqing_p));
		mIndicatorImageSelected.add(getResources().getDrawable(R.drawable.dongwu_p));
		mIndicatorImageSelected.add(getResources().getDrawable(R.drawable.katong_p));
		mIndicatorImageSelected.add(getResources().getDrawable(R.drawable.majiang_p));
    }

	@Override
	public void addIndicator(ScrollScreen scrollScreen) {
	    mScrollScreen = scrollScreen;
	    
		ImageView slidePot = new ImageView(getContext());
		slidePot.setPadding(NO_SELECTOR_PADDING, NO_SELECTOR_PADDING, NO_SELECTOR_PADDING, NO_SELECTOR_PADDING);
		
		slidePot.setLayoutParams(new LinearLayout.LayoutParams(mWidth, mWidth));
		slidePot.setScaleType(ScaleType.FIT_XY);
		addView(slidePot);
		
//        if (slidePot != null) {
//            slidePot.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int childCount = getChildCount();
//                    for (int i = 0; i < childCount; ++i) {
//                        View view = getChildAt(i);
//                        if (view == v && mScrollScreen != null) {
//                            mScrollScreen.setToScreen(i);
//                            break;
//                        }
//                    }
//                }
//            });
//        }
	}
	
	@Override
	public void setCurrentScreen(int index) {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			ImageView point = (ImageView) getChildAt(i);
			
            if (i < mIndicatorImageUnSelected.size()) {
                Drawable drawable = mIndicatorImageUnSelected.get(i);
                point.setImageDrawable(drawable);
            }
			
			if (i == index) {
			    point.setPadding(SELECTOR_PADDING, SELECTOR_PADDING, SELECTOR_PADDING, SELECTOR_PADDING);
			    Drawable drawable = mIndicatorImageSelected.get(i);
			    point.setImageDrawable(drawable);
			} else {
			    point.setPadding(NO_SELECTOR_PADDING, NO_SELECTOR_PADDING
			            , NO_SELECTOR_PADDING, NO_SELECTOR_PADDING);
                Drawable drawable = mIndicatorImageUnSelected.get(i);
                point.setImageDrawable(drawable);
			}
		}
	}
}
