package uk.co.mentalspace.android.bustimes;

import uk.co.mentalspace.android.bustimes.db.LocationsDBAdapter;
import android.content.Context;
import android.util.Log;

public abstract class LocationRefreshTask {
	private static final String LOGNAME = "LocationRefreshTask";
	
	protected static final int PROGRESS_POSITION_CONTACTING_SERVER = 0;
	protected static final int PROGRESS_POSITION_DOWNLOADING_DATA = 1;
	protected static final int PROGRESS_POSITION_PROCESSING_DATA = 2;
	
	protected static final int PROGRESS_INDEX_PROGRESS_LABEL = 0;
	protected static final int PROGRESS_INDEX_PROGRESS_VALUE = 1;
	
	protected boolean isFinished = false;
	protected boolean isCancelled = false;
	protected Context ctx = null;
	protected LocationsDBAdapter ldba = null;	
	protected LocationRefreshService lrs = null;
	
	protected int currentProgress = 0;
	protected String currentProgressLabel = "";
	
	public void init(Context ctx, LocationRefreshService lrs) {
		this.ctx = ctx; 
		this.lrs = lrs;
	}
		
	public int getCurrentProgress() {
		return currentProgress;
	}
	
	public String getCurrentProgressLabel() {
		return currentProgressLabel;
	}
	
	public boolean isFinished() {
		return isFinished;
	}
	
	public boolean isCancelled() {
		return isCancelled;
	}
	
	public void cancel() {
		isCancelled = true;
	}
	
	protected void publishProgress(String label, int value) {
		currentProgress = value;
		currentProgressLabel = label;

		lrs.updateProgressStatus(this);
	}
	
	protected void finish() {
		Log.d(LOGNAME, "finish called");
		isFinished = true;
	}

	public void execute() throws Exception {
		try {
			ldba = new LocationsDBAdapter(ctx);
			ldba.open();
			performRefresh();
		} finally {
			if (null != ldba) {
				try { ldba.close(); } catch (Exception e) { Log.e(LOGNAME, "Unknown exception", e); }
			}
		}
	}

	public abstract int getMaxProgress();
	public abstract void performRefresh();
	public abstract String getSourceId();
	public abstract String getSourceName();
	
}
