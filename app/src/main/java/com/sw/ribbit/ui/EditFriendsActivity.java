package com.sw.ribbit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sw.ribbit.R;
import com.sw.ribbit.adapters.UserAdapter;
import com.sw.ribbit.utils.ParseConstants;

import java.util.List;

public class EditFriendsActivity extends Activity {

	public static final String TAG = EditFriendsActivity.class.getSimpleName();

	protected List<ParseUser> mUsers;
	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrentUser;

	protected GridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.user_grid);

		mGridView = (GridView) findViewById(R.id.friends_grid);

		mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
		TextView emptyTextView = (TextView) findViewById(android.R.id.empty);
		mGridView.setEmptyView(emptyTextView);

		mGridView.setOnItemClickListener(mOnItemClickListener);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mCurrentUser = ParseUser.getCurrentUser();
		mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.orderByAscending(ParseConstants.KEY_USERNAME);
		query.setLimit(1000);
		setProgressBarIndeterminateVisibility(true);
		query.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> users, ParseException e) {
				setProgressBarIndeterminateVisibility(false);
				if (e == null) {
					mUsers = users;
					String[] usernames = new String[mUsers.size()];
					for (int i = 0; i < usernames.length; i++) {
						usernames[i] = mUsers.get(i).getUsername();
					}
					if (mGridView.getAdapter() == null) {
						UserAdapter adapter = new UserAdapter(EditFriendsActivity.this, mUsers);
						mGridView.setAdapter(adapter);
					} else {
						UserAdapter adapter = (UserAdapter) mGridView.getAdapter();
						adapter.refill(mUsers);
					}
					addFriendCheckmarks();
				} else {
					Log.e(TAG, e.getMessage());
					AlertDialog.Builder builder = new AlertDialog.Builder(EditFriendsActivity.this);
					builder.setMessage(e.getMessage())
							.setPositiveButton(android.R.string.ok, null)
							.show();
				}
			}
		});
	}

	private void addFriendCheckmarks() {
		mFriendsRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> friends, ParseException e) {
				if (e == null) {
					for (int i = 0; i < mUsers.size(); i++) {
						ParseUser user = mUsers.get(i);

						for (ParseUser friend : friends) {
							if (friend.getObjectId().equals(user.getObjectId())) {
								mGridView.setItemChecked(i, true);
							}
						}
					}
				} else {
					Log.e(TAG, e.getMessage());
				}
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button_custom, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		return super.onOptionsItemSelected(item);
	}

	private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			ImageView checkImageView = (ImageView) view.findViewById(R.id.check_image_view);

			if (mGridView.isItemChecked(position)) {
				// add friend
				mFriendsRelation.add(mUsers.get(position));
				checkImageView.setVisibility(View.VISIBLE);
			} else {
				mFriendsRelation.remove(mUsers.get(position));
				checkImageView.setVisibility(View.INVISIBLE);
			}
			mCurrentUser.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {
					if (e != null) {
						Log.e(TAG, e.getMessage());
					}
				}
			});
		}
	};
}
