package com.hybridplay.emptygame;

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
	private int mCurrentFrame = 0; // para comparar
	// the frame period
	private final static int    FRAME_PERIOD = 1000 / MAX_FPS;
	static final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8;
	public final static int 	READY = 0,RUNNING = 1, GAMEOVER = 2, WON = 3, DIE = 4;//, CONNECTING = 5;
	public final static String textOver = "GAME OVER", textCongrats = "You Won"
								, textBT = "CONNECTING TO HYBRIDPLAY SENSOR", textReady = "Ready Go";
	
	public float degree = 0;
	
	private SurfaceHolder surfaceHolder;
	private Thread surfaceThread = null;
	boolean isRunning = false;
	
	int currentFrame = 0; 	// for drawing sprite
	
	int movingTextX, movingTextY;   // for ready and gameover screen
	
	private Player player;
	private float playerSize;
	private float gameElementSize;
	
	private GameEngine gameEngine;
	private ArrayList<GameElements> gameElements;
	
	// bitmap
	private Bitmap player_img, gameElements_img, starts_img, columpio_fondo, nubes_fondo1, nubes_fondo2, nubes_fondo3;
	private Bitmap connecting;
	
	private Paint paint, paint2, paint3, paintBT;
	
	private Context mContext;
	
	//private int gameState;
	private Rect srcRect, srcConnecting, dstConnecting;
	public Rect dstRect;
	private Rect[] pSrcUp = new Rect[4];
	private Rect[] pSrcDown = new Rect[4];
	private Rect[] pSrcLeft = new Rect[4];
	private Rect[] pSrcRight = new Rect[4];
	
	private Rect[] trash_rect = new Rect[6];
	
	public Rect[] stage_rect = new Rect[6];
	
	private Rect src, dst;
	
	// draw timing data
	private long beginTime; // the time when the cycle begun
	private long timeDiff; // the time it took for the cycle to execute
	private int sleepTime; // ms to sleep (<0 if we're behind)
	public int framesSkipped; // number of frames being skipped

	private boolean isPlayOn;
	
	private float screenWidth;
	private float screenHeight;
	private float sentenceWidth, drawTextStartingX;
	
	public int counterForSprite=0;
	
	//////////////////////////////////SENSOR REFERENCE
	public Bitmap sUP_ON, sDOWN_ON, sLEFT_ON, sRIGHT_ON;
	public Bitmap sUP_OFF, sDOWN_OFF, sLEFT_OFF, sRIGHT_OFF;
	public Rect srcRect_UP, srcRect_DOWN, srcRect_LEFT, srcRect_RIGHT;
	public Rect dstRect_UP, dstRect_DOWN, dstRect_LEFT, dstRect_RIGHT;
	//////////////////////////////////SENSOR REFERENCE
	
	public GameSurfaceView(Context context) {
        super(context);
    }
	
	public GameSurfaceView(Context context, GameEngine gameEngine, int sWidth, int sHeight) {
		
		super(context);
		
		this.gameEngine = gameEngine;
		this.player = gameEngine.player;
		
		mContext = context;
		
		isPlayOn = true;
		
		screenWidth = sWidth;
		screenHeight = sHeight;
		
		playerSize = screenWidth / 4.f;  //kid size
		gameElementSize = screenWidth / 6.f;  //trash size

		initBitmap();  // init all Bitmap and its components
		initSprite();  // init spite
		
		gameElements = gameEngine.gameElements;
		
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
		
		gameElements_img = BitmapFactory.decodeResource(getResources(), R.drawable.trash);
		player_img = BitmapFactory.decodeResource(getResources(), R.drawable.kid1);
		starts_img = BitmapFactory.decodeResource(getResources(), R.drawable.stars_background);
		columpio_fondo = BitmapFactory.decodeResource(getResources(), R.drawable.fondocolumpiocactus);
		nubes_fondo1 = BitmapFactory.decodeResource(getResources(), R.drawable.fondo1);
		nubes_fondo2 = BitmapFactory.decodeResource(getResources(), R.drawable.fondo2);
		nubes_fondo3 = BitmapFactory.decodeResource(getResources(), R.drawable.fondo3);
		
		connecting = BitmapFactory.decodeResource(getResources(), R.drawable.connecting);
		
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.WHITE);
		paint.setTextSize((int)(playerSize/8));
		
		paint2 = new Paint();
		paint2.setAntiAlias(true);
		paint2.setColor(Color.WHITE);
		paint2.setTextSize(playerSize/4); 
		
		paint3 = new Paint();
		paint3.setAntiAlias(true);
		paint3.setColor(Color.WHITE);
		paint3.setTextSize(30);
		
		paintBT = new Paint();
		paintBT.setStyle(Paint.Style.STROKE);
		paintBT.setColor(Color.RED);
		
	}
	
	private void initSprite(){
		
		//hacer una funcion para crear el rect parametros, ancho, alto, filas, columnas		
		pSrcUp[0] = new Rect(0, 0, 128, 128);
		pSrcUp[1] = new Rect(128, 0, 256, 128);
		pSrcUp[2] = new Rect(256, 0, 384, 128);
		pSrcUp[3] = new Rect(384, 0, 512, 128);

		pSrcDown[0] = new Rect(0, 128, 128, 256);
		pSrcDown[1] = new Rect(128, 128, 256, 256);
		pSrcDown[2] = new Rect(256, 128, 384, 256);
		pSrcDown[3] = new Rect(384, 128, 512, 256);

		pSrcRight[0] = new Rect(0, 256, 128, 384);
		pSrcRight[1] = new Rect(128, 256, 256, 384);
		pSrcRight[2] = new Rect(256, 256, 384, 384);
		pSrcRight[3] = new Rect(384, 256, 512, 384);
		
		pSrcLeft[0] = new Rect(0, 384, 128, 512);
		pSrcLeft[1] = new Rect(128, 384, 256, 512);
		pSrcLeft[2] = new Rect(256, 384, 384, 512);
		pSrcLeft[3] = new Rect(384, 384, 512, 512);
		
		//imagen 384x256 con dos filas de tres imagenes
		trash_rect[0] = new Rect(0,0,128,128);
		trash_rect[1] = new Rect(128,0,256,128);
		trash_rect[2] = new Rect(256,0,384,128);
		trash_rect[3] = new Rect(0,128,128,256);
		trash_rect[4] = new Rect(128,128,256,256);
		trash_rect[5] = new Rect(384,128,512,256);

		if(gameEngine.getGameType().equals("Columpio") || gameEngine.getGameType().equals("Rueda")){ // oscila cactus
			src = new Rect(0,0, columpio_fondo.getWidth(), columpio_fondo.getHeight());
		}else if(gameEngine.getGameType().equals("Tobogan")){ // nubes
			src = new Rect(0,0, nubes_fondo1.getWidth(), nubes_fondo1.getHeight());
		}else{
			src = new Rect(0,0, starts_img.getWidth(), starts_img.getHeight());
		}
		
		dst = new Rect(0, 0, (int)screenWidth, (int)screenHeight);
		
		srcConnecting = new Rect(0, 0, connecting.getWidth(), connecting.getHeight() );
		dstConnecting = new Rect((int) screenWidth/2 - connecting.getWidth()/2,(int) screenHeight/2-connecting.getHeight()/2,(int) screenWidth/2 + connecting.getWidth()/2,(int) screenHeight/2+ connecting.getHeight()/2 );
		
		
	}
	
	//thread to update and draw. Game loop
	public void run() {
		Canvas canvas;
		
		while (isRunning) {
			canvas = null;
			if (gameEngine.getGameState() == READY){
				if (isPlayOn){
					isPlayOn = false;
				}
				updateReady(canvas);

			}
			if (gameEngine.getGameState() == RUNNING)  updateRunning(canvas);
			if (gameEngine.getGameState() == GAMEOVER) updateGameOver(canvas);
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
					canvas.drawRGB(20, 30, 75);
					
					drawStage(canvas);
					drawPlayer(canvas);
					drawGameElements(canvas);
					drawScore(canvas);
					drawConnection(canvas);
					drawSensor(canvas);
					
					//para dibujar los disparadores de eventos
					int borde = canvas.getWidth()/10;
					int distancia = canvas.getWidth()/8;
					
					int x1, y1, x2, y2;
					
					if(gameEngine.getGameType().equals("Balancin")){ // ---------------- Balancin
						// pinza horizontal - cuatro direcciones - ejes Z Y
						x1 = borde; y1 = borde+(distancia/2); //izquierda
						x2 = borde+distancia; y2 = borde+(distancia/2); //derecha
						//gameEngine.getmSensorZ().draw(canvas, paintBT, x2, y2, x1, y1);
						
						x1 = borde+(distancia/2); y1 = borde; //arriba
						x2 = borde+(distancia/2); y2 = borde+distancia; //abajo
						//gameEngine.getmSensorY().draw(canvas, paintBT, x1, y1, x2, y2);
						
					}else if(gameEngine.getGameType().equals("Caballito")){ // ---------- Caballito
						// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
						x1 = borde; y1 = borde+(distancia/2); //izquierda
						x2 = borde+distancia; y2 = borde+(distancia/2); //derecha
						//gameEngine.getmSensorX().draw(canvas, paintBT, x2, y2, x1, y1);
						
						x1 = borde+(distancia/2); y1 = borde; //arriba
						x2 = borde+(distancia/2); y2 = borde+distancia; //abajo
						//gameEngine.getmSensorY().draw(canvas, paintBT, x1, y1, x2, y2);
						
					}else if(gameEngine.getGameType().equals("Columpio")){ // ---------- Columpio
						// pinza vertical boton hacia abajo - oscilaci�n
						
					}else if(gameEngine.getGameType().equals("Rueda")){ // ---------- Rueda
						// pinza vertical boton hacia abajo - oscilaci�n
						
					}else if(gameEngine.getGameType().equals("SubeBaja")){ // ---------- SubeBaja
						// pinza horizontal - dos direcciones - eje Z
						x1 = borde; y1 = borde+(distancia/2); //izquierda
						x2 = borde+distancia; y2 = borde+(distancia/2); //derecha
						//gameEngine.getmSensorZ().draw(canvas, paintBT, x2, y2, x1, y1);
						
					}else if(gameEngine.getGameType().equals("Tobogan")){ // ---------- Tobogan
						// we use here only IR sensor
					}
					
					
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
				
					canvas.drawRGB(20, 30, 75);
					drawStage(canvas); // draw updated maze
					drawPlayer(canvas); // draw Kid
					drawGameElements(canvas);
					drawScore(canvas); // draw score and lives
					drawSensor(canvas);
					
					int borde = canvas.getWidth()/10;
					int distancia = canvas.getWidth()/8;
					
					int x1, y1, x2, y2;

					if(gameEngine.getGameType().equals("Balancin")){ // ---------------- Balancin
						// pinza horizontal - cuatro direcciones - ejes Z Y
						x1 = borde; y1 = borde+(distancia/2); //izquierda
						x2 = borde+distancia; y2 = borde+(distancia/2); //derecha
						//gameEngine.getmSensorZ().draw(canvas, paintBT, x2, y2, x1, y1);
						
						x1 = borde+(distancia/2); y1 = borde; //arriba
						x2 = borde+(distancia/2); y2 = borde+distancia; //abajo
						//gameEngine.getmSensorY().draw(canvas, paintBT, x1, y1, x2, y2);
						
					}else if(gameEngine.getGameType().equals("Caballito")){ // ---------- Caballito
						// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
						x1 = borde; y1 = borde+(distancia/2); //izquierda
						x2 = borde+distancia; y2 = borde+(distancia/2); //derecha
						//gameEngine.getmSensorX().draw(canvas, paintBT, x2, y2, x1, y1);
						
						x1 = borde+(distancia/2); y1 = borde; //arriba
						x2 = borde+(distancia/2); y2 = borde+distancia; //abajo
						//gameEngine.getmSensorY().draw(canvas, paintBT, x1, y1, x2, y2);
						
					}else if(gameEngine.getGameType().equals("Columpio")){ // ---------- Columpio
						// pinza vertical boton hacia abajo - oscilaci�n
						
					}else if(gameEngine.getGameType().equals("Rueda")){ // ---------- Rueda
						// pinza vertical boton hacia abajo - oscilaci�n
						
					}else if(gameEngine.getGameType().equals("SubeBaja")){ // ---------- SubeBaja
						// pinza horizontal - dos direcciones - eje Z
						x1 = borde; y1 = borde+(distancia/2); //izquierda
						x2 = borde+distancia; y2 = borde+(distancia/2); //derecha
						//gameEngine.getmSensorZ().draw(canvas, paintBT, x2, y2, x1, y1);
						
					}else if(gameEngine.getGameType().equals("Tobogan")){ // ---------- Tobogan
						// we use here only IR sensor
					}
					
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


	private void updateGameOver(Canvas canvas){
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
					isRunning = false;
					canvas.drawRGB(20, 30, 75);
					drawStage(canvas); // draw updated maze
					drawPlayer(canvas);
					drawGameElements(canvas);
					drawScore(canvas);
					drawSensor(canvas);
					
					//para dibujar los disparadores de eventos
					int borde = canvas.getWidth()/10;
					int distancia = canvas.getWidth()/8;
					
					int x1, y1, x2, y2;

					if(gameEngine.getGameType().equals("Balancin")){ // ---------------- Balancin
						// pinza horizontal - cuatro direcciones - ejes Z Y
						x1 = borde; y1 = borde+(distancia/2); //izquierda
						x2 = borde+distancia; y2 = borde+(distancia/2); //derecha
						//gameEngine.getmSensorZ().draw(canvas, paintBT, x2, y2, x1, y1);
						
						x1 = borde+(distancia/2); y1 = borde; //arriba
						x2 = borde+(distancia/2); y2 = borde+distancia; //abajo
						//gameEngine.getmSensorY().draw(canvas, paintBT, x1, y1, x2, y2);
						
					}else if(gameEngine.getGameType().equals("Caballito")){ // ---------- Caballito
						// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
						x1 = borde; y1 = borde+(distancia/2); //izquierda
						x2 = borde+distancia; y2 = borde+(distancia/2); //derecha
						//gameEngine.getmSensorX().draw(canvas, paintBT, x2, y2, x1, y1);
						
						x1 = borde+(distancia/2); y1 = borde; //arriba
						x2 = borde+(distancia/2); y2 = borde+distancia; //abajo
						//gameEngine.getmSensorY().draw(canvas, paintBT, x1, y1, x2, y2);
						
					}else if(gameEngine.getGameType().equals("Columpio")){ // ---------- Columpio
						// pinza vertical boton hacia abajo - oscilaci�n - eje X
						
					}else if(gameEngine.getGameType().equals("Rueda")){ // ---------- Rueda
						// pinza vertical boton hacia abajo - oscilaci�n - eje X
						
					}else if(gameEngine.getGameType().equals("SubeBaja")){ // ---------- SubeBaja
						// pinza horizontal - dos direcciones - eje Z
						x1 = borde; y1 = borde+(distancia/2); //izquierda
						x2 = borde+distancia; y2 = borde+(distancia/2); //derecha
						//gameEngine.getmSensorZ().draw(canvas, paintBT, x2, y2, x1, y1);
						
					}else if(gameEngine.getGameType().equals("Tobogan")){ // ---------- Tobogan
						// we use here only IR sensor
					}
					
					//measure the text then draw it at center
					sentenceWidth = paint2.measureText(textReady);
				    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
					canvas.drawText(textOver, drawTextStartingX , screenHeight/2, paint2);
					
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
				
		
		
		try {
			Thread.sleep(4000);
			((Activity) mContext).finish(); //terminamos el juego
		} catch (InterruptedException e) {

			e.printStackTrace();
		}
		
	}
		

	// draw current location of ghosts
	private void drawGameElements(Canvas canvas) {
		for (int i = 0; i < gameEngine.gameElements.size(); i++) {
			if (gameEngine.gameElements.get(i).isActive()){
				
				srcRect = trash_rect[i];
				
				int gX = gameElements.get(i).getX();
				int gY = gameElements.get(i).getY();

				Rect dst = new Rect(gX, gY, gX + (int) gameElementSize, gY + (int) gameElementSize);

				canvas.drawBitmap(gameElements_img, srcRect, dst, null);

			}
		}
	}

	// draw kid 
	private void drawPlayer(Canvas canvas) {
	
		int direction = player.getDir(); // get current direction of the kid
		
		//si no esta comiendo en ninguna direccion
	
		if (!player.isEating()){
			if (direction == UP)	srcRect = pSrcUp[currentFrame];
			else if (direction == DOWN)		srcRect = pSrcDown[currentFrame];
			else if (direction == RIGHT)		srcRect = pSrcRight[currentFrame];
			else if (direction == LEFT)		srcRect = pSrcLeft[currentFrame];	
			else srcRect = pSrcRight[currentFrame]; //falta del dibujo del centro no movimiento
		}
		
		if(player.isEatDown()){ 
			boolean anim = animEating(pSrcDown);
			if (anim) player.setEatDown(false); player.setEating(false);
		}
		if(player.isEatleft()){
			boolean anim = animEating(pSrcLeft);
			if (anim) player.setEatleft(false); player.setEating(false);
		}
		if(player.isEatRight()) {
			boolean anim = animEating(pSrcRight);
			if (anim) player.setEatRight(false); player.setEating(false);
		}
		if(player.isEatUp()) {
			boolean anim = animEating(pSrcUp);
			if (anim) player.setEatUp(false); player.setEating(false);
		}
						
		Rect dst = new Rect(player.getpX(), player.getpY(), player.getpX()+ (int) playerSize, player.getpY() + (int) playerSize);
		
		canvas.drawBitmap(player_img, srcRect, dst, null);
				
	}
	
	boolean animEating(Rect[] rect){
		
		if(mCurrentFrame == 0) {				
			mCurrentFrame ++;
			if(currentFrame < rect.length-1){//maximo 2+1 = 3
				currentFrame ++;
				srcRect = rect[currentFrame];
			}else{
				currentFrame = 0;
				return true;
			}			
		}else if (mCurrentFrame < MAX_FRAME_SKIPS){
			mCurrentFrame ++;
			srcRect = rect[currentFrame];
		}else if (mCurrentFrame >= MAX_FRAME_SKIPS){
			mCurrentFrame = 0;
		}
		return false;
				
	}
	
	
	// draw score
	public void drawScore(Canvas canvas){
		//canvas.drawText(Integer.toString(gameEngine.getLives()), 40, kidSize * 23, paint);
		int widthDraw = (int) (screenWidth - screenWidth/5);
		int heightDraw = (int) (screenHeight/8); 
		canvas.drawText("SCORE " + Integer.toString(gameEngine.getPlayerScore()), widthDraw, heightDraw , paint);
		canvas.drawText("TIME LEFT "+ Integer.toString(gameEngine.getTimer()), widthDraw, heightDraw + 25, paint);
		
	}
	
	//draw sensors
	public void drawBTSensor(Canvas canvas){
		//int h = canvas.getHeight(); //alto
		int w = canvas.getWidth(); //ancho
		float radius = w/40f;
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
	public void drawStage(Canvas canvas){
		if(gameEngine.getGameType().equals("Columpio") || gameEngine.getGameType().equals("Rueda")){ // oscila cactus
			canvas.drawBitmap(columpio_fondo, src, dst, null);
		}else if(gameEngine.getGameType().equals("Tobogan")){ // nubes
			if(gameEngine.toboganLevel == 0){
				canvas.drawBitmap(nubes_fondo1, src, dst, null);
			}else if(gameEngine.toboganLevel == 1){
				canvas.drawBitmap(nubes_fondo2, src, dst, null);
			}else if(gameEngine.toboganLevel == 2){
				canvas.drawBitmap(nubes_fondo3, src, dst, null);
			}
		}else{
			canvas.drawBitmap(starts_img, src, dst, null);
		}
	}
	
	public void drawConnection(Canvas canvas){
		canvas.drawBitmap(connecting, srcConnecting, dstConnecting, null);
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


