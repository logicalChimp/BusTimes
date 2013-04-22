package uk.co.mentalspace.android.bustimes.displays.android.listadapters;

import uk.co.mentalspace.android.bustimes.R;
import uk.co.mentalspace.android.bustimes.BusTime;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class BusTimeListAdapter extends ArrayAdapter<BusTime> {

	private final Context ctx;
	private final BusTime[] busTimes;
	
	public BusTimeListAdapter(Context context, BusTime[] times) {
		super(context, R.layout.row_layout_bus_times, times);
		ctx = context;
		busTimes = times;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_layout_bus_times,  parent, false);
		
		BusTime bt = busTimes[position];
//		((TextView)rowView.findViewById(R.id.rowLineNumber)).setText(String.valueOf(position+1));
		((TextView)rowView.findViewById(R.id.rowBusNumber)).setText(bt.getBusNumber());
		((TextView)rowView.findViewById(R.id.rowBusDestination)).setText(bt.getDestination());
		
		String due = bt.getEstimatedArrivalTime();
		try {
			Integer.parseInt(due);
//			due += " mins";
		} catch (NumberFormatException nfe) {
			//do nothing - just testing if a numeric value was supplied
		}
		((TextView)rowView.findViewById(R.id.rowBusExpected)).setText(due);
		
		return rowView;
	}
}
