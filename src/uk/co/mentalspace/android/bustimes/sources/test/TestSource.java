package uk.co.mentalspace.android.bustimes.sources.test;

import uk.co.mentalspace.android.bustimes.Source;

public class TestSource extends Source {
	
	public TestSource() {
		super("TestSource", "Test Source", 1, "uk.co.mentalspace.android.bustimes.sources.test.BusStopsRefresh", "uk.co.mentalspace.android.bustimes.sources.test.BusTimesRefresh", "");
	}

//	@Override
//	public LocationRefreshTask getLocationRefreshTask() {
//		LocationRefreshTask lrt = new BusStopsRefresh();
//		return lrt;
//	}
//
//	@Override
//	public BusTimeRefreshTask getBusTimesTask() {
//		BusTimeRefreshTask btrt = new BusTimesRefresh();
//		return btrt;
//	}

}
