package uk.co.mentalspace.android.bustimes;

import java.util.Date;

import android.annotation.TargetApi;
import uk.co.mentalspace.android.bustimes.displays.android.FavouriteLocationsActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class LocationRefreshService extends WakefulIntentService {

	public static final String ACTION_UPDATE_DATA_REFRESH_PROGRESS = "uk.co.mentalspace.bustimes.UpdateDataRefreshProgress";
	public static final String ACTION_REFRESH_LOCATION_DATA = "uk.co.mentalspace.bustimes.RefreshLocationData";
	public static final String ACTION_CANCEL_DATA_REFRESH = "uk.co.mentalspace.bustimes.CancelDataRefresh";
	public static final String ACTION_GET_REFRESH_PROGRESS = "uk.co.mentalspace.bustimes.GetRefreshLocationProgress";
	public static final String ACTION_LOCATION_REFRESH_TASK_COMPLETE = "uk.co.mentalspace.bustimes.LocationRefreshTaskComplete";
	
	public static final String EXTRA_MAX_VALUE = "max";
	public static final String EXTRA_CURRENT_VALUE="current";
	public static final String EXTRA_PROGRESS_LABEL = "label";
	public static final String EXTRA_SOURCE_ID = "sourceId";	

	private static final String LOGNAME = "LocationRefreshService";
	
	private static LocationRefreshTask lrt = null;
	private static int lrtId = 0;
	
	public LocationRefreshService() {
		super("LocationRefreshService");
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		String action = intent.getAction();
	    if (ACTION_CANCEL_DATA_REFRESH.equals(action)) {
	    	Log.d(LOGNAME, "Terminating current data refresh...");
	    	terminateRunningRefresh();
	    }
	    if (ACTION_GET_REFRESH_PROGRESS.equals(action)) {
			updateProgressStatus(lrt);
	    }
	    super.onStart(intent, startId);
	}
	
	private void terminateRunningRefresh() {
		if (null != lrt) {
			Log.d(LOGNAME, "Calling 'Cancel' on current LRT instance");
			lrt.cancel();
		}
	}
	
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
//	protected void onHandleIntent(Intent intent) {
	public void processIntent(Intent intent) {
		String action = intent.getAction();
		Log.d(LOGNAME, "Handling intent. Action: "+action);
		if (ACTION_REFRESH_LOCATION_DATA.equals(action)) {
			String srcName = intent.getStringExtra(EXTRA_SOURCE_ID);
			if (null == srcName || "".equals(srcName.trim())) {
				Log.w(LOGNAME, "Request to refresh location data, but invalid source id ["+srcName+"] given");
				return;
			}

			Source src = SourceManager.getSourceBySourceId(getApplicationContext(), srcName);
			if (null == src) {
				Log.e(LOGNAME, "Request to refresh location data, but no matching Source for id ["+srcName+"]");
				return;
			}
			
			lrt = src.getLocationRefreshTask();
			lrt.init(this, this);
			lrtId = lrt.hashCode();
			try {
				long startTime = (new Date()).getTime();
				lrt.execute();
				long endTime = (new Date()).getTime();

				LocationManager.createRefreshRecord(getApplicationContext(), srcName, startTime, endTime);
				updateNotificationProgressComplete(lrt);
			} catch (Exception e) {
				Log.e(LOGNAME, "Unknown exception", e);
				setNotification(lrt.getSourceName(), e);
				return;
			} finally {
				//null local reference, whether it completed or not
			    lrt = null;
			    lrtId = 0;
			}
		}
	}
	
	public void setNotification(String title, String msg, int drawable) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
        .setSmallIcon(drawable)
        .setContentTitle(title)
        .setContentText(msg);

		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, FavouriteLocationsActivity.class);
		
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(FavouriteLocationsActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		// mId allows you to update the notification later on.
		mNotificationManager.notify(lrtId, mBuilder.build());	
	}
	
	public void setNotification(String srcName, Exception e) {
        setNotification(srcName + " location refresh failed", "Exception: "+e, android.R.drawable.ic_menu_close_clear_cancel);
	}
	
	public void updateNotificationProgressMade(LocationRefreshTask lrt) {
		Log.d(LOGNAME, "Updating notification (in progress) for source ["+lrt.getSourceName()+"]");
		String title = lrt.getSourceName()+" location refresh";
		String progressMessage = "In progress ("+lrt.getCurrentProgress()+" / ~"+lrt.getMaxProgress()+")";
		setNotification(title, progressMessage, android.R.drawable.stat_sys_download);
	}
	
	public void updateNotificationProgressComplete(LocationRefreshTask lrt) {
		Log.d(LOGNAME, "Updating notification (complete) for source ["+lrt.getSourceName()+"]");
		String title = lrt.getSourceName()+" location refresh";
		String progressMessage = "Complete ("+lrt.getCurrentProgress()+" processed)";
		setNotification(title, progressMessage, android.R.drawable.stat_sys_download_done);
	}

	public void updateProgressStatus(LocationRefreshTask lrt) {
		if (null == lrt) {
			Log.w(LOGNAME, "Request to generate a progress update for a null LRT.");
			return;
		}

		updateNotificationProgressMade(lrt);
		
		Log.d(LOGNAME, "Sending progress update intent for source ["+lrt.getSourceName()+"]");		
		Intent intent = new Intent();
		intent.setAction(ACTION_UPDATE_DATA_REFRESH_PROGRESS);
		intent.putExtra(EXTRA_MAX_VALUE, lrt.getMaxProgress());
		intent.putExtra(EXTRA_CURRENT_VALUE, lrt.getCurrentProgress());
		intent.putExtra(EXTRA_PROGRESS_LABEL, lrt.getCurrentProgressLabel());
		intent.putExtra(EXTRA_SOURCE_ID, lrt.getSourceId());
		this.sendBroadcast(intent);
	}

}
