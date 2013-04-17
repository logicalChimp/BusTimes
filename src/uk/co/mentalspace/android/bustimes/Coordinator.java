package uk.co.mentalspace.android.bustimes;

import java.util.Collections;
import java.util.List;
import java.util.Timer;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class Coordinator {
	private static final String LOGNAME = "Coordinator";
	
	private static final BusTimeComparator btComparator = new BusTimeComparator();
	
	public static Timer timer = null;
	public static final long REFRESH_RATE = 30*1000L; //30 seconds

	public static void terminate() {
		if (null != timer) {
			timer.cancel();
			timer = null;
		}
	}
	
	public static Location getNextLocation(Renderer display, Location loc) {
		return LocationManager.getNextLocation(display.getDisplayContext(), loc);
	}
	
	public static Location getNearestLocation(Renderer display, int lat, int lon) {
		return LocationManager.getNearestSelectedLocation(display.getDisplayContext(), lat, lon);
	}

	public static void getBusTimes(Renderer display, Location loc) {
		getBusTimes(display, loc, true);
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static void getBusTimes(Renderer display, Location loc, boolean async) {
		Log.d(LOGNAME, "Getting bus times.  Async? "+async);

		if (null == loc) {
			Log.e(LOGNAME, "Request to get Bus Times for null location!");
			display.displayMessage("Please select at least one location",  Renderer.MESSAGE_ERROR);
			return;
		}
		
		Source src = getLocationSource(loc);
		if (null == src) {
			Log.w(LOGNAME, "Request to get Bus Times without selecting a data source");
			display.displayMessage("Please select a data source", Renderer.MESSAGE_ERROR);
			return;
		}
		
		Log.d(LOGNAME, "Initiating request of bus times for location: "+loc);
		display.displayMessage("fetching bus times...", Renderer.MESSAGE_NORMAL);
    	DataRefreshTask task = src.getBusTimesTask(display, loc);

    	if (async) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			else task.execute();
    	}
    	else task.executeSync();
	}
	
	public static Source getLocationSource(Location loc) {		
		Log.d(LOGNAME, "Fetching Source for location");
		String sourceId = loc.getSourceId();
		if (null == sourceId || "".equals(sourceId.trim())) {			
			Log.d("Coordinator", "No source specified for location - aborting");
			return null;
		}
        
		Log.d("Coordinator", "Fetching Source object for source id ["+sourceId+"]");
        Source src = SourceManager.getSource(sourceId);
        return src;
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
        	BusTime dummy = new BusTime("", "No Buses", "");
        	busTimes.add(dummy);
        } 
        
        //check number of busTimes entries - truncate to 10 if required
        else if (busTimes.size() > 10) {
    		Log.i("Coordinator", "Too many bus times reported - truncating to first 10"); 
        	busTimes = busTimes.subList(0, 10);
        }
        
        Collections.sort(busTimes, btComparator);
        
        //set timer to refresh list of bus times in 30 seconds

		Log.d("Coordinator", "Displaying bus times");
        //pass list of bus times to display method
        display.displayBusTimes(location, busTimes);
	}
	
}
