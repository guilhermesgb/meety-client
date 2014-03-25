package meety.client;

import java.util.HashMap;
import java.util.Map;

import meety.client.http.HttpUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class SignUpActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up);
		
		addItemsOnSpinnerSex();
		addItemsOnSpinnerCountry();
		
	}

	private void addItemsOnSpinnerSex() {
		Spinner sp = (Spinner)findViewById(R.id.spinner_sex);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.sex_arrays, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
	}
	
	private void addItemsOnSpinnerCountry() {
		Spinner sp = (Spinner) findViewById(R.id.spinner_country);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.countries_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
	}
	
	private boolean doRegisterHTTPRequest(String username, String password){

		Map<String, String> pairs = new HashMap<String, String>();
		pairs.put("Host", "meety-server.herokuapp.com");
		pairs.put("Content-Type", "application/json");
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
	
	public void register(View view){
		
		final EditText usernameText = (EditText) findViewById(R.id.text_recipient_username);
		final EditText passwordText = (EditText) findViewById(R.id.text_register_password);
		
		Context context = getApplicationContext();
		CharSequence toastText;
		
		if (!(TextUtils.isEmpty(usernameText.getText().toString())
				&& TextUtils.isEmpty(passwordText.getText().toString()))) {
			
			String username = usernameText.getText().toString(), password = passwordText.getText().toString();
			
			if ( doRegisterHTTPRequest(username, password) ){
				toastText = "User successfully registered!";
			}
			else{
				toastText = "Could not register this username/password pair!";
			}
		}
		else{
			toastText = "Please enter both an username and password";
		}
		
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, toastText, duration);
		toast.show();		
	}
}