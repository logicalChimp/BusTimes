package uk.co.mentalspace.android.bustimes;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;
import uk.co.mentalspace.android.bustimes.db.SourcesDBAdapter;
import uk.co.mentalspace.android.bustimes.sources.londonuk.LondonUK;
import uk.co.mentalspace.android.bustimes.sources.test.TestSource;

public class SourceManager {
	private static final String LOGNAME = "SourceManager";
	
	public static Source getSource(String id) {
		if ("londonuk-tfl".equals(id)) return new LondonUK();
		if ("TestSource".equals(id)) return new TestSource();
		else return null;
	}
	
	public static void createSource(Context ctx, Source src) {
		Log.d(LOGNAME, "Writing source ["+src+"] to db");
        SourcesDBAdapter sdba = new SourcesDBAdapter(ctx);
        try {
	        sdba.open();
	        sdba.createSource(src.getID(), src.getName(), src.getEstimatedLocationCount(), src.getLocationRefreshClassName(), src.getBTRefreshClassName(), src.getPolygonPointsJson());
        } catch (SQLiteException sle) {
        	Log.e(LOGNAME, "Failed to open Database: ", sle);
        	Toast.makeText(ctx, "Unable to create source", Toast.LENGTH_SHORT).show();
        } finally {
        	if (null != sdba) try {sdba.close(); } catch (Exception e) {Log.e(LOGNAME, "Unknown exception", e); }
        }
	}
	
	public static Source getSourceBySourceId(Context ctx, String srcId) {
		Log.d(LOGNAME, "Getting source for source id ["+srcId+"]");
        SourcesDBAdapter sdba = new SourcesDBAdapter(ctx);
        try {
	        sdba.openReadable();
	        Source src = sdba.getSourceById(srcId);
	        return src;
        } catch (SQLiteException sle) {
        	Log.e(LOGNAME, "Failed to open Database: ", sle);
        	Toast.makeText(ctx, "Unable to retrieve source", Toast.LENGTH_SHORT).show();
        	return null;
        } finally {
        	if (null != sdba) try {sdba.close(); } catch (Exception e) {Log.e(LOGNAME, "Unknown exception", e); }
        }
	}

	public static List<Source> getAllSources(Context ctx) {
		Log.d(LOGNAME, "Getting all sources");
        SourcesDBAdapter sdba = new SourcesDBAdapter(ctx);
        try {
	        sdba.openReadable();
	        List<Source> srcs = sdba.getAllSources();
	        if (null == srcs) srcs = new ArrayList<Source>();
	        return srcs;
        } catch (SQLiteException sle) {
        	Log.e(LOGNAME, "Failed to open Database: ", sle);
        	Toast.makeText(ctx, "Unable to retrieve all sources", Toast.LENGTH_SHORT).show();
    		return new ArrayList<Source>();
        } finally {
        	if (null != sdba) try {sdba.close(); } catch (Exception e) {Log.e(LOGNAME, "Unknown exception", e); }
        }
	}
	
}
