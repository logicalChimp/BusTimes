package uk.co.mentalspace.android.bustimes;

import uk.co.mentalspace.android.bustimes.locators.PreferredLocationLocator;

public class LocatorManager {

	public static Locator getLocator(String id) {
		return new PreferredLocationLocator();
	}
}
