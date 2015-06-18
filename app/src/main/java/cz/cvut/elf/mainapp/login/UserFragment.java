package cz.cvut.elf.mainapp.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import cz.cvut.elf.R;
import cz.cvut.elf.mainapp.ElfConstants;
import cz.cvut.elf.mainapp.data.ElfUsersDAO;

public class UserFragment extends Fragment {
	public static final int EDIT_ID = LoginActivity.ADD_ID + 1;
	public static final int DELETE_ID = EDIT_ID + 1;

	ImageButton button;
	TextView nameView;
	User user;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);

		View v = inflater.inflate(R.layout.fragment_user, container, false);
		button = (ImageButton) v.findViewById(R.id.user_image_button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (user == null) {
					return;
				}

				try {
					LoginActivity activity = (LoginActivity) getActivity();
					activity.onUserSelected(user);
				} catch (Exception e) {
					Log.e("Used in wrong activity", e.toString());
				}
			}
		});

		nameView = (TextView) v.findViewById(R.id.user_name_text);

		try {
			int userId = savedInstanceState.getInt(ElfConstants.BUNDLE_USER_ID);
			this.setUser(ElfUsersDAO.getInstance().getUser(userId));
		} catch (Exception e) {
		}

		if (user != null) {
			nameView.setText(user.getName());
			button.setImageResource(User.getBitmap(user.icon));
		}

		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		if (user != null) {
			bundle.putInt(ElfConstants.BUNDLE_USER_ID, user.getId());
		}
		super.onSaveInstanceState(bundle);
	}

	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		String s = "  " + getString(R.string.action_edit) + "  ";
		menu.add(Menu.NONE, EDIT_ID, Menu.NONE, s)
				.setIcon(R.drawable.ic_action_edit)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_ALWAYS
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		s = "  " + getString(R.string.action_delete) + "  ";
		menu.add(Menu.NONE, DELETE_ID, Menu.NONE, s)
				.setIcon(R.drawable.ic_action_delete)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			deleteDialog();
			return true;
		case EDIT_ID:
			editUser();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void editUser() {
		if (user == null) {
			return;
		}

		Intent intent = new Intent(getActivity(), UserEditationActivity.class);
		intent.putExtra(ElfConstants.BUNDLE_USER_ID, user.getId());
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		getActivity().startActivityForResult(intent, 0);
	}

	private void deleteDialog() {
		if (user == null) {
			return;
		}

		new AlertDialog.Builder(getActivity())
				.setTitle(getString(R.string.delete_dialog))
				.setPositiveButton(getString(R.string.yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								processDelete();
							}
						})
				.setNegativeButton(getString(R.string.no),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();

	}

	private void processDelete() {
		if (ElfConstants.DEBUG) {
			Log.d("Deleting user", "" + user.getId());
		}

		ElfUsersDAO.getInstance().deleteUser(user.getId(), getActivity());
		try {
			((LoginActivity) getActivity()).reload();
		} catch (Exception e) {
			Log.e("Deleting user", "user fragment used outside LoginActivity");
		}
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

}