package uk.co.mentalspace.android.bustimes;

public class Location {

	private long id = -1;
	private String stopCode = null;
	private String locationName = null;
	private String desc = null;
	private String srcPosA = null;
	private String srcPosB = null;
	private String heading = null;
	private String nickName = null;
	private int lat = 0;
	private int lon = 0;
	
	public Location(String stopCode, String locName, String desc, String srcPosA, String srcPosB, String heading, int lattitude, int longitude) {
		this(-1, stopCode, locName, desc, srcPosA, srcPosB, heading, lattitude, longitude);
	}
	
	public Location(long id, String stopCode, String locName, String desc, String srcPosA, String srcPosB, String heading, int lattitude, int longitude) {
		this.id = id;
		this.stopCode = stopCode;
		this.desc = desc;
		this.locationName = locName;
		this.srcPosA = srcPosA;
		this.srcPosB = srcPosB;
		this.heading = heading;
		lat = lattitude;
		lon = longitude;
	}
	
	public long getId() {
		return id;
	}
	
	public void setNickName(String name) {
		nickName = name;
	}
	
	public String getNickName() {
		return nickName;
	}
	
	public boolean hasId() {
		return this.id != -1;
	}
	
	public String getStopCode() {
		return stopCode;
	}
	
	public String getLocationName() {
		return locationName;
	}
	
	public String getDescription() {
		return desc;
	}
	
	public String getSrcPosA() {
		return srcPosA;
	}
	
	public String getSrcPosB() {
		return srcPosB;
	}
	
	public String getHeading() {
		return heading;
	}
	
	public int getLat() {
		return lat;
	}
	
	public int getLon() {
		return lon;
	}
}
