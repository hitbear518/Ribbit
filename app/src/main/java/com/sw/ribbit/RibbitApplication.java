package com.sw.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Sen on 8/9/2014.
 */
public class RibbitApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, "yMfj8MERj4peCCMoB0vRwbtNLGwrYiEZfymDjLlA", "erxB43Lx7kaCcKKSyvXm5JU8OFLmSUebmy9Vu7jd");
	}
}
