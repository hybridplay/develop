package com.hybridplay.fishing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceView;

public class Player extends SurfaceView{
	
	private Bitmap bitmap;      // the animation sequence
	private Rect sourceRect;    // the rectangle to be drawn from the animation bitmap
	public Rect destRect;
	private int frameNr;        // number of frames in animation
	private int currentFrame;   // the current frame
	private long frameTicker;   // the time of the last frame update
	private int framePeriod;    // milliseconds between each frame (1000/fps)
	  
	private int spriteWidth;    // the width of the sprite to calculate the cut out rectangle
	private int spriteHeight;   // the height of the sprite
	
	private float pX;              // the X coordinate of the object (top left of the image)
	private float pY;              // the Y coordinate of the object (top left of the image)
	
	private float gravity = 1;

	public float vX, vY = 0;
	
	public int screenW, screenH;
	
	public boolean alive, moveXall;
	
	public Player(Context context) {
        super(context);
    }
	
	public Player(Context context, Bitmap bitmap, int x, int y, int width, int height, int fps, int frameCount){
		super(context);
		this.bitmap = bitmap;
		screenW = width;
		screenH = height;
		vY = 0;
		vX = 0;
		//pX = screenW/10;
		//pY = screenH - (playerH*2);//screenH - screenH/6;
		moveXall = false;
	
		currentFrame = 0;
		frameNr = frameCount;
		spriteWidth = bitmap.getWidth() / frameCount;
		spriteHeight = bitmap.getHeight();
		sourceRect = new Rect(0, 0, spriteWidth, spriteHeight);
		
		framePeriod = 1000 / fps;
		frameTicker = 0l;
		this.pX = x-spriteWidth/2; //para colocarlos en el centro de la pantalla
		this.pY = y-spriteHeight/2;
		//destRect = new Rect((int)pX, (int) pY, (int) pX + spriteWidth, (int) pY + spriteHeight);
		destRect = new Rect((int) pX, (int) pY, (int) pX + spriteWidth,(int) pY + spriteHeight);
	}
	
	
	public void updatePlayer(long gameTime){
		
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
	    	
		if(pX < screenW/2){
			pX += vX;
		}else{
			moveXall = true;
		}
		
		if (pY > 50 && pY < screenH-200){
			pY += gravity; //para que caiga aumenta la Y
			pY -= vY; //para que se eleve disminuye la Y
			if(pY < 50) pY = 51;
			if (pY > screenH-200) pY = screenH-201;
			//Log.i("log vY ", "vY: " + vY);
			//Log.i("log y ", "y: " + y);
		}
			
		
		if (vX > 0) vX -= .1f; //para que se pare efecto rozamiento
		if (vY > 0) vY -= .1f;
		
//		{
//			vY += .2f; //fuerza motor bajar efecto rozamiento
//			if (vY > 1) vY = 1;
//		}
	}
	
	public void drawPlayer(Canvas canvas){
//		destRect.left = (int) pX;
//		destRect.top = (int) pY;
//		destRect.right = (int) pX + spriteWidth;
//		destRect.bottom = (int) pY + spriteHeight;
		destRect.set((int) pX, (int) pY, (int) pX + spriteWidth,(int) pY + spriteHeight);
		//destRect = new Rect((int)pX, (int) pY, (int) pX + spriteWidth, (int) pY + spriteHeight);
		canvas.drawBitmap(bitmap, sourceRect, destRect, null);
		
		//dstRect = new Rect((int)pX,(int)y,(int)pX+playerRect.width(),(int)y+playerRect.height());
		//canvas.drawBitmap(playerBmp,playerRect,dstRect,null);
		
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
