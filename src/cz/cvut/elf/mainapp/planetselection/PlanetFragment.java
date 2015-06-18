package cz.cvut.elf.mainapp.planetselection;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import cz.cvut.elf.mainapp.ElfConstants;
import cz.cvut.elf.mainapp.R;
import cz.cvut.elf.mainapp.data.ElfPlanetsDAO;
import cz.cvut.elf.mainapp.login.User;

public class PlanetFragment extends SherlockFragment {
	public static final int STATS_ID = Menu.FIRST + 1;
	public static final int DOWNLOAD_ID = STATS_ID + 1;
	public static final int UNINSTALL_ID = DOWNLOAD_ID + 1;

	ImageButton button;
	TextView nameView;
	Planet planet;
	ImageView locked;

	Menu menu;
	boolean uninstalling = false;
	boolean installing = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(true);

		View v = inflater.inflate(R.layout.fragment_planet, container, false);
		button = (ImageButton) v.findViewById(R.id.planet_image_button);
		nameView = (TextView) v.findViewById(R.id.planet_name_text);

		try {
			String planetShortcut = savedInstanceState
					.getString(ElfConstants.BUNDLE_PLANET_SHORTCUT);
			this.setPlanet(ElfPlanetsDAO.getInstance()
					.getPlanet(planetShortcut));
		} catch (Exception e) {
		}

		if (planet != null) {
			planet.loadBitmap(this.getActivity());
			if (planet.getBitmap() != null) {
				button.setImageBitmap(planet.getBitmap());
			}

			nameView.setText(planet.getName());
		}

		locked = (ImageView) v.findViewById(R.id.planet_locked_image);

		return v;
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		if (planet != null) {
			bundle.putString(ElfConstants.BUNDLE_PLANET_SHORTCUT,
					planet.getShortcut());
		}
		super.onSaveInstanceState(bundle);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		this.menu = menu;

		String s = "  " + getString(R.string.action_stats) + "  ";
		if (planet != null && planet.isInstalled()) {
			menu.add(Menu.NONE, STATS_ID, Menu.NONE, s)
					.setIcon(R.drawable.ic_action_storage)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_ALWAYS
									| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

			s = "  " + getString(R.string.action_uninstall) + "  ";
			menu.add(Menu.NONE, UNINSTALL_ID, Menu.NONE, s)
					.setIcon(R.drawable.ic_action_delete)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_ALWAYS
									| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		} else {
			s = "  " + getString(R.string.action_download) + "  ";
			menu.add(Menu.NONE, DOWNLOAD_ID, Menu.NONE, s)
					.setIcon(R.drawable.ic_action_download)
					.setShowAsAction(
							MenuItem.SHOW_AS_ACTION_ALWAYS
									| MenuItem.SHOW_AS_ACTION_WITH_TEXT);

		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (planet == null) {
			return;
		}

		getActivity().supportInvalidateOptionsMenu();

		if (uninstalling || installing) {
			planet.installed = true;
			try {
				String pckg = ElfConstants.NAMESPACE_PREFIX
						+ planet.getShortcut();
				ApplicationInfo info = getActivity().getPackageManager()
						.getApplicationInfo(pckg, 0);
			} catch (PackageManager.NameNotFoundException e) {
				planet.installed = false;
			}
			
			if (menu != null) {
				menu.clear();
				onCreateOptionsMenu(menu, getSherlockActivity()
						.getSupportMenuInflater());
			}
			uninstalling = false;
			installing = false;
		}

		if (planet.isInstalled()) {
			locked.setVisibility(View.GONE);
			button.setOnClickListener(new LaunchPlanetOnClickListener());
		} else {
			locked.setVisibility(View.VISIBLE);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					onDownloadActionClicked();
				}
			});

		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DOWNLOAD_ID:
			onDownloadActionClicked();
			return true;
		case UNINSTALL_ID:
			onUninstallActionClicked();
			return true;
		case STATS_ID:
			onStatsActionClicked();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void onStatsActionClicked() {
		if (planet == null) {
			return;
		}
		Intent intent = new Intent(getActivity(),
				PlanetStatisticsActivity.class);
		intent.putExtra(PlanetStatisticsActivity.PLANET_SHORTCUT_EXTRA,
				planet.shortcut);
		startActivity(intent);
	}

	private void onDownloadActionClicked() {
/*		final String appPackageName = "com.google.android.gm";
		try {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("market://details?id=" + appPackageName)));
		} catch (android.content.ActivityNotFoundException e) {
			startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("http://play.google.com/store/apps/details?id="
							+ appPackageName)));

		}*/
		if (planet == null) {
			return;
		}

		installing = true;
		String url = ElfConstants.SERVER_DIR_URL + planet.getShortcut() + ".html";
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	private void onUninstallActionClicked() {
		uninstalling = true;
		Intent intent = new Intent(Intent.ACTION_DELETE);
		intent.setData(Uri.parse(ElfConstants.PACKAGE_PREFIX
				+ planet.getShortcut()));
		startActivity(intent);
	}

	public void setPlanet(Planet planet) {
		this.planet = planet;
	}

	public Planet getPlanet() {
		return planet;
	}

	private class LaunchPlanetOnClickListener implements OnClickListener {

		static final String PACKAGE = ElfConstants.NAMESPACE_PREFIX;
		static final String INTENT_NAME = "planet_launch_intent";

		@Override
		public void onClick(View v) {
			if (planet == null || !planet.isInstalled()) {
				return;
			}
			try {
				String intentName = PACKAGE + planet.getShortcut() + "."
						+ INTENT_NAME;
				Intent intent = new Intent(intentName);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				intent.putExtra("USER_ID", User.selectedUser.getId());
				startActivity(intent);
			} catch (Exception e) {
				Log.e("Launch Planet", "Error while passing intent");
			}
		}
	}
}