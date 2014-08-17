package com.hybridplay.puzzlecity;

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

public class GameSurfaceView extends SurfaceView implements Runnable {
	
	private final static int    MAX_FPS = 50;
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
	
	private GameEngine gameEngine;
	private Player player;
	private Stage stage;
	private float playerSize, playerSize1;
	
	public Bitmap kid_img, plataforma, columpio_fondo, nubes_fondo1, nubes_fondo2, nubes_fondo3;
	public Bitmap bola_img, bomba_img, boom_img, cactus_img;
	public Bitmap ficha1, ficha2, ficha3, ficha4, ficha5, ficha6, ficha7, ficha8, ficha9, ficha10;
	public Bitmap cazamariposas_L, cazamariposas_R;
	public Bitmap nube1, nube2, nube3, nube4, vuelve;
	public Bitmap connecting, playerImg;
	public Bitmap laberinto_fondo, laberinto_mascara, laberinto_fondo1, laberinto_mascara1;

	//public Bitmap avionBitmap;
	public Bitmap avion_fondo1, avion_fondo2, avion_fondo3;
	public Paint paint, paint2, paint3, paint4, paintBT;
	private Context mContext;
	
	public int xFondo; // para mover el fondo
	
	public Nube nube;
	public Objetos obj;
	public Avion avion;
	public Fichas fichas;
	private ArrayList<Fichas> fichasArray;
	
	//private Rect avionRect;
	private Rect srcRect,  srcConnecting, dstConnecting;
	public Rect dstRect;
	public Rect[] srcKid = new Rect[8];
	public Rect plataformaRect, nubeRect, pDst;
	
	private Rect src, dst, dst1;
	
	// draw timing data
	private long beginTime; // the time when the cycle begun
	private long timeDiff; // the time it took for the cycle to execute
	private int sleepTime; // ms to sleep (<0 if we're behind)
	public int framesSkipped; // number of frames being skipped

	private boolean isPlayOn;

	private int screenWidth;
	private int screenHeight;
	private float sentenceWidth, drawTextStartingX;

	public int counterForSprite=0;
	
	public int nFondoAvioneta =0;
	
	public GameSurfaceView(Context context) {
        super(context);
    }
	
	public GameSurfaceView(Context context, GameEngine gameEngine, int sWidth, int sHeight) {
		
		super(context);
		
		this.gameEngine = gameEngine;
		//this.player = gameEngine.player;
		//this.stage = gameEngine.stage;
		
		mContext = context;
		
		isPlayOn = true;
		
		screenWidth = sWidth;
		screenHeight = sHeight;
		
		playerSize = screenWidth / 6.f;  //kid size
		playerSize1 = screenWidth / 9.f;  //kid size for laberinto

		initBitmap();  // init all Bitmap and its components
		initSprite();  // init sprite
		
		avion = new Avion(context,(int)screenWidth,(int)screenHeight);
		gameEngine.avion = avion;
		player = new Player(context, playerImg, (int)screenWidth/2,(int)screenHeight/2, screenWidth,screenHeight, 10, 5, 7);
		gameEngine.player = player;
		nube = new Nube(context,(int)screenWidth,(int)screenHeight);
		gameEngine.nube = nube;
		obj = new Objetos(context,(int)screenWidth,(int)screenHeight);
		gameEngine.obj = obj;
		
		if (gameEngine.getGameType().equals("Balancin")){
			stage = new Stage(context, laberinto_fondo, laberinto_mascara, 100, 130, screenWidth, screenHeight);
			gameEngine.stage = stage;
		}
		
		if (gameEngine.getGameType().equals("Caballito")){
			stage = new Stage(context, laberinto_fondo1, laberinto_mascara1, 100, 130, screenWidth, screenHeight);
			gameEngine.stage = stage;
		}

		   //creamos el array de basura y a�adimos 3 objetos
		if (gameEngine.getGameType().equals("Caballito")||gameEngine.getGameType().equals("Balancin")){
			fichasArray = new ArrayList<Fichas>();
			for (int i = 0; i < 10; i++) {
				fichasArray.add(new Fichas(context, gameEngine, (int)screenWidth,(int)screenHeight,gameEngine.getGameType()));
			}
			gameEngine.fichasArray = fichasArray;
		}
		
		if(gameEngine.getGameType().equals("SubeBaja")){
			fichas = new Fichas(context, gameEngine, (int)screenWidth,(int)screenHeight,gameEngine.getGameType());
			gameEngine.fichas = fichas;
		}
		
		
		xFondo = 0;
		
		
		
		
		//gameEngine.fichas = fichas;
		
		surfaceHolder = getHolder();
		isRunning = true;
		setKeepScreenOn(true);
	}
	
