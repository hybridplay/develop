package com.hybridplay.puzzlecity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceView;

import com.hybridplay.app.R;

public class Avion extends SurfaceView{
	
	public Bitmap avion;
	public Rect avionRect;
	
	//public boolean hasFicha;
	
	public Rect srcRect;
	public Rect dstRect;
	
	private float gravedad = .3f;
	
	public float pX, pY;
	public float vX, vY = 0;
	
	public int screenW, screenH;
	public int avionW, avionH;
	
	public boolean alive, moveXall;
	
	public Avion(Context context) {
        super(context);
    }
	
	public Avion(Context context, int w, int h){
		super(context);
		avion = BitmapFactory.decodeResource(getResources(), R.drawable.avion);
		avionW = avion.getWidth();
		avionH = avion.getHeight();
		avionRect = new Rect(0, 0, avionW, avionH);
		screenW = w;
		screenH = h;
		vY = 0;
		vX = 0;
		pX = screenW/10;
		pY = screenH - (avionH*2);//screenH - screenH/6;
		moveXall = false;
	}
	
	
	
	public void updateAvion(){
		if(pX < screenW/2){
			pX += vX;
		}else{
			moveXall = true;
		}
		
		//Log.i("log pY ", "pY: " + pY);
		
//		if (pY >= 50){
//			pY += gravedad + vY; 
//			if(pY < 50) pY = 50;
//			Log.i("log vY en > 50 ", "vY: " + vY);
//		}
		
		if (pY >= 50 && pY <= screenH-200){
			pY += gravedad + vY; 
			if(pY < 50) pY = 50;
			if (pY > screenH-200) pY = screenH-200;
			//Log.i("log vY en > 200 ", "vY: " + vY);
		}
			
		
		if (vX > 0) vX -= .1f; //para que se pare efecto rozamiento
		if (vY >= -2.5 && vY <= 1) {
			vY += .2f; //fuerza motor bajar efecto rozamiento
			if (vY > 1) vY = 1;
		}
	}
	public void drawAvion(Canvas canvas){
		dstRect = new Rect((int)pX,(int)pY,(int)pX+avionRect.width(),(int)pY+avionRect.height());
		canvas.drawBitmap(avion,avionRect,dstRect,null);
		
//		if(hasFicha){
//			fichaDstRect = new Rect((int)pX+(srcRect.width()/2),(int)pY,(int)pX+(srcRect.width()/2)+fichaSrcRect.width(),(int)pY+fichaSrcRect.height());
//			canvas.drawBitmap(ficha,fichaSrcRect,fichaDstRect,null);
//		}
		
	}
	
	public boolean isAlive(){
		return alive;
	}
	
	public void kill(){
		alive = false;
	}

	public float getvX() {
		return vX;
	}

	public void setvX(float vX) {
		this.vX = vX;
	}
	
	
}
