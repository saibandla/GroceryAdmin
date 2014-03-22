package net.schuemie.GroceryList;

import net.schuemie.GroceryList.GroceryListContentProvider.Items;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class ConfirmCheck extends Activity {
	public static final String CHECK_ITEM_OFF = "net.schuemie.GroceryList.action.CHECK_ITEM_OFF";
	public static final String ASK_QUESTION = "ASK_QUESTION";
	private CheckBox checkBox;
	private Uri itemUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		itemUri = getIntent().getData();
		if (!getPreference()) {
			checkOff();
			setResult(RESULT_OK);
			finish();
		}

		setContentView(R.layout.confimcheck);

		Button okButton = (Button) findViewById(R.id.ok);
		Button cancelButton = (Button) findViewById(R.id.cancel);
		checkBox = (CheckBox) findViewById(R.id.performcheck);

		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				checkOff();
				setResult(RESULT_OK);
				finish();
			}

		});

		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				setResult(RESULT_CANCELED);
				finish();
			}

		});

		checkBox.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				changePreference();
			}

		});

		TextView nameText = (TextView) findViewById(R.id.name);
		TextView amount_unitText = (TextView) findViewById(R.id.amount_unit);

		Cursor cursor = getContentResolver().query(itemUri, null, null, null,
				null);
		cursor.moveToFirst();
		String name = cursor.getString(cursor.getColumnIndex(Items.NAME));
		nameText.setText(name);
		amount_unitText.setText(cursor.getString(cursor
				.getColumnIndex(Items.AMOUNT)));
		String unit = cursor.getString(cursor.getColumnIndex(Items.UNIT));
		if (!unit.equals("")) {
			amount_unitText.append(" ");
			amount_unitText.append(unit);
		}
	}

	private void changePreference() {
		SharedPreferences preferences = getSharedPreferences(
				ConfirmCheck.class.getName(), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(ConfirmCheck.ASK_QUESTION, checkBox.isChecked());
		editor.commit();
	}

	private boolean getPreference() {
		SharedPreferences preferences = getSharedPreferences(
				ConfirmCheck.class.getName(), Context.MODE_PRIVATE);
		return preferences.getBoolean(ConfirmCheck.ASK_QUESTION, true);
	}

	private void checkOff() {
		ContentValues values = new ContentValues();
		values.put(Items.ONLIST, 0);
		getContentResolver().update(itemUri, values, null, null);
	}

}