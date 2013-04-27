package uk.co.mentalspace.android.bustimes;

import java.io.Serializable;
import java.util.Arrays;

import android.util.Log;

public class Source implements Serializable {
	private static final long serialVersionUID = 5205293391053288165L;
	private static final String LOGNAME = "Source";

	final protected long rowId;
	final protected String srcId;
	final protected String srcName;
	final protected int estLocCount;
	final protected String locRefreshClassName;
	final protected String btRefreshClassName;
	final protected String polygonPointsJson;
	final protected boolean isInstalled;
	final protected String[] installFiles;
	
	final protected long lastRefreshTimestamp;
	
	protected transient LocationRefreshTask lrt = null;
	protected transient BusTimeRefreshTask btrt = null;
	
	public Source(String srcId, String srcName, int estLocCount, String locRefreshClassname, String btRefreshClassname, String polygonPointsJson, boolean isInstalled) {
		this(-1, srcId, srcName, estLocCount, locRefreshClassname, btRefreshClassname, polygonPointsJson, isInstalled, "", -1);
	}
	
	public Source(long rowId, String srcId, String srcName, int estLocCount, String locRefreshClassname, String btRefreshClassname, String polygonPointsJson, boolean isInstalled, String installFiles, long lastRefreshTimestamp) {
		this.rowId = rowId;
		this.srcId = srcId;
		this.srcName = srcName;
		this.estLocCount = estLocCount;
		this.locRefreshClassName = locRefreshClassname;
		this.btRefreshClassName = btRefreshClassname;
		this.polygonPointsJson = polygonPointsJson;
		this.isInstalled = isInstalled;
		this.lastRefreshTimestamp = lastRefreshTimestamp;

		if (null != installFiles) this.installFiles = installFiles.split(",");
		else this.installFiles = new String[0];
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
		sb.append("], isInstalled [");
		sb.append(isInstalled);
		sb.append("], installFiles [");
		sb.append(installFiles);
		sb.append("]");
		return sb.toString();
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
	
	public String[] getInstallFiles() {
		return installFiles;
	}
	
	public long getLastRefreshTimestamp() {
		return lastRefreshTimestamp;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((btRefreshClassName == null) ? 0 : btRefreshClassName.hashCode());
		result = prime * result + estLocCount;
		result = prime * result + Arrays.hashCode(installFiles);
		result = prime * result + (isInstalled ? 1231 : 1237);
		result = prime * result + ((locRefreshClassName == null) ? 0 : locRefreshClassName.hashCode());
		result = prime * result + ((polygonPointsJson == null) ? 0 : polygonPointsJson.hashCode());
		result = prime * result + (int) (rowId ^ (rowId >>> 32));
		result = prime * result + ((srcId == null) ? 0 : srcId.hashCode());
		result = prime * result + ((srcName == null) ? 0 : srcName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Source)) return false;
		
		Source other = (Source) obj;
		
		if (rowId != other.rowId) return false;
		if (estLocCount != other.estLocCount) return false;
		if (!Arrays.equals(installFiles, other.installFiles)) return false;
		if (isInstalled != other.isInstalled) return false;
		
		if (!isParamEqual(btRefreshClassName, other.btRefreshClassName)) return false;
		if (!isParamEqual(locRefreshClassName, other.locRefreshClassName)) return false;
		if (!isParamEqual(polygonPointsJson, other.polygonPointsJson)) return false;
		if (!isParamEqual(srcId, other.srcId)) return false; 
		if (!isParamEqual(srcName, other.srcName)) return false;
		return true;
	}
	
	private boolean isParamEqual(Object left, Object right) {
		if (left == null) {
			if (right != null) return false;
		} else if (!left.equals(right)) return false;
		return true;
	}
}
