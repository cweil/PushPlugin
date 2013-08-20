package com.plugin.gcm;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

public class PushHandlerActivity extends Activity
{
	private static String TAG = "PushHandlerActivity"; 

	/*
	 * this activity will be started if the user touches a notification that we own. 
	 * We send it's data off to the push plugin for processing.
	 * If needed, we boot up the main activity to kickstart the application. 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Log.v(TAG, "onCreate");

		boolean isPushPluginActive = PushPlugin.isActive(); 
		if (!isPushPluginActive) {
			forceMainActivityReload();
			Log.d(TAG, "restarted main activity");
		}
		processPushBundle(isPushPluginActive);

		//No longer applicable with Google Play Services API
		//GCMIntentService.cancelNotification(this);

		finish();
		
	}

	/**
	 * Takes the pushBundle extras from the intent, 
	 * and sends it through to the PushPlugin for processing.
	 */
	private void processPushBundle(boolean isPushPluginActive)
	{
		Bundle extras = getIntent().getExtras();

		if (extras != null)	{
			
			Bundle originalExtras = extras.getBundle("pushBundle");

			if ( !isPushPluginActive ) { 
				originalExtras.putBoolean("coldstart", true);
			}

			PushPlugin.sendExtras(originalExtras);
		}
	}

	/**
	 * Forces the main activity to re-launch if it's unloaded.
	 */
	private void forceMainActivityReload()
	{
		//PackageManager pm = getPackageManager();
		//Intent intent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());
		
		// Intent intent = new Intent(Intent.ACTION_MAIN);
	    // intent.setComponent(ComponentName.unflattenFromString("com.phonegap.hello_world/com.phonegap.hello_world.HelloWorld"));
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

		Intent intent = new Intent(getApplicationContext(), com.phonegap.hello_world.HelloWorld.class);
		// intent.setComponent(ComponentName.unflattenFromString("com.phonegap.hello_world/.HelloWorld"));
		intent.setAction(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

		startActivity(intent);

	}

}