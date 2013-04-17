package uk.co.mentalspace.android.bustimes.sources.test;

import uk.co.mentalspace.android.bustimes.BusTimeRefreshTask;
import uk.co.mentalspace.android.bustimes.LocationRefreshTask;
import uk.co.mentalspace.android.bustimes.Source;

public class TestSource implements Source {
	

	@Override
	public String getName() {
		return "Test Source";
	}

	@Override
	public String getID() {
		return "TestSource";
	}

	@Override
	public LocationRefreshTask getLocationRefreshTask() {
		LocationRefreshTask lrt = new BusStopsRefresh();
		return lrt;
	}

	@Override
	public int getEstimatedLocationCount() {
		return 1;
	}

	@Override
	public BusTimeRefreshTask getBusTimesTask() {
		BusTimeRefreshTask btrt = new BusTimesRefresh();
		return btrt;
	}

}
