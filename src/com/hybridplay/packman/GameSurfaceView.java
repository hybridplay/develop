package com.hybridplay.packman;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hybridplay.app.R;

//deals with rendering the game data
public class GameSurfaceView extends SurfaceView implements Runnable {

	private final static int    MAX_FPS = 50;
	// maximum number of frames to be skipped
	private final static int    MAX_FRAME_SKIPS = 5;
	// the frame period
	private final static int    FRAME_PERIOD = 1000 / MAX_FPS;
	static final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8;
	private final static int 	CONNECTING = -1, READY = 0,RUNNING = 1, GAMEOVER = 2, WON = 3, DIE = 4;
	private final static String textOver = "GAME OVER", textCongrats = "You Won"
								, textNextLevel = "You unlocked next level", textReady = "Ready Go";
	
	
	private SurfaceHolder surfaceHolder;
	private Thread surfaceThread = null;
	boolean isRunning = false;
	
	int currentFrame = 0; 	// for drawing sprite
	int mCurrentFrame = 0;
	int movingTextX, movingTextY;   // for ready and gameover screen
	
	private Pacmon pacmon;
	
	private GameEngine gameEngine;
	private ArrayList<Monster> ghosts;
	
	// bitmap
	private Bitmap pac_img, wall, door, bluey_img, redy_img, yellowy_img, food, power ;
	
	//maze info
	private int[][] mazeArray;
	private int mazeRow, mazeColumn;
	private float blockSize;
	
	private Maze maze;
	
	private Paint paint, paint2, paint3, paintBT;
	
	private Context mContext;
	
	private int gameState;
	private Rect srcRect;
	private Rect dstRect;
	private Rect[] pSrcUp = new Rect[3];
	private Rect[] pSrcDown = new Rect[3];
	private Rect[] pSrcLeft = new Rect[3];
	private Rect[] pSrcRight = new Rect[3];
	private Rect[] pDst = new Rect[12];
	
	private Rect[] gSrcUp = new Rect[2];
	private Rect[] gSrcDown = new Rect[2];
	private Rect[] gSrcLeft = new Rect[2];
	private Rect[] gSrcRight = new Rect[2];
	private Rect[] gDst = new Rect[8];
	
	private Bitmap connecting;
	private Rect srcConnecting, dstConnecting;
	
	// draw timing data
	private long beginTime; // the time when the cycle begun
	private long timeDiff; // the time it took for the cycle to execute
	private int sleepTime; // ms to sleep (<0 if we're behind)
	private int framesSkipped; // number of frames being skipped

	private SoundEngine soundEngine; // sound manager
	private boolean isPlayOn;
	
	private float screenWidth;
	private float screenHeight;
	private float blockScaleFactor;
	private float sentenceWidth, drawTextStartingX;
	
	private int counterForSprite=0;
	
	//////////////////////////////////SENSOR REFERENCE
	public Bitmap sUP_ON, sDOWN_ON, sLEFT_ON, sRIGHT_ON;
	public Bitmap sUP_OFF, sDOWN_OFF, sLEFT_OFF, sRIGHT_OFF;
	public Rect srcRect_UP, srcRect_DOWN, srcRect_LEFT, srcRect_RIGHT;
	public Rect dstRect_UP, dstRect_DOWN, dstRect_LEFT, dstRect_RIGHT;
	//////////////////////////////////SENSOR REFERENCE
	
