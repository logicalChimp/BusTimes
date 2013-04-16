package uk.co.mentalspace.android.bustimes.sources.test;

import android.util.Log;
import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.LocationRefreshTask;
import uk.co.mentalspace.android.bustimes.db.LocationsDBAdapter;

public class BusStopsRefresh extends LocationRefreshTask {
	private static final String LOGNAME = "TestSource";
	private LocationsDBAdapter ldba = null;
	
	@Override
	protected String doInBackground(Void... strings) {
		if (null == ldba) {
			failure = new IllegalArgumentException("Database connection not initialised");
			return null;
		}

		try {
			publishProgress(PROGRESS_POSITION_PROCESSING_DATA, 0);
			ldba.open();
			
			String stopCode = "tsStopCode1";
			String stopName = "Test Source Stop";
			int lat = 513000;
			int lng = -1000;
			String srcPosA = "51.3";
			String srcPosB = "-0.1";
			String heading = "0";
			Location loc = ldba.getLocationByStopCode(stopCode);
			if (null == loc) {
				ldba.createLocation(stopCode, stopName, "", lat, lng, srcPosA, srcPosB, heading);
			} else {
				ldba.updateLocation(loc.getId(), stopCode, stopName, loc.getDescription(), loc.getLat(), loc.getLon(), srcPosA, srcPosB, heading, loc.getNickName(), loc.getChosen());
			}

			publishProgress(PROGRESS_POSITION_PROCESSING_DATA, 1);
			Log.d(LOGNAME, "Finished processing response.");
			
		} catch (Exception e) {
			Log.e(LOGNAME, "Unexception IOException occured: "+e);
			failure = e;
			return null;
		} finally {
			if (null != ldba) {
				try { ldba.close(); } catch (Exception e) { Log.e(LOGNAME, "Unknown exception", e); }
			}
		}
		
		finish();
		return null;
	}

	@Override
	public String getSourceId() {
		return "TestSource";
	}

}
