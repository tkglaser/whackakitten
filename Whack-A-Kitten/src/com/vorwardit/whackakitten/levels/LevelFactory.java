package com.vorwardit.whackakitten.levels;

import android.content.Context;

public class LevelFactory {
	
    private int mCanvasHeight;
	private int mCanvasWidth;
	private Context mContext;

	public LevelFactory(int canvasHeight, int canvasWidth, Context context) {
    	mCanvasHeight = canvasHeight;
    	mCanvasWidth = canvasWidth;
    	mContext = context;
    }
	
	public ILevel startNewGame()
	{
		return new GridLevel(
				mCanvasHeight, 
				mCanvasWidth, 
				mContext,
				3,
				3000,
				1);
	}
	
	public ILevel nextLevel(int levelId)
	{
		switch (levelId)
		{
		case 1:	 return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 3, 2500, levelId + 1);
		case 2:	 return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 3, 2000, levelId + 1);
		case 3:	 return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 3, 1500, levelId + 1);
		case 4:	 return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 3, 1000, levelId + 1);
		case 5:	 return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 4, 2500, levelId + 1);
		case 6:	 return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 4, 2000, levelId + 1);
		case 7:	 return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 4, 1500, levelId + 1);
		case 8:	 return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 4, 1000, levelId + 1);
		case 9:	 return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 5, 2500, levelId + 1);
		case 10: return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 5, 2000, levelId + 1);
		case 12: return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 5, 1500, levelId + 1);
		case 13: return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 6, 3000, levelId + 1);
		case 14: return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 6, 2500, levelId + 1);
		case 15: return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 6, 2000, levelId + 1);
		case 16: return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 7, 3000, levelId + 1);
		case 17: return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 7, 2500, levelId + 1);
		case 18: return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 8, 3500, levelId + 1);
		case 19: return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 8, 3000, levelId + 1);
		case 20: return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 8, 2500, levelId + 1);
		default: return new GridLevel(mCanvasHeight, mCanvasWidth, mContext, 8, 2000, levelId + 1);
		}
	}
}
