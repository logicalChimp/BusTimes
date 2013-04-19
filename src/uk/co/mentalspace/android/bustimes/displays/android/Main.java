package uk.co.mentalspace.android.bustimes.displays.android;

import uk.co.mentalspace.android.bustimes.LocationManager;
import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class Main extends Activity {
	private static final String LOGNAME = "StartPoint";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		int locations = LocationManager.getLocationsCount(this);
		if (-1 == locations) {
			//failed to read the database - display error message
			if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "Unable to read location count from database - aborting startup");
			finish();
			return;
		} else if (0 == locations) {
			//first run - display welcome message
			Intent intent = new Intent(this, FavouriteLocationsActivity.class);
			intent.setAction(FavouriteLocationsActivity.ACTION_SHOW_STAGE_ONE_WELCOME);
			this.startActivity(intent);
			finish();
			return;
		}
		
		int selected = LocationManager.getSelectedLocationsCount(this);
		if (-1 == selected) {
			//failed to read the database - display error message
			if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "Unable to read selected locations count from database - aborting startup");
			finish();
			return;
		} else if (0 == selected && !Preferences.GET_NEAREST_INCLUDES_NON_FAVOURITES) {
			//locations, but no favourites selected - show stage two welcome message
			Intent intent = new Intent(this, FavouriteLocationsActivity.class);
			intent.setAction(FavouriteLocationsActivity.ACTION_SHOW_STAGE_TWO_WELCOME);
			this.startActivity(intent);
			finish();
			return;
		} else if (0 < selected || Preferences.GET_NEAREST_INCLUDES_NON_FAVOURITES) {
			//either have selected favourites, or can use GPS to select nearest location - show bus times
			Intent intent = new Intent(this, BusTimeActivity.class);
			this.startActivity(intent);
			finish();
			return;
		}
		
		//got here? oops
		if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "Failed to match any start condition - aborting");
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return false;
	}

}
