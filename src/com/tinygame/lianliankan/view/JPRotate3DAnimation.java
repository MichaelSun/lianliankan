package com.tinygame.lianliankan.view;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.tinygame.lianliankan.LinkLinkApplication;

public class JPRotate3DAnimation extends Animation {
	private static final String	TAG	= "JPRotate3DAnimation";

	private float				mFromDegree;
	private float				mToDegree;
	private float				mCenterX;
	private float				mCenterY;
	private float				mLeft;
	private float				mTop;
	private Camera				mCamera;

	public JPRotate3DAnimation(float fromDegree, float toDegree, float left, float top, float centerX, float centerY) {
		this.mFromDegree = fromDegree;
		this.mToDegree = toDegree;
		this.mLeft = left;
		this.mTop = top;
		this.mCenterX = centerX;
		this.mCenterY = centerY;

	}

	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		final float FromDegree = mFromDegree;
		float degrees = FromDegree + (mToDegree - mFromDegree) * interpolatedTime;
		final float centerX = mCenterX;
		final float centerY = mCenterY;
		final Matrix matrix = t.getMatrix();

		float[] values = new float[9];
		matrix.getValues(values);
		Log.d(TAG, "MATRIX  scale:" + values[0] + ", degrees:" + degrees + ", centerX:" + centerX + ", centerY:"
				+ centerY);

		if (degrees <= -76.0f) {
			degrees = -90.0f;
			mCamera.save();
			mCamera.rotateY(degrees);
			mCamera.getMatrix(matrix);
			mCamera.restore();
		} else if (degrees >= 76.0f) {
			degrees = 90.0f;
			mCamera.save();
			mCamera.rotateY(degrees);
			mCamera.getMatrix(matrix);
			mCamera.restore();
		} else {
			mCamera.save();

//			mCamera.translate(0, 0, centerX);
//			mCamera.rotateY(degrees);
//			mCamera.translate(0, 0, -centerX);
			
			// 逐渐拉远
			float deltaZ = 120 * LinkLinkApplication.SCREEN_DENSITY * Math.abs(degrees) / 90;
			mCamera.translate(0, 0, deltaZ);
			mCamera.rotateY(degrees);
			mCamera.translate(0, 0, -deltaZ);
			
			mCamera.getMatrix(matrix);
			mCamera.restore();
		}

		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
	}
}
