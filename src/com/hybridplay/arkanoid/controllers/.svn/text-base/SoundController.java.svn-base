package uk.co.coldasice.projects.android.arkadroid.controllers;

import java.util.Random;

import uk.co.coldasice.projects.android.arkadroid.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundController {
	public static SoundPool sounds;
	public static int BRICK_SOUNDS[] = new int[4];
	public static int WALL_SOUNDS[] = new int[2];
	public static int PADDLE_SOUNDS[] = new int[2];
	public static int VICTORY_SOUNDS[] = new int[2];
	private static Random rand;

	public static void init(Context context) {
		sounds = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
		BRICK_SOUNDS[0] = sounds.load(context, R.raw.board1, 1);
		BRICK_SOUNDS[1] = sounds.load(context, R.raw.board2, 1);
		BRICK_SOUNDS[2] = sounds.load(context, R.raw.wood1, 1);
		BRICK_SOUNDS[3] = sounds.load(context, R.raw.wood2, 1);

		WALL_SOUNDS[0] = sounds.load(context, R.raw.glass1, 1);
		WALL_SOUNDS[1] = sounds.load(context, R.raw.glass2, 1);

		PADDLE_SOUNDS[0] = sounds.load(context, R.raw.paddle1, 1);
		PADDLE_SOUNDS[1] = sounds.load(context, R.raw.paddle2, 1);

		VICTORY_SOUNDS[0] = sounds.load(context, R.raw.cheer1, 1);
		VICTORY_SOUNDS[1] = sounds.load(context, R.raw.starwars, 1);
		rand = new Random(System.currentTimeMillis());
	}

	public static void playPaddleSound() {
		sounds.play(PADDLE_SOUNDS[rand.nextInt(PADDLE_SOUNDS.length)], 1, 1, 1, 0, 1);
	}

	public static void playWallSound() {
		sounds.play(WALL_SOUNDS[rand.nextInt(WALL_SOUNDS.length)], 1, 1, 1, 0, 1);
	}

	public static void playBrickSound() {
		sounds.play(BRICK_SOUNDS[rand.nextInt(BRICK_SOUNDS.length)], 1, 1, 1, 0, 1);
	}

	public static void playVictorySound() {
		sounds.play(VICTORY_SOUNDS[rand.nextInt(VICTORY_SOUNDS.length)], 1, 1, 1, 0, 1);
	}
}
