package com.plugin.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMIntentService extends IntentService {
	public static final int NOTIFICATION_ID = 1;
	private final String TAG = "GCMIntentService";
	NotificationCompat.Builder builder;

	public GCMIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent()");
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) { // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that GCM
			 * will be extended in the future with new message types, just ignore
			 * any message types you're not interested in, or that you don't
			 * recognize.
			 */
			if (GoogleCloudMessaging.
			MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				Log.d(TAG, "GCM Message with type=\"ERROR\"");
			} else if (GoogleCloudMessaging.
			MESSAGE_TYPE_DELETED.equals(messageType)) {
				Log.d(TAG, "GCM Message with type=\"DELETED\"");
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.
			MESSAGE_TYPE_MESSAGE.equals(messageType)) {

				// Post notification of received message.
				sendMessageAsJson(extras);
				Log.i(TAG, "Received: " + extras.toString());
			}
		}
	}

	private void sendMessageAsJson(Bundle extras) {
		PushPlugin.sendExtras(extras);
	}

}


