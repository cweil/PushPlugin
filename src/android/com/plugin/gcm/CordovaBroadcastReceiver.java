package com.plugin.gcm;

import android.util.Log;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;

// As per the new Google Play Services API, all GCM messages are now received as a
// broadcast and handled by this broadcast receiver. The GCMBroadcastReceiver has been 
// deprecated and now only a simple BroadcastReceiver type is needed. The need for an 
// intent service has been eliminated.
 
public class CordovaBroadcastReceiver extends BroadcastReceiver {
	
	private final String TAG = "CordovaBroadcastReceiver";
			
	@Override
	public void onReceive(Context context, Intent intent){
		Log.d(TAG, "method executed: onReceive()");
	}
	
}