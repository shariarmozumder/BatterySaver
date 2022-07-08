package com.batterysaverplus;
import java.util.Calendar;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

public class Updateservices extends Service {

private int batterylevel = 0;
private String batteryStatus = "";


public final static String TAG = "Bat";
public static Boolean debug     = true; 


private BroadcastReceiver myReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
            batterylevel = intent.getIntExtra("level", 0);

            final int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);


            final int status = intent.getIntExtra("status",
                    BatteryManager.BATTERY_STATUS_UNKNOWN);
            String strStatus;
            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                batteryStatus = "Charging";
            } else if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {
                batteryStatus = "Discharging";
            } else if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
                batteryStatus = "Not charging";
            } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                batteryStatus = "Full";
            } else {
                batteryStatus = "";
            }

            

            new Thread (new Runnable(){
                public void run(){
                    final Context c=getApplicationContext();
                    DBHelper db = new DBHelper(c);
                    db.record( batterylevel, status, plugged );
                    db.deleteOldEntries();
                    db.close();
                    if (debug) Log.i( TAG, "---------- Add record: " + batterylevel + " time: "+ Calendar.getInstance().getTimeInMillis() );    
                }
            }).start();
        }
    }
  };
@Override
public IBinder onBind(Intent arg0) {
    // TODO Auto-generated method stub
    return null;
}

@Override
public void onCreate() {
    // TODO Auto-generated method stub
    super.onCreate();

    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
    registerReceiver(myReceiver, intentFilter);
}

@Override
public void onDestroy() {
    // TODO Auto-generated method stub
    super.onDestroy();
    unregisterReceiver(myReceiver);
}

}