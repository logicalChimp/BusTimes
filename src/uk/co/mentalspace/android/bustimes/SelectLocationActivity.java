package uk.co.mentalspace.android.bustimes;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

public class SelectLocationActivity extends FragmentActivity implements OnCameraChangeListener, OnMyLocationChangeListener, OnInfoWindowClickListener {
	private static final String LOGNAME = "SelectLocationActivity";
	private static final float MARKER_MAX_ZOOM_LEVEL = 15.0f;
	private static final float DEFAULT_ZOOM_LEVEL = 16.0f;
	
	private HashMap<Location,Marker> markers = new HashMap<Location,Marker>();
	
	/**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;
    private boolean mapTracksUserPos = false;
    private LocationTracker posTracker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_location);
				
        setUpMapIfNeeded();
	}

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
    
    @Override
    protected void onPause() {
    	if (null != posTracker) {
    		posTracker.stopTrackingLocation();
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
    	mMap.setMyLocationEnabled(true);
//    	mMap.getUiSettings().setMyLocationButtonEnabled(false); //hide the stock button
    	mMap.getUiSettings().setAllGesturesEnabled(true);
    	mMap.getUiSettings().setZoomControlsEnabled(false);
    	mMap.getUiSettings().setCompassEnabled(true);

    	//listen for camera changes, and use it to draw stop locations
    	mMap.setOnCameraChangeListener(this);
    	mMap.setOnMyLocationChangeListener(this);
    	mMap.setOnInfoWindowClickListener(this);
    	
//    	posTracker = new LocationTracker(this);
//    	if (posTracker.canGetLocation()) {
//	    	LatLng userLatLng = new LatLng(posTracker.getLatitude(), posTracker.getLongitude());    	
//			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, DEFAULT_ZOOM_LEVEL));
//    	}
    }
    
    public void onCameraChange(CameraPosition position) {
    	Log.d(LOGNAME, "handling camera change");
    	if (null == mMap) return;

    	//remove all markers (will re-add visible ones once retrieved
    	Log.d(LOGNAME, "removing existing markers");
    	
    	//if too zoomed out, don't display any markers (otherwise map will be cluttered with 1000's!
    	if (position.zoom < MARKER_MAX_ZOOM_LEVEL) {
    		mMap.clear();
    		markers.clear();
    		Log.d(LOGNAME, "Zoomed out too far - not adding new markers");
    		return;
    	}
    	
        LatLng tl = mMap.getProjection().getVisibleRegion().farLeft;
        LatLng br = mMap.getProjection().getVisibleRegion().nearRight;
        Log.d(LOGNAME, "Getting locations in box tl ["+tl.latitude+","+br.longitude+"], br ["+br.latitude+", "+tl.longitude+"]");
        List<Location> locations = LocationManager.getLocationsInArea(this, (int)(tl.latitude*10000), (int)(br.longitude*10000), (int)(br.latitude*10000), (int)(tl.longitude*10000));
        if (null == locations) {
        	Log.i(LOGNAME, "null list of locations returned - initialising an empty list");
        	locations = new ArrayList<Location>();
        }
        Log.d(LOGNAME, "Number Locations found: "+locations.size());
        
        //remove all the markers that are no longer visible, without disturbing the remaining markers
        List<Location> toRemove = new ArrayList<Location>();
        toRemove.addAll(markers.keySet());
        toRemove.removeAll(locations);
        Log.d(LOGNAME, "Current marker count ["+markers.size()+"], to remove ["+toRemove.size()+"]");
        for (Location loc: toRemove) {
        	removeMarker(loc);
        }

        //identify all *new* locations that need to be displayed
        List<Location> reference = new ArrayList<Location>();
        reference.addAll(markers.keySet());
        reference.retainAll(locations);
        Log.d(LOGNAME, "Current marker count ["+markers.size()+"], to add ["+(locations.size() - reference.size())+"]");
        for (Location loc: locations) {
        	Log.d(LOGNAME, "Location ["+loc.getStopCode()+"] is already on map: "+reference.contains(loc));
        	if (!reference.contains(loc)) {
        		Log.d(LOGNAME, "...adding marker");
        		addMarker(loc);        	
        	}
        }
        
        Log.d(LOGNAME, "Total marker count: "+markers.size());
    }
    
    private void removeMarker(Location loc) {
    	if (markers.containsKey(loc)) {
    		markers.get(loc).remove();
        	markers.remove(loc);
    	}
    }
    
    private void addMarker(Location loc) {
     	MarkerOptions mo = getMarkerOptions(loc);
    	Marker marker = mMap.addMarker(mo);
    	markers.put(loc, marker);
    }
    
    private MarkerOptions getMarkerOptions(Location loc) {
    	LatLng ll = new LatLng(((double)loc.getLat())/10000,((double)loc.getLon())/10000);
    	MarkerOptions mo = new MarkerOptions().position(ll).title(loc.getLocationName()).snippet(loc.getStopCode());
    	if (loc.getChosen() == 1) mo.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
    	return mo;
    }

	@Override
	public void onMyLocationChange(android.location.Location arg0) {
    	if (null != mMap && mapTracksUserPos) {
        	LatLng userLatLng = new LatLng(arg0.getLatitude(), arg0.getLongitude());    	
    		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, DEFAULT_ZOOM_LEVEL));
    	}
	}
	
	public void onToggleGPSClicked(View view) {
		if (null == mMap) return; //map not loaded - ignore button actions
		
	    // Is the toggle on?
	    mapTracksUserPos = ((ToggleButton) view).isChecked();
//	    Log.d(LOGNAME, "Gps is toggled on: "+on);
//    	mMap.setMyLocationEnabled(on);
	}
	
	public void onFindStopCodeClicked(View view) {
		Log.d(LOGNAME, "Find Stop button clicked");
		EditText stopField = (EditText)this.findViewById(R.id.select_location_stop_code_filter);
		String stopCode = stopField.getText().toString();
		Log.d(LOGNAME, "Locating stop for code: "+stopCode);

		Location loc = LocationManager.getLocationByStopCode(this, stopCode);
		if (null != loc) {
			Log.d(LOGNAME, "Location found: "+loc.getLocationName());
        	LatLng ll = new LatLng(((double)loc.getLat())/10000,((double)loc.getLon())/10000);
    		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, DEFAULT_ZOOM_LEVEL));
		} else {
			Log.d(LOGNAME, "No location found");
		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
    	String stopCode = marker.getSnippet().trim();
		Log.d(LOGNAME, "Info Window clicked. Stop Code: "+stopCode);
    	Location loc = LocationManager.getLocationByStopCode(this, stopCode);
    	if (null == loc) {
    		Log.w(LOGNAME, "Failed to retrieve Location for stopCode");
    		return;
    	}
    	
//    	if (loc.getChosen() == 1) LocationManager.deselectLocation(this, loc.getId());
//    	else LocationManager.selectLocation(this, loc.getId());
//    	
//    	//invert the current 'chosen' flag
//    	loc.setChosen(1-loc.getChosen());
//    	
//    	//remove the old (wrong coloured) marker from the map
//    	removeMarker(loc);
//    	
//    	//and replace it with a fresh (right coloured) marker
//    	addMarker(loc);
//
//    	//trigger re-display of marker window, with 'chosen' indicator updated
////    	newMarker.showInfoWindow();
    	
//    	View popupView = getLayoutInflater().inflate(R.layout.map_info_window_popup, null);
//    	((TextView)popupView.findViewById(R.id.map_info_window_stop_code_value)).setText(loc.getStopCode());
//    	((TextView)popupView.findViewById(R.id.map_info_window_stop_name_value)).setText(loc.getLocationName());
//    	((EditText)popupView.findViewById(R.id.map_info_window_nick_name_value)).setText(loc.getNickName());
//    	Button chosenButton = ((Button)popupView.findViewById(R.id.map_info_window_monitored_button)); 
//    	chosenButton.setOnClickListener(this);
//    	if (loc.getChosen() == 1) {
//    		chosenButton.setText(R.string.map_info_window_monitored_on);
//    	} else {
//    		chosenButton.setText(R.string.map_info_window_monitored_off);
//    	}
//    	Button dismissButton = ((Button)popupView.findViewById(R.id.map_info_window_dismiss_button));
//    	dismissButton.setOnClickListener(this);
//    	
//    	popup = new PopupWindow(popupView, this.getWindow().getAttributes().width, this.getWindow().getAttributes().height, true);
////    	popup.setContentView(popupView);
//    	popup.setHeight(popup.getMaxAvailableHeight(this.getCurrentFocus()));
//    	popup.update();
//    	popup.showAtLocation(this.getCurrentFocus(), Gravity.BOTTOM, 10, 10);
//    	popup.update();
//    	Log.d(LOGNAME, "Displaying popup window");
    	new LocationPopupWindow(this, this, loc);
	}

}
