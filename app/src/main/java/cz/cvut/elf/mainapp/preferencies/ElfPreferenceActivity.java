package cz.cvut.elf.mainapp.preferencies;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

import cz.cvut.elf.R;
import cz.cvut.elf.mainapp.login.LoginActivity;

public class ElfPreferenceActivity extends PreferenceActivity {

	public static void start(Context context) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			context.startActivity(new Intent(context,
					ElfPreferenceActivityBC.class));
		} else {
			/*context.startActivity(new Intent(context,
					ElfPreferenceActivity.class));*/
			context.startActivity(new Intent(context,
					ElfPreferenceActivityBC.class));
		}
	}

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.headers, target);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
