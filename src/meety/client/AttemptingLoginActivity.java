package meety.client;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;


public class AttemptingLoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attempting_log_in);
		System.out.println("AttemptingLogIn");
		setUpActivity();
		
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				
				Intent intentFromPreviousActivity = getIntent();
				attemptLogIn(intentFromPreviousActivity);
			}

		}, 2000);
		
		//attemptLogIn(intentFromPreviousActivity);
		

	}

	private void setUpActivity() {
		TextView text_login_fail = (TextView) findViewById(R.id.text_login_fail);
		text_login_fail.setVisibility(View.GONE);
	}

	private void attemptLogIn(Intent intentFromPreviousActivity) {
		String username = intentFromPreviousActivity.getStringExtra("username");
		String password = intentFromPreviousActivity.getStringExtra("password");
		System.out.println("*=*=*=*=*=*=*=*=*=*=*=*=*=*=*");
		System.out.println("USERNAME " + username);
		System.out.println("PASSWORD " + password);
		System.out.println("*=*=*=*=*=*=*=*=*=*=*=*=*=*=*");
		
		//TODO send username and password to attempt login
		
		boolean authenticated = (username.equals("username") && password.equals("meety"));
		System.out.println("authenticated-----------------------" + authenticated);
		if(authenticated){
			callLoggedInActivity();
		}else{
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
		Intent intentMS = new Intent(getThis(), LoggedActivity.class);
		startActivity(intentMS);
		
	}

	/**
	 * Method necessary to get this class object from an inner scope like
	 * OnClickListener
	 * 
	 * @return
	 */
	private Activity getThis() {
		return this;
	}

}