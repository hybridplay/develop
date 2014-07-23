package uk.co.coldasice.projects.android.arkadroid.sprites;

import uk.co.coldasice.projects.android.arkadroid.controllers.GameRenderer;
import android.graphics.drawable.Drawable;

public class SpriteBall extends Sprite_Trail {
	private static final double INITIAL_DXY = 1.5;
	public static final double MAX_SPEED = 7;

	public double dx = INITIAL_DXY;
	public double dy = INITIAL_DXY;
	
	public boolean onPaddle = true;
	
	public SpriteBall(Drawable drawable, GameRenderer renderer) {
		super(drawable, renderer);
	}

	public double speed() {
		return speed(dx,dy);
	}
	
	public static double speed(double _dx, double _dy) {
		return Math.sqrt((_dx * _dx) + (_dy * _dy));
	}
	
	public void reset() {
		super.reset();
		dx = 0;
		dy = INITIAL_DXY;
		onPaddle = true;
	}
	
}
