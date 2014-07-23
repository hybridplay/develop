package uk.co.coldasice.projects.android.arkadroid.sprites;

import uk.co.coldasice.projects.android.arkadroid.controllers.GameRenderer;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public abstract class Sprite_Trail extends Sprite {

	private final int TRAILLENGTH = 5;
	private int latestTrail = 0;
	int[] trailX, trailY;
	
	public Sprite_Trail(Drawable drawable, GameRenderer renderer) {
		super(drawable, renderer,0,0);
		// this probably isn't a brick... let's display movement trails.
		trailX = new int[TRAILLENGTH];
		trailY = new int[TRAILLENGTH];
	}

	public void resetTrails() {
		for (int i=0; i<TRAILLENGTH; i++) {
			trailX[i] = (int)x;
			trailY[i] = (int)y;
		}
	}
	
	public void updateTrails() {
		if(++latestTrail==TRAILLENGTH)latestTrail=0;
		trailX[latestTrail] = (int)x;
		trailY[latestTrail] = (int)y;
	}
	
	@Override
	protected boolean preDraw(Canvas canv) {
		for (int i=latestTrail,j=0;j<TRAILLENGTH;i=i>0?i-1:TRAILLENGTH-1,j++) {
			this.drawable.setBounds(trailX[i], trailY[i], (trailX[i] + (int)w), (trailY[i] + (int)h));
			int alpha = (int)(255d * (TRAILLENGTH - (j+1)) / TRAILLENGTH);
			this.drawable.setAlpha(alpha);
			this.drawable.draw(canv);
		}
		return true;
	}

	@Override
	protected void postDraw(Canvas canv) {
		// nothing here...
	}

	public void reset() {
		resetTrails();
	}

}
