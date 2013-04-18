package uk.co.mentalspace.android.bustimes;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
 
/**
 * Acquires a partial WakeLock, allows TaskButtlerService to keep the CPU alive
 * until the work is done.
 * Taken from: http://dhimitraq.wordpress.com/tag/android-intentservice/ 
 * @author Dhimitraq Jorgji
 *
 */
public abstract class WakefulIntentService extends IntentService {
    public static final String LOCK_NAME = getLockName();

    private PowerManager.WakeLock lockLocal=null;
 
    public WakefulIntentService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager mgr = (PowerManager)getSystemService(Context.POWER_SERVICE);
        lockLocal = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME);
        lockLocal.setReferenceCounted(true);
    }
 
    @Override
    public void onStart(Intent intent, final int startId) {
        lockLocal.acquire();
        super.onStart(intent, startId);
    }
 
    @Override
    protected void onHandleIntent(Intent intent) {
    	try {
    		processIntent(intent);
    	} finally {
    		lockLocal.release();
    	}
    }
    
    public static String getLockName() {
    	return "uk.co.mentalspace.bustimes.WakefulIntentService";
    }
    
    public abstract void processIntent(Intent intent);
}