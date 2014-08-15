package com.hybridplay.puzzlecity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceView;

public class Player extends SurfaceView{
		
	public static final int UP = 4;
	public static final int DOWN = 8;
	public static final int RIGHT = 1;
	public static final int LEFT = 2;
	public static final int CENTER = 0;
	public boolean hasChangedDir = false;
	private int dir; // direction of movement 0 = not moving
	
	private boolean eating;
	
	private Bitmap bitmap;      // the animation sequence
	private Rect sourceRect;    // the rectangle to be drawn from the animation bitmap
	public Rect destRect;
	private int frameNrW;        // number of frames in animation 
	private int frameNrH;		// number of animations in the image (each animation is orizontal)
	private int currentFrame;   // the current frame
	private int currentAnimation; // the current animation selected in the image
	private long frameTicker;   // the time of the last frame update
	private int framePeriod;    // milliseconds between each frame (1000/fps)
	  
	private int spriteWidth;    // the width of the sprite to calculate the cut out rectangle
	private int spriteHeight;   // the height of the sprite
	
	private float pX, pXOrigin;              // the X coordinate of the object (top left of the image)
	private float pY, pYOrigin;              // the Y coordinate of the object (top left of the image)
	
	private float gravity = 1;

	public float vX, vY = 0;
	
	public int screenW, screenH;
	
	public boolean alive, moveXall;
	
	private int lives, normalSpeed, powerSpeed;
	private float angle;
	
	public Player(Context context) {
        super(context);
    }
	
	public Player(Context context, Bitmap bitmap, int x, int y, int width, int height, int fps, int frameCountW, int frameCountH){
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
		frameNrW = frameCountW;
		frameNrH = frameCountH;
		currentAnimation = 0; //0 = adelante , 1 = atras , 2 = derecha, 3 = izquierda, 4 = coge derecha, 5 = coge izquierda, 6 = salta, 7 = saluda
		spriteWidth = bitmap.getWidth() / frameCountW;
		spriteHeight = bitmap.getHeight() / frameCountH;
		sourceRect = new Rect(0, currentAnimation * spriteHeight, spriteWidth, currentAnimation * spriteHeight + spriteHeight);
		
		framePeriod = 1000 / fps;
		frameTicker = 0l;
		
		lives = 3;
		normalSpeed = 2;
		powerSpeed = 4;
		angle = 0f;
		
		this.pX = x-spriteWidth/2; //para colocarlos en el centro de la pantalla
		this.pY = y-spriteHeight/2;
		pXOrigin = x;
		pYOrigin = y;
		destRect = new Rect((int)pX, (int) pY, (int) pX + spriteWidth, (int) pY + spriteHeight);
	}
	
	public void updatePlayer(long gameTime){
		
	    if (gameTime > frameTicker + framePeriod) {
	    	frameTicker = gameTime;
	    	// increment the frame
	    	currentFrame++;
	    	if (currentFrame >= frameNrW) {
	    			currentFrame = 0;
	    		}
	    	}
	    	// define the rectangle to cut out sprite
	    	this.sourceRect.left = currentFrame * spriteWidth;
	    	this.sourceRect.top = currentAnimation * spriteHeight;
	    	this.sourceRect.right = this.sourceRect.left + spriteWidth;
	    	this.sourceRect.bottom = currentAnimation * spriteHeight + spriteHeight;
	    	
	}
	
	public void draw(Canvas canvas){
		destRect.set((int) pX, (int) pY, (int) pX + spriteWidth,(int) pY + spriteHeight);
		canvas.drawBitmap(bitmap, sourceRect, destRect, null);
		
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
	
	public void reset(){
		pX = pXOrigin;
		pY = pYOrigin;
		dir = RIGHT;
		hasChangedDir = false;
	}

	public float getpX() {
		return pX;
	}

	public float getpY() {
		return pY;
	}
	
	public int getDir() {
		return dir;
	}
	
	public void setDir(int dir){
		if(this.dir != dir){
			this.dir = dir;
			hasChangedDir = true;
		}
	}

	public void setpX(float pX) {
		this.pX = pX;
	}

	public void setpY(float pY) {
		this.pY = pY;
	}
	
	public boolean isEating() {
		return eating;
	}

	public void setEating(boolean eating) {
		this.eating = eating;
	}
	
	public int getpLives(){
		return lives;
	}
	
	public int getpNormalSpeed(){
		return normalSpeed;
	}
	
	public int getSpriteHeight(){
		return spriteHeight;
	}
	
	public int getSpriteWidth(){
		return spriteWidth;
	}
	
	public void setAngle(float angle){
		this.angle = angle;
	}
	
	public float getAngle(){
		return angle;
	}
	
	public void setCurrentAnimation(int anim){
		currentAnimation = anim;
	}
	
	
}
