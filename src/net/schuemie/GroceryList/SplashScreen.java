/**
 * 
 */
package net.schuemie.GroceryList;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

/**
 * @author saket
 * 
 */
public class SplashScreen extends Activity {

	protected boolean _active = true;

	// time to display the splash screen in ms
	protected int _splashTime = 4000;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		// DBhelpersms ha = new DBhelpersms(getBaseContext());
		// Intent intent = new Intent(RestoreDefaultDatabase.RESTORE_DEFAULT);
		// intent.putExtra("Type", "startup");
		// startActivity(intent);
		// // thread for displaying the SplashScreen
		DbHelper myb = new DbHelper(SplashScreen.this);
		SQLiteDatabase db = myb.getWritableDatabase();
		Thread splashThread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;
					while (_active && (waited < _splashTime)) {
						sleep(100);
						if (_active) {
							waited += 100;
						}
					}
				} catch (InterruptedException e) {
					// do nothing
				} finally {

					finish();
					startActivity(new Intent(SplashScreen.this, AdminPage.class));

				}
			}
		};
		splashThread.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
