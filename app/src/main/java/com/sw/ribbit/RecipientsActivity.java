package com.sw.ribbit;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

public class RecipientsActivity extends ListActivity {

	public static final String TAG = RecipientsActivity.class.getSimpleName();

	protected ParseUser mCurrentUser;
	protected ParseRelation<ParseUser> mFriendsRelation;

	protected List<ParseUser> mFriends;

	protected MenuItem mSendMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_recipients);

		getListView().setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		if (l.getCheckedItemCount() > 0) {
			mSendMenuItem.setVisible(true);
		} else {
			mSendMenuItem.setVisible(false);
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		mCurrentUser = ParseUser.getCurrentUser();
		mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

		setProgressBarIndeterminateVisibility(true);

		ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
		query.addAscendingOrder(ParseConstants.KEY_USERNAME);
		query.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> friends, ParseException e) {
				setProgressBarIndeterminateVisibility(false);

				if (e == null) {
					mFriends = friends;

					String[] usernames = new String[mFriends.size()];
					for (int i = 0; i < mFriends.size(); i++) {
						usernames[i] = mFriends.get(i).getUsername();
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(RecipientsActivity.this,
							android.R.layout.simple_list_item_checked, usernames);
					setListAdapter(adapter);
				} else {
					Log.e(TAG, e.getMessage());
					new AlertDialog.Builder(RecipientsActivity.this)
							.setMessage(e.getMessage())
							.setPositiveButton(android.R.string.ok, null)
							.show();
				}
			}
		});
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recipients, menu);
		mSendMenuItem = menu.findItem(R.id.action_send);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
		switch (id) {
		case R.id.action_send:

			break;
		}
        return super.onOptionsItemSelected(item);
    }
}
