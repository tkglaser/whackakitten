package com.vorwardit.whackakitten;

import com.vorwardit.whackakitten.levels.ILevel;
import com.vorwardit.whackakitten.levels.LevelFactory;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class RenderThread extends Thread {
    /*
     * State-tracking constants
     */
    public static final int STATE_LOSE = 1;
    public static final int STATE_PAUSE = 2;
    public static final int STATE_READY = 3;
    public static final int STATE_RUNNING = 4;
    public static final int STATE_WIN = 5;
    private int mMode;
	
    private Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private boolean mRun = false;
    private final Object mRunLock = new Object();
    private int mCanvasWidth = 1;
    private int mCanvasHeight = 1;
    private Bitmap mBackgroundImage;
    private Bitmap mLevelBackgroundImage;
    private Bitmap mStartImage;
    //private Bitmap mResumeImage;
    private ILevel mLevel = null;
    private LevelFactory mLevelFactory = null;
    private Paint mLinePaint;
    private Paint mCenterPaint;
    private int mPoints = 0;
    private long mHighScore;
    
	public RenderThread(SurfaceHolder surfaceHolder, Context context)
	{
		mContext = context;
		mSurfaceHolder = surfaceHolder;
		mHighScore = HighScore.Get(context);
		
		Resources res = context.getResources();
		final float scale = res.getDisplayMetrics().density;
		
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setARGB(255, 255, 255, 255);
        mLinePaint.setTextSize(15 * scale + 0.5f);
    	mCenterPaint = new Paint();
    	mCenterPaint.setAntiAlias(true);
    	mCenterPaint.setARGB(255, 255, 255, 255);
    	mCenterPaint.setTextSize(15 * scale + 0.5f);
    	mCenterPaint.setTextAlign(Align.CENTER);

        mBackgroundImage = BitmapFactory.decodeResource(res,
                R.drawable.wickerbg);
        mLevelBackgroundImage = BitmapFactory.decodeResource(res,
                R.drawable.levelbg);
        mStartImage = BitmapFactory.decodeResource(res,
                R.drawable.start);
//        mResumeImage = BitmapFactory.decodeResource(res,
//                R.drawable.resume);
	}

	public void setRunning(boolean b) {
		synchronized (mRunLock) {
            mRun = b;
            Log.v("", "mRun set to " + (b ? "true" : "false"));
        }
	}
	
    public void pause() {
        synchronized (mSurfaceHolder) {
            if (mMode == STATE_RUNNING) setState(STATE_PAUSE);
        }
    }

    public void unpause() {
        // Move the real time clock up to now
        synchronized (mSurfaceHolder) {
            //mLastTime = System.currentTimeMillis() + 100;
        }
        setState(STATE_RUNNING);
    }
    
    public void setState(int mode) {
        synchronized (mSurfaceHolder) {
            mMode = mode;
        }
    }

	public void setSurfaceSize(int width, int height) {
        synchronized (mSurfaceHolder) {
            mCanvasWidth = width;
            mCanvasHeight = height;
            mLevelFactory = new LevelFactory(mCanvasHeight, mCanvasWidth, mContext);

            // don't forget to resize the background image
            mBackgroundImage = Bitmap.createScaledBitmap(
                    mBackgroundImage, width, height, true);
            mLevelBackgroundImage = Bitmap.createScaledBitmap(
            		mLevelBackgroundImage, width, height, true);
            mStartImage = Bitmap.createScaledBitmap(
            		mStartImage, width, height, true);
//            mResumeImage = Bitmap.createScaledBitmap(
//            		mResumeImage, width, height, true);
        }
	}
	
    public void doStart() 
    {
        synchronized (mSurfaceHolder) {
            setState(STATE_READY);
        }
    }
    
    private long lastTime = 0;
    private static final long frameTimeMs = (1000 / 10);
    
    @Override
    public void run() {
        Log.v("", "Entering Thread loop");
        while (mRun) {
        	// do 25fps;
        	long now = System.currentTimeMillis();
        	long diffSinceLast = now - lastTime;
        	if (diffSinceLast < frameTimeMs)
        	{
        		// too early sleep for a bit
        		long sleepTime = frameTimeMs - diffSinceLast;
        		try {
                    Thread.sleep(sleepTime);
                } 
                catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
        	}
        	lastTime = now;
        	
            Canvas c = null;
            try {
                c = mSurfaceHolder.lockCanvas(null);
                synchronized (mSurfaceHolder) {
                    if ((mMode == STATE_RUNNING) && (mLevel != null))
                    {
                    	mLevel.update(System.currentTimeMillis());
                    }
                    // Critical section. Do not allow mRun to be set false until
                    // we are sure all canvas draw operations are complete.
                    //
                    // If mRun has been toggled false, inhibit canvas operations.
                    synchronized (mRunLock) {
                        if (mRun) doDraw(c);
                    }
                }
            } finally {
                // do this in a finally so that if an exception is thrown
                // during the above, we don't leave the Surface in an
                // inconsistent state
                if (c != null) {
                    mSurfaceHolder.unlockCanvasAndPost(c);
                }
            }
            if ((mLevelFactory != null) && (mMode == STATE_RUNNING))
            {
	            if (mLevel == null)
	            {
            		mPoints = 0;
	            	mLevel = mLevelFactory.startNewGame();
	            	mLevel.start(System.currentTimeMillis());
	            }
	            else if (mLevel.isFinished())
	            {
	            	if (mLevel.success())
	            	{
	            		++mPoints;
	            		mLevel = mLevelFactory.nextLevel(mLevel.levelId());
		            	mLevel.start(System.currentTimeMillis());
	            	}
	            	else
	            	{
	            		if (mPoints > mHighScore)
	            		{
	            			mHighScore = mPoints;
	            			HighScore.Save(mContext, mPoints);
	            		}
	            		mLevel = null;
	            		mMode = STATE_READY;
	            	}
	            }
            }
        }
        Log.v("", "Exiting Thread loop");
    }
    
    private float scaleX(float x)
    {
    	return (x * mCanvasWidth) / 100; 
    }
    
    private float scaleY(float y)
    {
    	return (y * mCanvasHeight) / 100; 
    }
      
    private void doDraw(Canvas canvas) 
    {
    	if (mMode == STATE_READY)
    	{
            canvas.drawBitmap(mStartImage, 0, 0, null);
			String highScore = "Highscore: " + Long.toString(mHighScore);
			canvas.drawText(highScore, scaleX(50), scaleY(40), mCenterPaint);
    	}
    	else if (mLevel != null)
    	{
	        canvas.drawBitmap(mLevelBackgroundImage, 0, 0, null);
	        mLevel.draw(canvas);
			String points = "Points: " + Long.toString(mPoints);
			canvas.drawText(points, scaleX(55), scaleY(5.5f), mLinePaint);
	        long remainingMs = Math.max(mLevel.getRemainingMS(), 0);
	        long secondsLeft = (long) Math.max(Math.ceil(remainingMs / 1000.0) - 1, 0);
	        long fractionLeft = (long) Math.max(Math.ceil(remainingMs % 1000.0), 0) / 100;
			String timeRemaining = "Remaining: " + Long.toString(secondsLeft) + "." + Long.toString(fractionLeft)  + "s";
			canvas.drawText(timeRemaining, scaleX(5), scaleY(5.5f), mLinePaint);
    	}
    }
    
    public boolean doTouchEvent(MotionEvent event) {
    	boolean handled = false;
        synchronized (mSurfaceHolder) {
        	if (mMode == STATE_READY)
        	{
        		mMode = STATE_RUNNING;
        	}
        	else if (mMode == STATE_RUNNING)
        	{
	        	if (mLevel != null)
	        	{
	        		handled = mLevel.tap(event.getX(), event.getY());
	        	}
        	}
        }
    	return handled;
    }


}
