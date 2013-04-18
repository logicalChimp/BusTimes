package uk.co.mentalspace.android.bustimes.db;

import java.util.ArrayList;
import java.util.List;
import uk.co.mentalspace.android.bustimes.Source;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SourcesDBAdapter {
	private static final String LOGNAME = "SourcesDBAdapter";
	
	private static final String KEY_ROWID = "_id";
	private static final String KEY_SOURCE_ID = "sourceId";
	private static final String KEY_SOURCE_NAME = "name";
	private static final String KEY_EST_LOC_COUNT = "estLocationCount";
	private static final String KEY_LOC_REFRESH_CLASSNAME = "locationRefreshClassName";
	private static final String KEY_BT_REFRESH_CLASSNAME = "busTimeRefreshClassName";
	private static final String KEY_POLYGON_POINTS_JSON = "areaPolygonPointsJson";

	private static final String[] ALL_COLUMNS = {KEY_ROWID, KEY_SOURCE_ID, KEY_SOURCE_NAME, KEY_EST_LOC_COUNT, KEY_LOC_REFRESH_CLASSNAME, KEY_BT_REFRESH_CLASSNAME, KEY_POLYGON_POINTS_JSON}; 
    private BusTimesDBHelper mDbHelper;
    private SQLiteDatabase mDb;

    private final Context mCtx;


    /**
     * Constructor - takes the context to allow the database to be opened/created
     * @param ctx the Context within which to work
     */
    public SourcesDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Get a read-only reference.  This should be safe to use even if
     * another thread is writing to the DB.
     * @return this (self reference, allowing this to be chained in an initialisation call)
     * @throws SQLException if the database could be neither opened or created
     */
    public SourcesDBAdapter openReadable() throws SQLException {
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
    public SourcesDBAdapter open() throws SQLException {
        mDbHelper = new BusTimesDBHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }
    
    public void close() {
        mDbHelper.close();
    }

    public long createSource(String srcId, String srcName, int estLocCount, String locRefreshClassname, String btRefreshClassname, String polygonPointsJson) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SOURCE_ID, srcId);
        initialValues.put(KEY_SOURCE_NAME, srcName);
        initialValues.put(KEY_EST_LOC_COUNT, estLocCount);
        initialValues.put(KEY_LOC_REFRESH_CLASSNAME, locRefreshClassname);
        initialValues.put(KEY_BT_REFRESH_CLASSNAME, btRefreshClassname);
        initialValues.put(KEY_POLYGON_POINTS_JSON, polygonPointsJson);

        return mDb.insert(BusTimesDBHelper.SOURCES_TABLE, null, initialValues);
    }

    /**
     * Delete the source with the given rowId
     * @param rowId id of the location to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteSource(long rowId) {
        return mDb.delete(BusTimesDBHelper.SOURCES_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchAllSources() {
        return mDb.query(BusTimesDBHelper.SOURCES_TABLE, ALL_COLUMNS, null, null, null, null, null);
    }
    
    public List<Source> getAllSources() {
    	Cursor c = fetchAllSources();
    	if (null == c) return null;

    	ArrayList<Source> srcs = new ArrayList<Source>();
    	try {
	    	c.moveToFirst();
	    	while (!c.isAfterLast()) {
		    	Source src = generateSource(c);
		    	srcs.add(src);
		    	c.moveToNext();
	    	}
    	} finally {
	    	if (null != c) c.close();
    	}
    	return srcs;
    }
    
    public Cursor fetchSourceById(String srcId) {
        Cursor mCursor = mDb.query(true, BusTimesDBHelper.SOURCES_TABLE, ALL_COLUMNS, KEY_ROWID + "=" + srcId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public Source getSourceById(String srcId) {
    	Cursor c = null;
    	try {
    		c = fetchSourceById(srcId);
        	if (null == c || c.getCount() == 0) return null;        	
        	return generateSource(c);
    	} catch (SQLException sqle) {
    		Log.e(LOGNAME, "Failed to locate closest selected location", sqle);
    		return null;
    	} finally {
    		if (null != c) c.close();
    	}
    }

    private Source generateSource(Cursor c) {
    	Source src = new Source(c.getLong(c.getColumnIndex(KEY_ROWID)),
                c.getString(c.getColumnIndex(KEY_SOURCE_ID)),
                c.getString(c.getColumnIndex(KEY_SOURCE_NAME)),
                c.getInt(c.getColumnIndex(KEY_EST_LOC_COUNT)),
                c.getString(c.getColumnIndex(KEY_LOC_REFRESH_CLASSNAME)),
                c.getString(c.getColumnIndex(KEY_BT_REFRESH_CLASSNAME)),
                c.getString(c.getColumnIndex(KEY_POLYGON_POINTS_JSON)));
    	return src;
    }
}
