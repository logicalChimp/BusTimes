package uk.co.mentalspace.android.bustimes;

import java.util.List;
import android.os.AsyncTask;

public abstract class DataRefreshTask extends AsyncTask<Void, Void, List<BusTime>> {

	protected Renderer display = null;
	protected Location location = null;
	protected Exception failure = null;
	
	public void init(Renderer taskDisplay, Location taskLocation) {
		display = taskDisplay;
		location = taskLocation;
	}
	
	public Exception getFailure() {
		return failure;
	}
	

	protected void onPostExecute(List<BusTime> busTimes) {
		if (null != busTimes) {
			Coordinator.updateBusTimes(display, location, busTimes);
		}
	}

	public abstract void executeSync();
	protected abstract List<BusTime> doInBackground(Void... strings);

}
