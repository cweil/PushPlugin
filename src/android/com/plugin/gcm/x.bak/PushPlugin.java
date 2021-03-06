package com.plugin.gcm;

import java.io.IOException;
import java.util.Iterator;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * @author Eoin Morgan
 */

public class PushPlugin extends CordovaPlugin {
	public static final String TAG = "PushPlugin";

	public static final String REGISTER = "register";
	public static final String UNREGISTER = "unregister";
	public static final String EXIT = "exit";

	private static CordovaWebView gWebView;
	private static String gECB;
	private static String gSenderID;
	private static Bundle gCachedExtras = null;
	// private Context context = cordova.getActivity().getApplicationContext();

	// DOESN'T WORK
	private GoogleCloudMessaging gcm = null;

	@Override
	public boolean execute(String action, JSONArray data,
			final CallbackContext callbackContext) throws JSONException {

		boolean result = false;

		Log.v(TAG, "execute: action=" + action);

		this.gcm = GoogleCloudMessaging
				.getInstance(this.cordova.getActivity().getApplicationContext());

		if (REGISTER.equals(action)) {

			Log.v(TAG, "execute: data=" + data.toString());

			try {
				JSONObject jo = data.getJSONObject(0);

				gWebView = this.webView;
				Log.v(TAG, "execute: jo=" + jo.toString());

				gECB = (String) jo.get("ecb");
				gSenderID = (String) jo.get("senderID");

				Log.v(TAG, "execute: ECB=" + gECB + " senderID=" + gSenderID);

				cordova.getThreadPool().execute(new Runnable() {
		            public void run() {
		                try {
							String regId = gcm.register(gSenderID);
							Log.d(TAG, "GCM regID: " + regId);
						} catch (IOException e) {
							Log.d(TAG, "GCM Error"+e.getMessage());
						}
		                Log.d(TAG, "calling callbackContext");
		                callbackContext.success(); // Thread-safe.
		            }
		        });					

				result = true;
			} catch (JSONException e) {
				Log.e(TAG, "execute: Got JSON Exception " + e.getMessage());
				result = false;
			}

			if (gCachedExtras != null) {
				Log.v(TAG, "sending cached extras");
				sendExtras(gCachedExtras);
				gCachedExtras = null;
			}

		} else if (UNREGISTER.equals(action)) {
			Log.v(TAG, "UNREGISTER");
			
			cordova.getThreadPool().execute(new Runnable() {
	            public void run() {
	            	try {
	    				gcm.unregister();
	    			} catch (IOException e) {
	    				Log.e(TAG, "Unable to unregister with GCM: " + e.getMessage());
	    			}
	                callbackContext.success(); // Thread-safe.
	            }
	        });	
			
			result = true;
		} else {
			result = false;
			Log.e(TAG, "Invalid action : " + action);
		}

		return result;
	}

	/*
	 * Sends a json object to the client as parameter to a method which is
	 * defined in gECB.
	 */
	public static void sendJavascript(JSONObject _json) {
		String _d = "javascript:" + gECB + "(" + _json.toString() + ")";
		Log.v(TAG, "sendJavascript: " + _d);

		if (gECB != null && gWebView != null) {
			gWebView.sendJavascript(_d);
		}
	}

	/*
	 * Sends the pushbundle extras to the client application. If the client
	 * application isn't currently active, it is cached for later processing.
	 */
	public static void sendExtras(Bundle extras) {
		if (extras != null) {
			if (gECB != null && gWebView != null) {
				sendJavascript(convertBundleToJson(extras));
			} else {
				Log.v(TAG,
						"sendExtras: caching extras to send at a later time.");
				gCachedExtras = extras;
				
		}
	}

	/*
	 * serializes a bundle to JSON.
	 */
	private static JSONObject convertBundleToJson(Bundle extras) {
		try {
			JSONObject json;
			json = new JSONObject().put("event", "message");

			JSONObject jsondata = new JSONObject();
			Iterator<String> it = extras.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				Object value = extras.get(key);

				// System data from Android
				if (key.equals("from") || key.equals("collapse_key")) {
					json.put(key, value);
				} else if (key.equals("foreground")) {
					json.put(key, extras.getBoolean("foreground"));
				} else if (key.equals("coldstart")) {
					json.put(key, extras.getBoolean("coldstart"));
				} else {
					// Maintain backwards compatibility
					if (key.equals("message") || key.equals("msgcnt")
							|| key.equals("soundname")) {
						json.put(key, value);
					}

					if (value instanceof String) {
						// Try to figure out if the value is another JSON object

						String strValue = (String) value;
						if (strValue.startsWith("{")) {
							try {
								JSONObject json2 = new JSONObject(strValue);
								jsondata.put(key, json2);
							} catch (Exception e) {
								jsondata.put(key, value);
							}
							// Try to figure out if the value is another JSON
							// array
						} else if (strValue.startsWith("[")) {
							try {
								JSONArray json2 = new JSONArray(strValue);
								jsondata.put(key, json2);
							} catch (Exception e) {
								jsondata.put(key, value);
							}
						} else {
							jsondata.put(key, value);
						}
					}
				}
			} // while
			json.put("payload", jsondata);

			Log.v(TAG, "extrasToJSON: " + json.toString());

			return json;
		} catch (JSONException e) {
			Log.e(TAG, "extrasToJSON: JSON exception");
		}
		return null;
	}

	public static boolean isActive() {
		return gWebView != null;
	}

	public void onDestroy() {
		gWebView = null;
		gECB = null;
		super.onDestroy();
	}
}