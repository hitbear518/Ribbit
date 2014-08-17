package com.sw.ribbit.adapters;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;
import com.sw.ribbit.R;
import com.sw.ribbit.ui.MyActivity;
import com.sw.ribbit.utils.ParseConstants;

/**
 * Created by Sen on 8/9/2014.
 */
public class RibbitApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, "yMfj8MERj4peCCMoB0vRwbtNLGwrYiEZfymDjLlA", "erxB43Lx7kaCcKKSyvXm5JU8OFLmSUebmy9Vu7jd");
		PushService.setDefaultPushCallback(this, MyActivity.class, R.drawable.ic_stat_ic_launcher);
		ParseInstallation.getCurrentInstallation().saveInBackground();
	}

	public static void updateParseInstallation(ParseUser user) {
		ParseInstallation installation = ParseInstallation.getCurrentInstallation();
		installation.put(ParseConstants.KEY_USER_ID, user.getObjectId());
		installation.saveInBackground();
	}
}
