package uk.co.mentalspace.android.bustimes.utils;

import uk.co.mentalspace.android.bustimes.LocationManager;
import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.R;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

public class LocationPopupWindow implements OnClickListener {
	private static final String LOGNAME = "LocationPopupWindow";
	private PopupWindow popup = null;
	private Context ctx = null;
	
	public LocationPopupWindow(Activity act, Context ctx, Location loc) {
		this.ctx = ctx;
		
    	View popupView = act.getLayoutInflater().inflate(R.layout.map_info_window_popup, null);
    	((TextView)popupView.findViewById(R.id.map_info_window_stop_code_value)).setText(loc.getStopCode());
    	((TextView)popupView.findViewById(R.id.map_info_window_stop_name_value)).setText(loc.getLocationName());
    	((EditText)popupView.findViewById(R.id.map_info_window_nick_name_value)).setText(loc.getNickName());
    	Button chosenButton = ((Button)popupView.findViewById(R.id.map_info_window_monitored_button)); 
    	chosenButton.setOnClickListener(this);
    	if (loc.getChosen() == 1) {
    		chosenButton.setText(R.string.map_info_window_monitored_on);
    	} else {
    		chosenButton.setText(R.string.map_info_window_monitored_off);
    	}
    	Button dismissButton = ((Button)popupView.findViewById(R.id.map_info_window_dismiss_button));
    	dismissButton.setOnClickListener(this);
    	
    	popup = new PopupWindow(popupView, act.getWindow().getAttributes().width, act.getWindow().getAttributes().height, true);
    	popup.setHeight(popup.getMaxAvailableHeight(act.getCurrentFocus()));
    	popup.update();
    	popup.showAtLocation(act.getCurrentFocus(), Gravity.BOTTOM, 10, 10);
    	popup.update();
    	Log.d(LOGNAME, "Displaying popup window");
	}
	
	public void onDismissPopupClicked(View view) {
		if (null != popup) {
			Log.d(LOGNAME, "Dismissing popup window");
			
			//check for change to nick name
			String stopCode = ((TextView)popup.getContentView().findViewById(R.id.map_info_window_stop_code_value)).getText().toString();
			if (null != stopCode && !"".equals(stopCode.trim())) {
				Location loc = LocationManager.getLocationByStopCode(ctx, stopCode);
				if (null != loc) {
					String nickName = ((EditText)popup.getContentView().findViewById(R.id.map_info_window_nick_name_value)).getText().toString();
					if (nickName != null && !nickName.equals(loc.getNickName())) {
						LocationManager.updateNickName(ctx, loc.getId(), nickName);
					}
				}
			}
			
			popup.dismiss();
			popup = null;
		}
	}
	
	public void onMonitorLocationClicked(View view) {
		Log.d(LOGNAME, "Monitor button clicked");
		String stopCode = ((TextView)popup.getContentView().findViewById(R.id.map_info_window_stop_code_value)).getText().toString();
		if (null == stopCode || "".equals(stopCode.trim())) return;
		
		Location loc = LocationManager.getLocationByStopCode(ctx, stopCode);
		if (null == loc) return;
		
		if (loc.getChosen() == 1) {
			LocationManager.deselectLocation(ctx, loc.getId());
    		((Button)view).setText(R.string.map_info_window_monitored_off);
    	} else {
    		((Button)view).setText(R.string.map_info_window_monitored_on);
			LocationManager.selectLocation(ctx, loc.getId());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.map_info_window_dismiss_button:
			onDismissPopupClicked(v);
			return;
		case R.id.map_info_window_monitored_button:
			onMonitorLocationClicked(v);
			return;
		}
	}

}
