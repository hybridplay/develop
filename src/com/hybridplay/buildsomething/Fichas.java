package com.hybridplay.buildsomething;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
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
	private float vY;
	public boolean alive;
	
	public Fichas(Context context) {
        super(context);
    }
	
	public Fichas(Context context, GameEngine gameEngine, int w, int h){
		super(context);
		this.gameEngine = gameEngine;
		screenW = w;
		screenH = h;
		vY = 3f;
		width = 70;
		height = 80;
		reloadFicha();
	}
	
	public void reloadFicha(){
		alive = true;
        
		pX = (float)(Math.random()*screenW);
		pY = -50;
		
		randomFicha = (int)Math.ceil(Math.random()*10);

		switch (randomFicha) {
			case 1:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.bici2);
				break;
			case 2:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.bici3);
				break;
			case 3:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.bici4);
				break;
			case 4:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.bici5);
				break;
			case 5:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.bici2);
				break;
			case 6:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.bici3);
				break;
			case 7:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.bici4);
				break;
			case 8:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.bici5);
				break;
			case 9:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.bici2);
				break;
			case 10:
				ficha = BitmapFactory.decodeResource(getResources(), R.drawable.bici3);
				break;
		}
		fichaSrcRect = new Rect(0,0,ficha.getWidth(),ficha.getHeight());
		fichaDstRect = new Rect((int)pX,(int)pY,(int)pX+(fichaSrcRect.width() *2),(int)pY+(fichaSrcRect.height()*2));
	}
	
	public void updateFicha(){
		if(pY < screenH + height){
			pY += vY;
			if (vY < 10) vY += .01f; //aumento progresivo de la velocidad hasta un limite
			//Log.i("log fichas pX" , "pX: " + pX);
			checkCollision(fichaSrcRect);
		}else{
			reloadFicha();
		}
	}
	
	//check if trash touch kid
	private void checkCollision(Rect rect){
		
		if (gameEngine.robot.destRect.intersect(fichaDstRect)){
			//Log.i("log", "colision");
			reloadFicha();
			gameEngine.playerScore++;
			if (gameEngine.playerScore >= 20){
				gameEngine.gameState = 3; //won = 3
			}
		}
		
	}
	
	public void drawFichas(Canvas canvas){
		fichaDstRect.left = (int) pX;
		fichaDstRect.top = (int) pY;
		fichaDstRect.right = (int)pX+fichaSrcRect.width();
		fichaDstRect.bottom = (int)pY+fichaSrcRect.height();
		//fichaDstRect = new Rect((int)pX,(int)pY,(int)pX+(fichaSrcRect.width() *2),(int)pY+(fichaSrcRect.height()*2));
		canvas.drawBitmap(ficha,fichaSrcRect,fichaDstRect,null);
	}
	
	public boolean isAlive(){
		return alive;
	}
	
	public void kill(){
		alive = false;
	}

}
