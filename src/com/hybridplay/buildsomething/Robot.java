package com.hybridplay.buildsomething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceView;

public class Robot extends SurfaceView{
	
	public static final int UP = 4;
	public static final int DOWN = 8;
	public static final int RIGHT = 1;
	public static final int LEFT = 2;
	public static final int CENTER = 0;
	public boolean hasChangedDir = false;
	
	public Bitmap robotImage;
	public Rect sourceRect;
	private int pwidth;
	private int pNormalSpeed = 20;
	
	//public boolean hasFicha;
	
	public Rect srcRect;
	public Rect destRect;
	
	private int frameNr;        // number of frames in animation
	private int currentFrame;   // the current frame
	private long frameTicker;   // the time of the last frame update
	private int framePeriod;    // milliseconds between each frame (1000/fps)
	  
	public int spriteWidth;    // the width of the sprite to calculate the cut out rectangle
	private int spriteHeight;   // the height of the sprite
	
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
	
	public Robot(Context context, Bitmap bitmap, int x, int y, int screenWidth, int screenHeight, int fps, int frameCount){
		super(context);
		robotImage = bitmap;
		//robotW = robotImage.getWidth();
		//rototH = robotImage.getHeight();
		//sourceRect = new Rect(0, 0, robotW, rototH);
		screenW = screenWidth;
		screenH = screenHeight;
		
		moveXall = false;
		currentFrame = 0;
		frameNr = frameCount;
		spriteWidth = bitmap.getWidth() / frameCount;
		spriteHeight = bitmap.getHeight();
		sourceRect = new Rect(0, 0, spriteWidth, spriteHeight);
		destRect = new Rect((int)pX, (int) pY, (int) (pX + spriteWidth *1.5), (int) (pY + spriteHeight *1.5));
		framePeriod = 1000 / fps;
		frameTicker = 0l;
		
		vY = 0;
		vX = 0;
		pX = x;
		pY = screenH - (int)(spriteHeight*1.5);//screenH - screenH/6;
	}
	
	
	
	public void updateRobot(long gameTime){
		if (gameTime > frameTicker + framePeriod) {
	    	frameTicker = gameTime;
	    	// increment the frame
	    	currentFrame++;
	    	if (currentFrame >= frameNr) {
	    			currentFrame = 0;
	    		}
	    	}
	    	// define the rectangle to cut out sprite
	    	this.sourceRect.left = currentFrame * spriteWidth;
	    	this.sourceRect.right = this.sourceRect.left + spriteWidth;
	    	
		
//		if (pY >= 50 && pY <= screenH-200){
//			pY += gravedad + vY; 
//			if(pY < 50) pY = 50;
//			if (pY > screenH-200) pY = screenH-200;
//			//Log.i("log vY en > 200 ", "vY: " + vY);
//		}
			
		
		//if (vX > 0) vX -= .1f; //para que se pare efecto rozamiento
		
//		if (vY >= -2.5 && vY <= 1) {
//			vY += .2f; //fuerza motor bajar efecto rozamiento
//			if (vY > 1) vY = 1;
//		}
	}
	public void drawRobot(Canvas canvas){
		destRect.left = (int) pX;
		destRect.top = (int) pY;
		destRect.right = (int)pX+sourceRect.width();
		destRect.bottom = (int)pY+sourceRect.height();
		
		//destRect = new Rect((int)pX,(int)pY,(int)(pX+sourceRect.width()*1.5),(int)(pY+sourceRect.height()*1.5));
		//destRect = new Rect((int)pX, (int) pY, (int) (pX + spriteWidth *1.5), (int) (pY + spriteHeight *1.5));
		//Log.i("log","spriteWidth " +spriteWidth );
		canvas.drawBitmap(robotImage,sourceRect,destRect,null);
		
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
