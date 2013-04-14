package uk.co.mentalspace.android.bustimes;


import java.util.List;
import android.content.Context;
import uk.co.mentalspace.android.bustimes.db.LocationsDBAdapter;

public class LocationManager {

	public static boolean isLocationSelected(Context ctx, int stopId) {
        LocationsDBAdapter ldba = new LocationsDBAdapter(ctx);
        try {
	        ldba.open();
	        Location loc = ldba.getLocationByID(stopId);
	        if (null == loc) return false;
	        return (loc.getChosen() == 1);
        } finally {
        	if (null != ldba) ldba.close();
        }
	}
	
	public static void selectLocation(Context ctx, long stopId) {
        LocationsDBAdapter ldba = new LocationsDBAdapter(ctx);
        try {
	        ldba.open();
	        Location loc = ldba.getLocationByID(stopId);
	        if (null == loc) return;
	        loc.setChosen(1);
	        ldba.updateLocation(loc);
        } finally {
        	if (null != ldba) ldba.close();
        }
	}
	
	public static void deselectLocation(Context ctx, long stopId) {
        LocationsDBAdapter ldba = new LocationsDBAdapter(ctx);
        try {
	        ldba.open();
	        Location loc = ldba.getLocationByID(stopId);
	        if (null == loc) return;
	        loc.setChosen(0);
	        ldba.updateLocation(loc);
        } finally {
        	if (null != ldba) ldba.close();
        }
	}
	
	public static void updateNickName(Context ctx, long stopId, String nickName) {
        LocationsDBAdapter ldba = new LocationsDBAdapter(ctx);
        try {
	        ldba.open();
	        Location loc = ldba.getLocationByID(stopId);
	        if (null == loc) return;
	        loc.setNickName(nickName);
	        ldba.updateLocation(loc);
        } finally {
        	if (null != ldba) ldba.close();
        }
	}
	
	public static List<Location> getSelectedLocations(Context ctx) {
        LocationsDBAdapter ldba = new LocationsDBAdapter(ctx);
        try {
	        ldba.open();
	        List<Location> locs = ldba.getSelectedLocations();
	        return locs;
        } finally {
        	if (null != ldba) ldba.close();
        }
	}
	
	public static Location getNextLocation(Context ctx, Location loc) {
		List<Location> locs = getSelectedLocations(ctx);
		if (null != loc && locs.contains(loc)) {
			int nextPosition = locs.indexOf(loc)+1;
			if (nextPosition == locs.size()) nextPosition = 0;
			return locs.get(nextPosition);
		} else {
			return locs.get(0);
		}
	}
	
	public static List<Location> getLocationsInArea(Context ctx, int top, int right, int bottom, int left) {
        LocationsDBAdapter ldba = new LocationsDBAdapter(ctx);
        try {
	        ldba.open();
	        List<Location> locations = ldba.getLocationsInArea(top, right, bottom, left);
	        return locations;
        } finally {
        	if (null != ldba) ldba.close();
        }
	}
	
	public static Location getNearestSelectedLocation(Context ctx, int lat, int lon) {
        LocationsDBAdapter ldba = new LocationsDBAdapter(ctx);
        try {
	        ldba.open();
	        Location loc = ldba.getClosestSelectedLocation(lat, lon);
	        return loc;
        } finally {
        	if (null != ldba) ldba.close();
        }
	}
	
	public static Location getLocationById(Context ctx, int stopId) {
        LocationsDBAdapter ldba = new LocationsDBAdapter(ctx);
        try {
	        ldba.open();
	        Location loc = ldba.getLocationByID(stopId);
	        return loc;
        } finally {
        	if (null != ldba) ldba.close();
        }
	}
	
	public static Location getLocationByStopCode(Context ctx, String stopCode) {
        LocationsDBAdapter ldba = new LocationsDBAdapter(ctx);
        try {
	        ldba.open();
	        Location loc = ldba.getLocationByStopCode(stopCode);
	        return loc;
        } finally {
        	if (null != ldba) ldba.close();
        }
	}
	
}
