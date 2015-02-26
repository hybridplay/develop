package com.hybridplay.puzzlecity;

import java.util.ArrayList;

public class GameEngine implements Runnable {
	
	private final static int    MAX_FPS = 50;
	// maximum number of frames to be skipped
	// the frame period
	private final static int    FRAME_PERIOD = 1000 / MAX_FPS;
	static final int  RIGHT = 1, LEFT = 2, UP = 4, DOWN = 8, CENTER = 0;
	public final static int 	READY = 0,RUNNING = 1, GAMEOVER = 2, WON = 3, DIE = 4, BOOM = 5;//, CONNECTING = 5;
	int screenWidth;
	
	// SENSOR DATA
	float angleX, angleY, angleYColumpio, angleZ;
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
	public int toboganBackPosX = 0, toboganBackPosXReset = 0;
	boolean firstJump = true;
	public float kVX = 5.3f, kVY = 6.8f;
	public long actualTime = System.currentTimeMillis();
	public long myTime;
	public long waitTime = 2500;
	
	private Thread mThread;
	public int playerScore;
	int timer; int timerCount;
	int lives;
	public Player player;
	Stage stage;
	Nube nube;
	Objetos obj;
	Avion avion;
	Fichas fichas;
	public ArrayList<Fichas> fichasArray;
	
	public int gameState;    // ready = 0; running = 1; lost == 2; won = 3;
	int powerMode;		// for count down of power speed
	int pNormalSpeed;	// pacmon normal moving speed
	int pPowerSpeed;	// pacmon power moving speed, when eat Power
	int inputDirection;
	int pX, pY;
	int newDirection;
	
	boolean checkBoom = false;
	boolean comeBack = false;
	
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
	public float minPX=10000, maxPX=0, minPYL=10000, minPYR=10000;
	public boolean updateRaquetas = true;
	
	// Device
	int width, height;
	
