package uk.co.mentalspace.android.bustimes;

public class Location {

	private String id = null;
	private String locationName = null;
	private int lat = 0;
	private int lon = 0;
	
	public Location(String stop_id, String locName, int lattitude, int longitude) {
		id = stop_id;
		lat = lattitude;
		locationName = locName;
		lon = longitude;
	}
	
	public String getId() {
		return id;
	}
	
	public String getLocationName() {
		return locationName;
	}
	
	public int getLat() {
		return lat;
	}
	
	public int getLon() {
		return lon;
	}
}
