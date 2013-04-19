package uk.co.mentalspace.android.bustimes.displays.android;

import java.util.ArrayList;
import uk.co.mentalspace.android.bustimes.BusTime;
import java.util.List;

import uk.co.mentalspace.android.bustimes.BusTimeRefreshService;
import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.LocationManager;
import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.R;
import uk.co.mentalspace.android.bustimes.Renderer;
import uk.co.mentalspace.android.bustimes.Source;
import uk.co.mentalspace.android.bustimes.SourceManager;
import uk.co.mentalspace.android.bustimes.displays.android.listadapters.BusTimeListAdapter;
import uk.co.mentalspace.android.bustimes.displays.android.listadapters.LocationsListAdapter;
import uk.co.mentalspace.android.bustimes.sources.londonuk.LondonUK;
import uk.co.mentalspace.android.bustimes.sources.test.TestSource;
import uk.co.mentalspace.android.bustimes.utils.LocationTracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class BusTimeActivity extends Activity implements Renderer, OnItemSelectedListener {
	private static final String LOGNAME = "BusTimeActivity";
	
	private BroadcastReceiver btReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	BusTimeActivity.this.receiveBroadcast(intent);
        }
    };
    private boolean btReceiverIsRegistered = false;

    private Location loc = null;
	private LocationTracker posTracker = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_time);
		findViewById(R.id.bus_times_results).setVisibility(View.GONE);
		findViewById(R.id.bus_times_message).setVisibility(View.GONE);
		((Spinner)findViewById(R.id.bus_times_location)).setOnItemSelectedListener(this);

		List<Location> locations = LocationManager.getSelectedLocations(this);		
		if (null == locations || locations.isEmpty()) {
			Intent config = new Intent(this, FavouriteLocationsActivity.class);
			this.startActivity(config);
			return;
		}
    }
    
	@Override
	public void onResume() {
		super.onResume();
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Resuming activity - starting fresh Coordinator");

		if (null == posTracker) {
			posTracker = new LocationTracker(this);
			if (!posTracker.isGPSEnabled) {
				posTracker.showSettingsAlert();
			}
		}

		if (!btReceiverIsRegistered) {
		    registerReceiver(btReceiver, new IntentFilter(BusTimeRefreshService.ACTION_LATEST_BUS_TIMES));
		    btReceiverIsRegistered = true;
		}

		//populateListOfSelectedLocations will populate list, and then trigger onListItemSelected
		populateListOfSelectedLocations();
	}
	
	private void populateListOfSelectedLocations() {
		List<Location> locations = LocationManager.getSelectedLocations(this);		
		Location[] locsArray = locations.toArray(new Location[]{});
		
		LocationsListAdapter claa = new LocationsListAdapter(this, locsArray);
		claa.setDropDownViewResource(R.layout.location_list_row_layout);
		Spinner spinner = (Spinner)findViewById(R.id.bus_times_location);
		spinner.setAdapter(claa);
	}
	
	private void showBusTimes() {
		if (null == loc) {
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "No location yet chosen - requesting load of nearest chosen location");
			int lat = (int)(posTracker.getLatitude()*10000);
			int lon = (int)(posTracker.getLongitude()*10000);
			loc = LocationManager.getNearestSelectedLocation(getDisplayContext(), lat, lon);
		}
		if (null == loc) {
			if (Preferences.ENABLE_LOGGING) Log.w(LOGNAME, "Cannot get 'nearest' location - maybe none chosen?");
			this.displayMessage(null, "Please select one or more locations to monitor.", Renderer.MESSAGE_ERROR);
			return;
		}
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Fetching bus times for location ["+loc+"]");
//		Coordinator.getBusTimes(this, loc);
		
		Intent intent = new Intent(this, BusTimeRefreshService.class);
		intent.setAction(BusTimeRefreshService.ACTION_REFRESH_BUS_TIMES);
		intent.putExtra(BusTimeRefreshService.EXTRA_LOCATION_ID, loc.getId());
		intent.putExtra(BusTimeRefreshService.EXTRA_SOURCE_ID, loc.getSourceId());
		this.startService(intent);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	public void finish() {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Terminating activity - terminating Coordinator timer");
		super.finish();
	}
	
	@Override
	public void onPause() {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Pausing activity - terminating Coordinator timer");
		if (null != posTracker) {
			posTracker.stopTrackingLocation();
			posTracker = null;
		}
		if (btReceiverIsRegistered) {
		    unregisterReceiver(btReceiver);
		    btReceiverIsRegistered = false;
		}
		super.onPause();
	}
	
	@Override
    public void execute(Runnable r) {
    	runOnUiThread(r);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.menu_update_sources:
    		Source tflSrc = new LondonUK();
    		SourceManager.createSource(this, tflSrc);
    		Source testSrc = new TestSource();
    		SourceManager.createSource(this, testSrc);
    		return true;
    	case R.id.menu_map:
    		startActivity(new Intent(this, SelectLocationActivity.class));
    		return true;
    	case R.id.menu_configure:
    		startActivity(new Intent(this, FavouriteLocationsActivity.class));
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
	@Override
	public String getID() {
		return "AndroidScreenRenderer";
	}
	
	@Override
	public Context getDisplayContext() {
		return this;
	}

	@Override
	public void displayMessage(Location loc, String msg, int msgLevel) {
		findViewById(R.id.bus_times_results).setVisibility(View.GONE);
		TextView tv = (TextView)findViewById(R.id.bus_times_message);
		tv.setVisibility(View.VISIBLE);
		tv.setText(msg);
	}

	@Override
	public void displayBusTimes(Location location, List<BusTime> busTimes) {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Displaying ["+busTimes.size()+"] bus times");
		findViewById(R.id.bus_times_message).setVisibility(View.GONE);
		ListView lv = (ListView)findViewById(R.id.bus_times_results);
		lv.setVisibility(View.VISIBLE);

		//TODO insert column headers more gracefully
		BusTime bt = new BusTime("Bus", "Destination", "ETA (mins)");
		ArrayList<BusTime> bts = new ArrayList<BusTime>();
		bts.add(bt);
		bts.addAll(busTimes);
		
		BusTimeListAdapter btla = new BusTimeListAdapter(this.getDisplayContext(), bts.toArray(new BusTime[bts.size()]));
		lv.setAdapter(btla);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (null == view) { 
			if (Preferences.ENABLE_LOGGING) Log.w(LOGNAME, "Null view selected - ignoring the item selection.");
			return;
		}
		
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Locations drop-down list item selected");
		// TODO Auto-generated method stub
		TextView tv = ((TextView)view.findViewById(R.id.chosen_location_stop_code_label));
		if (null == tv) return;
		
		String stopCode = tv.getText().toString();
		if (null == stopCode || "".equals(stopCode.trim())) return;
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Selected stop code: " + stopCode);
		
		loc = LocationManager.getLocationByStopCode(this, stopCode);
		if (null == loc) return;
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Selected location: " + loc.getLocationName());
		
		this.displayMessage(loc, "Fetching bus times...", Renderer.MESSAGE_NORMAL);
		showBusTimes();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		//do nothing
	}

	public void receiveBroadcast(Intent intent) {
		//don't bother processing the incoming intent if the user has deselected the current location
		if (null == loc) return;

		String action = intent.getAction();
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Received broadcast. action: "+action);
		
		if (BusTimeRefreshService.ACTION_LATEST_BUS_TIMES.equals(action)) {
			long locId = intent.getLongExtra(BusTimeRefreshService.EXTRA_LOCATION_ID, -1);
			String srcId = intent.getStringExtra(BusTimeRefreshService.EXTRA_SOURCE_ID);
			
			if (loc.getId() != locId || !loc.getSourceId().equals(srcId)) {
				if (Preferences.ENABLE_LOGGING) Log.w(LOGNAME, "Received updated bus times for location other than that selected. Loc id ["+locId+"], Src id ["+srcId+"].  Ignoring.");
				return;
			}
			
			@SuppressWarnings("unchecked")
			List<BusTime> busTimes = (List<BusTime>)intent.getSerializableExtra(BusTimeRefreshService.EXTRA_BUS_TIMES);
			
			int busTimesSize = (null == busTimes) ? -1 : busTimes.size();
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Received ["+busTimesSize+"] bus times");

			displayBusTimes(loc, busTimes);
		}
	}
}
