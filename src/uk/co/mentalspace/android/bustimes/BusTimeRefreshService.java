package uk.co.mentalspace.android.bustimes;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import uk.co.mentalspace.android.bustimes.utils.BusTimeComparator;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class BusTimeRefreshService extends IntentService {
	private static final String LOGNAME = "BusTimeRefreshService";
	
	public static final String ACTION_REFRESH_BUS_TIMES = "uk.co.mentalspace.bustimes.REFRESH";
	public static final String ACTION_LATEST_BUS_TIMES = "uk.co.mentalspace.bustimes.LATEST_BUS_TIMES";
	
	public static final String EXTRA_SOURCE_ID = "sourceId";
	public static final String EXTRA_LOCATION_ID = "locationId";
	public static final String EXTRA_BUS_TIMES = "busTimes";
	
	public BusTimeRefreshService() {
		super("BusTimeRefreshService");
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	
	@Override
	protected void onHandleIntent(Intent arg0) {
		String action = arg0.getAction();
		Log.d(LOGNAME, "Handling action: "+action);
		
		if (ACTION_REFRESH_BUS_TIMES.equals(action)) {
			String sourceId = arg0.getStringExtra(EXTRA_SOURCE_ID);
			if (null == sourceId || "".equals(sourceId.trim())) {
				Log.e(LOGNAME, "Invalid source id for refresh bus times. aborting.");
				return;
			}
			
			Source src = SourceManager.getSource(sourceId);
			if (null == src) {
				Log.e(LOGNAME, "No matching source for source id ["+sourceId+"]. aborting.");
				return;
			}
			
			long locationId = arg0.getLongExtra(EXTRA_LOCATION_ID, -1);
			if (-1 == locationId) {
				Log.e(LOGNAME, "No Location ID specified. aborting.");
				return;
			}
			
			Location loc = LocationManager.getLocationById(this.getApplicationContext(), locationId);
			if (null == loc) {
				Log.e(LOGNAME, "No match location for location id ["+locationId+"]. aborting.");
				return;
			}
			
			BusTimeRefreshTask btrt = src.getBusTimesTask();			
			List<BusTime> busTimes = btrt.getBusTimes(loc);
			
			BusTimeComparator btc = new BusTimeComparator();
			Collections.sort(busTimes, btc);
			
			Intent intent = new Intent();
			intent.setAction(ACTION_LATEST_BUS_TIMES);
			intent.putExtra(EXTRA_SOURCE_ID, sourceId);
			intent.putExtra(EXTRA_LOCATION_ID, locationId);
			intent.putExtra(EXTRA_BUS_TIMES, (Serializable)busTimes);

			int busTimesSize = (null == busTimes) ? -1 : busTimes.size();
			Log.d(LOGNAME, "Sending ["+busTimesSize+"] bus times back.");
			this.sendBroadcast(intent);
		}
		
	}

}
