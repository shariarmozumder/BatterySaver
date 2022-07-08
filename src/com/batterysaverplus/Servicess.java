package com.batterysaverplus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;

public class Servicess extends Service {
	
	private static final int NOTIFICATION_ID = 1;
	NotificationManager mNotificationManager;
	Intent intent;
    int level = -1;
    @Override public IBinder onBind(Intent intent) {
      // Not used
      return null;
    }
    
  public BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        
        
  		@Override
          public void onReceive(Context context, Intent intent) {
              level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                   Notification.Builder notificationBuilder = new Notification.Builder(
                                    Servicess.this);
                        notificationBuilder.setContentTitle("Battery Saver Plus");
                        notificationBuilder.setWhen(System.currentTimeMillis());
                        notificationBuilder.setSmallIcon(R.drawable.ic_launcher);
                        notificationBuilder.build();
                    
                        NotificationManager notifi = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification notification = new Notification(R.drawable.ic_launcher, getString(R.string.level), System.currentTimeMillis());

                        notification.flags = Notification.FLAG_ONGOING_EVENT;

                        Intent i = new Intent(getApplicationContext(),BatteryActivity.class); 
                       
                        PendingIntent penInt = PendingIntent.getActivity(getApplicationContext(), 0 , i , 0);

                        notification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), " " +level+"%", penInt);
                        notifi.notify(NOTIFICATION_ID,notification);
                        startForeground(NOTIFICATION_ID, notification);
                        Log.d("Raj", "Battery level is " + level);
          }
      };
    
   
    @Override public void onCreate() {
    	super.onCreate();
    	registerReceiver(batteryReceiver , new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onDestroy() {
      super.onDestroy();
      //unregisterReceiver(batteryReceiver);
    }

}
