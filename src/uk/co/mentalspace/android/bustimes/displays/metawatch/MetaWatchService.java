package uk.co.mentalspace.android.bustimes.displays.metawatch;

import android.app.IntentService;

import uk.co.mentalspace.android.bustimes.Coordinator;
import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.LocationTracker;
import android.content.Intent;
import android.util.Log;

public class MetaWatchService extends IntentService {
	private static final String LOGNAME = "MetaWatchService";
	
	private static final int BUTTON_NEXT_LOCATION = 5;
	
	private static Location loc = null;
	private LocationTracker posTracker = null;
	
	public MetaWatchService() {
		super("MetaWatchService");
	}
	
	protected LocationTracker getPosTracker() {
		if (null == posTracker) {
			posTracker = new LocationTracker(getApplicationContext());
		}
		return posTracker;
	}
	
	protected void onHandleIntent(Intent intent) {
		if (null == posTracker) {
			Log.d(LOGNAME, "Location Tracker not yet initialised - initialising...");
			posTracker = new LocationTracker(getApplicationContext());
		}
		
		Log.d(LOGNAME, "Meta Watch Service handling intent");
		final String action = intent.getAction();
		if (MetaWatchReceiver.MW_ACTIVATED.equals(action)) {
			Log.d(LOGNAME, "MetaWatch Activated, handing over to Coordinator");
//	        Coordinator.execute(this);
			MetaWatchDisplay mwd = new MetaWatchDisplay(getApplicationContext());
			if (null == loc) {
				LocationTracker pt = getPosTracker();
				int lat = (int)(pt.getLatitude()*10000);
				int lon = (int)(pt.getLongitude()*10000);
				loc = Coordinator.getNearestLocation(mwd, lat, lon);
			}
			Coordinator.getBusTimes(mwd, loc, false); //non-async
		}
		else if (MetaWatchReceiver.MW_DEACTIVATED.equals(action)) {
			Log.d(LOGNAME, "MetaWatch Deactivated, terminating");
//			Coordinator.terminate();
			terminate();
		}
		else if (MetaWatchReceiver.MW_BUTTON.equals(action)) {
			int btnId = intent.getIntExtra("button", -1);
			Log.d(LOGNAME, "Metawatch Button ["+btnId+"] pressed");
			if (BUTTON_NEXT_LOCATION == btnId) {
				MetaWatchDisplay mwd = new MetaWatchDisplay(getApplicationContext());
				Log.d(LOGNAME, "Getting next location. Current: "+loc.getLocationName());
				loc = Coordinator.getNextLocation(mwd, loc);
				Log.d(LOGNAME, "Next Location: "+loc.getLocationName());
				Coordinator.getBusTimes(mwd, loc);
			} else {
				Log.d(LOGNAME, "Wrong button. "+BUTTON_NEXT_LOCATION+" != "+btnId);
			}
		}
		else {
			Log.d(LOGNAME, "Unrecognised intent action: "+action);
		}
	}
	
	public void terminate() {
		if (null != posTracker) posTracker.stopTrackingLocation();
		stopSelf();
	}

}
