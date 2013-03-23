package uk.co.mentalspace.android.bustimes.displays.metawatch;

import uk.co.mentalspace.android.bustimes.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MetaWatchReceiver extends BroadcastReceiver {
	private static final String LOGNAME = "MetaWatchReceiver";

	public static final String MW_DISCOVERY = "org.metawatch.manager.APPLICATION_DISCOVERY";
	public static final String MW_ACTIVATED = "org.metawatch.manager.APPLICATION_ACTIVATE";
	public static final String MW_DEACTIVATED = "org.metawatch.manager.APPLICATION_DEACTIVATE";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();
		Log.d(LOGNAME, "Received Intent.  Action: " + action);

		if (MW_DISCOVERY.equals(action)) {
			metaWatchAnnounce(context);
		}
		else if (MW_ACTIVATED.equals(action)) {
//			Intent service = new Intent(context, MetaWatchActivity.class);
			Intent service = new Intent(context, MetaWatchService.class);
			service.setAction(action);
//			service.addFlags(Intent.FLAG_FROM_BACKGROUND);
//			service.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startActivity(service);
			context.startService(service);
		}
		else if (MW_DEACTIVATED.equals(action)) {
//			Intent service = new Intent(context, MetaWatchActivity.class);
			Intent service = new Intent(context, MetaWatchService.class);
			service.setAction(action);
//			service.addFlags(Intent.FLAG_FROM_BACKGROUND);
//			service.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startActivity(service);
			context.startService(service);
		}
		else {
			Log.d(LOGNAME, "Unrecognised intent action: "+action);
		}
	}

	public void metaWatchAnnounce(Context ctx) {
		Intent intent = new Intent("org.metawatch.manager.APPLICATION_ANNOUNCE");
		Bundle b = new Bundle();
		b.putString("id", ctx.getResources().getString(R.string.app_id));
		b.putString("name", ctx.getResources().getString(R.string.app_name));
		intent.putExtras(b);
		ctx.sendBroadcast(intent);
	}
	
}
