package com.plugin.gcm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class PushMessageReceiver extends BroadcastReceiver {
	static final String TAG = "PushReceiver";
	Context ctx;

	@Override
	public void onReceive(Context context, Intent intent) {
		ctx = context;

		Bundle extras = intent.getExtras();
		if (extras != null) {

			PushPlugin.sendExtras(extras);

		}

		setResultCode(Activity.RESULT_OK);
	}

}