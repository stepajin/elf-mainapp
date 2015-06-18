/* Copyright (c) 2008-2011 -- CommonsWare, LLC

	 Licensed under the Apache License, Version 2.0 (the "License");
	 you may not use this file except in compliance with the License.
	 You may obtain a copy of the License at

		 http://www.apache.org/licenses/LICENSE-2.0

	 Unless required by applicable law or agreed to in writing, software
	 distributed under the License is distributed on an "AS IS" BASIS,
	 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 See the License for the specific language governing permissions and
	 limitations under the License.
 */

package cz.cvut.elf.mainapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ElfDatabaseOpenHelper extends SQLiteOpenHelper {
	public static final String ID = "_id";

	public static final String USERS_TABLE_NAME = "users";
	public static final String USER_NAME = "name";
	public static final String USER_GENDER = "gender";
	public static final String USER_BORNED_DATE = "borned";
	public static final String USER_ICON = "icon";

	public static final String PLANETS_TABLE_NAME = "planets";
	public static final String PLANET_SHORTCUT = "shortcut";
	public static final String PLANET_CZ = "cz";
	public static final String PLANET_EN = "en";
	public static final String PLANET_INSTALLED = "installed";

	public ElfDatabaseOpenHelper(Context context) {
		super(context, USERS_TABLE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createUsers(db);
		createPlanets(db);
	}

	private void createUsers(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE users (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, gender CHAR, borned DATETIME, icon INTEGER);");

		ContentValues cv = new ContentValues();
	}

	private void createPlanets(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE planets (_id INTEGER PRIMARY KEY AUTOINCREMENT, shortcut TEXT, cz TEXT, en TEXT, installed INTEGER);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS users");
		onCreate(db);
	}
}