package com.hybridplay.fishing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceView;

import com.hybridplay.app.R;

public class Objects extends SurfaceView{
	public Bitmap object_img;
	private GameEngine gameEngine;
	public Rect objectSrcRect;
	public Rect objectDstRect;

	public int randomObject;
	
	public float pX, pY;
	public float vX, Vy; //speed
	
	public int screenW, screenH;
	private int width, height;
	
	public boolean alive;
	
	public Objects(Context context) {
        super(context);
    }
	
	public Objects(Context context, GameEngine gameEngine, int w, int h){
		super(context);
		this.gameEngine = gameEngine;
		screenW = w;
		screenH = h;
		vX = 5;
		width = 70;
		height = 80;
		reloadObject();
	}
	
	public void reloadObject(){
		alive = true;
        
		pX = screenW + 100;
		pY = (float)(Math.random()*screenH-200);
		
		randomObject = (int)Math.ceil(Math.random()*10);

		switch (randomObject) {
			case 1:
				object_img = BitmapFactory.decodeResource(getResources(), R.drawable.perla);
				break;
			case 2:
				object_img = BitmapFactory.decodeResource(getResources(), R.drawable.perla);
				break;
			case 3:
				object_img = BitmapFactory.decodeResource(getResources(), R.drawable.perla);
				break;
			case 4:
				object_img = BitmapFactory.decodeResource(getResources(), R.drawable.perla);
				break;
			case 5:
				object_img = BitmapFactory.decodeResource(getResources(), R.drawable.perla);
				break;
			case 6:
				object_img = BitmapFactory.decodeResource(getResources(), R.drawable.perla);
				break;
			case 7:
				object_img = BitmapFactory.decodeResource(getResources(), R.drawable.perla);
				break;
			case 8:
				object_img = BitmapFactory.decodeResource(getResources(), R.drawable.perla);
				break;
			case 9:
				object_img = BitmapFactory.decodeResource(getResources(), R.drawable.perla);
				break;
			case 10:
				object_img = BitmapFactory.decodeResource(getResources(), R.drawable.perla);
				break;
		}
		objectSrcRect = new Rect(0,0,object_img.getWidth(),object_img.getHeight());
		objectDstRect = new Rect((int)pX,(int)pY,(int)pX+objectSrcRect.width(),(int)pY+objectSrcRect.height());
	}
	
	public void updateObject(){
		if(pX > -object_img.getWidth()){
			pX -= gameEngine.player.vX;
			//Log.i("log fichas pX" , "pX: " + pX);
			checkCollision(objectDstRect);
		}else{
			reloadObject();
		}
	}
	
	//check if trash touch kid
	private void checkCollision(Rect rect){
		
		if (gameEngine.player.destRect.intersect(rect)){
			//Log.i("log", "colision");
			reloadObject();
			gameEngine.playerScore++;
		}
		
	}
	
	public void drawObject(Canvas canvas){
		objectDstRect.left = (int) pX;
		objectDstRect.top = (int) pY;
		objectDstRect.right = (int)pX+objectSrcRect.width();
		objectDstRect.bottom = (int)pY+objectSrcRect.height();
		//objectDstRect = new Rect((int)pX,(int)pY,(int)pX+objectSrcRect.width(),(int)pY+objectSrcRect.height());
		canvas.drawBitmap(object_img,objectSrcRect,objectDstRect,null);
	}
	
	public boolean isAlive(){
		return alive;
	}
	
	public void kill(){
		alive = false;
	}

}