	//Constructor create players, trash and stage
	public GameEngine(int width, int height){
		this.width = width;
		this.height = height;

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
	
	public void updateSensorColumpio(float yC){
		angleYColumpio = yC;
	}
	
	//update
	public void update(){
		updateTimer();
		updatePlayer();
		updateNubes();
	}
	
	public void updatePlayer(){
		
		pX = (int)player.getpX();
		pY = (int)player.getpY();
		int limitW = (int) width / 4;
		int limitH = (int) height / 4;
		
		if(getGameType().equals("Balancin")){ // ---------------- Balancin
			// pinza horizontal - cuatro direcciones - ejes Z Y
				if (triggerYL ){//{&& !triggerZL && !triggerZR) {
					player.setDir(DOWN);
					player.setCurrentAnimation(0);
					if (stage.canMove(DOWN, pX, pY, player.getSpriteWidth(), player.getSpriteHeight(),player.getpNormalSpeed())){
						stage.setpY(stage.getpY()+player.getpNormalSpeed());
						/*if(stage.getpX() >= -379 && stage.getpX() <= 1847 && stage.getpY() >= -177 && stage.getpY() <= 577){
							for (int i = 0; i < fichasArray.size(); i++) {
								if (fichasArray.get(i).isAlive()){
									fichasArray.get(i).setpY(fichasArray.get(i).getpY()-player.getpNormalSpeed());
								}
								//fichas.setpY(fichas.getpY()-player.getpNormalSpeed());
							}
						}*/
					}
				} else if (triggerYR ){// && !triggerZL && !triggerZR) {
					player.setDir(UP);
					player.setCurrentAnimation(1);
					if (stage.canMove(UP,pX, pY, player.getSpriteWidth(), player.getSpriteHeight(),player.getpNormalSpeed())){
						stage.setpY(stage.getpY()-player.getpNormalSpeed());
						/*if(stage.getpX() >= -379 && stage.getpX() <= 1847 && stage.getpY() >= -177 && stage.getpY() <= 577){
							for (int i = 0; i < fichasArray.size(); i++) {
								if (fichasArray.get(i).isAlive()){
									fichasArray.get(i).setpY(fichasArray.get(i).getpY()+player.getpNormalSpeed());
									//fichas.setpY(fichas.getpY()+player.getpNormalSpeed());
								}
							}
						}*/
					}
				}
				
				if (triggerZR ){// && !triggerYL && !triggerYR) {
					player.setDir(RIGHT);
					player.setCurrentAnimation(2);
					if (stage.canMove(RIGHT, pX, pY, player.getSpriteWidth(), player.getSpriteHeight(),player.getpNormalSpeed())){
						stage.setpX(stage.getpX()+player.getpNormalSpeed());
						/*if(stage.getpX() >= -379 && stage.getpX() <= 1847 && stage.getpY() >= -177 && stage.getpY() <= 577){
							for (int i = 0; i < fichasArray.size(); i++) {
								if (fichasArray.get(i).isAlive()){
									fichasArray.get(i).setpX(fichasArray.get(i).getpX()-player.getpNormalSpeed());
									//fichas.setpX(fichas.getpX()-player.getpNormalSpeed());
								}
							}
						}*/
					}
				} else if (triggerZL){// && !triggerYL && !triggerYR) {
					player.setDir(LEFT);
					player.setCurrentAnimation(3);
					if (stage.canMove(LEFT, pX, pY, player.getSpriteWidth(), player.getSpriteHeight(),player.getpNormalSpeed())){
						stage.setpX(stage.getpX()-player.getpNormalSpeed());
						/*if(stage.getpX() >= -379 && stage.getpX() <= 1847 && stage.getpY() >= -177 && stage.getpY() <= 577){
							for (int i = 0; i < fichasArray.size(); i++) {
								if (fichasArray.get(i).isAlive()){
									fichasArray.get(i).setpX(fichasArray.get(i).getpX()+player.getpNormalSpeed());
									//fichas.setpX(fichas.getpX()+player.getpNormalSpeed());
								}
							}
						}*/
					}
				}
			
				if (stage.checkExit(player.getSpriteWidth(), player.getSpriteHeight(),player.getpNormalSpeed())){
					gameState = WON;
				}
				 
			player.updatePlayer(System.currentTimeMillis());
			
//			for (int i = 0; i < fichasArray.size(); i++) {
//				if (fichasArray.get(i).isAlive()){
//					fichasArray.get(i).updateFicha();
//				}
//			}
			
			
		}else if(getGameType().equals("Caballito")){ // ---------- Caballito
			
			// pinza vertical boton hacia abajo - cuatro direcciones - ejes X Y
			
			// --------> CAMBIAR LA Y POR LA Z SI LA PINZA VA EN LA CABEZA DEL CABALLITO
			
			if (triggerZL){// && !triggerXL && !triggerXR) {
				player.setDir(DOWN);
				player.setCurrentAnimation(0);
				if (stage.canMove(DOWN, pX, pY, player.getSpriteWidth(), player.getSpriteHeight(),player.getpNormalSpeed())){
					stage.setpY(stage.getpY()+player.getpNormalSpeed());
//					if(stage.getpX() >= -379 && stage.getpX() <= 1847 && stage.getpY() >= -177 && stage.getpY() <= 577){
//						for (int i = 0; i < fichasArray.size(); i++) {
//							if (fichasArray.get(i).isAlive()){
//								fichasArray.get(i).setpY(fichasArray.get(i).getpY()-player.getpNormalSpeed());
//								//fichas.setpY(fichas.getpY()-player.getpNormalSpeed());
//							}
//						}
//					}
				}
			}else if (triggerZR){// && !triggerXL && !triggerXR) {
				player.setDir(UP);
				player.setCurrentAnimation(1);
				if (stage.canMove(UP,pX, pY, player.getSpriteWidth(), player.getSpriteHeight(),player.getpNormalSpeed())){
					stage.setpY(stage.getpY()-player.getpNormalSpeed());
//					if(stage.getpX() >= -379 && stage.getpX() <= 1847 && stage.getpY() >= -177 && stage.getpY() <= 577){
//						for (int i = 0; i < fichasArray.size(); i++) {
//							if (fichasArray.get(i).isAlive()){
//								fichasArray.get(i).setpY(fichasArray.get(i).getpY()+player.getpNormalSpeed());
//								//fichas.setpY(fichas.getpY()+player.getpNormalSpeed());
//							}
//						}
//					}
				}
			}
			
			if (triggerXR){// && !triggerZL && !triggerZR) {
				player.setDir(RIGHT);
				player.setCurrentAnimation(2);
				if (stage.canMove(RIGHT, pX, pY, player.getSpriteWidth(), player.getSpriteHeight(),player.getpNormalSpeed())){
					stage.setpX(stage.getpX()+player.getpNormalSpeed());
//					if(stage.getpX() >= -379 && stage.getpX() <= 1847 && stage.getpY() >= -177 && stage.getpY() <= 577){
//						for (int i = 0; i < fichasArray.size(); i++) {
//							if (fichasArray.get(i).isAlive()){
//								fichasArray.get(i).setpX(fichasArray.get(i).getpX()-player.getpNormalSpeed());
//								//fichas.setpX(fichas.getpX()-player.getpNormalSpeed());
//							}
//						}
//					}
				}
			}else if (triggerXL){// && !triggerZL && !triggerZR) {
				player.setDir(LEFT);
				player.setCurrentAnimation(3);
				if (stage.canMove(LEFT, pX, pY, player.getSpriteWidth(), player.getSpriteHeight(),player.getpNormalSpeed())){
					stage.setpX(stage.getpX()-player.getpNormalSpeed());
//					if(stage.getpX() >= -379 && stage.getpX() <= 1847 && stage.getpY() >= -177 && stage.getpY() <= 577){
//						for (int i = 0; i < fichasArray.size(); i++) {
//							if (fichasArray.get(i).isAlive()){
//								fichasArray.get(i).setpX(fichasArray.get(i).getpX()+player.getpNormalSpeed());
//								//fichas.setpX(fichas.getpX()+player.getpNormalSpeed());
//							}
//						}
//					}
				}				
			}
		
			if (stage.checkExit(player.getSpriteWidth(), player.getSpriteHeight(),player.getpNormalSpeed())){
				gameState = WON;
			}
				 
			player.updatePlayer(System.currentTimeMillis());
			
//			for (int i = 0; i < fichasArray.size(); i++) {
//				if (fichasArray.get(i).isAlive()){
//					fichasArray.get(i).updateFicha();
//				}
//			}
			
		}else if(getGameType().equals("Columpio")){ // ---------- Columpio
			// pinza vertical boton hacia abajo - oscilacion - eje X
	        float tempMedia = 0;
	        boolean goingRight = false;
	        for(int i=0;i<100;i++){
	        	tempMedia += angleYColumpio;
	        }
	        tempMedia /= 100;
	        
	        if(angleYColumpio > tempMedia){
	        	goingRight = false;
	        }else{
	        	goingRight = true;
	        }
	        
	        // update kid position
	        player.setAngle((float)Math.toRadians(angleYColumpio));
	        player.setpX(this.width/2 - 50 - (float)Math.sin(player.getAngle())*200);
	        player.setpY(this.height - 390 + (float)Math.cos(player.getAngle())*210);
	        
	        float flag = (float)Math.sin(player.getAngle());
	        
	        if(flag < 0.0f){
				player.setDir(RIGHT);
			}else{
				player.setDir(LEFT);
			}
	        
	        if(updateRaquetas){
	        	if(player.getDir() == LEFT){
	        		minPX = player.getpX();
	        		minPYL = player.getpY();
	        	}else if(player.getDir() == RIGHT){
	        		maxPX = player.getpX();
	        		minPYR = player.getpY();
	        	}
	        	updateRaquetas = false;
	        }
	        
	        for(int i=0;i<100;i++){
	        	tempMedia += angleYColumpio;
	        }
	        tempMedia /= 100;
	        
	        if(angleYColumpio > tempMedia && goingRight){
	        	updateRaquetas = true;
	        }else if(angleY < tempMedia && !goingRight){
	        	updateRaquetas = true;
	        }
			
		}else if(getGameType().equals("Rueda")){ // ---------- Rueda
			// pinza vertical boton hacia abajo - oscilacion - eje X
			
			
		}else if(getGameType().equals("SubeBaja")){ // ---------- SubeBaja
			// pinza horizontal/vertical - dos direcciones - eje Z || eje X
			
			if(triggerZR || triggerYR){
				if(avion.vX < 4) avion.vX += 1.4f; //se mueve a la derecha
				if(avion.vY >= -2.5) {
					avion.vY -= 2.5; //se eleva
					if(avion.vY < -2.5) avion.vY = (float) -2.5;
				}
			}
			
			avion.updateAvion();
			
			fichas.updateFicha();
			
		}else if(getGameType().equals("Tobogan")){ // ---------- Tobogan
			// we use here only IR sensor
			// system timeline update
			myTime = System.currentTimeMillis() - actualTime;
			
			if(distanceIR == 0 && toboganSemaphore && toboganState == tRESTART){
				toboganSemaphore = false;
				toboganState = tOPEN;
				player.setpX(this.width - 400);
		        player.setpY(0);
			}else if(toboganState == tOPEN && distanceIR != 0 && !toboganSemaphore){
				actualTime = System.currentTimeMillis();
				toboganState = tWAIT;
				toboganSemaphore = true;
				jumpSemaphore = true;
				// ni�o sentado a la espera de poder saltar
				player.setpX(this.width - 400);
		        player.setpY(0);
			}
			
			if(toboganState == tWAIT && myTime > waitTime){
				if(jumpSemaphore == true){
					toboganState = tJUMP; // se�al de poder saltar
					//Log.d("Game Tobogan","JUMP!");
					jumpSemaphore = false;
				}
			}else if(toboganState == tWAIT && myTime < waitTime && distanceIR == 0){
				//Log.d("Game Tobogan","RESET!");
				toboganState = tRESTART;
				toboganSemaphore = true;
				jumpSemaphore = true;
			}
			
			if(toboganState == tJUMP && distanceIR == 0){ // cuando el ni�o salta
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
			if (firstJump) toboganBackPosXReset = toboganBackPosX; 
			firstJump = false;
			int pX = (int)player.getpX();
			int pY = (int)player.getpY();
			
			pX -= kVX;
			pY += kVY;
			
			toboganBackPosX += kVX;
			
			if(toboganBackPosX > screenWidth*2.4){
				gameState = WON; // WIN
			}
			
			player.setpX(pX);
	        player.setpY(pY);
	        
	        if(pY > this.height + 10){
	        	//Log.d("PuzzleCity","WE LOSE");
	        	toboganJump = false;
	        	toboganState = tRESTART;
	    		toboganSemaphore = true;
	    		jumpSemaphore = true;
	    		actualTime = System.currentTimeMillis();
	    		toboganBackPosX = toboganBackPosXReset; 
	    		firstJump = true;
	    		comeBack = true;
	        }
		}
	}
	
	//check if kid take objects on columpio
	private void checkObjetosOnFicha(float gX, float gY){
		int pX = (int)player.getpX();
		int pY = (int)player.getpY();
		int radius = (int) width / 6 ; // 190 con pantalla de 800 

		if (Math.abs(pX - gX) + Math.abs(pY - gY) < radius) { // kid touch ficha
			if(obj.isBomba){
				checkBoom = true;
			}else{
				playerScore++;   // increase score
			}
			obj.reloadObjeto();
		}
	}
	
	//check if kid jump on nube with ficha
	private void checkNubeOnFicha(float gX, float gY){
		int pX = (int)player.getpX();
		int pY = (int)player.getpY();
		int radius = (int) width / 4 ; // 190 con pantalla de 800 
		
		if (Math.abs(pX - gX) + Math.abs(pY - gY) < radius) { // kid touch ficha or nube negra
			if(nube.hasFicha){
				eatFicha();
			}/*else if(nube.isBlack){
				nubeNegra();
			}*/
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
	
//	// nube negra ==> come back + time
//	private void nubeNegra() {
//		//Log.d("PuzzleCity","WE WIN");
//		comeBack = true;
//		nube.reloadNube();
//		toboganJump = false;
//		toboganState = tRESTART;
//		toboganBackPosX = 0; // come back
//		timer = 120; // reset timer
//		toboganSemaphore = true;
//		jumpSemaphore = true;
//		actualTime = System.currentTimeMillis();
//
//	}

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
			if (gameState == WON) updateGameWon();
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
	
	private void updateGameWon(){
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

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public String getGameType() {
		return gameType;
	}

	public void setGameType(String gameType) {
		this.gameType = gameType;
	}

}
