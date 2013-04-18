package uk.co.mentalspace.android.bustimes.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BusTimesDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "BusTimesLocationsData.db";
    private static final int DATABASE_VERSION = 5;

    private static final String LOCATIONS_TABLE_CREATE = "create table locations (_id integer primary key autoincrement, stopCode text not null, name text not null, desc text not null, lat integer, lng integer, srcPosA text not null, srcPosB text not null, heading text not null, nickName text not null, chosen int not null, sourceId text not null);";
    private static final String BUS_TIMES_REFRESH_TABLE_CREATE = "create table btrefreshlog (_id integer primary key autoincrement, sourceId string not null, startTime long not null, endTime long);";
    private static final String SOURCES_TABLE_CREATE = "create table sources (_id integer primary key autoincrement, sourceId text not null, name text not null, estLocationCount integer not null, locationRefreshClassName text not null, busTimeRefreshClassName text not null, areaPolygonPointsJson text not null);";
    
    public static final String LOCATIONS_TABLE = "locations";
    public static final String BUS_TIMES_REFRESH_TABLE = "btrefreshlog";
    public static final String SOURCES_TABLE = "sources";

    public BusTimesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LOCATIONS_TABLE_CREATE);
        db.execSQL(BUS_TIMES_REFRESH_TABLE_CREATE);
        db.execSQL(SOURCES_TABLE_CREATE);
        setDefaultOptions(db);
    }
    
    private void setDefaultOptions(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	if (oldVersion < 2) {
    		upgradeToVersion2(db);
    	}
    	if (oldVersion < 3) {
    		upgradeToVersion3(db);
    	}
    	if (oldVersion < 4) {
    		upgradeToVersion4(db);
    	}
    	if (oldVersion < 5) {
    		upgradeToVersion5(db);
    	}
    }
    
    private void upgradeToVersion5(SQLiteDatabase db) {
        db.execSQL(SOURCES_TABLE_CREATE);
    }
    
    private void upgradeToVersion4(SQLiteDatabase db) {
        db.execSQL(BUS_TIMES_REFRESH_TABLE_CREATE);
    }
    
    private void upgradeToVersion3(SQLiteDatabase db) {
    	final String alterLocationsAddSourceId = "alter table "+LOCATIONS_TABLE+" add column sourceId text not null default '';";
        db.execSQL(alterLocationsAddSourceId);
    }
    
    private void upgradeToVersion2(SQLiteDatabase db) {
    	final String alterLocationsAddNickName = "alter table "+LOCATIONS_TABLE+" add column nickName text not null default '';";
        db.execSQL(alterLocationsAddNickName);

        final String alterLocationsAddChosen = "alter table "+LOCATIONS_TABLE+" add column chosen int not null default 0;";
        db.execSQL(alterLocationsAddChosen);
    }
}

