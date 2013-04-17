package uk.co.mentalspace.android.bustimes.sources.test;

import java.util.List;
import java.util.ArrayList;
import uk.co.mentalspace.android.bustimes.BusTime;
import uk.co.mentalspace.android.bustimes.BusTimeRefreshTask;
import uk.co.mentalspace.android.bustimes.Location;

public class BusTimesRefresh implements BusTimeRefreshTask {

	private static int cycleCount = 0;
	
	public List<BusTime> getBusTimes(Location loc) {
		cycleCount = (cycleCount+1)%3;
		return getBusTimes(loc, cycleCount);
	}

	public List<BusTime> getBusTimes(Location loc, int offset) {
		ArrayList<BusTime> bts = new ArrayList<BusTime>();
		int eta = 0 + offset;
		
		BusTime bt = new BusTime("6", "Test Loc B", Integer.toString(3+offset));
		bts.add(bt);
		
		String etaString = (eta == 0) ? "Due" : Integer.toString(eta);
		bt = new BusTime("3", "Test Loc 1", etaString);
		bts.add(bt);
		
		bt = new BusTime("9", "Test Loc x", Integer.toString(6+offset));
		bts.add(bt);
		
		return bts;
	}
}
