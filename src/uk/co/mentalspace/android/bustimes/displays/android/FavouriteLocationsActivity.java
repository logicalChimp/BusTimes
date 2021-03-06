package uk.co.mentalspace.android.bustimes.displays.android;

import java.util.ArrayList;
import java.util.List;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import net.londatiga.android.QuickAction.OnActionItemClickListener;

import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.LocationManager;
import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.R;
import uk.co.mentalspace.android.bustimes.displays.android.listadapters.LocationsListAdapter;
import uk.co.mentalspace.android.bustimes.displays.android.popups.EditLocationPopup;
import uk.co.mentalspace.android.bustimes.utils.QABGenerator;

import android.os.Bundle;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FavouriteLocationsActivity extends FragmentActivity implements OnItemClickListener, OnDismissListener, OnActionItemClickListener, QuickAction.OnDismissListener  {

	private static final String LOGNAME = "FavLocsActivity";

	public static final String ACTION_SHOW_STAGE_TWO_WELCOME = "showStageTwoWelcome";
	public static final String ACTION_NORMAL = "actionNormal";
	
	private List<Location> locations = null;
	
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favourite_locations);
	}
	
	@Override
	protected void onResume() {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "onResume");
		super.onResume();
		configureLayout();
	}

	@Override
	protected void onPause() {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "onPause");
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_favourite_locations, menu);
		return true;
	}

	private void configureLayout() {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Configuring layout...");
		configureFavouriteLocationsList();
	}
	
	private void configureFavouriteLocationsList() {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Configuring favourite locations list");
		locations = LocationManager.getSelectedLocations(this);
		if (null == locations) locations = new ArrayList<Location>(); 
		
		LocationsListAdapter claa = new LocationsListAdapter(this, locations.toArray(new Location[]{}));
		ListView lv = (ListView)findViewById(R.id.configure_chosen_locations_list);
		lv.setAdapter(claa);
		lv.setOnItemClickListener(this);		
	}

	//click on item in locations list
	@Override
	public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
		if (null == locations || locations.size() <= position) return;
		
		Location loc = locations.get(position);
		final QuickAction mQuickAction = QABGenerator.getLocationQABar(this, loc, this, true);
		mQuickAction.show(view);
	}

	//dismiss of edit dialog
	@Override
	public void onDismiss(DialogInterface dialog) {
		configureFavouriteLocationsList();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add_favourite:
    		startActivity(new Intent(this, SelectLocationActivity.class));
			return true;
		case R.id.menu_manage_sources:
    		startActivity(new Intent(this, ManageSourcesActivity.class));
			return true;
		case R.id.menu_settings:
    		startActivity(new Intent(this, SettingsActivity.class));
    		return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	//dismiss of quick actions bar
	@Override
	public void onDismiss() {
		//TODO remove
		Toast.makeText(this, "Ups..dismissed", Toast.LENGTH_SHORT).show();
	}

	//click on item in actions bar
	@Override
	public void onItemClick(QuickAction source, int pos, int actionId) {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "QuickAction clicked. Pos ["+pos+"], id ["+actionId+"]");
		ActionItem ai = source.getActionItem(pos);
		Location loc = QABGenerator.getLocationFromActionItem(ai);
		
		switch (actionId) {
		case QABGenerator.ACTION_ITEM_ID_EDIT_LOCATION:
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "QuickAction 'Location Edit' clicked");
			FragmentManager fragmentManager = getSupportFragmentManager();
			EditLocationPopup elp = EditLocationPopup.newInstance(loc);
			
			//show dialog
			elp.show(fragmentManager, "EditLocationDialog");
			fragmentManager.executePendingTransactions();
			elp.getDialog().setOnDismissListener(this);
			return;
		case QABGenerator.ACTION_ITEM_ID_SHOW_ON_MAP:
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Sending intent to show location ["+loc+"] on map");
			Intent intent = new Intent(this, SelectLocationActivity.class);
			intent.setAction(SelectLocationActivity.ACTION_SHOW_LOC_ON_MAP);
			intent.putExtra(SelectLocationActivity.EXTRA_LOCATION, loc);
			startActivity(intent);
			return;
		case QABGenerator.ACTION_ITEM_ID_MAKE_FAVOURITE:
			LocationManager.selectLocation(this, loc.getId());
			configureFavouriteLocationsList();
			break;
		case QABGenerator.ACTION_ITEM_ID_UNMAKE_FAVOURITE:
			LocationManager.deselectLocation(this, loc.getId());
			configureFavouriteLocationsList();
			break;
		}
	}
}
