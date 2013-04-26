package uk.co.mentalspace.android.bustimes.displays.metawatch;


import android.content.Context;
import java.util.List;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import uk.co.mentalspace.android.bustimes.BusTime;
import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.Preferences;
import uk.co.mentalspace.android.bustimes.R;
import uk.co.mentalspace.android.bustimes.Display;
import uk.co.mentalspace.android.mw.utils.Utils;

public class MetaWatchDisplay implements Display {

	private static final String LOGNAME = "MetaWatchDisplay";
	
	private static final int METAWATCH_WIDTH = 96;
	private static final int METAWATCH_HEIGHT = 96;

	private static Typeface fontface;
	private static int fontsize = 8; //8pt;
	
	private Context ctx = null;

	public MetaWatchDisplay(Context ctx) {
		this.ctx = ctx;
	}
	
	@Override
	public String getID() {
		return "MetaWatchRenderer";
	}

    @Override
    public void execute(Runnable r) {
    	r.run();
    }
    
	@Override
	public void displayMessage(Location loc, String msg, int msgLevel) {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Displaying message: " + msg);
		Bitmap bitmap = Bitmap.createBitmap(METAWATCH_WIDTH, METAWATCH_HEIGHT, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		
		if (null == fontface) {
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Fontface not yet initialised - loading");
			fontface = Typeface.createFromAsset(ctx.getAssets(), "metawatch_8pt_7pxl_CAPS.ttf");
		}

		//set background color of the canvas
		canvas.drawColor(Color.WHITE);

		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Initialising a TextPaint..");
		//create default textpaint for drawing on canvas
		TextPaint tp = new TextPaint();
		tp.setColor(Color.BLACK);
		tp.setTypeface(fontface);
		tp.setTextSize(fontsize);
		tp.setTextAlign(Align.LEFT);

		int maxRows = 8;
		int startPos = 16;
		if (null != loc) {
			this.renderLocation(canvas, loc, tp);
			startPos = 42;
			maxRows = 5;
			tp.setTextAlign(Align.LEFT);
		}
		
		int start = 0;
		int row = 0;
		while (start < msg.length() && row < maxRows) {
			int end = start+17;
			end = (end >= msg.length()) ? -1 : end;
			String line = (end == -1) ? msg.substring(start) : msg.substring(start, end);
			int displayRow = startPos+(row*10);
			
			if (Preferences.ENABLE_LOGGING) Log.v(LOGNAME, "String ["+line+"], x ["+3+"], y ["+displayRow+"], ");
			canvas.drawText(line, 3, displayRow, tp);
			row++;
			start += 17;
		}
		
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Message converted to screen image...");
		sendApplicationUpdate(bitmap);
	}
	
	@Override
	public void displayBusTimes(Location location, List<BusTime> busTimes) {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Creating display bitmap");
		Bitmap bitmap = createDisplay(location, busTimes);
		sendApplicationUpdate(bitmap);
	}
	
	private void sendApplicationUpdate(Bitmap bitmap) {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Converting bitmap to byte array");
		int[] array = Utils.bitmapToPixelArray(bitmap);
		
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Sending Update intent");
		Intent intent = new Intent("org.metawatch.manager.APPLICATION_UPDATE");
		Bundle b = new Bundle();
		b.putString("id", ctx.getResources().getString(R.string.app_id));
		b.putIntArray("array", array);
		intent.putExtras(b);
		intent.setFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
		ctx.sendBroadcast(intent);
	}

	private Bitmap createDisplay(Location location, List<BusTime> busTimes) {
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Initialising source bitmap");
		Bitmap bitmap = Bitmap.createBitmap(METAWATCH_WIDTH, METAWATCH_HEIGHT, Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		
		//set background color of the canvas
		canvas.drawColor(Color.WHITE);

		if (null == fontface) {
			if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Fontface not yet initialised - loading");
			fontface = Typeface.createFromAsset(ctx.getAssets(), "metawatch_8pt_7pxl_CAPS.ttf");
		}

		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Initialising a TextPaint..");
		//create default textpaint for drawing on canvas
		TextPaint tp = new TextPaint();
		tp.setColor(Color.BLACK);
		tp.setTypeface(fontface);
		tp.setTextSize(fontsize);
		tp.setTextAlign(Align.CENTER);

		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Painting location");
		//add location details to top of display
		renderLocation(canvas, location, tp);
		
		if (Preferences.ENABLE_LOGGING) Log.d(LOGNAME, "Painting bus times");
		//add each bus time to the display
		for (int i=0;i<busTimes.size();i++) {
			renderBusTime(canvas, busTimes.get(i), i+1, tp); //+1 to make 1-based instead of 0-based
		}

		return bitmap;
	}
	
	private void renderLocation(Canvas canvas, Location location, TextPaint tp) {
		//paint in the text of the Nick Name (user-entered name for the location)
		String locName = location.getNickName();
		locName = (locName.length() > 16) ? locName.substring(0,16) : locName;		
		tp.setTextAlign(Align.LEFT);
		canvas.drawText(locName, 10, 10, tp);

		//paint in the text of the 'official' location next
		locName = location.getLocationName();
		locName = (locName.length() > 18) ? locName.substring(0,18) : locName;
		canvas.drawText(locName, 10, 20, tp);
	
		//create a paint object for drawing on the canvas
		Paint p = new Paint();
		p.setColor(Color.BLACK);		
		
		//draw in the icon indicating which button cycles to the next location
		Bitmap nextLocationIcon = Utils.loadBitmapFromAssets(ctx, "mw_next_location_lrg.bmp");
		canvas.drawBitmap(nextLocationIcon, 0, 4, p);

		//add the underline to separate titles from times
		canvas.drawLine(0, 31, 95, 31, p);
		
		//draw in column headers - use a BusTime object to get same spacing as column contents
		BusTime bt = new BusTime("Bus", "Dest", "ETA");
		renderBusTime(canvas, bt, 0, tp);
		
		//reset TP in case someone else needs to use it
		tp.setTextAlign(Align.CENTER);
	}
	
	private void renderBusTime(Canvas canvas, BusTime busTime, int busPosition, TextPaint tp) {
		int verticalOffset = (32+(busPosition*12)-2); //-2 = 2px padding bottom
		if (Preferences.ENABLE_LOGGING) Log.v(LOGNAME, "Bus time ["+busPosition+"] vertical offset ["+verticalOffset+"]");
		
		if (verticalOffset >= METAWATCH_HEIGHT) return; //ignore bus times that would render off the bottom of the screen
		
		if (Preferences.ENABLE_LOGGING) Log.v(LOGNAME, "Generate render points");
		Point num = new Point(20, verticalOffset); //20=20px from left, specify right limit because right aligned
		Point dest = new Point(22, verticalOffset); //22=22px from left, specify left limit because left aligned
		Point time = new Point(94, verticalOffset); //94=94px from left, specify right limit because right aligned

		if (Preferences.ENABLE_LOGGING) Log.v(LOGNAME, "Render bus number");
		//set text alignment to right (numeric data), and render bus position
		tp.setTextAlign(Align.RIGHT);
		canvas.drawText(busTime.getBusNumber(), num.x, num.y, tp);

		if (Preferences.ENABLE_LOGGING) Log.v(LOGNAME, "Render bus destination");
		//set text alignment to left (text data), and render destination (first 10 chars only)
		tp.setTextAlign(Align.LEFT);
		String destStr = (busTime.getDestination().length() > 9) ? busTime.getDestination().substring(0,9) : busTime.getDestination();
		canvas.drawText(destStr, dest.x, dest.y, tp);

		if (Preferences.ENABLE_LOGGING) Log.v(LOGNAME, "Render bus time");
		//set text alignment to right (numeric data), and render estimated time of arrival
		tp.setTextAlign(Align.RIGHT);
		canvas.drawText(busTime.getEstimatedArrivalTime(), time.x, time.y, tp);
		
		//reset alignment in case textpaint is used elsewhere
		tp.setTextAlign(Align.CENTER);
	}
	
	@Override
	public Context getDisplayContext() {
		return ctx;
	}
	
}
