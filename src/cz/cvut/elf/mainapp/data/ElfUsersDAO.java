package cz.cvut.elf.mainapp.data;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import cz.cvut.elf.mainapp.ElfConstants;
import cz.cvut.elf.mainapp.login.User;

public class ElfUsersDAO {

	static ElfUsersDAO INSTANCE;

	List<User> users = new ArrayList<User>();

	private ElfDatabaseOpenHelper db = null;
	private Cursor usersDbCursor = null;

	public static ElfUsersDAO getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ElfUsersDAO();
		}

		return INSTANCE;
	}

	private ElfUsersDAO() {
	}

	public void editUser(int userId, ContentValues values, Context context) {
		String tableName = ElfDatabaseOpenHelper.USERS_TABLE_NAME;
		String columnName = ElfDatabaseOpenHelper.ID;

		ElfDatabaseOpenHelper db = new ElfDatabaseOpenHelper(context);
		db.getWritableDatabase().update(tableName, values, columnName + "=?",
				new String[] { "" + userId });
		db.close();
	}

	public void writeUser(ContentValues values, Context context) {
		db = new ElfDatabaseOpenHelper(context);
		db.getWritableDatabase().insert(ElfDatabaseOpenHelper.USERS_TABLE_NAME,
				ElfDatabaseOpenHelper.USER_NAME, values);
		db.close();
	}

	public void deleteUser(int userId, Context context) {
		db = new ElfDatabaseOpenHelper(context);
		String column = ElfDatabaseOpenHelper.ID;
		db.getWritableDatabase().delete(ElfDatabaseOpenHelper.USERS_TABLE_NAME,
				column + "=?", new String[] { "" + userId });
		db.close();
	}

	public void loadUsers(Context context) {
		users.clear();

		db = new ElfDatabaseOpenHelper(context);
		usersDbCursor = db.getReadableDatabase().rawQuery(
				"SELECT * " + "FROM users", null);

		usersDbCursor.moveToFirst();
		while (!usersDbCursor.isAfterLast()) {
			int id = usersDbCursor.getInt(usersDbCursor
					.getColumnIndex(ElfDatabaseOpenHelper.ID));
			String name = usersDbCursor.getString(usersDbCursor
					.getColumnIndex(ElfDatabaseOpenHelper.USER_NAME));
			char gender = usersDbCursor.getString(usersDbCursor
					.getColumnIndex(ElfDatabaseOpenHelper.USER_GENDER)).charAt(0);

			String bornedString = usersDbCursor.getString(usersDbCursor
					.getColumnIndex(ElfDatabaseOpenHelper.USER_BORNED_DATE));
			System.out.println("Z DB DATE " + bornedString);

			GregorianCalendar borned = new GregorianCalendar();
			try {
				Date date = ElfConstants.DATE_FORMAT.parse(bornedString); 
				borned.setTimeInMillis(date.getTime());
			} catch (ParseException e) {
			}

			int icon = usersDbCursor.getInt(usersDbCursor
					.getColumnIndex(ElfDatabaseOpenHelper.USER_ICON));

			users.add(new User(id, name, gender, borned, icon));
			usersDbCursor.moveToNext();
		}

		db.close();
		usersDbCursor.close();
	}

	public List<User> getUsers() {
		return users;
	}
	
	public User getUser(int userId) {
		for (User u : users) {
			if (u.getId() == userId) {
				return u;
			}
		}
		
		return null;
	}
}