	public GameSurfaceView(Context context, GameEngine gameEngine, int sWidth, int sHeight) {
		
		super(context);
		
		this.gameEngine = gameEngine;
		this.pacmon = gameEngine.pacmon;
		gameState = CONNECTING;
		
		mContext = context;
		
		soundEngine = new SoundEngine(context);
		isPlayOn = true;
		
		screenWidth = sWidth;
		screenHeight = sHeight;
		
		blockSize = screenWidth / 15.f;  // size of block
		blockScaleFactor = blockSize / 32.f;  // scale factor for block
		
		maze = gameEngine.getMaze();
		mazeArray = gameEngine.getMazeArray();
		mazeRow = maze.getMazeRow();
		mazeColumn = maze.getMazeColumn();

		initBitmap();  // init all Bitmap and its components
		initSprite();  // init spite
		
		ghosts = gameEngine.ghosts;
		
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
	
	private void initBitmap(){
		// SENSOR
		initSensorGraphics();
		wall = BitmapFactory.decodeResource(getResources(), R.drawable.wall);
		door = BitmapFactory.decodeResource(getResources(), R.drawable.ghost_door);
		food = BitmapFactory.decodeResource(getResources(), R.drawable.food);
		power = BitmapFactory.decodeResource(getResources(), R.drawable.power);
		pac_img = BitmapFactory.decodeResource(getResources(), R.drawable.pacmon_sprite_green);
		bluey_img = BitmapFactory.decodeResource(getResources(), R.drawable.bluey_sprite);
		redy_img = BitmapFactory.decodeResource(getResources(), R.drawable.redy_sprite);
		yellowy_img = BitmapFactory.decodeResource(getResources(), R.drawable.yellowy_sprite);
		
		connecting = BitmapFactory.decodeResource(getResources(), R.drawable.connecting);
		
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		paint.setTextSize((int)(blockSize/(1.5)));
		
		paint2 = new Paint();
		paint2.setAntiAlias(true);
		paint2.setColor(Color.WHITE);
		paint2.setTextSize(blockSize*2); // 2 times the size of block width
		
		paint3 = new Paint();
		paint3.setAntiAlias(true);
		paint3.setColor(Color.WHITE);
		paint3.setTextSize(30);
		
		paintBT = new Paint();
		paintBT.setStyle(Paint.Style.STROKE);
		paintBT.setColor(Color.RED);
		
	}
	
	private void initSprite(){
		
		srcConnecting = new Rect(0, 0, connecting.getWidth(), connecting.getHeight() );
		dstConnecting = new Rect((int) screenWidth/2 - connecting.getWidth()/2,(int) screenHeight/2-connecting.getHeight()/2,(int) screenWidth/2 + connecting.getWidth()/2,(int) screenHeight/2+ connecting.getHeight()/2 );
		
		
		pSrcUp[0] = new Rect(0, 0, 32, 32);
		pSrcUp[1] = new Rect(32, 0, 64, 32);
		pSrcUp[2] = new Rect(64, 0, 96, 32);
		
		pSrcDown[0] = new Rect(0, 32, 32, 64);
		pSrcDown[1] = new Rect(32, 32, 64, 64);
		pSrcDown[2] = new Rect(64, 32, 96, 64);
		
		pSrcRight[0] = new Rect(0, 64, 32, 96);
		pSrcRight[1] = new Rect(32, 64, 64, 96);
		pSrcRight[2] = new Rect(64, 64, 96, 96);
		
		pSrcLeft[0] = new Rect(0, 96, 32, 128);
		pSrcLeft[1] = new Rect(32, 96, 64, 128);
		pSrcLeft[2] = new Rect(64, 96, 96, 128);
		
		gSrcUp[0] = new Rect(0, 0, 32, 32);
		gSrcUp[1] = new Rect(32, 0, 64, 32);
		
		gSrcDown[0] = new Rect(0, 32, 32, 64);
		gSrcDown[1] = new Rect(32, 32, 64, 64);
		
		gSrcRight[0] = new Rect(0, 64, 32, 96);
		gSrcRight[1] = new Rect(32, 64, 64, 96);
		
		gSrcLeft[0] = new Rect(0, 96, 32, 128);
		gSrcLeft[1] = new Rect(32, 96, 64, 128);
		
	}
	
	//thread to update and draw. Game loop
	public void run() {
		Canvas canvas;
		
		while (isRunning) {
			canvas = null;
			if (gameEngine.getGameState() == CONNECTING){
				updateConnecting(canvas);
			}
			if (gameEngine.getGameState() == READY){
				if (isPlayOn){
				//	soundEngine.play(4);
					isPlayOn = false;
				}
				updateReady(canvas);

			}
			if (gameEngine.getGameState() == RUNNING)  updateRunning(canvas);
			if (gameEngine.getGameState() == GAMEOVER) updateGameOver(canvas);
			if (gameEngine.getGameState() == WON)	   updateWon(canvas);
			if (gameEngine.getGameState() == DIE)	   updateDie(canvas);
		}
	}
	
	private void updateConnecting(Canvas canvas){
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
					canvas.drawRGB(0, 0, 0);
					drawMaze(canvas); // draw updated maze
					drawPacmon(canvas);
					drawGhost(canvas);
					drawScore(canvas);
					drawSensor(canvas);
					
					int borde = canvas.getWidth()/10;
					int distancia = canvas.getWidth()/8;
					
					int x1, y1, x2, y2;

					x1 = borde; y1 = borde+(distancia/2); //izquierda
					x2 = borde+distancia; y2 = borde+(distancia/2); //derecha
					//gameEngine.getmSensorX().draw(canvas, paintBT, x2, y2, x1, y1);
					
					x1 = borde+(distancia/2); y1 = borde; //arriba
					x2 = borde+(distancia/2); y2 = borde+distancia; //abajo
					//gameEngine.getmSensorY().draw(canvas, paintBT, x1, y1, x2, y2);
					
					
					//long time = 5L - timeDiff/1000;

					canvas.drawBitmap(connecting, srcConnecting, dstConnecting, null);
					
					try {
						Thread.sleep(25);
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
	
	// when game is in ready mode
	private void updateReady(Canvas canvas){
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
					canvas.drawRGB(0, 0, 0);
					drawMaze(canvas); // draw updated maze
					drawPacmon(canvas);
					drawGhost(canvas);
					drawScore(canvas);
					drawSensor(canvas);
					
					int borde = canvas.getWidth()/10;
					int distancia = canvas.getWidth()/8;
					
					int x1, y1, x2, y2;

					x1 = borde; y1 = borde+(distancia/2); //izquierda
					x2 = borde+distancia; y2 = borde+(distancia/2); //derecha
					//gameEngine.getmSensorX().draw(canvas, paintBT, x2, y2, x1, y1);
					
					x1 = borde+(distancia/2); y1 = borde; //arriba
					x2 = borde+(distancia/2); y2 = borde+distancia; //abajo
					//gameEngine.getmSensorY().draw(canvas, paintBT, x1, y1, x2, y2);
					
					
					//long time = 5L - timeDiff/1000;

					//measure the text then draw it at center
					sentenceWidth = paint2.measureText(textReady);
					
				    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
					canvas.drawText(textReady, drawTextStartingX , screenHeight/2, paint2);
					
					try {
						Thread.sleep(25);
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
	
	private void updateRunning(Canvas canvas){
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
					beginTime = System.currentTimeMillis();
					framesSkipped = 0; // resetting the frames skipped
				
					canvas.drawRGB(0, 0, 0);
					drawMaze(canvas); // draw updated maze
					drawPacmon(canvas); // draw Pacman
					drawGhost(canvas); // draw ghosts
					drawScore(canvas); // draw score and lives
					drawSensor(canvas);
					
					int borde = canvas.getWidth()/10;
					int distancia = canvas.getWidth()/8;
					
					int x1, y1, x2, y2;

					x1 = borde; y1 = borde+(distancia/2); //izquierda
					x2 = borde+distancia; y2 = borde+(distancia/2); //derecha
					//gameEngine.getmSensorX().draw(canvas, paintBT, x2, y2, x1, y1);
					
					x1 = borde+(distancia/2); y1 = borde; //arriba
					x2 = borde+(distancia/2); y2 = borde+distancia; //abajo
					//gameEngine.getmSensorY().draw(canvas, paintBT, x1, y1, x2, y2);
					
					
					
					// calculate how long did the cycle take
					timeDiff = System.currentTimeMillis() - beginTime;
					// calculate sleep time
					sleepTime = (int) (FRAME_PERIOD - timeDiff);

					if (sleepTime > 0) {
						// if sleepTime > 0 we're OK
						try {
							// send the thread to sleep for a short period
							// very useful for battery saving
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
						}
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
	
	//dead animation
	private void updateDie(Canvas canvas){
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
					canvas.drawRGB(0, 0, 0);
					drawMaze(canvas); // draw updated maze
					drawPacmon(canvas);
					drawGhost(canvas);
					drawScore(canvas);
					drawSensor(canvas);
					
					int borde = canvas.getWidth()/10;
					int distancia = canvas.getWidth()/8;
					
					int x1, y1, x2, y2;

					x1 = borde; y1 = borde+(distancia/2); //izquierda
					x2 = borde+distancia; y2 = borde+(distancia/2); //derecha
					//gameEngine.getmSensorX().draw(canvas, paintBT, x2, y2, x1, y1);
					
					x1 = borde+(distancia/2); y1 = borde; //arriba
					x2 = borde+(distancia/2); y2 = borde+distancia; //abajo
					//gameEngine.getmSensorY().draw(canvas, paintBT, x1, y1, x2, y2);
					
					try {
						Thread.sleep(25);
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
	
	private void updateGameOver(Canvas canvas){
		canvas = surfaceHolder.lockCanvas();
		isRunning = false;
		
		//measure the text then draw it at center
		sentenceWidth = paint2.measureText(textOver);
	    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
		canvas.drawText(textOver, drawTextStartingX , screenHeight/2 - blockSize*2, paint2);
		
		sentenceWidth = paint2.measureText(gameEngine.getPlayerScore());
	    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
		canvas.drawText(gameEngine.getPlayerScore(), drawTextStartingX, screenHeight/2 + blockSize*2, paint2);
		
		surfaceHolder.unlockCanvasAndPost(canvas);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		
		((Activity) mContext).finish();
	}
	
	private void updateWon(Canvas canvas){
		canvas = surfaceHolder.lockCanvas();
		isRunning = false;
		
		//measure the text then draw it at center
		sentenceWidth = paint2.measureText(textCongrats);
	    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
		canvas.drawText(textCongrats, drawTextStartingX , screenHeight/2 - blockSize*2, paint2);
		
//		sentenceWidth = paint2.measureText(textNextLevel);
//	    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
//		canvas.drawText(textNextLevel, drawTextStartingX, screenHeight/2 , paint2);
		
		sentenceWidth = paint2.measureText(gameEngine.getPlayerScore());
	    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
		canvas.drawText(gameEngine.getPlayerScore(), drawTextStartingX, screenHeight/2, paint2);
		
		surfaceHolder.unlockCanvasAndPost(canvas);
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		
		
		((Activity) mContext).finish();
	}
	

	// draw current location of ghosts
	private void drawGhost(Canvas canvas) {
		mCurrentFrame = ++mCurrentFrame % 2;
		for (int i = 0; i < gameEngine.ghosts.size(); i++) {
			int direction = ghosts.get(i).getDir();

			if (direction == UP)	srcRect = gSrcUp[mCurrentFrame];
			else if (direction == DOWN)		srcRect = gSrcDown[mCurrentFrame];
			else if (direction == RIGHT)		srcRect = gSrcRight[mCurrentFrame];
			else 	srcRect = gSrcLeft[mCurrentFrame];	
			
			int gX = Math.round(ghosts.get(i).getX() * blockScaleFactor);
			int gY = Math.round(ghosts.get(i).getY() * blockScaleFactor);
			
			Rect dst = new Rect(gX, gY, (int)(gX + blockSize), (int) (gY + blockSize));
				
			if (i == 0)
				canvas.drawBitmap(bluey_img, srcRect, dst, null);
			else if (i == 1)
				canvas.drawBitmap(redy_img, srcRect, dst, null);
			else if (i == 2)
				canvas.drawBitmap(yellowy_img, srcRect, dst, null);
		}
	}

	// draw pacmon 
	private void drawPacmon(Canvas canvas) {
		
		if(counterForSprite>90)
			counterForSprite=0;
		
		counterForSprite++;
		
		if(counterForSprite%6==0)
		{
			currentFrame = ++currentFrame % 3;
		}
		
		
		//currentFrame = ++currentFrame % 3;
		
		int direction = pacmon.getDir(); // get current direction of pacmon
		
		if (direction == UP)	srcRect = pSrcUp[currentFrame];
		else if (direction == DOWN)		srcRect = pSrcDown[currentFrame];
		else if (direction == RIGHT)		srcRect = pSrcRight[currentFrame];
		else 	srcRect = pSrcLeft[currentFrame];	
	
		int pX = Math.round(pacmon.getpX() * blockScaleFactor);
		int pY = Math.round(pacmon.getpY() * blockScaleFactor);

		Rect dst = new Rect(pX, pY, (int)(pX + blockSize), (int) (pY + blockSize));
		canvas.drawBitmap(pac_img, srcRect, dst, null);
		
	}
	
	// draw score
	public void drawScore(Canvas canvas){
		canvas.drawText(gameEngine.getLives(), 40, blockSize * 23, paint);
		
		canvas.drawText(gameEngine.getPlayerScore(), 40, blockSize * 24 , paint);
		canvas.drawText(gameEngine.getTimer(), screenWidth/2, blockSize * 23, paint);
	}
	
	//draw sensors
	public void drawBTSensor(Canvas canvas){
		int h = canvas.getHeight(); //alto
		int w = canvas.getWidth(); //ancho
		float radius = w/30f;
		int borde = w/10;
		int distancia = w/8;
		canvas.drawCircle(borde+(distancia/2), borde, radius , paintBT);//arriba
		canvas.drawCircle(borde+(distancia/2), borde+distancia, radius , paintBT);//abajo
		canvas.drawCircle(borde, borde+(distancia/2), radius , paintBT);//izquierda
		canvas.drawCircle(borde+distancia, borde+(distancia/2), radius , paintBT);//derecha
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
	
	
	@Override
	public void draw(Canvas canvas) {

	}
	
	// draw current maze with food
	public void drawMaze(Canvas canvas){
		for (int i = 0; i < mazeRow; i++){
			for (int j = 0; j < mazeColumn; j++){
				if (mazeArray[i][j] == 0)
					canvas.drawBitmap(wall, j*blockSize, i*blockSize, null);
				if (mazeArray[i][j] == 3)
					canvas.drawBitmap(door, j*blockSize, i*blockSize, null);
				if (mazeArray[i][j] == 1)
					canvas.drawBitmap(food, j*blockSize, i*blockSize, null);
				if (mazeArray[i][j] == 2)
					canvas.drawBitmap(power, j*blockSize, i*blockSize, null);
			}
		}
	}
	
	private void drawSensor(Canvas canvas){
		if(gameEngine.getGameType().equals("Columpio")){
			// pinza vertical boton hacia abajo - oscilacion - eje Z
			if(gameEngine.triggerZR){ // LEFT
				canvas.drawBitmap(sLEFT_ON, srcRect_LEFT, dstRect_LEFT, null);
			}else{
				canvas.drawBitmap(sLEFT_OFF, srcRect_LEFT, dstRect_LEFT, null);
			}
			
			if(gameEngine.triggerZL){ // RIGHT
				canvas.drawBitmap(sRIGHT_ON, srcRect_RIGHT, dstRect_RIGHT, null);
			}else{
				canvas.drawBitmap(sRIGHT_OFF, srcRect_RIGHT, dstRect_RIGHT, null);
			}
		}else if(gameEngine.getGameType().equals("Tobogan")){
			// we use here only IR sensor
			
		}else if(gameEngine.getGameType().equals("SubeBaja")){
			// pinza horizontal - dos direcciones - eje Z
			if(gameEngine.triggerZR){ // UP
				canvas.drawBitmap(sUP_ON, srcRect_UP, dstRect_UP, null);
			}else{
				canvas.drawBitmap(sUP_OFF, srcRect_UP, dstRect_UP, null);
			}
			
			if(gameEngine.triggerZL){ // DOWN
				canvas.drawBitmap(sDOWN_ON, srcRect_DOWN, dstRect_DOWN, null);
			}else{
				canvas.drawBitmap(sDOWN_OFF, srcRect_DOWN, dstRect_DOWN, null);
			}
		}else if(gameEngine.getGameType().equals("Balancin")){
			// pinza horizontal - cuatro direcciones - ejes Z Y
			if(gameEngine.triggerYR){ // UP
				canvas.drawBitmap(sUP_ON, srcRect_UP, dstRect_UP, null);
			}else{
				canvas.drawBitmap(sUP_OFF, srcRect_UP, dstRect_UP, null);
			}
			
			if(gameEngine.triggerYL){ // DOWN
				canvas.drawBitmap(sDOWN_ON, srcRect_DOWN, dstRect_DOWN, null);
			}else{
				canvas.drawBitmap(sDOWN_OFF, srcRect_DOWN, dstRect_DOWN, null);
			}
			
			if(gameEngine.triggerZR){ // LEFT
				canvas.drawBitmap(sLEFT_ON, srcRect_LEFT, dstRect_LEFT, null);
			}else{
				canvas.drawBitmap(sLEFT_OFF, srcRect_LEFT, dstRect_LEFT, null);
			}
			if(gameEngine.triggerZL){ // RIGHT
				canvas.drawBitmap(sRIGHT_ON, srcRect_RIGHT, dstRect_RIGHT, null);
			}else{
				canvas.drawBitmap(sRIGHT_OFF, srcRect_RIGHT, dstRect_RIGHT, null);
			}
		}else if(gameEngine.getGameType().equals("Caballito")){
			// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
			
			// --------> CAMBIAR LA Y POR LA Z SI LA PINZA VA EN LA CABEZA DEL CABALLITO
			
			
			if(gameEngine.triggerYR){ // UP
				canvas.drawBitmap(sUP_ON, srcRect_UP, dstRect_UP, null);
			}else{
				canvas.drawBitmap(sUP_OFF, srcRect_UP, dstRect_UP, null);
			}
			
			if(gameEngine.triggerYL){ // DOWN
				canvas.drawBitmap(sDOWN_ON, srcRect_DOWN, dstRect_DOWN, null);
			}else{
				canvas.drawBitmap(sDOWN_OFF, srcRect_DOWN, dstRect_DOWN, null);
			}
			
			if(gameEngine.triggerXR){ // LEFT
				canvas.drawBitmap(sLEFT_ON, srcRect_LEFT, dstRect_LEFT, null);
			}else{
				canvas.drawBitmap(sLEFT_OFF, srcRect_LEFT, dstRect_LEFT, null);
			}
			if(gameEngine.triggerXL){ // RIGHT
				canvas.drawBitmap(sRIGHT_ON, srcRect_RIGHT, dstRect_RIGHT, null);
			}else{
				canvas.drawBitmap(sRIGHT_OFF, srcRect_RIGHT, dstRect_RIGHT, null);
			}
		}
	}
}


