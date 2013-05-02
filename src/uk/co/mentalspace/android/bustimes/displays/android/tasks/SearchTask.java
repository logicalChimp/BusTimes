package uk.co.mentalspace.android.bustimes.displays.android.tasks;

import java.util.Locale;
import java.util.List;

import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.displays.android.OnPointFoundListener;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

public class SearchTask extends AsyncTask<Void, Void, Boolean> {
	private static final String LOGNAME = "SearchClicked";
    private String toSearch;
    private Address address;
    private Context ctx;
    private OnPointFoundListener opfl;
    private LatLng point = null;

    public SearchTask(Context ctx, String toSearch, OnPointFoundListener opfl) {
        this.toSearch = toSearch;
        this.ctx = ctx;
        this.opfl = opfl;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {
            Geocoder geocoder = new Geocoder(ctx, Locale.UK);
            List<Address> results = geocoder.getFromLocationName(toSearch, 1);

            if (results.size() == 0) {
                return false;
            }

            address = results.get(0);

            // Now do something with this address:
            point = new LatLng(address.getLatitude(), address.getLongitude());

        } catch (Exception e) {
        	if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "Something went wrong: ", e);
            return false;
        }
        return true;
    }
    
    @Override
    protected void onPostExecute(Boolean result) {
		if (null == opfl) {
			if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "Null OPFL - Cannot report results!");
			return;
		}
		
		try {
			opfl.onPointFound(point);
		} catch (Exception e) {
			if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "Unknown exception reporting found point", e);
		}
    }
}