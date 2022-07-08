package com.batterysaverplus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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

public class BatterySaverActivity extends Activity {

private StartAppAd startAppAd = new StartAppAd(this);
	
	/** StartApp Native Ad declaration */
	private StartAppNativeAd startAppNativeAd = new StartAppNativeAd(this);
	private NativeAdDetails nativeAd = null;
	private InterstitialAd interstitial;
	int planestatus;
	ScheduledExecutorService scheduledExecutorService;
	private SeekBar seekBar;
	public Intent intent;
	TextView BatteryPercentageLoader;	
	AudioManager myAudioManager;
	static final String TURN_ON = "Enable";
	static final String TURN_OFF = "Disable";
	RelativeLayout blue;
	RelativeLayout wifi;
	RelativeLayout gps;
	RelativeLayout plane;
	RelativeLayout data;
	RelativeLayout ring;
	ImageView blue1;
	ImageView wifi1;
	ImageView gps1;
	//ToggleButton plane1;
	ImageView plane1;
	ImageView data1;
	ImageView ring1;
	ImageView setting;
	ImageView aboutimage;
	WifiManager wifiManager;
	ToggleButton mod;
	int count ;
	Method dataConnSwitchmethod;
	Class telephonyManagerClass;
	Object ITelephonyStub;
	Class ITelephonyClass;
	ConnectivityManager conman;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		StartAppSDK.init(this, AppConstants.DEVELOPER_ID, AppConstants.APP_ID, true); 
		setContentView(R.layout.batterymain);
		wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		startAppNativeAd.loadAd(
				new NativeAdPreferences()
					.setAdsNumber(1)
					.setAutoBitmapDownload(true)
					.setImageSize(NativeAdBitmapSize.SIZE150X150),
				nativeAdListener);
		// Prepare the Interstitial Ad
				interstitial = new InterstitialAd(BatterySaverActivity.this);
				// Insert the Ad Unit ID
				interstitial.setAdUnitId(AppConstants.Interstitial_Id);
				
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
			
		blue = (RelativeLayout)findViewById(R.id.blue);
		wifi = (RelativeLayout)findViewById(R.id.wifi);
		wifi1 = (ImageView)findViewById(R.id.wifi1);
		blue1 = (ImageView)findViewById(R.id.blue1);
		ring1 = (ImageView)findViewById(R.id.ring1);
		gps = (RelativeLayout)findViewById(R.id.gps);
		data = (RelativeLayout)findViewById(R.id.data);
		ring = (RelativeLayout)findViewById(R.id.ring);
		plane = (RelativeLayout)findViewById(R.id.plane);
		gps1 = (ImageView)findViewById(R.id.gps1);
		plane1 = (ImageView)findViewById(R.id.plane1);
		data1 = (ImageView)findViewById(R.id.data1);
		ring1 = (ImageView)findViewById(R.id.ring1);
		setting = (ImageView)findViewById(R.id.setting);
		aboutimage = (ImageView)findViewById(R.id.aboutimage);
		seekBar = (SeekBar) findViewById(R.id.seekBar1);
		
		conman = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter(); 
	 
		//wifiManager
		if(wifiManager.isWifiEnabled())
		if(wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING)
		{
			wifi1.setImageResource(R.drawable.ic_action_network_wif);
		}
		else
		{
			//wifiManager.setWifiEnabled(true);
			wifi1.setImageResource(R.drawable.ic_action_network_wifi);
		}
		
		//Bluetooth
			if(adapter.getState() == BluetoothAdapter.STATE_ON) {
		        //adapter.disable();
		        blue1.setImageResource(R.drawable.ic_action_bluetootha);
		    } else if (adapter.getState() == BluetoothAdapter.STATE_OFF){
		        //adapter.enable();
		        blue1.setImageResource(R.drawable.ic_action_bluetooth);
		    } else {
		        //State.INTERMEDIATE_STATE;
		    } 
			
			//RingTone
			int modee = myAudioManager.getRingerMode();
				if (modee == 2) 
				{ //Add item selected
				
				ring1.setImageResource(R.drawable.ic_action_volume_on);
				} 
				else if (modee == 0) 
				{ //Accept item selected
				
				ring1.setImageResource(R.drawable.ic_action_volume_muted);
				} 
				else if (modee == 1) { //Upload item selected
				
				ring1.setImageResource(R.drawable.vibration);
				}
				
