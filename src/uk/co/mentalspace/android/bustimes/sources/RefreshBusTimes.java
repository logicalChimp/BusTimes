package uk.co.mentalspace.android.bustimes.sources;

import android.util.Log;
import uk.co.mentalspace.android.bustimes.DataRefreshTask;

public class RefreshBusTimes extends DataRefreshTask {
	private static final String LOGNAME = "RefreshBusTimes";
	
	@Override
	public void run() {
		display.execute(new Runnable() {
			public void run() {
				
		        //fetch list of bus times for the location
				LondonUK_AsyncBusTimes async = null;
				try {
					Log.d(LOGNAME, "Creating async task...");
					async = new LondonUK_AsyncBusTimes();
				} catch (Exception e) {
					Log.d(LOGNAME, "Unexpected error", e);
					return;
				}
				
				Log.d(LOGNAME, "Initialising async task with display, location");
				async.init(display, location);
				
				Log.d(LOGNAME, "Executing async task");
				async.execute();
			}
		});
	}

//	private static final String BUS_TIMES_URL = "http://countdown.api.tfl.gov.uk/interfaces/ura/instant_V1";
//
//	@Override
//	public void run() {
//		String url = BUS_TIMES_URL + "?StopCode1="+location.getId()+"&DirectionID=1&VisitNumber=1&ReturnList=LineName,DestinationText,EstimatedTime";
//		Log.d(LOGNAME, "Data feed url: "+url);
//
//		ArrayList<BusTime> busTimes = new ArrayList<BusTime>();
//		BufferedReader br = null;
//		try {
//			HttpClient client = new DefaultHttpClient();
//			HttpGet request = new HttpGet(url);
//			Log.d(LOGNAME, "Requesting data from Server");
//			HttpResponse response = client.execute(request);
//			
//			Log.d(LOGNAME, "Request executed - processing response");
//			InputStreamReader isr = new InputStreamReader(response.getEntity().getContent());
//			br = new BufferedReader(isr);
//			
//			String line = br.readLine();
//			Log.d(LOGNAME, "First line: "+line);
//			long refTime = getRefTime(line);
//
//			line = br.readLine();  //ignore first line - headers
//			while (null != line && !("".equals(line))) {
//				Log.d(LOGNAME, "Line: " + line);
//				BusTime bt = getBusTime(line, refTime);
//				busTimes.add(bt);
//				line = br.readLine();
//			}
//
//			Log.d(LOGNAME, "Finished processing response.");
//			
//		} catch (IOException ioe) {
//			Log.e(LOGNAME, "Unexception IOException occured: ", ioe);
//		} finally {
//			if (null != br) {
//				try {
//					br.close();
//				} catch (IOException ioe2) {
//					Log.e(LOGNAME, "Failed to close input stream. cause: ", ioe2);
//				}
//			}
//		}
//				
//		final List<BusTime> bts = busTimes;
//		display.execute(new Runnable() {
//			public void run() {
//				Coordinator.updateBusTimes(display, location, bts);
//			}
//		});
//	}
//
//	private long getRefTime(String line) {
//		try {
//			JSONArray j = new JSONArray(line);
//			if (j.length() != 3) return new Date().getTime();
//			return j.getLong(2);
//		} catch (JSONException e) {
//			Log.e(LOGNAME, "JSON Error parsing header.\nData: "+line+"\nError: ",e);
//		}
//		return new Date().getTime();
//	}
//	
//	private BusTime getBusTime(String line, long refTime) {
//		if (null == line || "".equals(line.trim())) return new BusTime("", "", "");
//		
//		try {
//			JSONArray j = new JSONArray(line);
//			if (j.length() < 4) return new BusTime("", "", "");
//			
//			long l = j.getLong(3) - refTime;
//			if (l < 0) l = 0; //prevent negative time estimates
//			l = l/1000; //convert to seconds
//			l = l/60; //convert to minutes
//			String est = (l > 0) ? String.valueOf(l) : "Due";
//			return new BusTime(j.getString(1), j.getString(2), est);
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			Log.e(LOGNAME, "JSON Error parsing bus data.\nData: "+line+"\nError: ", e);
//		}
//		return new BusTime("", "", "");
//	}
}
