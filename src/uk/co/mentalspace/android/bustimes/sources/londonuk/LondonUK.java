package uk.co.mentalspace.android.bustimes.sources.londonuk;

import android.util.Log;
import uk.co.mentalspace.android.bustimes.BusTimeRefreshTask;
import uk.co.mentalspace.android.bustimes.LocationRefreshTask;
import uk.co.mentalspace.android.bustimes.Source;

public class LondonUK extends Source {
	private static final String LOGNAME = "Source:LondonUK";

	public LondonUK() {
		super("londonuk-tfl", "London, UK (TFL)", 19600, "uk.co.mentalspace.android.bustimes.sources.londonuk.LondonUK_AsyncBusStops", "uk.co.mentalspace.android.bustimes.sources.londonuk.LondonUK_AsyncBusTimes", "");
	}

	
	public LocationRefreshTask getLocationRefreshTask() {
		Log.d(LOGNAME, "Creating new Location Refresh Task");
		return new LondonUK_AsyncBusStops();
	}
	
	@Override
	public BusTimeRefreshTask getBusTimesTask() {
		Log.d(LOGNAME, "Creating new Bus Time Refresh Task");
    	BusTimeRefreshTask task = new LondonUK_AsyncBusTimes();
    	return task;
	}

}
