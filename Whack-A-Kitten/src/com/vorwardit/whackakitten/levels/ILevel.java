package com.vorwardit.whackakitten.levels;

import android.graphics.Canvas;

public interface ILevel {
	public void start(long time);
	public void update(long time);
	public boolean tap(float x, float y);
	public void draw(Canvas c);
	public boolean isFinished();
	public boolean success();
	public int levelId();
	public long getRemainingMS();
}
