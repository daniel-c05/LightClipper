package com.thoughts.apps.clipper.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.thoughts.apps.clipper.Constants;

/**
 * Created by user on 06-30-13.
 */
public class DbOpenHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "clipper.db";

    private static final int DB_VERSION = 1;

    /**
     * The autoincrement key
     */
    public static final String _ID = "_id";

    public static final String TABLE_NAME = "clips";
    public static final String CLIP_TEXT = "text";
    public static final String CLIP_STAMP = "stamp";

    public static final String [] PROJECTION = {
            _ID,
            CLIP_TEXT,
            CLIP_TEXT
    };

    private static final String DATABASE_CREATE = "create table " + TABLE_NAME
            + "(" + _ID + " integer primary key autoincrement, "
            + CLIP_TEXT + " text not null, "
            + CLIP_STAMP + " text not null);"
            ;

    public DbOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE);
        onCreate(sqLiteDatabase);
    }
}
