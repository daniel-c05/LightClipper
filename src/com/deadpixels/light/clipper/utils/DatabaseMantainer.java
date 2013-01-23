/*******************************************************************************
 * Copyright 2007 The Android Open Source Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.deadpixels.light.clipper.utils;

import java.util.ArrayList;

import com.deadpixels.light.clipper.Home;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * This class is NOT in use throughout the project. 
 * @author SkullKandy
 *
 */
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
	
	public ArrayList<String> getAll() {
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
