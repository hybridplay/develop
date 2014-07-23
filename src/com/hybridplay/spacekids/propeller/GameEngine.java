package com.hybridplay.spacekids.propeller;

import java.util.ArrayList;

import android.util.Log;

import com.hybridplay.bluetooth.MySensor;



// direction notes: 1 = up, 2 = down, 3 = right, 4 = left
/*
 * GameEngine class is the controller of the game. GameEngine oversees updates 
 * 		models(maze, pacmon, monster) as well as call drawing.
 * 		
 */

public class GameEngine implements Runnable {
	private final static int    MAX_FPS = 50;
	// maximum number of frames to be skipped
	// the frame period
	private final static int    FRAME_PERIOD = 1000 / MAX_FPS;
	static final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8, CENTER = 0;
	public final static int 	READY = 0,RUNNING = 1, GAMEOVER = 2, WON = 3, DIE = 4;//, CONNECTING = 5;
	
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
	Kid kid;
	ArrayList<Trash> trash;
	
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
	
	//hybridPlay sensor
	private MySensor mSensorX, mSensorY, mSensorZ, mSensorIR;
	
	int width, height;
	
	//Constructor create players, trash and stage
	public GameEngine(int width, int height){
		
		this.width = width;
		this.height = height;
		
		kid = new Kid(width/2, height/2, 128, 128);  // new kid 256 es el tama�o del sprite que tendra
		lives = kid.getpLives();
		
		playerScore = 0;
		timer = 120; 
		timerCount = 0;
		gameState = READY;
		toboganState = tRESTART;
		
        // create sensors for hybriplay
        mSensorX = new MySensor("x");
        mSensorY = new MySensor("y");
        mSensorZ = new MySensor("z");
        mSensorIR = new MySensor("IR");
		
        //creamos el array de basura y a�adimos 3 objetos
        trash = new ArrayList<Trash>();
		trash.add(new Trash(2, width, -height));
		trash.add(new Trash(1, width, height));
		trash.add(new Trash(2, -width, -height));
		trash.add(new Trash(4, width/2, height/2));
		trash.add(new Trash(8, -width/2, -height/2));
		trash.add(new Trash(4, width/4, height));

		isRunning = true;
		mThread = new Thread(this);
		mThread.start();
		
	}
	
	//update
	public void update(){
		updateTimer();
		updateKid();
		updateTrash();
	}
	
