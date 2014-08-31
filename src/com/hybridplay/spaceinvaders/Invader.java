/*
 *  Space Invaders
 *
 *  Copyright (C) 2012 Glow Worm Applications
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Street #330, Boston, MA 02111-1307, USA.
 */

package com.hybridplay.spaceinvaders;

public class Invader extends GridObject {

	Invader(boolean[][] b) {
		super(b);
	}
	Invader(boolean[][] b,int Xco, int Yco) {
		super(b,Xco,Yco);
	}
	boolean moveInvalid(int level, boolean right, int levelSize){
		
		if( !right ){
			if(0 > x-level){
				return true;
			}
		} else {
			if(levelSize < x+level+ObjectManager.INVADER_WIDTH){
				return true;
			}
		}
		return false;
	}
	void horizontalMove(int level, boolean right){
		if(!right){
			level*=-1;
		}
		x+=level;
	}
	void verticalMove(){
		y+=ObjectManager.INVADER_HEIGHT;
	}
}

