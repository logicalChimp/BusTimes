package uk.co.mentalspace.android.bustimes;

import java.util.List;

import android.content.Context;

public interface Renderer {

	public static final int MESSAGE_DEBUG = 0;
	public static final int MESSAGE_NORMAL = 1;
	public static final int MESSAGE_ERROR = 2;
	
	public String getID();
	
	public Context getDisplayContext();
	
	public void displayMessage(Location location, String msg, int msgLevel);
	public void displayBusTimes(Location location, List<BusTime> busTimes);
	
	public void execute(Runnable r);
}
