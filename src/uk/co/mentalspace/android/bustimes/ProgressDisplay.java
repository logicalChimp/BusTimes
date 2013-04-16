package uk.co.mentalspace.android.bustimes;

public interface ProgressDisplay {

	public void setVisibility(int code);
	public void setMaxValue(int max);
	public void incrementProgressBy(int value);
	public void setProgress(int value);
	public void setProgressLabel(String value);
}
