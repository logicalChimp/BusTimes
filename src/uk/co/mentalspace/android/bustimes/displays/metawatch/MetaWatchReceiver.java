package uk.co.mentalspace.android.bustimes.displays.metawatch;

import android.content.BroadcastReceiver;
import uk.co.mentalspace.android.bustimes.BusTimeRefreshService;
import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.R;
import uk.co.mentalspace.android.utils.MW;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MetaWatchReceiver extends BroadcastReceiver implements MW {
	private static final String LOGNAME = "MetaWatchReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			final String action = intent.getAction();
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Received Intent.  Action: " + action);
	
			final String appId = intent.getStringExtra("id");
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Intent intended for app ["+appId+"]");
			
			if (ACTION_DISCOVERY.equals(action)) {
				metaWatchAnnounce(context);
			}
			else if (ACTION_BUTTON_PRESS.equals(action)) {
	//			String msg = "Btn Press: button ["+intent.getIntExtra("button", -1)+"] type ["+intent.getIntExtra("type", -1)+"]";
	//			Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
				Bundle b = intent.getExtras();
				int btnId = b.getInt("button");
				int btnType = b.getInt("type");
				if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Button press. btn id ["+btnId+"], type ["+btnType+"]");
				if (BUTTON_TYPE_NO_HOLD == btnType) {
					Intent service = new Intent(context, MetaWatchService.class);
					service.setAction(action);
					service.putExtra("button", btnId);
					service.putExtra("type", btnType);
					context.startService(service);
				}
			}
			else if (ACTION_ACTIVATE.equals(action)) {
				Intent service = new Intent(context, MetaWatchService.class);
				service.setAction(action);
				context.startService(service);
			}
			else if (ACTION_DEACTIVATE.equals(action)) {
				Intent service = new Intent(context, MetaWatchService.class);
				service.setAction(action);
				context.startService(service);
			}
			else if (BusTimeRefreshService.ACTION_LATEST_BUS_TIMES.equals(action)) {
				Intent service = new Intent(context, MetaWatchService.class);
				service.setAction(BusTimeRefreshService.ACTION_LATEST_BUS_TIMES);
				service.putExtras(intent.getExtras());
				context.startService(service);
			}
			else {
				if (Preferences.ENABLE_LOGGING) Log.w(LOGNAME, "Unrecognised intent action: "+action);
			}
		} catch (Exception e) {
			if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "Unexpected exception", e);
		}
	}

	public static void metaWatchAnnounce(Context ctx) {
		Intent announce = new Intent();
		announce.setAction(ACTION_ANNOUNCE);
		Bundle b = new Bundle();
		b.putString("id", ctx.getResources().getString(R.string.app_id));
		b.putString("name", ctx.getResources().getString(R.string.app_name));
		announce.putExtras(b);
		announce.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
		ctx.sendBroadcast(announce);
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Sending Metawatch announce");
	}
	
	public static void metaWatchStart(Context ctx) {
		Intent announce = new Intent(ACTION_START);
		Bundle b = new Bundle();
		b.putString("id", ctx.getResources().getString(R.string.app_id));
		b.putString("name", ctx.getResources().getString(R.string.app_name));
		announce.putExtras(b);
		announce.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
		ctx.sendBroadcast(announce);
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Sending Metawatch Start");
	}
	
	public static void metaWatchStop(Context ctx) {
		Intent announce = new Intent(ACTION_STOP);
		Bundle b = new Bundle();
		b.putString("id", ctx.getResources().getString(R.string.app_id));
		b.putString("name", ctx.getResources().getString(R.string.app_name));
		announce.putExtras(b);
		announce.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
		ctx.sendBroadcast(announce);
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Sending Metawatch stop");
	}
	
}
