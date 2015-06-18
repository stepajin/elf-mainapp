package cz.cvut.elf.mainapp.data;

import cz.cvut.elf.mainapp.ElfConstants;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AppInstalledReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		System.out.println("akce " + action);
		
		String pckg = intent.getData().toString();
		System.out.println("akce balicek " + pckg + " porovnat s " + ElfConstants.PACKAGE_PREFIX);
		
		if (!pckg.startsWith(ElfConstants.PACKAGE_PREFIX)) {
			System.out.println("akce different app");
			return;
		}

		String planetShortcut = pckg.replaceFirst(ElfConstants.PACKAGE_PREFIX, "");
		System.out.println("akce planeta " + planetShortcut);
		if (planetShortcut.equals("mainapp")) {
			System.out.println("akce mainapp");
			Log.d("Elf app", "updated");
			return;
		}

		ElfPlanetsDAO dao = ElfPlanetsDAO.getInstance();
		dao.loadPlanets(context);
		if (!dao.containPlanet(planetShortcut)) {
			System.out.println("akce neznama planeta");
			/* not existing planet */
			return;
		}

		System.out.println("akce");
		if (action.equals(Intent.ACTION_PACKAGE_ADDED)
				|| action.equals(Intent.ACTION_PACKAGE_INSTALL)) {
			Log.d("Elf", "new planet installed: " + planetShortcut);
			System.out.println("akce install " + planetShortcut);
			dao.updatePlanetInstalled(planetShortcut, true, context);
		} else if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
			Log.d("Elf", "planet uninstalled: " + planetShortcut);
			System.out.println("akce uninstall " + planetShortcut);
			dao.updatePlanetInstalled(planetShortcut, false, context);
		} else {
			System.out.println("akce blabla");
		}
	}
}
