package uk.co.mentalspace.android.bustimes.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LocationsRefreshDBAdapter {

	public static final String KEY_ROWID = "_id";
	public static final String KEY_SOURCE_ID = "sourceId";
	public static final String KEY_START_TIME = "startTime";
	public static final String KEY_END_TIME = "endTime";

	private static final String[] ALL_COLUMNS = {KEY_ROWID, KEY_SOURCE_ID, KEY_START_TIME, KEY_END_TIME};
	private BusTimesDBHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final Context mCtx;

    /**
     * Constructor - takes the context to allow the database to be opened/created
     * @param ctx the Context within which to work
     */
    public LocationsRefreshDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Get a read-only reference.  This should be safe to use even if
     * another thread is writing to the DB.
     * @return this (self reference, allowing this to be chained in an initialisation call)
     * @throws SQLException if the database could be neither opened or created
     */
    public LocationsRefreshDBAdapter openReadable() throws SQLException {
        mDbHelper = new BusTimesDBHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
        return this;
    }

    /**
     * Opens the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an initialisation call)
     * @throws SQLException if the database could be neither opened or created
     */
    public LocationsRefreshDBAdapter open() throws SQLException {
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
        return mDb.delete(BusTimesDBHelper.BUS_TIMES_REFRESH_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchMostRecentRefreshRecord(String sourceId) {
        Cursor mCursor =
                mDb.query(true, BusTimesDBHelper.LOCATIONS_TABLE, ALL_COLUMNS, KEY_SOURCE_ID + "=" + sourceId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
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
}
