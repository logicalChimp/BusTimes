package uk.co.mentalspace.android.bustimes.sources.londonuk;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.LocationRefreshTask;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;
import android.util.Log;

public class LondonUK_AsyncBusStops extends LocationRefreshTask {
	private static final String BUS_LOCATIONS_URL = "http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=willinghamg%40hotmail.com&feedId=10";
	private static final String LOGNAME = "LondonUK_AsyncBusStops";

	@Override
	public String getSourceId() {
		//TODO remove hard coding, and read from master 'LondonUK' value instead
		return "londonuk-tfl";
	}
	
	protected String doInBackground(Void... strings) {
		if (null == ldba) {
			failure = new IllegalArgumentException("Database connection not initialised");
			return null;
		}

		String url = BUS_LOCATIONS_URL;
		Log.d(LOGNAME, "Data feed url: "+url);

		BufferedReader br = null;
		try {
			publishProgress(PROGRESS_POSITION_CONTACTING_SERVER, 0);
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			Log.d(LOGNAME, "Requesting data from Server");
			HttpResponse response = client.execute(request);
			
			publishProgress(PROGRESS_POSITION_DOWNLOADING_DATA, 0);
			Log.d(LOGNAME, "Request executed - processing response");
			InputStreamReader isr = new InputStreamReader(response.getEntity().getContent());
			br = new BufferedReader(isr);
			
			String line = br.readLine();
			Log.d(LOGNAME, "First line: "+line);
			
			//skip the first line, as it is headers
			line = br.readLine();

			//keep a counter for the progress bar
			int count = 0;
			
			//open the DB connection now, instead of inside the loop
			publishProgress(PROGRESS_POSITION_PROCESSING_DATA, count);
			ldba.open();
			Map<String,String> keys = ldba.getComboKeys(getSourceId());
			while (null != line && !("".equals(line))) {
				processLocation(keys, line);

				line = br.readLine();
				count++;
				if (count%100 == 0) {
					publishProgress(PROGRESS_POSITION_PROCESSING_DATA, count);
				}
			}

			Log.d(LOGNAME, "Finished processing response.");
			
		} catch (IOException ioe) {
			Log.e(LOGNAME, "Unexception IOException occured: "+ioe);
			failure = ioe;
			return null;
		} finally {
			if (null != br) {
				try { br.close(); } catch (IOException ioe2) { Log.e(LOGNAME, "Failed to close input stream. cause: "+ioe2); }
			}
			if (null != ldba) {
				try { ldba.close(); } catch (Exception e) { Log.e(LOGNAME, "Unknown exception", e); }
			}
		}
		
		finish();
		return null;
	}

	private void processLocation(Map<String,String> keys, String s) {
		String cols[] = s.split(",");
		if (cols.length < 6) return;

		String comboKey = ldba.getComboKey(cols[1], cols[4], cols[5]);
		String stopCode = keys.get(comboKey);

		if (null == stopCode) {
			if (keys.entrySet().contains(cols[1])) {
				//stop code exists but srcPosA or srcPosB don't match - has moved location - delete old one and re-create
				ldba.deleteLocationByStopCode(stopCode);
			}
			createNewLocation(cols);
		}
		else {
			Location loc = ldba.getLocationByStopCode(stopCode);
			ldba.updateLocation(loc.getId(), cols[1], cols[3], loc.getDescription(), loc.getLat(), loc.getLon(), cols[4], cols[5], cols[6], loc.getNickName(), loc.getChosen(), this.getSourceId());
		}
	}
	
	private void createNewLocation(String cols[]) {		
		LatLng latlng = null;
		try {
			double spa = Double.parseDouble(cols[4]);
			double spb = Double.parseDouble(cols[5]);
			latlng = new OSRef(spa,spb).toLatLng();
			latlng.toWGS84();
		} catch (NumberFormatException nfe) {
			Log.e(LOGNAME, "Failed to parse northing,easting values ["+cols[4]+","+cols[5]+"].  Other values ["+cols[0]+","+cols[1]+","+cols[3]+","+cols[6]+"]");
			return;
		}
		int lat = (int)(latlng.getLat()*10000);
		int lng = (int)(latlng.getLng()*10000);
		ldba.createLocation(cols[1], cols[3], "", lat, lng, cols[4], cols[5], cols[6], this.getSourceId());
	}
}
