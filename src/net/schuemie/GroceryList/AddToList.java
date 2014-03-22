package net.schuemie.GroceryList;

import net.schuemie.GroceryList.GroceryListContentProvider.Items;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddToList extends Activity {
	public static final String MODIFY_ITEM_STATUS = "net.schuemie.GroceryList.action.MODIFY_ITEM_STATUS";
	private TextView nameText;
	private TextView amount_unitText;
	private EditText amountEdit;
	private EditText unitEdit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addtolist);

		Button createButton = (Button) findViewById(R.id.add);
		Button cancelButton = (Button) findViewById(R.id.cancel);
		Button plusButton = (Button) findViewById(R.id.plus);
		Button minusButton = (Button) findViewById(R.id.minus);

		createButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				storeData(true);
				finish();
			}

		});

		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				storeData(false);
				finish();
			}

		});

		plusButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				double amount = getAmount();
				if (amount == 0)
					amount = 1;
				else if (amount < 999)
					for (int i = 2; i > -3; i--) {
						double factor = Math.pow(10, i);
						if (hasZeroModulus(amount, factor)) {
							amount += factor;
							break;
						}
					}
				amountEdit.setText(GroceryListContentProvider.format
						.format(amount));
				updateAmountUnit();

			}
		});

		minusButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				double amount = getAmount();
				if (amount > 0.01)
					for (int i = 2; i > -3; i--) {
						double factor = Math.pow(10, i);
						if (hasZeroModulus(amount, factor)) {
							if (amount == factor && i > -2)
								factor = Math.pow(10, i - 1);
							amount -= factor;

							amountEdit
									.setText(GroceryListContentProvider.format
											.format(amount));
							updateAmountUnit();
							break;
						}
					}
			}
		});

		nameText = (TextView) findViewById(R.id.name);
		amount_unitText = (TextView) findViewById(R.id.amount_unit);
		amountEdit = (EditText) findViewById(R.id.amount);
		unitEdit = (EditText) findViewById(R.id.unit);

		Cursor cursor = getContentResolver().query(getIntent().getData(), null,
				null, null, null);
		cursor.moveToFirst();
		String name = cursor.getString(cursor.getColumnIndex(Items.NAME));
		nameText.setText(name);
		amountEdit
				.setText(cursor.getString(cursor.getColumnIndex(Items.AMOUNT)));
		unitEdit.setText(cursor.getString(cursor.getColumnIndex(Items.UNIT)));
		View.OnKeyListener keyListener = new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				updateAmountUnit();
				return false;
			}
		};

		amountEdit.setOnKeyListener(keyListener);
		unitEdit.setOnKeyListener(keyListener);

		updateAmountUnit();
	}

	/**
	 * Workaround for Java bug: 0.9 % 0.1 is not equal to 0!
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean hasZeroModulus(double x, double y) {
		return (Math.round((x * 100000) % (y * 100000)) == 0);
	}

	private void updateAmountUnit() {
		amount_unitText.setText(amountEdit.getText().toString());
		String unit = unitEdit.getText().toString();
		if (!unit.equals("")) {
			amount_unitText.append(" ");
			amount_unitText.append(unit);
		}
	}

	private void storeData(boolean onList) {
		ContentValues values = new ContentValues();
		values.put(Items.AMOUNT, getAmount());
		values.put(Items.UNIT, unitEdit.getText().toString());
		values.put(Items.ONLIST, onList ? 1 : 0);
		getContentResolver().update(getIntent().getData(), values, null, null);
	}

	private double getAmount() {
		double amount;
		try {
			amount = Double.parseDouble(amountEdit.getText().toString());
		} catch (Exception e) {
			amount = 0d;
		}
		return amount;
	}

}