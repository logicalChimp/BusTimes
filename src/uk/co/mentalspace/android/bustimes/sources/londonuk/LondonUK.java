package uk.co.mentalspace.android.bustimes.sources.londonuk;

import android.util.Log;
import uk.co.mentalspace.android.bustimes.BusTimeRefreshTask;
import uk.co.mentalspace.android.bustimes.LocationRefreshTask;
import uk.co.mentalspace.android.bustimes.Source;

public class LondonUK implements Source {

	private static final String LOGNAME = "Source:LondonUK";
	
	@Override
	public String getName() {
		return "London, UK (TFL)";
	}

	@Override
	public String getID() {
		return "londonuk-tfl";
	}
	
	public int getEstimatedLocationCount() {
		return 20000;
	}

	public LocationRefreshTask getLocationRefreshTask() {
		Log.d(LOGNAME, "Creating new Location Refresh Task");
		return new LondonUK_AsyncBusStops();
	}
	
	@Override
	public BusTimeRefreshTask getBusTimesTask() {
    	BusTimeRefreshTask task = new LondonUK_AsyncBusTimes();
    	return task;
	}

}
