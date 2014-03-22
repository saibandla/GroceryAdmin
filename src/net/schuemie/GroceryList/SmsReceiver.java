package net.schuemie.GroceryList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
	String fromaddress;
	String messagebody;

	@Override
	public void onReceive(final Context context, Intent intent) {
		fromaddress = "";
		messagebody = "";

		System.err.println("onReceive is triggereeeeeeeeeeeeeeeeeeee");

		// ---get the SMS message passed in---
		Bundle bundle = intent.getExtras();
		SmsMessage[] msgs = null;

		String str = "";
		if (bundle != null) {
			// ---retrieve the SMS message received---
			Object[] pdus = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				str += "SMS from mmm"
						+ msgs[i].getOriginatingAddress().toString();
				if (msgs[i].getOriginatingAddress() != null)
					fromaddress += msgs[i].getOriginatingAddress().toString();
				str += " :";
				str += msgs[i].getMessageBody().toString();
				if (msgs[i].getMessageBody() != null)
					messagebody += msgs[i].getMessageBody().toString();
				str += "\n";

			}
			// ---display the new SMS message---
			Log.d("smsrecevr", "msgggggggggggggggggggg");
			Toast.makeText(context, str, Toast.LENGTH_SHORT).show();

			try {
				// for(int i=0;i<fromaddress.length;i++)
				// {
				if (messagebody.contains("NewUserRegistration")) {

					Intent newintent = new Intent();
					newintent.setClass(context, SmsAlertDialogActivity.class);
					newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					String delemsg = messagebody;
					// messagebody.replace("Grocery12F3", "");

					newintent.putExtra("smsbody", messagebody);
					newintent.putExtra("smstype", "neewuser");
					newintent.putExtra("fromaddr", fromaddress);
					context.startActivity(newintent);

					// new deleteSms(context, delemsg, fromaddress);

				} else if (messagebody.contains("Grocery12F3")) {
					Intent newintent = new Intent();
					newintent.setClass(context, SmsAlertDialogActivity.class);
					newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					String delemsg = messagebody;
					// messagebody.replace("Grocery12F3", "");

					newintent.putExtra("smsbody", messagebody);
					newintent.putExtra("smstype", "Order");
					newintent.putExtra("fromaddr", fromaddress);
					context.startActivity(newintent);

				} else {
					// new deleteSms(context, messagebody, fromaddress);
					System.exit(0);
				}

				// }
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

		}
	}
}