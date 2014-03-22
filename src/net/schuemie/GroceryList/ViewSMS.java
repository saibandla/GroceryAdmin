package net.schuemie.GroceryList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ViewSMS extends Activity {

	ListView listMessages;
	Cursor viewsms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewsms);
		listMessages = (ListView) this.findViewById(R.id.listSms);
		listMessages.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				long i = arg0.getSelectedItemId();
				Log.d("Cursor", i + "");
				viewsms.moveToPosition(arg2);
				String msg_body = viewsms.getString(viewsms
						.getColumnIndex(Database.TXT_MSG));
				if (msg_body.contains("Grocery12F3")) {
					msg_body = msg_body.replace("Grocery12F3", "");
				}
				msg_body = msg_body.replace(',', '\n');
				Intent newIntent = new Intent(ViewSMS.this, SngleSms.class);
				newIntent.putExtra("message", msg_body);

				startActivity(newIntent);
			}

		});
	}

	@Override
	public void onStart() {
		super.onStart();
		try {
			DbHelper dbhelper = new DbHelper(this);
			SQLiteDatabase db = dbhelper.getReadableDatabase();
			Log.d("ViewSms", "select from databse");

			viewsms = db.query(Database.INBOX_TABLE_NAME, null, null, null,
					null, null, null);

			if (viewsms.getCount() == 0) {
				this.findViewById(R.id.heading).setVisibility(View.INVISIBLE);
				this.findViewById(R.id.textError).setVisibility(View.VISIBLE);
			} else {
				this.findViewById(R.id.heading).setVisibility(View.VISIBLE);
				this.findViewById(R.id.textError).setVisibility(View.INVISIBLE);
			}
			// ArrayList<Map<String, String>> listSms = new
			// ArrayList<Map<String, String>>();
			// while (viewsms.moveToNext()) {
			// // get customer details for display
			// Log.d("ViewSMS", "sms detail displayed");
			// LinkedHashMap<String, String> cust = new LinkedHashMap<String,
			// String>();
			//
			// String smsfrom = viewsms.getString(viewsms
			// .getColumnIndex(Database.MSG_FROM));
			// String smsdate = viewsms.getString(viewsms
			// .getColumnIndex(Database.SMS_DATE));
			// String smsbody = viewsms.getString(viewsms
			// .getColumnIndex(Database.TXT_MSG));
			//
			// //
			// cust.put("addr2",viewcust.getString(viewcust.getColumnIndex(Database.CUSTOMERS_ADDRESS2)));
			//
			// cust.put("smsfrom", smsfrom);
			// cust.put("smsdate", smsdate);
			// cust.put("smsbody", smsbody);
			//
			// listSms.add(cust);
			//
			// }
			//
			// viewsms.close();
			// db.close();
			// dbhelper.close();
			//
			// Log.d("ViewCustomers", "customers layout is visible");
			//
			// SimpleAdapter adapter = new SimpleAdapter(this, listSms,
			// R.layout.list_sms, new String[] { "smsfrom", "smsdate",
			// "smsbody" }, new int[] { R.id.textSmsFrom,
			// R.id.textSmsDate, R.id.textSmsBody });
			CursorAdapter adapter = new InboxCursorAdapter(this, viewsms);

			listMessages.setAdapter(adapter);
		} catch (Exception ex) {
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

}
