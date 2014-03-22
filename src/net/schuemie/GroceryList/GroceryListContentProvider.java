package net.schuemie.GroceryList;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public class GroceryListContentProvider extends ContentProvider {
	public static final String AUTHORITY = "net.schuemie.GroceryList";
	public static final String ITEMS_TYPE = "vnd.android.cursor.dir/net.schuemie.groceryitem";
	public static final String ITEM_TYPE = "vnd.android.cursor.item/net.schuemie.groceryitem";
	public static final String CATEGORIES_TYPE = "vnd.android.cursor.dir/net.schuemie.grocerycategory";
	public static final String CATEGORY_TYPE = "vnd.android.cursor.item/net.schuemie.grocerycategory";
	public static final Uri ITEMS_URI = Uri.parse("content://" + AUTHORITY
			+ "/items");
	public static final Uri CATEGORIES_URI = Uri.parse("content://" + AUTHORITY
			+ "/categories");

	public static boolean isNew = false;
	private static final String TAG = "GroveryListContentProvider";
	private static final String DATABASE_NAME = "grocerylist.db";
	private static final int DATABASE_VERSION = 1;
	private static final int ITEMS = 1;
	private static final int ITEM_ID = 2;
	private static final int CATEGORIES = 3;
	private static final int CATEGORY_ID = 4;

	public static NumberFormat format = new DecimalFormat("######.##");
	public static final String ITEMS_TABLE_NAME = "items";
	public static final String CATEGORIES_TABLE_NAME = "categories";
	private DatabaseHelper databaseHelper;
	private static final UriMatcher uriMatcher;

	@Override
	public boolean onCreate() {
		databaseHelper = new DatabaseHelper(getContext());
		if (databaseHelper.newDatabase) {// This is a new install!
			// Intent intent = new
			// Intent(RestoreDefaultDatabase.RESTORE_DEFAULT);
			// getContext().startActivity(intent);
			// isNew = true;
		}
		return true;
	}

	public void close() {
		databaseHelper.close();
	}

	public void deleteAll() {
		SQLiteDatabase database = databaseHelper.getReadableDatabase();
		database.execSQL("DROP TABLE IF EXISTS " + ITEMS_TABLE_NAME);
		database.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE_NAME);
		databaseHelper.createDatabase(database);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String orderBy = "";

		switch (uriMatcher.match(uri)) {
		case ITEMS:
			qb.setTables(ITEMS_TABLE_NAME);
			orderBy = Items.NAME;
			break;

		case ITEM_ID:
			qb.setTables(ITEMS_TABLE_NAME);
			qb.appendWhere(BaseColumns._ID + "=" + uri.getPathSegments().get(1));
			break;

		case CATEGORIES:
			qb.setTables(CATEGORIES_TABLE_NAME);
			orderBy = Categories.NAME;
			break;

		case CATEGORY_ID:
			qb.setTables(CATEGORIES_TABLE_NAME);
			qb.appendWhere(BaseColumns._ID + "=" + uri.getPathSegments().get(1));
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
		// Get the database and run the query
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case ITEMS:
			return ITEMS_TYPE;
		case ITEM_ID:
			return ITEM_TYPE;
		case CATEGORIES:
			return CATEGORIES_TYPE;
		case CATEGORY_ID:
			return CATEGORY_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		long rowId;
		switch (uriMatcher.match(uri)) {
		case ITEMS:
			rowId = database.insert(ITEMS_TABLE_NAME, null, initialValues);
			if (rowId > 0) {
				Uri noteUri = ContentUris.withAppendedId(ITEMS_URI, rowId);
				getContext().getContentResolver().notifyChange(noteUri, null);
				return noteUri;
			}
			throw new SQLException("Failed to insert row into " + uri);

		case CATEGORIES:
			rowId = database.insert(CATEGORIES_TABLE_NAME, null, initialValues);
			if (rowId > 0) {
				Uri noteUri = ContentUris.withAppendedId(CATEGORIES_URI, rowId);
				getContext().getContentResolver().notifyChange(noteUri, null);
				return noteUri;
			}
			throw new SQLException("Failed to insert row into " + uri);

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		int count;
		switch (uriMatcher.match(uri)) {
		case ITEMS:
			count = database.delete(ITEMS_TABLE_NAME, where, whereArgs);
			break;
		case ITEM_ID:
			String itemId = uri.getPathSegments().get(1);
			count = database.delete(ITEMS_TABLE_NAME,
					BaseColumns._ID
							+ "="
							+ itemId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case CATEGORIES:
			count = database.delete(CATEGORIES_TABLE_NAME, where, whereArgs);
			break;
		case CATEGORY_ID:
			String categoryId = uri.getPathSegments().get(1);
			getContext().getContentResolver().delete(ITEMS_URI,
					Items.CATEGORY + "=" + categoryId, null);
			count = database.delete(
					CATEGORIES_TABLE_NAME,
					BaseColumns._ID
							+ "="
							+ categoryId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			// database.delete(ITEMS_TABLE_NAME, Items.CATEGORY + "=" +
			// categoryId, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase database = databaseHelper.getWritableDatabase();
		int count;
		switch (uriMatcher.match(uri)) {
		case ITEMS:
			count = database.update(ITEMS_TABLE_NAME, values, where, whereArgs);
			break;
		case ITEM_ID:
			String itemId = uri.getPathSegments().get(1);
			count = database.update(ITEMS_TABLE_NAME, values,
					BaseColumns._ID
							+ "="
							+ itemId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		case CATEGORIES:
			count = database.update(CATEGORIES_TABLE_NAME, values, where,
					whereArgs);
			break;
		case CATEGORY_ID:
			String categoryId = uri.getPathSegments().get(1);
			count = database.update(
					CATEGORIES_TABLE_NAME,
					values,
					BaseColumns._ID
							+ "="
							+ categoryId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	private class DatabaseHelper extends SQLiteOpenHelper {
		public boolean newDatabase = false;

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			createDatabase(db);
			newDatabase = true;
		}

		public void createDatabase(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + ITEMS_TABLE_NAME + " ("
					+ BaseColumns._ID + " INTEGER PRIMARY KEY," + Items.NAME
					+ " TEXT," + Items.AMOUNT + " FLOAT," + Items.UNIT
					+ " TEXT," + Items.ONLIST + " INTEGER," + Items.CATEGORY
					+ " INTEGER" + ");");
			db.execSQL("CREATE TABLE " + CATEGORIES_TABLE_NAME + " ("
					+ BaseColumns._ID + " INTEGER PRIMARY KEY,"
					+ Categories.NAME + " TEXT" + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Log.w(TAG, "Upgrading database from version " + oldVersion +
			// " to "
			// + newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + ITEMS_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + CATEGORIES_TABLE_NAME);
			onCreate(db);
		}
	}

	public static class Items implements BaseColumns {
		/**
		 * The name of the item
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String NAME = "name";

		/**
		 * The category of the item
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CATEGORY = "category";

		/**
		 * The amount
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String AMOUNT = "amount";

		/**
		 * The unit (e.g. "grams", "liters")
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String UNIT = "unit";

		/**
		 * Is the item on the shopping list?
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String ONLIST = "onlist";
	}

	public static class Categories implements BaseColumns {
		/**
		 * The name of the category
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String NAME = "name";
	}

	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, "items", ITEMS);
		uriMatcher.addURI(AUTHORITY, "items/#", ITEM_ID);
		uriMatcher.addURI(AUTHORITY, "categories", CATEGORIES);
		uriMatcher.addURI(AUTHORITY, "categories/#", CATEGORY_ID);
	}
}
