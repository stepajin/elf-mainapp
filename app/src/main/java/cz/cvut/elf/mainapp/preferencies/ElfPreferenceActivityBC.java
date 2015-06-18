package cz.cvut.elf.mainapp.preferencies;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import cz.cvut.elf.R;

public class ElfPreferenceActivityBC extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);
	}
}
