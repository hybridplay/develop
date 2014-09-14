package com.hybridplay.emptygame;

import java.util.ArrayList;

// direction notes: 1 = up, 2 = down, 3 = right, 4 = left
/*
 * GameEngine class is the controller of the game. GameEngine oversees updates 
 * 		to the elements of the games
 * 		
 */

public class GameEngine implements Runnable {
	private final static int    MAX_FPS = 50;
	// maximum number of frames to be skipped
	// the frame period
	private final static int    FRAME_PERIOD = 1000 / MAX_FPS;
	static final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8, CENTER = 0;
	public final static int 	READY = 0,RUNNING = 1, GAMEOVER = 2, WON = 3, DIE = 4;//, CONNECTING = 5;
	
	// SENSOR DATA
	float angleX, angleY, angleZ;
	int distanceIR;
	boolean triggerXL, triggerXR, triggerYL, triggerYR, triggerZL, triggerZR;
	
	// the game type (Balancin,Caballito,Columpio,Rueda,SubeBaja,Tobogan)
	private String gameType;
	public boolean toboganSemaphore = true;
	public boolean jumpSemaphore = true;
	public final static int	tOPEN = 0, tWAIT = 1, tJUMP = 2, tRESTART = 3;
	public int toboganState;
	public int toboganLevel = 0;
	private long actualTime = System.currentTimeMillis();
	private long myTime;
	private long waitTime = 4000;
	
	private Thread mThread;
	Player player;
	ArrayList<GameElements> gameElements;
	
	int playerScore;
	int timer; int timerCount;
	int lives;
	
	private int gameState;    // ready = 0; running = 1; lost == 2; won = 3;
	
	int powerMode;		// for count down of power speed
	int pNormalSpeed;	// pacmon normal moving speed
	int pPowerSpeed;	// pacmon power moving speed, when eat Power
	int inputDirection;
	int pX, pY;
	int newDirection;

	
	private boolean isRunning;
	private boolean moveAll = false;
	
	public int level=-1, status=-1;
	
	//timer
	private long beginTime; // the time when the cycle begun
	private long timeDiff; // the time it took for the cycle to execute
	private int sleepTime; // ms to sleep (<0 if we're behind)
	public int framesSkipped; // number of frames being skipped si que se usan
	
	
	public long readyCountDown; //si que se usan
	
	int screenWidth, screenHeight;
	
	//Constructor create players, trash and stage
	public GameEngine(int width, int height){
		
		this.screenWidth = width;
		this.screenHeight = height;
		
		player = new Player(width/2, height/2, 128, 128);  // new kid 256 es el tama�o del sprite que tendra
		lives = player.getpLives();
		
		playerScore = 0;
		timer = 120; 
		timerCount = 0;
		gameState = READY;
		toboganState = tRESTART;
		
        //creamos el array de basura y a�adimos 3 objetos
        gameElements = new ArrayList<GameElements>();
		gameElements.add(new GameElements(2, width, -height));
		gameElements.add(new GameElements(1, width, height));
		gameElements.add(new GameElements(2, -width, -height));
		gameElements.add(new GameElements(4, width/2, height/2));
		gameElements.add(new GameElements(8, -width/2, -height/2));
		gameElements.add(new GameElements(4, width/4, height));

		isRunning = true;
		mThread = new Thread(this);
		mThread.start();
		
	}
	
	public void updateSensorData(float aX,float aY,float aZ, int dIR, boolean tXL, boolean tXR, boolean tYL, boolean tYR, boolean tZL, boolean tZR){
		angleX = aX;
		angleY = aY;
		angleZ = aZ;
		distanceIR = dIR;
		triggerXL = tXL;
		triggerXR = tXR;
		triggerYL = tYL;
		triggerYR = tYR;
		triggerZL = tZL;
		triggerZR = tZR;
	}
	
	//update
	public void update(){
		updateTimer();
		updateKid();
		updateGameElements();
	}
	
