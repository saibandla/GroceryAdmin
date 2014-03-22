package net.schuemie.GroceryList;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsRcv extends BroadcastReceiver {

	public final static String CUSTOM_INTENT = "net.schuemie.GroceryList.action.SmsAlertDialogActivity";

	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {

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
			Toast.makeText(context, messages, Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressLint("NewApi")
	private void putSmsToDatabase(SmsMessage sms, Context context) {
		DataBaseHelper dataBaseHelper = new DataBaseHelper(context);

		SQLiteDatabase db = dataBaseHelper.getWritableDatabase();

		String mydate = java.text.DateFormat.getDateTimeInstance().format(
				Calendar.getInstance().getTime());
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

}
