package net.schuemie.GroceryList;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SMSdup extends Activity {

	Button btnSendSMS;
	EditText txtPhoneNo;
	EditText txtMessage;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms);
		btnSendSMS = (Button) findViewById(R.id.btnSendSMS);
		txtPhoneNo = (EditText) findViewById(R.id.txtPhoneNo);
		txtMessage = (EditText) findViewById(R.id.txtMessage);

		/*
		 * Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		 * sendIntent.putExtra("sms_body", "Content of the SMS goes here...");
		 * sendIntent.setType("vnd.android-dir/mms-sms");
		 * startActivity(sendIntent);
		 */

		btnSendSMS.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String phoneNo = txtPhoneNo.getText().toString();
				String message = txtMessage.getText().toString();

				Log.d("SMSdup", "onlick send buttom.................");

				if (phoneNo.length() > 0 && message.length() > 0)
					sendSMS(phoneNo, message);
				else
					Toast.makeText(getBaseContext(),
							"Please enter both phone number and message.",
							Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void sendSMS(String phoneNumber, String message) {
		Log.d("SMSdup", "sendSMS.................");

		/*
		 * PendingIntent pi = PendingIntent.getActivity(this, 0, new
		 * Intent(this, test.class), 0); SmsManager sms =
		 * SmsManager.getDefault(); sms.sendTextMessage(phoneNumber, null,
		 * message, pi, null);
		 */

		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
				SENT), 0);

		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
				new Intent(DELIVERED), 0);

		// ---when the SMS has been sent---
		registerReceiver(new BroadcastReceiver() {
			@SuppressLint("NewApi")
			@Override
			public void onReceive(Context context, Intent intent) {

				Log.d("SMSdup", "onReceive.................");

				// —get the SMS message passed in—
				Bundle bundle = intent.getExtras();
				SmsMessage[] msgs = null;
				String messages = "";
				if (bundle != null) {
					// —retrieve the SMS message received—
					Object[] smsExtra = (Object[]) bundle.get("pdus");
					msgs = new SmsMessage[smsExtra.length];

					for (int i = 0; i < msgs.length; i++) {
						SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);
						// take out content from sms
						String body = sms.getMessageBody().toString();
						String address = sms.getOriginatingAddress();

						messages += "SMS from " + address + "  :\n";
						messages += body + " \n";

						putSmsToDatabase(sms, context);
					}
					// —display the new SMS message—
					Toast.makeText(context, messages, Toast.LENGTH_SHORT)
							.show();
				}

			}

			@SuppressLint("NewApi")
			private void putSmsToDatabase(SmsMessage sms, Context context) {

				Log.d("SMSdup", "putSmsTODatabase...............");

				DataBaseHelper dataBaseHelper = new DataBaseHelper(context);

				SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

				String mydate = java.text.DateFormat.getDateTimeInstance()
						.format(Calendar.getInstance().getTime());
				// Create SMS row
				ContentValues values = new ContentValues();

				values.put("address", sms.getOriginatingAddress().toString());
				values.put("date", mydate);
				values.put("body", sms.getMessageBody().toString());
				// values.put( READ, MESSAGE_IS_NOT_READ );
				// values.put( STATUS, sms.getStatus() );
				// values.put( TYPE, MESSAGE_TYPE_INBOX );
				// values.put( SEEN, MESSAGE_IS_NOT_SEEN );

				db.insert("datatable", null, values);

				db.close();

			}
		}, new IntentFilter());

	}
}