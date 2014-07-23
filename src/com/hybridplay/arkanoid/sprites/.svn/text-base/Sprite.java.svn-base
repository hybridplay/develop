package uk.co.coldasice.projects.android.arkadroid.sprites;

import uk.co.coldasice.projects.android.arkadroid.controllers.GameRenderer;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public abstract class Sprite {

	protected final Drawable drawable;
	public double y;
	public double x;
	public double w;
	public double h;
	protected GameRenderer renderer;
	Rect rect = new Rect();
	protected int alpha = 255;
	
	public Sprite(Drawable drawable, GameRenderer renderer, double _x, double _y) {
		this.drawable = drawable;
		this.w = drawable.getIntrinsicWidth();
		this.h = drawable.getIntrinsicHeight();
		this.x = _x;
		this.y = _y;
		this.renderer = renderer;
	}
	
	public final void draw (Canvas canv) {
		boolean continueDraw = preDraw(canv);
		// preDraw finished with false? Stop now
		if (!continueDraw) return;
		this.drawable.setBounds((int)x, (int)y, (int)(x+w), (int)(y+h));
		this.drawable.setAlpha(alpha);
		this.drawable.draw(canv);
		postDraw(canv);
	}
	
	protected abstract boolean preDraw(Canvas canv);
	protected abstract void postDraw(Canvas canv);
	
	public void setX(double x) {
		int width = this.renderer.w;
		if (x < 0) this.x = 0;
		else if (x + w > width) this.setXEdge(width-1);
		else this.x = x;
	}
	
	public void setY (double y) {
		int height = this.renderer.h;
		if (y < 0) this.y = 0;
		else if (y + h > height) this.setYEdge(height-1);
		else this.y = y;
	}

	public void setXEdge(double x) {
		this.setX(x - w);
	}
	
	public void setYEdge(double y) {
		this.setY(y - h);
	}
	
	public void setXMiddle(double newX) {
		this.x = newX - (w / 2);
	}
	
	public void setYMiddle(double newY) {
		this.y = newY - (h / 2);
	}
	
	public void setXY (double x, double y) {
		this.setX(x);
		this.setY(y);
	}
	
	public void setXYEdge(double x, double y) {
		this.setXEdge(x);
		this.setYEdge(y);
	}
	
	public void setXYMiddle (double x, double y) {
		this.setXMiddle(x);
		this.setYMiddle(y);
	}
	
	public boolean collidesWith(Rect rect) {
		return getBounds().intersect(rect);
	}

	Rect getBounds() {
		rect.set((int)x, (int)y, (int)(x+w), (int)(y+h));
		return rect;
	}

	public double getMidX() {
		return x + (w/2);
	}
	
}
