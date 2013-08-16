package com.plugin.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

// As per the new Google Play Services API, all GCM messages are now received as a
// broadcast and handled by this broadcast receiver. The GCMBroadcastReceiver has been 
// deprecated and now only a simple BroadcastReceiver type is needed. The need for an 
// intent service has been eliminated.

public class CordovaBroadcastReceiver extends WakefulBroadcastReceiver {

	private final String TAG = "CordovaBroadcastReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "method executed: onReceive()");

		// Explicitly specify that GcmIntentService will handle the intent.
		ComponentName comp = new ComponentName(context.getPackageName(),
				GCMIntentService.class.getName());
		// Start the service, keeping the device awake while it is launching.
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}

}