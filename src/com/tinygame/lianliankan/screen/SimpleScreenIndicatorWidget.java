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
	
    private ArrayList<Drawable> mIndicatorImage;
    private float mDensity;
    private int mWidth;
    private GradientDrawable mIndicatorBg;
    
    private static final int NO_SELECTOR_PADDING = 10;
    private static final int SELECTOR_PADDING = 1;
    
	public SimpleScreenIndicatorWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mDensity = context.getResources().getDisplayMetrics().density;
		mWidth = (int) (48 * mDensity);
		mIndicatorBg = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                        new int[] { 0xfffffce7, 0x00000000 });
		mIndicatorBg.setShape(GradientDrawable.RECTANGLE);
		mIndicatorBg.setGradientType(GradientDrawable.RADIAL_GRADIENT);
		mIndicatorBg.setGradientRadius((float)(Math.sqrt(2) * 60));
		mIndicatorBg.setCornerRadii(new float[] { 6, 6, 6, 6,
                6, 6, 6, 6 });
		
		mIndicatorImage = new ArrayList<Drawable>();
		
        Bitmap orgBt = AssetsImageLoader.loadBitmapFromAsset(context, "image/shengdan");
        mIndicatorImage.add(new BitmapDrawable(Utils.getRoundedCornerBitmap(orgBt, 10.0f, 1, 40)));
        orgBt = AssetsImageLoader.loadBitmapFromAsset(context, "image/biaoqing");
        mIndicatorImage.add(new BitmapDrawable(Utils.getRoundedCornerBitmap(orgBt, 10.0f, 1, 40)));
        orgBt = AssetsImageLoader.loadBitmapFromAsset(context, "image/dongwu");
        mIndicatorImage.add(new BitmapDrawable(Utils.getRoundedCornerBitmap(orgBt, 10.0f, 1, 40)));
        orgBt = AssetsImageLoader.loadBitmapFromAsset(context, "image/katong");
        mIndicatorImage.add(new BitmapDrawable(Utils.getRoundedCornerBitmap(orgBt, 10.0f, 1, 40)));
        orgBt = AssetsImageLoader.loadBitmapFromAsset(context, "image/majiang");
        mIndicatorImage.add(new BitmapDrawable(Utils.getRoundedCornerBitmap(orgBt, 10.0f, 1, 40)));
    }

	@Override
	public void addIndicator() {
		ImageView slidePot = new ImageView(getContext());
		slidePot.setPadding(NO_SELECTOR_PADDING, NO_SELECTOR_PADDING, NO_SELECTOR_PADDING, NO_SELECTOR_PADDING);
		
		slidePot.setLayoutParams(new LinearLayout.LayoutParams(mWidth, mWidth));
		slidePot.setScaleType(ScaleType.FIT_XY);
		addView(slidePot);
	}
	
	@Override
	public void setCurrentScreen(int index) {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			ImageView point = (ImageView) getChildAt(i);
			
            if (i < mIndicatorImage.size()) {
                Drawable drawable = mIndicatorImage.get(i);
                point.setImageDrawable(drawable);
            }
			
			if (i == index) {
			    point.setPadding(SELECTOR_PADDING, SELECTOR_PADDING, SELECTOR_PADDING, SELECTOR_PADDING);
			    point.setBackgroundDrawable(mIndicatorBg);
			} else {
			    point.setPadding(NO_SELECTOR_PADDING, NO_SELECTOR_PADDING
			            , NO_SELECTOR_PADDING, NO_SELECTOR_PADDING);
			    point.setBackgroundDrawable(null);
			}
			
//			if (i == index) {
//				if (index < mIndicatorImage.size()) {
//				    Drawable drawable = mIndicatorImage.get(index);
////				    point.setBackgroundDrawable(R.drawable.egg);
//				    point.setImageDrawable(drawable);
//				} else {
//				    point.setBackgroundResource(R.drawable.screen_pot_selected);				    
//				}
//			} else {
////				point.setBackgroundResource(R.drawable.screen_pot);
//				point.setImageResource(R.drawable.screen_pot);
//			}
		}
	}
}
