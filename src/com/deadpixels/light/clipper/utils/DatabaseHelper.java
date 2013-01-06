package com.deadpixels.light.clipper.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	public static final String DB_NAME = "ClipsDataBase";
	public static final int DB_VERSION = 1; 
	public static final String TABLE_CLIPS = "ClipsTable";
	public static final String DB_COL_CLIP_ID = "ClipId";
	public static final String DB_COL_CLIP_CONTENT = "Content";
	public static final String DB_COL_CLIP_FAVORITE = "Favorite";
	public static final String [] DB_CLIPS_ALL = {
		DB_COL_CLIP_ID,
		DB_COL_CLIP_CONTENT,
		DB_COL_CLIP_FAVORITE
	};
	

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE "+ TABLE_CLIPS + " ("+ DB_COL_CLIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
				DB_COL_CLIP_CONTENT + " TEXT , " + DB_COL_CLIP_FAVORITE + " TEXT)" );		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIPS);
		onCreate(db);
	}

}
