package uk.co.mentalspace.android.bustimes;

import java.io.Serializable;

import android.util.Log;

public class Source implements Serializable {
	private static final long serialVersionUID = 5205293391053288165L;
	private static final String LOGNAME = "Source";

	private long rowId = -1;
	private String srcId = null;
	private String srcName = null;
	private int estLocCount = 0;
	private String locRefreshClassName = null;
	private String btRefreshClassName = null;
	private String polygonPointsJson = null;
	private boolean isInstalled = false;
	
	private transient LocationRefreshTask lrt = null;
	private transient BusTimeRefreshTask btrt = null;
	
	public Source(String srcId, String srcName, int estLocCount, String locRefreshClassname, String btRefreshClassname, String polygonPointsJson, boolean isInstalled) {
		this(-1, srcId, srcName, estLocCount, locRefreshClassname, btRefreshClassname, polygonPointsJson, isInstalled);
	}
	
	public Source(long rowId, String srcId, String srcName, int estLocCount, String locRefreshClassname, String btRefreshClassname, String polygonPointsJson, boolean isInstalled) {
		this.rowId = rowId;
		this.srcId = srcId;
		this.srcName = srcName;
		this.estLocCount = estLocCount;
		this.locRefreshClassName = locRefreshClassname;
		this.btRefreshClassName = btRefreshClassname;
		this.polygonPointsJson = polygonPointsJson;
		this.isInstalled = isInstalled;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("RowID [");
		sb.append(rowId);
		sb.append("], srcId [");
		sb.append(srcId);
		sb.append("], srcName [");
		sb.append(srcName);
		sb.append("], estLocCount [");
		sb.append(estLocCount);
		sb.append("], locRefreshClassname [");
		sb.append(locRefreshClassName);
		sb.append("], btRefreshClassname [");
		sb.append(btRefreshClassName);
		sb.append("], polygonPointsJson [");
		sb.append(polygonPointsJson);
		sb.append("]");
		return sb.toString();
	}
	
	public boolean isEqual(Object o) {
		if (null == o) return false;
		if (this == o) return true;
		if (!this.getClass().equals(o.getClass())) return false;
		
		Source other = (Source)o;
		if (this.getRowId() != -1) return this.getRowId() == other.getRowId();
		else {
			if (other.getRowId() != -1) return false;
			return this.getID().equals(other.getID());
		}
	}
	
	public long getRowId() {
		return rowId;
	}
	
	public String getName() {
		return srcName;
	}
	
	public String getID() {
		return srcId;
	}
	
	public int getEstimatedLocationCount() {
		return estLocCount;
	}
	
	public String getLocationRefreshClassName() {
		return locRefreshClassName;
	}
	
	public String getBTRefreshClassName() {
		return btRefreshClassName;
	}
	
	public String getPolygonPointsJson() {
		return polygonPointsJson;
	}
	
	public boolean isInstalled() {
		return isInstalled;
	}
	
	public LocationRefreshTask getLocationRefreshTask() {
		if (null != lrt) return lrt;
		if (null == locRefreshClassName) return null;

		try {
			Class<?> cls = Class.forName(locRefreshClassName);
			Object o = cls.newInstance();
			if (o instanceof LocationRefreshTask) {
				lrt = (LocationRefreshTask)o;
				return lrt;
			}
		} catch (ClassNotFoundException cnfe) {
			if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "Failed to load location refresh task ["+locRefreshClassName+"]", cnfe);
		} catch (IllegalAccessException iae) {
			if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "Failed to load location refresh task ["+locRefreshClassName+"]", iae);
		} catch (InstantiationException ie) {
			if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "Failed to load location refresh task ["+locRefreshClassName+"]", ie);
		}
		return null;
	}
	
	public BusTimeRefreshTask getBusTimesTask() {
		if (null != btrt) return btrt;
		if (null == btRefreshClassName) return null;

		try {
			Class<?> cls = Class.forName(btRefreshClassName);
			Object o = cls.newInstance();
			if (o instanceof BusTimeRefreshTask) {
				btrt = (BusTimeRefreshTask)o;
				return btrt;
			}
		} catch (ClassNotFoundException cnfe) {
			if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "Failed to load Bus Time refresh task ["+btRefreshClassName+"]", cnfe);
		} catch (IllegalAccessException iae) {
			if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "Failed to load Bus Time refresh task ["+btRefreshClassName+"]", iae);
		} catch (InstantiationException ie) {
			if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "Failed to load Bus Time refresh task ["+btRefreshClassName+"]", ie);
		}
		return null;
	}
}
