/**
 * 
 */
package net.schuemie.GroceryList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

/**
 * @author Bhargav
 * 
 */
public class UserLoggedInPage extends Activity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admindashboard);

	}

	public void showInbox(View v) {
		// Toast.makeText(getBaseContext(), "Inbox", Toast.LENGTH_LONG).show();
		Intent intentInbox = new Intent(UserLoggedInPage.this, ViewSMS.class);
		startActivity(intentInbox);
	}

	public void listSAllUsers(View v) {
		// retrieveEntries();
		Intent intentInbox = new Intent(UserLoggedInPage.this, ViewAll.class);
		startActivity(intentInbox);
		Toast.makeText(getBaseContext(), "Listing all users", Toast.LENGTH_LONG)
				.show();
	}

	public void addCategory(View v) {
		Intent intent = new Intent(Intent.ACTION_EDIT,
				GroceryListContentProvider.CATEGORIES_URI);
		startActivity(intent);
		Toast.makeText(getBaseContext(), "addCategory", Toast.LENGTH_LONG)
				.show();
	}

	public void addItem(View v) {
		Intent addItem = new Intent(UserLoggedInPage.this, CreateCategory.class);
		startActivity(addItem);
		Toast.makeText(getBaseContext(), "addItem", Toast.LENGTH_LONG).show();
	}

	public void changePassword(View v) {
		Intent showlist = new Intent(UserLoggedInPage.this, GroceryList.class);
		startActivity(showlist);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		finish();
	}

}
