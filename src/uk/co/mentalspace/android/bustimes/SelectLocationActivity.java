package uk.co.mentalspace.android.bustimes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import uk.co.mentalspace.android.bustimes.db.LocationsDBAdapter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;
import android.os.Bundle;

public class SelectLocationActivity extends FragmentActivity implements OnCameraChangeListener, OnMyLocationChangeListener {
	private static final String LOGNAME = "SelectLocationActivity";
	private static final float MARKER_MAX_ZOOM_LEVEL = 14.0f;
	private static final float DEFAULT_ZOOM_LEVEL = 15.5f;
	
	private List<Marker> markers = new ArrayList<Marker>();
	
	/**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;
    private LocationsDBAdapter ldba = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_location);
				
        setUpMapIfNeeded();
        ldba = new LocationsDBAdapter(this);
        ldba.open();
	}

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (null == ldba) {
        	ldba = new LocationsDBAdapter(this);
        	ldba.open();
        }
    }
    
    @Override
    protected void onPause() {
    	if (null != ldba) {
    		ldba.close();
    		ldba = null;
    	}
    	super.onPause();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not have been
     * completely destroyed during this process (it is likely that it would only be stopped or
     * paused), {@link #onCreate(Bundle)} may not be called again so we should call this method in
     * {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.locationSelectionMap)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
    	mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    	mMap.setMyLocationEnabled(false);
    	mMap.getUiSettings().setMyLocationButtonEnabled(false); //hide the stock button
    	mMap.getUiSettings().setAllGesturesEnabled(true);
    	mMap.getUiSettings().setZoomControlsEnabled(false);
    	mMap.getUiSettings().setCompassEnabled(true);

    	//listen for camera changes, and use it to draw stop locations
    	mMap.setOnCameraChangeListener(this);
    	mMap.setOnMyLocationChangeListener(this);
    }
    
    public void onCameraChange(CameraPosition position) {
    	Log.d(LOGNAME, "handling camera change");
    	if (null == mMap) return;

    	//remove all markers (will re-add visible ones once retrieved
    	Log.d(LOGNAME, "removing existing markers");
    	mMap.clear();
    	markers.clear();
    	
    	//if too zoomed out, don't display any markers (otherwise map will be cluttered with 1000's!
    	if (position.zoom < MARKER_MAX_ZOOM_LEVEL) {
    		Log.d(LOGNAME, "Zoomed out too far - not adding new markers");
    		return;
    	}
    	
        LatLng tl = mMap.getProjection().getVisibleRegion().farLeft;
        LatLng br = mMap.getProjection().getVisibleRegion().nearRight;
        Log.d(LOGNAME, "Getting locations in box tl ["+tl.latitude+","+br.longitude+"], br ["+br.latitude+", "+tl.longitude+"]");
        List<Location> locations = ldba.getLocationsInArea((int)(tl.latitude*10000), (int)(br.longitude*10000), (int)(br.latitude*10000), (int)(tl.longitude*10000));
        if (null == locations) {
        	Log.d(LOGNAME, "null list of locations returned - skipped adding to map");
        	return;
        }
        
        Log.d(LOGNAME, "Number Locations found: "+locations.size());
        Iterator<Location> iter = locations.iterator();
        while (iter.hasNext()) {
        	Location loc = iter.next();
        	LatLng ll = new LatLng(((double)loc.getLat())/10000,((double)loc.getLon())/10000);
        	Marker marker = mMap.addMarker(new MarkerOptions().position(ll).title(loc.getLocationName()).snippet(loc.getStopCode()));
        	markers.add(marker);
        }
    }

	@Override
	public void onMyLocationChange(android.location.Location arg0) {
    	if (null != mMap) {
        	LatLng userLatLng = new LatLng(arg0.getLatitude(), arg0.getLongitude());    	
    		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, DEFAULT_ZOOM_LEVEL));
    	}
	}
	
	public void onToggleGPSClicked(View view) {
		if (null == mMap) return; //map not loaded - ignore button actions
		
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
    	mMap.setMyLocationEnabled(on);
	}
	
	public void onFindStopIdClicked(View view) {
		Log.d(LOGNAME, "Find Stop button clicked");
		EditText stopField = (EditText)this.findViewById(R.id.select_location_stop_code_filter);
		String stopCode = stopField.getText().toString();
		Log.d(LOGNAME, "Locating stop for code: "+stopCode);
		
		LocationsDBAdapter ldba = new LocationsDBAdapter(this);
		try {
			ldba.open();
			Location loc = ldba.getLocationByStopCode(stopCode);
			if (null != loc) {
				Log.d(LOGNAME, "Location found: "+loc.getLocationName());
	        	LatLng ll = new LatLng(((double)loc.getLat())/10000,((double)loc.getLon())/10000);
	    		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, DEFAULT_ZOOM_LEVEL));
			} else {
				Log.d(LOGNAME, "No location found");
			}
		} finally {
			if (null != ldba) ldba.close();
		}
	}
}
