package meety.client;

import java.util.HashMap;
import java.util.Map;

import meety.client.http.HttpUtils;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


public class MeetyLogin extends Activity {

	public static final int REQUEST_CODE = 101;
	public static final int RESULT_CODE_OK = 1010;

	private boolean doRegisterHTTPRequest(String username, String password){

		Map<String, String> pairs = new HashMap<String, String>();
		pairs.put("Host", "meety-server.herokuapp.com");
		pairs.put("Content-Type", "application/json; charset=utf-8");
		pairs.put("Accept", "application/json");
		JSONObject headers = new JSONObject(pairs);

		pairs.clear();
		pairs.put("username", username);
		pairs.put("password", password);
		JSONObject payload = new JSONObject(pairs);
		
		JSONObject response = HttpUtils.
				doPOSTHttpRequest("http://meety-server.herokuapp.com/register", headers, payload);

		if ( response == null ){
			return false;
		} else
			try {
				Integer responseCode = (Integer) response.get("code");
				String responseBody = (String) response.get("body");
				CharSequence toastText = "Code: "+responseCode;
				Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
				toastText = "Body: "+responseBody;
				Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
				String responsePayload = (String) response.get("payload_received");
				toastText = "Payload: "+responsePayload;
				Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
				if ( responseCode == 200 ){
					return true;
				}
			} catch (Exception e) {
				System.out.println("doRegisterHTTPRequest EXCEPTION");
				CharSequence toastText = "doRegisterHTTPRequest EXCEPTION";
				Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
				toast.show();
				e.printStackTrace();
			}
			return false;
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
				String responseBody = (String) response.get("body");
				CharSequence toastText = "Code: "+responseCode;
				Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
				toastText = "Body: "+responseBody;
				Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
				String responsePayload = (String) response.get("payload_received");
				toastText = "Payload: "+responsePayload;
				Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show();
				if ( responseCode == 200 ){
					return true;
				}
			} catch (Exception e) {
				System.out.println("doLoginHTTPRequest EXCEPTION");
				CharSequence toastText = "doLoginHTTPRequest EXCEPTION";
				Toast toast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT);
				toast.show();
				e.printStackTrace();
			}
			return false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meety_login);
		final EditText usernameText = (EditText) findViewById(R.id.meety_login_username);
		usernameText.setVisibility(View.VISIBLE);
		final EditText passwordText = (EditText) findViewById(R.id.meety_login_password);
		passwordText.setVisibility(View.VISIBLE);
		final Button loginButton = (Button) findViewById(R.id.meety_login_login_button);
		loginButton.setVisibility(View.VISIBLE);
		final Button signUpButton = (Button) findViewById(R.id.meety_login_signup_button);
		signUpButton.setVisibility(View.VISIBLE);
		final ProgressBar progressBar = (ProgressBar) findViewById(R.id.meety_login_progress);
		progressBar.setVisibility(View.INVISIBLE);
	}
	
	public void attemptLogin(View view){
		
		final EditText usernameText = (EditText) findViewById(R.id.meety_login_username);
		final EditText passwordText = (EditText) findViewById(R.id.meety_login_password);
		
		if(TextUtils.isEmpty(usernameText.getText().toString()) && TextUtils.isEmpty(passwordText.getText().toString())) {
			usernameText.setText("username");
			passwordText.setText("password");
		}

		Context context = getApplicationContext();
		CharSequence toastText;
		int duration = Toast.LENGTH_SHORT;
		
		if (!(TextUtils.isEmpty(usernameText.getText().toString())
				&& TextUtils.isEmpty(passwordText.getText().toString()))) {
			
			String username = usernameText.getText().toString(), password = passwordText.getText().toString();
			
			final Button loginButton = (Button) findViewById(R.id.meety_login_login_button);
			loginButton.setVisibility(View.INVISIBLE);
			final Button signUpButton = (Button) findViewById(R.id.meety_login_signup_button);
			signUpButton.setVisibility(View.INVISIBLE);
			final ProgressBar progressBar = (ProgressBar) findViewById(R.id.meety_login_progress);
			progressBar.setVisibility(View.VISIBLE);
			
			if ( doLoginHTTPRequest(username, password) ){
				toastText = "User successfully logged in!";
				Toast toast = Toast.makeText(context, toastText, duration);
				toast.show();

				setResult(RESULT_CODE_OK);
				MeetyLogin.this.finish();
				return;
			}
			else{
				toastText = "Could not log in!";
				loginButton.setVisibility(View.VISIBLE);
				signUpButton.setVisibility(View.VISIBLE);
				progressBar.setVisibility(View.INVISIBLE);
			}
		}
		else{
			toastText = "Please enter both an username and password";
		}
		
		Toast toast = Toast.makeText(context, toastText, duration);
		toast.show();
		
	}
	
	public void attemptSignUp(View view){

		final EditText usernameText = (EditText) findViewById(R.id.meety_login_username);
		final EditText passwordText = (EditText) findViewById(R.id.meety_login_password);
		
		if(TextUtils.isEmpty(usernameText.getText().toString()) && TextUtils.isEmpty(passwordText.getText().toString())) {
			usernameText.setText("username");
			passwordText.setText("password");
		}

		Context context = getApplicationContext();
		CharSequence toastText;
		
		if (!(TextUtils.isEmpty(usernameText.getText().toString())
				&& TextUtils.isEmpty(passwordText.getText().toString()))) {
			
			String username = usernameText.getText().toString(), password = passwordText.getText().toString();

			final Button loginButton = (Button) findViewById(R.id.meety_login_login_button);
			loginButton.setVisibility(View.INVISIBLE);
			final Button signUpButton = (Button) findViewById(R.id.meety_login_signup_button);
			signUpButton.setVisibility(View.INVISIBLE);
			final ProgressBar progressBar = (ProgressBar) findViewById(R.id.meety_login_progress);
			progressBar.setVisibility(View.VISIBLE);
			
			if ( doRegisterHTTPRequest(username, password) ){
				toastText = "User successfully registered!";
			}
			else{
				toastText = "Could not register this username/password pair!";
			}
			
			loginButton.setVisibility(View.VISIBLE);
			signUpButton.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.INVISIBLE);
		}
		else{
			toastText = "Please enter both an username and  a password";
		}
		
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, toastText, duration);
		toast.show();

	}

}