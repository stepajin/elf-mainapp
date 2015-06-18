package cz.cvut.elf.mainapp.planetselection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import cz.cvut.elf.mainapp.ElfConstants;
import cz.cvut.elf.mainapp.data.ElfPlanetsDAO;

public class Planet {

	public static final String BITMAP_DIR_NAME = "planets";
	public static final String BITMAP_DIR_PATH = "planets/";

	String shortcut;
	String name;
	Bitmap bitmap;
	boolean installed;
	
	public Planet(String shortcut, String name, boolean installed) {
		this.shortcut = shortcut;
		this.name = name;
		this.installed = installed;
	}

	public String getShortcut() {
		return shortcut;
	}

	public String getName() {
		return name;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	public boolean isInstalled() {
		return installed;
	}

	public void loadBitmap(Context context) {
		ElfPlanetsDAO dao = ElfPlanetsDAO.getInstance();
		bitmap = dao.loadPlanetBitmap(shortcut, context);
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
}
