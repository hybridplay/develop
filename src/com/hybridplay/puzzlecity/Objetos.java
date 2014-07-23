package com.hybridplay.puzzlecity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceView;

import com.hybridplay.app.R;

public class Objetos extends SurfaceView {
	
	public Bitmap obj;
	public boolean isFicha;
	public boolean dir;
	
	public Rect srcRect;
	public Rect dstRect;
	
	public int randomObj;
	
	public float pX, pY;
	public float vX, vY;
	
	public int screenW, screenH;
	
	public boolean alive;
	
	public Objetos(Context context) {
        super(context);
    }
	
	public Objetos(Context context, int w, int h){
		super(context);
		
		screenW = w;
		screenH = h;
		
		reloadObjeto();
	}
	
	public void reloadObjeto(){
		alive = true;
		
		if(Math.random() < 0.6){
			isFicha = true;
		}else{
			isFicha = false;
		}
		
		if(Math.random() < 0.5){
			dir = true;
		}else{
			dir = false;
		}
		
		if(dir){ // from left
			pX = -100;
			pY = 140;
		
			vX = 2 + (float)Math.random()*4;
			vY = 2 + (float)Math.random()*4;
		}else{ // from right
			pX = screenW + (float)(Math.random()*30);
			pY = 140;
		
			vX = 2 + (float)Math.random()*4;
			vY = 2 + (float)Math.random()*4;
		}
		
		if(isFicha == false){
			randomObj = (int)Math.ceil(Math.random()*4);

			if(randomObj == 1){
				obj = BitmapFactory.decodeResource(getResources(), R.drawable.bola);
				srcRect = new Rect(0,0,50,49);
			}else if(randomObj == 2){
				obj = BitmapFactory.decodeResource(getResources(), R.drawable.bomba);
				srcRect = new Rect(0,0,60,58);
			}else if(randomObj == 3){
				obj = BitmapFactory.decodeResource(getResources(), R.drawable.cactus);
				srcRect = new Rect(0,0,60,76);
			}
		}else{
			int randomFicha = (int)Math.ceil(Math.random()*10);
			
			switch (randomFicha) {
			case 1:
				obj = BitmapFactory.decodeResource(getResources(), R.drawable.pieza1);
				break;
			case 2:
				obj = BitmapFactory.decodeResource(getResources(), R.drawable.pieza2);
				break;
			case 3:
				obj = BitmapFactory.decodeResource(getResources(), R.drawable.pieza3);
				break;
			case 4:
				obj = BitmapFactory.decodeResource(getResources(), R.drawable.pieza4);
				break;
			case 5:
				obj = BitmapFactory.decodeResource(getResources(), R.drawable.pieza5);
				break;
			case 6:
				obj = BitmapFactory.decodeResource(getResources(), R.drawable.pieza6);
				break;
			case 7:
				obj = BitmapFactory.decodeResource(getResources(), R.drawable.pieza7);
				break;
			case 8:
				obj = BitmapFactory.decodeResource(getResources(), R.drawable.pieza8);
				break;
			case 9:
				obj = BitmapFactory.decodeResource(getResources(), R.drawable.pieza9);
				break;
			case 10:
				obj = BitmapFactory.decodeResource(getResources(), R.drawable.pieza10);
				break;
			}
			srcRect = new Rect(0,0,70,80);
		}
		
	}

	
	public void updateObjeto(){
		if(dir){ // from left
			if(pX < screenW/3){
				pX += vX;
			}else{
				pY += vY;
			}
			if(pY > screenH + 30){
				reloadObjeto();
			}
		}else{ // from right
			if(pX > screenW/3 * 2){
				pX -= vX;
			}else{
				pY += vY;
			}
			if(pY > screenH + 30){
				reloadObjeto();
			}
		}
	}
	
	public void drawObjeto(Canvas canvas){
		dstRect = new Rect((int)pX,(int)pY,(int)pX+srcRect.width(),(int)pY+srcRect.height());
		canvas.drawBitmap(obj,srcRect,dstRect,null);
	}
	
	public boolean isAlive(){
		return alive;
	}
	
	public void kill(){
		alive = false;
	}
}
