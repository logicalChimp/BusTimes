package uk.co.mentalspace.android.bustimes.displays.metawatch;

import java.util.List;

import uk.co.mentalspace.android.bustimes.BusTime;
import uk.co.mentalspace.android.bustimes.BusTimeRefreshService;
import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.LocationManager;
import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.Renderer;
import uk.co.mentalspace.android.bustimes.WakefulIntentService;
import uk.co.mentalspace.android.bustimes.utils.LocationTracker;
import android.content.Intent;
import android.util.Log;

public class MetaWatchService extends WakefulIntentService {
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
	
	public void processIntent(Intent intent) {
		if (null == posTracker) {
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Location Tracker not yet initialised - initialising...");
			posTracker = new LocationTracker(getApplicationContext());
		}
		
		try {
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Meta Watch Service handling intent");
			final String action = intent.getAction();
			if (MetaWatchReceiver.MW_ACTIVATED.equals(action)) {
				if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "MetaWatch app activated, handing over to Coordinator");
				MetaWatchDisplay mwd = new MetaWatchDisplay(getApplicationContext());
				if (null == loc) {
					LocationTracker pt = getPosTracker();
					int lat = (int)(pt.getLatitude()*10000);
					int lon = (int)(pt.getLongitude()*10000);
					loc = LocationManager.getNearestSelectedLocation(getApplicationContext(), lat, lon);
				}

				getBusTimes(mwd, loc); 
			}
			else if (MetaWatchReceiver.MW_DEACTIVATED.equals(action)) {
				if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "MetaWatch app deactivated, displaying blank screen and terminating");
				MetaWatchDisplay mwd = new MetaWatchDisplay(getApplicationContext());
				mwd.displayMessage(null, "", MetaWatchDisplay.MESSAGE_NORMAL);
				//GPS will auto-disconnect when this function terminates
			}
			else if (MetaWatchReceiver.MW_BUTTON.equals(action)) {
				int btnId = intent.getIntExtra("button", -1);
				if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Metawatch Button ["+btnId+"] pressed");
				if (BUTTON_NEXT_LOCATION == btnId) {
					MetaWatchDisplay mwd = new MetaWatchDisplay(getApplicationContext());
					if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Getting next location. Current: "+loc.getLocationName());
					
					loc = LocationManager.getNextLocation(getApplicationContext(), loc);
					if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Next Location: "+loc.getLocationName());
					getBusTimes(mwd, loc);
				} else {
					if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Wrong button. "+BUTTON_NEXT_LOCATION+" != "+btnId);
				}
			}
			else if (BusTimeRefreshService.ACTION_LATEST_BUS_TIMES.equals(action)) {
				if (null == loc) {
					if (Preferences.ENABLE_LOGGING) Log.w(LOGNAME, "Received updated bus times, but no location selected.  Ignoring");
				} else {
					long locId = intent.getLongExtra(BusTimeRefreshService.EXTRA_LOCATION_ID, -1);
					String srcId = intent.getStringExtra(BusTimeRefreshService.EXTRA_SOURCE_ID);
					
					if (loc.getId() != locId || !loc.getSourceId().equals(srcId)) {
						if (Preferences.ENABLE_LOGGING) Log.w(LOGNAME, "Received updated bus times, but for a different location than selected.  Ignoring.");
					} else {
						if (intent.hasExtra(BusTimeRefreshService.EXTRA_BUS_TIMES)) {
							@SuppressWarnings("unchecked")
							List<BusTime> busTimes = (List<BusTime>)intent.getSerializableExtra(BusTimeRefreshService.EXTRA_BUS_TIMES);
							
							int busTimesSize = (null == busTimes) ? -1 : busTimes.size();
							if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Received ["+busTimesSize+"] bus times");

							MetaWatchDisplay mwd = new MetaWatchDisplay(getApplicationContext());
							mwd.displayBusTimes(loc, busTimes);
						}
					}
				}
			}
			else {
				if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Unrecognised intent action: "+action);
			}
		} finally {
			terminate();	
		}
	}
	
	public void getBusTimes(Renderer display, Location loc) {
		Intent service = new Intent(this, BusTimeRefreshService.class);
		service.setAction(BusTimeRefreshService.ACTION_REFRESH_BUS_TIMES);
		service.putExtra(BusTimeRefreshService.EXTRA_LOCATION_ID, loc.getId());
		service.putExtra(BusTimeRefreshService.EXTRA_SOURCE_ID, loc.getSourceId());
		this.startService(service);

		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Initiating request of bus times for location: "+loc);
		display.displayMessage(loc, "Getting bus times", Renderer.MESSAGE_NORMAL);
	}

	public void terminate() {
		if (null != posTracker) {
			posTracker.stopTrackingLocation();
		}
		posTracker = null;
	}

	
}
