package uk.co.mentalspace.android.bustimes.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.mentalspace.android.bustimes.Location;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class LocationsDBAdapter extends BaseDBAdapter<Location> {
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
    public static final String KEY_NICK_NAME = "nickName";
    public static final String KEY_CHOSEN = "chosen";
    public static final String KEY_SOURCE_ID = "sourceId";

	private static final String[] ALL_COLUMNS = new String[] {KEY_ROWID, KEY_STOP_CODE, KEY_NAME, KEY_DESC, KEY_WGS84_LAT, KEY_WGS84_LONG, 
		KEY_SRC_POS_A, KEY_SRC_POS_B, KEY_HEADING, KEY_NICK_NAME, KEY_CHOSEN, KEY_SOURCE_ID};


    /**
     * Constructor - takes the context to allow the database to be opened/created
     * @param ctx the Context within which to work
     */
    public LocationsDBAdapter(Context ctx) {
    	super(ctx);
    }

    /**
     * Delete the location with the given rowId
     * @param rowId id of the location to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteLocation(long rowId) {
    	return delete(KEY_ROWID + "=" + rowId);
    }

    public boolean deleteLocationByStopCode(String stopCode) {
    	return delete(KEY_STOP_CODE + "=" + stopCode);
    }

    /**
     * Return a Cursor over the list of all locations in the database
     * @return Cursor over all notes
     */
    public Cursor fetchAllLocations() {
    	return fetch(BusTimesDBHelper.LOCATIONS_TABLE, ALL_COLUMNS, null);
    }
    
    public Cursor fetchClosestSelectedLocation(int lat, int lon) {
    	String sql = "select _id, stopCode, name, desc, lat, lng, srcPosA, srcPosB, heading, nickName, chosen, sourceId, " +
    			"abs(lat - "+lat+") + abs(lng - "+lon+") as distance from "+BusTimesDBHelper.LOCATIONS_TABLE+" where chosen = 1 order by distance asc";
    	//Math.abs(lat - "+lat+") + 
    	Cursor c = mDb.rawQuery(sql, null);
    	if (null != c) c.moveToFirst();
    	
    	return c;
    }
    
    public Location getClosestSelectedLocation(int lat, int lon) {
		Cursor c = fetchClosestSelectedLocation(lat, lon);
		return getSingle(c);
    }

    /**
     * Return a Cursor positioned at the location that matches the given rowId
     * 
     * @param rowId id of note to retrieve
     * @return Cursor positioned to matching location, if found
     * @throws SQLException if location could not be found/retrieved
     */
    public Cursor fetchLocationByID(long rowId) throws SQLException {
    	return fetch(BusTimesDBHelper.LOCATIONS_TABLE, ALL_COLUMNS, KEY_ROWID + "=" + rowId);
    }
    
    public Location getLocationByID(long rowId) {
		Cursor c = fetchLocationByID(rowId);
		return getSingle(c);
    }
    
    /**
     * Return a Cursor positioned at the location that matches the given stop code
     * @param stopCode the StopCode of the location to be retrieved
     * @return Cursor positioned to the matching location, if found
     * @throws SQLException if a matching location could not be found / retrieved
     */
    public Cursor fetchLocationByStopCode(String stopCode) throws SQLException {
    	return fetch(BusTimesDBHelper.LOCATIONS_TABLE, ALL_COLUMNS, KEY_STOP_CODE + "= '"+stopCode+"'");
    }
    
    public Location getLocationByStopCode(String stopCode) {
		Cursor c = fetchLocationByStopCode(stopCode);
		return getSingle(c);
    }
    
    public Cursor fetchLocationsInArea(int top, int right, int bottom, int left) throws SQLException {
    	return fetch(BusTimesDBHelper.LOCATIONS_TABLE, ALL_COLUMNS, 
                		KEY_WGS84_LAT+" <= "+top+" AND "+KEY_WGS84_LAT+" >= "+bottom+" AND "+KEY_WGS84_LONG+" >= "+left+" AND "+KEY_WGS84_LONG+" <= "+right);
    }
    
    public List<Location> getLocationsInArea(int top, int right, int bottom, int left) throws SQLException {
    	Log.d(LOGNAME, "Getting locations in area ["+top+", "+right+", "+bottom+", "+left+"]");
    	Cursor c = fetchLocationsInArea(top, right, bottom, left);
    	return getList(c);
    }
    
    public Cursor fetchSelectedLocations() throws SQLException {
    	return fetch(BusTimesDBHelper.LOCATIONS_TABLE, ALL_COLUMNS, KEY_CHOSEN+" = 1");
    }
    
    public List<Location> getSelectedLocations() throws SQLException {
    	Log.d(LOGNAME, "Getting all Selected locations");
    	Cursor c = fetchSelectedLocations();
    	return getList(c);
    }
    
    protected Location populateFromCursor(Cursor c) {
    	Location loc = new Location(c.getLong(c.getColumnIndex(KEY_ROWID)),
                c.getString(c.getColumnIndex(KEY_STOP_CODE)),
                c.getString(c.getColumnIndex(KEY_NAME)), 
                c.getString(c.getColumnIndex(KEY_DESC)),
                c.getString(c.getColumnIndex(KEY_SRC_POS_A)),
                c.getString(c.getColumnIndex(KEY_SRC_POS_B)),
                c.getString(c.getColumnIndex(KEY_HEADING)),
                c.getInt(c.getColumnIndex(KEY_WGS84_LAT)),
                c.getInt(c.getColumnIndex(KEY_WGS84_LONG)),
                c.getString(c.getColumnIndex(KEY_NICK_NAME)),
                c.getInt(c.getColumnIndex(KEY_CHOSEN)),
                c.getString(c.getColumnIndex(KEY_SOURCE_ID)));
		return loc;
    }
    
    /**
     * @return rowId or -1 if failed
     */
    public long createLocation(String stopCode, String name, String desc, int lat, int lng, String srcPosA, String srcPosB, String heading, String sourceId) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_STOP_CODE, stopCode);
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_DESC, desc);
        initialValues.put(KEY_WGS84_LAT, lat);
        initialValues.put(KEY_WGS84_LONG, lng);
        initialValues.put(KEY_SRC_POS_A, srcPosA);
        initialValues.put(KEY_SRC_POS_B, srcPosB);
        initialValues.put(KEY_HEADING, heading);
        initialValues.put(KEY_NICK_NAME, "");
        initialValues.put(KEY_CHOSEN, 0);
        initialValues.put(KEY_SOURCE_ID, sourceId);

        return mDb.insert(BusTimesDBHelper.LOCATIONS_TABLE, null, initialValues);
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
    public boolean updateLocation(long rowId, String stopCode, String name, String desc, int lat, int lng, String srcPosA, String srcPosB, String heading, String nickName, int chosen, String sourceId) {
        ContentValues args = new ContentValues();
        args.put(KEY_STOP_CODE, stopCode);
        args.put(KEY_NAME, name);
        args.put(KEY_DESC, desc);
        args.put(KEY_WGS84_LAT, lat);
        args.put(KEY_WGS84_LONG, lng);
        args.put(KEY_SRC_POS_A, srcPosA);
        args.put(KEY_SRC_POS_B, srcPosB);
        args.put(KEY_HEADING, heading);
        args.put(KEY_NICK_NAME, nickName);
        args.put(KEY_CHOSEN, chosen);
        args.put(KEY_SOURCE_ID, sourceId);
        return mDb.update(BusTimesDBHelper.LOCATIONS_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    public boolean updateLocation(Location loc) {
    	return updateLocation(loc.getId(), loc.getStopCode(), loc.getLocationName(), loc.getDescription(), loc.getLat(), loc.getLon(), loc.getSrcPosA(), loc.getSrcPosB(), loc.getHeading(), loc.getNickName(), loc.getChosen(), loc.getSourceId());
    }

    public String getComboKey(String stopCode, String srcPosA, String srcPosB) {
    	return stopCode+"_"+srcPosA+"_"+srcPosB;
    }

    public Map<String, String> getComboKeys(String sourceId) {
    	String sql = "select stopCode, srcPosA, srcPosB from "+BusTimesDBHelper.LOCATIONS_TABLE+" where sourceId = '"+sourceId+"'";
    	Log.d(LOGNAME, "Getting combo keys.  SQL: "+sql);

    	Cursor c = mDb.rawQuery(sql, null);
    	if (null != c) c.moveToFirst();
    	
    	HashMap<String,String> keys = new HashMap<String,String>();
    	try {
	    	while (!c.isAfterLast()) {
	    		String stopCode = c.getString(c.getColumnIndex(KEY_STOP_CODE));
	    		String srcPosA = c.getString(c.getColumnIndex(KEY_SRC_POS_A));
	    		String srcPosB = c.getString(c.getColumnIndex(KEY_SRC_POS_B));

	    		String comboKey = getComboKey(stopCode, srcPosA, srcPosB);
	    		
	    		keys.put(comboKey, stopCode);
		    	c.moveToNext();
	    	}
	    	return keys;
    	} finally {
	    	if (null != c) c.close();
    	}
    }

	@Override
	protected String getTableName() {
		return BusTimesDBHelper.LOCATIONS_TABLE;
	}

	@Override
	protected String[] getAllColumnNames() {
		return ALL_COLUMNS;
	}
}
