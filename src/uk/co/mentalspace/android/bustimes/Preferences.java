package uk.co.mentalspace.android.bustimes;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class Preferences {

	public static final String KEY_SOURCE_ID = "sourceID";
	public static final String KEY_PREFERRED_STOP_ID = "PreferredStopID";
	
	private static boolean initialised = false;
	
	private static OnSharedPreferenceChangeListener prefChangeListener = new OnSharedPreferenceChangeListener() {
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			//do nothing for now
		}
	};
	
	public static void initialise(Context ctx) {
		if (initialised) return;
		
		PreferenceManager.getDefaultSharedPreferences(ctx).registerOnSharedPreferenceChangeListener(prefChangeListener);
		
		initialised = true;
	}
	
	public static String getPreference(Context ctx, String key) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sharedPreferences.getString(key, null);
	}
	
	
}
