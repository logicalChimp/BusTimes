package uk.co.mentalspace.android.bustimes.db;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

public class LocationsRefreshDBAdapter extends BaseDBAdapter<Void> {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_SOURCE_ID = "sourceId";
	public static final String KEY_START_TIME = "startTime";
	public static final String KEY_END_TIME = "endTime";

	private static final String[] ALL_COLUMNS = {KEY_ROWID, KEY_SOURCE_ID, KEY_START_TIME, KEY_END_TIME};

    /**
     * Constructor - takes the context to allow the database to be opened/created
     * @param ctx the Context within which to work
     */
    public LocationsRefreshDBAdapter(Context ctx) {
    	super(ctx);
    }

    /**
     * @return rowId or -1 if failed
     */
    public long createBTRefreshRecord(String sourceId, long startTime, long endTime) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SOURCE_ID, sourceId);
        initialValues.put(KEY_START_TIME, startTime);
        initialValues.put(KEY_END_TIME, endTime);

        return mDb.insert(BusTimesDBHelper.BUS_TIMES_REFRESH_TABLE, null, initialValues);
    }
    
    /**
     * Delete the location with the given rowId
     * @param rowId id of the location to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteBTRefreshRecord(long rowId) {
        return delete(KEY_ROWID + "=" + rowId);
    }

    public Cursor fetchMostRecentRefreshRecord(String sourceId) {
    	return fetch(BusTimesDBHelper.LOCATIONS_TABLE, ALL_COLUMNS, KEY_SOURCE_ID + "=" + sourceId);
    }
    
    public long getTimeOfLastRefresh(String sourceId) {
    	long noRecord = -1;
    	Cursor c = null;
    	try {
    		c = fetchMostRecentRefreshRecord(sourceId);
        	if (null == c || c.getCount() == 0) return noRecord;
            long refreshTime = c.getLong(c.getColumnIndex(KEY_END_TIME));
            return refreshTime;
    	} catch (SQLException sqle) {
    		return noRecord;
    	} finally {
    		if (null != c) try {c.close(); } catch (Exception e) { /*Ignore because there is nothing to be done here */}
    	}
    }

	@Override
	protected Void populateFromCursor(Cursor c) {
		//not used
		return null;
	}

	@Override
	protected String getTableName() {
		return BusTimesDBHelper.BUS_TIMES_REFRESH_TABLE;
	}

	@Override
	protected String[] getAllColumnNames() {
		return ALL_COLUMNS;
	}
}
