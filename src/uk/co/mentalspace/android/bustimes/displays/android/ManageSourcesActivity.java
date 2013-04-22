package uk.co.mentalspace.android.bustimes.displays.android;

import java.util.List;

import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import net.londatiga.android.QuickAction.OnActionItemClickListener;

import uk.co.mentalspace.android.bustimes.LocationRefreshService;
import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.R;
import uk.co.mentalspace.android.bustimes.Source;
import uk.co.mentalspace.android.bustimes.SourceManager;
import uk.co.mentalspace.android.bustimes.displays.android.listadapters.SourcesListAdapter;
import uk.co.mentalspace.android.bustimes.utils.BTActionItem;
import android.os.Bundle;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ManageSourcesActivity extends FragmentActivity implements OnItemClickListener, OnDismissListener, OnActionItemClickListener, QuickAction.OnDismissListener {
	private static final String LOGNAME = "ManageSourcesActivity";
	
	private static final int ACTION_ID_INSTALL_SOURCE = 0;
	private static final int ACTION_ID_REFRESH_SOURCE = 1;
	private static final int ACTION_ID_UNINSTALL_SOURCE = 2;
	
	public static final String ACTION_SHOW_STAGE_ONE_WELCOME = "showStageOneWelcome";
	
	private List<Source> srcs = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "onCreate start");
		setContentView(R.layout.activity_manage_sources);
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
		final QuickAction mQuickAction 	= new QuickAction(this);

		if (!src.isInstalled()) {
			ActionItem installItem = new BTActionItem<Source>(ACTION_ID_INSTALL_SOURCE, getString(R.string.source_actions_popup_install), getResources().getDrawable(android.R.drawable.stat_sys_download_done), src);
			mQuickAction.addActionItem(installItem);
		} else {
			ActionItem refreshItem = new BTActionItem<Source>(ACTION_ID_REFRESH_SOURCE, getString(R.string.source_actions_popup_refresh), getResources().getDrawable(android.R.drawable.stat_notify_sync_noanim), src);
			mQuickAction.addActionItem(refreshItem);
	        ActionItem uninstallItem = new BTActionItem<Source>(ACTION_ID_UNINSTALL_SOURCE, getString(R.string.source_actions_popup_uninstall), getResources().getDrawable(android.R.drawable.ic_menu_set_as), src);
			mQuickAction.addActionItem(uninstallItem);
		}
		
		//setup the action item click listener
		mQuickAction.setOnActionItemClickListener(this);
		mQuickAction.setOnDismissListener(this);
		mQuickAction.show(view);
		mQuickAction.setAnimStyle(QuickAction.ANIM_GROW_FROM_RIGHT);
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
		case ACTION_ID_INSTALL_SOURCE:
			//TODO add SQL scripts for each source for initial installation, to eliminate the 15-min wait
			SourceManager.updateInstallationStatus(this, src.getID(), true);
			configureLayout();
			return;
		case ACTION_ID_REFRESH_SOURCE:
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Sending intent to refresh locations for source ["+src.getName()+"]");
			Intent intent = new Intent(this, LocationRefreshService.class);
			intent.setAction(LocationRefreshService.ACTION_REFRESH_LOCATION_DATA);
			intent.putExtra(LocationRefreshService.EXTRA_SOURCE_ID, src.getID());
			this.startService(intent);
			Toast.makeText(this, "Refresh queued", Toast.LENGTH_SHORT).show();
			return;
		case ACTION_ID_UNINSTALL_SOURCE:
			//TODO add SQL function to delete all locations associated with the source
			SourceManager.updateInstallationStatus(this, src.getID(), false);
			configureLayout();
			return;
		}
	}
	
	@Override
	public void onDismiss() {
		//TODO remove
		Toast.makeText(this, "Ups..dismissed", Toast.LENGTH_SHORT).show();
	}
}
