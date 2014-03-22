package net.schuemie.GroceryList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SmsRead extends Activity {
	static final String Tag = "ReadSms";
	SQLiteDatabase db;
	String nuimb = "";
	DbHelper mydb = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.smsreader);
		mydb = new DbHelper(SmsRead.this);
		TextView smsTxt = (TextView) findViewById(R.id.smsTxt);
		final String text = getIntent().getStringExtra("smsbody");
		String msg_body = text;
		if (msg_body.contains("Grocery12F3")) {
			msg_body = msg_body.replace("Grocery12F3", "");
		}
		msg_body = msg_body.replace(',', '\n');
		smsTxt.setText(msg_body);

		TextView frmTxt = (TextView) findViewById(R.id.fromtxt);
		final String fromtext = getIntent().getStringExtra("smsfrom");
		frmTxt.setText(fromtext);
		Button saveBtn = (Button) findViewById(R.id.buttonsave);
		saveBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// deleteSMS(getBaseContext(), text, fromtext);

				saveSMS(getBaseContext(), text, fromtext);
			}
		});

	}

	public void saveSMS(final Context context, final String message,
			String number) {
		nuimb = number;

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
							if (message.equals(body)) {
								Log.v(Tag, "saving data");

								Database.addInboxSms(getBaseContext(), address,
										date, body);
								Thread.sleep(10000);

								new deleteSms(getBaseContext(), body, address);
								finish();
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

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.exit(0);
	}
}
