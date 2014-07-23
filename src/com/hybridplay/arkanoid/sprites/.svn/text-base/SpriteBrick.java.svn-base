package uk.co.coldasice.projects.android.arkadroid.sprites;

import uk.co.coldasice.projects.android.arkadroid.controllers.GameRenderer;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;


public class SpriteBrick extends Sprite {
	private ShapeDrawable colour;
	
	private boolean killed = false;
	
	public SpriteBrick(Drawable drawable, GameRenderer renderer, int brickColour, double _x, double _y) {
		super(drawable, renderer, _x, _y);
		colour = new ShapeDrawable(new RectShape());
		colour.setBounds((int)x+3, (int)y+2, (int)(x+w)-3, (int)(y+h)-3);
		colour.getPaint().setColor(0x66000000+brickColour);
	}

	@Override
	protected void postDraw(Canvas canv) {
		this.colour.setAlpha(alpha);
		this.colour.draw(canv);
		if (killed) alpha -=32;
	}

	@Override
	protected boolean preDraw(Canvas canv) {
		if (killed && alpha < 0) return false;
		// nothing needed at the moment
		return true;
	}
	
	public void kill() {
		killed = true;
	}
	
	public void unkill() {
		killed  = false;
		alpha = 255;
	}
	
	public boolean dead() {
		return killed;
	}
	
}
