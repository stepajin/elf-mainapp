package cz.cvut.elf.mainapp.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class UsersProvider extends ContentProvider {
	static final String PROVIDER_NAME = "cz.cvut.elf.mainapp.provider";
	static final String URL = "content://" + PROVIDER_NAME + "/users";
	public static final Uri CONTENT_URI = Uri.parse(URL);
	static final int USERS = 1;
	static final int USER_ID = 2;
	static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(PROVIDER_NAME, "users", USERS);
		uriMatcher.addURI(PROVIDER_NAME, "users/#", USER_ID);
	}

	@Override
	public boolean onCreate() {
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase db = (new ElfDatabaseOpenHelper(getContext()))
				.getReadableDatabase();

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(ElfDatabaseOpenHelper.USERS_TABLE_NAME);

		switch (uriMatcher.match(uri)) {
		case USERS:
			break;
		case USER_ID:
			queryBuilder.appendWhere(ElfDatabaseOpenHelper.ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		return "User";
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
