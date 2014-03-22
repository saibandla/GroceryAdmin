package net.schuemie.GroceryList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.gsm.SmsManager;

public class SendSmsToAll {
	DbHelper mydb = null;

	public SendSmsToAll(final Context context, final String message) {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated constructor stub
				mydb = new DbHelper(context);
				SQLiteDatabase db = mydb.getReadableDatabase();
				SmsManager smsmgr = SmsManager.getDefault();
				String[] columns = { "phoneno" };
				Cursor cursor = db.query(true, DbHelper.SAKET_TABLE_NAME,
						columns, null, null, null, null, null, null);
				cursor.moveToFirst();
				if (cursor.isFirst()) {
					do {
						try {
							String phoneno = cursor.getString(cursor
									.getColumnIndex("phoneno"));
							smsmgr.sendTextMessage(phoneno, null, message,
									null, null);

							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} while (cursor.moveToNext());
				}
			}
		}.start();
	}
}
