package com.asunnotthesun.android.widget.coverflow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import it.sephiroth.android.library.widget.HListView;

public class CoverFlowListView extends HListView {

	private static final int AMBIENT_LIGHT = 55;
	private static final int DIFFUSE_LIGHT = 200;
	private static final float SPECULAR_LIGHT = 70;
	private static final float SHININESS = 200;
	private static final int MAX_INTENSITY = 0xFF;

	private final Camera mCamera = new Camera();
	private final Matrix mMatrix = new Matrix();	
	private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
	private final Paint mDebugPaint = new Paint();
	
	public CoverFlowListView(Context context) {
		super(context);
	}
	
	public CoverFlowListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public CoverFlowListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		
		Bitmap bitmap = getChildDrawingCache(child);
		final int top = child.getTop();
		final int left = child.getLeft();
		final int childCenterY = child.getHeight() / 2;
		final int childCenterX = child.getWidth() / 2;
		final int parentCenterX = getWidth() / 2;
		final int absChildCenterX = child.getLeft() + childCenterX;
		final int distanceX = parentCenterX - absChildCenterX;
		
		final int distanceFromLeftCenterX = (int) ((getWidth() * 0.25f - 0.125f) - absChildCenterX);
		final int distanceFromRightCenterX =(int) ((getWidth() * 0.75f + 0.125f) - absChildCenterX);
		
		final int r = getWidth() / 4;
		
		prepareMatrix(mMatrix, distanceX, r);

		mMatrix.preTranslate(-childCenterX, -childCenterY);
		mMatrix.postTranslate(childCenterX, childCenterY);
		
		final int d = Math.min(r, Math.abs(distanceX));
		if(d < 100){
			mMatrix.postTranslate(left, top);
		}else {
			if(distanceX < 0){		// LEFT
				mMatrix.postTranslate(left + distanceFromRightCenterX * 0.3f, top);
			}else {					// RIGHT
				mMatrix.postTranslate(left + distanceFromLeftCenterX * 0.3f, top);
			}
			
		}
		canvas.drawBitmap(bitmap, mMatrix, mPaint);

		return false;

	}
	
	private void prepareMatrix(final Matrix outMatrix, int distanceX, int r){
		
		final int d = Math.min(r, Math.abs(distanceX));
		final float translateZ = (float) Math.sqrt((r * r) - (d * d));
		//solve for t: d = r*cos(t)
		double radians = Math.acos((float) d / r);		
		double degree = 90 - ((180 / Math.PI) * radians);
		
		if(degree > 70) degree = 70;
		if(degree < 10) degree = 0;
		
		mCamera.save();
		mCamera.translate(0, 0, r-translateZ);

		if (distanceX < 0) {
			degree = 360 - degree;
		}
				
		mCamera.rotateY((float) degree);
		mCamera.getMatrix(outMatrix);
		mCamera.restore();

		mPaint.setColorFilter(calculateLight((float) degree));

	}

	private Bitmap getChildDrawingCache(final View child){
		Bitmap bitmap = child.getDrawingCache();
		if (bitmap == null) {
			child.setDrawingCacheEnabled(true);
			child.buildDrawingCache();
			bitmap = child.getDrawingCache();
		}
		return bitmap;
	}	

	private LightingColorFilter calculateLight(final float rotation) {
		final double cosRotation = Math.cos(Math.PI * rotation / 180);
		int intensity = AMBIENT_LIGHT + (int) (DIFFUSE_LIGHT * cosRotation);
		int highlightIntensity = (int) (SPECULAR_LIGHT * Math.pow(cosRotation, SHININESS));
		if (intensity > MAX_INTENSITY) {
			intensity = MAX_INTENSITY;
		}
		if (highlightIntensity > MAX_INTENSITY) {
			highlightIntensity = MAX_INTENSITY;
		}
		final int light = Color.rgb(intensity, intensity, intensity);
		final int highlight = Color.rgb(highlightIntensity, highlightIntensity, highlightIntensity);
		return new LightingColorFilter(light, highlight);
	} 

}
