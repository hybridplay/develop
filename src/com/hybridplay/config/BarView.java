/*
  Copyright (c) 2009 Bonifaz Kaufmann. 
  
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
package com.hybridplay.config;

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
    private float   mYOffset;
    private int     mColorX, mColorY, mColorZ, mColorIR;
  //  private float	  mMaxValueX = 0, mMinValueX = 1024, mCenterValueX = 0; 
    //los max y min se van acercando al centro con el tiempo
  //  private static float minDistValueX = 20;  //minimo para no tocar el centro
  //  private static float speedMinDistValue = 0.5f; //velocidad con la que se acercan al centro
  //  private float   fireMaxX = 0, fireMinX = 1024; //limites de disparo de accion
    
    private float   mWidth;
    private float   maxValue = 1024f; //1024 originalmente
    
    private Sensor mSensorX, mSensorY, mSensorZ, mSensorIR;
    
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
        mSensorX = new Sensor("x");
        mSensorY = new Sensor("y");
        mSensorZ = new Sensor("z");
        mSensorIR = new Sensor("IR");
    }
    
    public void drawBar(float valueX, float valueY, float valueZ, float valueIR){
    //public void drawBar(Sensor mSensorX, Sensor mSensorY, Sensor mSensorZ, Sensor mSensorIR){
    	final Paint paint = mPaint;
        
        mCanvas.drawColor(Color.WHITE); //borramos todo

        //DIBUJAMOS LA X
        final float x = mYOffset + valueX * mScale;
        mSensorX.update(x);
        
        final float y = mYOffset + valueY * mScale;
        mSensorY.update(y);
        
        final float z = mYOffset + valueZ * mScale;
        mSensorZ.update(z);
        
        final float ir = mYOffset + valueIR * mScale;
        mSensorIR.update(ir);
        
        mCanvas = mSensorX.draw(mCanvas, paint, mColorX, 20, 0);
        mCanvas = mSensorY.draw(mCanvas, paint, mColorY, 70, 0);
        mCanvas = mSensorZ.draw(mCanvas, paint, mColorZ, 120, 0);
        mCanvas = mSensorIR.draw(mCanvas, paint, mColorIR, 170, 0);
        
        
		invalidate();
    }
    
    public void setMaxValue(int max){
    	maxValue = max;
    	mScale = - (mYOffset * (1.0f / maxValue));
    }
    
    /*
    public void setSpeed(float speed){
    	mSpeed = speed;
    }
    */
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        mCanvas.setBitmap(mBitmap);
        mCanvas.drawColor(0xFFFFFFFF);
        mYOffset = h;
        mScale = - (mYOffset * (1.0f / maxValue));
        mWidth = w;
       // mLastX = mWidth;
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        synchronized (this) {
        	
            if (mBitmap != null) {
            	/*
                if (mLastX >= mWidth) {
                    mLastX = 0;
                    mLastY = 0;
                    mLastZ = 0;
                    final Canvas cavas = mCanvas;
                    cavas.drawColor(0xFFFFFFFF);
                    mPaint.setColor(0xFF777777);
                    cavas.drawLine(0, mYOffset, mWidth, mYOffset, mPaint);
                }
                */
            	
                canvas.drawBitmap(mBitmap, 0, 0, null);
            }
            
        } 
    }
}
