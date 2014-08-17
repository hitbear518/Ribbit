package com.sw.ribbit.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.sw.ribbit.R;
import com.sw.ribbit.utils.MD5Util;

import java.util.List;

/**
 * Created by Sen on 8/14/2014.
 */
public class UserAdapter extends ArrayAdapter<ParseUser> {

	public static final String TAG = UserAdapter.class.getSimpleName();

	protected Context mContext;
	protected List<ParseUser> mUsers;

	public UserAdapter(Context context, List<ParseUser> users) {
		super(context, R.layout.user_item, users);
		mContext = context;
		mUsers = users;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
			holder = new ViewHolder();
			holder.userImageView = (ImageView) convertView.findViewById(R.id.user_image_view);
			holder.checkImageView = (ImageView) convertView.findViewById(R.id.check_image_view);
			holder.nameLabel = (TextView) convertView.findViewById(R.id.name_label);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ParseUser user = mUsers.get(position);
		String email = user.getEmail().toLowerCase();

		if (email.equals("")) {
			holder.userImageView.setImageResource(R.drawable.avatar_empty);
		} else {
			String hash = MD5Util.md5Hex(email);
			String gravatarUrl = "http://www.gravatar.com/avatar/" + hash +
					"?s=204&d=404";
			Picasso.with(mContext)
					.load(gravatarUrl)
					.placeholder(R.drawable.avatar_empty)
					.into(holder.userImageView);
		}

		GridView gridView = (GridView) parent;
		if (gridView.isItemChecked(position)) {
			holder.checkImageView.setVisibility(View.VISIBLE);
		} else {
			holder.checkImageView.setVisibility(View.INVISIBLE);
		}


		holder.nameLabel.setText(user.getUsername());

		return convertView;
	}

	private static class ViewHolder {
		ImageView userImageView;
		TextView nameLabel;
		ImageView checkImageView;
	}

	public void refill(List<ParseUser> users) {
		mUsers.clear();
		mUsers.addAll(users);
		notifyDataSetChanged();
	}
}
