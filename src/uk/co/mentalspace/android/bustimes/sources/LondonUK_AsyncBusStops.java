package uk.co.mentalspace.android.bustimes.sources;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import uk.co.mentalspace.android.bustimes.Coordinator;
import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.Renderer;
import android.os.AsyncTask;
import android.util.Log;

public class LondonUK_AsyncBusStops extends AsyncTask<Void, Void, Map<String,Location>> {
	private static final String BUS_LOCATIONS_URL = "http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=willinghamg%40hotmail.com&feedId=10";
	private static final String LOGNAME = "LondonUK_AsyncBusStops";

	private Renderer display = null;
	
	private Exception failure = null;
	
	public void init(Renderer renderer) {
		display = renderer;
	}
	
	public Exception getFailure() {
		return failure;
	}
	
	protected Map<String,Location> doInBackground(Void... strings) {
		if (null == display) {
			failure = new IllegalArgumentException("Renderer not initialised");
			return null;
		}

		String url = BUS_LOCATIONS_URL;
		Log.d(LOGNAME, "Data feed url: "+url);

		HashMap<String,Location> locations = new HashMap<String,Location>();
		BufferedReader br = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			Log.d(LOGNAME, "Requesting data from Server");
			HttpResponse response = client.execute(request);
			
			Log.d(LOGNAME, "Request executed - processing response");
			InputStreamReader isr = new InputStreamReader(response.getEntity().getContent());
			br = new BufferedReader(isr);
			
			String line = br.readLine();
			Log.d(LOGNAME, "First line: "+line);
			while (null != line && !("".equals(line))) {
				Log.v(LOGNAME, "Parsing stops line into Location");
				Location l = getLocation(line);
				if (null != l) locations.put(l.getId(), l);

				line = br.readLine();
			}

			Log.d(LOGNAME, "Finished processing response.");
			
		} catch (IOException ioe) {
			Log.e(LOGNAME, "Unexception IOException occured: "+ioe);
			return null;
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException ioe2) {
					Log.e(LOGNAME, "Failed to close input stream. cause: "+ioe2);
				}
			}
		}
		
		return locations;
	}

	private Location getLocation(String s) {
		try {
			String cols[] = s.split(",");
			if (cols.length < 6) return null;
			
			String id = cols[1];
			String name = cols[3];
			String latStr = cols[4];
			String lonStr = cols[5];
			int lat = Integer.parseInt(latStr);
			int lon = Integer.parseInt(lonStr);
	
			Location location = new Location(id, name, lat, lon);
			
			return location;
		} catch (NumberFormatException nfe) {
			Log.d(LOGNAME, "Failed to parse location", nfe);
			return null;
		}
	}

	protected void onProgressUpdate(Void... progress) {		
	}
	
	protected void onPostExecute(Map<String,Location> locations) {
		if (null != locations) {
			LondonUK.setLocations(locations);
			Coordinator.execute(display);
		}
	}
}
