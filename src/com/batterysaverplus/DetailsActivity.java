package com.batterysaverplus;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.startapp.android.publish.AdEventListener;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;
import com.startapp.android.publish.nativead.NativeAdDetails;
import com.startapp.android.publish.nativead.NativeAdPreferences;
import com.startapp.android.publish.nativead.NativeAdPreferences.NativeAdBitmapSize;
import com.startapp.android.publish.nativead.StartAppNativeAd;

public class DetailsActivity extends Activity {

private StartAppAd startAppAd = new StartAppAd(this);
private InterstitialAd interstitial;
	/** StartApp Native Ad declaration */
	private StartAppNativeAd startAppNativeAd = new StartAppNativeAd(this);
	private NativeAdDetails nativeAd = null;
	
	TextView textBatteryLevel = null;
	TextView text1 = null;
	TextView text2 = null;
	TextView text3 = null;
	TextView text4 = null;
	TextView text5 = null;
	TextView text6 = null;
	String batteryLevelInfo = "Battery Level";

	final AdEventListener nativeAdListener = new AdEventListener() {
		
		@Override
		public void onFailedToReceiveAd(com.startapp.android.publish.Ad arg0) {
			// TODO Auto-generated method stub
			Log.e("MyApplication", "Error while loading Ad");
		}

		@Override
		public void onReceiveAd(com.startapp.android.publish.Ad arg0) {
			// TODO Auto-generated method stub
			ArrayList<NativeAdDetails> ads = startAppNativeAd.getNativeAds();    // get NativeAds list

            // Print all ads details to log
            Iterator<NativeAdDetails> iterator = ads.iterator();
            while(iterator.hasNext()){
                  Log.d("MyApplication", iterator.next().toString());
		}
    }
	};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StartAppSDK.init(this, AppConstants.DEVELOPER_ID, AppConstants.APP_ID, true); 
		setContentView(R.layout.details);

		// Prepare the Interstitial Ad
		interstitial = new InterstitialAd(DetailsActivity.this);
		// Insert the Ad Unit ID
		interstitial.setAdUnitId(AppConstants.Interstitial_Id);
		
		// Request for Ads
		AdRequest adRequest = new AdRequest.Builder()
		
		// Add a test device to show Test Ads
		 .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
		 .addTestDevice("")
				.build();
		
		// Load ads into Interstitial Ads
		interstitial.loadAd(adRequest);
		
		// Prepare an Interstitial Ad Listener
		interstitial.setAdListener(new AdListener() {
			public void onAdLoaded() {
				// Call displayInterstitial() function
				displayInterstitial();
			}
		});
	
		startAppNativeAd.loadAd(
				new NativeAdPreferences()
					.setAdsNumber(1)
					.setAutoBitmapDownload(true)
					.setImageSize(NativeAdBitmapSize.SIZE150X150),
				nativeAdListener);
		
		textBatteryLevel = (TextView) findViewById(R.id.txtBatteryInfo);
		text1 = (TextView) findViewById(R.id.textView1);
		text2 = (TextView) findViewById(R.id.textView2);
		text3 = (TextView) findViewById(R.id.textView3);
		text4 = (TextView) findViewById(R.id.textView4);
		text5 = (TextView) findViewById(R.id.textView5);
		text6 = (TextView) findViewById(R.id.textView6);
		registerBatteryLevelReceiver();
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(battery_receiver);

		super.onDestroy();
	}

	private BroadcastReceiver battery_receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean isPresent = intent.getBooleanExtra("present", false);
			String technology = intent.getStringExtra("technology");
			int plugged = intent.getIntExtra("plugged", -1);
			int scale = intent.getIntExtra("scale", -1);
			int health = intent.getIntExtra("health", 0);
			int status = intent.getIntExtra("status", 0);
			int rawlevel = intent.getIntExtra("level", -1);
			int voltage = intent.getIntExtra("voltage", 0);
			int temperature = intent.getIntExtra("temperature", 0);
			int level = 0;

			Bundle bundle = intent.getExtras();

			Log.i("BatteryLevel", bundle.toString());

			if (isPresent) {
				if (rawlevel >= 0 && scale > 0) {
					level = (rawlevel * 100) / scale;
				}

				String info = "Battery Level: " + level + "%\n";
				String info1 = ("Technology: " + technology + "\n");
				String info2 = ("Plugged: " + getPlugTypeString(plugged) + "\n");
				String info3 = ("Health: " + getHealthString(health) + "\n");
				String info4 = ("Status: " + getStatusString(status) + "\n");
				String info5 = ("Voltage: " + voltage + "\n");
				String info6 = ("Temperature: " + temperature + "\n");

				setBatteryLevelText(info);
				setBatteryLevelText1(info1);
				setBatteryLevelText2(info2);
				setBatteryLevelText3(info3);
				setBatteryLevelText4(info4);
				setBatteryLevelText5(info5);
				setBatteryLevelText6(info6);
				
				
			} else {
				setBatteryLevelText("Battery not present!!!");
			}
		}
	};

	private String getPlugTypeString(int plugged) {
		String plugType = "Unknown";

		switch (plugged) {
		case BatteryManager.BATTERY_PLUGGED_AC:
			plugType = "AC";
			break;
		case BatteryManager.BATTERY_PLUGGED_USB:
			plugType = "USB";
			break;
		}

		return plugType;
	}
	public void displayInterstitial() {
		// If Ads are loaded, show Interstitial else show nothing.
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}
	private String getHealthString(int health) {
		String healthString = "Unknown";

		switch (health) {
		case BatteryManager.BATTERY_HEALTH_DEAD:
			healthString = "Dead";
			break;
		case BatteryManager.BATTERY_HEALTH_GOOD:
			healthString = "Good";
			break;
		case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
			healthString = "Over Voltage";
			break;
		case BatteryManager.BATTERY_HEALTH_OVERHEAT:
			healthString = "Over Heat";
			break;
		case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
			healthString = "Failure";
			break;
		}

		return healthString;
	}

	private String getStatusString(int status) {
		String statusString = "Unknown";

		switch (status) {
		case BatteryManager.BATTERY_STATUS_CHARGING:
			statusString = "Charging";
			break;
		case BatteryManager.BATTERY_STATUS_DISCHARGING:
			statusString = "Discharging";
			break;
		case BatteryManager.BATTERY_STATUS_FULL:
			statusString = "Full";
			break;
		case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
			statusString = "Not Charging";
			break;
		}

		return statusString;
	}

	private void setBatteryLevelText(String text) {
		textBatteryLevel.setText(text);
	}
	private void setBatteryLevelText1(String text) {
		text1.setText(text);
	}
	private void setBatteryLevelText2(String text) {
		text2.setText(text);
	}
	private void setBatteryLevelText3(String text) {
		text3.setText(text);
	}
	private void setBatteryLevelText4(String text) {
		text4.setText(text);
	}
	private void setBatteryLevelText5(String text) {
		text5.setText(text);
	}
	private void setBatteryLevelText6(String text) {
		text6.setText(text);
	}

	private void registerBatteryLevelReceiver() {
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

		registerReceiver(battery_receiver, filter);
	}
	@Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    
    	Intent intent = new Intent(getApplicationContext(),BatteryActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
    	startAppAd.onBackPressed();
    	super.onBackPressed();
    }
	@Override
	public void onResume() {
		super.onResume();
		startAppAd.onResume();
	}
    
    @Override
	public void onPause() {
		super.onPause();
		startAppAd.onPause();
	}
}
