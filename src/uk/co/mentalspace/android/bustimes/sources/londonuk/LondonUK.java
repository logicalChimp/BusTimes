package uk.co.mentalspace.android.bustimes.sources.londonuk;

import android.util.Log;
import uk.co.mentalspace.android.bustimes.DataRefreshTask;
import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.LocationRefreshTask;
import uk.co.mentalspace.android.bustimes.Renderer;
import uk.co.mentalspace.android.bustimes.Source;

public class LondonUK implements Source {

//	private static Map<String,Location> locations = null;
//	private static boolean locationsRequested = false;
	
	private static final String LOGNAME = "Source:LondonUK";
	
	@Override
	public String getName() {
		return "London, UK (TFL)";
	}

	@Override
	public String getID() {
		return "londonuk-tfl";
	}
	
//	public static void setLocations(Map<String,Location> locs) {
//		locations = locs;
//	}
//	
	public int getEstimatedLocationCount() {
		return 58000;
	}

	@Override
//	public Location getNearestStop(Renderer display, int lat, int lon) {
//		//validate supplied display, lat, lon
//		//  if not valid, throw illegal argument exception
//		
//		//fetch list of stops from preferences
//		//if not present in preferences, 
//		//  download list of stops from internet,
//		//  parse list into internet format (indexed by stop id)
//		//  and store in preferences
//		//if no internet connection, display error message and return null
//		
//		//iterate over list of stops, and perform basic geolocation comparison against supplied lat/lon
//		//create Location object from closest stop
//		//return Location object
//		return null;
//	}
//
//	@Override
//	public Location getSpecificStop(Renderer display, String locationID) {
//		Log.d(LOGNAME, "Fetching location for specific stop: "+locationID);
//		
//		if (null == locations || locations.isEmpty()) {
//			populateLocations(display);
//			return null;
//		} else {
//			Location location = locations.get(locationID);
//			return location;
//		}
//		//fetch list of stops from preferences
//		//if not present in preferences, 
//		//  download list of stops from internet,
//		//  parse list into internet format (indexed by stop id)
//		//  and store in preferences
//		//if no internet connection, display error message and return null
//
//		//parse locationID (if required) into suitable format
//		//fetch stop from list using locationID and convert to Location object
//		//return Location object
//		
//		//return null;
//		String stopId = Preferences.getPreference(display.getDisplayContext(), Preferences.KEY_PREFERRED_STOP_ID);
//		return new Location(stopId, "Dummy Stop", 530000, -1000);
//	}

	public LocationRefreshTask getLocationRefreshTask() {
		Log.d(LOGNAME, "Creating new Location Refresh Task");
		return new LondonUK_AsyncBusStops();
	}
	
//	public void loadLocations(Context ctx) {
//		Log.d(LOGNAME, "Creating Bus Stops asyncronous task");
////		LondonUK_AsyncBusStops async = new LondonUK_AsyncBusStops();
//		LocationRefreshTask async = getLocationRefreshTask();
//		async.init(ctx);
//		
//		Log.d(LOGNAME, "Executing asyncronous task");
//		async.execute();
//	}
//	
//	public void loadLocations(Context ctx, ProgressDisplay pd) {
//		Log.d(LOGNAME, "Creating Bus Stops asyncronous task (with Progress Bar)");
////		LondonUK_AsyncBusStops async = new LondonUK_AsyncBusStops();
//		LocationRefreshTask async = getLocationRefreshTask();
//		async.init(ctx, pd);
//		
//		Log.d(LOGNAME, "Executing asyncronous task");
//		async.execute();
//	}
//	
//	private void populateLocations(Renderer display) {
//		Log.d(LOGNAME, "Building locations list");
//		if (null == locations || locations.isEmpty()) {
//			if (locationsRequested) return;
//
//			Log.d(LOGNAME, "Creating Bus Stops asyncronous task");
//			LondonUK_AsyncBusStops async = new LondonUK_AsyncBusStops();
//			async.init(display.getDisplayContext());
//			
//			Log.d(LOGNAME, "Executing asyncronous task");
//			async.execute();
//
//			locationsRequested = true;
//		}
//	}
	
	@Override
	public DataRefreshTask getBusTimes(Renderer display, Location location) {
    	DataRefreshTask task = new RefreshBusTimes();
    	task.init(display, location);
    	return task;
	}
	
}
