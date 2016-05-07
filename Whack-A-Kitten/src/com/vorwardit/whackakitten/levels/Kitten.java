package com.vorwardit.whackakitten.levels;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Kitten 
{
	private int mX;
	private int mY;
	private int mSizeX;
	private int mSizeY;
    private Bitmap mImage;
    private boolean mIsHit = false;
    private boolean mIsDead = false;
	public Kitten(int x, int y, int sizeX, int sizeY, Bitmap image)
	{
		mX = x;
		mY = y;
		mSizeX = sizeX;
		mSizeY = sizeY;
		
        mImage = image;
	}
	
	public void draw(Canvas c)
	{
		if (!mIsDead)
		{
			c.drawBitmap(mImage, mX, mY, null);
		}
	}	
	public void hit(float x, float y)
	{
		if ( (mX <= x) && (x <= (mX + mSizeX)) &&
			 (mY <= y) && (y <= (mY + mSizeY)))
		{
			mIsHit = true;
		}
	}
	
	public boolean isHit()
	{
		return mIsHit;
	}
	
	public void kill()
	{
		mIsDead = true;
	}
	
	public boolean isDead()
	{
		return mIsDead;
	}
}
