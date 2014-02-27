package meety.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

	private void attemptLogin(Intent intentFromPreviousActivity) {
		String username = intentFromPreviousActivity.getStringExtra("username");
		String password = intentFromPreviousActivity.getStringExtra("password");
		
		System.out.println("ATTEMPTING LOGIN!");
		
		try {
			URL url;
			HttpURLConnection urlConn;
//			DataOutputStream printout;
			url = new URL ("http://meety-server.herokuapp.com/login");
			urlConn = (HttpURLConnection)url.openConnection();
			urlConn.setRequestMethod("POST");
//			urlConn.setDoInput (true);
//			urlConn.setDoOutput (true);
			urlConn.setUseCaches (false);
//			urlConn.setRequestProperty("Content-Type","application/json");   
			urlConn.setRequestProperty("Host", "meety-server.herokuapp.com");
			urlConn.setRequestProperty("Authorization", "Basic "+
					Base64.encodeToString((username+":"+password).getBytes(), Base64.DEFAULT));
			System.out.println("ABOUT TO ISSUE REQUEST TO SERVER");
			urlConn.connect();
//			//Create JSONObject here
//			JSONObject jsonParam = new JSONObject();
//			jsonParam.put("username", username);
//			jsonParam.put("password", password);
//			printout = new DataOutputStream(urlConn.getOutputStream ());
//			printout.writeUTF(URLEncoder.encode(jsonParam.toString(),"UTF-8"));
//			printout.flush ();
//			printout.close ();

			System.out.println("AWAITING RESPONSE");
			int httpResult =urlConn. getResponseCode();
		    if(httpResult ==HttpURLConnection.HTTP_OK){
		    	System.out.println("RESPONSE OK");
				callLoggedInActivity();
		    }else{
		    	System.out.println("RESPONSE FAIL: "+Integer.toString(httpResult));
		    }
		}catch (MalformedURLException e){
			System.out.print("MALFORMED URL => ");
			System.out.println(e.getMessage());
		}catch (IOException e){
			System.out.print("IO EXCEPTION => ");
			System.out.println(e.getMessage());
//		}catch (Exception e){
//			System.out.print("WTF just happened?? => ");
//			System.out.println(e.getMessage());
		}
    	denyLogIn();
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