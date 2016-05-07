package com.vorwardit.whackakitten;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class RenderView extends SurfaceView implements SurfaceHolder.Callback {
    private RenderThread mThread = null;

	public RenderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
			
		setFocusable(true);
	}
	
	public RenderThread getThread()
	{
		return mThread;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v("", "surfaceCreated");
		if (mThread == null)
		{
			mThread = new RenderThread(holder, getContext());
		}
		mThread.setRunning(true);
		mThread.start();
		mThread.doStart();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
        mThread.setSurfaceSize(width, height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v("", "surfaceDestroyed");
        boolean retry = true;
        mThread.setRunning(false);
        while (retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
        mThread = null;
	}
	
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        if (!hasWindowFocus && (mThread != null)) mThread.pause();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
    	if ((mThread == null) || (!mThread.doTouchEvent(event)))
    	{
    		return super.onTouchEvent(event);
    	}
    	else
    	{
    		return true;
    	}
    }
}
