package cz.cvut.elf.mainapp.login;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.jraf.android.backport.switchwidget.Switch;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import cz.cvut.elf.mainapp.ElfConstants;
import cz.cvut.elf.mainapp.R;
import cz.cvut.elf.mainapp.ViewPagerActivity;
import cz.cvut.elf.mainapp.coverflow.CoverFlow;
import cz.cvut.elf.mainapp.coverflow.ResourceImageAdapter;
import cz.cvut.elf.mainapp.data.ElfDatabaseOpenHelper;
import cz.cvut.elf.mainapp.data.ElfUsersDAO;

public class UserEditationActivity extends ViewPagerActivity {

	public static final int CANCEL_ID = Menu.FIRST + 1;
	public static final int BACKSPACE_ID = CANCEL_ID + 1;
	public static final int ACCEPT_ID = BACKSPACE_ID + 1;

	private int result = RESULT_CANCELED;

	private User editedUser = null;
	private User userData;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		try {
			int editedUserId = getIntent().getExtras().getInt(
					ElfConstants.BUNDLE_USER_ID);
			editedUser = ElfUsersDAO.getInstance().getUser(editedUserId);
		} catch (Exception e) {
			// creating new user
		}

		try {
			userData = (User)bundle.getSerializable("user");
			
			int editedUserId = bundle.getInt(ElfConstants.BUNDLE_USER_ID);
			editedUser = ElfUsersDAO.getInstance().getUser(editedUserId);
		} catch (Exception e) {
		}

