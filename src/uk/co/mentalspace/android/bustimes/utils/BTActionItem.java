package uk.co.mentalspace.android.bustimes.utils;

import android.graphics.drawable.Drawable;
import net.londatiga.android.ActionItem;

public class BTActionItem<T> extends ActionItem {

	protected T data = null;
	
	public BTActionItem(T data) {
		super();
		this.data = data;
	}
	
    public BTActionItem(Drawable icon, T data) {
    	super(icon);
    	this.data = data;
    }
    
    public BTActionItem(int actionId, Drawable icon, T data) {
    	super(actionId, icon);
    	this.data = data;
    }
    
    public BTActionItem(int actionId, String title, Drawable icon, T data) {
    	super(actionId, title, icon);
    	this.data = data;
    }
    
	public T getData() {
		return data;
	}

}
