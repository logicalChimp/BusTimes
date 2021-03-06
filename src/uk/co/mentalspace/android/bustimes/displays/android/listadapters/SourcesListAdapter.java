package uk.co.mentalspace.android.bustimes.displays.android.listadapters;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.R;
import uk.co.mentalspace.android.bustimes.Source;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class SourcesListAdapter extends ArrayAdapter<Source> {
	private static final String LOGNAME = "SourcesListAdapter";
	private Context ctx = null;
	private Source[] srcs = null;
	
	public SourcesListAdapter(Context context, Source[] sources) {
		super(context, R.layout.row_layout_sources_list, sources);
		this.ctx = context;
		this.srcs = sources;
	}
	
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}
	
	protected View getCustomView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_layout_sources_list,  parent, false);
		
		Source src = srcs[position];
		if (null == src) {
			src = new Source("", "", 0, "", "", "", false);
		}
		
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "source: "+src);
		((TextView)rowView.findViewById(R.id.sources_list_row_name)).setText(src.getName());
		
		TextView rttv = ((TextView)rowView.findViewById(R.id.sources_list_row_refresh_time));
		if (0 >= src.getLastRefreshTimestamp()) {
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Source has no refresh time ["+src.getLastRefreshTimestamp()+"]");
			rttv.setText("");
		} else {
			DateFormat simpleDateFormat = SimpleDateFormat.getDateTimeInstance();
			Date refreshTime = new Date();
			refreshTime.setTime(src.getLastRefreshTimestamp());
			String dateAsString = simpleDateFormat.format(refreshTime);
			rttv.setText("Last refreshed: "+dateAsString);
		}
		return rowView;
	}
}
