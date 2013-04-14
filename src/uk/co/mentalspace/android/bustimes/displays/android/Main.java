package uk.co.mentalspace.android.bustimes.displays.android;


import uk.co.mentalspace.android.bustimes.BusTime;

import java.util.ArrayList;
import java.util.List;

import uk.co.mentalspace.android.bustimes.ChosenLocationsArrayAdapter;
import uk.co.mentalspace.android.bustimes.ConfigurationActivity;
import uk.co.mentalspace.android.bustimes.Coordinator;
import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.LocationManager;
import uk.co.mentalspace.android.bustimes.LocationTracker;
import uk.co.mentalspace.android.bustimes.R;
import uk.co.mentalspace.android.bustimes.Renderer;
import uk.co.mentalspace.android.bustimes.SelectLocationActivity;
import uk.co.mentalspace.android.bustimes.SettingsActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class Main extends Activity implements Renderer, OnItemSelectedListener {
	private static final String LOGNAME = "AndroidDisplay";
	
	private Location loc = null;
	private LocationTracker posTracker = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		findViewById(R.id.bus_times_results).setVisibility(View.GONE);
		findViewById(R.id.bus_times_message).setVisibility(View.GONE);
		((Spinner)findViewById(R.id.bus_times_location)).setOnItemSelectedListener(this);

		Log.d(this.getLocalClassName(), "Main Activity loaded, handing over to Coordinator");
    }

	@Override
	public void onResume() {
		super.onResume();
		Log.d(this.getLocalClassName(), "Resuming activity - starting fresh Coordinator");
		if (null == posTracker) {
			posTracker = new LocationTracker(this);
			if (!posTracker.isGPSEnabled) {
				posTracker.showSettingsAlert();
			}
		}
		populateListOfSelectedLocations();
		showBusTimes();
	}
	
	private void populateListOfSelectedLocations() {
		List<Location> locations = LocationManager.getSelectedLocations(this);
		Location[] locsArray = locations.toArray(new Location[]{});
		
		ChosenLocationsArrayAdapter claa = new ChosenLocationsArrayAdapter(this, locsArray);
		claa.setDropDownViewResource(R.layout.chosen_location_row_layout);
		Spinner spinner = (Spinner)findViewById(R.id.bus_times_location);
		spinner.setAdapter(claa);
	}
	
	private void showBusTimes() {
		Log.d(LOGNAME, "Fetching bus times...");
		if (null == loc) {
			int lat = (int)(posTracker.getLatitude()*10000);
			int lon = (int)(posTracker.getLongitude()*10000);
			loc = Coordinator.getNearestLocation(this, lat, lon);
		}
		Coordinator.getBusTimes(this, loc);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	public void finish() {
		Log.d(this.getLocalClassName(), "Terminating activity - terminating Coordinator timer");
		Coordinator.terminate();
		super.finish();
	}
	
	@Override
	public void onPause() {
		Log.d(this.getLocalClassName(), "Pausing activity - terminating Coordinator timer");
		Coordinator.terminate();
		if (null != posTracker) {
			posTracker.stopTrackingLocation();
			posTracker = null;
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
    	case R.id.menu_settings:
    		startActivity(new Intent(this, SettingsActivity.class));
    		return true;
    	case R.id.menu_map:
    		startActivity(new Intent(this, SelectLocationActivity.class));
    		return true;
    	case R.id.menu_configure:
    		startActivity(new Intent(this, ConfigurationActivity.class));
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
	public void displayMessage(String msg, int msgLevel) {
		findViewById(R.id.bus_times_results).setVisibility(View.GONE);
		TextView tv = (TextView)findViewById(R.id.bus_times_message);
		tv.setVisibility(View.VISIBLE);
		tv.setText(msg);
	}

	@Override
	public void displayBusTimes(Location location, List<BusTime> busTimes) {
		Log.d(LOGNAME, "Displaying ["+busTimes.size()+"] bus times");
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
		Log.d(LOGNAME, "Locations drop-down list item selected");
		// TODO Auto-generated method stub
		String stopCode = ((TextView)view.findViewById(R.id.chosen_location_stop_code_label)).getText().toString();
		if (null == stopCode || "".equals(stopCode.trim())) return;
		Log.d(LOGNAME, "Selected stop code: " + stopCode);
		
		loc = LocationManager.getLocationByStopCode(this, stopCode);
		if (null == loc) return;
		Log.d(LOGNAME, "Selected location: " + loc.getLocationName());
		
		showBusTimes();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		//do nothing
	}

}
