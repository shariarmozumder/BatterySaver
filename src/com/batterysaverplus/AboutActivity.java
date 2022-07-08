package com.batterysaverplus;


import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class AboutActivity extends Activity{

private StartAppAd startAppAd = new StartAppAd(this);
private InterstitialAd interstitial;
	/** StartApp Native Ad declaration */
	private StartAppNativeAd startAppNativeAd = new StartAppNativeAd(this);
	private NativeAdDetails nativeAd = null;
	TextView contact;
	TextView share;
	TextView rate;
	TextView more;
	
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
		setContentView(R.layout.about);
		// Prepare the Interstitial Ad
				interstitial = new InterstitialAd(AboutActivity.this);
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
		
		contact = (TextView)findViewById(R.id.contact);
		share = (TextView)findViewById(R.id.share);
		rate = (TextView)findViewById(R.id.rate);
		more = (TextView)findViewById(R.id.more);
		
		contact.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
						 "mailto", "gurutechnolabs@gmail.com", null));
						 emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contact For - Battery Saver Plus And Battery TaskKiller");
						 startActivity(Intent.createChooser(emailIntent, null));
			}
		});
		
		share.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder alert = new AlertDialog.Builder(AboutActivity.this);
				LinearLayout layout = new LinearLayout(getApplicationContext());
		        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		        layout.setOrientation(LinearLayout.VERTICAL);
		        layout.setLayoutParams(parms);

		        layout.setGravity(Gravity.CLIP_VERTICAL);
		        layout.setPadding(2, 2, 2, 2);
				TextView tv = new TextView(getApplicationContext());
			    tv.setText("Share App");
			    tv.setPadding(40, 40, 40, 40);
			    tv.setGravity(Gravity.CENTER);
			    tv.setTextSize(20);
			    final String str = "Hello,"
			    		+ "I Like BatterySaverPlus And Process MemoryBoost By Guru Technolabs."+"Please Download Following Link"+"\n"+"https://play.google.com/store/apps/details?id=com.batterysaverplus"+"\n"+"Thank You...";
			    EditText et = new EditText(getApplicationContext());
			    et.setText(str);

			    alert.setView(et);
			    alert.setCancelable(false);
		        alert.setTitle("Share");
		        alert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		            	dialog.cancel();
		            }
		        });
		        alert.setNegativeButton("Share", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		            	Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
		                sharingIntent.setType("text/plain");
		                //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
		                sharingIntent.putExtra(Intent.EXTRA_TEXT, str);
		                startActivity(Intent.createChooser(sharingIntent, "Share via"));
		            }
		        });
		        alert.show();
			}
		});
		
		rate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("market://details?id=com.batterysaverplus&hl=en"));
				startActivity(intent);
			}
		});
		
		more.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(android.content.Intent.ACTION_VIEW);
				i.setData(Uri.parse("https://play.google.com/store/search?q=guru+technolabs"));
				startActivity(i);
			}
		});
	}
	public void freeAppClick(View view){
		if (nativeAd != null){
			nativeAd.sendClick(this);
		}
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
    
    	Intent intent = new Intent(getApplicationContext(),BatterySaverActivity.class);
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