		if (editedUser != null && userData == null) {
			userData = new User(editedUser.getId(), editedUser.getName(),
					editedUser.getGender(), editedUser.getBornDate(),
					editedUser.getIcon());
		} else if (userData == null) {
			userData = new User(-1, "", 'm', new GregorianCalendar(), 0);
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		if (editedUser != null) {
			bundle.putInt(ElfConstants.BUNDLE_USER_ID, editedUser.getId());
			bundle.putSerializable("user", userData);
		}

		super.onSaveInstanceState(bundle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		String s = "  " + getString(R.string.action_cancel) + "  ";
		menu.add(Menu.NONE, CANCEL_ID, Menu.NONE, s)
				.setIcon(R.drawable.ic_action_cancel)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		s = "  " + getString(R.string.action_cancel_changes) + "  ";
		menu.add(Menu.NONE, BACKSPACE_ID, Menu.NONE, s)
				.setIcon(R.drawable.ic_action_backspace)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_IF_ROOM
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		s = "  " + getString(R.string.action_accept) + "  ";
		menu.add(Menu.NONE, ACCEPT_ID, Menu.NONE, s)
				.setIcon(R.drawable.ic_action_accept)
				.setShowAsAction(
						MenuItem.SHOW_AS_ACTION_ALWAYS
								| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case CANCEL_ID:
		case android.R.id.home:
			close();
			return true;

		case BACKSPACE_ID:
			Intent intent = getIntent();
			overridePendingTransition(0, 0);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			finish();

			overridePendingTransition(0, 0);
			startActivity(intent);

			return true;

		case ACCEPT_ID:
			processData();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void processData() {
		String name = userData.getName();
		char gender = userData.getGender();
		int icon = userData.icon;
		String dateString = ElfConstants.DATE_FORMAT.format(userData
				.getBornDate().getTime());

		/* check data */
		if (name.length() == 0) {
			new AlertDialog.Builder(this)
					.setTitle(getString(R.string.empty_name_dialog))
					.setPositiveButton(getString(android.R.string.ok),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									pager.setCurrentItem(0);
								}
							}).show();
			return;
		}

		/* Insert data to database */
		ContentValues values = new ContentValues();

		values.put(ElfDatabaseOpenHelper.USER_NAME, name);
		values.put(ElfDatabaseOpenHelper.USER_GENDER, "" + gender);
		values.put(ElfDatabaseOpenHelper.USER_BORNED_DATE, dateString);
		values.put(ElfDatabaseOpenHelper.USER_ICON, icon);

		if (editedUser != null) {
			ElfUsersDAO.getInstance()
					.editUser(editedUser.getId(), values, this);
		} else {
			ElfUsersDAO.getInstance().writeUser(values, this);
		}

		result = RESULT_OK;
		close();
	}

	@Override
	protected List<Fragment> getFragments() {

		List<Fragment> list = new ArrayList<Fragment>();
		list.add(new UserNameFragment());
		list.add(new UserGenderFragment());
		list.add(new UserBirthDateFragment());
		list.add(new UserGalleryFragment());
		list.add(new ConfirmationFragment());

		return list;
	}

	private void close() {
		setResult(result);
		finish();
		overridePendingTransition(0, 0);
	}

	@Override
	public void onBackPressed() {
		close();
	}

	/*****************
	 * 
	 * NAME SELECTION
	 * 
	 ****************/

	public static class UserNameFragment extends SherlockFragment {

		EditText editText;
		String name;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_new_user_name_form,
					container, false);
			editText = (EditText) v.findViewById(R.id.new_user_name);

			try {
				final UserEditationActivity activity = (UserEditationActivity) getActivity();
				if (activity.editedUser != null) {
					String name = activity.editedUser.getName();
					editText.setText(name);
				}

				editText.addTextChangedListener(new TextWatcher() {
					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						activity.userData.setName(s.toString());
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				});
			} catch (Exception e) {
				Log.e("fragment", "used in wrong activity");
			}
			return v;
		}
	}

	/*******************
	 * 
	 * GENDER SELECTION
	 * 
	 *******************/

	public static class UserGenderFragment extends SherlockFragment {

		Switch switchView;
		ImageView genderImage;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_new_user_gender,
					container, false);

			switchView = (Switch) v.findViewById(R.id.switch_new_user_gender);
			genderImage = (ImageView) v
					.findViewById(R.id.new_user_selected_gender);

			try {
				final UserEditationActivity activity = (UserEditationActivity) getActivity();
				switchView
						.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								if (isChecked) {
									genderImage
											.setImageResource(R.drawable.boy);
									activity.userData
											.setGender(ElfConstants.MALE);
								} else {
									genderImage
											.setImageResource(R.drawable.girl);
									activity.userData
											.setGender(ElfConstants.FEMALE);
								}
							}
						});

				if (activity.editedUser != null) {
					if (activity.editedUser.getGender() == ElfConstants.MALE) {
						switchView.setChecked(true);
					} else {
						genderImage.setImageResource(R.drawable.girl);
						switchView.setChecked(false);
					}
				} else {
					switchView.setChecked(true);
				}

			} catch (Exception e) {
				Log.e("fragment", "used in wrong activity");
			}

			return v;
		}
	}

	/**********************
	 * 
	 * BIRTH DATE SELECTION
	 * 
	 **********************/

	public static class UserBirthDateFragment extends SherlockFragment {

		DatePicker datePicker;
		TextView textView;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_new_user_age_form,
					container, false);
			datePicker = (DatePicker) v.findViewById(R.id.new_user_date_picker);
			textView = (TextView) v.findViewById(R.id.new_user_age_text);

			try {
				final UserEditationActivity activity = (UserEditationActivity) getActivity();

				DatePicker.OnDateChangedListener listener = new DatePicker.OnDateChangedListener() {

					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						GregorianCalendar date = new GregorianCalendar(year,
								monthOfYear, dayOfMonth);
						activity.userData.setBornDate(date);
						textView.setText(activity.userData.getAge() + " "
								+ getString(R.string.years));
					}
				};

				GregorianCalendar date = new GregorianCalendar();
				if (activity.editedUser != null) {
					date = activity.editedUser.getBornDate();
				}
				
				datePicker.init(date.get(GregorianCalendar.YEAR),
					date.get(GregorianCalendar.MONTH),
					date.get(GregorianCalendar.DAY_OF_MONTH), listener);

				textView.setText(activity.userData.getAge() + " "
						+ getString(R.string.years));

			} catch (Exception e) {
				Log.e("fragment", "used in wrong activity");
			}

			return v;
		}
	}

	/****************
	 * 
	 * ICON SELECTION
	 * 
	 ****************/
	public static class UserGalleryFragment extends SherlockFragment {

		ImageView selectedImage;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_new_user_icon_gallery,
					container, false);

			CoverFlow gallery = (CoverFlow) v.findViewById(R.id.coverflow);
			ResourceImageAdapter adapter = new ResourceImageAdapter(
					getActivity());
			adapter.setResources(User.images);
			gallery.setAdapter(adapter);

			selectedImage = (ImageView) v
					.findViewById(R.id.new_user_selected_image);

			gallery.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(final AdapterView<?> parent,
						final View view, final int position, final long id) {
					selectedImage.setImageResource(User.getBitmap(position));

					try {
						final UserEditationActivity activity = (UserEditationActivity) getActivity();
						activity.userData.setIcon(position);
					} catch (Exception e) {
						Log.e("fragment", "used in wrong activity");
					}
				}

				@Override
				public void onNothingSelected(final AdapterView<?> parent) {
				}
			});

			try {
				UserEditationActivity activity = (UserEditationActivity) getActivity();
				if (activity.editedUser != null) {
					int icon = activity.editedUser.getIcon();
					gallery.setSelection(icon);
				}
			} catch (Exception e) {
				Log.e("fragment", "used in wrong activity");
			}

			return v;
		}
	}

	/**************************
	 * 
	 * CONFIRMATION FRAGMENT
	 * 
	 *************************/

	public static class ConfirmationFragment extends SherlockFragment {

		ImageButton button;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View v = inflater.inflate(R.layout.fragment_new_user_confimation,
					container, false);
			button = (ImageButton) v.findViewById(R.id.new_user_confirm_button);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						UserEditationActivity activity = (UserEditationActivity) getActivity();
						activity.processData();
					} catch (Exception e) {
						Log.e("fragment", "used in wrong activity");
					}
				}
			});

			return v;
		}

	}
}
