package uk.co.coldasice.projects.android.arkadroid.controllers;

import java.util.ArrayList;
import java.util.Random;

import uk.co.coldasice.projects.android.arkadroid.R;
import uk.co.coldasice.projects.android.arkadroid.sprites.SpriteBall;
import uk.co.coldasice.projects.android.arkadroid.sprites.SpriteBrick;
import uk.co.coldasice.projects.android.arkadroid.sprites.SpritePaddle;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class GameState {

	enum RunningOrPaused {
		paused, running
	}
	
	RunningOrPaused runningOrPaused = RunningOrPaused.paused;
	private static final Random random = new Random();

	private static final int MAX_LIVES = 5;
	private int livesLeft = MAX_LIVES + 1;
	private int currentScore = 0;
	
	String livesLeftStr = Integer.toString(livesLeft);
	String scoreStr = Integer.toString(currentScore);
	
	// sprites
	public SpriteBall ball;
	public SpritePaddle paddle;
	ArrayList<SpriteBrick> bricks;
	
	int bricksKilledInARow = 0;
	boolean winner = false;
	String infoText = "";
	String wonText;
	String diedText;
	String livesText;
	
	public GameState() {
	}

	public void init(Resources r, GameRenderer renderer) {
		ball = new SpriteBall(r.getDrawable(R.drawable.ball), renderer);
		paddle = new SpritePaddle(r.getDrawable(R.drawable.paddle), renderer, ball);
		bricks = new ArrayList<SpriteBrick>();
		Drawable brickImg = r.getDrawable(R.drawable.brick);
		int paddingTop = 20;
		int paddingSides = 60;
		int howManyBricks = (renderer.w-paddingSides) / brickImg.getIntrinsicWidth();
		int brickOffsetX = (renderer.w - (howManyBricks * brickImg.getIntrinsicWidth())) / 2;
		for (int j=0; j<5; j++) {
			for (int i=0; i < howManyBricks; i++) {
				SpriteBrick brick = new SpriteBrick(brickImg, renderer, random.nextInt(0x00FFFFFF), 
						i * brickImg.getIntrinsicWidth() + brickOffsetX, 
						(j * brickImg.getIntrinsicHeight()) + paddingTop);
				bricks.add(brick);
			}
		}
		wonText = r.getString(R.string.won_text);
		diedText = r.getString(R.string.died_text);
	}
	
	public boolean isRunning() {
		return runningOrPaused == RunningOrPaused.running;
	}
	
	public boolean isPaused() {
		return runningOrPaused == RunningOrPaused.paused;
	}
	
	public void setRunning() {
		runningOrPaused = RunningOrPaused.running;
	}
	
	public void setPaused() {
		runningOrPaused = RunningOrPaused.paused;
	}
	
	int getScoreToAdd() {
		int scoreToAdd = 0;
		for (int i=0; i<bricksKilledInARow; i++) {
			scoreToAdd += i+1;
		}
		return scoreToAdd;
	}

	public void reset() {
		boolean fullReset = false;
		if (isRunning()) {
			currentScore += getScoreToAdd();
			bricksKilledInARow = 0;
			livesLeft--;
		}
		if (livesLeft <= 0) {
			fullReset = true;
			infoText = diedText;
			livesLeft = MAX_LIVES;
		}
		else {
			if (winner) {
				fullReset = true;
				infoText = wonText;
				livesLeft = MAX_LIVES;
			}
		}
		paddle.reset();
		ball.reset();
		if (fullReset) {
			currentScore = 0;
			for (SpriteBrick sp: bricks) {
				sp.unkill();
			}
		}
		livesLeftStr = Integer.toString(livesLeft);
		scoreStr = Integer.toString(currentScore);
	}
	
	public void updateScoreString(){
		scoreStr = Integer.toString(currentScore+getScoreToAdd());

	}

}
