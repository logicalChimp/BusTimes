package uk.co.mentalspace.android.bustimes.utils;

import android.content.Context;
import android.util.Log;
import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.R;
import uk.co.mentalspace.android.bustimes.Source;
import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import net.londatiga.android.QuickAction.OnActionItemClickListener;

public class QABGenerator {
	private static final String LOGNAME = "QABGenerator";
	
	public static final int ACTION_ITEM_ID_EDIT_LOCATION = 1;
	public static final int ACTION_ITEM_ID_MAKE_FAVOURITE = 2;
	public static final int ACTION_ITEM_ID_UNMAKE_FAVOURITE = 3;
	public static final int ACTION_ITEM_ID_SHOW_ON_MAP = 4;
	
	public static final int ACTION_ITEM_ID_INSTALL_SOURCE = 5;
	public static final int ACTION_ITEM_ID_UNINSTALL_SOURCE = 6;
	public static final int ACTION_ITEM_ID_REFRESH_SOURCE = 7;
	
	public static QuickAction getManageSourceQABar(Context ctx, Source src, OnActionItemClickListener listener) {
		if (null == src) throw new IllegalArgumentException("Cannot create Quick Action Bar for null source");

		final QuickAction mQuickAction 	= new QuickAction(ctx);

		ActionItem item = null;
		if (!src.isInstalled()) {
			item = getSourceInstallActionItem(ctx, src);
			mQuickAction.addActionItem(item);
		} else {
			item = getSourceUninstallActionItem(ctx, src);
			mQuickAction.addActionItem(item);
			
	        item = getSourceRefreshActionItem(ctx, src);
			mQuickAction.addActionItem(item);
		}
		
		//setup the action item click listener
		if (null != listener) mQuickAction.setOnActionItemClickListener(listener);
		
		return mQuickAction;
	}

	public static QuickAction getLocationQABar(Context ctx, Location loc, OnActionItemClickListener listener, boolean showShowOnMap) {
		if (null == loc) throw new IllegalArgumentException("Cannot create Quick Action Bar for null location");
		
		final QuickAction mQuickAction 	= new QuickAction(ctx);

		ActionItem item = null;
		if (loc.getChosen()) {
			item = getLocationUnmakeFavouriteActionItem(ctx, loc);
			mQuickAction.addActionItem(item);
		} else {
			item = getLocationMakeFavouriteActionItem(ctx, loc);
			mQuickAction.addActionItem(item);
		}
		item = getLocationEditActionItem(ctx, loc);
		mQuickAction.addActionItem(item);
		
		if (showShowOnMap) {
			item = getLocationShowOnMapActionItem(ctx, loc);
			mQuickAction.addActionItem(item);
		}

		//setup the action item click listener
		if (null != listener) mQuickAction.setOnActionItemClickListener(listener);
		
		return mQuickAction;
	}
	
	@SuppressWarnings("unchecked")
	public static Location getLocationFromActionItem(ActionItem actionItem) {
		try {
			return ((BTActionItem<Location>)actionItem).getData();
		} catch (ClassCastException cce) {
			if (Preferences.ENABLE_LOGGING) Log.e(LOGNAME, "Invalid action item", cce);
			return null;
		}
	}
	
	protected static ActionItem getSourceInstallActionItem(Context ctx, Source src) {
		return new BTActionItem<Source>(ACTION_ITEM_ID_INSTALL_SOURCE, ctx.getString(R.string.source_actions_popup_install), ctx.getResources().getDrawable(R.drawable.list), src);
	}
	
	protected static ActionItem getSourceUninstallActionItem(Context ctx, Source src) {
		return new BTActionItem<Source>(ACTION_ITEM_ID_UNINSTALL_SOURCE, ctx.getString(R.string.source_actions_popup_uninstall), ctx.getResources().getDrawable(R.drawable.trash_can), src);
	}
	
	protected static ActionItem getSourceRefreshActionItem(Context ctx, Source src) {
		return new BTActionItem<Source>(ACTION_ITEM_ID_REFRESH_SOURCE, ctx.getString(R.string.source_actions_popup_refresh), ctx.getResources().getDrawable(R.drawable.refresh), src);
	}
	
	protected static ActionItem getLocationEditActionItem(Context ctx, Location loc) {
		return new BTActionItem<Location>(ACTION_ITEM_ID_EDIT_LOCATION, ctx.getString(R.string.location_actions_edit), ctx.getResources().getDrawable(R.drawable.pencil), loc);
	}
	
	protected static ActionItem getLocationMakeFavouriteActionItem(Context ctx, Location loc) {
		return new BTActionItem<Location>(ACTION_ITEM_ID_MAKE_FAVOURITE, ctx.getString(R.string.location_actions_make_favourite), ctx.getResources().getDrawable(R.drawable.bookmark), loc);
	}

	protected static ActionItem getLocationUnmakeFavouriteActionItem(Context ctx, Location loc) {
		return new BTActionItem<Location>(ACTION_ITEM_ID_UNMAKE_FAVOURITE, ctx.getString(R.string.location_actions_unmake_favourite), ctx.getResources().getDrawable(R.drawable.book), loc);
	}

	protected static ActionItem getLocationShowOnMapActionItem(Context ctx, Location loc) {
		return new BTActionItem<Location>(ACTION_ITEM_ID_SHOW_ON_MAP, ctx.getString(R.string.location_actions_show_on_map), ctx.getResources().getDrawable(R.drawable.world), loc);
	}
}
