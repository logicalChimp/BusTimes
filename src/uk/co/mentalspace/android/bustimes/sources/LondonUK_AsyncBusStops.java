package uk.co.mentalspace.android.bustimes.sources;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.ProgressDisplay;
import uk.co.mentalspace.android.bustimes.db.LocationsDBAdapter;
import uk.me.jstott.jcoord.LatLng;
import uk.me.jstott.jcoord.OSRef;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

public class LondonUK_AsyncBusStops extends AsyncTask<Void, Integer, String> {
	private static final String BUS_LOCATIONS_URL = "http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=willinghamg%40hotmail.com&feedId=10";
	private static final String LOGNAME = "LondonUK_AsyncBusStops";

	private LocationsDBAdapter ldba = null;	
	private Exception failure = null;
	private ProgressDisplay progressDisplay = null;
	private String[] progressLabels = new String[] {"Contacting server", "Downloading data", "Translating TFL coords to Lat/Long"};
	
	public void init(Context ctx) {
		ldba = new LocationsDBAdapter(ctx);
	}
	
	public void init(Context ctx, ProgressDisplay pd) {
		ldba = new LocationsDBAdapter(ctx);
		progressDisplay = pd;
	}
	
	public Exception getFailure() {
		return failure;
	}
	
	@Override
	protected void onPreExecute() {
		if (null != progressDisplay) {
			progressDisplay.setMaxValue(58000); //TODO remove hard-coded value
			progressDisplay.setProgress(0);
			progressDisplay.setProgressLabel("");
			progressDisplay.setVisibility(View.VISIBLE);
		}
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
			this.publishProgress(0, 0);
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			Log.d(LOGNAME, "Requesting data from Server");
			HttpResponse response = client.execute(request);
			
			this.publishProgress(1, 0);
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
			this.publishProgress(2, count);
			ldba.open();
			while (null != line && !("".equals(line))) {
//				Log.v(LOGNAME, "Parsing stops line...");
				processLocation(line);
//				Location l = getLocation(line);
//				if (null != l) locations.put(l.getId(), l);

				line = br.readLine();
				count++;
				if (count%100 == 0) {
					this.publishProgress(2, count);
				}
			}

			Log.d(LOGNAME, "Finished processing response.");
			
		} catch (IOException ioe) {
			Log.e(LOGNAME, "Unexception IOException occured: "+ioe);
			failure = ioe;
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
		return null;
	}

	private void processLocation(String s) {
		String cols[] = s.split(",");
		if (cols.length < 6) return;
		
//		String stopCode = cols[1];
		Location loc = ldba.getLocationByStopCode(cols[1]);

		if (null == loc) createNewLocation(cols);
		else {
//			String srcPosA = cols[4];
//			String srcPosB = cols[5];
			
			if (cols[4] != loc.getSrcPosA() || cols[5] != loc.getSrcPosB()) {
				//stop has moved location - delete old one and re-create
				ldba.deleteLocation(loc.getId());
				createNewLocation(cols);
			} else {
				//stop still in same place - update other details
//				String name = cols[3];
//				String desc = "";
//				String heading = cols[6];
				ldba.updateLocation(loc.getId(), cols[1], cols[3], loc.getDescription(), loc.getLat(), loc.getLon(), cols[4], cols[5], cols[6], loc.getNickName(), loc.getChosen());
			}
		}
	}
	
	private void createNewLocation(String cols[]) {		
//		String stopCode = cols[1];
//		String name = cols[3];
//		String desc = "";
//		String srcPosA = cols[4];
//		String srcPosB = cols[5];
//		String heading = cols[6];

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
		ldba.createLocation(cols[1], cols[3], "", lat, lng, cols[4], cols[5], cols[6]);
	}
	
	protected void onProgressUpdate(Integer... progress) {		
		if (null != progressDisplay) {
			int label = progress[0];
			int value = progress[1];
			
			String progressLabel = progressLabels[label];
			if (progress[0] == 2) progressLabel += " ("+value+")";
			progressDisplay.setProgressLabel(progressLabel);
			progressDisplay.setProgress(value);
		}
	}
	
	protected void onPostExecute() {
		if (null != progressDisplay) {
			progressDisplay.setVisibility(View.GONE);
		}
		
		if (null != ldba) {
			ldba.close();
			ldba = null;
		}
	}
}
