package uk.co.mentalspace.android.bustimes;

public class Preferences {

	public static enum Firmware {DEVELOPER, PRODUCTION};
	public static enum RefreshRate {SOURCE_DEFAULT, THIRTY_SECONDS, SIXTY_SECONDS};
	
	public static boolean ENABLE_LOGGING = true;
	public static boolean GET_NEAREST_INCLUDES_NON_FAVOURITES = true;
	public static Firmware FW_VERSION = Firmware.PRODUCTION;
	public static boolean AUTO_REFRESH_BUS_TIMES = true;
	public static RefreshRate AUTO_REFRESH_RATE = RefreshRate.SOURCE_DEFAULT;
	public static boolean REMEMBER_LAST_MAP_POSITION = true;
	public static boolean ENABLE_LOGFILE = false;
	public static String DEBUG_LOGFILE_PATH = "";
	public static boolean USE_GPS_TO_SELECT_LOCATION = true;
}
