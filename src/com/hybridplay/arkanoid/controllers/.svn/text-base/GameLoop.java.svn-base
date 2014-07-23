package uk.co.coldasice.projects.android.arkadroid.controllers;

import android.graphics.Rect;
import uk.co.coldasice.projects.android.arkadroid.controllers.GameState;
import uk.co.coldasice.projects.android.arkadroid.sprites.SpriteBall;
import uk.co.coldasice.projects.android.arkadroid.sprites.SpriteBrick;

public class GameLoop {
	double ballW,ballH,ballX,ballY;
	private static final double PHYSICS_SPEED = 13.0;
	
	private GameState gameState;
	double timediff;
	private long lastupdate;
	
	private double dXRemaining,dYRemaining;
	
	Rect newBallBound = new Rect();
	
	public GameLoop(GameState state) {
		this.gameState = state;
	}
	
	public void init(){
		ballW = gameState.ball.w;
		ballH = gameState.ball.h;
	}
	
	public void reset() {
		lastupdate = System.currentTimeMillis();
	}
	
	public void updateGame(GameRenderer renderer) {
		long now = System.currentTimeMillis();
		timediff = (now - lastupdate) / PHYSICS_SPEED;
		lastupdate = now;
		if (timediff>10.0) {
			timediff = 10.0;
		}
		
		//Move Paddle
		gameState.paddle.update(timediff);
		
		if(gameState.ball.onPaddle)return;
		
		//Set total delta X and Y required for this update
		dXRemaining = gameState.ball.dx*timediff;
		dYRemaining = gameState.ball.dy*timediff;
		
		do{
			ballX = gameState.ball.x;
			ballY = gameState.ball.y;
			
			//Loser ??
			if (ballY >= gameState.paddle.y + gameState.paddle.h) {
				gameState.setPaused();
				return;
			}
			
			//New ball position after current dX and dY
			double newX = ballX+dXRemaining;
			double newY = ballY+dYRemaining;
			newBallBound.set((int)newX, (int)newY, (int)(newX+ballW), (int)(newY+ballH));
		
			//Default limits are the walls
			double limitX = renderer.w;
			double limitY = renderer.h;
			if(dXRemaining < 0.0)limitX = 0.0;
			if(dYRemaining < 0.0)limitY = 0.0;
			
			boolean brickHit = false;
			boolean paddleHit = false;
			boolean allDead = true;
			
			//Have you hit a brick?
			for (int i=0;i<gameState.bricks.size();i++) {
				SpriteBrick brick = gameState.bricks.get(i);
				if (brick.dead()) continue;
				allDead = false;
				if (brick.collidesWith(newBallBound)) {
					SoundController.playBrickSound();
					brickHit = true;
					brick.kill();
					// add one to the current run (how many bricks killed in a row)
					gameState.bricksKilledInARow++;
					gameState.updateScoreString();
					
					//Limit is the edge of the brick facing the ball
					limitX = brick.x;
					if(dXRemaining < 0)limitX += brick.w;
					limitY = brick.y;
					if(dYRemaining < 0)limitY += brick.h;
					
					break;
				}
			}
			//Your A Real Winner!
			if (allDead) {
				SoundController.playVictorySound();
				gameState.winner = true;
				gameState.setPaused();
				return;
			}
			
			//If you missed the bricks maybe you hit the paddle
			if(!brickHit){
				if(gameState.paddle.collidesWith(newBallBound)){
					paddleHit = true;
					//Limit is the edge of the paddle facing the ball
					limitX = gameState.paddle.x;
					if(dXRemaining < 0)limitX += gameState.paddle.w;
					limitY = gameState.paddle.y;
					if(dYRemaining < 0)limitY += gameState.paddle.h;
					SoundController.playPaddleSound();
				}
			}
			//If moving left i.e. dX >= 0 check X limit
			if(dXRemaining >= 0.0 && newX >= limitX-ballW){
				double canTravelX = (limitX-ballW)-ballX;
				double canTravelY = (canTravelX/dXRemaining)*dYRemaining;	
				//Check max Y distance
				double canTravelY2 = checkY(newY,limitY);
				//If implied Y distance is less than limit Y distance move the ball. otherwise fall through and let Y bounce first.
				if(swapAxis(canTravelY,canTravelY2)){	
					moveBall(canTravelX,canTravelY);
					bounceX();
					if(!brickHit && !paddleHit)SoundController.playWallSound();
					continue;
				}
			}
			//If moving right i.e. dX < 0 check X limit
			if(dXRemaining < 0.0 && newX < limitX){
				double canTravelX = limitX - ballX;
				double canTravelY = (canTravelX/dXRemaining)*dYRemaining;
				double canTravelY2 = checkY(newY,limitY);
				//If implied Y distance is less than limit Y distance move the ball. otherwise fall through and let Y bounce first.
				if(swapAxis(canTravelY,canTravelY2)){
					moveBall(canTravelX,canTravelY);
					bounceX();
					if(!brickHit && !paddleHit)SoundController.playWallSound();
					continue;
				}
			}	
			//If moving down i.e. dY >= 0 check Y limit
			if(dYRemaining >= 0.0 && newY >= limitY-ballH){
				double canTravelY = limitY-ballH - ballY;
				double canTravelX = (canTravelY/dYRemaining)*dXRemaining;
				//Always move ball. Earlier checks would have continued in X limit crossed first.
				moveBall(canTravelX,canTravelY);
				//Check for paddle hit and do magic it required
				if(paddleHit)paddleHit();
				else {
					bounceY();
					if(!brickHit)SoundController.playWallSound();
				}
				continue;
			}
			//If moving up i.e. dY < 0 check Y limit
			if(dYRemaining < 0.0 && newY < limitY){
				double canTravelY = limitY - ballY;
				double canTravelX = (canTravelY/dYRemaining)*dXRemaining;
				//Always move ball. Earlier checks would have continued in X limit crossed first.
				moveBall(canTravelX,canTravelY);
				bounceY();
				if(!brickHit && !paddleHit)SoundController.playWallSound();
				continue;
			}
			moveBall(dXRemaining,dYRemaining);
			break;
			
		}while(true);
	}