	public void updateKid(){
		
		pX = player.getpX();
		pY = player.getpY();
		int limitW = (int) screenWidth / 4;
		int limitH = (int) screenHeight / 4;
		
		
		if(getGameType().equals("Balancin")){ // ---------------- Balancin
			// pinza horizontal - cuatro direcciones - ejes Z Y
			if (triggerYL) {
				player.setDir(DOWN);
				if (pY + player.getPheight() < screenHeight - limitH){
					pY = pY + player.getpNormalSpeed();
					moveAll = false;
				}else{
					moveAll = true;
				}
			}else if (triggerYR) {
				player.setDir(UP);
				if (pY > limitH) {
					pY = pY - player.getpNormalSpeed();
					moveAll = false;
				}else{
					moveAll = true;
				}
			}
			
			if (triggerZR) {
				player.setDir(RIGHT);
				if (pX + player.getPwidth() < screenWidth - limitW) {
					pX = pX + player.getpNormalSpeed();
					moveAll = false;
				}else{
					//movemos el resto
					moveAll = true;
				}
			}else if (triggerZL) {
				player.setDir(LEFT);
				if (pX > limitW) {
					pX = pX - player.getpNormalSpeed();
					moveAll = false;
				}else{
					moveAll = true;
				}
				
			}
			// update kid position
			player.setpX(pX);
			player.setpY(pY);
			
		}else if(getGameType().equals("Caballito")){ // ---------- Caballito
			// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
			if (triggerYL) {
				player.setDir(DOWN);
				if (pY + player.getPheight() < screenHeight - limitH){
					pY = pY + player.getpNormalSpeed();
					moveAll = false;
				}else{
					moveAll = true;
				}
			}else if (triggerYR) {
				player.setDir(UP);
				if (pY > limitH) {
					pY = pY - player.getpNormalSpeed();
					moveAll = false;
				}else{
					moveAll = true;
				}
			}
			
			if (triggerXR) {
				player.setDir(RIGHT);
				if (pX + player.getPwidth() < screenWidth - limitW) {
					pX = pX + player.getpNormalSpeed();
					moveAll = false;
				}else{
					//movemos el resto
					moveAll = true;
				}
			}else if (triggerXL) {
				player.setDir(LEFT);
				if (pX > limitW) {
					pX = pX - player.getpNormalSpeed();
					moveAll = false;
				}else{
					moveAll = true;
				}
				
			}
			// update kid position
			player.setpX(pX);
			player.setpY(pY);
			
		}else if(getGameType().equals("Columpio")){ // ---------- Columpio
			// pinza vertical boton hacia abajo - oscilaci�n - eje X
	        player.setAngle(angleX);
	
	        // update kid position
			player.setpX((int) (300 + Math.cos(player.getAngle())*500));
			player.setpY((int) (-300 + Math.sin(player.getAngle())*500));
			
		}else if(getGameType().equals("Rueda")){ // ---------- Rueda
			// pinza vertical boton hacia abajo - oscilaci�n - eje X
	        player.setAngle(angleX);
	
	        // update kid position
			player.setpX((int) (300 + Math.cos(player.getAngle())*500));
			player.setpY((int) (-300 + Math.sin(player.getAngle())*500));
			
		}else if(getGameType().equals("SubeBaja")){ // ---------- SubeBaja
			// pinza horizontal - dos direcciones - eje Z
			
			if (triggerZR || triggerXR) {
				player.setDir(RIGHT);
				if (pX + player.getPwidth() < screenWidth - limitW) {
					pX = pX + player.getpNormalSpeed();
					moveAll = false;
					if (pY > limitH) {
						pY = pY - player.getpNormalSpeed();
					}
				}else{
					if (pY + player.getPheight() < screenHeight - limitH){
						pY = pY + player.getpNormalSpeed();
					}
					//movemos el resto
					moveAll = true;
				}
			}else if (triggerZL || triggerXL) {
				player.setDir(LEFT);
				if (pX > limitW) {
					pX = pX - player.getpNormalSpeed();
					moveAll = false;
					if (pY > limitH) {
						pY = pY - player.getpNormalSpeed();
					}
				}else{
					if (pY + player.getPheight() < screenHeight - limitH){
						pY = pY + player.getpNormalSpeed();
					}
					moveAll = true;
				}
				
			}
			// update kid position
			player.setpX(pX);
			player.setpY(pY);
			
		}else if(getGameType().equals("Tobogan")){ // ---------- Tobogan
			// we use here only IR sensor
			
			// system timeline update
			myTime = System.currentTimeMillis() - actualTime;
			
			
			if(toboganState == tRESTART && toboganSemaphore && distanceIR == 0){
				toboganSemaphore = false;
				toboganState = tOPEN;
				//Log.d("Game Tobogan","EMPTY"); // posici�n vacia esperando al user
			}else if(distanceIR != 0 && !toboganSemaphore){
				actualTime = System.currentTimeMillis();
				toboganState = tWAIT;
				toboganSemaphore = true;
				jumpSemaphore = true;
				//Log.d("Game Tobogan","WAIT!"); // ni�o sentado a la espera de poder saltar
			}
			
			if(toboganState == tWAIT && myTime > waitTime){
				if(jumpSemaphore == true){
					toboganState = tJUMP; // se�al de poder saltar
					//Log.d("Game Tobogan","JUMP!");
					jumpSemaphore = false;
				}
			}else if(toboganState == tWAIT && myTime < waitTime && distanceIR == 0){
				//Log.d("Game Tobogan","RESET!"); //the user has jump before the time so we reset the system
				toboganState = tRESTART;
				toboganSemaphore = true;
				jumpSemaphore = true;
			}
			
			if(toboganState == tJUMP && distanceIR == 0){ // cuando el ni�o salta
				// launch jump function
				//Log.d("Game Tobogan","El ni�o ha saltado!");
				toboganState = tRESTART;
				toboganSemaphore = true;
				jumpSemaphore = true;
				actualTime = System.currentTimeMillis();
			}
		}
		
	}
	


