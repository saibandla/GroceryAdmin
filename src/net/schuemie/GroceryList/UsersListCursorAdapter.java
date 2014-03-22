package net.schuemie.GroceryList;

import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class UsersListCursorAdapter extends CursorAdapter {
	private final LayoutInflater inflater;
	public static Vector vector = new Vector();

	public UsersListCursorAdapter(Context context, Cursor c) {
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
		View view = inflater.inflate(R.layout.userdetailsitem, null);
		updateView(view, cursor);
		return view;
	}

	private void updateView(View view, Cursor cursor) {
		TextView nameLabel = (TextView) view.findViewById(R.id.user_name);

		nameLabel.setText(cursor.getString(cursor.getColumnIndex("username")));
		TextView phone = (TextView) view.findViewById(R.id.phone_no);
		String date_k = cursor.getString(cursor.getColumnIndex("phoneno"));

		phone.setText(date_k);
		TextView addr = (TextView) view.findViewById(R.id.address_u);
		String msg_body = cursor.getString(cursor.getColumnIndex("address"));

		msg_body = msg_body.replace(',', '\n');
		addr.setText(msg_body);

	}

}
