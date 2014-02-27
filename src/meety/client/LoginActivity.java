package meety.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;


public class LoginActivity extends Activity {

	//Future plans: these texts will be set depending on the user language
	private String userName;
	private final String passwordDefault = "meety";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login);
		
		setTextByLanguage();
		
		System.out.println("¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?¿?");
		
		
		/**
		final Button logInButton = (Button) findViewById(R.id.button_log_in);
		
		System.out.println("Log In Button created");
		logInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Send log in request!");
                System.out.println("Username: " + userNameText.getText());
                System.out.println("Password: " + passwordText.getText());
                System.out.println("========================================");
                
                Intent intent = new Intent(getThis(), AttemptingLogIn.class);
                System.out.println("CRIOU O INTENT");
                startActivity(intent);
            }
        });
        **/
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

	}

	//dummy method to set user language for texts
	private void setTextByLanguage(){
		userName = "username";
	}

	/**
	 * Method necessary to get this class object from an inner scope like OnClickListener
	 * @return
	 */
	private Activity getThis(){
		return this;
	}
	
	public void attemptLogIn(View view){
		
		// try to avoid these methods to improve performance after cliking LOG IN BUTTON
		
		final EditText userNameText = (EditText) findViewById(R.id.text_username);
		final EditText passwordText = (EditText) findViewById(R.id.text_birthdate);
		
		if(TextUtils.isEmpty(userNameText.getText().toString()) && TextUtils.isEmpty(passwordText.getText().toString())) {
			userNameText.setText(userName);
			passwordText.setText(passwordDefault);
		}	
		
        Intent intent = new Intent(getThis(), AttemptingLoginActivity.class);
        intent.putExtra("username", userNameText.getText().toString());
        intent.putExtra("password", passwordText.getText().toString());
        startActivity(intent);
        
	}
	
	
	public void signUpScreen (View view){
        Intent intent = new Intent(getThis(), SignUpActivity.class);
        startActivity(intent);
		
	}

}