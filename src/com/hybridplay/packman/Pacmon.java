package com.hybridplay.packman;

public class Pacmon {

	public static final int UP = 4;
	public static final int DOWN = 8;
	public static final int RIGHT = 1;
	public static final int LEFT = 2;
	public static final int CENTER = 0;
	
	//position by grid
	private int pX;
	private int pY;
	
	private int pXOrigin;
	private int pYOrigin;
	
	private int pLives;
	private int pNormalSpeed;
	private int pPowerSpeed;
	
	private int dir; // direction of movement 0 = not moving
	
	public Pacmon (){
		pX = pY = 32;
		pXOrigin = pYOrigin = 1;
		pLives = 3;
		pNormalSpeed = 2;
		pPowerSpeed = 4;
		dir = CENTER;
	}
	
	public void reset(){
		pX = pY = 32;
		pXOrigin = pYOrigin = 1;
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
	
}
