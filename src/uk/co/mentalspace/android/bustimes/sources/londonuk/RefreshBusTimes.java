package uk.co.mentalspace.android.bustimes.sources.londonuk;

import android.util.Log;
import uk.co.mentalspace.android.bustimes.DataRefreshTask;

public class RefreshBusTimes extends DataRefreshTask {
	private static final String LOGNAME = "RefreshBusTimes";
	
	@Override
	public void run() {
		display.execute(new Runnable() {
			public void run() {
				executeSync();
			}
		});
	}
	
	public void executeSync() {
        //fetch list of bus times for the location
		LondonUK_AsyncBusTimes async = null;
		try {
			Log.d(LOGNAME, "Creating async task...");
			async = new LondonUK_AsyncBusTimes();
		} catch (Exception e) {
			Log.d(LOGNAME, "Unexpected error", e);
			return;
		}
		
		Log.d(LOGNAME, "Initialising async task with display, location");
		async.init(display, location);
		
		Log.d(LOGNAME, "Executing async task");
		async.execute();
	}
}
