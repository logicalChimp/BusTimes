package uk.co.mentalspace.android.bustimes.sources.test;

import uk.co.mentalspace.android.bustimes.Location;
import android.util.Log;
import uk.co.mentalspace.android.bustimes.LocationRefreshTask;

public class BusStopsRefresh extends LocationRefreshTask {
	private static final String LOGNAME = "TestSource";
	
	@Override
	public void performRefresh() {
		publishProgress("Generating Test Data", 0);
		
		String stopCode = "tsStopCode1";
		String stopName = "Test Source Stop";
		int lat = 513000;
		int lng = -1000;
		String srcPosA = "51.3";
		String srcPosB = "-0.1";
		String heading = "0";
		Location loc = ldba.getLocationByStopCode(stopCode);
		if (null == loc) {
			ldba.createLocation(stopCode, stopName, "", lat, lng, srcPosA, srcPosB, heading, getSourceId());
		} else {
			ldba.updateLocation(loc.getId(), stopCode, stopName, loc.getDescription(), loc.getLat(), loc.getLon(), srcPosA, srcPosB, heading, loc.getNickName(), loc.getChosen(), loc.getSourceId());
		}

		publishProgress("Test Data Generated", 1);
		Log.d(LOGNAME, "Finished processing response.");
		
		
		finish();
	}

	@Override
	public String getSourceId() {
		return "TestSource";
	}

	@Override
	public int getMaxProgress() {
		return 1;
	}

	@Override
	public String getSourceName() {
		return "Test Source";
	}

}
