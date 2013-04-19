package uk.co.mentalspace.android.bustimes.displays.android;

import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.LocationManager;
import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

public class EditLocationPopup extends DialogFragment implements OnClickListener {
	private static final String LOGNAME = "EditLocationPopup";
	private Location loc = null;
	
	public static final String BUNDLE_LOCATION = "location";
	
	public EditLocationPopup() {
		super();
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public static EditLocationPopup newInstance(Location loc) {
		Bundle args = new Bundle();
		args.putSerializable(EditLocationPopup.BUNDLE_LOCATION, loc);
		
		EditLocationPopup elp = new EditLocationPopup();
		elp.setArguments(args);

		return elp;
	}
	
	private View getPopupView(LayoutInflater inflater) {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Creating popup window view");
		View popupView = inflater.inflate(R.layout.edit_location_popup, null);
		if (null == popupView) if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "popupView is NULL");
		if (null == loc) if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "Location is NULL");
		
		if (null != popupView && null != loc) {
	    	((TextView)popupView.findViewById(R.id.map_info_window_stop_name_value)).setText(loc.getLocationName());
	    	((EditText)popupView.findViewById(R.id.map_info_window_nick_name_value)).setText(loc.getNickName());
	    	ToggleButton btn = ((ToggleButton)popupView.findViewById(R.id.map_info_window_monitored_button));
	    	btn.setChecked(loc.getChosen() == 1);
	    	btn.setOnClickListener(this);
		}
    	return popupView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loc = (Location)this.getArguments().getSerializable(BUNDLE_LOCATION);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Creating edit location as a dialog");
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View popupView = getPopupView(inflater);
		builder.setView(popupView)
			.setTitle("Edit Location")
			.setNeutralButton("Close", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int id) {
					if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Dismiss button clicked");
					checkValuesAndDismiss();
				}
			});
		
		return builder.create();
	}
	
	public void checkValuesAndDismiss() {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "checking Nick Name before dismissing");
		EditText et = ((EditText)getDialog().findViewById(R.id.map_info_window_nick_name_value));
		String nickName = (null == et.getText()) ? "" : et.getText().toString();
		if (nickName != null && !nickName.equals(loc.getNickName())) {
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Updating nickname ["+nickName+"] for stop code ["+loc.getStopCode()+"]");
			LocationManager.updateNickName(getActivity(), loc.getId(), nickName);
		} else {
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Not updating nickname ["+nickName+"] for stop code ["+loc.getStopCode()+"]");
		}
		EditLocationPopup.this.getDialog().dismiss();
	}

	public void onMonitorLocationClicked(View view) {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Monitor button toggled");
		ToggleButton btn = (ToggleButton)view;
		
		if (btn.isChecked()) {
			LocationManager.selectLocation(getActivity(), loc.getId());
    	} else {
			LocationManager.deselectLocation(getActivity(), loc.getId());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.map_info_window_monitored_button:
			onMonitorLocationClicked(v);
			return;
		}
	}
}
