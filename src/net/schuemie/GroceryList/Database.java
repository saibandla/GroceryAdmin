package net.schuemie.GroceryList;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Database {
	public static final String INBOX_TABLE_NAME = "inbox";
	public static final String msg_ID = "_id";
	public static final String MSG_FROM = "fromaddress";
	public static final String TXT_MSG = "msg";
	public static final String SMS_DATE = "msgdate";

	public static void addInboxSms(Context context, String msgFrom,
			String date, String txtMSG) {
		DbHelper dbhelper = null;
		SQLiteDatabase db = null;

		try {
			dbhelper = new DbHelper(context);
			db = dbhelper.getWritableDatabase();
			// db.beginTransaction();

			ContentValues values = new ContentValues();

			values.put(MSG_FROM, msgFrom);
			values.put(SMS_DATE, date);
			values.put(TXT_MSG, txtMSG);

			long rowid = db.insert(Database.INBOX_TABLE_NAME, null, values);
			Log.d("INBOX MSG", "Inserted into INBOX " + rowid);

		} catch (Exception ex) {
			Log.d("INBOX", "Error in addsms -->" + ex.getMessage());

		} finally {
			if (db != null && db.isOpen()) {
				db.close();
			}
		}
	}
}