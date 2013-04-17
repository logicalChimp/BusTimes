package uk.co.mentalspace.android.bustimes;

import java.util.List;

public interface BusTimeRefreshTask {

	public List<BusTime> getBusTimes(Location location);
}
