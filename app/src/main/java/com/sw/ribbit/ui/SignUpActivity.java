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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.sw.ribbit.R;
import com.sw.ribbit.adapters.RibbitApplication;

public class SignUpActivity extends Activity {

	protected EditText mUsernameEditText;
	protected EditText mPasswordEditText;
	protected EditText mEmailEditText;
	protected Button mSignUpButton;
	protected Button mCancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_sign_up);

		ActionBar actionBar = getActionBar();
		actionBar.hide();

		mUsernameEditText = (EditText) findViewById(R.id.username_field);
		mPasswordEditText = (EditText) findViewById(R.id.password_field);
		mEmailEditText = (EditText) findViewById(R.id.email_field);

		mCancelButton = (Button) findViewById(R.id.cancel_button);
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		mSignUpButton = (Button) findViewById(R.id.sign_up_button);
		mSignUpButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = mUsernameEditText.getText().toString();
				String password = mPasswordEditText.getText().toString();
				String email = mEmailEditText.getText().toString();

				username = username.trim();
				password = password.trim();
				email = email.trim();

				if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
					AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
					builder.setMessage(R.string.sign_up_error_message)
							.setPositiveButton(android.R.string.ok, null)
							.show();
				} else {
					setProgressBarIndeterminateVisibility(true);

					final ParseUser newUser = new ParseUser();
					newUser.setUsername(username);
					newUser.setPassword(password);
					newUser.setEmail(email);
					newUser.signUpInBackground(new SignUpCallback() {
						@Override
						public void done(ParseException e) {
							setProgressBarIndeterminateVisibility(false);

							if (e == null) {
								RibbitApplication.updateParseInstallation(ParseUser.getCurrentUser());

								Intent intent = new Intent(SignUpActivity.this, MyActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(intent);
							} else {
								AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
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
