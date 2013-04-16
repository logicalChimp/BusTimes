package uk.co.mentalspace.android.bustimes.sources.test;

import java.util.ArrayList;
import java.util.List;

import uk.co.mentalspace.android.bustimes.BusTime;
import uk.co.mentalspace.android.bustimes.Coordinator;
import uk.co.mentalspace.android.bustimes.DataRefreshTask;

public class BusTimesRefresh extends DataRefreshTask {

	private static int cycleCount = 0;
	
	@Override
	public void executeSync() {
		cycleCount = (cycleCount+1)%3;
		List<BusTime> busTimes = getBusTimes(2-cycleCount);
		Coordinator.updateBusTimes(display, location, busTimes);
	}

	@Override
	public void run() {
		display.execute(new Runnable() {
			public void run() {
				cycleCount = (cycleCount+1)%3;
				List<BusTime> busTimes = getBusTimes(2-cycleCount);
				Coordinator.updateBusTimes(display, location, busTimes);
			}
		});
	}

	private List<BusTime> getBusTimes(int offset) {
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
