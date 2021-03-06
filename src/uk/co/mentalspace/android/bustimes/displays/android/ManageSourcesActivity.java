package uk.co.mentalspace.android.bustimes.displays.android;


import java.util.List;
import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import net.londatiga.android.QuickAction.OnActionItemClickListener;

import uk.co.mentalspace.android.bustimes.LocationManager;
import uk.co.mentalspace.android.bustimes.LocationRefreshService;
import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.R;
import uk.co.mentalspace.android.bustimes.Source;
import uk.co.mentalspace.android.bustimes.SourceManager;
import uk.co.mentalspace.android.bustimes.displays.android.listadapters.SourcesListAdapter;
import uk.co.mentalspace.android.bustimes.displays.android.tasks.InstallSourceTask;
import uk.co.mentalspace.android.bustimes.utils.BTActionItem;
import uk.co.mentalspace.android.bustimes.utils.QABGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ManageSourcesActivity extends FragmentActivity implements OnItemClickListener, OnDismissListener, OnActionItemClickListener, QuickAction.OnDismissListener, OnTaskCompleteListener {
	private static final String LOGNAME = "ManageSourcesActivity";
	
	private static final int TASK_ID_INSTALL_LOCATIONS = 1;
	
	public static final String ACTION_SHOW_STAGE_ONE_WELCOME = "showStageOneWelcome";
	
	private List<Source> srcs = null;
	private Task task = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "onCreate start");
		setContentView(R.layout.activity_manage_sources);
		
		final Task data = (Task) getLastCustomNonConfigurationInstance();
	    if (data != null && !data.isCancelled() && !(data.getStatus() == AsyncTask.Status.FINISHED)) {
	    	task = data;
	    	task.setOnTaskCompleteListener(this);
	    }
	}

	@Override
	public Object onRetainCustomNonConfigurationInstance() {
		//blank out the reference to the current OTCL (this) to avoid dead references etc - will be reset later
		if (null != task) task.setOnTaskCompleteListener(null);
	    return task;
	}
	
	@Override
	protected void onResume() {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "onResume start");
		super.onResume();
		configureLayout();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_manage_sources, menu);
		return true;
	}

	private void configureLayout() {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Configuring layout...");
		srcs = SourceManager.getAllSources(this);		
		Source[] srcsArray = srcs.toArray(new Source[]{});

		SourcesListAdapter sla = new SourcesListAdapter(this, srcsArray);
		sla.setDropDownViewResource(R.layout.row_layout_sources_list);
		ListView lv = (ListView)findViewById(R.id.manage_sources_source_list);
		lv.setAdapter(sla);
		lv.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//show actions bar.  actions to show as:
		// * if not installed: show install icon
		// * if installed: show refresh and delete icons
		if (null == srcs || srcs.size() <= position) return;
		
		Source src = srcs.get(position);
		final QuickAction mQuickAction = QABGenerator.getManageSourceQABar(this, src, this);
		mQuickAction.show(view);
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
	}

	@Override
	public void onItemClick(QuickAction source, int pos, int actionId) {
		ActionItem ai = source.getActionItem(pos);
		@SuppressWarnings("unchecked")
		Source src = ((BTActionItem<Source>)ai).getData();
		
		switch (actionId) {
		case QABGenerator.ACTION_ITEM_ID_INSTALL_SOURCE:
			task = new InstallSourceTask(this, TASK_ID_INSTALL_LOCATIONS, this, src);
			task.execute();
			return;
		case QABGenerator.ACTION_ITEM_ID_REFRESH_SOURCE:
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Sending intent to refresh locations for source ["+src.getName()+"]");
			Intent intent = new Intent(this, LocationRefreshService.class);
			intent.setAction(LocationRefreshService.ACTION_REFRESH_LOCATION_DATA);
			intent.putExtra(LocationRefreshService.EXTRA_SOURCE_ID, src.getID());
			this.startService(intent);
			Toast.makeText(this, "Refresh queued", Toast.LENGTH_SHORT).show();
			return;
		case QABGenerator.ACTION_ITEM_ID_UNINSTALL_SOURCE:
			boolean result = LocationManager.deleteSourceLocations(this, src.getID());
			if (result) {
				SourceManager.updateInstallationStatus(this, src.getID(), false);
				configureLayout();
			} else {
				Toast.makeText(this, "Failed to uninstall source "+src.getName(), Toast.LENGTH_SHORT).show();
			}
			return;
		}
	}
	
	@Override
	public void onDismiss() {
		//QuickAction bar dismissed - do nothing for now
	}

	@Override
	public void onTaskComplete(Task task) {
		if (null == task) return;
		switch (task.getId()) {
		case TASK_ID_INSTALL_LOCATIONS:
			//null the reference, to reduce overhead if user rotates the screen after completing the task
			task = null;

			//always refresh after task, even if it fails
			configureLayout();

			return;
		}
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

}
