package meety.client;

import java.util.HashMap;
import java.util.Map;

import meety.client.http.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;


public class AttemptingLoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attempting_log_in);

		TextView text_login_fail = (TextView) findViewById(R.id.text_login_fail);
		text_login_fail.setVisibility(View.GONE);
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				
				Intent intentFromPreviousActivity = getIntent();
				attemptLogin(intentFromPreviousActivity);
			}

		}, 2000);
	}

	private boolean doLoginHTTPRequest(String username, String password){

		Map<String, String> pairs = new HashMap<String, String>();
		pairs.put("Host", "meety-server.herokuapp.com");
		pairs.put("Accept", "application/json");
		pairs.put("Authorization", "Basic "+
				Base64.encodeToString((username+":"+password).getBytes(), Base64.NO_WRAP));
		JSONObject headers = new JSONObject(pairs);
		
		JSONObject response = HttpUtils.doPOSTHttpRequest("http://meety-server.herokuapp.com/login", headers);
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
	
	private void attemptLogin(Intent intentFromPreviousActivity) {
		String username = intentFromPreviousActivity.getStringExtra("username");
		String password = intentFromPreviousActivity.getStringExtra("password");
		
		if ( doLoginHTTPRequest(username, password) || (username.equals("username") && password.equals("password") ) ){ //just to enable login skipts
			callLoggedInActivity();
		}
		else {
			denyLogIn();
		}
	}

	
	private void denyLogIn() {
		TextView logo_meety = (TextView) findViewById(R.id.logo_meety);
		logo_meety.setTextColor(this.getResources().getColor(R.color.orange_deny_login));
		
		ProgressBar progressBarView = (ProgressBar) findViewById(R.id.progressBarView);
		progressBarView.setVisibility(View.GONE);
		
		Integer colorFrom = getResources().getColor(R.color.white);
		Integer colorTo = getResources().getColor(R.color.orange_deny_login);
		ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
		colorAnimation.addUpdateListener(new AnimatorUpdateListener() {

		    @Override
		    public void onAnimationUpdate(ValueAnimator animator) {
				TextView text_login_fail = (TextView) findViewById(R.id.text_login_fail);
				text_login_fail.setVisibility(View.VISIBLE);
		        text_login_fail.setTextColor((Integer)animator.getAnimatedValue());
		    }

		});
		colorAnimation.setDuration(1597); //17th fibonacci's term
		colorAnimation.setRepeatMode(Animation.REVERSE);
		colorAnimation.setRepeatCount(Animation.INFINITE);
		colorAnimation.start();
	}

	private void callLoggedInActivity() {
		Intent intentMS = new Intent(this, LoggedActivity.class);
		startActivity(intentMS);
		
	}

}