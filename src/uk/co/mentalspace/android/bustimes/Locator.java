package uk.co.mentalspace.android.bustimes;

import android.content.Context;

public interface Locator {

	public String getId();
	public Location getLocation(Renderer display);
	public Location getLocation(Context ctx, String stopCode);
}
