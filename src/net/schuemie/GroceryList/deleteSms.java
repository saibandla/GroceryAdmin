package net.schuemie.GroceryList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

public class deleteSms {
	static final String Tag = "DeleteSms";

	public deleteSms(final Context context, final String message, String number) {
		// TODO Auto-generated constructor stub

		new Thread() {
			@Override
			@SuppressLint("NewApi")
			public void run() {
				try {

					// mLogger.logInfo("Deleting SMS from inbox");
					Uri uriSms = Uri.parse("content://sms/inbox");
					Cursor c = context.getContentResolver().query(
							uriSms,
							new String[] { "_id", "thread_id", "address",
									"person", "date", "body" }, null, null,
							null);
					// Toast.makeText(getBaseContext(), "Deleted successfully",
					// Toast.LENGTH_LONG).show();
					if (c != null && c.moveToFirst()) {
						do {
							long id = c.getLong(0);
							long threadId = c.getLong(1);
							String address = c.getString(2);
							String body = c.getString(5);
							String date = c.getString(4);
							Log.d(Tag, String.format("%s : %s", address, body));
							String[] arrayid = { body };

							if (message.equals(body)) {

								Thread.sleep(10000);
								context.getContentResolver().delete(
										Uri.parse("content://sms"), "body=?",
										arrayid);
								Log.d(Tag, "Deleted successfully");

								// Uri uri = ContentUris.withAppendedId(
								// Sms.Inbox.CONTENT_URI, id);
								// context.getContentResolver().delete(uri,
								// null,
								// null);

							}
						} while (c.moveToNext());

					}
				} catch (Exception e) {
					e.printStackTrace();
					// mLogger.logError("Could not delete SMS from inbox: " +
					// e.getMessage());
				}
			}
		}.start();

	}

	public void deletedsu() {

	}
}
