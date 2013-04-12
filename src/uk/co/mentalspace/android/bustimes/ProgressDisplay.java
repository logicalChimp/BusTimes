package uk.co.mentalspace.android.bustimes;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressDisplay {
	
	private View container;
	private TextView textView;
	private ProgressBar progressView;
	
	public ProgressDisplay(Context ctx, View container, TextView label, ProgressBar progress) {
		this.container = container;
		this.textView = label;
		this.progressView = progress;
	}
	
	public void setVisibility(int code) {
		container.setVisibility(code);
	}
	
	public void setMaxValue(int max) {
		progressView.setMax(max);
	}
	
	public void incrementProgressBy(int value) {
		progressView.incrementProgressBy(value);
	}
	
	public void setProgress(int value) {
		progressView.setProgress(value);
	}
	
	public void setProgressLabel(String value) {
		textView.setText(value);
	}
}
