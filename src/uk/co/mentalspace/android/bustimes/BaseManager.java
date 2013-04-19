package uk.co.mentalspace.android.bustimes;

import android.content.Context;

import android.util.Log;
import uk.co.mentalspace.android.bustimes.db.BaseDBAdapter;

public class BaseManager<T> {
	private static final String LOGNAME = "BaseManager2";

	public static class Task<F> {
		public F run(Context ctx) {
			BaseDBAdapter<?> bdba = getDBAdapter(ctx);
	        try {
		        bdba.openReadable();
		        return doWork();
	        } finally {
	        	if (null != bdba) try {bdba.close(); } catch (Exception e) {Log.e(LOGNAME, "Unknown exception", e); }
	        }
		}
		protected F doWork() {
			return null;
		}
		protected BaseDBAdapter<?> getDBAdapter(Context ctx) {
			return null;
		}
	}
}
