package com.mobappv2_1;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.webkit.WebView.FindListener;
import android.widget.TextView;
import android.widget.Toast;

public class MessageHandlerClient extends IntentService {

	String mes; 
    private Handler handler;
    
    TextView txtv;
    
    // Timing calculation
    long start, end1, gcmt ;
    String t1;
    
    public MessageHandlerClient() {
        super("MHClient");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }
    
    @Override
    public void onHandleIntent(Intent intent) {
    	//start = SystemClock.uptimeMillis();
    	
    	Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        
        // The getMessageType() intent parameter must be the intent you received in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        mes = extras.getString("m");
        //System.out.println(mes) ;     	
        
        Log.i("GCM", "Received : (" + messageType + ")  "+ extras.getString("m")); 
        
        sendMessage(mes) ;
        
        BroadcastReceiverClient.completeWakefulIntent(intent);
        /*
        end1 = SystemClock.uptimeMillis();
    	gcmt = end1 - start;
    	t1 = String.valueOf(gcmt);*/
    	//showToast(t1);
    }

    
	 // Send an Intent with an action named "gcm-result". The Intent sent should 
	 // be received by the ReceiverActivity.
	 private void sendMessage(String message) {
	   Log.d("sender", "Broadcasting message");
	   Intent intent = new Intent("gcm-result");
	   // You can also include some extra data.
	   intent.putExtra("message", message);
	   LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	 }
    
    
    public void showToast(final String t12){
        handler.post(new Runnable() {
            public void run() {
            	//fup.tvoutput.setText(mes); 
                Toast.makeText(getApplicationContext(),t12 , Toast.LENGTH_LONG).show();
            }
         });
    }
}