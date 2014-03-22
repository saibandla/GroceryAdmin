package net.schuemie.GroceryList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBhelpersms extends SQLiteOpenHelper {

	public static final int DB_VERSION = 1;
	public static final String DB_NAME = "smsinbox.db";

	public DBhelpersms(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		final String inbox_table_sql = "CREATE TABLE IF NOT EXISTS  "
				+ Database.INBOX_TABLE_NAME
				+ "("
				+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "fromaddress TEXT NOT NULL, msgdate TEXT NOT NULL, msg TEXT NOT NULL);";
		try {
			db.execSQL(inbox_table_sql);
			Log.d("smsinbox", "Table Created");
		} catch (Exception ex) {
			Log.d("inbox msg",
					"Error in DBhelper.onCreate() : " + ex.getMessage());
		}

	}

	private void createTables(SQLiteDatabase database) {
		String inbox_table_sql = "CREATE TABLE IF NOT EXISTS  "
				+ Database.INBOX_TABLE_NAME + " " + "( " + Database.msg_ID
				+ " integer  primary key autoincrement," + Database.MSG_FROM
				+ " TEXT," + Database.SMS_DATE + " TEXT," + Database.TXT_MSG
				+ " TEXT)";

		try {
			database.execSQL(inbox_table_sql);
			Log.d("smsinbox", "Table Created");
		} catch (Exception ex) {
			Log.d("inbox msg",
					"Error in DBhelper.onCreate() : " + ex.getMessage());
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
