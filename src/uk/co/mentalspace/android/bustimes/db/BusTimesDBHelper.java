package uk.co.mentalspace.android.bustimes.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import uk.co.mentalspace.android.bustimes.Preferences;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BusTimesDBHelper extends SQLiteOpenHelper {
	private static final String LOGNAME = "BusTimesDBHelper";
    private static final String DATABASE_NAME = "BusTimesLocationsData.db";
    private static final int DATABASE_VERSION = 7;

    public static final String LOCATIONS_TABLE = "locations";
    public static final String BUS_TIMES_REFRESH_TABLE = "btrefreshlog";
    public static final String SOURCES_TABLE = "sources";
    
    private Context ctx = null;

    public BusTimesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        setDefaultOptions(db);
        onUpgrade(db, 0, DATABASE_VERSION); //run ALL upgrade scripts - inc. v1 - to create a full database
    }
    
    private void setDefaultOptions(SQLiteDatabase db) {
    }

    public static boolean executeSQLScript(Context ctx, SQLiteDatabase database, String scriptName) {
    	return executeSQLScript(ctx, database, scriptName, null);
    }
    
    public static boolean executeSQLScript(Context ctx, SQLiteDatabase database, String scriptName, String prepend) {
    	if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Executing script ["+scriptName+"]");
		AssetManager assetManager = ctx.getAssets();

		BufferedReader br = null;
		database.beginTransaction();
	    try{
	        InputStream inputStream = assetManager.open(scriptName);
	        br = new BufferedReader(new InputStreamReader(inputStream));

	        String line = null;
	        while (null != (line = br.readLine())) {
				String sqlStatement = line.trim(); //createScript[i].trim();
	            // TODO You may want to parse out comments here
	            if (sqlStatement.length() > 0) {
					if (null != prepend) sqlStatement = prepend + sqlStatement;
                    database.execSQL(sqlStatement + ";");
                }
	        }
	        database.setTransactionSuccessful();
	        return true;
	    } catch (IOException e){
	    	if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "IOException occured loading script", e);
	    } catch (SQLException e) {
	    	if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "SQLException occured loading script", e);
	    } finally {
	    	if (null != br) try {br.close();} catch (IOException ioe) {/*Do nothing here */};
    		if (null != database) database.endTransaction();
	    }
	    return false;
	}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Upgrading from ["+oldVersion+"] to ["+newVersion+"]");
    	if (oldVersion < newVersion) {
    		//explicitly fall-through - start at 'oldVersion', and run each upgrade to reach 'newVersion'
    		switch (oldVersion) {
    		case 0:
    			BusTimesDBHelper.executeSQLScript(ctx, db, "db_upgrade_to_version_1.sql");
    		case 1:
    			BusTimesDBHelper.executeSQLScript(ctx, db, "db_upgrade_to_version_2.sql");
    		case 2:
    			BusTimesDBHelper.executeSQLScript(ctx, db, "db_upgrade_to_version_3.sql");
    		case 3:
    			BusTimesDBHelper.executeSQLScript(ctx, db, "db_upgrade_to_version_4.sql");
    		case 4:
    			BusTimesDBHelper.executeSQLScript(ctx, db, "db_upgrade_to_version_5.sql");
    		case 5:
    			BusTimesDBHelper.executeSQLScript(ctx, db, "db_upgrade_to_version_6.sql");
    		case 6:
    			BusTimesDBHelper.executeSQLScript(ctx, db, "db_upgrade_to_version_7.sql");
    		}
    	}
    }
    
}

