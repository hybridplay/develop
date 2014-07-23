package com.hybridplay.arkanoid;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.hybridplay.arkanoid.controllers.GameLoop;
import com.hybridplay.arkanoid.controllers.GameRenderer;
import com.hybridplay.arkanoid.controllers.GameState;

public class ArkaNoidGameThread extends Thread {

	private boolean mRun = true;
	private SurfaceHolder holder;

	private boolean resetSafe;

	private long nextRender;
	private final double PADDLE_SPEED = 4.0;
	public final int RENDER_EVERY_MS = 30;
	private final int SLEEP_FOR_MS = 10;

	private GameRenderer renderer;

	private GameLoop gameloop;

	private GameState gameState;
	
	public ArkaNoidGameThread(SurfaceHolder holder, Context context) {
		this.holder = holder;
		Resources r = context.getResources();
		this.gameState = new GameState();
		this.gameloop = new GameLoop(gameState);
		this.renderer = new GameRenderer(gameState, r);
		gameState.init(r, renderer);
		gameloop.init();
	}

	@Override
	public void run() {
		try { Thread.sleep(SLEEP_FOR_MS); } catch (InterruptedException e) {	}
		Canvas canv;
		resetSafe = true;
		reset();
		while (mRun) {
			canv = null;
			try {
				long now = System.currentTimeMillis();
				if (now > nextRender) canv = this.holder.lockCanvas();
				synchronized (holder) {
					if (gameState.isRunning()) {
						resetSafe = true;
						gameloop.updateGame(renderer);
					}
					else if (gameState.isPaused()) reset();
					if (canv != null){
						renderer.render(canv);
						gameState.ball.updateTrails();
						gameState.paddle.updateTrails();
						nextRender = now + RENDER_EVERY_MS;
					}
				}
			}
			finally {
				if (canv != null) holder.unlockCanvasAndPost(canv);
			}
			try { Thread.sleep(SLEEP_FOR_MS); } catch (InterruptedException e) {	}
		}
	}
	

	private void reset() {
		if (!resetSafe) return;
		gameState.reset();
		renderer.reset();
		gameloop.reset();
		resetSafe = false;
	}

	public void pause() {
		gameState.setPaused();
	}

	public void gameStop() {
		gameState.setPaused();
		mRun = false;
	}

	public void setRunning() {
		mRun  = true;
	}
	
	public void gameGo() {
		gameState.setRunning();
		resetSafe = true;
		reset();
	}

	public void setSize(int width, int height) {
		synchronized (holder) {
			renderer.setSize(width, height);	
		}
	}
	
	public void changedLeft(double speed, boolean pressed) {
		synchronized (holder) {
			this.gameState.paddle.setSpeed(speed);
			this.gameState.paddle.setLeftPressed(pressed);
		}
	}
	
	public void changedRight(double speed, boolean pressed) {
		synchronized (holder) {
			this.gameState.paddle.setSpeed(speed);
			this.gameState.paddle.setRightPressed(pressed);
		}
	}
	
	public void changedBoth(double speed, boolean pressedLeft, boolean pressedRight) {
		synchronized (holder) {
			this.gameState.paddle.setSpeed(speed);
			this.gameState.paddle.setLeftPressed(pressedLeft);
			this.gameState.paddle.setRightPressed(pressedRight);
		}
	}
	
	boolean keyUp(int keyCode, KeyEvent msg) {
		/*if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
			changedLeft(PADDLE_SPEED,false);
			return true;
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
			changedRight(PADDLE_SPEED,false);
			return true;
		}*/
		return false;
	}

	public boolean keyDown(int keyCode, KeyEvent msg) {
		boolean handled = false;
		if (gameState.isPaused()) {
			gameGo();
			handled = true;
		}
		/*if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			changedLeft(PADDLE_SPEED,true);
			handled = true;
		}
		else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			changedRight(PADDLE_SPEED,true);
			handled = true;
		}*/
		if (gameState.ball.onPaddle && (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)){
			gameState.ball.onPaddle = false;
			handled = true;
		}
		
		return handled;
	}

	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		//int width = renderer.w;
		
		switch (action) {
			case MotionEvent.ACTION_DOWN: {
				if (gameState.isPaused()) {
					gameGo();
				}
				if (gameState.ball.onPaddle){
					gameState.ball.onPaddle = false;
				}
				
				/*int where = (int)event.getX();
				if (where < width/3) changedLeft(PADDLE_SPEED,true); //hacia la izquierda
				else if (where > (width - width/3)) changedRight(PADDLE_SPEED,true); //hacia la derecha*/
				return true;
			}
			/*case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:{
				changedBoth(PADDLE_SPEED, false, false);
				return true;
			}
			case MotionEvent.ACTION_MOVE: {
				boolean left = false;
				boolean right = false;
				int where = (int)event.getX();
				if (where < width/3) left = true;
				else if (where > (width - width/3)) right = true;
				changedBoth(PADDLE_SPEED, left, right);
				return true;
			}*/
		}
		return false;
	}
	
}
