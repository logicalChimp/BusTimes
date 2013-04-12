package uk.co.mentalspace.android.bustimes;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceClickListener;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class SettingsActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		final Context ctx = this;
		addPreferencesFromResource(R.layout.settings);
	
		Preference refreshData = findPreference("RefreshLocations");
		refreshData.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference arg0) {
				Source src = Coordinator.getChosenSource(ctx);
				src.loadLocations(ctx);
				return false;
			}
		});
	}
}
