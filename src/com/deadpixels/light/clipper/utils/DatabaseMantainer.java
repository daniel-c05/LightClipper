package com.deadpixels.light.clipper.utils;

import java.util.ArrayList;

import com.deadpixels.light.clipper.Home;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseMantainer {
	
	private DatabaseHelper mDatabaseHelper;
	private SQLiteDatabase mDatabase;

	public DatabaseMantainer (Context context) {
		mDatabaseHelper = new DatabaseHelper(context);
	}

	public void open () {
		try {
			mDatabase = mDatabaseHelper.getWritableDatabase();
		} catch (Exception e) {
			Log.w(Home.TAG, e.toString());
		}
	}

	public void close() {
		mDatabaseHelper.close();
	}
	
	public void addClipToDatabase (String clip) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(DatabaseHelper.DB_COL_CLIP_CONTENT, clip);
		mDatabase.insert(DatabaseHelper.TABLE_CLIPS, null, contentValues);		
	}
	
	public void deleteClipFromDatabase (String clip) {
		mDatabase.delete(DatabaseHelper.TABLE_CLIPS, DatabaseHelper.DB_COL_CLIP_CONTENT + " = " + clip, null);
	}
	
	public ArrayList<String> getAllComments() {
		ArrayList<String> clips = new ArrayList<String>();

	    Cursor cursor = mDatabase.query(DatabaseHelper.TABLE_CLIPS,
	        DatabaseHelper.DB_CLIPS_ALL, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      String clip = cursor.getString(1);
	      clips.add(clip);
	      cursor.moveToNext();
	    }
	    cursor.close();
	    return clips;
	  }


}
