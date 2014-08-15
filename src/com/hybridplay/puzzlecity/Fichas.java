package com.hybridplay.puzzlecity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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
	
	private float pX, pY;
	public float vX;
	
	public int screenW, screenH;
	private int width, height;
	
	public boolean alive;
	private String gameType;
	
	public Fichas(Context context) {
        super(context);
    }
	
	public Fichas(Context context, GameEngine gameEngine, int w, int h, String gameType){
		super(context);
		this.gameEngine = gameEngine;
		screenW = w;
		screenH = h;
		vX = 5;
		width = 70;
		height = 80;
		this.gameType = gameType;
		reloadFicha(gameType);
	}
	
	public void reloadFicha(String gameType){
		alive = true;
        
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
		
		if (gameType.equals("SubeBaja")){
				pX = screenW + 100;
				pY = (float)(Math.random()*screenH-200);
				fichaDstRect = new Rect((int)pX, (int)pY, (int)pX+width, (int)pY+height);
			} 
		
		if(gameType.equals("Balancin")||gameType.equals("Caballito")){
				pX = (float)(Math.random()*gameEngine.stage.stageImg.getWidth())-gameEngine.stage.getpX();
				pY = (float)(Math.random()*gameEngine.stage.stageImg.getHeight())-gameEngine.stage.getpY();
				
//				if (!onThePath((int) pX, (int) pY, width, height, 10)){ //chequea que este sobre negro
//					reloadFicha(gameType);
//					Log.i("log", "ubicada sobre blanco RELOAD");
//				} else {
					fichaDstRect = new Rect((int) (gameEngine.stage.getpX() + pX),(int)(gameEngine.stage.getpY()+ pY),(int)(gameEngine.stage.getpX()+pX+width),(int)(gameEngine.stage.getpY()+pY+height));
//					Log.i("log", "ubicada sobre negro OK");
//				}

		}
		
		fichaSrcRect = new Rect(0,0,width,height);
		
	}
	
	public void updateFicha(){
		
		if (gameType.equals("SubeBaja")){
			
			if(pX > -ficha.getWidth()){
				pX -= gameEngine.avion.vX;
				//Log.i("log fichas pX" , "pX: " + pX);
				fichaDstRect.set((int)pX,(int)pY,(int)pX+width,(int)pY+height);
				checkCollision(fichaDstRect);
			}else{
				reloadFicha(gameType);
			}
		}
		
		if (gameType.equals("Balancin")||gameType.equals("Caballito")){
//			pX = gameEngine.stage.getpX();
//			pY = gameEngine.stage.getpY();
			//fichaDstRect.set((int)pX + gameEngine.stage.getpX(),(int)pY + gameEngine.stage.getpY(),(int)pX+width + gameEngine.stage.getpX(),(int)pY+height+gameEngine.stage.getpY());
			fichaDstRect.set((int)pX ,(int)pY,(int)pX+width,(int)pY+height);
			checkCollision(fichaDstRect);
		}
	}
	
	//check if trash touch kid
	private void checkCollision(Rect rect){
		if (gameType.equals("SubeBaja")){
			if (gameEngine.avion.dstRect.intersect(rect)){
				//Log.i("log", "colision");
				reloadFicha(gameType);
				gameEngine.playerScore++;
			}
		}else if (gameType.equals("Balancin")||gameType.equals("Caballito")){
			if(gameEngine.player.destRect.intersect(rect)){
				alive = false;
				//reloadFicha(gameType);
				gameEngine.playerScore++;
			}
		}
	}
	
	public void drawFichas(Canvas canvas){
//		Log.i("log","pX " + pX);
//		Log.i("log","pY " + pY);
		canvas.drawBitmap(ficha,fichaSrcRect,fichaDstRect,null);
	}
	
	public Boolean onThePath( int fichaX, int fichaY, int fichaW, int fichaH, int playerSpeed){
		//poner aqui los limites de pantalla?
		playerSpeed = 10;


		if (gameEngine.stage.stageMask.getPixel(
					(int) (pX + screenW/2 + fichaW/2 + playerSpeed), (int) (pY+ screenH/2 - fichaH/2 - playerSpeed))== Color.BLACK && //right&top point
							gameEngine.stage.stageMask.getPixel((int) (pX + screenW/2 + fichaW/2 + playerSpeed), (int) (pY+ screenH/2 + fichaH/2 + playerSpeed))== Color.BLACK //buttom&right
					){
				return true;
			
		} else if (gameEngine.stage.stageMask.getPixel(
					(int) (pX + screenW/2 - fichaW/2 - playerSpeed), (int) (pY+ screenH/2 - fichaH/2 - playerSpeed)) == Color.BLACK && //left&top point
							gameEngine.stage.stageMask.getPixel((int) (pX + screenW/2 - fichaH/2 - playerSpeed), (int) (pY+ screenH/2 + fichaH/2 + playerSpeed))== Color.BLACK //buttom&left
					){
				return true;
			
		} else if (gameEngine.stage.stageMask.getPixel((int) (pX + screenW/2 - fichaW/2 - playerSpeed), (int) (pY+ screenH/2 - fichaH/2 - playerSpeed)) == Color.BLACK && 	//left&top point){
				gameEngine.stage.stageMask.getPixel((int) (pX + screenW/2 + fichaW/2 + playerSpeed), (int) (pY+ screenH/2 - fichaH/2 - playerSpeed))== Color.BLACK){ 	//right&top point
				return true;
			
		}else if (gameEngine.stage.stageMask.getPixel((int) (pX + screenW/2 - fichaW/2 - playerSpeed), (int) (pY+ screenH/2 + fichaH/2 + playerSpeed)) == Color.BLACK && 	//buttom&left
				gameEngine.stage.stageMask.getPixel((int) (pX + screenW/2 + fichaW/2 + playerSpeed), (int) (pY+ screenH/2 + fichaH/2 + playerSpeed))== Color.BLACK){ 					//buttom&right
				return true;
			
		}else{
			Log.i("log","color Blanco");
			return false;
		}
		
	}
	
	public boolean isAlive(){
		return alive;
	}
	
	public void kill(){
		alive = false;
	}
	
	public void setpX(float x){
		pX = x;
	}
	
	public float getpX(){
		return pX;
	}
	
	public void setpY(float y){
		pY = y;
	}
	
	public float getpY(){
		return pY;
	}

}
