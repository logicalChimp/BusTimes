package uk.co.mentalspace.android.bustimes.displays.android;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class Task extends AsyncTask<Void, Void, Boolean>{

	protected int id = 0;
	protected Context ctx = null;
	protected ProgressDialog pd = null;
	protected OnTaskCompleteListener otcl = null;
	
	public Task(Context ctx, int id, OnTaskCompleteListener otcl) {
		super();
		this.id = id;
		this.ctx = ctx;
		this.otcl = otcl;
	}
	
	public int getId() {
		return id;
	}

	public void setOnTaskCompleteListener(OnTaskCompleteListener otcl) {
		this.otcl = otcl;
	}
	

	@Override
	protected void onPreExecute() {
		pd = new ProgressDialog(ctx);
		pd.setMessage("Installing...");
		pd.show();
	}
	
	@Override
	protected void onPostExecute(Boolean bool) {
		if (null != pd) pd.dismiss();
		if (null != otcl) otcl.onTaskComplete(this);
	}
}
