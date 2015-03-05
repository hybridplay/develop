package com.hybridplay.glTron;

import com.hybridplay.app.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CanvasView extends SurfaceView  implements Runnable{
	
	private SurfaceHolder surfaceHolder;
	private Thread surfaceThread = null;
	boolean isRunning = false;
	
	int currentFrame = 0; 	// for drawing sprite
	
	String playWith;
	boolean triggerXL, triggerXR, triggerYL, triggerYR, triggerZL, triggerZR;
	
	//////////////////////////////////SENSOR REFERENCE
	public Bitmap sUP_ON, sDOWN_ON, sLEFT_ON, sRIGHT_ON;
	public Bitmap sUP_OFF, sDOWN_OFF, sLEFT_OFF, sRIGHT_OFF;
	public Rect srcRect_UP, srcRect_DOWN, srcRect_LEFT, srcRect_RIGHT;
	public Rect dstRect_UP, dstRect_DOWN, dstRect_LEFT, dstRect_RIGHT;
	//////////////////////////////////SENSOR REFERENCE
	
	public CanvasView(Context context) {
        super(context);
	}
	
	public CanvasView(Context context,String pw) {
        super(context);
        playWith = pw;
        initSensorGraphics();
        
        surfaceHolder = getHolder();
		isRunning = true;
		setKeepScreenOn(true);
    }
	
	private void initSensorGraphics(){
		sUP_ON = BitmapFactory.decodeResource(getResources(), R.drawable.arriba_on);
		sUP_OFF = BitmapFactory.decodeResource(getResources(), R.drawable.arriba_off);
		
		sDOWN_ON = BitmapFactory.decodeResource(getResources(), R.drawable.abajo_on);
		sDOWN_OFF = BitmapFactory.decodeResource(getResources(), R.drawable.abajo_off);
		
		sLEFT_ON = BitmapFactory.decodeResource(getResources(), R.drawable.derecha_on);
		sLEFT_OFF = BitmapFactory.decodeResource(getResources(), R.drawable.derecha_off);
		
		sRIGHT_ON = BitmapFactory.decodeResource(getResources(), R.drawable.izquierda_on);
		sRIGHT_OFF = BitmapFactory.decodeResource(getResources(), R.drawable.izquierda_off);
		
		srcRect_UP = new Rect(0,0,sUP_ON.getWidth(),sUP_ON.getHeight());
		srcRect_DOWN = new Rect(0,0,sDOWN_ON.getWidth(),sDOWN_ON.getHeight());
		srcRect_RIGHT = new Rect(0,0,sLEFT_ON.getWidth(),sLEFT_ON.getHeight());
		srcRect_LEFT = new Rect(0,0,sRIGHT_ON.getWidth(),sRIGHT_ON.getHeight());
		
		dstRect_UP = new Rect(60,20,60+sUP_ON.getWidth(),20+sUP_ON.getHeight());
		dstRect_DOWN = new Rect(60,106,60+sDOWN_ON.getWidth(),106+sDOWN_ON.getHeight());
		dstRect_RIGHT = new Rect(30,50,30+sLEFT_ON.getWidth(),50+sLEFT_ON.getHeight());
		dstRect_LEFT = new Rect(118,50,118+sRIGHT_ON.getWidth(),50+sRIGHT_ON.getHeight());
	}
	
	//thread to update and draw. Game loop
	public void run() {
		Canvas canvas;

		while (isRunning) {
			canvas = null;
			update(canvas);
			
		}
	}
	
	// when game is in ready mode
	private void update(Canvas canvas){
		try {
			canvas = surfaceHolder.lockCanvas();
			if (canvas == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				surfaceHolder = getHolder();
			} else {

				synchronized (surfaceHolder) {
					canvas.drawARGB(0, 0, 0, 0);
					drawSensor(canvas);
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		} finally {
			// in case of an exception the surface is not left in
			// an inconsistent state
			if (canvas != null) {
				surfaceHolder.unlockCanvasAndPost(canvas);
			}
		}

	}
	
	public void pause() {
		isRunning = false;
		while(true){
			try {
				surfaceThread.join();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			break;
		}
		surfaceThread = null;
	}
	
	public void resume() {
		isRunning = true;
		surfaceThread = new Thread(this);
		surfaceThread.start();
		setKeepScreenOn(true);
	}
	
	public void updateSensor(boolean tXL,boolean tXR,boolean tYL,boolean tYR,boolean tZL,boolean tZR){
		triggerXL = tXL;
		triggerXR = tXR;
		triggerYL = tYL;
		triggerYR = tYR;
		triggerZL = tZL;
		triggerZR = tZR;
	}
	
	private void drawSensor(Canvas canvas){
		if(playWith.equals("Columpio")){
			// pinza vertical boton hacia abajo - oscilacion - eje Z
			if(triggerZR){ // LEFT
				canvas.drawBitmap(sLEFT_ON, srcRect_LEFT, dstRect_LEFT, null);
			}else{
				canvas.drawBitmap(sLEFT_OFF, srcRect_LEFT, dstRect_LEFT, null);
			}
			
			if(triggerZL){ // RIGHT
				canvas.drawBitmap(sRIGHT_ON, srcRect_RIGHT, dstRect_RIGHT, null);
			}else{
				canvas.drawBitmap(sRIGHT_OFF, srcRect_RIGHT, dstRect_RIGHT, null);
			}
		}else if(playWith.equals("Tobogan")){
			// we use here only IR sensor
			
		}else if(playWith.equals("SubeBaja")){
			// pinza horizontal - dos direcciones - eje Z
			if(triggerZR){ // UP
				canvas.drawBitmap(sUP_ON, srcRect_UP, dstRect_UP, null);
			}else{
				canvas.drawBitmap(sUP_OFF, srcRect_UP, dstRect_UP, null);
			}
			
			if(triggerZL){ // DOWN
				canvas.drawBitmap(sDOWN_ON, srcRect_DOWN, dstRect_DOWN, null);
			}else{
				canvas.drawBitmap(sDOWN_OFF, srcRect_DOWN, dstRect_DOWN, null);
			}
		}else if(playWith.equals("Balancin")){
			// pinza horizontal - cuatro direcciones - ejes Z Y
			if(triggerYR){ // UP
				canvas.drawBitmap(sUP_ON, srcRect_UP, dstRect_UP, null);
			}else{
				canvas.drawBitmap(sUP_OFF, srcRect_UP, dstRect_UP, null);
			}
			
			if(triggerYL){ // DOWN
				canvas.drawBitmap(sDOWN_ON, srcRect_DOWN, dstRect_DOWN, null);
			}else{
				canvas.drawBitmap(sDOWN_OFF, srcRect_DOWN, dstRect_DOWN, null);
			}
			
			if(triggerZR){ // LEFT
				canvas.drawBitmap(sLEFT_ON, srcRect_LEFT, dstRect_LEFT, null);
			}else{
				canvas.drawBitmap(sLEFT_OFF, srcRect_LEFT, dstRect_LEFT, null);
			}
			if(triggerZL){ // RIGHT
				canvas.drawBitmap(sRIGHT_ON, srcRect_RIGHT, dstRect_RIGHT, null);
			}else{
				canvas.drawBitmap(sRIGHT_OFF, srcRect_RIGHT, dstRect_RIGHT, null);
			}
		}else if(playWith.equals("Caballito")){
			// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
			
			// --------> CAMBIAR LA Y POR LA Z SI LA PINZA VA EN LA CABEZA DEL CABALLITO
			
			if(triggerYR){ // UP
				canvas.drawBitmap(sUP_ON, srcRect_UP, dstRect_UP, null);
			}else{
				canvas.drawBitmap(sUP_OFF, srcRect_UP, dstRect_UP, null);
			}
			
			if(triggerYL){ // DOWN
				canvas.drawBitmap(sDOWN_ON, srcRect_DOWN, dstRect_DOWN, null);
			}else{
				canvas.drawBitmap(sDOWN_OFF, srcRect_DOWN, dstRect_DOWN, null);
			}
			
			if(triggerXR){ // LEFT
				canvas.drawBitmap(sLEFT_ON, srcRect_LEFT, dstRect_LEFT, null);
			}else{
				canvas.drawBitmap(sLEFT_OFF, srcRect_LEFT, dstRect_LEFT, null);
			}
			if(triggerXL){ // RIGHT
				canvas.drawBitmap(sRIGHT_ON, srcRect_RIGHT, dstRect_RIGHT, null);
			}else{
				canvas.drawBitmap(sRIGHT_OFF, srcRect_RIGHT, dstRect_RIGHT, null);
			}
		}
	}

}
