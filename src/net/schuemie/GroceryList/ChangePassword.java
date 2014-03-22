package net.schuemie.GroceryList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePassword extends Activity {

	public SharedPreferences prefs;
	private String prefName = "MyPref";
	private static final String TEXT_VALUE_KEY = "nothing";
	String passtemp = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_password);

		final EditText txtNewPassword = (EditText) findViewById(R.id.editTextnewpass);
		final EditText txtCurrentPassword = (EditText) findViewById(R.id.editTextcurrentpass);
		final EditText txtConfirmPassword = (EditText) findViewById(R.id.editTextconfirmpas);

		Button btnCancel = (Button) findViewById(R.id.buttoncancel);

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();

			}
		});

		Button btnSave = (Button) findViewById(R.id.buttonsave);

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				prefs = getSharedPreferences(prefName, MODE_PRIVATE);
				passtemp = prefs.getString(TEXT_VALUE_KEY, "qwerty");
				if (txtConfirmPassword.getText().toString()
						.equalsIgnoreCase("")
						| txtCurrentPassword.getText().toString()
								.equalsIgnoreCase("")
						| txtCurrentPassword.getText().toString()
								.equalsIgnoreCase("")) {
					Toast.makeText(getBaseContext(),
							"Please Complete the Information",
							Toast.LENGTH_SHORT).show();
				} else if (!txtNewPassword
						.getText()
						.toString()
						.equalsIgnoreCase(
								txtConfirmPassword.getText().toString())) {
					Toast.makeText(getBaseContext(),
							"These Passwords Don't Match !", Toast.LENGTH_SHORT)
							.show();
				} else if (!passtemp.equalsIgnoreCase(txtCurrentPassword
						.getText().toString())) {
					Toast.makeText(getBaseContext(), "Incorrect curr. passwd!",
							Toast.LENGTH_LONG).show();
				} else {
					// /------- //---save the values in the EditText view to
					// preferences---
					prefs = getPreferences(MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString(TEXT_VALUE_KEY, txtNewPassword.getText()
							.toString());
					// ---saves the values---
					editor.commit();
					// /--------
					String Password = txtNewPassword.getText().toString();
					Toast.makeText(getBaseContext(), Password,
							Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

}
