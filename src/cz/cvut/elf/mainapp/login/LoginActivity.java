package cz.cvut.elf.mainapp.login;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import cz.cvut.elf.mainapp.R;
import cz.cvut.elf.mainapp.ViewPagerActivity;
import cz.cvut.elf.mainapp.data.ElfUsersDAO;
import cz.cvut.elf.mainapp.planetselection.PlanetSelectionActivity;
import cz.cvut.elf.mainapp.preferencies.ElfPreferenceActivity;

public class LoginActivity extends ViewPagerActivity {

	public static final int ADD_ID = Menu.FIRST + 1;
	public static final int SETTINGS_ID = 99;
	public static final int ABOUT_ID = SETTINGS_ID + 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setTitle(getString(R.string.login));
	}

	public void reload() {
		Intent intent = getIntent();
		overridePendingTransition(0, 0);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		finish();

		overridePendingTransition(0, 0);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		String s = "  " + getString(R.string.action_create) + "  ";
		menu.add(Menu.NONE, ADD_ID, Menu.NONE, s)
				.setIcon(R.drawable.ic_action_user_add)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_ALWAYS
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		s = "  " + getString(R.string.action_settings) + "  ";
		menu.add(Menu.NONE, SETTINGS_ID, Menu.NONE, s)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		s = "  " + getString(R.string.action_about) + "  ";
		menu.add(Menu.NONE, ABOUT_ID, Menu.NONE, s).setShowAsAction(
				MenuItem.SHOW_AS_ACTION_NEVER);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case ADD_ID:
			startNewUserActivity();
			return true;
		case SETTINGS_ID:
			ElfPreferenceActivity.start(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected List<Fragment> getFragments() {
		List<Fragment> list = new ArrayList<Fragment>();

		ElfUsersDAO dao = ElfUsersDAO.getInstance();
		dao.loadUsers(this);
		List<User> users = dao.getUsers();
		
		for (User user : users) {
			UserFragment userFragment = new UserFragment();
			userFragment.setUser(user);
			list.add(userFragment);
		}

		list.add(new CreateNewButtonFragment());

		return list;
	}

	public void startNewUserActivity() {
		Intent intent = new Intent(this, UserEditationActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivityForResult(intent, 0);
	}

	public void onUserSelected(User user) {
		User.selectedUser = user;
		Intent intent = new Intent(this, PlanetSelectionActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			reload();
		}
	}
}