	public void updateKid(){
		
		pX = kid.getpX();
		pY = kid.getpY();
		int limitW = (int) width / 4;
		int limitH = (int) height / 4;
		
		
		if(getGameType().equals("Balancin")){ // ---------------- Balancin
			// pinza horizontal - cuatro direcciones - ejes Z Y
			if (mSensorY.isFireMinActive()) {
				kid.setDir(DOWN);
				if (pY + kid.getPheight() < height - limitH){
					pY = pY + kid.getpNormalSpeed();
					moveAll = false;
				}else{
					moveAll = true;
				}
			}else if (mSensorY.isFireMaxActive()) {
				kid.setDir(UP);
				if (pY > limitH) {
					pY = pY - kid.getpNormalSpeed();
					moveAll = false;
				}else{
					moveAll = true;
				}
			}
			
			if (mSensorZ.isFireMaxActive()) {
				kid.setDir(RIGHT);
				if (pX + kid.getPwidth() < width - limitW) {
					pX = pX + kid.getpNormalSpeed();
					moveAll = false;
				}else{
					//movemos el resto
					moveAll = true;
				}
			}else if (mSensorZ.isFireMinActive()) {
				kid.setDir(LEFT);
				if (pX > limitW) {
					pX = pX - kid.getpNormalSpeed();
					moveAll = false;
				}else{
					moveAll = true;
				}
				
			}
			// update kid position
			kid.setpX(pX);
			kid.setpY(pY);
		}else if(getGameType().equals("Caballito")){ // ---------- Caballito
			// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
			if (mSensorY.isFireMinActive()) {
				kid.setDir(DOWN);
				if (pY + kid.getPheight() < height - limitH){
					pY = pY + kid.getpNormalSpeed();
					moveAll = false;
				}else{
					moveAll = true;
				}
			}else if (mSensorY.isFireMaxActive()) {
				kid.setDir(UP);
				if (pY > limitH) {
					pY = pY - kid.getpNormalSpeed();
					moveAll = false;
				}else{
					moveAll = true;
				}
			}
			
			if (mSensorX.isFireMaxActive()) {
				kid.setDir(RIGHT);
				if (pX + kid.getPwidth() < width - limitW) {
					pX = pX + kid.getpNormalSpeed();
					moveAll = false;
				}else{
					//movemos el resto
					moveAll = true;
				}
			}else if (mSensorX.isFireMinActive()) {
				kid.setDir(LEFT);
				if (pX > limitW) {
					pX = pX - kid.getpNormalSpeed();
					moveAll = false;
				}else{
					moveAll = true;
				}
				
			}
			// update kid position
			kid.setpX(pX);
			kid.setpY(pY);
			
		}else if(getGameType().equals("Columpio")){ // ---------- Columpio
			// pinza vertical boton hacia abajo - oscilaci�n - eje X
			mSensorX.addValueList(mSensorX.getActualValue());            
            
			if (mSensorX.getMediumValue() < mSensorX.getOldMediumValue()){
				kid.setDir(RIGHT);
			}else{
				kid.setDir(LEFT);
			}
	
			mSensorX.setOldMediumValue(mSensorX.getMediumValue());
	      
			            
	        kid.setAngle((float) (Math.PI * mSensorX.getMediumValue() / 650));
	
	        // update kid position
			kid.setpX((int) (300 + Math.cos(kid.getAngle())*500));
			kid.setpY((int) (-300 + Math.sin(kid.getAngle())*500));
			
		}else if(getGameType().equals("Rueda")){ // ---------- Rueda
			// pinza vertical boton hacia abajo - oscilaci�n - eje X
			mSensorX.addValueList(mSensorX.getActualValue());            
            
			if (mSensorX.getMediumValue() < mSensorX.getOldMediumValue()){
				kid.setDir(RIGHT);
			}else{
				kid.setDir(LEFT);
			}
	
			mSensorX.setOldMediumValue(mSensorX.getMediumValue());
	      
			            
	        kid.setAngle((float) (Math.PI * mSensorX.getMediumValue() / 650));
	
	        // update kid position
			kid.setpX((int) (300 + Math.cos(kid.getAngle())*500));
			kid.setpY((int) (-300 + Math.sin(kid.getAngle())*500));
			
		}else if(getGameType().equals("SubeBaja")){ // ---------- SubeBaja
			// pinza horizontal - dos direcciones - eje Z
			
			if (mSensorZ.isFireMaxActive()) {
				kid.setDir(RIGHT);
				if (pX + kid.getPwidth() < width - limitW) {
					pX = pX + kid.getpNormalSpeed();
					moveAll = false;
					if (pY > limitH) {
						pY = pY - kid.getpNormalSpeed();
					}
				}else{
					if (pY + kid.getPheight() < height - limitH){
						pY = pY + kid.getpNormalSpeed();
					}
					//movemos el resto
					moveAll = true;
				}
			}else if (mSensorZ.isFireMinActive()) {
				kid.setDir(LEFT);
				if (pX > limitW) {
					pX = pX - kid.getpNormalSpeed();
					moveAll = false;
					if (pY > limitH) {
						pY = pY - kid.getpNormalSpeed();
					}
				}else{
					if (pY + kid.getPheight() < height - limitH){
						pY = pY + kid.getpNormalSpeed();
					}
					moveAll = true;
				}
				
			}
			// update kid position
			kid.setpX(pX);
			kid.setpY(pY);
		}else if(getGameType().equals("Tobogan")){ // ---------- Tobogan
			// we use here only IR sensor
			
			// system timeline update
			myTime = System.currentTimeMillis() - actualTime;
			
			
			if(mSensorIR.getActualValue() < 512 && toboganSemaphore && toboganState == tRESTART){
				toboganSemaphore = false;
				toboganState = tOPEN;
				Log.d("Game Tobogan","EMPTY"); // posici�n vacia
			}else if(mSensorIR.getActualValue() > 512 && !toboganSemaphore){
				actualTime = System.currentTimeMillis();
				toboganState = tWAIT;
				toboganSemaphore = true;
				jumpSemaphore = true;
				Log.d("Game Tobogan","WAIT!"); // ni�o sentado a la espera de poder saltar
			}
			
			if(toboganState == tWAIT && myTime > waitTime){
				if(jumpSemaphore == true){
					toboganState = tJUMP; // se�al de poder saltar
					Log.d("Game Tobogan","JUMP!");
					jumpSemaphore = false;
				}
			}else if(toboganState == tWAIT && myTime < waitTime && mSensorIR.getActualValue() < 512){
				Log.d("Game Tobogan","RESET!");
				toboganState = tRESTART;
				toboganSemaphore = true;
				jumpSemaphore = true;
			}
			
			if(toboganState == tJUMP && mSensorIR.getActualValue() < 512){ // cuando el ni�o salta
				// launch jump function
				Log.d("Game Tobogan","El ni�o ha saltado!");
				toboganState = tRESTART;
				toboganSemaphore = true;
				jumpSemaphore = true;
				actualTime = System.currentTimeMillis();
			}
		}
		
	}
	


