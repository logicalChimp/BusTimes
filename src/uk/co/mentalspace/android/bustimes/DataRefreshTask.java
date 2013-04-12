package uk.co.mentalspace.android.bustimes;

import java.util.TimerTask;

public abstract class DataRefreshTask extends TimerTask {

	protected Renderer display = null;
	protected Location location = null;
	
//	public abstract boolean hasData(); 
	
	public void init(Renderer taskDisplay, Location taskLocation) {
		display = taskDisplay;
		location = taskLocation;
	}

}
