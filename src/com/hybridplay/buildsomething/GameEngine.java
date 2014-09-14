package com.hybridplay.buildsomething;

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
	public int	tOPEN = 0, tWAIT = 1, tJUMP = 2, tRESTART = 3;
	public int toboganState;
	public int toboganLevel = 0;
	public boolean toboganJump = false;
	public float kVX = 5.3f, kVY = 6.8f;
	public long actualTime = System.currentTimeMillis();
	public long myTime;
	public long waitTime = 4000;
	
	private Thread mThread;
	public int playerScore;
	int timer; int timerCount;
	int lives;
	Kid kid;
	Nube nube;
	Objetos obj;
	Robot robot;
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
	
	public float minPX=10000, maxPX=0, minPYL=10000, minPYR=10000;
	public boolean updateRaquetas = true;
	
	// Device
	int width, height;
	
	//Constructor create players, trash and stage
	public GameEngine(int width, int height){
		this.width = width;
		this.height = height;
		//Log.i("log width en constructor", Integer.toString(this.width));
		
		kid = new Kid(width/2, height/2, 128, 128);  // new kid 256 es el tamaï¿½o del sprite que tendra
		lives = kid.getpLives();

		playerScore = 0;
		timer = 120; 
		timerCount = 0;
		gameState = READY;
		toboganState = tRESTART;
		toboganSemaphore = true;
        
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
		updateRobot();
		updateNubes();
//		updateAvion();
//		updateFichas();
	}
	
	public void updateRobot(){
		
		pX = (int)robot.getpX();
		pY = (int)robot.getpY();
	
		if(getGameType().equals("Columpio")){ // ---------- Columpio
			
		}else if(getGameType().equals("Rueda")){ // ---------- Rueda
			// pinza vertical boton hacia abajo - oscilaciï¿½n - eje X
			
			
		}else if(getGameType().equals("Balancin")){
			
			if (triggerZR) { //a la derecha
				//robot.setDir(RIGHT);
				//Log.i("log robot.getPwidth()", Integer.toString(robot.robotW));
				
				if (pX + robot.spriteWidth < width ) {
					
					pX = pX + robot.getpNormalSpeed();
					//moveAll = false;
				}
				
				
			}else if (triggerZL) { //a la izquierda
				//robot.setDir(LEFT);
				if (pX > 0) {
					pX = pX - robot.getpNormalSpeed();
				
				}
			}
			
			// update kid position
			robot.setpX(pX);
			robot.setpY(pY);
			
			robot.updateRobot(System.currentTimeMillis()); //para animar la imagen
			//avion.updateAvion();
			
			fichas.updateFicha();
			
		}else if(getGameType().equals("Caballito")){
			
			if (triggerZR) { //a la derecha
				//robot.setDir(RIGHT);
				//Log.i("log robot.getPwidth()", Integer.toString(robot.robotW));
				
				if (pX + robot.spriteWidth < width ) {
					
					pX = pX + robot.getpNormalSpeed();
					//moveAll = false;
				}
				
				
			}else if (triggerZL) { //a la izquierda
				//robot.setDir(LEFT);
				if (pX > 0) {
					pX = pX - robot.getpNormalSpeed();
				
				}
			}
			
			// update kid position
			robot.setpX(pX);
			robot.setpY(pY);
			
			robot.updateRobot(System.currentTimeMillis()); //para animar la imagen
			//avion.updateAvion();
			
			fichas.updateFicha();
			
		}else if(getGameType().equals("SubeBaja")){ // ---------- SubeBaja
			// pinza horizontal - dos direcciones - eje Z
			
			if (triggerZR || triggerXR) { //a la derecha
				//robot.setDir(RIGHT);
				//Log.i("log robot.getPwidth()", Integer.toString(robot.robotW));
				
				if (pX + robot.spriteWidth < width ) {
					
					pX = pX + robot.getpNormalSpeed();
					//moveAll = false;
				}
				
				
			}else if (triggerZL || triggerXL) { //a la izquierda
				//robot.setDir(LEFT);
				if (pX > 0) {
					pX = pX - robot.getpNormalSpeed();
				
				}
			}
			
			// update kid position
			robot.setpX(pX);
			robot.setpY(pY);
			
			robot.updateRobot(System.currentTimeMillis()); //para animar la imagen
			//avion.updateAvion();
			
			fichas.updateFicha();
			
			
		}else if(getGameType().equals("Tobogan")){ // ---------- Tobogan
			// we use here only IR sensor
			
			// system timeline update
			myTime = System.currentTimeMillis() - actualTime;
			
			if(distanceIR == 0 && toboganSemaphore && toboganState == tRESTART){
				toboganSemaphore = false;
				toboganState = tOPEN;
				kid.setpX(this.width - 400);
		        kid.setpY(0);
			}else if(toboganState == tOPEN && distanceIR != 0 && !toboganSemaphore){
				actualTime = System.currentTimeMillis();
				toboganState = tWAIT;
				toboganSemaphore = true;
				jumpSemaphore = true;
				// ni–o sentado a la espera de poder saltar
				kid.setpX(this.width - 400);
		        kid.setpY(0);
			}
			
			if(toboganState == tWAIT && myTime > waitTime){
				if(jumpSemaphore == true){
					toboganState = tJUMP; // seï¿½al de poder saltar
					//Log.d("Game Tobogan","JUMP!");
					jumpSemaphore = false;
				}
			}else if(toboganState == tWAIT && myTime < waitTime && distanceIR == 0){
				//Log.d("Game Tobogan","RESET!");
				toboganState = tRESTART;
				toboganSemaphore = true;
				jumpSemaphore = true;
			}
			
			if(toboganState == tJUMP && distanceIR == 0){ // cuando el niï¿½o salta
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
		if (playerScore == 10){
			gameState = WON;
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
		
		if (playerScore == 10){
			gameState = WON;
		}

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
			if (gameState == WON)		updateWon();
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
	
	private void updateWon(){
		pause();
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
