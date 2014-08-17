package com.hybridplay.config;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GraphView extends View {
	private Bitmap  mBitmap;
	private Paint   mPaint = new Paint();
    private Canvas  mCanvas = new Canvas();
    
	private float   mSpeed = 1.0f;
	private float   mScale;
	private float   mYOffset;
	private float   mWidth;
    private float   maxValue = 1024f;
    
	public float   mLastX, mLastY, mLastZ;
    public float   mLastValueX,mLastValueY,mLastValueZ;
    public int     mColorX, mColorY, mColorZ;
    
    
    public GraphView(Context context) {
        super(context);
        init();
    }
    
    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init(){
    	mColorX = Color.RED;
    	mColorY = Color.BLUE;
    	mColorZ = Color.GREEN;
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    }
    
    public void addDataPoint(int valueX, int valueY, int valueZ, int valBat, int valIR){
        final Paint paint = mPaint;
        
        //Log.d("log mYOffset",Float.toString(mYOffset));
        //Log.d("log mScale",Float.toString(mScale));
        
        //DIBUJAMOS LA X
        float newX = mLastX + mSpeed;
        final float x = mYOffset + valueX * mScale;

        paint.setColor(mColorX);
        paint.setStrokeWidth(2.5f);
        mCanvas.drawLine(mLastX, mLastValueX, newX, x, paint);
        mLastValueX = x;

        mLastX += mSpeed;
       
        paint.setTextSize(16); 
        mCanvas.drawText("Value X: ", 10, 20, paint); 

        
        //DIBUJAMOS LA Y
        float newY = mLastY + mSpeed;
        final float y = mYOffset + valueY * mScale;
        
        paint.setColor(mColorY);
        mCanvas.drawLine(mLastY, mLastValueY, newY, y, paint);
        mLastValueY = y;
        mLastY += mSpeed;
        
        mCanvas.drawText("Value Y: ", 10, 40, paint); 
        
        //DIBUJAMOS LA Z
        float newZ = mLastZ + mSpeed;
        final float z = mYOffset + valueZ * mScale;
        
        paint.setColor(mColorZ);
        mCanvas.drawLine(mLastZ, mLastValueZ, newZ, z, paint);
        mLastValueZ = z;
        mLastZ += mSpeed;

        mCanvas.drawText("Value Z: ", 10, 60, paint); 
        
		invalidate();
    }
    
    public void setMaxValue(int max){
    	maxValue = max;
    	mScale = - (mYOffset * (1.0f / maxValue));
    }
    
    public void setSpeed(float speed){
    	mSpeed = speed;
    }
    
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        mCanvas.setBitmap(mBitmap);
        mCanvas.drawColor(0xFFFFFFFF);
        mYOffset = h;
        mScale = - (mYOffset * (1.0f / maxValue));
        mWidth = w;
        mLastX = mWidth;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (this) {
            if (mBitmap != null) {
                if (mLastX >= mWidth) {
                    mLastX = 0;
                    mLastY = 0;
                    mLastZ = 0;
                    final Canvas cavas = mCanvas;
                    cavas.drawColor(0xFFFFFFFF);
                    mPaint.setColor(0xFF777777);
                    cavas.drawLine(0, mYOffset, mWidth, mYOffset, mPaint);
                }
                canvas.drawBitmap(mBitmap, 0, 0, null);
            }
        } 
    }
}