	//update trash movements and locations
	public void updateGameElements(){
		int gNormalSpeed;
		int gX, gY;
		int gDir;
		
		for (int i = 0; i < gameElements.size(); i++) {
			if (gameElements.get(i).isActive()){ //si esta activa
			
				gX = gameElements.get(i).getX();
				gY = gameElements.get(i).getY();
				gDir = gameElements.get(i).getDir();
				gNormalSpeed = gameElements.get(0).getNormalSpeed();
				
				if(gDir == DOWN && gY > screenHeight) gY = 0;
				if(gDir == UP && gY < 0) gY = screenHeight;
				if(gDir == RIGHT && gX > screenWidth) gX = 0;
				if(gDir == LEFT && gX < 0) gX = screenWidth;
				
				if (moveAll){
					if (player.getDir() == RIGHT) gX = gX - player.getpNormalSpeed();
					if (player.getDir() == LEFT) gX = gX + player.getpNormalSpeed();
					if (player.getDir() == UP) gY = gY + player.getpNormalSpeed();
					if (player.getDir() == DOWN) gY = gY - player.getpNormalSpeed();
				}else{	
					if (gDir == RIGHT) gX = gX + gNormalSpeed;
					else if (gDir == LEFT) gX = gX - gNormalSpeed;
					else if (gDir == DOWN) gY = gY + gNormalSpeed;
					else if (gDir == UP) gY = gY - gNormalSpeed;
				}
				
				// set new location of ghost after moving
				gameElements.get(i).setX(gX);
				gameElements.get(i).setY(gY);
				
				checkCollision(gX, gY, i);
			}else{
				gameElements.get(i).reset(player.getpX(), player.getpY());
			}

		}
		
	}
	
	//check if trash touch kid
	private void checkCollision(int gX, int gY, int i){
		int pX = player.getpX();
		int pY = player.getpY();
		int radius = (int) screenWidth / 4 ; // 190 con pantalla de 800 
		int maxDistance = screenWidth;
		
		if (Math.abs(pX - gX) + Math.abs(pY - gY) < radius) { //ghost touches player
			eatGameElements(i);
		}
		
		if (Math.abs(pX - gX) + Math.abs(pY - gY) > maxDistance){ //si esta muy lejos la reseteamos para que aparezca cerca del ni�o
			gameElements.get(i).setActive(false);
		}
		
	}
	

	// eat food ==> score and power ==> speed
	private void eatGameElements(int i) {
		
		playerScore++;   // increase score
		
		gameElements.get(i).setActive(false);
		
		if (player.getDir() == RIGHT) player.setEatRight(true); player.setEating(true);
		if (player.getDir() == LEFT) player.setEatleft(true); player.setEating(true);
		if (player.getDir() == UP) player.setEatUp(true); player.setEating(true);
		if (player.getDir() == DOWN) player.setEatDown(true); player.setEating(true);

	}
	
	// count down timer once per MAX_FPS
	private void updateTimer(){
		timerCount++;
		if (timerCount % 40 == 0){
			timer--;
			timerCount = 0;
			powerMode--;
		}
		if (timer == -1){
			gameState = GAMEOVER;  // LOST
		}
	}
	

	public void run() {
		while (isRunning){
			if (gameState == READY)    updateReady();
			if (gameState == RUNNING)  updateRunning();
			if (gameState == GAMEOVER) updateGameOver();		
		}
	}
	
	// loop through ready if gameState is READY
	private void updateReady(){
		beginTime = System.currentTimeMillis();

		readyCountDown = 1L - timeDiff/1000;		
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
	
	// loop through running if gameState is RUNNING
	private void updateRunning(){
		beginTime = System.currentTimeMillis();
		framesSkipped = 0; // resetting the frames skipped
		
		update();
	
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
	
	private void updateGameOver(){
		pause();
	}
	
	public void pause() {
		isRunning = false;
	}
	
	public void resume() {
		isRunning = true;
	}

	public int getGameState() {
		return gameState;
	}

	public void setGameState(int gameState) {
		this.gameState = gameState;
	}

	public int getPlayerScore() {
		return playerScore;
	}

	public void setPlayerScore(int playerScore) {
		this.playerScore = playerScore;
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public Player getKid() {
		return player;
	}

	public void setKid(Player kid) {
		this.player = kid;
	}

	public String getGameType() {
		return gameType;
	}

	public void setGameType(String gameType) {
		this.gameType = gameType;
	}
	
	
	
}
