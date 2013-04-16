package uk.co.mentalspace.android.bustimes.sources.test;

import uk.co.mentalspace.android.bustimes.DataRefreshTask;
import uk.co.mentalspace.android.bustimes.Location;
import uk.co.mentalspace.android.bustimes.LocationRefreshTask;
import uk.co.mentalspace.android.bustimes.Renderer;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getEstimatedLocationCount() {
		return 1;
	}

	@Override
	public DataRefreshTask getBusTimes(Renderer display, Location location) {
		// TODO Auto-generated method stub
		return null;
	}

}
