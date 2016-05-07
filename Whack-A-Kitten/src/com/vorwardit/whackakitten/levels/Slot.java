package com.vorwardit.whackakitten.levels;

public class Slot {
	private int mX;
	private int mY;
	private boolean mFilled;

	public Slot(int x, int y)
	{
		mX = x;
		mY = y;
		mFilled = false;
	}
	
	public int x()
	{
		return mX;
	}
	
	public int y()
	{
		return mY;
	}
	
	public void fill()
	{
		mFilled = true;
	}
	
	public boolean isFilled()
	{
		return mFilled;
	}
}
