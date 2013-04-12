package uk.co.mentalspace.android.bustimes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ChosenLocationsArrayAdapter extends ArrayAdapter<String> {

	private final Context ctx;
	private final String[] stopCodes;
	private final Locator locator;
	
	public ChosenLocationsArrayAdapter(Context context, String[] stopCodes, Locator locator) {
		super(context, R.layout.chosen_location_row_layout, stopCodes);
		ctx = context;
		this.stopCodes = stopCodes;
		this.locator = locator;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.chosen_location_row_layout,  parent, false);
		
		String stopCode = stopCodes[position];
		Location loc = locator.getLocation(ctx, stopCode);
		if (null == loc) {
			loc = new Location("", "", "", "", "", "", 0,0);
		}
		
		((TextView)rowView.findViewById(R.id.chosen_location_nick_name_label)).setText(loc.getNickName());
		((TextView)rowView.findViewById(R.id.chosen_location_stop_name_label)).setText(loc.getLocationName());
		((TextView)rowView.findViewById(R.id.chosen_location_stop_code_label)).setText(loc.getStopCode());
		
		return rowView;
	}
}
