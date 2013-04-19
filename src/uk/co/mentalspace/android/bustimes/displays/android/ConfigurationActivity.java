package uk.co.mentalspace.android.bustimes.displays.android;

import java.util.List;
import java.util.ArrayList;

import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.LocationManager;
import uk.co.mentalspace.android.bustimes.LocationRefreshService;
import uk.co.mentalspace.android.bustimes.R;
import uk.co.mentalspace.android.bustimes.Source;
import uk.co.mentalspace.android.bustimes.SourceManager;
import uk.co.mentalspace.android.bustimes.utils.LocationsListAdapter;
import uk.co.mentalspace.android.bustimes.utils.SourcesListAdapter;

import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class ConfigurationActivity extends FragmentActivity implements OnClickListener, OnItemClickListener, OnItemSelectedListener, OnDismissListener {

	private static final String LOGNAME = "ConfigurationActivity";
	
	private List<Source> srcs = null;
	private Source selectedSource = null;
	
	private BroadcastReceiver drsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	ConfigurationActivity.this.receiveBroadcast(intent);
        }
    };
    private boolean drsReceiverIsRegistered = false;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuration);
//		configureLayout();
	}
	
	@Override
	protected void onResume() {
		Log.d(LOGNAME, "onResume");
		super.onResume();
		configureLayout();
		if (!drsReceiverIsRegistered) {
		    registerReceiver(drsReceiver, new IntentFilter(LocationRefreshService.ACTION_UPDATE_DATA_REFRESH_PROGRESS));
		    drsReceiverIsRegistered = true;

		    //request update on service progress - after re-registering the broadcast receiver
			Intent intent = new Intent(this, LocationRefreshService.class);
			intent.setAction(LocationRefreshService.ACTION_GET_REFRESH_PROGRESS);
			startService(intent);
		}		
	}

	@Override
	protected void onPause() {
		Log.d(LOGNAME, "onPause");
		if (drsReceiverIsRegistered) {
		    unregisterReceiver(drsReceiver);
		    drsReceiverIsRegistered = false;
		}
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.configuration, menu);
		return true;
	}

	private void configureLayout() {
		Log.d(LOGNAME, "Configuring layout...");
		srcs = SourceManager.getAllSources(this);		
		Source[] srcsArray = srcs.toArray(new Source[]{});
		
		((Spinner)findViewById(R.id.configure_select_source)).setOnItemSelectedListener(this);
		SourcesListAdapter sla = new SourcesListAdapter(this, srcsArray);
		sla.setDropDownViewResource(R.layout.sources_list_row_layout);
		Spinner spinner = (Spinner)findViewById(R.id.configure_select_source);
		spinner.setAdapter(sla);

		LinearLayout refreshLocationsGroup = (LinearLayout)this.findViewById(R.id.configure_source_force_download_group);
		refreshLocationsGroup.setVisibility(View.VISIBLE);
		
		LinearLayout addLocationsGroup = (LinearLayout)this.findViewById(R.id.configure_add_location_group);
		addLocationsGroup.setVisibility(View.VISIBLE);
		
		Button refreshLocationsButton = (Button)this.findViewById(R.id.configure_source_refresh_data_button);
		refreshLocationsButton.setOnClickListener(this);
		
		Button addLocationButton = (Button)this.findViewById(R.id.configure_browse_locations_button);
		addLocationButton.setOnClickListener(this);
		
		configureFavouriteLocationsList();
	}
	
	private void configureFavouriteLocationsList() {
		Log.d(LOGNAME, "Configuring favourite locations list");
		List<Location> selectedLocations = LocationManager.getSelectedLocations(this);
		if (null == selectedLocations) selectedLocations = new ArrayList<Location>(); 
		LocationsListAdapter claa = new LocationsListAdapter(this, selectedLocations.toArray(new Location[]{}));
		ListView lv = (ListView)findViewById(R.id.configure_chosen_locations_list);
		lv.setAdapter(claa);
		lv.setOnItemClickListener(this);		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int viewId = arg0.getId();
		switch (viewId) {
		case R.id.configure_source_refresh_data_button:
			//trigger download of stops
			if (null == selectedSource) return;
			
			Log.d(LOGNAME, "Sending intent to start Data Refresh Service");
			Intent intent = new Intent(this, LocationRefreshService.class);
			intent.setAction(LocationRefreshService.ACTION_REFRESH_LOCATION_DATA);
			intent.putExtra(LocationRefreshService.EXTRA_SOURCE_ID, selectedSource.getID());
			this.startService(intent);
			
//			Source src = SourceManager.getSource(sourceId);
//			View container = (View)this.findViewById(R.id.configure_source_progress_group);
//			TextView label = (TextView)this.findViewById(R.id.configure_locations_download_progress_label);
//			ProgressBar bar = (ProgressBar)this.findViewById(R.id.configure_locations_download_progress_bar);
//			ProgressDisplayImpl pd = new ProgressDisplayImpl(this, container, label, bar);
//			pd.setMaxValue(src.getEstimatedLocationCount());
//			src.loadLocations(this, pd);
			return;
		case R.id.configure_browse_locations_button:
			//trigger display of location selector activity
    		startActivity(new Intent(this, SelectLocationActivity.class));
			return;
		default:
		}
	}

	@Override
	public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
		String stopCode = ((TextView)view.findViewById(R.id.chosen_location_stop_code_label)).getText().toString();
		if (null == stopCode || "".equals(stopCode.trim())) return;
		
		Location loc = LocationManager.getLocationByStopCode(this, stopCode);
		if (null == loc) return;
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		EditLocationPopup elp = EditLocationPopup.newInstance(loc);
		
		//show dialog
		elp.show(fragmentManager, "EditLocationDialog");
		fragmentManager.executePendingTransactions();
		elp.getDialog().setOnDismissListener(this);
	}

    public void receiveBroadcast(Intent intent) {
    	String action = intent.getAction();
    	Log.d(LOGNAME, "Received broadcast intent.  Action: " + action);
    	if (LocationRefreshService.ACTION_UPDATE_DATA_REFRESH_PROGRESS.equals(action)) {
			TextView label = (TextView)this.findViewById(R.id.configure_locations_download_progress_label);
			ProgressBar bar = (ProgressBar)this.findViewById(R.id.configure_locations_download_progress_bar);
			
			bar.setMax(intent.getIntExtra(LocationRefreshService.EXTRA_MAX_VALUE, 0));
			bar.setProgress(intent.getIntExtra(LocationRefreshService.EXTRA_CURRENT_VALUE, 0));
			label.setText(intent.getStringExtra(LocationRefreshService.EXTRA_PROGRESS_LABEL));
			
			View container = (View)this.findViewById(R.id.configure_source_progress_group);
			container.setVisibility(View.VISIBLE);
    	}
    	if (LocationRefreshService.ACTION_LOCATION_REFRESH_TASK_COMPLETE.equals(action)) {
			View container = (View)this.findViewById(R.id.configure_source_progress_group);
			container.setVisibility(View.GONE);
			Toast.makeText(this, "Locations download complete", Toast.LENGTH_SHORT).show();
    	}
    }

	@Override
	public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
		if (null == srcs || srcs.size() <= position) {
			((Button)this.findViewById(R.id.configure_source_refresh_data_button)).setEnabled(false);
			return;
		}
		
		selectedSource = srcs.get(position);
		((Button)this.findViewById(R.id.configure_source_refresh_data_button)).setEnabled(true);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		//do nothing
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		configureFavouriteLocationsList();
	}

}
