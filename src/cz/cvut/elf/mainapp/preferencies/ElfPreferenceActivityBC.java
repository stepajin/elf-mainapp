package cz.cvut.elf.mainapp.preferencies;

import cz.cvut.elf.mainapp.R;
import cz.cvut.elf.mainapp.R.xml;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ElfPreferenceActivityBC extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);
	}
}
