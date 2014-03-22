package net.schuemie.GroceryList;

import java.sql.Date;
import java.util.Vector;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class InboxCursorAdapter extends CursorAdapter {
	private final LayoutInflater inflater;
	public static Vector vector = new Vector();

	public InboxCursorAdapter(Context context, Cursor c) {
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
		View view = inflater.inflate(R.layout.inboxitem, null);
		updateView(view, cursor);
		return view;
	}

	private void updateView(View view, Cursor cursor) {
		TextView nameLabel = (TextView) view.findViewById(R.id.address_no);

		nameLabel.setText(cursor.getString(cursor
				.getColumnIndex(Database.MSG_FROM)));
		TextView amount_unitText = (TextView) view
				.findViewById(R.id.date_arrive);
		String date_k = cursor.getString(cursor
				.getColumnIndex(Database.SMS_DATE));
		Long dt = Long.parseLong(date_k);
		Date msg_date = new Date(dt);

		amount_unitText.setText(msg_date.toLocaleString());
		TextView sms_body = (TextView) view.findViewById(R.id.sms_body);
		String msg_body = cursor.getString(cursor
				.getColumnIndex(Database.TXT_MSG));
		if (msg_body.contains("Grocery12F3")) {
			msg_body = msg_body.replace("Grocery12F3", "");
		}
		msg_body = msg_body.replace(',', '\n');
		sms_body.setText(msg_body);

	}

}
