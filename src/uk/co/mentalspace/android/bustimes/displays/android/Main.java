package uk.co.mentalspace.android.bustimes.displays.android;


import uk.co.mentalspace.android.bustimes.BusTime;
import java.util.List;
import uk.co.mentalspace.android.bustimes.Coordinator;
import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.R;
import uk.co.mentalspace.android.bustimes.Renderer;
import uk.co.mentalspace.android.bustimes.SettingsActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class Main extends Activity implements Renderer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		findViewById(R.id.busTimesView).setVisibility(View.GONE);
		findViewById(R.id.msgView).setVisibility(View.GONE);

		Log.d(this.getLocalClassName(), "Main Activity loaded, handing over to Coordinator");
        Coordinator.execute(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

	@Override
	public void finish() {
		Log.d(this.getLocalClassName(), "Terminating activity - terminating Coordinator timer");
		Coordinator.terminate();
		super.finish();
	}
	
	@Override
	public void onPause() {
		Log.d(this.getLocalClassName(), "Pausing activity - terminating Coordinator timer");
		Coordinator.terminate();
		super.onPause();
	}
	
	@Override
	public void onResume() {
		Log.d(this.getLocalClassName(), "Resuming activity - starting fresh Coordinator");
		super.onResume();
		Coordinator.execute(this);
	}

	@Override
    public void execute(Runnable r) {
    	runOnUiThread(r);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.menu_settings:
    		startActivity(new Intent(this, SettingsActivity.class));
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    
	@Override
	public String getID() {
		return "AndroidScreenRenderer";
	}
	
	@Override
	public Context getDisplayContext() {
		return this;
	}

	@Override
	public void displayMessage(String msg, int msgLevel) {
		findViewById(R.id.busTimesView).setVisibility(View.GONE);
		TextView tv = (TextView)findViewById(R.id.msgView);
		tv.setVisibility(View.VISIBLE);
		tv.setText(msg);
	}

	@Override
	public void displayBusTimes(Location location, List<BusTime> busTimes) {
		findViewById(R.id.msgView).setVisibility(View.GONE);
		ListView lv = (ListView)findViewById(R.id.busTimesView);
		lv.setVisibility(View.VISIBLE);
		
		BusTimeListAdapter btla = new BusTimeListAdapter(this.getDisplayContext(), busTimes.toArray(new BusTime[busTimes.size()]));
		lv.setAdapter(btla);
	}

}
