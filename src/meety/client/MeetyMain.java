package meety.client;

import java.util.HashMap;
import java.util.Map;

import meety.client.http.HttpUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


public class MeetyMain extends Activity {

	public static boolean WAIT_FOR_CALL = false;
	public static boolean WAIT_FOR_RESPONSE = false;

	private boolean doIsLoggedHTTPRequest(){

		Map<String, String> pairs = new HashMap<String, String>();
		pairs.put("Host", "meety-server.herokuapp.com");
		pairs.put("Accept", "application/json");
		JSONObject headers = new JSONObject(pairs);

		JSONObject response = HttpUtils.
				doGETHttpRequest("http://meety-server.herokuapp.com/logged", headers);

		if ( response == null ){
			return false;
		} else {
			try {
				Integer responseCode = (Integer) response.get("code");
				String responseBody = (String) response.get("body");
				
				if ( responseCode == 200 ){
					String logged = new JSONObject(responseBody).getString("status").split(" ")[0];
					if ( !logged.equals("nobody") ){
						Context context = getApplicationContext();
						int duration = Toast.LENGTH_SHORT;
						CharSequence toastText = "Welcome to Meety, "+logged+"...";
						Toast toast = Toast.makeText(context, toastText, duration);
						toast.show();
						return true;
					}
				}
			} catch (Exception e) {
				System.out.println("doIsLoggedHTTPRequest EXCEPTION");
				CharSequence toastText = "doIsLoggedHTTPRequest EXCEPTION";
				Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG);
				toast.show();
				e.printStackTrace();
			}
			return false;
		}
	}
	
	private static boolean doAnswerCallHTTPRequest(String username, String answer){

		Map<String, String> pairs = new HashMap<String, String>();
		pairs.put("Host", "meety-server.herokuapp.com");
		pairs.put("Accept", "application/json");
		pairs.put("Content-Type", "application/json");
		JSONObject headers = new JSONObject(pairs);

		pairs.clear();
		pairs.put("username", username);
		pairs.put("answer", answer);
		JSONObject payload = new JSONObject(pairs);
		
		JSONObject response = HttpUtils.
				doPOSTHttpRequest("http://meety-server.herokuapp.com/answer", headers, payload);

		if ( response == null ){
			return false;
		} else
			try {
				Integer responseCode = (Integer) response.get("code");

				if ( responseCode == 200 ){
					return true;
				}
			} catch (Exception e) {
				System.out.println("doRespondCallHTTPRequest EXCEPTION");
				e.printStackTrace();
			}
			return false;
	}
	
	private static boolean doMonitorCallsHTTPRequest(final Handler handler, final Context context, final Activity activity){

		System.out.println("Monitoring calls...");
		
		Map<String, String> pairs = new HashMap<String, String>();
		pairs.put("Host", "meety-server.herokuapp.com");
		pairs.put("Content-Type", "application/json");
		pairs.put("Accept", "application/json");
		JSONObject headers = new JSONObject(pairs);

		JSONObject response = HttpUtils.doGETHttpRequest("http://meety-server.herokuapp.com/call", headers);

		if ( response == null ){
			return false;
		} else
			try {
				Integer responseCode = (Integer) response.get("code");
				String responseBody = (String) response.get("body");
				
				JSONObject calls = new JSONObject(responseBody).getJSONObject("calls");
				JSONArray received = calls.getJSONArray("received");
				
				for ( int i=0; i<received.length(); i++ ){
					JSONObject call = received.getJSONObject(i);
					if ( call.get("status").equals("seen") ){
						
						MeetyMain.WAIT_FOR_CALL = false;
						
						final String sender = (String) call.get("sender");
						int duration = Toast.LENGTH_SHORT;
						CharSequence toastText = "Incoming call from: "+sender+"...";
						Toast toast = Toast.makeText(context, toastText, duration);
						toast.show();
						
						AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
						alertDialogBuilder.setTitle("Call from: "+sender+"...");
						alertDialogBuilder
							.setMessage("Share geolocation with "+sender+"?")
							.setCancelable(false)
							.setPositiveButton("Accept",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int id) {
									if ( doAnswerCallHTTPRequest(sender, "accepted") ){
										CharSequence toastText = "You've just accepted call from "+sender+"...";
										Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_SHORT);
										toast.show();
										dialog.cancel();
										Intent meetySessionActivity = new Intent();
										meetySessionActivity.setClass(context, MeetySession.class);
										activity.startActivityForResult(meetySessionActivity, MeetySession.REQUEST_CODE);
									}
									else{
										CharSequence toastText = sender+" has cancelled the call...";
										Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_SHORT);
										toast.show();
									}
								}
							})
							.setNegativeButton("Decline",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,int id) {
									if ( doAnswerCallHTTPRequest(sender, "denied") ){
										CharSequence toastText = "You've just denied call from "+sender+"...";
										Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_SHORT);
										toast.show();
									}
									dialog.cancel();

									handler.postDelayed(new Runnable() {
										public void run() {
											MeetyMain.WAIT_FOR_CALL = true;
											handler.postDelayed(new MonitorCalls(handler, context, activity), 5000);
										}
									}, 5000);
									
							}
						});
			 
						AlertDialog alertDialog = alertDialogBuilder.create();
						alertDialog.show();
					}
				}
				
				if ( responseCode == 200 ){
					return true;
				}
			} catch (Exception e) {
				System.out.println("doMonitorCallsHTTPRequest EXCEPTION");
				CharSequence toastText = "doMonitorCallsHTTPRequest EXCEPTION";
				Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_LONG);
				toast.show();
				e.printStackTrace();
			}
			return false;
	}
	
	static class MonitorCalls implements Runnable{

		private final Handler handler;
		private final Context context;
		private final Activity activity;
		public MonitorCalls(Handler handler, Context context, Activity activity){
			this.handler = handler;
			this.context = context;
			this.activity = activity;
		}
		
		@Override
		public void run() {
			MeetyMain.doMonitorCallsHTTPRequest(handler, context, activity);
			if ( MeetyMain.WAIT_FOR_CALL ){
				handler.postDelayed(this, 5000);
			}
		}
	}

	private boolean doCallHTTPRequest(String username, String action){

		Map<String, String> pairs = new HashMap<String, String>();
		pairs.put("Host", "meety-server.herokuapp.com");
		pairs.put("Content-Type", "application/json");
		pairs.put("Accept", "application/json");
		JSONObject headers = new JSONObject(pairs);

		pairs.clear();
		pairs.put("username", username);
		pairs.put("action", action);
		JSONObject payload = new JSONObject(pairs);
		
		JSONObject response = HttpUtils.
				doPOSTHttpRequest("http://meety-server.herokuapp.com/call", headers, payload);

		if ( response == null ){
			return false;
		} else
			try {
				Integer responseCode = (Integer) response.get("code");
				if ( responseCode == 200 ){
					return true;
				}
				else{
					return false;
				}
			} catch (Exception e) {
				System.out.println("doCallHTTPRequest EXCEPTION");
				CharSequence toastText = "doCallHTTPRequest EXCEPTION";
				Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG);
				toast.show();
				e.printStackTrace();
			}
			return false;
	}

	private static boolean doWaitForResponseHTTPRequest(Context context, final Activity activity, String recipient){

		System.out.println("Waiting for response...");
		
		Map<String, String> pairs = new HashMap<String, String>();
		pairs.put("Host", "meety-server.herokuapp.com");
		pairs.put("Content-Type", "application/json");
		pairs.put("Accept", "application/json");
		JSONObject headers = new JSONObject(pairs);

		JSONObject response = HttpUtils.doGETHttpRequest("http://meety-server.herokuapp.com/call", headers);

		if ( response == null ){
			return false;
		} else
			try {
				Integer responseCode = (Integer) response.get("code");
				String responseBody = (String) response.get("body");
				
				JSONObject calls = new JSONObject(responseBody).getJSONObject("calls");
				JSONArray sent = calls.getJSONArray("sent");
				
				int duration = Toast.LENGTH_SHORT;

				for ( int i=0; i<sent.length(); i++ ){
					JSONObject call = sent.getJSONObject(i);
					if ( call.get("recipient").equals(recipient) && call.get("status").equals("accepted") ){

						MeetyMain.WAIT_FOR_RESPONSE = false;

						final Button cancelCallButton = (Button) activity.findViewById(R.id.meety_main_session_cancel_button);
						cancelCallButton.setVisibility(View.INVISIBLE);
						final ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.meeety_main_progress);
						progressBar.setVisibility(View.VISIBLE);
						
						CharSequence toastText = recipient+" has accepted your call...";
						Toast toast = Toast.makeText(context, toastText, duration);
						toast.show();
						
						Intent meetySessionActivity = new Intent();
						meetySessionActivity.setClass(context, MeetySession.class);
						activity.startActivityForResult(meetySessionActivity, MeetySession.REQUEST_CODE);
					}
					else if ( call.get("recipient").equals(recipient) && call.get("status").equals("denied") ){

						MeetyMain.WAIT_FOR_RESPONSE = false;

						final Button cancelCallButton = (Button) activity.findViewById(R.id.meety_main_session_cancel_button);
						cancelCallButton.setVisibility(View.INVISIBLE);
						final ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.meeety_main_progress);
						progressBar.setVisibility(View.INVISIBLE);
						
						CharSequence toastText = recipient+" has denied your call...";
						Toast toast = Toast.makeText(context, toastText, duration);
						toast.show();
					}
				}
				
				if ( responseCode == 200 ){
					return true;
				}
			} catch (Exception e) {
				System.out.println("doWaitForResponseHTTPRequest EXCEPTION");
				CharSequence toastText = "doWaitForResponseHTTPRequest EXCEPTION";
				Toast toast = Toast.makeText(context, toastText, Toast.LENGTH_LONG);
				toast.show();
				e.printStackTrace();
			}
			return false;
	}
	
	static class MonitorResponse implements Runnable{

		private final Handler handler;
		private final Context context;
		private final Activity activity;
		private String recipient;

		public MonitorResponse(Handler handler, Context context, Activity activity, String recipient){
			this.handler = handler;
			this.context = context;
			this.activity = activity;
			this.recipient = recipient;
		}
		
		@Override
		public void run() {
			MeetyMain.doWaitForResponseHTTPRequest(context, activity, recipient);
			if ( MeetyMain.WAIT_FOR_RESPONSE ){
				handler.postDelayed(this, 5000);
			}
			else{
				final Button startCallButton = (Button) activity.findViewById(R.id.meety_main_session_call_button);
				startCallButton.setVisibility(View.VISIBLE);
				final Button cancelCallButton = (Button) activity.findViewById(R.id.meety_main_session_cancel_button);
				cancelCallButton.setVisibility(View.INVISIBLE);
				final ProgressBar progressBar = (ProgressBar) activity.findViewById(R.id.meeety_main_progress);
				progressBar.setVisibility(View.INVISIBLE);				
			}
		}
		
	}
	
	private String current_recipient = null;
	private Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meety_main);
		
		final EditText recipientUsernameText = (EditText) findViewById(R.id.meety_main_username);
		final Button startCallButton = (Button) findViewById(R.id.meety_main_session_call_button);
		final Button cancelCallButton = (Button) findViewById(R.id.meety_main_session_cancel_button);
		final ProgressBar progressBar = (ProgressBar) findViewById(R.id.meeety_main_progress);
		recipientUsernameText.setVisibility(View.INVISIBLE);
		startCallButton.setVisibility(View.INVISIBLE);
		cancelCallButton.setVisibility(View.INVISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		
		if ( !doIsLoggedHTTPRequest() ){
			Intent loginActivity = new Intent();
			loginActivity.setClass(getApplicationContext(), MeetyLogin.class);
			startActivityForResult(loginActivity, MeetyLogin.REQUEST_CODE);
		}
		else{
			startActivity();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if ( requestCode == MeetyLogin.REQUEST_CODE ){
			if ( resultCode == MeetyLogin.RESULT_CODE_OK ){
				startActivity();
			}
			else{
				finish();
			}
		}
		else if ( requestCode == MeetySession.REQUEST_CODE ){
			if ( resultCode == MeetySession.RESULT_CODE_OK ){
				startActivity();
			}else{
				CharSequence toastText = "IT SEEMS LIKE THE SESSION WAS ABORTED UNEXPECTEDLY";
				Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG);
				toast.show();
				startActivity();
			}
		}
	}
	
	private void startActivity(){
		
		final EditText recipientUsernameText = (EditText) findViewById(R.id.meety_main_username);
		final Button startCallButton = (Button) findViewById(R.id.meety_main_session_call_button);
		final Button cancelCallButton = (Button) findViewById(R.id.meety_main_session_cancel_button);
		final ProgressBar progressBar = (ProgressBar) findViewById(R.id.meeety_main_progress);
		progressBar.setVisibility(View.INVISIBLE);
		recipientUsernameText.setVisibility(View.VISIBLE);
		startCallButton.setVisibility(View.VISIBLE);
		cancelCallButton.setVisibility(View.INVISIBLE);

		this.handler = new Handler();
		
		MeetyMain.WAIT_FOR_CALL = true;
		MeetyMain.WAIT_FOR_RESPONSE = false;
		handler.post(new MonitorCalls(handler, getApplicationContext(), this));
	}

	public void sessionCall(View view) {

		final EditText recipientUsernameText = (EditText) findViewById(R.id.meety_main_username);
		
		Context context = getApplicationContext();
		CharSequence toastText;
		int duration = Toast.LENGTH_SHORT;
		
		if ( TextUtils.isEmpty(recipientUsernameText.getText().toString()) ){
			toastText = "Recipient username cannot be blank";
			Toast toast = Toast.makeText(context, toastText, duration);
			toast.show();
			return;
		}

		current_recipient = recipientUsernameText.getText().toString();
		
		if ( doCallHTTPRequest(current_recipient, "call") ){
			
			final Button startCallButton = (Button) findViewById(R.id.meety_main_session_call_button);
			startCallButton.setVisibility(View.INVISIBLE);
			final Button cancelCallButton = (Button) findViewById(R.id.meety_main_session_cancel_button);
			cancelCallButton.setVisibility(View.VISIBLE);
			final ProgressBar progressBar = (ProgressBar) findViewById(R.id.meeety_main_progress);
			progressBar.setVisibility(View.VISIBLE);
			recipientUsernameText.setEnabled(false);

			toastText = "Calling "+current_recipient+"...";
			Toast toast = Toast.makeText(context, toastText, duration);
			toast.show();

			MeetyMain.WAIT_FOR_CALL = false;
			MeetyMain.WAIT_FOR_RESPONSE = true;
			handler.post(new MonitorResponse(handler, getApplicationContext(), this, current_recipient));
		
		}
		else{

			toastText = "Could not call "+current_recipient+"...";
			Toast toast = Toast.makeText(context, toastText, duration);
			toast.show();
		}
		
	}

	public void cancelCall(View view) {

		final EditText recipientUsernameText = (EditText) findViewById(R.id.meety_main_username);
		
		Context context = getApplicationContext();
		CharSequence toastText;
		int duration = Toast.LENGTH_SHORT;
		
		if ( doCallHTTPRequest(current_recipient, "cancel") ){
			
			recipientUsernameText.setEnabled(true);
			
			final Button startCallButton = (Button) findViewById(R.id.meety_main_session_call_button);
			startCallButton.setVisibility(View.VISIBLE);
			final Button cancelCallButton = (Button) findViewById(R.id.meety_main_session_cancel_button);
			cancelCallButton.setVisibility(View.INVISIBLE);
			final ProgressBar progressBar = (ProgressBar) findViewById(R.id.meeety_main_progress);
			progressBar.setVisibility(View.INVISIBLE);

			toastText = "Cancelling call to "+current_recipient+"...";
			Toast toast = Toast.makeText(context, toastText, duration);
			toast.show();

			MeetyMain.WAIT_FOR_CALL = true;
			MeetyMain.WAIT_FOR_RESPONSE = false;
			handler.post(new MonitorCalls(handler, getApplicationContext(), this));
			
		}
		else{

			toastText = "Could not cancel call to "+current_recipient+"...";
			Toast toast = Toast.makeText(context, toastText, duration);
			toast.show();
		}
		
	}
	
}