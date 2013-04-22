package uk.co.mentalspace.android.bustimes.displays.android.popups;

import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FindLocationPopup extends DialogFragment implements OnClickListener {
	private static final String LOGNAME = "FindLocationPopup";

	private String searchTerm = null;
	
	public FindLocationPopup() {
		super();
	}
	
	public static FindLocationPopup newInstance() {
		Bundle args = new Bundle();
		
		FindLocationPopup flp = new FindLocationPopup();
		flp.setArguments(args);

		return flp;
	}
	
	private View getPopupView(LayoutInflater inflater) {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Creating popup window view");
		View popupView = inflater.inflate(R.layout.popup_find_location, null);
		if (null == popupView) if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "popupView is NULL");
		
		if (null != popupView) {
			((Button)popupView.findViewById(R.id.find_location_search_button)).setOnClickListener(this);
	    	final EditText mapSearchBox = (EditText)popupView.findViewById(R.id.find_location_textfield);
	    	mapSearchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
	            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	                 if (actionId == EditorInfo.IME_ACTION_SEARCH ||
	                        actionId == EditorInfo.IME_ACTION_DONE ||
	                        actionId == EditorInfo.IME_ACTION_GO ||
	                        event.getAction() == KeyEvent.ACTION_DOWN &&
	                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
	
	                    // hide virtual keyboard
	                	 runSearch(mapSearchBox);
	                    return true;
	                }
	                return false;
	            }
	        });
		}
	    
		return popupView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Creating find location as a dialog");
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View popupView = getPopupView(inflater);
		builder.setView(popupView);
		
		return builder.create();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.find_location_search_button:
			EditText tv = (EditText)getDialog().findViewById(R.id.find_location_textfield);
			runSearch(tv);
			return;
		default:
			return;
		}
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	private void runSearch(EditText mapSearchBox) {
        // hide virtual keyboard
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mapSearchBox.getWindowToken(), 0);

        searchTerm = mapSearchBox.getText().toString();
        dismiss();
//        String toFind = mapSearchBox.getText().toString();
//        
//        //hide search field - AFTER getting the search value!
//        dismiss(); 
//        
//		Location loc = LocationManager.getLocationByStopCode(getActivity(), toFind);
//		if (null != loc) {
//			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Location found: "+loc.getLocationName());			
//        	LatLng ll = new LatLng(((double)loc.getLat())/10000,((double)loc.getLon())/10000);
//        	opfl.onPointFound(ll);
//		} else {
//			//not a stop code entered - do a general google search
//		    new SearchTask(getActivity(), toFind, opfl).execute();
//	        mapSearchBox.setText("", TextView.BufferType.EDITABLE);
////	    
////	        progressIndicator = new ProgressDialog(this);
////	        progressIndicator.setMessage("Searching...");
////	        progressIndicator.show();
//	    }
    }
}
