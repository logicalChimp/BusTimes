package uk.co.mentalspace.android.bustimes.displays.android;

import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.LocationManager;
import uk.co.mentalspace.android.bustimes.Source;
import uk.co.mentalspace.android.bustimes.SourceManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class InstallSourceTask extends Task {
	private static final String LOGNAME = "InstallSouceTask";
	
	private Source src = null;
	
	public InstallSourceTask(Context ctx, int id, OnTaskCompleteListener otcl, Source src) {
		super(ctx, id, otcl);
		this.src = src;
	}
	
	@Override
	protected Boolean doInBackground(Void... arg0) {
		String[] installLocationSources = src.getInstallFiles();
		boolean installComplete = true;
		if (null != installLocationSources && 0 != installLocationSources.length) { 
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Displaying progress indicator...");
			for (String installSource : installLocationSources) {
				installComplete = installComplete && LocationManager.installLocationsFromScript(ctx, installSource);
			}
		} else {
			installComplete = false;
		}
		
		if (installComplete) {
			SourceManager.updateInstallationStatus(ctx, src.getID(), true);
			return true;
		} else {
			Toast.makeText(ctx, "Failed to install "+src.getName(), Toast.LENGTH_SHORT).show();
			return false;
		}
	}

}
