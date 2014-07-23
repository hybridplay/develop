package com.hybridplay.spacekids.propeller;

public class Trash {

	private int x, initX;
	private int y, initY;
	private int dir;
	private int normalSpeed;
	private boolean active;
	private float degree;
	private float rotation;
	
//	private int state;  // 0 = in cage, 1 = door step, 2 outside
	
	
	public Trash(int dir, int x, int y){
		this.x = x;
		initX = x;
		this.y = y;
		initY = y;
		this.dir = dir;
		normalSpeed = 1;
		active = true;
		//this.rotation = rotation;
		//degree = 0;
//		state = 0;
	}
	
	// reset ghost when player die
	public void reset(int kidx, int kidy){
		x = kidx + initX;
		y = kidy + initY;
		active = true;
		//dir = 8;
	}

	

	public int getX() {
		return x;
	}



	public void setX(int x) {
		this.x = x;
	}



	public int getY() {
		return y;
	}



	public void setY(int y) {
		this.y = y;
	}



	public int getDir() {
		return dir;
	}



	public int getNormalSpeed() {
		return normalSpeed;
	}



	public void setNormalSpeed(int normalSpeed) {
		this.normalSpeed = normalSpeed;
	}



	public void setDir(int newDirection) {
		this.dir = newDirection;
		
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public float getDegree() {
		return degree;
	}

	public void setDegree(float degree) {
		this.degree = degree;
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}


//
//	public int getState() {
//		return state;
//	}
//
//
//
//	public void setState(int state) {
//		this.state = state;
//	}
//	
}
