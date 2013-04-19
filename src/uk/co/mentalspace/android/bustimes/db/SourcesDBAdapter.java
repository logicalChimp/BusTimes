package uk.co.mentalspace.android.bustimes.db;

import java.util.List;
import uk.co.mentalspace.android.bustimes.Source;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class SourcesDBAdapter extends BaseDBAdapter<Source> {
	private static final String KEY_ROWID = "_id";
	private static final String KEY_SOURCE_ID = "sourceId";
	private static final String KEY_SOURCE_NAME = "name";
	private static final String KEY_EST_LOC_COUNT = "estLocationCount";
	private static final String KEY_LOC_REFRESH_CLASSNAME = "locationRefreshClassName";
	private static final String KEY_BT_REFRESH_CLASSNAME = "busTimeRefreshClassName";
	private static final String KEY_POLYGON_POINTS_JSON = "areaPolygonPointsJson";

	private static final String[] ALL_COLUMNS = {KEY_ROWID, KEY_SOURCE_ID, KEY_SOURCE_NAME, KEY_EST_LOC_COUNT, KEY_LOC_REFRESH_CLASSNAME, KEY_BT_REFRESH_CLASSNAME, KEY_POLYGON_POINTS_JSON}; 

    /**
     * Constructor - takes the context to allow the database to be opened/created
     * @param ctx the Context within which to work
     */
    public SourcesDBAdapter(Context ctx) {
        super(ctx);
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
    	return delete(KEY_ROWID + "=" + rowId);
    }

    public Cursor fetchAllSources() {
    	return fetch(BusTimesDBHelper.SOURCES_TABLE, ALL_COLUMNS, null);
    }
    
    public List<Source> getAllSources() {
    	Cursor c = fetchAllSources();
    	return getList(c);
    }
    
    public Cursor fetchSourceById(String srcId) {
    	return fetch(BusTimesDBHelper.SOURCES_TABLE, ALL_COLUMNS, KEY_SOURCE_ID + "= '" + srcId +"'");
    }
    
    public Source getSourceById(String srcId) {
		Cursor c = fetchSourceById(srcId);
		return getSingle(c);
    }

    @Override
	protected Source populateFromCursor(Cursor c) {
    	Source src = new Source(c.getLong(c.getColumnIndex(KEY_ROWID)),
                c.getString(c.getColumnIndex(KEY_SOURCE_ID)),
                c.getString(c.getColumnIndex(KEY_SOURCE_NAME)),
                c.getInt(c.getColumnIndex(KEY_EST_LOC_COUNT)),
                c.getString(c.getColumnIndex(KEY_LOC_REFRESH_CLASSNAME)),
                c.getString(c.getColumnIndex(KEY_BT_REFRESH_CLASSNAME)),
                c.getString(c.getColumnIndex(KEY_POLYGON_POINTS_JSON)));
    	return src;
    }

	@Override
	protected String getTableName() {
		return BusTimesDBHelper.SOURCES_TABLE;
	}

	@Override
	protected String[] getAllColumnNames() {
		return ALL_COLUMNS;
	}
}
