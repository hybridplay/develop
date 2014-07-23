package com.hybridplay.puzzlecity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceView;

import com.hybridplay.app.R;

public class Fichas extends SurfaceView{
	public Bitmap ficha;
	private GameEngine gameEngine;
	public Rect fichaSrcRect;
	public Rect fichaDstRect;

	public int randomFicha;
	
	public float pX, pY;
	public float vX;
	
	public int screenW, screenH;
	private int width, height;
	
	public boolean alive;
	
	public Fichas(Context context) {
        super(context);
    }
	
	public Fichas(Context context, GameEngine gameEngine, int w, int h){
		super(context);
		this.gameEngine = gameEngine;
		screenW = w;
		screenH = h;
		vX = 5;
		width = 70;
		height = 80;
		reloadFicha();
	}
	
	public void reloadFicha(){
		alive = true;
        
		pX = screenW + 100;
		pY = (float)(Math.random()*screenH-200);
		
		randomFicha = (int)Math.ceil(Math.random()*10);

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
		fichaSrcRect = new Rect(0,0,width,height);
	}
	
	public void updateFicha(){
		if(pX > -ficha.getWidth()){
			pX -= gameEngine.avion.vX;
			//Log.i("log fichas pX" , "pX: " + pX);
			checkCollision(fichaSrcRect);
		}else{
			reloadFicha();
		}
	}
	
	//check if trash touch kid
	private void checkCollision(Rect rect){
//		int pX = (int) gameEngine.avion.vX;
//		int pY = (int) gameEngine.avion.vY;
//		
//		int radius = (int) screenW / 4 ; // 190 con pantalla de 800 
//		Log.i("log","radius: " + radius);
//		Log.i("log","math: " + Math.abs(pX - gX) + Math.abs(pY - gY));
//		
		//int radius = width;
		//int maxDistance = width;
		
		if (gameEngine.avion.dstRect.intersect(fichaDstRect)){
			//Log.i("log", "colision");
			reloadFicha();
			gameEngine.playerScore++;
		}
		
//		if (Math.abs(pX - gX) + Math.abs(pY - gY) < radius) { 
//			//eatTrash(i);
//			reloadFicha();
//			Log.i("log", "colision");
//		}
		
//		if (Math.abs(pX - gX) + Math.abs(pY - gY) > maxDistance){ //si esta muy lejos la reseteamos para que aparezca cerca del ni�o
//			trash.get(i).setActive(false);
//		}
		
	}
	
	public void drawFichas(Canvas canvas){
		fichaDstRect = new Rect((int)pX,(int)pY,(int)pX+fichaSrcRect.width(),(int)pY+fichaSrcRect.height());
		canvas.drawBitmap(ficha,fichaSrcRect,fichaDstRect,null);
	}
	
	public boolean isAlive(){
		return alive;
	}
	
	public void kill(){
		alive = false;
	}

}
