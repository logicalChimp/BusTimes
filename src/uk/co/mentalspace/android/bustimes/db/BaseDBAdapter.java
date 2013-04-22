package uk.co.mentalspace.android.bustimes.db;

import java.util.ArrayList;
import java.util.List;

import uk.co.mentalspace.android.bustimes.Preferences;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public abstract class BaseDBAdapter<T> {
	private static final String LOGNAME = "BaseDBAdapter";
	
	protected BusTimesDBHelper mDbHelper;
    protected SQLiteDatabase mDb;

    protected final Context mCtx;


    /**
     * Constructor - takes the context to allow the database to be opened/created
     * @param ctx the Context within which to work
     */
    public BaseDBAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    /**
     * Get a read-only reference.  This should be safe to use even if
     * another thread is writing to the DB.
     * @return this (self reference, allowing this to be chained in an initialisation call)
     * @throws SQLException if the database could be neither opened or created
     */
    public void openReadable() throws SQLException {
        mDbHelper = new BusTimesDBHelper(mCtx);
        mDb = mDbHelper.getReadableDatabase();
    }

    /**
     * Opens the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an initialisation call)
     * @throws SQLException if the database could be neither opened or created
     */
    public void open() throws SQLException {
        mDbHelper = new BusTimesDBHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
    }
    
    public void close() {
        mDbHelper.close();
    }

    /**
     * Delete the location with the given rowId
     * @param rowId id of the location to delete
     * @return true if deleted, false otherwise
     */
    protected boolean delete(String whereClause) {
        return mDb.delete(getTableName(), whereClause, null) > 0;
    }
        
    public Cursor fetch(String tableName, String[] cols, String whereClause) {
        Cursor mCursor = mDb.query(true, tableName, cols, whereClause, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
    
    public boolean update(String tableName, ContentValues args, String whereClause) {
        return mDb.update(tableName, args, whereClause, null) > 0;
    }
    
    public T getSingle(Cursor c) {
    	try {
        	if (null == c || c.getCount() == 0) return null;
        	if (c.isBeforeFirst()) c.moveToFirst();
        	return populateFromCursor(c);
    	} catch (SQLException sle) {
    		if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "Database error", sle);
    		return null;
    	} finally {
    		if (null != c) c.close();
    	}
    }
    
    public List<T> getList(Cursor c) {
    	List<T> rtn = new ArrayList<T>();
    	if (null == c) return rtn;

    	try {
	    	if (c.isBeforeFirst()) c.moveToFirst();
	    	while (!c.isAfterLast()) {
	    		T single = populateFromCursor(c);
		    	rtn.add(single);
		    	c.moveToNext();
	    	}
    	} catch (SQLiteException sle) {
    		if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "Database error", sle);
    		return rtn;
    	} finally {
	    	if (null != c) c.close();
    	}
    	return rtn;
    }
        
    protected abstract T populateFromCursor(Cursor c);
    protected abstract String getTableName();
    protected abstract String[] getAllColumnNames();
}
