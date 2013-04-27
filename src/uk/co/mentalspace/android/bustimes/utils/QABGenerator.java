package uk.co.mentalspace.android.bustimes.utils;

import android.content.Context;
import android.util.Log;
import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.R;
import net.londatiga.android.ActionItem;
import net.londatiga.android.QuickAction;
import net.londatiga.android.QuickAction.OnActionItemClickListener;

public class QABGenerator {
	private static final String LOGNAME = "QABGenerator";
	
	public static final int ACTION_ITEM_ID_EDIT_LOCATION = 1;
	public static final int ACTION_ITEM_ID_MAKE_FAVOURITE = 2;
	public static final int ACTION_ITEM_ID_UNMAKE_FAVOURITE = 3;
	public static final int ACTION_ITEM_ID_SHOW_ON_MAP = 4;
	
	public static QuickAction getLocationQABar(Context ctx, Location loc, OnActionItemClickListener listener, boolean showShowOnMap) {
		if (null == loc) return null;
		
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
	
	protected static ActionItem getLocationEditActionItem(Context ctx, Location loc) {
		return new BTActionItem<Location>(ACTION_ITEM_ID_EDIT_LOCATION, ctx.getString(R.string.location_actions_edit), ctx.getResources().getDrawable(android.R.drawable.ic_menu_edit), loc);
	}
	
	protected static ActionItem getLocationMakeFavouriteActionItem(Context ctx, Location loc) {
		return new BTActionItem<Location>(ACTION_ITEM_ID_MAKE_FAVOURITE, ctx.getString(R.string.location_actions_make_favourite), ctx.getResources().getDrawable(android.R.drawable.star_big_on), loc);
	}

	protected static ActionItem getLocationUnmakeFavouriteActionItem(Context ctx, Location loc) {
		return new BTActionItem<Location>(ACTION_ITEM_ID_UNMAKE_FAVOURITE, ctx.getString(R.string.location_actions_unmake_favourite), ctx.getResources().getDrawable(android.R.drawable.star_big_off), loc);
	}

	protected static ActionItem getLocationShowOnMapActionItem(Context ctx, Location loc) {
		return new BTActionItem<Location>(ACTION_ITEM_ID_SHOW_ON_MAP, ctx.getString(R.string.location_actions_show_on_map), ctx.getResources().getDrawable(android.R.drawable.ic_menu_mapmode), loc);
	}
}