	private void initBitmap(){
		// KID
		playerImg = BitmapFactory.decodeResource(getResources(), R.drawable.kidorange);
		kid_img = BitmapFactory.decodeResource(getResources(), R.drawable.kidorange);
		plataforma = BitmapFactory.decodeResource(getResources(), R.drawable.plataforma);
		// OBJECTS
		bola_img = BitmapFactory.decodeResource(getResources(), R.drawable.bola);
		bomba_img = BitmapFactory.decodeResource(getResources(), R.drawable.bomba);
		boom_img = BitmapFactory.decodeResource(getResources(), R.drawable.boom);
		cactus_img = BitmapFactory.decodeResource(getResources(), R.drawable.cactus);
		//avionBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.avion);
				
		// FICHAS
		ficha1 = BitmapFactory.decodeResource(getResources(), R.drawable.pieza1);
		ficha2 = BitmapFactory.decodeResource(getResources(), R.drawable.pieza2);
		ficha3 = BitmapFactory.decodeResource(getResources(), R.drawable.pieza3);
		ficha4 = BitmapFactory.decodeResource(getResources(), R.drawable.pieza4);
		ficha5 = BitmapFactory.decodeResource(getResources(), R.drawable.pieza5);
		ficha6 = BitmapFactory.decodeResource(getResources(), R.drawable.pieza6);
		ficha7 = BitmapFactory.decodeResource(getResources(), R.drawable.pieza7);
		ficha8 = BitmapFactory.decodeResource(getResources(), R.drawable.pieza8);
		ficha9 = BitmapFactory.decodeResource(getResources(), R.drawable.pieza9);
		ficha10 = BitmapFactory.decodeResource(getResources(), R.drawable.pieza10);
		// RAQUETAS
		cazamariposas_L = BitmapFactory.decodeResource(getResources(), R.drawable.cazamariposas_izq);
		cazamariposas_R = BitmapFactory.decodeResource(getResources(), R.drawable.cazamariposas_der);
		// NUBES
		nube1 = BitmapFactory.decodeResource(getResources(), R.drawable.nube1);
		nube2 = BitmapFactory.decodeResource(getResources(), R.drawable.nube2);
		nube3 = BitmapFactory.decodeResource(getResources(), R.drawable.nube3);
		nube4 = BitmapFactory.decodeResource(getResources(), R.drawable.nube4);
		vuelve = BitmapFactory.decodeResource(getResources(), R.drawable.vuelveinicio);
		// FONDOS
		columpio_fondo = BitmapFactory.decodeResource(getResources(), R.drawable.fondocolumpiocactus);
		nubes_fondo1 = BitmapFactory.decodeResource(getResources(), R.drawable.fondo1);
		nubes_fondo2 = BitmapFactory.decodeResource(getResources(), R.drawable.fondo2);
		nubes_fondo3 = BitmapFactory.decodeResource(getResources(), R.drawable.fondo3);
		avion_fondo1 = BitmapFactory.decodeResource(getResources(), R.drawable.fondoavioneta1);
		avion_fondo2 = BitmapFactory.decodeResource(getResources(), R.drawable.fondoavioneta2);
		avion_fondo3 = BitmapFactory.decodeResource(getResources(), R.drawable.fondoavioneta3);
		laberinto_fondo = BitmapFactory.decodeResource(getResources(), R.drawable.laberintofondo);
		laberinto_mascara = BitmapFactory.decodeResource(getResources(), R.drawable.laberintomascara);
		laberinto_fondo1 = BitmapFactory.decodeResource(getResources(), R.drawable.laberintodos);
		laberinto_mascara1 = BitmapFactory.decodeResource(getResources(), R.drawable.laberintodosmascara);
		
		connecting = BitmapFactory.decodeResource(getResources(), R.drawable.connecting);
		
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLACK);
		paint.setTextSize((int)(playerSize/8));
		
		paint2 = new Paint();
		paint2.setAntiAlias(true);
		paint2.setColor(Color.BLACK);
		paint2.setTextSize(playerSize/4); 
		
		paint3 = new Paint();
		paint3.setAntiAlias(true);
		paint3.setColor(Color.WHITE);
		paint3.setTextSize(30);
		
		paint4 = new Paint();
		paint4.setAntiAlias(true);
		paint4.setColor(Color.BLACK);
		paint4.setTextSize(playerSize/2);
		
