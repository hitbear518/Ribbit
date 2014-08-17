package com.sw.ribbit.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.sw.ribbit.R;
import com.sw.ribbit.adapters.UserAdapter;
import com.sw.ribbit.utils.ParseConstants;

import java.util.List;

/**
 * Created by Sen on 8/10/2014.
 */
public class FriendsFragment extends Fragment {

	public static final String TAG = FriendsFragment.class.getSimpleName();

	protected List<ParseUser> mFriends;
	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrentUser;
	protected GridView mGridView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.user_grid, container, false);
		mGridView = (GridView) rootView.findViewById(R.id.friends_grid);
		TextView emptyTextView = (TextView) rootView.findViewById(android.R.id.empty);
		mGridView.setEmptyView(emptyTextView);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		mCurrentUser = ParseUser.getCurrentUser();
		mFriendsRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

		getActivity().setProgressBarIndeterminateVisibility(true);

		ParseQuery<ParseUser> query = mFriendsRelation.getQuery();
		query.addAscendingOrder(ParseConstants.KEY_USERNAME);
		query.findInBackground(new FindCallback<ParseUser>() {
			@Override
			public void done(List<ParseUser> friends, ParseException e) {
				if (getActivity() == null) return;

				getActivity().setProgressBarIndeterminateVisibility(false);

				if (e == null) {
					mFriends = friends;

					String[] usernames = new String[mFriends.size()];
					for (int i = 0; i < mFriends.size(); i++) {
						usernames[i] = mFriends.get(i).getUsername();
					}
					if (mGridView.getAdapter() == null) {
						UserAdapter adapter = new UserAdapter(getActivity(), mFriends);
						mGridView.setAdapter(adapter);
					} else {
						UserAdapter adapter = (UserAdapter) mGridView.getAdapter();
						adapter.refill(mFriends);
					}

				} else {
					Log.e(TAG, e.getMessage());
					new AlertDialog.Builder(getActivity())
							.setMessage(e.getMessage())
							.setPositiveButton(android.R.string.ok, null)
							.show();
				}
			}
		});
	}
}
