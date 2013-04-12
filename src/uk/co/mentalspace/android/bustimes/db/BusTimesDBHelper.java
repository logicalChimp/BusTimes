package uk.co.mentalspace.android.bustimes.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BusTimesDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BusTimesLocationsData.db";
    private static final int DATABASE_VERSION = 1;

    private static final String LOCATIONS_TABLE_CREATE = "create table locations (_id integer primary key autoincrement, stopCode text not null, name text not null, desc text not null, lat integer, lng integer, srcPosA text not null, srcPosB text not null, heading text not null);";
    public static final String LOCATIONS_TABLE = "locations";

    public BusTimesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LOCATIONS_TABLE_CREATE);
        setDefaultOptions(db);
    }
    
    private void setDefaultOptions(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LOCATIONS_TABLE);
        onCreate(db);
    }
}