		paintBT = new Paint();
		paintBT.setStyle(Paint.Style.STROKE);
		paintBT.setColor(Color.RED);
		
	}
	
	private void initSprite(){
		
		srcKid[0] = new Rect(0, 0, 128, 128);
		srcKid[1] = new Rect(0, 128, 128, 256);
		srcKid[2] = new Rect(0, 256, 128, 384);
		srcKid[3] = new Rect(0, 384, 128, 512);
		srcKid[4] = new Rect(0, 512, 128, 640);
		srcKid[5] = new Rect(0, 640, 128, 768);
		srcKid[6] = new Rect(0, 768, 128, 896);
		srcKid[7] = new Rect(0, 896, 128, 1024);
		
		plataformaRect = new Rect(0,0,167,26);
		nubeRect = new Rect(0,0,225,143);
		//avionRect = new Rect(0, 0, 192, 156);
		
		if(gameEngine.getGameType().equals("Columpio") || gameEngine.getGameType().equals("Rueda")){ // oscila cactus
			src = new Rect(0,0, columpio_fondo.getWidth(), columpio_fondo.getHeight());
		}else if(gameEngine.getGameType().equals("Tobogan")){ // nubes
			src = new Rect(0,0, nubes_fondo1.getWidth(), nubes_fondo1.getHeight());
		}else if(gameEngine.getGameType().equals("SubeBaja")){
			src = new Rect(0,0, avion_fondo1.getWidth(), avion_fondo1.getHeight());
		}else if(gameEngine.getGameType().equals("Balancin")){
			src = new Rect(0,0, laberinto_fondo.getWidth(), laberinto_fondo.getHeight());
			dst1 = new Rect(0,0, laberinto_fondo.getWidth(), laberinto_fondo.getHeight());
		}else if(gameEngine.getGameType().equals("Caballito")){
			src = new Rect(0,0, laberinto_fondo1.getWidth(), laberinto_fondo1.getHeight());
			dst1 = new Rect(0,0, laberinto_fondo1.getWidth(), laberinto_fondo1.getHeight());			
		}else{
			src = new Rect(0,0, columpio_fondo.getWidth(), columpio_fondo.getHeight());
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
					drawFichas(canvas);
					drawScore(canvas);
					drawConnection(canvas);

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

					//measure the text then draw it at center
//					sentenceWidth = paint2.measureText(textReady);
//					drawTextStartingX = (screenWidth - sentenceWidth) / 2;
//					canvas.drawText(textReady, drawTextStartingX , screenHeight/2, paint2);

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
					drawFichas(canvas);
					drawScore(canvas); // draw score and lives

					
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
					drawStage(canvas);
					drawPlayer(canvas);
					drawFichas(canvas);
					drawScore(canvas);
					
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
	
	// draw current location of objects
	private void drawFichas(Canvas canvas) {
		if(gameEngine.getGameType().equals("Columpio")){
			gameEngine.obj.updateObjeto();
			gameEngine.obj.drawObjeto(canvas);
		}else if(gameEngine.getGameType().equals("Tobogan")){
			gameEngine.nube.updateNube();
			gameEngine.nube.drawNube(canvas);
		}else if(gameEngine.getGameType().equals("SubeBaja")){
			gameEngine.fichas.drawFichas(canvas);
		}else if(gameEngine.getGameType().equals("Balancin")||gameEngine.getGameType().equals("Caballito")){
			
			for (int i = 0; i < gameEngine.fichasArray.size(); i++) {
				if (gameEngine.fichasArray.get(i).isAlive()){
					gameEngine.fichasArray.get(i).drawFichas(canvas);
				}
			}
		}
	}
	
	// draw kid 
	private void drawPlayer(Canvas canvas) {
		srcRect = srcKid[0];
		
		if(gameEngine.getGameType().equals("Columpio")){
			Rect dst;
			pDst = new Rect((int)player.getpX(), (int)player.getpY()+(int)playerSize,(int)player.getpX()+167,(int)player.getpY()+(int)playerSize+26);
			canvas.drawBitmap(plataforma,plataformaRect,pDst,null);
		

			// draw raquetas
			Rect raquetaSrc = new Rect(0,0,114,97);
			Rect raquetaLdst = new Rect((int)gameEngine.minPX,(int)gameEngine.minPYL,(int)gameEngine.minPX+114,(int)gameEngine.minPYL+97);
			Rect raquetaRdst = new Rect((int)gameEngine.maxPX,(int)gameEngine.minPYR,(int)gameEngine.maxPX+114,(int)gameEngine.minPYR+97);

			canvas.drawBitmap(cazamariposas_L,raquetaSrc,raquetaLdst,null);
			canvas.drawBitmap(cazamariposas_R,raquetaSrc,raquetaRdst,null);
			
			dst = new Rect((int)player.getpX(), (int)player.getpY(), (int)player.getpX()+ (int) playerSize, (int)player.getpY() + (int) playerSize);
			canvas.drawBitmap(kid_img, srcRect, dst, null);
		}else if(gameEngine.getGameType().equals("Tobogan")){
			Rect dst;
			pDst = new Rect((int)player.getpX(), (int)player.getpY()+(int)playerSize/2,(int)player.getpX()+225,(int)player.getpY() + (int)playerSize/2 + 143);
			canvas.drawBitmap(nube1,nubeRect,pDst,null);
			
			dst = new Rect((int)player.getpX(), (int)player.getpY(), (int)player.getpX()+ (int) playerSize, (int)player.getpY() + (int) playerSize);
			canvas.drawBitmap(kid_img, srcRect, dst, null);
		}else if(gameEngine.getGameType().equals("SubeBaja")){
//			Rect dst;
//			dst = new Rect((int)avion.getpX(), (int)thisKid.getpY(), (int)thisKid.getpX()+ (int) kidSize, (int)thisKid.getpY() + (int) kidSize);
//			canvas.drawBitmap(avionBitmap, avionRect, dst, null);
			avion.drawAvion(canvas);
		}else if(gameEngine.getGameType().equals("Balancin") || gameEngine.getGameType().equals("Caballito")){
//			dst = new Rect((int)player.getpX(), (int)player.getpY(), (int)player.getpX()+ (int) playerSize1, (int)player.getpY() + (int) playerSize1);
//			canvas.drawBitmap(kid_img, srcRect, dst, null);
			player.draw(canvas);
		}
		
	}
	
	// draw score
	public void drawScore(Canvas canvas){
		//canvas.drawText(Integer.toString(gameEngine.getLives()), 40, kidSize * 23, paint);
		int widthDraw = (int) (screenWidth - screenWidth/5);
		int heightDraw = (int) (screenHeight/8); 
		canvas.drawText("SCORE " + Integer.toString(gameEngine.getPlayerScore()), widthDraw, heightDraw , paint);
		canvas.drawText("TIME LEFT "+ Integer.toString(gameEngine.getTimer()), widthDraw, heightDraw + 25, paint);
		
		if(gameEngine.toboganState == gameEngine.tOPEN){
			sentenceWidth = paint2.measureText("GET READY!");
		    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
			canvas.drawText("GET READY!", drawTextStartingX , screenHeight/2, paint4);
		}else if(gameEngine.toboganState == gameEngine.tWAIT){
			sentenceWidth = paint2.measureText("WAIT!");
		    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
			canvas.drawText("WAIT!", drawTextStartingX , screenHeight/2, paint4);
		}else if(gameEngine.toboganState == gameEngine.tJUMP && gameEngine.toboganJump == false){
			sentenceWidth = paint2.measureText("JUMP!");
		    drawTextStartingX = (screenWidth - sentenceWidth) / 2;
			canvas.drawText("JUMP!", drawTextStartingX , screenHeight/2, paint4);
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
	
	@Override
	public void draw(Canvas canvas) {

	}
	
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
		}else if(gameEngine.getGameType().equals("SubeBaja")){
			if (avion.moveXall = true){
				if (xFondo >= -screenWidth){
					xFondo = xFondo - (int) avion.vX;
					dst.set(xFondo, 0, xFondo + (int) screenWidth, (int) screenHeight);
					if (nFondoAvioneta == 0){
						canvas.drawBitmap(avion_fondo1, src, dst, null); //primera vez
					}else if (nFondoAvioneta < 10){
						canvas.drawBitmap(avion_fondo2, src, dst, null); //loop
					} else if (nFondoAvioneta ==10){ //fin
						canvas.drawBitmap(avion_fondo3, src, dst, null); 
						if (xFondo < 1){
							gameEngine.gameState = GAMEOVER;
						}
					}
					
					dst.set(xFondo+(int) screenWidth, 0, xFondo + ((int) screenWidth *2), (int) screenHeight);
					canvas.drawBitmap(avion_fondo2, src, dst, null);
					
					
				}else { //dibujamos 5 veces la imagen 2 del fondo
					xFondo = 0;
					nFondoAvioneta ++;
				}
			}
	
		}else if(gameEngine.getGameType().equals("Balancin")||gameEngine.getGameType().equals("Caballito")){
						
			stage.draw(canvas, (int)player.getpX(), (int)player.getpY(), player.getSpriteWidth(), player.getSpriteHeight(),player.getpNormalSpeed());
			
			
		}else{
			canvas.drawBitmap(columpio_fondo, src, dst, null);
		}
	
	}
	
	public void drawConnection(Canvas canvas){
		canvas.drawBitmap(connecting, srcConnecting, dstConnecting, null);
	}
}