	private boolean swapAxis(double canTravelY, double canTravelY2) {
		return Math.abs(canTravelY2) >= Math.abs(canTravelY);
	}
	
	private double checkY(double newY, double limitY){
		double canTravelY2=dYRemaining;
		if(dYRemaining >= 0.0 && newY >= limitY-ballH){
			canTravelY2 = limitY - ballH - ballY;
		}
		if(dYRemaining < 0.0 && newY < limitY){
			canTravelY2 = limitY - ballY;
		}
		return canTravelY2;
	}
	
	private void moveBall(double canTravelX, double canTravelY){
		gameState.ball.setX(ballX+canTravelX);
		gameState.ball.setY(ballY+canTravelY);
		dXRemaining -= canTravelX;
		dYRemaining -= canTravelY;
	}
	
	private void bounceX(){
		dXRemaining *= -1.0;
		gameState.ball.dx *= -1.0;
	}
	
	private void bounceY(){
		dYRemaining *= -1.0;
		gameState.ball.dy *= -1.0;
	}
	
	private void paddleHit(){
		double ballMidx = gameState.ball.getMidX();
		double paddleMidx = gameState.paddle.getMidX();
		
		// get the original ball speed
		double speed = gameState.ball.speed();
		double speedRemaining = SpriteBall.speed(dXRemaining,dYRemaining);
		
		// get the diff between the mid points
		double diff = paddleMidx - ballMidx;
		if (Math.abs(diff) < 3) {
			bounceY();
		}
		else {
			double maxSpin = (gameState.paddle.w+20) / 2;
			double spin = diff / maxSpin;
			
			double shootAngle = 90 - (90 * spin);
			double new_ballDy = Math.sin(Math.toRadians(shootAngle));
			double new_ballDx = 0 - Math.cos(Math.toRadians(shootAngle));
			
			gameState.ball.dx = new_ballDx * speed;
			dXRemaining = new_ballDx * speedRemaining;
			
			if (new_ballDy > 0) new_ballDy = -new_ballDy;
			gameState.ball.dy = new_ballDy * speed;
			dYRemaining = new_ballDy * speedRemaining;
		}
		// speed up the ball a bit
		if(speed < SpriteBall.MAX_SPEED){
			gameState.ball.dx *= 1.08;
			gameState.ball.dy *= 1.08;
		}
	}
}
