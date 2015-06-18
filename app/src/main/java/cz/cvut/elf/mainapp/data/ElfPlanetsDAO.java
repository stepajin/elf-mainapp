package cz.cvut.elf.mainapp.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import cz.cvut.elf.mainapp.ElfConstants;
import cz.cvut.elf.mainapp.planetselection.Planet;

public class ElfPlanetsDAO {

	private static ElfPlanetsDAO INSTANCE = null;

	List<Planet> planets = new ArrayList<Planet>();

	private ElfDatabaseOpenHelper db = null;
	private Cursor planetsDbCursor = null;

	public static ElfPlanetsDAO getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ElfPlanetsDAO();
		}

		return INSTANCE;
	}

	private ElfPlanetsDAO() {
	}

	public void close() {
		if (db != null) {
			db.close();
		}
		if (planetsDbCursor != null) {
			planetsDbCursor.close();
		}
	}

	public Bitmap loadPlanetBitmap(String shortcut, Context context) {
		Bitmap bitmap = null;
		try {
			File dir = context.getDir(Planet.BITMAP_DIR_NAME,
					Context.MODE_PRIVATE);
			String path = dir.getAbsolutePath();
			File f = new File(path, shortcut + ".png");
			bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	public void savePlanetBitmap(Bitmap bitmap, String shorcut, Context context) {
		File dir = context.getDir(Planet.BITMAP_DIR_NAME, Context.MODE_PRIVATE);
		File mypath = new File(dir, shorcut + ".png");

		if (ElfConstants.DEBUG) {
			Log.d("Planet DAO", "saving " + dir.getAbsolutePath() + "/"
					+ shorcut + ".png");
		}

		FileOutputStream fos = null;
		try {

			fos = new FileOutputStream(mypath);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writePlanet(Planet planet, Context context) {
		if (ElfConstants.DEBUG) {
			Log.d("Planet DAO", "write planet " + planet.getShortcut());
		}

		String shortcut = planet.getShortcut();
		String cz = planet.getName();
		ContentValues values = new ContentValues(2);

		values.put(ElfDatabaseOpenHelper.PLANET_SHORTCUT, shortcut);
		values.put(ElfDatabaseOpenHelper.PLANET_CZ, cz);
		values.put(ElfDatabaseOpenHelper.PLANET_EN, cz);
		values.put(ElfDatabaseOpenHelper.PLANET_INSTALLED, 0);

		ElfDatabaseOpenHelper db = new ElfDatabaseOpenHelper(context);

		db.getWritableDatabase().insert(
				ElfDatabaseOpenHelper.PLANETS_TABLE_NAME,
				ElfDatabaseOpenHelper.PLANET_SHORTCUT, values);

		db.close();
	}

	public void updatePlanetInstalled(String shortcut, boolean installed,
			Context context) {
		db = new ElfDatabaseOpenHelper(context);

		ContentValues cv = new ContentValues();
		cv.put(ElfDatabaseOpenHelper.PLANET_INSTALLED, installed);
		String tableName = ElfDatabaseOpenHelper.PLANETS_TABLE_NAME;
		String columnName = ElfDatabaseOpenHelper.PLANET_SHORTCUT;
		db.getWritableDatabase().update(tableName, cv, columnName + "=?",
				new String[] { shortcut });

		db.close();
	}

	public void loadPlanets(Context context) {
		planets.clear();
		db = new ElfDatabaseOpenHelper(context);
		planetsDbCursor = db.getReadableDatabase().rawQuery(
				"SELECT _ID, shortcut, cz, en, installed " + "FROM planets",
				null);

		planetsDbCursor.moveToFirst();
		while (!planetsDbCursor.isAfterLast()) {
			String shortcut = planetsDbCursor.getString(planetsDbCursor
					.getColumnIndex(ElfDatabaseOpenHelper.PLANET_SHORTCUT));
			String cz = planetsDbCursor.getString(planetsDbCursor
					.getColumnIndex(ElfDatabaseOpenHelper.PLANET_CZ));
			int installedInt = planetsDbCursor.getInt(planetsDbCursor
					.getColumnIndex(ElfDatabaseOpenHelper.PLANET_INSTALLED));
			boolean installed = (installedInt == 0) ? false : true;

			planets.add(new Planet(shortcut, cz, installed));
			planetsDbCursor.moveToNext();
		}

		db.close();
		planetsDbCursor.close();
	}

	public boolean containPlanet(String shortcut) {
		for (Planet p : planets) {
			if (p.getShortcut().equals(shortcut)) {
				return true;
			}
		}
		return false;
	}

	public List<Planet> getPlanets() {
		return planets;
	}

	public Planet getPlanet(String planetShortcut) {
		for (Planet p : planets) {
			if (p.getShortcut().equals(planetShortcut)) {
				return p;
			}
		}
		
		return null;
	}
	
}
