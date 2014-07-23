package com.hybridplay.buildsomething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceView;

import com.hybridplay.app.R;

public class Robot extends SurfaceView{
	
	public static final int UP = 4;
	public static final int DOWN = 8;
	public static final int RIGHT = 1;
	public static final int LEFT = 2;
	public static final int CENTER = 0;
	public boolean hasChangedDir = false;
	
	public Bitmap robotImage;
	public Rect robotRect;
	private int pwidth;
	private int pNormalSpeed = 20;
	
	//public boolean hasFicha;
	
	public Rect srcRect;
	public Rect dstRect;
	
	private float gravedad = .3f;
	
	public float pX, pY;
	public float vX, vY = 0;
	
	public int screenW, screenH;
	public int robotW, rototH;
	
	public boolean alive, moveXall;
	
	private int dir; // direction of movement 0 = not moving

	
	public Robot(Context context) {
        super(context);
    }
	
	public Robot(Context context, int w, int h){
		super(context);
		robotImage = BitmapFactory.decodeResource(getResources(), R.drawable.robot);
		robotW = robotImage.getWidth();
		rototH = robotImage.getHeight();
		robotRect = new Rect(0, 0, robotW, rototH);
		screenW = w;
		screenH = h;
		vY = 0;
		vX = 0;
		pX = screenW/10;
		pY = screenH - (int)(rototH*1.5);//screenH - screenH/6;
		moveXall = false;
	}
	
	
	
	public void updateAvion(){
		if(pX > 0 && pX < screenW){
			
		}
		
//		if(pX < screenW/2){
//			pX += vX;
//		}else{
//			moveXall = true;
//		}
//		
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
	public void drawActor(Canvas canvas){
		dstRect = new Rect((int)pX,(int)pY,(int)(pX+robotRect.width()*1.5),(int)(pY+robotRect.height()*1.5));
		canvas.drawBitmap(robotImage,robotRect,dstRect,null);
		
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
	
	public void setDir(int dir){
		if(this.dir != dir){
			this.dir = dir;
			hasChangedDir = true;
		}
	}
	
	public float getpX() {
		return pX;
	}

	public float getpY() {
		return pY;
	}
	
	public int getPwidth() {
		return pwidth;
	}

	public void setPwidth(int pwidth) {
		this.pwidth = pwidth;
	}
	
	public int getpNormalSpeed() {
		return pNormalSpeed;
	}
	
	public void setpX(float pX) {
		this.pX = pX;
	}

	public void setpY(float pY) {
		this.pY = pY;
	}
	
	
}