				//DataConnection
				if(isMobileDataEnable() == true)
				{
					//Toast.makeText(getApplicationContext(), "Disable", Toast.LENGTH_SHORT).show();
					data1.setImageResource(R.drawable.ic_action_import_exporta);
				}
				else
				{
					//Toast.makeText(getApplicationContext(), "Enable", Toast.LENGTH_SHORT).show();
					data1.setImageResource(R.drawable.ic_action_import_export);
				}
				
		mod = (ToggleButton)findViewById(R.id.mod1);

		mod.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if(isChecked == true)
				{
				wifiManager.setWifiEnabled(false);
    			wifi1.setImageResource(R.drawable.ic_action_network_wifi);
    			
    			BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    			mBluetoothAdapter.disable(); 
    			blue1.setImageResource(R.drawable.ic_action_bluetooth);
				
        		toggleMobileDataConnection(false);
        		data1.setImageResource(R.drawable.ic_action_import_export);

				android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(),
		        android.provider.Settings.System.SCREEN_BRIGHTNESS, 50);
				seekBar.setProgress(50);
						  
		        myAudioManager.setRingerMode(1);
				ring1.setImageResource(R.drawable.untitle_1);
				}
				else
				{
					
					android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(),
			                android.provider.Settings.System.SCREEN_BRIGHTNESS, 255);
			    			seekBar.setProgress(255);
			        		
			        		myAudioManager.setRingerMode(2);
							ring1.setImageResource(R.drawable.ic_action_volume_o);
				}
			}
		});
		BatteryPercentageLoader = (TextView) findViewById(R.id.tv_percentage);
		getBatteryPercentage();
		//Setting
		
		setting.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try{ 
					startActivity((new Intent("android.settings.SETTINGS")).setFlags(0x10000000));
				}
				catch(Exception ex){
					
				} 		
			}
		});
		//About
		aboutimage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent aboutintent = new Intent(getApplicationContext(),AboutActivity.class);
				startActivity(aboutintent);
			}
		});
		
		
		//Bluetooth
		
		blue.setOnClickListener(new View.OnClickListener() {
			//BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			@Override
			public void onClick(View v) {
				
				if(adapter != null) {
				    if(adapter.getState() == BluetoothAdapter.STATE_ON) {
				        adapter.disable();
				        blue1.setImageResource(R.drawable.ic_action_bluetooth);
				    } else if (adapter.getState() == BluetoothAdapter.STATE_OFF){
				        adapter.enable();
				        blue1.setImageResource(R.drawable.ic_action_bluetootha);
				    } else {
				        //State.INTERMEDIATE_STATE;
				    } 
				}
				
			}
		});
	
		//Wifi 
		        wifi.setOnClickListener(new View.OnClickListener() {
		        	@Override
		        	public void onClick(View v) {
		        		// TODO Auto-generated method stub
		        		if(wifiManager.isWifiEnabled())
		        		{
		        			wifiManager.setWifiEnabled(false);
		        			wifi1.setImageResource(R.drawable.ic_action_network_wifi);
		        		}
		        		else
		        		{
		        			wifiManager.setWifiEnabled(true);
		        			wifi1.setImageResource(R.drawable.ic_action_network_wif);
		        		}
		        		
		        	}
		        });
		        
		//Gps 
		    gps.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	        		startActivity(intent);
				}
			});
	
		    data.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(isMobileDataEnable() == true)
					{
						//Toast.makeText(getApplicationContext(), "Disable", Toast.LENGTH_SHORT).show();
						toggleMobileDataConnection(false);
						data1.setImageResource(R.drawable.ic_action_import_export);
					}
					else
					{
						//Toast.makeText(getApplicationContext(), "Enable", Toast.LENGTH_SHORT).show();
						toggleMobileDataConnection(true);
						data1.setImageResource(R.drawable.ic_action_import_exporta);
					}
				}
			});
			
		    //Profile
		    ring.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					 int pos = myAudioManager.getRingerMode();
					if (pos == 2) { //Add item selected
						myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
						ring1.setImageResource(R.drawable.ic_action_volume_mued);
					} else if (pos == 0) { //Accept item selected
						myAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
						ring1.setImageResource(R.drawable.untitle_1);
					} else if (pos == 1) { //Upload item selected
						myAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
						ring1.setImageResource(R.drawable.ic_action_volume_o);
					}	
				}
			});
		    
		    //plane
		    plane1.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					 Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
					 startActivity(intent);	
				}
			});
        //Brightness
		    
		seekBar.setMax(255);
		float curBrightnessValue = 0;
		  try {
		   curBrightnessValue = android.provider.Settings.System.getInt(
		     getContentResolver(),
		     android.provider.Settings.System.SCREEN_BRIGHTNESS);
		  } catch (SettingNotFoundException e) {
		   e.printStackTrace();
		  }

		   int screen_brightness = (int) curBrightnessValue;
		  seekBar.setProgress(screen_brightness);
		  seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
		   int progress = 0;
		   public void onProgressChanged(SeekBar seekBar, int progresValue,
		     boolean fromUser) {
		    progress = progresValue;
		   }

		    public void onStartTrackingTouch(SeekBar seekBar) {
		    // Do something here,
		    // if you want to do anything at the start of
		    // touching the seekbar
		   }

		    public void onStopTrackingTouch(SeekBar seekBar) {
		    	ContentResolver resolver= getContentResolver();
		        Uri uri = android.provider.Settings.System
		                .getUriFor("screen_brightness");
		        android.provider.Settings.System.putInt(resolver, "screen_brightness",
		        		progress);
		        resolver.notifyChange(uri, null);
		   }
		  });		   
	}
	private void getBatteryPercentage() {
		   
		   
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
	    	   //Toast.makeText(getApplicationContext(), "USB", Toast.LENGTH_LONG).show();
	       }
	      else if (acCharge == true) {
	    	  //Toast.makeText(getApplicationContext(), "AC", Toast.LENGTH_LONG).show();
	      	}
	      else
	       {
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
		 public void updateUI(boolean state) {
		  //set text according to state
		  if(state) {
		   //tBMobileData.setText(TURN_OFF);             
		  } else {
		   //tBMobileData.setText(TURN_ON);
		  }
		 }

	/*	
		public void changeAirPlaneStatus(boolean v)
		{
			 if (Build.VERSION.SDK_INT <= 17)
             {
				 boolean in= Settings.System.putInt(this.getContentResolver(),Settings.System.AIRPLANE_MODE_ON, v ? 0 : 1);
				 if(in == true)//means this is the request to turn ON AIRPLANE mode
			    {
					 Toast.makeText(getApplicationContext(), "Air Plane Mode is On",Toast.LENGTH_LONG).show();
		    		//plane1.setImageResource(R.drawable.ic_action_airplane_mode);
		    		Settings.Global.getInt(getContentResolver(),
		                    Settings.Global.AIRPLANE_MODE_ON, 1);//Turning ON Airplane mode.
		    		//Toast.makeText(getApplicationContext(), "Air Plane Mode is On",Toast.LENGTH_LONG).show();//Displaying a Message to user
		    		 Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);//creating intent and Specifying action for AIRPLANE mode.
					 intent.putExtra("state", true);////indicate the "state" of airplane mode is changed to ON
		             //sendBroadcast(intent);//Broadcasting and Intent
					 plane1.setImageResource(R.drawable.ic_action_airplane_mode);
					 sendBroadcast(intent);//Broadcasting and Intent
		    	}
		    	else //means this is the request to turn OFF AIRPLANE mode
		    	{
		    		//plane1.setImageResource(R.drawable.ic_action_airplane_mode_off);
		    		Settings.Global.getInt(getApplicationContext().getContentResolver(),
		                    Settings.Global.AIRPLANE_MODE_ON, 0);//Turning OFF Airplane mode.
		    		//Toast.makeText(getApplicationContext(), "Air Plane Mode is Off",Toast.LENGTH_LONG).show();//Displaying a Message to user
		    		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);//creating intent and Specifying action for AIRPLANE mode.
		            intent.putExtra("state", false);//indicate the "state" of airplane mode is changed to OFF
		            plane1.setImageResource(R.drawable.ic_action_airplane_mode_off);
		            sendBroadcast(intent);//Broadcasting and Intent
		    	}
             }
			 else if(Build.VERSION.SDK_INT > 17)
			 {
				
			 }
		}
		 */
		 	    
		 public boolean isMobileDataEnable() {
		  boolean mobileDataEnabled = false; // Assume disabled
		  ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		  try {
		   Class cmClass = Class.forName(cm.getClass().getName());
		   @SuppressWarnings("unchecked")
		Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
		   method.setAccessible(true); // Make the method callable
		   // get the setting for "mobile data"
		   mobileDataEnabled = (Boolean)method.invoke(cm);
		  } catch (Exception e) {
		   // Some problem accessible private API and do whatever error handling you want here
		  }
		  return mobileDataEnabled;
		 }
		 public void displayInterstitial() {
				// If Ads are loaded, show Interstitial else show nothing.
				if (interstitial.isLoaded()) {
					interstitial.show();
				}
			}
		 public boolean toggleMobileDataConnection(boolean ON)
		 {
		  try {
		   //create instance of connectivity manager and get system connectivity service
		   final ConnectivityManager conman = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		   //create instance of class and get name of connectivity manager system service class
		   final Class conmanClass  = Class.forName(conman.getClass().getName());
		   //create instance of field and get mService Declared field 
		   final Field iConnectivityManagerField= conmanClass.getDeclaredField("mService");
		   //Attempt to set the value of the accessible flag to true
		   iConnectivityManagerField.setAccessible(true);
		   //create instance of object and get the value of field conman
		   final Object iConnectivityManager = iConnectivityManagerField.get(conman);
		   //create instance of class and get the name of iConnectivityManager field
		   final Class iConnectivityManagerClass=  Class.forName(iConnectivityManager.getClass().getName());
		   //create instance of method and get declared method and type
		   final Method setMobileDataEnabledMethod= iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled",Boolean.TYPE);
		   //Attempt to set the value of the accessible flag to true
		   setMobileDataEnabledMethod.setAccessible(true);
		   //dynamically invoke the iConnectivityManager object according to your need (true/false)
		   setMobileDataEnabledMethod.invoke(iConnectivityManager, ON);
		  } catch (Exception e){
		  }
		  return true;
		 }
		 
		    	/*@SuppressLint("NewApi")
				@SuppressWarnings("deprecation")
				public void modifyAirplanemode(boolean mode) {
		    	Settings.System.putInt(getContentResolver(),Settings.System.AIRPLANE_MODE_ON, mode ? 1 : 0);// Turning ON/OFF Airplane mode.
		    	Settings.Global.putInt(getApplication().getContentResolver(), Global.AIRPLANE_MODE_ON, mode ? 1 : 0);
		    	Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);// creating intent and Specifying action for AIRPLANE mode.
		    	intent.putExtra("state", !mode);// indicate the "state" of airplane mode is changed to ON/OFF
		    	sendBroadcast(intent);// Broadcasting and Intent
		    	} */
		    	@Override
		    	public boolean onOptionsItemSelected(MenuItem item) {
		    	    switch (item.getItemId()) {
		    	        case android.R.id.home:
		    	            // app icon in action bar clicked; go home
		    	            Intent intent = new Intent(this, BatteryActivity.class);
		    	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    	            startActivity(intent);
		    	            return true;
		    	        default:
		    	            return super.onOptionsItemSelected(item);
		    	    }
		    	}
		    	/** Native Ad Callback */
				private AdEventListener nativeAdListener = new AdEventListener() {
					
					
					@Override
					public void onFailedToReceiveAd(com.startapp.android.publish.Ad arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onReceiveAd(com.startapp.android.publish.Ad arg0) {
						// TODO Auto-generated method stub
						// Get the native ad
									ArrayList<NativeAdDetails> nativeAdsList = startAppNativeAd.getNativeAds();
									if (nativeAdsList.size() > 0){
										nativeAd = nativeAdsList.get(0);
									}
									
									// Verify that an ad was retrieved
									if (nativeAd != null){
										
										// When ad is received and displayed - we MUST send impression
										nativeAd.sendImpression(BatterySaverActivity.this);
									}
					}
				};
		    	@Override
		        public void onBackPressed() {
		        	// TODO Auto-generated method stub
		    		startAppAd.onBackPressed();
		        	Intent intent = new Intent(getApplicationContext(),BatteryActivity.class);
		        	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        	startActivity(intent);
		        	super.onBackPressed();
		        	startAppAd.showAd();
				    startAppAd.loadAd();
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
