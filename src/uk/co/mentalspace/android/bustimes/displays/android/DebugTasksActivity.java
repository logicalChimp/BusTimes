package uk.co.mentalspace.android.bustimes.displays.android;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.R;
import uk.co.mentalspace.android.bustimes.db.LocationsDBAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class DebugTasksActivity extends Activity implements OnClickListener {
	private static final String LOGNAME = "DebugTasksActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug_tasks);
		
		((Button)findViewById(R.id.debug_task_dump_locations)).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.debug_tasks, menu);
		return true;
	}

	private boolean dumpLocationsToSQLFile(String fileName) {
		if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
		    Toast.makeText(this, "External SD card not mounted", Toast.LENGTH_LONG).show();
		    return true;
		}
		
		LocationsDBAdapter ldba = null;
		FileOutputStream fos = null;
		try {
			ldba = new LocationsDBAdapter(this);
			ldba.openReadable();
			Cursor c = ldba.fetchAllLocations();
			if (null != c && c.getCount() > 0) {
				File sdCard = Environment.getExternalStorageDirectory();
    			File dir = new File (sdCard.getAbsolutePath() + "/bustimes/db/");
    			dir.mkdirs();
    			
    			File outputFile = null;
				StringBuffer script = new StringBuffer();
				c.moveToFirst();
				while (!c.isAfterLast()) {
					if (0 == c.getPosition() % 5000) {
						int index = c.getPosition() / 5000;
		    			outputFile = new File(dir, fileName+"-"+index+".sql");
		    			if (!outputFile.exists()) outputFile.createNewFile();
		    			fos = new FileOutputStream(outputFile);
					}
	    			script = new StringBuffer();
//					script.append("insert into locations");
//					script.append(" (");
//					script.append(LocationsDBAdapter.KEY_STOP_CODE);
//					script.append(", ");
//					script.append(LocationsDBAdapter.KEY_NAME);
//					script.append(", ");
//					script.append(LocationsDBAdapter.KEY_DESC);
//					script.append(", ");
//					script.append(LocationsDBAdapter.KEY_WGS84_LAT);
//					script.append(", ");
//					script.append(LocationsDBAdapter.KEY_WGS84_LONG);
//					script.append(", ");
//					script.append(LocationsDBAdapter.KEY_SRC_POS_A);
//					script.append(", ");
//					script.append(LocationsDBAdapter.KEY_SRC_POS_B);
//					script.append(", ");
//					script.append(LocationsDBAdapter.KEY_HEADING);
//					script.append(", ");
//					script.append(LocationsDBAdapter.KEY_NICK_NAME);
//					script.append(", ");
//					script.append(LocationsDBAdapter.KEY_CHOSEN);
//					script.append(", ");
//					script.append(LocationsDBAdapter.KEY_SOURCE_ID);
//					script.append(")");
					script.append(" values ('");
					script.append(c.getString(c.getColumnIndex(LocationsDBAdapter.KEY_STOP_CODE)));
					script.append("', '");
					script.append(c.getString(c.getColumnIndex(LocationsDBAdapter.KEY_NAME)));
					script.append("', '");
					script.append(c.getString(c.getColumnIndex(LocationsDBAdapter.KEY_DESC)));
					script.append("', ");
					script.append(c.getInt(c.getColumnIndex(LocationsDBAdapter.KEY_WGS84_LAT)));
					script.append(", ");
					script.append(c.getInt(c.getColumnIndex(LocationsDBAdapter.KEY_WGS84_LONG)));
					script.append(", '");
					script.append(c.getString(c.getColumnIndex(LocationsDBAdapter.KEY_SRC_POS_A)));
					script.append("', '");
					script.append(c.getString(c.getColumnIndex(LocationsDBAdapter.KEY_SRC_POS_B)));
					script.append("', '");
					script.append(c.getString(c.getColumnIndex(LocationsDBAdapter.KEY_HEADING)));
					script.append("', '");
					script.append(c.getString(c.getColumnIndex(LocationsDBAdapter.KEY_NICK_NAME)));
					script.append("', ");
					script.append(c.getInt(c.getColumnIndex(LocationsDBAdapter.KEY_CHOSEN)));
					script.append(", '");
					script.append(c.getString(c.getColumnIndex(LocationsDBAdapter.KEY_SOURCE_ID)));
					script.append("');\n");

					fos.write(script.toString().getBytes());
	    			fos.flush();
					c.moveToNext();
				}
			}
			return true;
		} catch (FileNotFoundException fnfe) {
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Failed to open file", fnfe);
		} catch (IOException ioe) {
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "IO Exception", ioe);
		} finally {
			if (null != ldba) try {ldba.close();} catch (Exception e) {/* do nothing here */};
			if (null != fos) try {fos.close();} catch (IOException ioe) { /* do nothing here */};
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.debug_task_dump_locations:
			dumpLocationsToSQLFile("debug_locations_dump");
			return;
		}
	}
}
