package com.sw.ribbit.ui;

import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.sw.ribbit.adapters.MessageAdapter;
import com.sw.ribbit.utils.ParseConstants;
import com.sw.ribbit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sen on 8/10/2014.
 */
public class InboxFragment extends ListFragment {

	protected List<ParseObject> mMessages;
	protected SwipeRefreshLayout mSwipeRefreshLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

		mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
		mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
		mSwipeRefreshLayout.setColorScheme(R.color.swipe_refresh_1, R.color.swipe_refresh_2,
				R.color.swipe_refresh_3, R.color.swipe_refresh_4);
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();

		getActivity().setProgressBarIndeterminateVisibility(true);

		retrieveMessages();
	}

	private void retrieveMessages() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseConstants.CLASS_MESSAGES);
		query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
		query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> messages, ParseException e) {
				if (getActivity() == null) return;
				getActivity().setProgressBarIndeterminateVisibility(false);

				if (mSwipeRefreshLayout.isRefreshing()) {
					mSwipeRefreshLayout.setRefreshing(false);
				}

				if (e == null) {
					mMessages = messages;
					String[] usernames = new String[mMessages.size()];
					for (int i = 0; i < mMessages.size(); i++) {
						usernames[i] = mMessages.get(i).getString(ParseConstants.KEY_SENDER_NAME);
					}
					if (getListAdapter() == null) {
						MessageAdapter adapter = new MessageAdapter(getActivity(), mMessages);
						setListAdapter(adapter);
					} else {
						MessageAdapter adapter = (MessageAdapter) getListAdapter();
						adapter.refill(mMessages);
					}
				}
			}
		});
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		ParseObject message = mMessages.get(position);
		String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
		ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
		Uri fileUri = Uri.parse(file.getUrl());

		if (messageType.equals(ParseConstants.TYPE_IMAGE)) {
			Intent intent = new Intent(getActivity(), ViewImageActivity.class);
			intent.setData(fileUri);
			startActivity(intent);
		} else {
			Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
			intent.setDataAndType(fileUri, "video/*");
			startActivity(intent);
		}

		List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);

		if (ids.size() == 1) {
			message.deleteInBackground();
		} else {
			ids.remove(ParseUser.getCurrentUser().getObjectId());

			ArrayList<String> idsToRemove = new ArrayList<String>();
			idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

			message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
			message.saveInBackground();
		}
	}

	protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
		@Override
		public void onRefresh() {
			retrieveMessages();
		}
	};
}
