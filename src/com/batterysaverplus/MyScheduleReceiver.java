package com.batterysaverplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyScheduleReceiver extends BroadcastReceiver {


    // Restart service every 30 min
    public static final long REPEAT_TIME = 30*1000*4;//1800000 ;

    @Override
    public void onReceive(Context context, Intent intent) {
    	Intent service = new Intent(context, Servicess.class);
    		context.startService(service);
}}