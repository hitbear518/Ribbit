package com.sw.ribbit.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.sw.ribbit.R;
import com.sw.ribbit.adapters.RibbitApplication;

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

		ActionBar actionBar = getActionBar();
		actionBar.hide();

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
								RibbitApplication.updateParseInstallation(parseUser);

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
}
