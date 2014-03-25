package meety.client;

import java.util.HashMap;
import java.util.Map;

import meety.client.http.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class LoggedActivity extends Activity {

	private static HashMap<String, JSONObject> incomingCalls = new HashMap<String, JSONObject>();
	
	private static boolean doMonitorCallsHTTPRequest(Context context){

		Map<String, String> pairs = new HashMap<String, String>();
		pairs.put("Host", "meety-server.herokuapp.com");
		pairs.put("Content-Type", "application/json");
		pairs.put("Accept", "application/json");
		JSONObject headers = new JSONObject(pairs);

		JSONObject response = HttpUtils.
				doGETHttpRequest("http://meety-server.herokuapp.com/call", headers);

		if ( response == null ){
			return false;
		} else
			try {
				Integer responseCode = (Integer) response.get("code");
				String responseBody = (String) response.get("body");
				
				JSONObject calls = new JSONObject(responseBody).getJSONObject("calls");
				JSONArray received = calls.getJSONArray("received");
				
				synchronized(LoggedActivity.class){
					
					for ( int i=0; i<received.length(); i++ ){
						JSONObject call = received.getJSONObject(i);
						if ( call.get("status").equals("seen") ){
							String sender = (String) call.get("sender");
							incomingCalls.put(sender, call);
							int duration = Toast.LENGTH_SHORT;
							CharSequence toastText = "Incoming call from: "+sender+"...";
							Toast toast = Toast.makeText(context, toastText, duration);
							toast.show();
						}
					}
				}
				
				if ( responseCode == 200 ){
					return true;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
	}
	
	static class MonitorCalls implements Runnable{

		static final Handler handler = new Handler();
		
		private final Context context;
		public MonitorCalls(Context context){
			this.context = context;
		}
		
		@Override
		public void run() {
			LoggedActivity.doMonitorCallsHTTPRequest(context);
			handler.postDelayed(this, 30000);
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logged);
		MonitorCalls.handler.post(new MonitorCalls(getApplicationContext()));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.meety_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.about) {
			// TODO method call for "about"
			return true;
		} else if (itemId == R.id.settings) {
			// TODO method call for "settings"
			return true;
		} else if (itemId == R.id.friendlist) {
			// TODO method call for "friendlist"
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	private boolean doIsLoggedHTTPRequest(){

		Map<String, String> pairs = new HashMap<String, String>();
		pairs.put("Host", "meety-server.herokuapp.com");
		pairs.put("Accept", "application/json");
		JSONObject headers = new JSONObject(pairs);

		JSONObject response = HttpUtils.
				doGETHttpRequest("http://meety-server.herokuapp.com/logged", headers);

		if ( response == null ){
			return false;
		} else
			try {
				Integer responseCode = (Integer) response.get("code");

				if ( responseCode == 200 ){
					return true;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return false;
	}

	public void sessionRequest(View view) {

		final EditText recipientUsernameText = (EditText) findViewById(R.id.text_recipient_username);
		
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;
		
		if ( TextUtils.isEmpty(recipientUsernameText.getText().toString()) ){
			CharSequence toastText = "Recipient username cannot be null";
			Toast toast = Toast.makeText(context, toastText, duration);
			toast.show();
			return;
		}

		if ( doIsLoggedHTTPRequest() ){
			
			Intent intentAttMS = new Intent(this, AttemptingMeetySessionActivity.class);
			intentAttMS.putExtra("username", recipientUsernameText.getText().toString());
			startActivity(intentAttMS);
		}
		else{
			CharSequence toastText = "Server won't recognize you, please authenticate again";
			Toast toast = Toast.makeText(context, toastText, duration);
			toast.show();
			return;
		}
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	System.exit(0);
	    	//android.os.Process.killProcess(android.os.Process.myPid());
	    }
	    return super.onKeyDown(keyCode, event);
	}

}