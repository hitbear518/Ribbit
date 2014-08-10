package com.sw.ribbit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends Activity {

	protected EditText mUsernameEditText;
	protected EditText mPasswordEditText;
	protected Button mLoginButton;

	protected TextView mSignUpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_login);

		mSignUpTextView = (TextView) findViewById(R.id.sign_up_text);
		mSignUpTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
				startActivity(intent);
			}
		});

		mUsernameEditText = (EditText) findViewById(R.id.username_field);
		mPasswordEditText = (EditText) findViewById(R.id.password_field);
		mLoginButton = (Button) findViewById(R.id.login_button);
		mLoginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = mUsernameEditText.getText().toString();
				String password = mPasswordEditText.getText().toString();

				username = username.trim();
				password = password.trim();

				if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
					builder.setMessage(R.string.login_error_message)
							.setPositiveButton(android.R.string.ok, null)
							.show();
				} else {
					setProgressBarIndeterminateVisibility(true);
					ParseUser.logInInBackground(username, password, new LogInCallback() {
						@Override
						public void done(ParseUser parseUser, ParseException e) {
							setProgressBarIndeterminateVisibility(false);

							if (e == null) {
								Intent intent = new Intent(LoginActivity.this, MyActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							} else {
								AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
								builder.setMessage(e.getMessage())
										.setPositiveButton(android.R.string.ok, null)
										.show();
							}
						}
					});
				}
			}
		});
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
