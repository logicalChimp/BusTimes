package uk.co.mentalspace.android.bustimes.sources;

import uk.co.mentalspace.android.bustimes.DataRefreshTask;
import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.Renderer;
import uk.co.mentalspace.android.bustimes.Source;

public class LondonUK implements Source {

//	private static final String LOGNAME = "Source:LondonUK";
//	private static final String BUS_LOCATIONS_URL = "http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=willinghamg@hotmail.com&Id=10";
	
	@Override
	public String getName() {
		return "London, UK (TFL)";
	}

	@Override
	public String getID() {
		return "londonuk-tfl";
	}

	@Override
	public Location getNearestStop(Renderer display, int lat, int lon) {
		//validate supplied display, lat, lon
		//  if not valid, throw illegal argument exception
		
		//fetch list of stops from preferences
		//if not present in preferences, 
		//  download list of stops from internet,
		//  parse list into internet format (indexed by stop id)
		//  and store in preferences
		//if no internet connection, display error message and return null
		
		//iterate over list of stops, and perform basic geolocation comparison against supplied lat/lon
		//create Location object from closest stop
		//return Location object
		return null;
	}

	@Override
	public Location getSpecificStop(Renderer display, String locationID) {
		//validate supplied display, lat, lon
		//  if not valid, throw illegal argument exception
		
		//fetch list of stops from preferences
		//if not present in preferences, 
		//  download list of stops from internet,
		//  parse list into internet format (indexed by stop id)
		//  and store in preferences
		//if no internet connection, display error message and return null

		//parse locationID (if required) into suitable format
		//fetch stop from list using locationID and convert to Location object
		//return Location object
		
		//return null;
		String stopId = Preferences.getPreference(display.getDisplayContext(), Preferences.KEY_PREFERRED_STOP_ID);
		return new Location(stopId, "Dummy Stop", 530000, -1000);
	}

	@Override
	public DataRefreshTask getBusTimes(Renderer display, Location location) {
    	DataRefreshTask task = new RefreshBusTimes();
    	task.init(display, location);
    	return task;
	}
	
}
