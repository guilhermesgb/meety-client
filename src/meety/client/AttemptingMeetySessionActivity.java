package meety.client;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

/**
 * 1) Wait for session's partner response via server. ´
 * 2) Collect the GPS location before the Meety Session starts, so the users map position
 * will be placed in since the beginging of the next activity.
 * 
 * @author Tales Tenorio de Souza Pimentel This class is used for two reasons 
 
 * 
 */
public class AttemptingMeetySessionActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.attempting_meety_session);
		setUpActivity();
		System.out.println("AttemptingMeetySession");

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {

				attemptMeetySession();
			}

		}, 3000);

	}

	private void setUpActivity() {
		TextView text_answer_fail = (TextView) findViewById(R.id.text_answer_fail);
		text_answer_fail.setVisibility(View.GONE);
	}

	private void attemptMeetySession() {
		boolean answer = true;

		if (answer) {
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
		Intent intent = new Intent(getThis(), MeetySessionActivity.class);
		startActivity(intent);

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
