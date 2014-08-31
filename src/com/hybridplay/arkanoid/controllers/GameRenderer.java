package com.hybridplay.arkanoid.controllers;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import com.hybridplay.app.R;

public class GameRenderer {
	
	private Paint paint = new Paint();
	private Bitmap connecting;
	private Rect srcConnecting, dstConnecting;
	private GameState gameState;
	public int w;
	public int h;
	private final String startText;
	private final String scoreText;
	private final String livesText;
	
		
	public GameRenderer(GameState gameState, Resources r) {
		this.gameState = gameState;
		paint.setStyle(Paint.Style.FILL);
		paint.setAntiAlias(true);
		paint.setStrokeWidth(1);
		paint.setColor(Color.WHITE);
		DisplayMetrics disp = r.getDisplayMetrics();
		this.w = disp.widthPixels;
		this.h = disp.heightPixels;
		startText = r.getString(R.string.start_text);
		scoreText = r.getString(R.string.score_text);
		livesText = r.getString(R.string.lives_text);
		
		connecting = BitmapFactory.decodeResource(r, R.drawable.connecting);
		srcConnecting = new Rect(0, 0, connecting.getWidth(), connecting.getHeight() );
		dstConnecting = new Rect((int) w/2 - connecting.getWidth()/2,(int) h/2-connecting.getHeight()/2,(int) w/2 + connecting.getWidth()/2,(int) h/2+ connecting.getHeight()/2 );
		
	}
	
	public void reset() {
		
	}

	public void render(Canvas canv) {

		canv.drawColor(Color.BLACK);
		paint.setColor(Color.RED);
		paint.setAlpha(128);
		canv.drawRect(0, h-55, w, h, paint);
		paint.setColor(Color.WHITE);
		paint.setAlpha(255);
		for (int i=0;i<gameState.bricks.size();i++) {
			gameState.bricks.get(i).draw(canv);
		}
		gameState.ball.draw(canv);
		gameState.paddle.draw(canv);
		
		if (!gameState.connected){
			canv.drawBitmap(connecting, srcConnecting, dstConnecting, null);
		}else if (gameState.connected && gameState.isPaused()) {
			paint.setColor(Color.WHITE);
			paint.setTextSize(34);
			canv.drawText(startText, 40, h/2, paint);			
		}else if (gameState.connected){
			paint.setTextSize(32);
			canv.drawText(gameState.infoText, 10, 30, paint);
			canv.drawText(scoreText, 90, 30, paint);
			canv.drawText(gameState.scoreStr, 190, 30, paint);
			canv.drawText(livesText, 250, 30, paint);
			canv.drawText(gameState.livesLeftStr, 390, 30, paint);
		}
		
	}

	public void setSize(int width, int height) {
		this.w = width;
		this.h = height;
	}

}
