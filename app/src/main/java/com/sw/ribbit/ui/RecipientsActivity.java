package com.sw.ribbit.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.sw.ribbit.adapters.UserAdapter;
import com.sw.ribbit.utils.FileHelper;
import com.sw.ribbit.utils.ParseConstants;
import com.sw.ribbit.R;

import java.util.ArrayList;
import java.util.List;

public class RecipientsActivity extends Activity {

	public static final String TAG = RecipientsActivity.class.getSimpleName();

	protected ParseUser mCurrentUser;
	protected ParseRelation<ParseUser> mFriendsRelation;

	protected List<ParseUser> mFriends;

	protected MenuItem mSendMenuItem;

	protected Uri mMediaUri;
	protected String mFileType;

	protected GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.user_grid);

		mGridView = (GridView) findViewById(R.id.friends_grid);
		mGridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

		TextView emptyTextView = (TextView) findViewById(android.R.id.empty);

		mMediaUri = getIntent().getData();
		mFileType = getIntent().getStringExtra(ParseConstants.KEY_FILE_TYPE);

		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (mGridView.getCheckedItemCount() > 0) {
					mSendMenuItem.setVisible(true);
				} else {
					mSendMenuItem.setVisible(false);
				}

				ImageView checkImageView = (ImageView) view.findViewById(R.id.check_image_view);
				if (mGridView.isItemChecked(position)) {
					checkImageView.setVisibility(View.VISIBLE);
				} else {
					checkImageView.setVisibility(View.INVISIBLE);
				}
			}
		});
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
					if (mGridView.getAdapter() == null) {
						UserAdapter adapter = new UserAdapter(RecipientsActivity.this, mFriends);
						mGridView.setAdapter(adapter);
					} else {
						UserAdapter adapter = (UserAdapter) mGridView.getAdapter();
						adapter.refill(mFriends);
					}
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
        // automatically handle clicks on the Home/Up button_custom, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
		switch (id) {
		case R.id.action_send:
			ParseObject message = createMessage();
			if (message == null) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.error_selecting_file)
						.setPositiveButton(android.R.string.ok, null)
						.show();
			} else {
				send(message);
				finish();
			}
			break;
		}
        return super.onOptionsItemSelected(item);
    }

	protected ParseObject createMessage() {
		ParseObject message = new ParseObject(ParseConstants.CLASS_MESSAGES);
		message.put(ParseConstants.KEY_SENDER_ID, mCurrentUser.getObjectId());
		message.put(ParseConstants.KEY_SENDER_NAME, mCurrentUser.getUsername());
		message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIds());
		message.put(ParseConstants.KEY_FILE_TYPE, mFileType);

		byte[] fileBytes = FileHelper.getByteArrayFromFile(this, mMediaUri);

		if (fileBytes == null) {
			return null;
		} else {
			if (mFileType.equals(ParseConstants.TYPE_IMAGE)) {
				fileBytes = FileHelper.reduceImageForUpload(fileBytes);
			}

			String fileName = FileHelper.getFileName(this, mMediaUri, mFileType);
			ParseFile file = new ParseFile(fileName, fileBytes);
			message.put(ParseConstants.KEY_FILE, file);
			return message;
		}
	}

	protected ArrayList<String> getRecipientIds() {
		ArrayList<String> recipientIds = new ArrayList<String>();
		for (int i = 0; i < mGridView.getCount(); i++) {
			if (mGridView.isItemChecked(i)) {
				recipientIds.add(mFriends.get(i).getObjectId());
			}
		}
		return recipientIds;
	}

	protected void send(ParseObject message) {
		message.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					Toast.makeText(getApplicationContext(), R.string.success_message, Toast.LENGTH_LONG).show();
					sendPushNotifications();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(RecipientsActivity.this);
					builder.setMessage(getString(R.string.error_sending_message))
							.setPositiveButton(android.R.string.ok, null)
							.show();
				}
			}
		});
	}

	private void sendPushNotifications() {
		ParseQuery<ParseInstallation> query = ParseInstallation.getQuery();
		query.whereContainedIn(ParseConstants.KEY_USER_ID, getRecipientIds());

		ParsePush push = new ParsePush();
		push.setQuery(query);
		push.setMessage(getString(R.string.push_message, mCurrentUser.getUsername()));
		push.sendInBackground();
	}
}
