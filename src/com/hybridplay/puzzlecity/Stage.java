package com.hybridplay.puzzlecity;

import com.hybridplay.app.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceView;


public class Stage extends SurfaceView{
	
	//position by grid
		private int pX;
		private int pY;
		public Bitmap stageImg;
		public Bitmap stageMask;
		private int pXOrigin;
		private int pYOrigin;
		private int screenWidth, screenHeight;
		public Rect srcRect, dstRect;
		private Paint paint;
		private int dir;
	
	public Stage(Context context) {
        super(context);
    }
	
	//constructor
	public Stage(Context context, Bitmap stageImg, Bitmap stageMask, int x, int y, int width, int height){
		super(context);
		pXOrigin = x;
		pYOrigin = y;
		pX = pXOrigin;
		pY = pYOrigin;
		this.stageImg = stageImg;
		this.stageMask = stageMask;
		this.screenWidth = width;
		this.screenHeight = height;
		//dstRect = new Rect(0,0,stageImg.getWidth(),stageImg.getHeight());
		dstRect = new Rect(0,0,screenWidth,screenHeight);
		//srcRect = new Rect(pX,pY,pX+stageImg.getWidth(),pY+stageImg.getHeight());
		srcRect = new Rect(pX,pY,pX+screenWidth,pY+screenHeight);
		
		paint = new Paint();
		paint.setAntiAlias(true);
		
	}

	public Boolean checkExit(int playerW, int playerH, int playerSpeed){
		if (dir == 1){ //right
			if (stageMask.getPixel((int) (pX + screenWidth/2 + playerW/2 + playerSpeed), (int) (pY+ screenHeight/2 + playerH/2 + playerSpeed)) == getResources().getColor(R.color.green_end)){
				return true;
			}
		} else if (dir == 2){ //left
			if (stageMask.getPixel(
					(int) (pX + screenWidth/2 - playerW/2 - playerSpeed), (int) (pY+ screenHeight/2 - playerH/2 - playerSpeed)) == getResources().getColor(R.color.green_end)){
				return true;
			}
		} else if (dir == 4){ //up
			if (stageMask.getPixel((int) (pX + screenWidth/2 - playerW/2 - playerSpeed), (int) (pY+ screenHeight/2 - playerH/2 - playerSpeed)) == getResources().getColor(R.color.green_end)){ 	//right&top point
				return true;
			}
		} else if (dir == 8){ //down
			if (stageMask.getPixel((int) (pX + screenWidth/2 + playerW/2 + playerSpeed), (int) (pY+ screenHeight/2 + playerH/2 + playerSpeed))== getResources().getColor(R.color.green_end)){ 					//buttom&right
				return true;
			}
		}
		
		return false;
	}
	
	public Boolean canMove(int direccion, int playerX, int playerY, int playerW, int playerH, int playerSpeed){
		//direccion RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8, CENTER = 0t
		dir = direccion;
		if(pX >= -384 && pX <= 1850 && pY >= -180 && pY <= 580){
			if (direccion == 1){ //right
				if (stageMask.getPixel(
						(int) (pX + screenWidth/2 + playerW/2 + playerSpeed), (int) (pY+ screenHeight/2 - playerH/2 - playerSpeed)) != Color.WHITE && //right&top point
						stageMask.getPixel((int) (pX + screenWidth/2 + playerW/2 + playerSpeed), (int) (pY+ screenHeight/2 + playerH/2 + playerSpeed)) != Color.WHITE //buttom&right
						){
					return true;
				}
			} else if (direccion == 2){ //left
				if (stageMask.getPixel(
						(int) (pX + screenWidth/2 - playerW/2 - playerSpeed), (int) (pY+ screenHeight/2 - playerH/2 - playerSpeed)) != Color.WHITE && //left&top point
						stageMask.getPixel((int) (pX + screenWidth/2 - playerW/2 - playerSpeed), (int) (pY+ screenHeight/2 + playerH/2 + playerSpeed)) != Color.WHITE //buttom&left
						){
					return true;
				}
			} else if (direccion == 4){ //up
				if (stageMask.getPixel((int) (pX + screenWidth/2 - playerW/2 - playerSpeed), (int) (pY+ screenHeight/2 - playerH/2 - playerSpeed)) != Color.WHITE && 	//left&top point){
						stageMask.getPixel((int) (pX + screenWidth/2 - playerW/2 - playerSpeed), (int) (pY+ screenHeight/2 - playerH/2 - playerSpeed)) != Color.WHITE){ 	//right&top point
					return true;
				}
			} else if (direccion == 8){ //down
				if (stageMask.getPixel((int) (pX + screenWidth/2 - playerW/2 - playerSpeed), (int) (pY+ screenHeight/2 + playerH/2 + playerSpeed)) != Color.WHITE && 	//buttom&left
						stageMask.getPixel((int) (pX + screenWidth/2 + playerW/2 + playerSpeed), (int) (pY+ screenHeight/2 + playerH/2 + playerSpeed)) != Color.WHITE){ 					//buttom&right
					return true;
				}
			}else{
				Log.i("log","color Blanco");
				return false;
			}
		}else{
			if(pX < -384){
				pX = -380;
			}
			if(pX > 1850){
				pX = 1848;
			}
			
			if(pY < -180){
				pY = -178;
			}
			if(pY > 580){
				pY = 578;
			}
			return false;
		}
		
		return false;
	}
	
	public void draw(Canvas canvas, int playerX, int playerY, int playerW, int playerH, int playerSpeed){

		srcRect.set(pX, pY, pX+screenWidth,pY+screenHeight);
		canvas.drawBitmap(stageImg, srcRect, dstRect, null);
		
//		paint.setColor(Color.RED);
//		canvas.drawCircle((int) (screenWidth/2 - playerW/2 - playerSpeed), (int) (screenHeight/2 - playerH/2 - playerSpeed), 20, paint);
//
//		paint.setColor(Color.GREEN);
//		canvas.drawCircle((int) (screenWidth/2 + playerW/2 + playerSpeed), (int) (screenHeight/2 - playerH/2 - playerSpeed), 20, paint);
//		
//		paint.setColor(Color.BLUE);
//		canvas.drawCircle((int) (screenWidth/2 - playerW/2 - playerSpeed), (int) (screenHeight/2 + playerH/2 + playerSpeed), 20, paint);
//
//		paint.setColor(Color.YELLOW);
//		canvas.drawCircle((int) (screenWidth/2 + playerW/2 + playerSpeed), (int) (screenHeight/2 + playerH/2 + playerSpeed), 20, paint);

	}

	public int getpX() {
		return pX;
	}



	public void setpX(int pX) {
		this.pX = pX;
	}



	public int getpY() {
		return pY;
	}

	public void setpY(int pY) {
		this.pY = pY;
	}

	public int getpXOrigin() {
		return pXOrigin;
	}



	public void setpXOrigin(int pXOrigin) {
		this.pXOrigin = pXOrigin;
	}



	public int getpYOrigin() {
		return pYOrigin;
	}



	public void setpYOrigin(int pYOrigin) {
		this.pYOrigin = pYOrigin;
	}
	
	


}