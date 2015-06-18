package cz.cvut.elf.mainapp.planetselection;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import cz.cvut.elf.R;
import cz.cvut.elf.mainapp.ViewPagerActivity;
import cz.cvut.elf.mainapp.data.ElfPlanetsDAO;
import cz.cvut.elf.mainapp.login.LoginActivity;
import cz.cvut.elf.mainapp.preferencies.ElfPreferenceActivity;

public class PlanetSelectionActivity extends ViewPagerActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		showSelectedUser();
		getSupportActionBar().setTitle(getString(R.string.planets));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onPause() {
		super.onPause();
		overridePendingTransition(0, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		String s = "  " + getString(R.string.action_settings) + "  ";
		menu.add(Menu.NONE, LoginActivity.SETTINGS_ID, Menu.NONE, s)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		s = "  " + getString(R.string.action_about) + "  ";
		menu.add(Menu.NONE, LoginActivity.ABOUT_ID, Menu.NONE, s)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Intent intent = new Intent(this, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(intent);
			return true;
		case LoginActivity.SETTINGS_ID:
			ElfPreferenceActivity.start(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	protected List<Fragment> getFragments() {

		List<Fragment> list = new ArrayList<Fragment>();

		ElfPlanetsDAO.getInstance().loadPlanets(this);
		List<Planet> planets = ElfPlanetsDAO.getInstance().getPlanets();
		for (Planet p : planets) {
			PlanetFragment pf = new PlanetFragment();
			pf.setPlanet(p);
			list.add(pf);
		}

		return list;
	}
}
