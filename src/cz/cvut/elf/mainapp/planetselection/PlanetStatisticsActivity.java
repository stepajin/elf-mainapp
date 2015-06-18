package cz.cvut.elf.mainapp.planetselection;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.view.MenuItem;

import cz.cvut.elf.mainapp.ViewPagerActivity;
import cz.cvut.elf.mainapp.data.ElfPlanetsDAO;
import cz.cvut.elf.mainapp.login.LoginActivity;

public class PlanetStatisticsActivity extends ViewPagerActivity {

	public static final String PLANET_SHORTCUT_EXTRA = "shortcut";

	List<ActionBar.Tab> tabs = new ArrayList<ActionBar.Tab>();
	List<Fragment> taskStatsFragments = new ArrayList<Fragment>();
	String planetShortcut;

	@Override
	protected void onCreate(Bundle bundle) {
		Intent intent = getIntent();
		planetShortcut = intent.getStringExtra(PLANET_SHORTCUT_EXTRA);

		super.onCreate(bundle);

		if (planetShortcut == null) {
			return;
		}

		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(ElfPlanetsDAO.getInstance()
				.getPlanet(planetShortcut).getName());
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		for (Fragment f : taskStatsFragments) {
			try {
				TaskStatsFragment tf = (TaskStatsFragment) f;
				Task task = tf.task;

				ActionBar.Tab tab = actionBar.newTab();
				tab.setText(task.getName());
				tabs.add(tab);
				tab.setTabListener(new TabListener(tf));

			} catch (Exception e) {

			}
		}

		for (ActionBar.Tab t : tabs) {
			actionBar.addTab(t);
		}

		pager.setOnPageChangeListener(new PageChangeListener());
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, PlanetSelectionActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected List<Fragment> getFragments() {
		try {
			ContentResolver cr = getContentResolver();
			Cursor c = cr.query(
					Uri.parse("content://cz.cvut.elf." + planetShortcut
							+ ".provider/tasks"), null, null, null, null);

			c.moveToFirst();
			while (!c.isAfterLast()) {
				String taskName = c.getString(c.getColumnIndex("name"));
				int taskId = c.getInt(c.getColumnIndex("_id"));
				Task task = new Task(taskName, taskId);

				TaskStatsFragment fragment = new TaskStatsFragment();
				fragment.setTask(task);
				taskStatsFragments.add(fragment);

				c.moveToNext();
			}

			c.close();
		} catch (Exception e) {
		}

		return taskStatsFragments;
	}

	public String getPlanetShortcut() {
		return planetShortcut;
	}

	protected class PageChangeListener extends
			ViewPagerActivity.PageChangeListener {
		@Override
		public void onPageSelected(int position) {
			super.onPageSelected(position);

			getSupportActionBar().setSelectedNavigationItem(position);
		}
	}

	public class TabListener implements ActionBar.TabListener {

		TaskStatsFragment fragment;

		public TabListener(TaskStatsFragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			if (pager == null) {
				return;
			}

			pager.setCurrentItem(taskStatsFragments.indexOf(fragment));
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}
}
