package net.schuemie.GroceryList;

import java.util.Vector;

import net.schuemie.GroceryList.GroceryListContentProvider.Items;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class GroceryListCursorAdapter extends CursorAdapter {
	private LayoutInflater inflater;
	public static Vector vector = new Vector();

	public GroceryListCursorAdapter(Context context, Cursor c) {
		super(context, c);
		inflater = LayoutInflater.from(context);
		vector.clear();
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		updateView(view, cursor);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = inflater.inflate(R.layout.item, null);
		updateView(view, cursor);
		return view;
	}

	private void updateView(View view, Cursor cursor) {
		TextView nameLabel = (TextView) view.findViewById(R.id.name);

		nameLabel.setText(cursor.getString(cursor.getColumnIndex(Items.NAME)));
		TextView amount_unitText = (TextView) view
				.findViewById(R.id.amount_unit);
		if (cursor.getInt(cursor.getColumnIndex(Items.ONLIST)) == 0) {
			nameLabel.setTextColor(Color.GRAY);
			amount_unitText.setText("");
		} else {
			nameLabel.setTextColor(Color.WHITE);
			// amount_unitText.setTextColor(Color.WHITE);
			System.err.println("nameeeeeeeeeeeeeeeeeeeeeeeeee = "
					+ cursor.getString(cursor.getColumnIndex(Items.NAME)));
			vector.add(cursor.getString(cursor.getColumnIndex(Items.NAME)));
			System.err.println("vector size = " + vector.size());
			double amountDouble = cursor.getDouble(cursor
					.getColumnIndex(Items.AMOUNT));
			String amount;
			if (amountDouble == 0)
				amount = "";
			else
				amount = GroceryListContentProvider.format.format(amountDouble);
			String unit = cursor.getString(cursor.getColumnIndex(Items.UNIT));
			if (unit == null)
				unit = "";
			amount_unitText.setText(amount);
			if (!unit.equals("")) {
				amount_unitText.append(" ");
				amount_unitText.append(unit);
			}
		}

	}

}
