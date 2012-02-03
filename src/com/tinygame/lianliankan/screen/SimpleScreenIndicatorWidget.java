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
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tinygame.lianliankan.R;
import com.tinygame.lianliankan.utils.AssetsImageLoader;
import com.tinygame.lianliankan.utils.Utils;

public class SimpleScreenIndicatorWidget extends LinearLayout implements ScrollScreen.ScreenIndicator {
	
    private ArrayList<Drawable> mIndicatorImage; 
    
	public SimpleScreenIndicatorWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
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
		slidePot.setPadding(5, 5, 5, 5);
		addView(slidePot);
	}
	
	@Override
	public void setCurrentScreen(int index) {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {
			ImageView point = (ImageView) getChildAt(i);
			
			if (i == index) {
				if (index < mIndicatorImage.size()) {
				    Drawable drawable = mIndicatorImage.get(index);
				    point.setBackgroundDrawable(drawable);
				} else {
				    point.setBackgroundResource(R.drawable.screen_pot_selected);				    
				}
			} else {
				point.setBackgroundResource(R.drawable.screen_pot);
			}
		}
	}
}
