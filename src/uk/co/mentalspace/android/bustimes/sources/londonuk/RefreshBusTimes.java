package uk.co.mentalspace.android.bustimes.sources.londonuk;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import uk.co.mentalspace.android.bustimes.DataRefreshTask;

public class RefreshBusTimes extends DataRefreshTask {
	private static final String LOGNAME = "RefreshBusTimes";
	
	@Override
	public void run() {
		display.execute(new Runnable() {
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			public void run() {
				Log.d(LOGNAME, "Refreshing Bus Times asyncronously");
				LondonUK_AsyncBusTimes async = getBusTimesTask();
				if (null == async) return;

				Log.d(LOGNAME, "Executing async task");
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) async.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			    else async.execute();
			}
		});
	}
	
	@Override
	public void executeSync() {
		LondonUK_AsyncBusTimes async = getBusTimesTask();
		if (null == async) return;
		
		Log.d(LOGNAME, "Executing async task synchronously");
		async.executeSync();
	}
	
	private LondonUK_AsyncBusTimes getBusTimesTask() {
        //fetch list of bus times for the location
		LondonUK_AsyncBusTimes async = null;
		try {
			Log.d(LOGNAME, "Creating async task...");
			async = new LondonUK_AsyncBusTimes();
		} catch (Exception e) {
			Log.d(LOGNAME, "Unexpected error", e);
			return null;
		}
		
		Log.d(LOGNAME, "Initialising async task with display, location");
		async.init(display, location);

		return async;
	}
}
