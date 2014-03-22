package net.schuemie.GroceryList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Toast;

public class SmsAlertDialogActivity extends Activity {
	String text;
	String fromtext, type;
	String acceptbutton = "";
	private final DbHelper myDb = new DbHelper(SmsAlertDialogActivity.this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grocery_home);
		text = getIntent().getStringExtra("smsbody");
		fromtext = getIntent().getStringExtra("fromaddr");
		type = getIntent().getStringExtra("smstype");
		System.err
				.println("SmsAlertDialogActivity read ======================= "
						+ text);
		if (type.equals("Order")) {
			acceptbutton = "Read";
		} else {
			acceptbutton = "Save";
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("New Message")
				.setMessage("Are you sure you want to Save Data ?")
				.setCancelable(false)
				.setPositiveButton(acceptbutton,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								if (acceptbutton.equals("Read")) {
									Intent i = new Intent(
											SmsAlertDialogActivity.this,
											SmsRead.class);
									i.putExtra("smsbody", text);
									i.putExtra("smsfrom", fromtext);
									startActivity(i);
								} else if (acceptbutton.equals("Save")) {

									SQLiteDatabase db = myDb
											.getWritableDatabase();
									new deleteSms(SmsAlertDialogActivity.this,
											text, fromtext);
									text = text.replace("NewUserRegistration ",
											"");
									String[] mymsg = text.split(",");
									ContentValues values = new ContentValues();
									values.put("username", mymsg[0]);
									values.put("phoneno", fromtext);
									values.put("email", mymsg[1]);
									values.put("address", mymsg[2]);
									try {
										db.insert(DbHelper.SAKET_TABLE_NAME,
												null, values);
										Toast.makeText(getApplicationContext(),
												"Inserted", Toast.LENGTH_SHORT)
												.show();
										finish();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								System.err
										.println("SmsAlertDialogActivity read =============2222========== "
												+ text);
								/*
								 * TextView view = new
								 * TextView(SmsAlertDialogActivity.this); Uri
								 * uriSMSURI = Uri.parse("content://sms/inbox");
								 * Cursor cur =
								 * getContentResolver().query(uriSMSURI, null,
								 * null, null,null); String sms = ""; while
								 * (cur.moveToNext()) { sms += "From :" +
								 * cur.getString(2) + " : " +
								 * cur.getString(11)+"\n"; } view.setText(sms);
								 * setContentView(view);
								 */
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}
}