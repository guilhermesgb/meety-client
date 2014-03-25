package meety.client;

import java.util.HashMap;
import java.util.Map;

import meety.client.http.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class AttemptingMeetySessionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attempting_meety_session);
		TextView text_answer_fail = (TextView) findViewById(R.id.text_answer_fail);
		text_answer_fail.setVisibility(View.GONE);

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {

				Intent intentFromPreviousActivity = getIntent();
				attemptMeetySession(intentFromPreviousActivity);
			}

		}, 3000);

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
				String responseBody = (String) response.get("body");
				
				Context context = getApplicationContext();
				int duration = Toast.LENGTH_SHORT;
				CharSequence toastText = responseBody;
				Toast toast = Toast.makeText(context, toastText, duration);
				toast.show();
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
	
	private void attemptMeetySession(Intent intentFromPreviousActivity) {

		String username = intentFromPreviousActivity.getStringExtra("username");
		
		if (doCallHTTPRequest(username, "call")) {
			callMeetySessionActivity();
		} else {
			denyAnswer();
		}
	}

	private void denyAnswer() {
		TextView logo_meety = (TextView) findViewById(R.id.logo_meety_not_session);
		logo_meety.setTextColor(this.getResources().getColor(
				R.color.orange_deny_login));
		TextView text_answer_fail = (TextView) findViewById(R.id.text_answer_fail);
		text_answer_fail.setVisibility(View.VISIBLE);

		ProgressBar progressBarView = (ProgressBar) findViewById(R.id.progressBarViewAttMeetySession);
		progressBarView.setVisibility(View.GONE);

	}

	private void callMeetySessionActivity() {
		Intent intent = new Intent(this, MeetySessionActivity.class);
		startActivity(intent);
	}
}