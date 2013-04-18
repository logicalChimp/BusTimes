package uk.co.mentalspace.android.bustimes;

import java.io.Serializable;

public class Location implements Serializable {

	private static final long serialVersionUID = -6198852740620788874L;
	
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
	private int chosen = 0;
	private String sourceId = null;
	
	public Location(String stopCode, String locName, String desc, String srcPosA, String srcPosB, String heading, int lattitude, int longitude) {
		this(-1, stopCode, locName, desc, srcPosA, srcPosB, heading, lattitude, longitude);
	}
	
	public Location(long id, String stopCode, String locName, String desc, String srcPosA, String srcPosB, String heading, int lattitude, int longitude) {
		this(id, stopCode, locName, desc, srcPosA, srcPosB, heading, lattitude, longitude, "", 0, "");
	}
	
	public Location(long id, String stopCode, String locName, String desc, String srcPosA, String srcPosB, String heading, int lattitude, int longitude, String nickName, int chosen, String sourceId) {
		this.id = id;
		this.stopCode = stopCode;
		this.desc = desc;
		this.locationName = locName;
		this.srcPosA = srcPosA;
		this.srcPosB = srcPosB;
		this.heading = heading;
		lat = lattitude;
		lon = longitude;
		this.nickName = nickName;
		this.chosen = chosen;
		this.sourceId = sourceId;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[id:");
		sb.append(this.id);
		sb.append("], [stopCode:");
		sb.append(this.stopCode);
		sb.append("], [name:");
		sb.append(this.locationName);
		sb.append("], [desc:");
		sb.append(this.desc);
		sb.append("], [lat:");
		sb.append(this.lat);
		sb.append("], [lon:");
		sb.append(this.lon);
		sb.append("], [Nick:");
		sb.append(this.nickName);
		sb.append("], [Chosen:");
		sb.append(this.chosen);
		sb.append("], [Source:");
		sb.append(this.sourceId);
		sb.append("]");
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		if (null == o) return false;
		if (this == o) return true;
		if (!this.getClass().equals(o.getClass())) return false;
		
		Location other = (Location)o;
		if (this.getId() != -1) return this.getId() == other.getId();
		else {
			if (other.getId() != -1) return false;
			if (null == this.getStopCode()) return null == other.getStopCode();
			
			//use pattern 'boolean && <test>' = if boolean is false, test will be skipped
			//this avoids running tests once objects have been proved not equal 
			boolean isEqual = true;
			isEqual = isEqual && this.getStopCode().equals(other.getStopCode());
			isEqual = isEqual && this.getSourceId().equals(other.getSourceId());
			isEqual = isEqual && this.getNickName().equals(other.getNickName());
			isEqual = isEqual && this.getChosen() == other.getChosen();
			
			return isEqual;
		}
	}
	
	public long getId() {
		return id;
	}
	
	public void setChosen(int chosen) {
		this.chosen = chosen;
	}
	
	public int getChosen() {
		return chosen;
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
	
	public String getSourceId() {
		return sourceId;
	}
}
