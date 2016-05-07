package com.vorwardit.whackakitten;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;
import android.util.Log;

public class HighScore {
	
	private static final String TAG = "VorwardIT.HighScore";
	private static final String FILENAME = "com.vorwardit.whackakitten.highscore";
	
	private static Long mScore = null;
	
	public static void Save(Context c, long score) {
		mScore = score;
		saveScore(c, score);
	}
	
	public static long Get(Context c) {
		if (mScore == null)
		{
			mScore = loadScore(c);
			if (mScore == null)
			{
				return 0;
			}
		}
		return mScore;
	}
	
	private static boolean saveScore(Context c, long score) {
	    try {
	    	Log.v(TAG, "saveScore: Saving...");
	        FileOutputStream fos = c.openFileOutput(FILENAME, Context.MODE_PRIVATE);
	        ObjectOutputStream oos = new ObjectOutputStream(fos);
	        
	        oos.writeLong(score);
	        oos.close();
	    	Log.v(TAG, "saveScore: Saved.");
	    } catch (IOException e) {
	    	Log.e(TAG, "saveScore: Save Failed", e);
	        e.printStackTrace();
	        return false;
	    }

	    return true;
	}

	private static Long loadScore(Context c) {
	    try {
	        FileInputStream fis = c.openFileInput(FILENAME);
	        ObjectInputStream is = new ObjectInputStream(fis);
	        long result = is.readLong();
	        is.close();
	        
	        return result;
	    } catch (IOException e) {
	    	Log.e(TAG, "loadLocation: Load Failed", e);
	        e.printStackTrace();
	    }

    	Log.v(TAG, "loadLocation: Loaded null");
	    return null;
	}
}
