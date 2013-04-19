package uk.co.mentalspace.android.bustimes;

import java.util.List;

import android.content.Context;
import android.util.Log;
import uk.co.mentalspace.android.bustimes.db.BaseDBAdapter;
import uk.co.mentalspace.android.bustimes.db.SourcesDBAdapter;

public class SourceManager extends BaseManager<Source> {
	private static final String LOGNAME = "SourceManager";
	
	protected static class SrcTask<E> extends Task<E> {
		protected SourcesDBAdapter sdba = null;
		protected BaseDBAdapter<?> getDBAdapter(Context ctx) {
	        if (null == sdba) sdba = new SourcesDBAdapter(ctx);
	        return sdba;
		}
	}
	
	public static void createSource(Context ctx, final Source src) {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Writing source ["+src+"] to db");
		Task<Void> srcTask = new SrcTask<Void>() {
			protected Void doWork() {
		        sdba.createSource(src.getID(), src.getName(), src.getEstimatedLocationCount(), src.getLocationRefreshClassName(), src.getBTRefreshClassName(), src.getPolygonPointsJson());
		        return null;
			}
		};
		srcTask.run(ctx);
	}
	
	public static Source getSourceBySourceId(Context ctx, final String srcId) {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Getting source for source id ["+srcId+"]");
		Task<Source> srcTask = new SrcTask<Source>() {
			protected Source doWork() {
		        return sdba.getSourceById(srcId);
			}
		};
		return srcTask.run(ctx);
	}

	public static List<Source> getAllSources(Context ctx) {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Getting all sources");
		Task<List<Source>> srcTask = new SrcTask<List<Source>>() {
			protected List<Source> doWork() {
		        List<Source> srcs = sdba.getAllSources();
		        return srcs;
			}
		};
		return srcTask.run(ctx);
	}
	
}
