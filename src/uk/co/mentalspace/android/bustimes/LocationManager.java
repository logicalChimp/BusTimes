package uk.co.mentalspace.android.bustimes;

import android.content.Context;

import java.util.List;
import android.util.Log;
import uk.co.mentalspace.android.bustimes.db.BaseDBAdapter;
import uk.co.mentalspace.android.bustimes.db.LocationsDBAdapter;
import uk.co.mentalspace.android.bustimes.db.LocationsRefreshDBAdapter;

public class LocationManager extends BaseManager<Location> {
	private static final String LOGNAME = "LocationManager2";

	protected static class LocTask<E> extends Task<E> {
		protected LocationsDBAdapter ldba = null;
		protected BaseDBAdapter<?> getDBAdapter(Context ctx) {
	        if (null == ldba) ldba = new LocationsDBAdapter(ctx);
	        return ldba;
		}
	}
	
	public static boolean isLocationSelected(Context ctx, final int stopId) {
		Log.d(LOGNAME, "Testing location selection status. loc id: "+stopId);
		
		Task<Boolean> locTask = new LocTask<Boolean>() {
			protected Boolean doWork() {
		        Location loc = ldba.getLocationByID(stopId);
		        if (null == loc) return false;
		        return (loc.getChosen() == 1);
			}
		};
		return locTask.run(ctx);
	}
	
	public static void selectLocation(Context ctx, final long stopId) {
		Log.d(LOGNAME, "Selecting location id: "+stopId);

		Task<Boolean> locTask = new LocTask<Boolean>() {
			protected Boolean doWork() {
		        Location loc = ldba.getLocationByID(stopId);
		        if (null == loc) return false;
		        loc.setChosen(1);
		        return ldba.updateLocation(loc);
			}
		};
		locTask.run(ctx);
	}
	
	public static void deselectLocation(Context ctx, final long stopId) {
		Log.d(LOGNAME, "Deselecting stop: "+stopId);

		Task<Boolean> locTask = new LocTask<Boolean>() {
			protected Boolean doWork() {
		        Location loc = ldba.getLocationByID(stopId);
		        if (null == loc) return false;
		        loc.setChosen(0);
		        return ldba.updateLocation(loc);
			}
		};
		locTask.run(ctx);
	}
	
	public static void updateNickName(Context ctx, final long stopId, final String nickName) {
		Log.d(LOGNAME, "Updating Nick Name");

		Task<Boolean> locTask = new LocTask<Boolean>() {
			protected Boolean doWork() {
		        Location loc = ldba.getLocationByID(stopId);
		        if (null == loc) return false;
		        loc.setNickName(nickName);
		        return ldba.updateLocation(loc);
			}
		};
		locTask.run(ctx);
	}
	
	public static List<Location> getSelectedLocations(Context ctx) {
		Log.d(LOGNAME, "Getting selected locations");
		Task<List<Location>> locTask = new LocTask<List<Location>>() {
			protected List<Location> doWork() {
		        return ldba.getSelectedLocations();
			}
		};
		return locTask.run(ctx);
	}
	
	public static Location getNextLocation(Context ctx, Location loc) {
		Log.d(LOGNAME, "Getting next location after loc: "+loc);
		List<Location> locs = getSelectedLocations(ctx);
		if (null != loc) {
			if (locs.contains(loc)) {
				int nextPosition = locs.indexOf(loc)+1;
				if (nextPosition == locs.size()) nextPosition = 0;
				if (-1 == nextPosition) nextPosition = 0;
				return locs.get(nextPosition);
			} else {
				return locs.get(0);
			}
		} else {
			return null;
		}
	}
	
	public static List<Location> getLocationsInArea(Context ctx, final int top, final int right, final int bottom, final int left) {
		Log.d(LOGNAME, "Getting locations in area t ["+top+"], r ["+right+"], b ["+bottom+"], l ["+left+"]");
		Task<List<Location>> locTask = new LocTask<List<Location>>() {
			protected List<Location> doWork() {
		        return ldba.getLocationsInArea(top, right, bottom, left);
			}
		};
		return locTask.run(ctx);
	}
	
	public static Location getNearestSelectedLocation(Context ctx, final int lat, final int lon) {
		Log.d(LOGNAME, "Getting nearest selected location to lat ["+lat+"], lon ["+lon+"]");
		Task<Location> locTask = new LocTask<Location>() {
			protected Location doWork() {
		        return ldba.getClosestSelectedLocation(lat, lon);
			}
		};
		return locTask.run(ctx);
	}
	
	public static Location getLocationById(Context ctx, final long stopId) {
		Log.d(LOGNAME, "Getting location by Id: "+stopId);
		Task<Location> locTask = new LocTask<Location>() {
			protected Location doWork() {
		        return ldba.getLocationByID(stopId);
			}
		};
		return locTask.run(ctx);
	}
	
	public static Location getLocationByStopCode(Context ctx, final String stopCode) {
		Log.d(LOGNAME, "Getting location by stop code: "+stopCode);
		Task<Location> locTask = new LocTask<Location>() {
			protected Location doWork() {
		        return ldba.getLocationByStopCode(stopCode);
			}
		};
		return locTask.run(ctx);
	}
	
	public static boolean createRefreshRecord(Context ctx, final String sourceId, final long startTime, final long endTime) {
		Log.d(LOGNAME, "Creating locations refresh record for source: "+sourceId);
		Task<Boolean> locTask = new LocTask<Boolean>() {
			protected LocationsRefreshDBAdapter lrdba = null;
			protected BaseDBAdapter<?> getDBAdapter(Context ctx) {
		        if (null == lrdba) lrdba = new LocationsRefreshDBAdapter(ctx);
		        return lrdba;
			}
			protected Boolean doWork() {
		        long rowId = lrdba.createBTRefreshRecord(sourceId, startTime, endTime);
		        return rowId != -1;
			}
		};
		return locTask.run(ctx);
	}
	
}
