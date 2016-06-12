package com.mobappv2_1;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mobappv2_1.AndroidMultiPartEntity.ProgressListener;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UploadFile2 extends Activity {

	Bitmap bmp;
	TextView messageText, tvoutput;
    Button uploadButton, startBtn, showbtn;
    ImageView serverimg;
    ProgressDialog dialog = null;
    
    String disdata ; 
     
    int serverResponseCode = 0;
    int outfile = 0 ;
    
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    private ProgressDialog mProgressDialog;

    // File Path
    //MainMob fpath = new MainMob();
    //String uploadFilePath = fpath.selpath;    
    //final String uploadFileName = "katewinslet.jpg";
    //final String uploadFileName = "text.txt";
    //final String uploadFileName = "javatophp.java";
    
//    String uploadFilePath ;   //path from the previous Activity and do some change in code
    
    // for c programming
    final String uploadFilePath = "/mnt/sdcard/expt/cprog/";
    //final String uploadFilePath = "/mnt/sdcard/expt_evaluation/";
    final String uploadFileName = "search.c";   //Not needed when file path is selected from previous Activity
    //final String uploadFileName = "InsertionSort.c";
    //final String uploadFileName = "matmul.c";
    //final String uploadFileName = "mergesort.c";
    
    // for java programming
    //final String uploadFilePath = "/mnt/sdcard/expt/javaprog/"; 
    //final String uploadFileName = "search.java";
    //final String uploadFileName = "insertsort.java";
    //final String uploadFileName = "matmul.java";
    //final String uploadFileName = "mergesort.java";

    String upLoadServerUri = null;
//    String serverip = "http://192.168.12.97:80/android_connect/";
    String serverip = "http://54.69.110.134:80/android_connect/";
    
    // for mobile id of device
    String m_androidId;
    
    // PHP script path
    String postReceiverUrl = serverip + "test2.php";
    
    // Timing calculation
    long start, end1, end2, upt, tt;
    String t1;
    long totalSize = 0;
    
    //RegMain2 rm2 = new RegMain2();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.upload_file);

		//uploadButton = (Button)findViewById(R.id.uploadButton);
        messageText  = (TextView)findViewById(R.id.messageText);
        
        // android mobile id of device 
     	m_androidId = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
        
        // Get the bundle
//        Bundle bundle = getIntent().getExtras();
        // Extract the data
//        uploadFilePath = bundle.getString("key");

        // File uploading to server
     	//start = SystemClock.uptimeMillis();
        new Uploadserver().execute();
        /*
        end1 = SystemClock.uptimeMillis();
    	upt = end1 - start;
    	t1 = String.valueOf(upt);*/
    	//Toast.makeText(getApplicationContext(), t, Toast.LENGTH_SHORT).show(); 
        
        
/*      Intent i = getIntent();
        String opmsg = i.getStringExtra("m");
        
        System.out.println(opmsg);
*/    
        tvoutput = (TextView) findViewById(R.id.textView);
        
        LocalBroadcastManager.getInstance(this).registerReceiver(mHandleReceiver, new IntentFilter("gcm-result"));
        
	}

	public class Uploadserver extends AsyncTask<Void, Integer, String> {
	
		@Override
		protected String doInBackground(Void... params) {
			
			return uploadcode();
		}
	
		@SuppressWarnings("deprecation")
		private String uploadcode() {		
			String responseString = null;
			start = SystemClock.uptimeMillis();	
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(postReceiverUrl);
	
			try {
				AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
					new ProgressListener() {
	
						@Override
						public void transferred(long num) {
							publishProgress((int) ((num / (float) totalSize) * 100));
						}
				});
				
//				File sourceFile = new File(uploadFilePath); //change here
				File sourceFile = new File(uploadFilePath + uploadFileName);
				//System.out.println("File path : " + sourceFile.getAbsolutePath());
	
				// Adding file data to http body
				entity.addPart("uploaded_file", new FileBody(sourceFile));
	
				// Extra parameters if you want to pass to server
				entity.addPart("mid",new StringBody(m_androidId));
				//entity.addPart("email", new StringBody("abc@gmail.com"));
				
				totalSize = entity.getContentLength();
				httppost.setEntity(entity);
	
				// Making server call
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity r_entity = response.getEntity();
	
				int statusCode = response.getStatusLine().getStatusCode();
				
				System.out.println(statusCode);
				if (statusCode == 200) {
					// Server response
					responseString = EntityUtils.toString(r_entity);
				} else {
					responseString = "Error occurred! Http Status Code: " + statusCode;
				}
	
			} catch (ClientProtocolException e) {
				responseString = e.toString();
			} catch (IOException e) {
				responseString = e.toString();
			}
	
			end1 = SystemClock.uptimeMillis();
	    	upt = end1 - start;
	    	t1 = String.valueOf(upt);
			
			return responseString;
			
		}	

		@Override
		protected void onPostExecute(String result) {
			// below both log and alert shows the same response
			Log.i("Response from server: " , result);
			// showing the server response in an alert dialog
			showAlert(result);
	
			super.onPostExecute(result);
		}
			
	}
	
	private void showAlert(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message).setTitle("Response from server: ")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// do nothing
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
		
	private final BroadcastReceiver mHandleReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			String newMessage = intent.getExtras().getString("message");
	        
			end2 = SystemClock.uptimeMillis();
        	tt = end2 - start;
        	String t = String.valueOf(tt);
        	Toast.makeText(getApplicationContext(), "Upload Time: " + t1 + "\nTotal Time: " + t, Toast.LENGTH_LONG)
        	.show();
			
			//System.out.println("FileUpload: Hello World... " + newMessage) ;
			
			tvoutput.setText(newMessage);        
		}
		
	};
	
	@Override
	protected void onDestroy() {
	  // Unregister since the activity is about to be closed.
	  LocalBroadcastManager.getInstance(this).unregisterReceiver(mHandleReceiver);
	  super.onDestroy();
	}	
}