package com.hybridplay.puzzlecity;

public class Kid {
	
	public static final int UP = 4;
	public static final int DOWN = 8;
	public static final int RIGHT = 1;
	public static final int LEFT = 2;
	public static final int CENTER = 0;
	public boolean hasChangedDir = false;
	
	private boolean eating;
	
	//position by grid
	private float pX;
	private float pY;
	private int pwidth;
	private int pheight;
	private float pangle;

	private float pXOrigin;
	private float pYOrigin;

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
	}
	
	public void reset(){
		pX = pXOrigin;
		pY = pYOrigin;
		dir = RIGHT;
		hasChangedDir = false;
	}

	public float getpX() {
		return pX;
	}

	public float getpY() {
		return pY;
	}

	public float getpXOrigin() {
		return pXOrigin;
	}

	public float getpYOrigin() {
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
		if(this.dir != dir){
			this.dir = dir;
			hasChangedDir = true;
		}
	}

	public void setpX(float pX) {
		this.pX = pX;
	}

	public void setpY(float pY) {
		this.pY = pY;
	}

	public void setpXOrigin(float pXOrigin) {
		this.pXOrigin = pXOrigin;
	}

	public void setpYOrigin(float pYOrigin) {
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
