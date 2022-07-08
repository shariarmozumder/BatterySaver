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
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.startapp.android.publish.AdEventListener;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;
import com.startapp.android.publish.nativead.NativeAdDetails;
import com.startapp.android.publish.nativead.StartAppNativeAd;


public class ChargerActivity extends Activity {
	private StartAppAd startAppAd = new StartAppAd(this);
	private InterstitialAd interstitial;
	/** StartApp Native Ad declaration */
	private StartAppNativeAd startAppNativeAd = new StartAppNativeAd(this);
	private NativeAdDetails nativeAd = null;
	Animation anim;
	TextView BatteryPercentageLoader;	
	TextView txt;
	ImageView image,image1,image2,image3;
	
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
	protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	StartAppSDK.init(this, AppConstants.DEVELOPER_ID, AppConstants.APP_ID, true); 
	setContentView(R.layout.charger);
	// Prepare the Interstitial Ad
			interstitial = new InterstitialAd(ChargerActivity.this);
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
		
	image = (ImageView)findViewById(R.id.imageView3);
	image1 = (ImageView)findViewById(R.id.imageView4);
	image2 = (ImageView)findViewById(R.id.imageView5);
	image3 = (ImageView)findViewById(R.id.imageView2);
	//image3.startAnimation(anim);
	anim = new AlphaAnimation(0.0f, 1.0f);
	anim.setDuration(1000); //You can manage the blinking time with this parameter
	anim.setStartOffset(20);
	anim.setRepeatMode(Animation.REVERSE);
	anim.setRepeatCount(Animation.INFINITE);
	BatteryPercentageLoader = (TextView) findViewById(R.id.tv_percentage);
	txt = (TextView)findViewById(R.id.textView1);
	 getBattery();
	 
	image.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String str = "Use unfluctuated Power Supply to Charge Efficiently and Fastly.";
			ChromeHelpPopup chromeHelpPopup = new ChromeHelpPopup(ChargerActivity.this,str);
			chromeHelpPopup.show(v);
		}
	});
	 
	image1.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String str = "Remove Power Supply When Fully Charged.";
			ChromeHelpPopup chromeHelpPopup = new ChromeHelpPopup(ChargerActivity.this,str);
			chromeHelpPopup.show(v);
		}
	});

	image2.setOnClickListener(new View.OnClickListener() {
	
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String str = "Use Trikle Charge To Keep Electrons Flowing Inside The Lithium Battery,to Make Up For Self-Discharge And Get Longer Battery Life.";
			ChromeHelpPopup chromeHelpPopup = new ChromeHelpPopup(ChargerActivity.this,str);
			chromeHelpPopup.show(v);
		}
	});
	}
       
	private void getBattery() {
		   
		   
		  BroadcastReceiver batteryLevel = new BroadcastReceiver() {
		    
		   public void onReceive(Context context, Intent intent) {
		    context.unregisterReceiver(this);
		    int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
	        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
	                            status == BatteryManager.BATTERY_STATUS_FULL;
	    
	        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
	        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
	        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
	       if (usbCharge == true)
	       {
	    	  txt.setText("USB");
	    	  image3.startAnimation(anim);
	       }
	      else if (acCharge == true) {
	    	  txt.setText("AC");
	    	  image3.startAnimation(anim);
	      	}
	      else
	       {
	    	  image3.setVisibility(View.INVISIBLE);
	    	  txt.setVisibility(View.INVISIBLE);
	    	  //Toast.makeText(getApplicationContext(), "Not Charged", Toast.LENGTH_LONG).show();
	       }
		    int currentLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		    int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
		    int level= -1;
		    if (currentLevel >= 0 && scale > 0) {
		     level = (currentLevel * 100) / scale;
		    }
		    BatteryPercentageLoader.setText(level + "%");
		   }
		  };
		  IntentFilter batteryLevelFilter = new IntentFilter(
				    Intent.ACTION_BATTERY_CHANGED);
				 registerReceiver(batteryLevel, batteryLevelFilter);
	}
	public void displayInterstitial() {
		// If Ads are loaded, show Interstitial else show nothing.
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}
	@Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    
    	/*Intent intent = new Intent(getApplicationContext(),BatteryActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);*/
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