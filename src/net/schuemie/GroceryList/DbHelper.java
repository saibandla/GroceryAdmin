/**
 * 
 */
package net.schuemie.GroceryList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author saket
 * 
 */
public class DbHelper extends SQLiteOpenHelper {
	SQLiteDatabase db;
	private static final String DATABASE_NAME = "saket.db";
	private static final int DATABASE_VERSION = 1;
	public static final String SAKET_TABLE_NAME = "login";
	private static final String SAKET_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS  "
			+ SAKET_TABLE_NAME
			+ "("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "username TEXT NOT NULL, phoneno TEXT NOT NULL, email TEXT NOT NULL, address TEXT NOT NULL);";
	private static final String SAKET_DB_ADMIN = "INSERT INTO "
			+ SAKET_TABLE_NAME + "values(1, admin, password, admin@gmail.com);";
	final String inbox_table_sql = "CREATE TABLE IF NOT EXISTS  "
			+ Database.INBOX_TABLE_NAME
			+ "("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "fromaddress TEXT NOT NULL, msgdate TEXT NOT NULL, msg TEXT NOT NULL);";

	public DbHelper(Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		System.out.println("In constructor");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {

		try {
			// Create Database
			db.execSQL(SAKET_TABLE_CREATE);
			db.execSQL(inbox_table_sql);
			// create admin account
			db.execSQL(SAKET_DB_ADMIN);
			System.out.println("In onCreate");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String isPasswordExists(String passwrd) {
		db = this.getReadableDatabase();
		Cursor cursor = db.query(SAKET_TABLE_NAME, null, " password=?",
				new String[] { passwrd }, null, null, null);
		if (cursor.getCount() < 1) // UserName Not Exist
		{
			cursor.close();
			return "Not Exist";
		} else {
			cursor.moveToFirst();
			String username = cursor.getString(cursor
					.getColumnIndex("username"));
			cursor.close();
			return username;

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
	 * .SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int oldVersion, int newVersion) {

	}

}
