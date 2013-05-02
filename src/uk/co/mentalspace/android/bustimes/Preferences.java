package uk.co.mentalspace.android.bustimes;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

public class Preferences implements OnSharedPreferenceChangeListener {

	protected static final String KEY_ENABLE_LOGGING = "enable_logging";
	protected static final String KEY_GET_NEAREST_INCLUDES_NON_FAVOURITES = "get_nearest_includes_non_favourite";
	protected static final String KEY_MW_FIRMWARE_VERSION = "mw_firmware_version";
	protected static final String KEY_AUTO_REFRESH_BUS_TIMES = "auto_refresh_bus_times";
	protected static final String KEY_AUTO_REFRESH_RATE = "auto_refresh_rate";
	protected static final String KEY_REMEMBER_LAST_MAP_POS = "remember_last_map_pos";
	protected static final String KEY_ENABLE_LOGGING_TO_FILE = "enable_logging_to_file";
	protected static final String KEY_LOGFILE_LOCATION = "logfile_location";
	protected static final String KEY_USE_GPS_TO_SELECT_LOCATION = "use_gps_to_select_location";
	
	public static enum Firmware {DEVELOPER, PRODUCTION};
	public static enum RefreshRate {SOURCE_DEFAULT, THIRTY_SECONDS, SIXTY_SECONDS};
	
	public static boolean ENABLE_LOGGING = false;
	public static boolean GET_NEAREST_INCLUDES_NON_FAVOURITES = true;
	public static Firmware FW_VERSION = Firmware.PRODUCTION;
	public static boolean AUTO_REFRESH_BUS_TIMES = true;
	public static RefreshRate AUTO_REFRESH_RATE = RefreshRate.SOURCE_DEFAULT;
	public static boolean REMEMBER_LAST_MAP_POSITION = true;
	public static boolean ENABLE_LOGFILE = false;
	public static String DEBUG_LOGFILE_PATH = "";
	public static boolean USE_GPS_TO_SELECT_LOCATION = true;

	protected static Preferences self = null;
	
	public static Preferences getInstance() {
		if (null == self) self = new Preferences();
		return self;
	}
	
	public static void loadPreferences(Context ctx) {
		loadPreferences(PreferenceManager.getDefaultSharedPreferences(ctx));
	}

	protected static void loadSharedPreference(SharedPreferences sharedPrefs, String key) {
		if (KEY_ENABLE_LOGGING.equals(key)) {
			ENABLE_LOGGING = sharedPrefs.getBoolean(key, true);
		}
		if (KEY_GET_NEAREST_INCLUDES_NON_FAVOURITES.equals(key)) {
			//app coded as name of constant indicates, but inverse easier to describe in settings!
			boolean limitToFavs = sharedPrefs.getBoolean(key, true);
			GET_NEAREST_INCLUDES_NON_FAVOURITES = !limitToFavs;
		}
		if (KEY_MW_FIRMWARE_VERSION.equals(key)) {
			String token = sharedPrefs.getString(key, Firmware.PRODUCTION.toString());
			if (Firmware.DEVELOPER.equals(token)) FW_VERSION = Firmware.DEVELOPER;
			else FW_VERSION = Firmware.PRODUCTION;
		}
		if (KEY_AUTO_REFRESH_BUS_TIMES.equals(key)) {
			AUTO_REFRESH_BUS_TIMES = sharedPrefs.getBoolean(key, true);
		}
		if (KEY_AUTO_REFRESH_RATE.equals(key)) {
			String token = sharedPrefs.getString(key, RefreshRate.SOURCE_DEFAULT.toString());
			AUTO_REFRESH_RATE = RefreshRate.valueOf(token);
			if (null == AUTO_REFRESH_RATE) AUTO_REFRESH_RATE = RefreshRate.SOURCE_DEFAULT;
		}
		if (KEY_REMEMBER_LAST_MAP_POS.equals(key)) {
			REMEMBER_LAST_MAP_POSITION = sharedPrefs.getBoolean(key, true);
		}
		if (KEY_ENABLE_LOGGING_TO_FILE.equals(key)) {
			ENABLE_LOGFILE = sharedPrefs.getBoolean(key, false);
		}
		if (KEY_LOGFILE_LOCATION.equals(key)) {
			DEBUG_LOGFILE_PATH = sharedPrefs.getString(key, "");
			if (null == DEBUG_LOGFILE_PATH || "".equals(DEBUG_LOGFILE_PATH.trim())) ENABLE_LOGFILE = false;
		}
		if (KEY_USE_GPS_TO_SELECT_LOCATION.equals(key)) {
			USE_GPS_TO_SELECT_LOCATION = sharedPrefs.getBoolean(key, true);
		}
	}

	public static void loadPreferences(SharedPreferences sharedPrefs) {
		loadSharedPreference(sharedPrefs, KEY_ENABLE_LOGGING);
		loadSharedPreference(sharedPrefs, KEY_GET_NEAREST_INCLUDES_NON_FAVOURITES);
		loadSharedPreference(sharedPrefs, KEY_MW_FIRMWARE_VERSION);
		loadSharedPreference(sharedPrefs, KEY_AUTO_REFRESH_BUS_TIMES);
		loadSharedPreference(sharedPrefs, KEY_AUTO_REFRESH_RATE);
		loadSharedPreference(sharedPrefs, KEY_REMEMBER_LAST_MAP_POS);
		loadSharedPreference(sharedPrefs, KEY_ENABLE_LOGGING_TO_FILE);
		loadSharedPreference(sharedPrefs, KEY_LOGFILE_LOCATION);
		loadSharedPreference(sharedPrefs, KEY_USE_GPS_TO_SELECT_LOCATION);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		loadSharedPreference(arg0, arg1);
	}
}
