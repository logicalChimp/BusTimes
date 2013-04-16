package uk.co.mentalspace.android.bustimes;

import java.util.HashMap;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class DataRefreshService extends IntentService {

	public static final String ACTION_UPDATE_DATA_REFRESH_PROGRESS = "uk.co.mentalspace.bustimes.UpdateDataRefreshProgress";
	public static final String ACTION_REFRESH_LOCATION_DATA = "uk.co.mentalspace.bustimes.RefreshLocationData";
	public static final String ACTION_CANCEL_DATA_REFRESH = "uk.co.mentalspace.bustimes.CancelDataRefresh";
	public static final String ACTION_GET_REFRESH_PROGRESS = "uk.co.mentalspace.bustimes.GetRefreshLocationProgress";
	public static final String ACTION_LOCATION_REFRESH_TASK_COMPLETE = "uk.co.mentalspace.bustimes.LocationRefreshTaskComplete";
	
	public static final String EXTRA_MAX_VALUE = "max";
	public static final String EXTRA_CURRENT_VALUE="current";
	public static final String EXTRA_PROGRESS_LABEL = "label";
	public static final String EXTRA_SOURCE_NAME = "source";	

	private static final String LOGNAME = "DataRefreshService";
	
	private static final HashMap<String,LocationRefreshTask> lrts = new HashMap<String,LocationRefreshTask>();
	
	public DataRefreshService() {
		super("DataRefreshService");
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		String action = intent.getAction();
	    if (ACTION_CANCEL_DATA_REFRESH.equals(action)) {
	    	Log.d(LOGNAME, "Terminating current data refresh...");
	    	String srcId = intent.getStringExtra(EXTRA_SOURCE_NAME);
	    	terminateRunningRefresh(srcId);
	    }
	    if (ACTION_GET_REFRESH_PROGRESS.equals(action)) {
	    	if (lrts.isEmpty()) {
	    		Log.d(LOGNAME, "Request for current progress - but no active location refresh tasks");
	    	} else {
	    		String srcName = intent.getStringExtra(EXTRA_SOURCE_NAME);
	    		if (null == srcName || "".equals(srcName.trim())) {
	    			Log.d(LOGNAME, "No source specified - sending update for ALL running tasks");
		    		updateProgressStatusForAllTasks();
	    		} else {
		    		Log.d(LOGNAME, "Sending current progress for source ["+srcName+"]");
	    			updateProgressStatus(srcName);
	    		}
	    	}
	    }
	    if (ACTION_LOCATION_REFRESH_TASK_COMPLETE.equals(action)) {
	    	String srcId = intent.getStringExtra(EXTRA_SOURCE_NAME);
	    	Log.d(LOGNAME, "Location refresh task for source ["+srcId+"] complete");
	    	
	    	if (null == srcId || "".equals(srcId.trim())) {
	    		Log.e(LOGNAME, "Location Refresh Task Complete reported with null/empty source name ["+srcId+"]");
	    	} else {
	    		LocationRefreshTask lrt = lrts.get(srcId);
	    		if (!lrt.isFinished()) {
	    			Log.w(LOGNAME, "Requested LRT is NOT finished! removing anyway...");
	    		}
	    		Log.d(LOGNAME, "Removing LRT for source ["+srcId+"] from collection");
	    		lrts.remove(srcId);
	    	}
	    }
	    super.onStart(intent, startId);
	}
	
	private void terminateRunningRefresh(String sourceId) {
		Log.d(LOGNAME, "Request to terminate LRT for Source: "+sourceId);
		LocationRefreshTask lrt = lrts.get(sourceId);
		if (null == lrt) return;
		if (lrt.isCancelled()) return;
		lrt.cancel(true);
	}
	
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		Log.d(LOGNAME, "Handling intent. Action: "+action);
		if (ACTION_REFRESH_LOCATION_DATA.equals(action)) {
			String srcName = intent.getStringExtra(EXTRA_SOURCE_NAME);
			if (null == srcName || "".equals(srcName.trim())) {
				Log.w(LOGNAME, "Request to refresh location data, but invalid source id ["+srcName+"] given");
				return;
			}

			if (lrts.containsKey(srcName)) {
				Log.w(LOGNAME, "Request to refresh locations for source ["+srcName+"], but refresh already running - ignoring");
				return;
			}
			
			Source src = SourceManager.getSource(srcName);
			if (null == src) {
				Log.e(LOGNAME, "Request to refresh location data, but no matching Source for id ["+srcName+"]");
				return;
			}
			
			LocationRefreshTask lrt = src.getLocationRefreshTask();
			lrt.init(this);
			lrts.put(srcName, lrt);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) lrt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		    else lrt.execute();
		}
	}
	
	private void updateProgressStatusForAllTasks() {
		Set<String> srcs = lrts.keySet();
		for (String src: srcs) {
			LocationRefreshTask lrt = lrts.get(src);
			if (lrt.isCancelled()) continue;
			if (lrt.isFinished()) continue;
			updateProgressStatus(lrt);
		}
	}
	
	private void updateProgressStatus(String srcName) {
		if (null == srcName || "".equals(srcName.trim())) {
			Log.e(LOGNAME, "Request to update status for invalid source id ["+srcName+"]");
			return;
		}
		
		LocationRefreshTask lrt = lrts.get(srcName);
		updateProgressStatus(lrt);
	}
	
	public void updateProgressStatus(LocationRefreshTask lrt) {
		if (null == lrt) {
			Log.w(LOGNAME, "Request to generate a progress update for a null LRT.");
			return;
		}
		
		Log.d(LOGNAME, "Sending progress update intent for source ["+lrt.getSourceId()+"]");
		
		Intent intent = new Intent();
		intent.setAction(ACTION_UPDATE_DATA_REFRESH_PROGRESS);
		intent.putExtra(EXTRA_MAX_VALUE, lrt.getMaxProgress());
		intent.putExtra(EXTRA_CURRENT_VALUE, lrt.getCurrentProgress());
		intent.putExtra(EXTRA_PROGRESS_LABEL, lrt.getCurrentProgressLabel());
		intent.putExtra(EXTRA_SOURCE_NAME, lrt.getSourceId());
		this.sendBroadcast(intent);
	}

}
