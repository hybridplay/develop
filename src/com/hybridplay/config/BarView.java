package com.hybridplay.config;

import com.hybridplay.bluetooth.Sensor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class BarView extends View {

	private Bitmap  mBitmap;
	private Paint   mPaint = new Paint();
    private Canvas  mCanvas = new Canvas();
    
    private float   mScale;
    private int   	mYOffset;
    private int     mColorX, mColorY, mColorZ, mColorIR;
    private int   	maxValue = 1024;
    
    public  Sensor mSensorX, mSensorY, mSensorZ, mSensorIR;
    
    public BarView(Context context) {
        super(context);
        init();
    }
    
    public BarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init(){
    	mColorX = Color.RED;
    	mColorY = Color.BLUE;
    	mColorZ = Color.GREEN;
    	mColorIR = Color.BLACK;
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mSensorX = new Sensor("x",280,380,0);
        mSensorY = new Sensor("y",280,380,0);
        mSensorZ = new Sensor("z",280,380,0);
        mSensorIR = new Sensor("IR",20,512,1);
    }
    
    public void drawBar(int valueX, int valueY, int valueZ, int valueIR){
    	final Paint paint = mPaint;
        
        mCanvas.drawColor(Color.WHITE); //borramos todo
        
        setMaxValue(380);
        final int x = mYOffset + (int)(valueX * mScale);
        mSensorX.update(x,valueX);
        
        final int y = mYOffset + (int)(valueY * mScale);
        mSensorY.update(y,valueY);
        
        final int z = mYOffset + (int)(valueZ * mScale);
        mSensorZ.update(z,valueZ);
        
        setMaxValue(1024);
        final int ir = mYOffset + (int)(valueIR * mScale);
        mSensorIR.update(ir,valueIR);
        
        mCanvas = mSensorX.draw(mCanvas, paint, mColorX, 20, 0);
        mCanvas = mSensorY.draw(mCanvas, paint, mColorY, 70, 0);
        mCanvas = mSensorZ.draw(mCanvas, paint, mColorZ, 120, 0);
        mCanvas = mSensorIR.draw(mCanvas, paint, mColorIR, 170, 30);
        
		invalidate();
    }
    
    public void setMaxValue(int max){
    	maxValue = max;
    	mScale = - (mYOffset * (1.0f / maxValue));
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        mCanvas.setBitmap(mBitmap);
        mCanvas.drawColor(0xFFFFFFFF);
        mYOffset = h;
        mScale = - (mYOffset * (1.0f / maxValue));
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (this) {
        	
            if (mBitmap != null) { 	
                canvas.drawBitmap(mBitmap, 0, 0, null);
            }
            
        } 
    }
}
