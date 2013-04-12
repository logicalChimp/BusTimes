package uk.co.mentalspace.android.bustimes;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ConfigurationActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuration);
		configureLayout();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		configureLayout();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.configuration, menu);
		return true;
	}

	private void configureLayout() {
		String sourceId = Preferences.getPreference(this, Preferences.KEY_SOURCE_ID);
		if (null != sourceId && !"".equals(sourceId.trim())) {
			Source src = SourceManager.getSource(sourceId);
			TextView srcLabel = (TextView)this.findViewById(R.id.configure_source_chosen_source);
			srcLabel.setText(src.getName());
			
			LinearLayout refreshLocationsGroup = (LinearLayout)this.findViewById(R.id.configure_source_force_download_group);
			refreshLocationsGroup.setVisibility(View.VISIBLE);
			
			LinearLayout addLocationsGroup = (LinearLayout)this.findViewById(R.id.configure_add_location_group);
			addLocationsGroup.setVisibility(View.VISIBLE);
		}
		
		String chosenStops = Preferences.getPreference(this, Preferences.KEY_PREFERRED_STOP_ID);
		if (null != chosenStops && !"".equals(chosenStops.trim())) {
			String[] stopCodes = chosenStops.split(",");

			String locatorId = Preferences.getPreference(this, Preferences.KEY_LOCATOR_ID);
	        if (null != locatorId && !"".equals(locatorId.trim())) {
	        	Locator locator = LocatorManager.getLocator(locatorId);
	        	if (null != locator) {
	    			ChosenLocationsArrayAdapter claa = new ChosenLocationsArrayAdapter(this, stopCodes, locator);
	    			ListView lv = (ListView)findViewById(R.id.configure_chosen_locations_list);
	    			lv.setAdapter(claa);
	        	}
	        }
		}
		
		LinearLayout sourceSelectorGroup = (LinearLayout)this.findViewById(R.id.configure_source_group);
		sourceSelectorGroup.setOnClickListener(this);
		
		Button refreshLocationsButton = (Button)this.findViewById(R.id.configure_source_refresh_data_button);
		refreshLocationsButton.setOnClickListener(this);
		
		Button addLocationButton = (Button)this.findViewById(R.id.configure_browse_locations_button);
		addLocationButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int viewId = arg0.getId();
		switch (viewId) {
		case R.id.configure_source_group:
			//display popup-list of possible sources
			return;
		case R.id.configure_source_refresh_data_button:
			//trigger download of stops
			String sourceId = Preferences.getPreference(this, Preferences.KEY_SOURCE_ID);
			if (null == sourceId || "".equals(sourceId.trim())) return;
			
			Source src = SourceManager.getSource(sourceId);
			View container = (View)this.findViewById(R.id.configure_source_progress_group);
			TextView label = (TextView)this.findViewById(R.id.configure_locations_download_progress_label);
			ProgressBar bar = (ProgressBar)this.findViewById(R.id.configure_locations_download_progress_bar);
			ProgressDisplay pd = new ProgressDisplay(this, container, label, bar);
			pd.setMaxValue(src.getEstimatedLocationCount());
			src.loadLocations(this, pd);
			return;
		case R.id.configure_browse_locations_button:
			//trigger display of location selector activity
    		startActivity(new Intent(this, SelectLocationActivity.class));
			return;
		default:
		}
	}
}