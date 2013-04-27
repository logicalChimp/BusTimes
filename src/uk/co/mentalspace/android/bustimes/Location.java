package uk.co.mentalspace.android.bustimes;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

public class Location implements Serializable {

	private static final long serialVersionUID = -6198852740620788874L;
	
	private final long id;
	private final String stopCode;
	private final String locationName;
	private final String desc;
	private final String srcPosA;
	private final String srcPosB;
	private final String heading;
	private final String nickName;
	private final int lat;
	private final int lon;
	private final boolean chosen;
	private final String sourceId;
	
	public Location(String stopCode, String locName, String desc, String srcPosA, String srcPosB, String heading, int lattitude, int longitude) {
		this(-1, stopCode, locName, desc, srcPosA, srcPosB, heading, lattitude, longitude);
	}
	
	public Location(long id, String stopCode, String locName, String desc, String srcPosA, String srcPosB, String heading, int lattitude, int longitude) {
		this(id, stopCode, locName, desc, srcPosA, srcPosB, heading, lattitude, longitude, "", false, "");
	}
	
	public Location(long id, String stopCode, String locName, String desc, String srcPosA, String srcPosB, String heading, int lattitude, int longitude, String nickName, boolean chosen, String sourceId) {
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
		sb.append("], [srcPosA:");
		sb.append(this.srcPosA);
		sb.append("], [srcPosB:");
		sb.append(this.srcPosB);
		sb.append("], [heading:");
		sb.append(this.heading);
		sb.append("], [Nick:");
		sb.append(this.nickName);
		sb.append("], [Chosen:");
		sb.append(this.chosen);
		sb.append("], [Source:");
		sb.append(this.sourceId);
		sb.append("]");
		return sb.toString();
	}
	
	public long getId() {
		return id;
	}
	
	public Location setChosen(boolean chosen) {
		return new Location(id, stopCode, locationName, desc, srcPosA, srcPosB, heading, lat, lon, nickName, chosen, sourceId);
	}
	
	public boolean getChosen() {
		return chosen;
	}
	
	public Location setNickName(String nickName) {
		return new Location(id, stopCode, locationName, desc, srcPosA, srcPosB, heading, lat, lon, nickName, chosen, sourceId);
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
	
	public LatLng getLatLng() {
		return new LatLng( ((double)lat)/10000, ((double)lon)/10000 );
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (chosen ? 1231 : 1237);
		result = prime * result + ((desc == null) ? 0 : desc.hashCode());
		result = prime * result + ((heading == null) ? 0 : heading.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + lat;
		result = prime * result + ((locationName == null) ? 0 : locationName.hashCode());
		result = prime * result + lon;
		result = prime * result + ((nickName == null) ? 0 : nickName.hashCode());
		result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
		result = prime * result + ((srcPosA == null) ? 0 : srcPosA.hashCode());
		result = prime * result + ((srcPosB == null) ? 0 : srcPosB.hashCode());
		result = prime * result + ((stopCode == null) ? 0 : stopCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Location)) return false;

		Location other = (Location) obj;
		
		//test simple params first
		if (id != other.id) return false;
		if (lat != other.lat) return false;		
		if (lon != other.lon) return false;
		if (chosen != other.chosen) return false;

		//then test the obj params, by recursing to their .equals methods
		if (!isParamEqual(desc, other.desc)) return false;
		if (!isParamEqual(heading, other.heading)) return false; 
		if (!isParamEqual(locationName, other.locationName)) return false;
		if (!isParamEqual(nickName, other.nickName)) return false; 
		if (!isParamEqual(sourceId, other.sourceId)) return false;
		if (!isParamEqual(srcPosA, other.srcPosA)) return false;
		if (!isParamEqual(srcPosB, other.srcPosB)) return false;
		if (!isParamEqual(stopCode, other.stopCode)) return false;

		return true;
	}
	
	private boolean isParamEqual(Object left, Object right) {
		if (left == null) {
			if (right != null) return false;
		} else if (!left.equals(right)) return false;
		return true;
	}
}
