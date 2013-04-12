package uk.co.mentalspace.android.bustimes.locators;

import android.content.Context;
import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.Locator;
import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.Renderer;
import uk.co.mentalspace.android.bustimes.db.LocationsDBAdapter;

public class PreferredLocationLocator implements Locator {

	public String getId() {
		return "PreferredLocationLocator";
	}
	
	public Location getLocation(Renderer display) {
		Context ctx = display.getDisplayContext();
        String preferredStopId = Preferences.getPreference(ctx, Preferences.KEY_PREFERRED_STOP_ID);
        if (null == preferredStopId || "".equals(preferredStopId.trim())) return null;

        Location loc = getLocation(ctx, preferredStopId);
        if (null != loc) return loc;
        
        //just in case user enters new stop code that is not yet in the locations feed
        return new Location(preferredStopId, "", "", "", "", "", 0, 0);
	}
	
	public Location getLocation(Context ctx, String stopCode) {
        if (null == stopCode || "".equals(stopCode.trim())) return null;
        
        //TODO tidy up - nasty nsaty nasty!
        LocationsDBAdapter ldba = new LocationsDBAdapter(ctx);
        Location loc = null;
        try {
	        ldba.open();
	        loc = ldba.getLocationByStopCode(stopCode);
        } finally {
        	if (null != ldba) ldba.close();
        }

        return loc;
	}

}
