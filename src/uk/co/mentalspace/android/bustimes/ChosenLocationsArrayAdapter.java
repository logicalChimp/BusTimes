package uk.co.mentalspace.android.bustimes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChosenLocationsArrayAdapter extends ArrayAdapter<Location> {

	private final Context ctx;
	private final Location[] selectedLocations;
	private static final String LOGNAME = "ChosenLocationArrayAdapter";
	
	public ChosenLocationsArrayAdapter(Context context, Location[] selectedLocations) {
		super(context, R.layout.chosen_location_row_layout, selectedLocations);
		ctx = context;
		this.selectedLocations = selectedLocations;
	}
	
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}
	
	protected View getCustomView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.chosen_location_row_layout,  parent, false);
		
		Location loc = selectedLocations[position];
		if (null == loc) {
			loc = new Location("", "", "", "", "", "", 0,0);
		}
		
		Log.d(LOGNAME, "Selected location: "+loc);
		((TextView)rowView.findViewById(R.id.chosen_location_nick_name_label)).setText(loc.getNickName());
		((TextView)rowView.findViewById(R.id.chosen_location_stop_name_label)).setText(loc.getLocationName());
		((TextView)rowView.findViewById(R.id.chosen_location_stop_code_label)).setText(loc.getStopCode());
		
		return rowView;
	}
}
