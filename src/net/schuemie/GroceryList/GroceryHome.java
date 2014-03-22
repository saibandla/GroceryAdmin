package net.schuemie.GroceryList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class GroceryHome extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.grocery_home);

		Button StartShopping = (Button) findViewById(R.id.button1);
		ImageButton Login = (ImageButton) findViewById(R.id.buttonLogin);

		StartShopping.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(GroceryHome.this, ViewSMS.class);
				startActivity(intent);
			}
		});

		Login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("MainActivity", "login button clicked");
				Intent intent = new Intent(GroceryHome.this, SplashScreen.class);
				startActivity(intent);

			}
		});

	}
}
