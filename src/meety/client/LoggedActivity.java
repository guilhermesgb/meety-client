package meety.client;

import java.util.HashMap;
import java.util.Map;

import meety.client.http.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


public class LoggedActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logged);
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

		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;

		CharSequence toastText = "Checking 'who is logged' status...";
		Toast toast = Toast.makeText(context, toastText, duration);
		toast.show();
		
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
				String responseBody = (String) response.get("body");

				if ( responseCode == 200 ){

					toastText = responseBody;
					toast = Toast.makeText(context, toastText, duration);
					toast.show();
					
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
//		// TODO Popup window to inform that the status of the request: 'calling'
//		// or 'missed'
//
		doIsLoggedHTTPRequest();

//		boolean startSession = true;
//
//		if (startSession) {
//			attemptMeetySession();
//		}
		
	}

//	private void attemptMeetySession() {
//		Intent intentAttMS = new Intent(this, AttemptingMeetySessionActivity.class);
//		startActivity(intentAttMS);
//	}

}