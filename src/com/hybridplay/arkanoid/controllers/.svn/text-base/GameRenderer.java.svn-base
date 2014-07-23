package uk.co.coldasice.projects.android.arkadroid.controllers;


import uk.co.coldasice.projects.android.arkadroid.R;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;

public class GameRenderer {
	
	private Paint paint = new Paint();
	private Bitmap imgBackground;
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
		imgBackground = BitmapFactory.decodeResource(r, R.drawable.texture);
		DisplayMetrics disp = r.getDisplayMetrics();
		this.w = disp.widthPixels;
		this.h = disp.heightPixels;
		startText = r.getString(R.string.start_text);
		scoreText = r.getString(R.string.score_text);
		livesText = r.getString(R.string.lives_text);
	}
	
	public void reset() {
		
	}

	public void render(Canvas canv) {

		canv.drawBitmap(imgBackground, 0, 0, null);
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
		
		if (gameState.isPaused()) {
			paint.setTextSize(25);
			canv.drawText(startText, 40, h/2, paint);
		}
		else {
			paint.setTextSize(12);
			canv.drawText(gameState.infoText, 10, 14, paint);
			canv.drawText(scoreText, 90, 14, paint);
			canv.drawText(gameState.scoreStr, 130, 14, paint);
			canv.drawText(livesText, 170, 14, paint);
			canv.drawText(gameState.livesLeftStr, 230, 14, paint);
		}
		
	}

	public void setSize(int width, int height) {
		this.w = width;
		this.h = height;
		imgBackground = Bitmap.createScaledBitmap(imgBackground, w, h, true);
	}

}
