package com.hybridplay.fishing;


public class Stage {
	
	//position by grid
		private int pX;
		private int pY;
		//private int pwidth;
		//private int pheight;
		
		private int pXOrigin;
		private int pYOrigin;
	
	
	
	//constructor
	public Stage(int x, int y) {
		pXOrigin = x;
		pYOrigin = y;
		pX = pXOrigin;
		pY = pYOrigin;
		
		//pwidth = width;
		//pheight = height;
	
	}



	public int getpX() {
		return pX;
	}



	public void setpX(int pX) {
		this.pX = pX;
	}



	public int getpY() {
		return pY;
	}



	public void setpY(int pY) {
		this.pY = pY;
	}



//	public int getPwidth() {
//		return pwidth;
//	}
//
//
//
//	public void setPwidth(int pwidth) {
//		this.pwidth = pwidth;
//	}
//
//
//
//	public int getPheight() {
//		return pheight;
//	}
//
//
//
//	public void setPheight(int pheight) {
//		this.pheight = pheight;
//	}



	public int getpXOrigin() {
		return pXOrigin;
	}



	public void setpXOrigin(int pXOrigin) {
		this.pXOrigin = pXOrigin;
	}



	public int getpYOrigin() {
		return pYOrigin;
	}



	public void setpYOrigin(int pYOrigin) {
		this.pYOrigin = pYOrigin;
	}
	
	


}