package com.hybridplay.spacekids.propeller;

public class Kid {

	public static final int UP = 4;
	public static final int DOWN = 8;
	public static final int RIGHT = 1;
	public static final int LEFT = 2;
	public static final int CENTER = 0;
	
	private boolean eatUp, eatDown, eatleft, eatRight, eating;
	
	//position by grid
	private int pX;
	private int pY;
	private int pwidth;
	private int pheight;
	private float pangle;
	
	private int pXOrigin;
	private int pYOrigin;
	
	private int pLives;
	private int pNormalSpeed;
	private int pPowerSpeed;
	
	private int dir; // direction of movement 0 = not moving
	
	public Kid (int x, int y, int width, int height){
		pXOrigin = x;
		pYOrigin = y;
		pX = pXOrigin;
		pY = pYOrigin;
		setAngle(0);
		pLives = 3;
		pNormalSpeed = 2;
		pPowerSpeed = 4;
		dir = RIGHT;
		pwidth = width;
		pheight = height;
		eatUp = false;
		eatDown = false;
		eatleft= false;
		eatRight = false;
		eating = false;
	}
	
	public boolean isEatUp() {
		return eatUp;
	}

	public void setEatUp(boolean eatUp) {
		this.eatUp = eatUp;
	}

	public boolean isEatDown() {
		return eatDown;
	}

	public void setEatDown(boolean eatDown) {
		this.eatDown = eatDown;
	}

	public boolean isEatleft() {
		return eatleft;
	}

	public void setEatleft(boolean eatleft) {
		this.eatleft = eatleft;
	}

	public boolean isEatRight() {
		return eatRight;
	}

	public void setEatRight(boolean eatRight) {
		this.eatRight = eatRight;
	}

	public void reset(){
		pX = pXOrigin;
		pY = pYOrigin;
		dir = RIGHT;
	}

	public int getpX() {
		return pX;
	}

	public int getpY() {
		return pY;
	}

	public int getpXOrigin() {
		return pXOrigin;
	}

	public int getpYOrigin() {
		return pYOrigin;
	}

	public int getpLives() {
		return pLives;
	}

	public int getpNormalSpeed() {
		return pNormalSpeed;
	}

	public int getpPowerSpeed() {
		return pPowerSpeed;
	}

	public int getDir() {
		return dir;
	}
	
	public void setDir(int dir){
		this.dir = dir;
	}

	public void setpX(int pX) {
		this.pX = pX;
	}

	public void setpY(int pY) {
		this.pY = pY;
	}

	public void setpXOrigin(int pXOrigin) {
		this.pXOrigin = pXOrigin;
	}

	public void setpYOrigin(int pYOrigin) {
		this.pYOrigin = pYOrigin;
	}

	public int getPwidth() {
		return pwidth;
	}

	public void setPwidth(int pwidth) {
		this.pwidth = pwidth;
	}

	public int getPheight() {
		return pheight;
	}

	public void setPheight(int pheight) {
		this.pheight = pheight;
	}

	public boolean isEating() {
		return eating;
	}

	public void setEating(boolean eating) {
		this.eating = eating;
	}

	public float getAngle() {
		return pangle;
	}

	public void setAngle(float pangle) {
		this.pangle = pangle;
	}
	
	
	
}
