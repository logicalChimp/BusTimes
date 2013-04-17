package uk.co.mentalspace.android.bustimes;

public interface Source {

	public String getName();
	public String getID();
	
//	public Location getNearestStop(Renderer display, int lat, int lon);
//	public Location getSpecificStop(Renderer display, String locationID);

	public LocationRefreshTask getLocationRefreshTask();
//	public void loadLocations(Context ctx);
//	public void loadLocations(Context ctx, ProgressDisplay pd);
	public int getEstimatedLocationCount();
	
	public BusTimeRefreshTask getBusTimesTask();
//	public BusTimeRefreshTask getBusTimesTask(Renderer display, Location location);
//	public void getBusTimes(Renderer display, Location location);
//	public void getBusTimesAsync(Renderer display, Location location);

	//function-handle for async task to callback to
//	public ArrayList<BusTime> getBusTimeData(Renderer display, Location location);
}
