package com.sw.ribbit;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Sen on 8/10/2014.
 */
public class FriendsFragment extends ListFragment {

	public static final String TAG = FriendsFragment.class.getSimpleName();

	protected List<ParseUser> mFriends;
	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrentUser;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_friends, container, false);
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
				if (getActivity() != null) {
					getActivity().setProgressBarIndeterminateVisibility(false);
				}

				if (e == null) {
					mFriends = friends;

					String[] usernames = new String[mFriends.size()];
					for (int i = 0; i < mFriends.size(); i++) {
						usernames[i] = mFriends.get(i).getUsername();
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
							android.R.layout.simple_list_item_1, usernames);
					setListAdapter(adapter);
				} else {
					Log.e(TAG, e.getMessage());
					new AlertDialog.Builder(getListView().getContext())
							.setMessage(e.getMessage())
							.setPositiveButton(android.R.string.ok, null)
							.show();
				}
			}
		});
	}
}
