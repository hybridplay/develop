package com.hybridplay.puzzlecity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceView;

import com.hybridplay.app.R;

public class Nube extends SurfaceView {
	
	public Bitmap nube;
	public Bitmap ficha;
	public boolean hasFicha;
	public boolean isBlack = false;
	
	public Rect srcRect;
	public Rect dstRect;
	
	public Rect fichaSrcRect;
	public Rect fichaDstRect;
	
	public int randomNube;
	public int randomFicha;
	
	public float pX, pY;
	public float vX;
	
	public int screenW, screenH;
	
	public boolean alive;
	
	public Nube(Context context) {
        super(context);
    }
	
	public Nube(Context context, int w, int h){
		super(context);
		
		screenW = w;
		screenH = h;
		
		reloadNube();
	}
	
	public void reloadNube(){
		alive = true;
        
		pX = screenW + (float)(Math.random()*60);
		pY = screenH/2 + screenH/4;
		
		vX = 3 + (float)Math.random()*4;
		
		if(Math.random() < 0.8){
			hasFicha = true;
		}else{
			hasFicha = false;
		}
		
		isBlack = false;
		
		randomNube = (int)Math.ceil(Math.random()*3);
		randomFicha = (int)Math.ceil(Math.random()*10);
		
		if(randomNube == 1){
			nube = BitmapFactory.decodeResource(getResources(), R.drawable.nube2);
			srcRect = new Rect(0,0,225,118);
		}else if(randomNube == 2){
			nube = BitmapFactory.decodeResource(getResources(), R.drawable.nube3);
			srcRect = new Rect(0,0,326,115);
		}else if(randomNube == 3){
			nube = BitmapFactory.decodeResource(getResources(), R.drawable.nube4);
			srcRect = new Rect(0,0,200,104);
			hasFicha = false;
			isBlack = true;
		}
		
		switch (randomFicha) {
			case 1:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.pieza1);
				break;
			case 2:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.pieza2);
				break;
			case 3:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.pieza3);
				break;
			case 4:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.pieza4);
				break;
			case 5:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.pieza5);
				break;
			case 6:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.pieza6);
				break;
			case 7:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.pieza7);
				break;
			case 8:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.pieza8);
				break;
			case 9:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.pieza9);
				break;
			case 10:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.pieza10);
				break;
		}
		fichaSrcRect = new Rect(0,0,70,80);
	}
	
	public void updateNube(){
		if(pX > -300){
			pX -= vX;
		}else{
			reloadNube();
		}
	}
	
	public void drawNube(Canvas canvas){
		dstRect = new Rect((int)pX,(int)pY,(int)pX+srcRect.width(),(int)pY+srcRect.height());
		canvas.drawBitmap(nube,srcRect,dstRect,null);
		
		if(hasFicha){
			fichaDstRect = new Rect((int)pX+(srcRect.width()/2),(int)pY,(int)pX+(srcRect.width()/2)+fichaSrcRect.width(),(int)pY+fichaSrcRect.height());
			canvas.drawBitmap(ficha,fichaSrcRect,fichaDstRect,null);
		}
		
	}
	
	public boolean isAlive(){
		return alive;
	}
	
	public void kill(){
		alive = false;
	}

}
