package uk.co.mentalspace.android.bustimes;

import uk.co.mentalspace.android.bustimes.sources.londonuk.LondonUK;
import uk.co.mentalspace.android.bustimes.sources.test.TestSource;

public class SourceManager {

	public static Source getSource(String id) {
		if ("londonuk-tfl".equals(id)) return new LondonUK();
		if ("TestSource".equals(id)) return new TestSource();
		else return null;
	}
}
