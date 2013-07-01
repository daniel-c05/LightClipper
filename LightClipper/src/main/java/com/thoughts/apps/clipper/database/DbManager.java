package com.thoughts.apps.clipper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.thoughts.apps.clipper.Constants;
import com.thoughts.apps.clipper.utils.ClipHelper;

import java.util.Calendar;

/**
 * Created by user on 06-30-13.
 */
public class DbManager {

    private static DbOpenHelper mHelper;
    private static SQLiteDatabase mDatabase;

    private static void open (Context context) {
        if (mDatabase != null && mDatabase.isOpen()) {
            return;
        }
        try {
            mHelper = new DbOpenHelper(context);
            mDatabase = mHelper.getWritableDatabase();
        } catch (Exception e) {
            Constants.logMessage(e.toString());
        }
    }

    private static void close () {
        if (mDatabase != null) {
            mDatabase.close();
        }
    }

    /**
     * Delete the clips from the database, and clear the clipboard.
     * @param context
     */
    public static void deleteClips(final Context context) {
        if (mDatabase == null || !mDatabase.isOpen())
            open(context);

        Log.v(Constants.TAG, "Deleting clips");
        mDatabase.delete(DbOpenHelper.TABLE_NAME, null, null);
        ClipHelper.clearClipboard(context);
    }

    /**
     * Delete the clips from the database, and clear the clipboard.
     * @param context
     */
    public static void deleteClip(final Context context, final String clipText) {
        if (mDatabase == null || !mDatabase.isOpen())
            open(context);

        Log.v(Constants.TAG, "Deleting " + clipText + " from clips");
        String where = DbOpenHelper.CLIP_TEXT + " = '" + clipText + "'";
        mDatabase.delete(DbOpenHelper.TABLE_NAME, where, null);
    }

    /**
     *
     * @param context The context.
     * @param clipText The actual String to save to the database.
     */
    public static void saveClip(final Context context, final String clipText) {
        if (mDatabase == null || !mDatabase.isOpen())
            open(context);

        String where = DbOpenHelper.CLIP_TEXT + " = '" + clipText + "'";
        Cursor mCursor = mDatabase.query(DbOpenHelper.TABLE_NAME,
                new String [] {DbOpenHelper.CLIP_TEXT}, where, null, null, null, null);

        ContentValues mValues = new ContentValues();
        mValues.put(DbOpenHelper.CLIP_TEXT, clipText);
        mValues.put(DbOpenHelper.CLIP_STAMP, getGregorianDate());

        if (mCursor != null) {
            if (!mCursor.moveToFirst()) {
                //Here, this item has never been stored, insert it.
                mDatabase.insert(DbOpenHelper.TABLE_NAME, null, mValues);
            }
            else {
                mDatabase.update(DbOpenHelper.TABLE_NAME, mValues, where, null);
            }
            mCursor.close();
        }
    }

    /**
     *
     * @param context The context.
     * @return A Cursor pointing to the list of the clips that were saved via {@link #saveClip(Context, String)}
     */
    public static Cursor getSavedClips(final Context context) {
        if (mDatabase == null || !mDatabase.isOpen())
            open(context);

        return mDatabase.query(DbOpenHelper.TABLE_NAME,
                DbOpenHelper.PROJECTION, null, null, null, null, DbOpenHelper.CLIP_STAMP);
    }

    /**
     * Get a date String in the format: YYYY-MM-DD.
     * @return A Date String, formatted as YYYY-MM-DD.
     */
    private static String getGregorianDate () {

        //No date is supplied, then return today
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        String monthStr = String.valueOf(month + 1);
        String dayStr = String.valueOf(day);
        if (monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }
        if (dayStr.length() == 1) {
            dayStr = "0" + dayStr;
        }
        return year + "-" + monthStr + "-" + dayStr;

    }

}
