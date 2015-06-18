package cz.cvut.elf.mainapp;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import android.widget.TextView;


import cz.cvut.elf.R;
import cz.cvut.elf.mainapp.login.User;

public abstract class ViewPagerActivity extends ActionBarActivity {
	protected ViewPagerAdapter pageAdapter;
	protected ViewPager pager;
	protected ImageButton leftButton;
	protected ImageButton rightButton;
	protected ImageButton selectedUserButton;
	protected TextView selectedUserText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewpager);
		leftButton = (ImageButton) findViewById(R.id.left_button);
		rightButton = (ImageButton) findViewById(R.id.right_button);

		List<Fragment> fragments = getFragments();
		pageAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
				fragments);
		pager = (ViewPager) findViewById(R.id.viewpager);
		pager.setAdapter(pageAdapter);

		OnPageChangeListener onPageChangeListener = new PageChangeListener();
		pager.setOnPageChangeListener(onPageChangeListener);

		leftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				previousFragment();
			}
		});

		rightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nextFragment();
			}
		});

		onPageChangeListener.onPageSelected(0); /* to disable leftButton */

		selectedUserButton = (ImageButton) findViewById(R.id.selected_user_image_button);
		selectedUserText = (TextView) findViewById(R.id.selected_user_name_text);
	}

	private void nextFragment() {
		int pos = pager.getCurrentItem();
		pager.setCurrentItem(pos + 1);
	}

	private void previousFragment() {
		int pos = pager.getCurrentItem();
		pager.setCurrentItem(pos - 1);
	}

	protected abstract List<Fragment> getFragments();

	protected void showSelectedUser() {
		if (User.selectedUser == null) {
			return;
		}
		selectedUserButton.setImageResource(User.getBitmap(User.selectedUser
				.getIcon()));
		selectedUserText.setText(User.selectedUser.getName());
		selectedUserButton.setVisibility(View.VISIBLE);
		selectedUserText.setVisibility(View.VISIBLE);

		selectedUserButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

	}

	/****************
	 * 
	 * ADAPTER
	 * 
	 ****************/

	protected class ViewPagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> fragments;

		public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int position) {
			return this.fragments.get(position);
		}

		@Override
		public int getCount() {
			return this.fragments.size();
		}

		public void delete(int pos) {
			fragments.remove(pos);
			notifyDataSetChanged();
		}
	}

	/****************
	 * 
	 * LISTENER
	 * 
	 ****************/

	protected class PageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int page) {
			if (pager == null) {
				return;
			}

			int pos = pager.getCurrentItem();
			if (pos == 0) {
				leftButton.setVisibility(ImageButton.INVISIBLE);
				leftButton.setClickable(false);
			} else {
				leftButton.setVisibility(ImageButton.VISIBLE);
				leftButton.setClickable(true);
			}

			if (pos == pageAdapter.getCount() - 1) {
				rightButton.setVisibility(ImageButton.INVISIBLE);
				rightButton.setClickable(false);
			} else {
				rightButton.setVisibility(ImageButton.VISIBLE);
				rightButton.setClickable(true);
			}

			if (getCurrentFocus() != null) {
				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), 0);
			}
		}
	}
}