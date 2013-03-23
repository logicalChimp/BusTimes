package uk.co.mentalspace.android.bustimes;

import uk.co.mentalspace.android.bustimes.sources.LondonUK;

public class SourceManager {

	public static Source getSource(String id) {
		if ("londonuk-tfl".equals(id)) return new LondonUK();
		else return null;
	}
}
