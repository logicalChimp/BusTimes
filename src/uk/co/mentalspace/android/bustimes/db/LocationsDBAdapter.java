package uk.co.mentalspace.android.bustimes.db;

import java.util.ArrayList;
import java.util.List;

import uk.co.mentalspace.android.bustimes.Location;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocationsDBAdapter {
	private static final String LOGNAME = "LocationsDBAdapter";
	
    public static final String KEY_ROWID = "_id";
    public static final String KEY_STOP_CODE = "stopCode";
    public static final String KEY_NAME = "name";
    public static final String KEY_DESC = "desc";
    public static final String KEY_WGS84_LAT = "lat";
    public static final String KEY_WGS84_LONG = "lng";
    public static final String KEY_SRC_POS_A = "srcPosA";
    public static final String KEY_SRC_POS_B = "srcPosB";
    public static final String KEY_HEADING = "heading";

    private BusTimesDBHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final Context mCtx;


    /**
     * Constructor - takes the context to allow the database to be opened/created
     * @param ctx the Context within which to work
     */
    public LocationsDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Opens the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public LocationsDBAdapter open() throws SQLException {
        mDbHelper = new BusTimesDBHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }

    /**
     * @return rowId or -1 if failed
     */
    public long createLocation(String stopCode, String name, String desc, int lat, int lng, String srcPosA, String srcPosB, String heading) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_STOP_CODE, stopCode);
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_DESC, desc);
        initialValues.put(KEY_WGS84_LAT, lat);
        initialValues.put(KEY_WGS84_LONG, lng);
        initialValues.put(KEY_SRC_POS_A, srcPosA);
        initialValues.put(KEY_SRC_POS_B, srcPosB);
        initialValues.put(KEY_HEADING, heading);

        return mDb.insert(BusTimesDBHelper.LOCATIONS_TABLE, null, initialValues);
    }

    /**
     * Delete the location with the given rowId
     * @param rowId id of the location to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteLocation(long rowId) {
        return mDb.delete(BusTimesDBHelper.LOCATIONS_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all locations in the database
     * @return Cursor over all notes
     */
    public Cursor fetchAllLLocations() {
        return mDb.query(BusTimesDBHelper.LOCATIONS_TABLE, new String[] {KEY_ROWID, KEY_STOP_CODE, KEY_NAME, KEY_DESC, KEY_WGS84_LAT, KEY_WGS84_LONG, 
        		KEY_SRC_POS_A, KEY_SRC_POS_B, KEY_HEADING}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the location that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching location, if found
     * @throws SQLException if location could not be found/retrieved
     */
    public Cursor fetchLocationByID(long rowId) throws SQLException {
        Cursor mCursor =
                mDb.query(true, BusTimesDBHelper.LOCATIONS_TABLE, new String[] {KEY_ROWID, KEY_STOP_CODE, KEY_NAME, KEY_DESC, KEY_WGS84_LAT, KEY_WGS84_LONG, 
                		KEY_SRC_POS_A, KEY_SRC_POS_B, KEY_HEADING}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public Location getLocationByStopCode(String stopCode) {
    	Cursor c = null;
    	try {
    		c = fetchLocationByStopCode(stopCode);
        	if (null == c || c.getCount() == 0) return null;        	
        	return generateLocation(c);
    	} catch (SQLException sqle) {
    		return null;
    	} finally {
    		if (null != c) c.close();
    	}
    }
    
    private Location generateLocation(Cursor c) {
    	Location loc = new Location(c.getLong(c.getColumnIndex(KEY_ROWID)),
                c.getString(c.getColumnIndex(KEY_STOP_CODE)),
                c.getString(c.getColumnIndex(KEY_NAME)), 
                c.getString(c.getColumnIndex(KEY_DESC)),
                c.getString(c.getColumnIndex(KEY_SRC_POS_A)),
                c.getString(c.getColumnIndex(KEY_SRC_POS_B)),
                c.getString(c.getColumnIndex(KEY_HEADING)),
                c.getInt(c.getColumnIndex(KEY_WGS84_LAT)),
                c.getInt(c.getColumnIndex(KEY_WGS84_LONG)));
		return loc;
    }
    
    /**
     * Return a Cursor positioned at the location that matches the given stop code
     * @param stopCode the StopCode of the location to be retrieved
     * @return Cursor positioned to the matching location, if found
     * @throws SQLException if a matching location could not be found / retrieved
     */
    public Cursor fetchLocationByStopCode(String stopCode) throws SQLException {

        Cursor mCursor =
                mDb.query(true, BusTimesDBHelper.LOCATIONS_TABLE, new String[] {KEY_ROWID, KEY_STOP_CODE, KEY_NAME, KEY_DESC, KEY_WGS84_LAT, KEY_WGS84_LONG, 
                		KEY_SRC_POS_A, KEY_SRC_POS_B, KEY_HEADING}, KEY_STOP_CODE + "= ?", new String[]{stopCode}, 
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public Cursor fetchLocationsInArea(int top, int right, int bottom, int left) throws SQLException {
        Cursor mCursor =
                mDb.query(true, BusTimesDBHelper.LOCATIONS_TABLE, new String[] {KEY_ROWID, KEY_STOP_CODE, KEY_NAME, KEY_DESC, KEY_WGS84_LAT, KEY_WGS84_LONG, 
                		KEY_SRC_POS_A, KEY_SRC_POS_B, KEY_HEADING}, 
                		KEY_WGS84_LAT+" <= "+top+" AND "+KEY_WGS84_LAT+" >= "+bottom+" AND "+KEY_WGS84_LONG+" >= "+left+" AND "+KEY_WGS84_LONG+" <= "+right, 
                		null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public List<Location> getLocationsInArea(int top, int right, int bottom, int left) throws SQLException {
    	Log.d(LOGNAME, "Getting locations in area ["+top+", "+right+", "+bottom+", "+left+"]");
    	Cursor c = fetchLocationsInArea(top, right, bottom, left);
    	if (null == c) return null;

    	ArrayList<Location> locs = new ArrayList<Location>();
    	try {
	    	c.moveToFirst();
	    	while (!c.isAfterLast()) {
		    	Location loc = generateLocation(c);
		    	locs.add(loc);
		    	c.moveToNext();
	    	}
    	} catch (Exception e) {
    		Log.e(LOGNAME, "Unknown exception occured: ", e);
    		
    	} finally {
	    	if (null != c) c.close();
    	}
    	return locs;
    }
    
    /**
     * Update the location using the details provided. The location to be updated is
     * specified using the rowId, and it is altered to use the values passed in
     * 
     * @param rowId id of note to update
     * @param title value to set note title to
     * @param body value to set note body to
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateBeacon(long rowId, String stopCode, String name, String desc, int lat, int lng, String srcPosA, String srcPosB, String heading) {
        ContentValues args = new ContentValues();
        args.put(KEY_STOP_CODE, lng);
        args.put(KEY_NAME, name);
        args.put(KEY_DESC, desc);
        args.put(KEY_WGS84_LAT, lat);
        args.put(KEY_WGS84_LONG, lng);
        args.put(KEY_SRC_POS_A, srcPosA);
        args.put(KEY_SRC_POS_B, srcPosB);
        args.put(KEY_HEADING, heading);

        return mDb.update(BusTimesDBHelper.LOCATIONS_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

}