	//update trash movements and locations
	public void updateTrash(){
		int gNormalSpeed;
		int gX, gY;
		int gDir;
		
		for (int i = 0; i < trash.size(); i++) {
			if (trash.get(i).isActive()){ //si esta activa
			
				gX = trash.get(i).getX();
				gY = trash.get(i).getY();
				gDir = trash.get(i).getDir();
				gNormalSpeed = trash.get(0).getNormalSpeed();
				
				if(gDir == DOWN && gY > height) gY = 0;
				if(gDir == UP && gY < 0) gY = height;
				if(gDir == RIGHT && gX > width) gX = 0;
				if(gDir == LEFT && gX < 0) gX = width;
				
				if (moveAll){
					if (kid.getDir() == RIGHT) gX = gX - kid.getpNormalSpeed();
					if (kid.getDir() == LEFT) gX = gX + kid.getpNormalSpeed();
					if (kid.getDir() == UP) gY = gY + kid.getpNormalSpeed();
					if (kid.getDir() == DOWN) gY = gY - kid.getpNormalSpeed();
				}else{	
					if (gDir == RIGHT) gX = gX + gNormalSpeed;
					else if (gDir == LEFT) gX = gX - gNormalSpeed;
					else if (gDir == DOWN) gY = gY + gNormalSpeed;
					else if (gDir == UP) gY = gY - gNormalSpeed;
				}
				
				// set new location of ghost after moving
				trash.get(i).setX(gX);
				trash.get(i).setY(gY);
				
				checkCollision(gX, gY, i);
			}else{
				trash.get(i).reset(kid.getpX(), kid.getpY());
			}

		}
		
	}
	
	//check if trash touch kid
	private void checkCollision(int gX, int gY, int i){
		int pX = kid.getpX();
		int pY = kid.getpY();
		int radius = (int) width / 4 ; // 190 con pantalla de 800 
		int maxDistance = width;
		
		if (Math.abs(pX - gX) + Math.abs(pY - gY) < radius) { //ghost touches player
			eatTrash(i);
		}
		
		if (Math.abs(pX - gX) + Math.abs(pY - gY) > maxDistance){ //si esta muy lejos la reseteamos para que aparezca cerca del ni�o
			trash.get(i).setActive(false);
		}
		
	}
	

	// eat food ==> score and power ==> speed
	private void eatTrash(int i) {
		
		playerScore++;   // increase score
		
		trash.get(i).setActive(false);
		
		if (kid.getDir() == RIGHT) kid.setEatRight(true); kid.setEating(true);
		if (kid.getDir() == LEFT) kid.setEatleft(true); kid.setEating(true);
		if (kid.getDir() == UP) kid.setEatUp(true); kid.setEating(true);
		if (kid.getDir() == DOWN) kid.setEatDown(true); kid.setEating(true);

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

	public MySensor getmSensorX() {
		return mSensorX;
	}

	public void setmSensorX(MySensor mSensorX) {
		this.mSensorX = mSensorX;
	}

	public MySensor getmSensorY() {
		return mSensorY;
	}

	public void setmSensorY(MySensor mSensorY) {
		this.mSensorY = mSensorY;
	}

	public MySensor getmSensorZ() {
		return mSensorZ;
	}

	public void setmSensorZ(MySensor mSensorZ) {
		this.mSensorZ = mSensorZ;
	}

	public MySensor getmSensorIR() {
		return mSensorIR;
	}

	public void setmSensorIR(MySensor mSensorIR) {
		this.mSensorIR = mSensorIR;
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

	public Kid getKid() {
		return kid;
	}

	public void setKid(Kid kid) {
		this.kid = kid;
	}

	public String getGameType() {
		return gameType;
	}

	public void setGameType(String gameType) {
		this.gameType = gameType;
	}
	
	
	
}
