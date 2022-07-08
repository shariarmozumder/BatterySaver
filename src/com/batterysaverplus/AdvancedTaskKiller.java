package com.batterysaverplus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.startapp.android.publish.AdEventListener;
import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.StartAppSDK;
import com.startapp.android.publish.nativead.NativeAdDetails;
import com.startapp.android.publish.nativead.StartAppNativeAd;

public class AdvancedTaskKiller extends Activity
implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener{
	private static final int DIALOG_NEW_VERSION_ALERT = 7;
	public static final String NOTIFY_MESSAGE = "Menu->Settings to disable this.";
	public static final String NOTIFY_TITLE = "Open Advanced Task Killer Pro";
	ActivityManager mActivityManager = null;
	private TaskListAdapters.ProcessListAdapter mAdapter;
	private ArrayList<ProcessDetailInfo> mDetailList;
	private AlertDialog mMenuDialog;
	private Handler mHandler;
	private InterstitialAd interstitial;
	private StartAppAd startAppAd = new StartAppAd(this);
	/** StartApp Native Ad declaration */
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
	private void IgnoreSystemApp(){
		@SuppressWarnings("rawtypes")
		Iterator localIterator = getPackageManager().getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES).iterator();
		while (true){
			if (!localIterator.hasNext())
				return;
			PackageInfo localPackageInfo = (PackageInfo)localIterator.next();
			if (((ApplicationInfo.FLAG_SYSTEM & localPackageInfo.applicationInfo.flags) != 1) ||
					(!ProcessDetailInfo.IsPersistentApp(localPackageInfo)) || (localPackageInfo.applicationInfo == null)) 
					//(CommonLibrary.IsSystemProcessName(localPackageInfo.applicationInfo.processName)))
				continue;
			//ProcessDetailInfo.SetIgnored(true, this, localPackageInfo.applicationInfo.processName);
		}
	}

	private void bindEvent(){
		findViewById(R.id.btn_task).setOnClickListener(new View.OnClickListener(){
			public void onClick(View paramView){
				//android.util.Log.e("ATK", "Start manully kill!");
				AdvancedTaskKiller.this.killAllTasks();
			}
		});
		ListView localListView = (ListView)findViewById(R.id.listbody);
		localListView.setOnItemLongClickListener(this);
		localListView.setOnItemClickListener(this);
	}

	private void detail(TaskListAdapters.ListViewItem paramListViewItem){
		Intent localIntent;
		try{
			localIntent = new Intent("android.intent.action.VIEW");
			if (CommonLibrary.IsGingerbreadOrlater()){
				localIntent.setClassName("com.android.settings", "com.android.settings.applications.InstalledAppDetails");
				localIntent.setData(Uri.fromParts("package", paramListViewItem.detailProcess.getPackageName(), null));
				startActivity(localIntent);
				return;
			}
		}
		catch (Exception localException){
			android.util.Log.e("ATK", localException.toString());
			Toast.makeText(this, paramListViewItem.detailProcess.getPackageName(), 0).show();
		}
		return;
	}

	private void doAction(TaskListAdapters.ListViewItem paramListViewItem, int paramInt){
		switch (paramInt){
		case Setting.ACTION_KILL:
			kill(paramListViewItem);
			break;
		case Setting.ACTION_SWITCH:
			switchTo(paramListViewItem);
			break;
		case Setting.ACTION_SELECT:
			selectOrUnselect(paramListViewItem);
			break;
		case Setting.ACTION_IGNORE:
			ignore(paramListViewItem);
			break;
		case Setting.ACTION_DETAIL:
			detail(paramListViewItem);
			break;
		case Setting.ACTION_MENU:
			menu(paramListViewItem);
			break;
		default:
			return;
		}
	}

	private ListView getListView(){
		return (ListView)findViewById(R.id.listbody);
	}

	@SuppressWarnings("unused")
	private String getValuesFromPreference(SharedPreferences paramSharedPreferences){
		String str1 = "";
		String str2 = null;
		try{
			@SuppressWarnings("rawtypes")
			Iterator localIterator = paramSharedPreferences.getAll().entrySet().iterator();
			if (!localIterator.hasNext()){
				str2 = str1;
				return str2;
			}
			@SuppressWarnings("rawtypes")
			String str = str1 + ((Map.Entry)localIterator.next()).getKey() + "\r\n";
			str1 = str;
		}
		catch (Exception localException){
			str2 = localException.toString();
		}
		return str2;
	}

	private int getVersionCode(){
		PackageManager localPackageManager = getPackageManager();
		int i;
		try{
			i = localPackageManager.getPackageInfo(getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES).versionCode;
			return i;
		}
		catch (PackageManager.NameNotFoundException localNameNotFoundException){
			i = Setting.VERSION_CODE;
		}
		return i;
	}

	private void ignore(TaskListAdapters.ListViewItem paramListViewItem){
		paramListViewItem.detailProcess.setIgnored(true);
		refresh();
	}

	private void kill(TaskListAdapters.ListViewItem paramListViewItem){
		try{
			if (paramListViewItem.detailProcess.getPackageName().equals(getPackageName())){
				CommonLibrary.KillATK(mActivityManager, this);
			}else{
				CommonLibrary.KillApp(this, mActivityManager, paramListViewItem.detailProcess.getPackageName());
				mDetailList.remove(paramListViewItem.detailProcess);
				mAdapter.notifyDataSetChanged();
				refreshMem();
			}
		}
		catch (Exception localException){
			mAdapter.notifyDataSetChanged();
		}
	}

	private void killAllTasks(){
		CommonLibrary.KillProcess(this, mDetailList, mActivityManager, true);
		getRunningProcess();
		mAdapter = new TaskListAdapters.ProcessListAdapter(this, mDetailList);
		getListView().setAdapter(mAdapter);
		refreshMem();
		//android.util.Log.e("ATK", "Manually kill ends");
	}

	private void menu(TaskListAdapters.ListViewItem paramListViewItem){
		final TaskListAdapters.ListViewItem p = paramListViewItem;
		mMenuDialog = new AlertDialog.Builder(this)
		.setTitle(paramListViewItem.detailProcess.getLabel())
		.setIcon(paramListViewItem.detailProcess.getIcon())
		.setItems(R.array.select_dialog_items, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface paramDialogInterface, int paramInt){
				switch (paramInt){
				case 0:
					AdvancedTaskKiller.this.kill(p);
					break;
				case 1:
					AdvancedTaskKiller.this.selectOrUnselect(p);
					break;
				case 2:
					AdvancedTaskKiller.this.switchTo(p);
					break;
				case 3:
					AdvancedTaskKiller.this.ignore(p);
					break;
				case 4:
					AdvancedTaskKiller.this.detail(p);
					break;
				default:
					return;
				}
			}
		}).create();
		mMenuDialog.show();
	}

	private void refreshMem(){
		if (mHandler == null)
			mHandler = new Handler(){
			public void handleMessage(Message paramMessage){
				try{
					String str = "Available Memory: " + CommonLibrary.MemoryToString(
							CommonLibrary.getAvaliableMemory(AdvancedTaskKiller.this.mActivityManager));
					AdvancedTaskKiller.this.setTitle(str + "  " + Setting.getAutoKillInfo());
					Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
					return;
				}
				catch (Exception localException){
					localException.printStackTrace();
				}
			}
		};
		mHandler.sendEmptyMessageDelayed(0, 700L);
	}

	private void selectOrUnselect(TaskListAdapters.ListViewItem paramListViewItem){
		if (paramListViewItem.detailProcess.getSelected()){
			paramListViewItem.iconCheck.setImageResource(R.drawable.btn_check_off);
			paramListViewItem.detailProcess.setSelected(false);
		}
		else{
			paramListViewItem.iconCheck.setImageResource(R.drawable.btn_check_on);
			paramListViewItem.detailProcess.setSelected(true);
		}
	}

	@SuppressWarnings("unused")
	private void showRunningServices(){
		try{
			Intent localIntent = new Intent();
			localIntent.setClassName("com.android.settings", "com.android.settings.RunningServices");
			startActivity(localIntent);
			return;
		}
		catch (Exception localException){
			Toast.makeText(this, R.string.do_not_support, 0).show();
		}
	}

	private void switchTo(TaskListAdapters.ListViewItem paramListViewItem){
		try{
			Intent localIntent = new Intent("android.intent.action.MAIN");
			localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			localIntent.setClassName(paramListViewItem.detailProcess.getPackageName(), paramListViewItem.detailProcess.getBaseActivity());
			startActivity(localIntent);
			return;
		}
		catch (Exception localException){
		}
	}

	@SuppressWarnings("unused")
	private void uninstall(TaskListAdapters.ListViewItem paramListViewItem){
		startActivity(new Intent("android.intent.action.DELETE", Uri.parse("package:" + paramListViewItem.detailProcess.getPackageName())));
	}

	public void getRunningProcess(){
		mDetailList = CommonLibrary.GetRunningProcess(this, mActivityManager);
		mAdapter = new TaskListAdapters.ProcessListAdapter(this, mDetailList);
	}

	public void onConfigurationChanged(Configuration paramConfiguration){
		super.onConfigurationChanged(paramConfiguration);
	}

	@SuppressWarnings("deprecation")
	public void onCreate(Bundle paramBundle){
		super.onCreate(paramBundle);
		new Setting(getSharedPreferences("AdvTaskKillerSettings", 0), this);
		if ((Setting.AUTO_KILL_LEVEL > 0) && (CommonLibrary.NextRun == null))
			CommonLibrary.ScheduleAutoKill(this, false, Setting.AUTO_KILL_FREQUENCY);
		if (Setting.IS_BUTTON_AT_TOP)
			StartAppSDK.init(this, AppConstants.DEVELOPER_ID, AppConstants.APP_ID, true); 
			setContentView(R.layout.main);
			// Prepare the Interstitial Ad
			interstitial = new InterstitialAd(AdvancedTaskKiller.this);
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
		
		AlertDialog alertDialog = new AlertDialog.Builder(
                AdvancedTaskKiller.this).create();
		
		alertDialog.setTitle("Important Note:");
		alertDialog.setCancelable(false);
		alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		
		alertDialog.setMessage("Please Deselect BatterySaverPlus And Your Important Application...");

		// Setting Icon to Dialog
		alertDialog.setIcon(R.drawable.url);

		// Setting OK Button
		alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
        // Write your code here to execute after dialog closed
        	dialog.cancel();
        }
});

		// Showing Alert Message
		alertDialog.show();
		
		
		
		mActivityManager = ((ActivityManager)getSystemService("activity"));
		int i = getVersionCode();
		if (i != Setting.VERSION_CODE){
			Setting.setVersionCode(i);
			Setting.setIgnoreServiceFrontApp(false);
			showDialog(DIALOG_NEW_VERSION_ALERT);
			
		}
	}
	public void displayInterstitial() {
		// If Ads are loaded, show Interstitial else show nothing.
		if (interstitial.isLoaded()) {
			interstitial.show();
		}
	}
	public boolean onCreateOptionsMenu(Menu paramMenu){
		paramMenu.add(0, 0, 0, "Settings").setIcon(android.R.drawable.ic_menu_preferences);
		paramMenu.add(0, 4, 4, "Service").setIcon(android.R.drawable.ic_menu_view);
		paramMenu.add(0, 5, 5, "Exit").setIcon(android.R.drawable.ic_menu_close_clear_cancel);
		if (Setting.IS_LOG_ENABLE)
			paramMenu.add(0, 6, 6, "Log").setIcon(android.R.drawable.ic_menu_agenda);
		return true;
	}

	protected void onDestroy(){
		super.onDestroy();
		
	}

	public void onItemClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong){
		doAction((TaskListAdapters.ListViewItem)paramView.getTag(), Setting.CLICK_ACTION);
	}

	public boolean onItemLongClick(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong){
		TaskListAdapters.ListViewItem localListViewItem = (TaskListAdapters.ListViewItem)paramView.getTag();
		if ((Setting.LONG_PRESS_ACTION == 0) && (getPackageName().equals(localListViewItem.detailProcess.getPackageName()))){
			AutoStartReceiver.ClearNotification(this);
			finish();
		}else{
			doAction(localListViewItem, Setting.LONG_PRESS_ACTION);
		}
		return true;
	}

	public void refresh(){
		getRunningProcess();
		getListView().setAdapter(this.mAdapter);
		bindEvent();
		refreshMem();
		AutoStartReceiver.RefreshNotification(this);
	}

	@SuppressWarnings("unused")
	private class IgnoreTask extends AsyncTask<String, String, String>{
		private IgnoreTask(){
		}

		protected String doInBackground(String[] paramArrayOfString){
			AdvancedTaskKiller.this.IgnoreSystemApp();
			return "OK";
		}

		@SuppressWarnings("deprecation")
		protected void onPostExecute(String paramString){
			AdvancedTaskKiller.this.removeDialog(5);
			AdvancedTaskKiller.this.refresh();
		}
	}

	@SuppressWarnings("unused")
	private class KillAllTask extends AsyncTask<String, String, String>{
		private KillAllTask(){
		}

		protected String doInBackground(String[] paramArrayOfString){
			try{
				CommonLibrary.KillProcess(AdvancedTaskKiller.this, AdvancedTaskKiller.this.mDetailList, 
						AdvancedTaskKiller.this.mActivityManager, true);
				AdvancedTaskKiller.this.getRunningProcess();
				return "OK";
			}
			catch (Exception localException){
				android.util.Log.e("ATK", localException.toString());
			}
			return "OK";
		}

		protected void onPostExecute(String paramString){
			AdvancedTaskKiller.this.mAdapter = new TaskListAdapters.ProcessListAdapter(AdvancedTaskKiller.this, 
					AdvancedTaskKiller.this.mDetailList);
			AdvancedTaskKiller.this.getListView().setAdapter(AdvancedTaskKiller.this.mAdapter);
			AdvancedTaskKiller.this.refreshMem();
		}
	}

	@SuppressWarnings("deprecation")
		protected void onPostExecute(String paramString){
			AdvancedTaskKiller.this.removeDialog(1);
			if (paramString == null)
				return;
		}
	@Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
		startAppAd.onBackPressed();
    	Intent intent = new Intent(getApplicationContext(),BatteryActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    	startActivity(intent);
    	super.onBackPressed();
    }
	@Override
	public void onResume() {
		super.onResume();
		startAppAd.onResume();
		refresh();
	}
    
    @Override
	public void onPause() {
		super.onPause();
		startAppAd.onPause();
	}
    
	}
