package meety.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


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

	public void sessionRequest(View view) {
		// TODO Popup window to inform that the status of the request: 'calling'
		// or 'missed'

		boolean startSession = true;

		if (startSession) {
			attemptMeetySession();
		}
	}

	private void attemptMeetySession() {
		Intent intentAttMS = new Intent(this, AttemptingMeetySessionActivity.class);
		startActivity(intentAttMS);
	}

}