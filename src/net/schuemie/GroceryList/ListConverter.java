package net.schuemie.GroceryList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.Toast;

public class ListConverter extends Activity {

	@Override
	public void setContentView(int layoutResID) {
		// TODO Auto-generated method stub
		super.setContentView(R.layout.sms);
		Log.d("ListConverter", "setconentview is calllllldddddddddddddddddddd");

	}

	private GroceryList mItemsView;
	public static final int mStringItemsITEMNAME = 1;

	private void sendList() {

		Log.d("ListConverter", "sendlist() is calllllldddddddddddddddddddd");

		if (mItemsView.getListAdapter() instanceof CursorAdapter) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < mItemsView.getListAdapter().getCount(); i++) {
				Cursor item = (Cursor) mItemsView.getListAdapter().getItem(i);

				sb.append(item.getString(mStringItemsITEMNAME));
				// Put additional info (price, tags) in brackets

			}

			Intent i = new Intent();
			i.setAction(Intent.ACTION_SEND);
			i.setType("text/plain");

			try {
				startActivity(Intent.createChooser(i, getString(R.string.send)));
			} catch (ActivityNotFoundException e) {

			}
		} else {
			Toast.makeText(this, R.string.empty_list_not_sent,
					Toast.LENGTH_SHORT);
		}

	}

}