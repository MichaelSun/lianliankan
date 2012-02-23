package com.tinygame.lianliankan.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	private static ToastUtil mInstance = null;
	private Toast mToast = null;
	
	private ToastUtil(Context context) {
		mToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_LONG);
	}
	
	public static ToastUtil getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new ToastUtil(context);
		}
		
		return mInstance;
	}
	
	public void showToast(int resId) {
	    mToast.cancel();
		mToast.setText(resId);
		mToast.show();
	}
	
	public void showToast(CharSequence str) {
	    mToast.cancel();
		mToast.setText(str);
		mToast.show();
	}
}
