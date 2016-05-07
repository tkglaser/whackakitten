package com.vorwardit.whackakitten.levels;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.vorwardit.whackakitten.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class GridLevel implements ILevel {
	
    private Context mContext;
    private List<Kitten> mKittens = new ArrayList<Kitten>();
    private List<Bitmap> mKittenImages = new ArrayList<Bitmap>();
    private List<Integer> mSFX = new ArrayList<Integer>();
	private int mLevelId;
	private AtomicInteger mPlaying = new AtomicInteger();
	private long mStartTime;
	private long mNowTime;
	private long mLevelClearTime;
	private int mMilliSecondsToComplete;
	private Paint mCountdownPaint;
	private Paint mFinishPaint;
	private boolean mSuccess = false;
	private long mShowResultStart = 0;
	private boolean mShowResultMode = false;
    
    public GridLevel(
    		int canvasHeight, int canvasWidth, 
    		Context context,
    		int numberCats,
    		int msToComplete,
    		int levelId) {
    	mContext = context;
    	mLevelId = levelId;
    	mMilliSecondsToComplete = msToComplete;
    	int scale = Math.min(canvasHeight, canvasWidth) / 3;
    	int padding = scale / 5;
    	int topScreenPadding = canvasHeight / 10;
    	int bottomScreenPadding = canvasHeight / 10;
    	int canvasHeightNet = canvasHeight - topScreenPadding - bottomScreenPadding;
    	mPlaying.set(0);
    	
    	mCountdownPaint = new Paint();
    	mCountdownPaint.setAntiAlias(true);
    	mCountdownPaint.setARGB(255, 0, 255, 0);
    	mCountdownPaint.setTextSize(30);
    	mFinishPaint = new Paint();
    	mFinishPaint.setAntiAlias(true);
    	mFinishPaint.setARGB(255, 255, 255, 255);
    	mFinishPaint.setTextSize(60);
    	mFinishPaint.setTextAlign(Align.CENTER);
    	
        Resources res = context.getResources();
        int size = scale - 2*padding;
        loadKitten(res, size, R.drawable.kitten1);
        loadKitten(res, size, R.drawable.kitten2);
        loadKitten(res, size, R.drawable.kitten3);
        loadKitten(res, size, R.drawable.kitten4);
        loadKitten(res, size, R.drawable.kitten5);
        loadKitten(res, size, R.drawable.kitten6);
        mSFX.add(R.raw.cat_meow_human_voice_3);
        mSFX.add(R.raw.cat_meow);
        mSFX.add(R.raw.dog_labradoodle_bark_single);
        mSFX.add(R.raw.human_face_punch);
        mSFX.add(R.raw.impact_rock_on_rubble_003);
        mSFX.add(R.raw.single_face_slap);
        mSFX.add(R.raw.vehicle_crash_large_glass);
        
        // how many rows?
        int maxRows = (int) Math.floor(canvasHeightNet / scale);
        List<Slot> slots = new ArrayList<Slot>();
    	for (int col = 0; col < 3; ++col)
    	{
        	for (int row = 0; row < maxRows; ++row)
        	{
        		slots.add(new Slot(col * scale, row * scale));
        	}
    	}
    	
    	numberCats = Math.min(numberCats, slots.size());
    	
    	for (int i = 0; i < numberCats; ++i)
    	{
    		boolean collision = false;
    		do
    		{
    			Slot randomSlot = slots.get(new Random().nextInt(slots.size()));
    			if (!randomSlot.isFilled())
    			{
        			collision = false;
    				randomSlot.fill();
        			mKittens.add(new Kitten(
        					randomSlot.x() + padding, 
        					randomSlot.y() + padding + topScreenPadding, 
        					size, 
        					size, 
        					mKittenImages.get(new Random().nextInt(mKittenImages.size()))));
    			}
    			else
    			{
    				collision = true;
    			}
    		}
    		while (collision);
    	}    	
    }
    
    private void loadKitten(Resources res, int size, int name)
    {
		Bitmap fullImage = BitmapFactory.decodeResource(res, name);
		mKittenImages.add(Bitmap.createScaledBitmap(fullImage, size, size, true));
    }
    
	@Override
	public void start(long time) {
		mStartTime = time;
		mLevelClearTime = time;
	}
    
	@Override
	public void update(long time) {
		mNowTime = time;
		if (!mShowResultMode)
		{
			mLevelClearTime = time;
			if (getRemainingMS() <= 0)
			{
				mShowResultMode = true;
				mShowResultStart = time;
			}
		}
	}
	
	private boolean allDead()
	{
    	for (Kitten k : mKittens)
    	{
    		if (!k.isDead())
    		{
    			return false;
    		}
    	}
    	return true;
	}

	@Override
	public boolean tap(float x, float y) {
		boolean handled = false;
    	for (Kitten k : mKittens)
    	{
    		k.hit(x, y);
    		if (k.isHit() && !k.isDead())
    		{
    			k.kill();
    			handled = true;
    			MediaPlayer mp = MediaPlayer.create(mContext, mSFX.get(new Random().nextInt(mSFX.size())));
                mp.setOnCompletionListener(new OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        mPlaying.decrementAndGet();
                    }

                });
                mPlaying.incrementAndGet();
                mp.start();
    		}
    	}
    	if (!mShowResultMode)
    	{
	    	if (allDead() && (getRemainingMS() > 0))
	    	{
				mShowResultMode = true;
	    		mSuccess = true;
	    		mShowResultStart = mNowTime;
	    	}
    	}
    	return handled;
	}
	
	public boolean success()
	{
		return mSuccess;
	}
	
	public long getRemainingMS()
	{
		long diff = mLevelClearTime - mStartTime;
		return mMilliSecondsToComplete - diff;
	}

	@Override
	public void draw(Canvas c) {
		if (mShowResultMode)
		{
			if (mSuccess)
			{
				c.drawText("Level complete", c.getWidth()/2, c.getHeight()/2  , mFinishPaint);
			}
			else
			{
				c.drawText("Game Over", c.getWidth()/2, c.getHeight()/2  , mFinishPaint);
			}
		}
		else
		{
	        for (Kitten k : mKittens)
	        {
	        	k.draw(c);
	        }
		}
	}


	@Override
	public boolean isFinished() {
		if (mShowResultMode)
		{
			if ((mNowTime - mShowResultStart) < 1000)
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		return false;
	}


	@Override
	public int levelId() {
		return mLevelId;
	}
}
