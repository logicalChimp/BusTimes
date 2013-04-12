package uk.co.mentalspace.android.bustimes;

import java.util.List;
import java.util.Timer;
import android.content.Context;
import android.util.Log;

public class Coordinator {

	public static Timer timer = null;
	public static final long REFRESH_RATE = 30*1000L; //30 seconds

	public static void terminate() {
		if (null != timer) {
			timer.cancel();
			timer = null;
		}
	}
	
	public static Source getChosenSource(Context ctx) {
		Log.d("Coordinator", "Fetching current Source");
		String sourceId = Preferences.getPreference(ctx, Preferences.KEY_SOURCE_ID);
		if (null == sourceId || "".equals(sourceId.trim())) {			
			Log.d("Coordinator", "No source specified");
			return null;
		}
        
		Log.d("Coordinator", "Fetching Source object for source id");
        Source src = SourceManager.getSource(sourceId);
        return src;
	}

	public static void execute(Renderer display) {
		
		Log.d("Coordinator", "Retrieving display context");
		Context ctx = display.getDisplayContext();
		
		Log.d("Coordinator", "Fetching bus times source id from preferences");
		//get id of preferred source of bus times from preferences
		String sourceId = Preferences.getPreference(ctx, Preferences.KEY_SOURCE_ID);

		Log.d("Coordinator", "Validating bus timed source id");
        //if not set, display message asking user to select bus time source, and exit function
		if (null == sourceId || "".equals(sourceId.trim())) {			
			Log.e("Coordinator", "Bus times source id null or empty - aborting");
			String msg = ctx.getResources().getString(R.string.msg_no_source_selected);
			display.displayMessage(msg, Renderer.MESSAGE_ERROR);
			return;
		}
        
		Log.d("Coordinator", "Fetching Source object for source id");
        //get specified source from source manager by id
        Source src = SourceManager.getSource(sourceId);
        if (null == src) {
    		Log.e("Coordinator", "Source object not found - aborting");
        	String msg = ctx.getResources().getString(R.string.msg_invalid_source_selected);
			display.displayMessage(msg, Renderer.MESSAGE_ERROR);
			return;
        }
        
        String locatorId = Preferences.getPreference(ctx, Preferences.KEY_LOCATOR_ID);
        if (null == locatorId || "".equals(locatorId.trim())) {
        	String msg = ctx.getResources().getString(R.string.msg_no_locator_selected);
        	display.displayMessage(msg, Renderer.MESSAGE_ERROR);
        	return;
        }
        
        Locator locator = LocatorManager.getLocator(locatorId);
        if (null == locator) {
        	String msg = ctx.getResources().getString(R.string.msg_invalid_locator_selected);
        	display.displayMessage(msg, Renderer.MESSAGE_ERROR);
        	return;
        }
        
        Location loc = locator.getLocation(display);
        //check if GPS is enabled
        //  if it is, attempt to get lat/lon from GPS
        //  fetch location from source by lat/lon
        //  Location loc = src.getNearestStop(this, lat, lon);
        
//		Log.d("Coordinator", "Trying to retrieve preferred stop id from preferences");
//        //else, check if preferred stop is set in preferences
//        String preferredStopId = Preferences.getPreference(ctx, Preferences.KEY_PREFERRED_STOP_ID);
//        if (null != preferredStopId) {
//    		Log.d("Coordinator", "Preferred stop id not null - converting to location");
//        	loc = src.getSpecificStop(display, preferredStopId);
//        }
        
		Log.d("Coordinator", "Checking for valid location object");
        //else, display message to user asking them to either enable GPS, or enter a preferred stop ID in the preferences, and exit function
        if (null == loc) {
    		Log.e("Coordinator", "No valid location object retrieved by locator");
        	String msg = ctx.getResources().getString(R.string.msg_unable_to_select_stop);
			display.displayMessage(msg, Renderer.MESSAGE_ERROR);
			return;
        }
        
		Log.d("Coordinator", "Initiating request of list of bus times for location");
		display.displayMessage("fetching bus times...", Renderer.MESSAGE_NORMAL);
    	DataRefreshTask task = src.getBusTimes(display, loc);

    	if (null == timer) timer = new Timer();
    	timer.scheduleAtFixedRate(task, 0, REFRESH_RATE);

//    	try {
//			src.getBusTimesAsync(display, loc);
//		} catch (Exception e) {
//			Log.e("Coordinator", "Unknown failure ["+e+"] to retrieve bus times - aborting");
//			display.displayMessage("Unknown error whilst getting bus times.  Error: "+e, Renderer.MESSAGE_ERROR);
//			return;
//		}
	}
	
	public static void updateBusTimes(Renderer display, Location location, List<BusTime> busTimes) {
		Context ctx = display.getDisplayContext();
		
        //if the list is null, report error to user
        if (null == busTimes) {
    		Log.e("Coordinator", "Unknown failure to retrieve bus times - aborting"); 
        	String msg = ctx.getResources().getString(R.string.msg_failed_to_get_bus_times);
			display.displayMessage(msg, Renderer.MESSAGE_ERROR);
			return;
        } 
        
        //if no bus times available, add 'fake' entry stating no times available.
        else if (busTimes.size() == 0) {
    		Log.w("Coordinator", "No bus times reported - displaying warning message");
        	BusTime dummy = new BusTime("", "No Bus Times Reported", "");
        	busTimes.add(dummy);
        } 
        
        //check number of busTimes entries - truncate to 10 if required
        else if (busTimes.size() > 10) {
    		Log.i("Coordinator", "Too many bus times reported - truncating to first 10"); 
        	busTimes = busTimes.subList(0, 10);
        }
        
        //set timer to refresh list of bus times in 30 seconds

		Log.d("Coordinator", "Displaying bus times");
        //pass list of bus times to display method
        display.displayBusTimes(location, busTimes);
	}
	
}
