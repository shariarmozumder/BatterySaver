package com.batterysaverplus;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.batterysaverplus.view.CircleImageView;
import com.batterysaverplus.view.CircleLayout;
import com.batterysaverplus.view.CircleLayout.OnCenterClickListener;
import com.batterysaverplus.view.CircleLayout.OnItemClickListener;
import com.batterysaverplus.view.CircleLayout.OnRotationFinishedListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.startapp.android.publish.AdEventListener;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;
import com.startapp.android.publish.nativead.NativeAdDetails;
import com.startapp.android.publish.nativead.StartAppNativeAd;

public class BatteryActivity extends Activity implements OnItemClickListener, OnRotationFinishedListener, OnCenterClickListener {
	TextView selectedTextView;
	Intent intent;
	Animation anim;
	private InterstitialAd interstitial;
	private StartAppAd startAppAd = new StartAppAd(this);
	private StartAppNativeAd startAppNativeAd = new StartAppNativeAd(this);
	private NativeAdDetails nativeAd = null;

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
		super.onCreate(savedInstanceState);
		StartAppSDK.init(this, AppConstants.DEVELOPER_ID, AppConstants.APP_ID, true); 
		setContentView(R.layout.activity_main);
		

		CircleLayout circleMenu = (CircleLayout) findViewById(R.id.main_circle_layout);
		//circleMenu.setOnItemSelectedListener(this);
		circleMenu.setOnItemClickListener(this);
		circleMenu.setOnRotationFinishedListener(this);
		circleMenu.setOnCenterClickListener(this);

		// Prepare the Interstitial Ad
				interstitial = new InterstitialAd(BatteryActivity.this);
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
		
		selectedTextView = (TextView) findViewById(R.id.main_selected_textView);
		selectedTextView.setText(((CircleImageView) circleMenu
				.getSelectedItem()).getName());
		anim = new AlphaAnimation(0.0f, 1.0f);
		anim.setDuration(1000); //You can manage the blinking time with this parameter
		anim.setStartOffset(20);
		anim.setRepeatMode(Animation.REVERSE);
		anim.setRepeatCount(Animation.INFINITE);
		
	}
	public void displayInterstitial() {
		// If Ads are loaded, show Interstitial else show nothing.
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}
	/*@Override
	public void onItemSelected(View view, String name) {
		selectedTextView.setText(name);

		switch (view.getId()) {
			case R.id.main_battery:
				// Handle facebook selection
				break;
			case R.id.main_charger:
				// Handle google selection
				break;
			case R.id.main_details:
				// Handle linkedin selection
				break;
			case R.id.main_graph:
				// Handle myspace selection
				break;
			case R.id.main_taskiller:
				// Handle twitter selection
				break;
			
		}
		
	}*/

	@Override
	public void onItemClick(View view, String name) {
		/*selectedTextView.setText(name);
		selectedTextView.startAnimation(anim);*/
		//Toast.makeText(getApplicationContext(),getResources().getString(R.string.start_app) + " " + name,Toast.LENGTH_SHORT).show();
		switch (view.getId()) {
		case R.id.main_battery:
			// Handle facebook click
			intent = new Intent(getApplicationContext(),BatterySaverActivity.class);
			startActivity(intent);
			break;
		case R.id.main_charger:
			// Handle google click
			intent = new Intent(getApplicationContext(),ChargerActivity.class);
			startActivity(intent);
			break;
		case R.id.main_details:
			// Handle linkedin click
			intent = new Intent(getApplicationContext(),DetailsActivity.class);
			startActivity(intent);
			break;
		case R.id.main_graph:
			// Handle twitter click
			intent = new       
		    Intent(getApplicationContext(),com.batterysaverplus.Updateservices.class);
		    BatteryActivity.this.startService(intent);
			
			intent = new BatteryChart().execute(this);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    startActivity(intent);
		    startAppAd.onBackPressed();
		       
			break;
		case R.id.main_taskiller:
			// Handle wordpress click
			intent = new Intent(getApplicationContext(),AdvancedTaskKiller.class);
			startActivity(intent);
			break;
		}
		
	}

	@Override
	public void onCenterClick() {
		//Toast.makeText(getApplicationContext(), R.string.center_click,Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onRotationFinished(View view, String name) {
		Animation animation = new RotateAnimation(0, 360, view.getWidth() / 2,
				view.getHeight() / 2);
		animation.setDuration(400);
		view.startAnimation(animation);
		selectedTextView.setText(name);
		selectedTextView.startAnimation(anim);
	}
	@Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
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
