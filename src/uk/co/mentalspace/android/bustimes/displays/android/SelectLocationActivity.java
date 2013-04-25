package uk.co.mentalspace.android.bustimes.displays.android;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.LocationManager;
import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.R;
import uk.co.mentalspace.android.bustimes.displays.android.popups.EditLocationPopup;
import uk.co.mentalspace.android.bustimes.displays.android.popups.FindLocationPopup;
import uk.co.mentalspace.android.utils.LocationTracker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class SelectLocationActivity extends FragmentActivity implements OnCameraChangeListener, OnMyLocationChangeListener, OnMarkerClickListener, OnPointFoundListener {
	private static final String LOGNAME = "SelectLocationActivity";
	private static final float MARKER_MAX_ZOOM_LEVEL = 15.0f;
	private static final float DEFAULT_ZOOM_LEVEL = 16.0f;
	
	public static final String ACTION_SHOW_LOC_ON_MAP = "showOnMap";
	public static final String EXTRA_LOCATION = "location";

	private static final String DIALOG_ID_FIND_LOCATION = "FindLocationDialog";
	private static final String DIALOG_ID_EDIT_LOCATION = "EditLocationDialog";
	
	private HashMap<Location,Marker> markers = new HashMap<Location,Marker>();
	private HashMap<Marker,Location> locations = new HashMap<Marker,Location>();
	private ProgressDialog progressIndicator = null;
	
	/**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;
    private boolean mapTracksUserPos = false;
    private LocationTracker posTracker;
    private EditLocationPopup elp = null;
    private FindLocationPopup flp = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_location);
	}

    @Override
    protected void onResume() {
    	if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "resuming...");
        super.onResume();
        setUpMapIfNeeded();
        
        Intent intent = this.getIntent();
        if (ACTION_SHOW_LOC_ON_MAP.equals(intent.getAction())) {
        	if (intent.hasExtra(EXTRA_LOCATION)) {
        		Location loc = (Location)intent.getSerializableExtra(EXTRA_LOCATION);
        		if (null != loc) {
                	LatLng ll = loc.getLatLng();
            		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, DEFAULT_ZOOM_LEVEL));
        		}
        	}
        }
    }
    
	@Override
    protected void onPause() {
    	if (null != posTracker) {
    		posTracker.stopTrackingLocation();
    	}
    	super.onPause();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_select_locations, menu);
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Handling key-up.  keyCode ["+keyCode+"], keyEvent ["+event+"]");
		switch (keyCode) {
		case KeyEvent.KEYCODE_SEARCH:
			showSearch(true);
			return true;
		default:
			return super.onKeyUp(keyCode, event);
		}
	}
	
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.menu_search_location:
    		showSearch(true);
    		return true;
    	default:
    		return false;
    	}
    }
    
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void showSearch(final boolean show) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		flp = FindLocationPopup.newInstance();
		
		final SelectLocationActivity self = this;
		
		//show dialog
		flp.show(fragmentManager, DIALOG_ID_FIND_LOCATION);
		fragmentManager.executePendingTransactions();
		flp.getDialog().setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				self.onDismiss(arg0, DIALOG_ID_FIND_LOCATION);
			}
		});
		
		WindowManager.LayoutParams p = flp.getDialog().getWindow().getAttributes();

		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			p.y = getActionBar().getHeight(); //findViewById(R.id.locationSelectionMap).getTop();
		} else {
			p.y = 50; //getActionBar().getHeight(); //findViewById(R.id.locationSelectionMap).getTop();
		}		
		flp.getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
		flp.getDialog().getWindow().setAttributes(p);
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
    	mMap.setOnMarkerClickListener(this);
    }
    
    private void runSearch(String toFind) {
    	if (null == toFind || "".equals(toFind.trim())) return;
    	
		Location loc = LocationManager.getLocationByStopCode(this, toFind);
		if (null != loc) {
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Location found: "+loc.getLocationName());
        	LatLng ll = new LatLng(((double)loc.getLat())/10000,((double)loc.getLon())/10000);
    		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, DEFAULT_ZOOM_LEVEL));
		} else {
			//not a stop code entered - do a general google search
		    new SearchTask(this, toFind, this).execute();
	    
	        progressIndicator = new ProgressDialog(this);
	        progressIndicator.setMessage("Searching...");
	        progressIndicator.show();
	    }
    }
    
    public void onCameraChange(CameraPosition position) {
    	if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "handling camera change");
    	if (null == mMap) return;

    	//remove all markers (will re-add visible ones once retrieved
    	if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "removing existing markers");
    	
    	//if too zoomed out, don't display any markers (otherwise map will be cluttered with 1000's!
    	if (position.zoom < MARKER_MAX_ZOOM_LEVEL) {
    		mMap.clear();
    		markers.clear();
    		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Zoomed out too far - not adding new markers");
    		return;
    	}
    	
        LatLng tl = mMap.getProjection().getVisibleRegion().farLeft;
        LatLng br = mMap.getProjection().getVisibleRegion().nearRight;
        if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Getting locations in box tl ["+tl.latitude+","+br.longitude+"], br ["+br.latitude+", "+tl.longitude+"]");
        List<Location> locations = LocationManager.getLocationsInArea(this, (int)(tl.latitude*10000), (int)(br.longitude*10000), (int)(br.latitude*10000), (int)(tl.longitude*10000));
        if (null == locations) {
        	if (Preferences.ENABLE_LOGGING) Log.i(LOGNAME, "null list of locations returned - initialising an empty list");
        	locations = new ArrayList<Location>();
        }
        if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Number Locations found: "+locations.size());
        
        //remove all the markers that are no longer visible, without disturbing the remaining markers
        List<Location> toRemove = new ArrayList<Location>();
        toRemove.addAll(markers.keySet());
        toRemove.removeAll(locations);
        if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Current marker count ["+markers.size()+"], to remove ["+toRemove.size()+"]");
        for (Location loc: toRemove) {
        	removeMarker(loc);
        }

        //identify all *new* locations that need to be displayed
        List<Location> reference = new ArrayList<Location>();
        reference.addAll(markers.keySet());
        reference.retainAll(locations);
        if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Current marker count ["+markers.size()+"], to add ["+(locations.size() - reference.size())+"]");
        for (Location loc: locations) {
        	if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Location ["+loc.getStopCode()+"] is already on map: "+reference.contains(loc));
        	if (!reference.contains(loc)) {
        		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "...adding marker");
        		addMarker(loc);        	
        	}
        }
        
        if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Total marker count: "+markers.size());
    }
    
    private void removeMarker(Location loc) {
    	if (markers.containsKey(loc)) {
    		markers.get(loc).remove();
    		Marker marker = markers.get(loc);
        	markers.remove(loc);
        	locations.remove(marker);
    	}
    }
    
    private void addMarker(Location loc) {
     	MarkerOptions mo = getMarkerOptions(loc);
    	Marker marker = mMap.addMarker(mo);
    	markers.put(loc, marker);
    	locations.put(marker,loc);
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
	
	@Override
	public boolean onMarkerClick(Marker arg0) {
		Location loc = locations.get(arg0);

		//center the map on the marker that was clicked
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(arg0.getPosition(), DEFAULT_ZOOM_LEVEL));

		FragmentManager fragmentManager = getSupportFragmentManager();
		elp = EditLocationPopup.newInstance(loc);
		
		final SelectLocationActivity self = this;
		
		//show dialog
		elp.show(fragmentManager, DIALOG_ID_EDIT_LOCATION);
		fragmentManager.executePendingTransactions();
		elp.getDialog().setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface arg0) {
				self.onDismiss(arg0, DIALOG_ID_EDIT_LOCATION);
			}
		});

		//return true to indicate we have handled the event
		return true;
	}

	public void onDismiss(DialogInterface arg0, String id) {
		if (DIALOG_ID_EDIT_LOCATION.equals(id)) {
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Dialog dismissed - removing edited marker");
			Location loc = elp.getLocation();
			Marker marker = markers.get(loc);
			markers.remove(loc);
			locations.remove(marker);
			
			elp = null;
	
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Removed edited marker - refreshing map");
			//force map to redraw for the same position
	        onCameraChange(mMap.getCameraPosition());
		}
		if (DIALOG_ID_FIND_LOCATION.equals(id)) {
			if (null == flp) return;
			String toFind = flp.getSearchTerm();
			if (null == toFind || "".equals(toFind.trim())) return;
			
			runSearch(toFind);
		}
	}

	@Override
	public void onPointFound(LatLng ll) {
        if (null != progressIndicator) progressIndicator.dismiss();

        if (null == ll) {
        	Toast.makeText(this, "No matches", Toast.LENGTH_SHORT).show();
        	return;
        }
    	if (null != mMap) {
    		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, DEFAULT_ZOOM_LEVEL));
    	}
	}

}
