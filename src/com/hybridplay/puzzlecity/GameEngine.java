package com.hybridplay.puzzlecity;

import com.hybridplay.bluetooth.MySensor;

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
	public int	tOPEN = 0, tWAIT = 1, tJUMP = 2, tRESTART = 3;
	public int toboganState;
	public int toboganLevel = 0;
	public boolean toboganJump = false;
	public float kVX = 5.3f, kVY = 6.8f;
	public long actualTime = System.currentTimeMillis();
	public long myTime;
	public long waitTime = 2500;
	
	private Thread mThread;
	public int playerScore;
	int timer; int timerCount;
	int lives;
	Kid kid;
	Nube nube;
	Objetos obj;
	Avion avion;
	Fichas fichas;
	
	public int gameState;    // ready = 0; running = 1; lost == 2; won = 3;
	int powerMode;		// for count down of power speed
	int pNormalSpeed;	// pacmon normal moving speed
	int pPowerSpeed;	// pacmon power moving speed, when eat Power
	int inputDirection;
	int pX, pY;
	int newDirection;
	
	public boolean isRunning;
	public boolean moveAll = false;
	
	public int level=-1, status=-1;
	
	//timer
	private long beginTime; // the time when the cycle begun
	private long timeDiff; // the time it took for the cycle to execute
	private int sleepTime; // ms to sleep (<0 if we're behind)
	public int framesSkipped; // number of frames being skipped si que se usan
	public long readyCountDown; //si que se usan
	
	// hybridPlay sensor
	private MySensor mSensorX, mSensorY, mSensorZ, mSensorIR;
	public float minPX=10000, maxPX=0, minPYL=10000, minPYR=10000;
	public boolean updateRaquetas = true;
	
	// Device
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
		toboganSemaphore = true;
		
        // create sensors for hybriplay
        mSensorX = new MySensor("x");
        mSensorY = new MySensor("y");
        mSensorZ = new MySensor("z");
        mSensorIR = new MySensor("IR");
        
        isRunning = true;
		mThread = new Thread(this);
		mThread.start();
		
	}
	
	//update
	public void update(){
		updateTimer();
		updateKid();
		updateNubes();
//		updateAvion();
//		updateFichas();
	}
	
	public void updateKid(){
		
		pX = (int)kid.getpX();
		pY = (int)kid.getpY();
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
			
			if(kid.hasChangedDir){
				kid.hasChangedDir = false;
				updateRaquetas = true;
			}
	
			mSensorX.setOldMediumValue(mSensorX.getMediumValue());
	        
	        float rawPos = (float)(Math.PI * (1.0-(mSensorX.getMediumValue()/mSensorX.getMaxValue())) );
	        
	        kid.setAngle(rawPos);
	
	        // update kid position
	        kid.setpX(this.width/2 - 60 - (float)Math.cos(Math.PI *kid.getAngle())*220);
	        kid.setpY(this.height - 150 - (float)Math.abs(Math.cos(Math.PI *kid.getAngle()))*220);
	        
	        float flag = (float)Math.cos(Math.PI *kid.getAngle());
	        // TODO - Corregir el if para el correcto posicionamiento de las raquetas
	        if(flag < 0.0f){
				kid.setDir(RIGHT);
			}else{
				kid.setDir(LEFT);
			}
	        
	        if(updateRaquetas){
	        	if(kid.getDir() == LEFT){
	        		minPX = kid.getpX();
	        		minPYL = kid.getpY();
	        	}else if(kid.getDir() == RIGHT){
	        		maxPX = kid.getpX();
	        		minPYR = kid.getpY();
	        	}
	        	updateRaquetas = false;
	        }
			
		}else if(getGameType().equals("Rueda")){ // ---------- Rueda
			// pinza vertical boton hacia abajo - oscilaci�n - eje X
			
			
		}else if(getGameType().equals("SubeBaja")){ // ---------- SubeBaja
			// pinza horizontal - dos direcciones - eje Z
			
			if(mSensorZ.isFireMaxActive()){
				if(avion.vX < 4) avion.vX += 1.4f; //se mueve a la derecha
				if(avion.vY >= -2.5) {
					avion.vY -= 2.5; //se eleva
					if(avion.vY < -2.5) avion.vY = (float) -2.5;
				}
			}
			
			avion.updateAvion();
			
			fichas.updateFicha();
			
			
//			if (mSensorZ.isFireMaxActive()) {
//				kid.setDir(RIGHT);
//				if (pX + kid.getPwidth() < width - limitW) {
//					pX = pX + kid.getpNormalSpeed();
//					moveAll = false;
//					if (pY > limitH) {
//						pY = pY - kid.getpNormalSpeed();
//					}
//				}else{
//					if (pY + kid.getPheight() < height - limitH){
//						pY = pY + kid.getpNormalSpeed();
//					}
//					//movemos el resto
//					moveAll = true;
//				}
//			}else if (mSensorZ.isFireMinActive()) {
//				kid.setDir(LEFT);
//				if (pX > limitW) {
//					pX = pX - kid.getpNormalSpeed();
//					moveAll = false;
//					if (pY > limitH) {
//						pY = pY - kid.getpNormalSpeed();
//					}
//				}else{
//					if (pY + kid.getPheight() < height - limitH){
//						pY = pY + kid.getpNormalSpeed();
//					}
//					moveAll = true;
//				}
//				
//			}
			// update kid position
			//kid.setpX(pX);
			//kid.setpY(pY);
		}else if(getGameType().equals("Tobogan")){ // ---------- Tobogan
			// we use here only IR sensor
			int limit = 300;
			// system timeline update
			myTime = System.currentTimeMillis() - actualTime;
			
			if(mSensorIR.getActualValue() < limit && toboganSemaphore && toboganState == tRESTART){
				toboganSemaphore = false;
				toboganState = tOPEN;
				kid.setpX(this.width - 400);
		        kid.setpY(0);
			}else if(toboganState == tOPEN && mSensorIR.getActualValue() > limit && !toboganSemaphore){
				actualTime = System.currentTimeMillis();
				toboganState = tWAIT;
				toboganSemaphore = true;
				jumpSemaphore = true;
				// ni�o sentado a la espera de poder saltar
				kid.setpX(this.width - 400);
		        kid.setpY(0);
			}
			
			if(toboganState == tWAIT && myTime > waitTime){
				if(jumpSemaphore == true){
					toboganState = tJUMP; // se�al de poder saltar
					//Log.d("Game Tobogan","JUMP!");
					jumpSemaphore = false;
				}
			}else if(toboganState == tWAIT && myTime < waitTime && mSensorIR.getActualValue() < limit){
				//Log.d("Game Tobogan","RESET!");
				toboganState = tRESTART;
				toboganSemaphore = true;
				jumpSemaphore = true;
			}
			
			if(toboganState == tJUMP && mSensorIR.getActualValue() < limit){ // cuando el ni�o salta
				// launch jump function
				toboganJump = true;
			}
		}
		
	}
	
	//update trash movements and locations
	public void updateNubes(){
		
		if(getGameType().equals("Columpio")){
			checkObjetosOnFicha(obj.pX, obj.pY);
		}else if(getGameType().equals("Tobogan")){
			checkNubeOnFicha(nube.pX, nube.pY);
			jump();
		}

	}
	
	private void jump(){
		if(toboganJump == true){
			int pX = (int)kid.getpX();
			int pY = (int)kid.getpY();
			
			pX -= kVX;
			pY += kVY;
			
			kid.setpX(pX);
	        kid.setpY(pY);
	        
	        if(pY > this.height + 10){
	        	//Log.d("PuzzleCity","WE LOSE");
	        	toboganJump = false;
	        	toboganState = tRESTART;
	    		toboganSemaphore = true;
	    		jumpSemaphore = true;
	    		actualTime = System.currentTimeMillis();
	        }
		}
	}
	
	//check if kid jump on nube with ficha
	private void checkObjetosOnFicha(float gX, float gY){
		int radius = (int) width / 6 ; // 190 con pantalla de 800 

		if (Math.abs(minPX - gX) + Math.abs(minPYL - gY) < radius || Math.abs(maxPX - gX) + Math.abs(minPYR - gY) < radius) { // kid touch ficha
			playerScore++;   // increase score
			obj.reloadObjeto();
		}
	}
	
	//check if kid jump on nube with ficha
	private void checkNubeOnFicha(float gX, float gY){
		int pX = (int)kid.getpX();
		int pY = (int)kid.getpY();
		int radius = (int) width / 4 ; // 190 con pantalla de 800 
		
		if (Math.abs(pX - gX) + Math.abs(pY - gY) < radius) { // kid touch ficha
			if(nube.hasFicha){
				eatFicha();
			}
		}
	}
	
	// eat ficha ==> score
	private void eatFicha() {
		//Log.d("PuzzleCity","WE WIN");
		nube.reloadNube();
		toboganJump = false;
		playerScore++;   // increase score
		toboganState = tRESTART;
		toboganSemaphore = true;
		jumpSemaphore = true;
		actualTime = System.currentTimeMillis();

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
	
	@Override
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