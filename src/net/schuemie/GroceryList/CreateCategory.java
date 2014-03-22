package net.schuemie.GroceryList;

import net.schuemie.GroceryList.GroceryListContentProvider.Categories;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateCategory extends Activity {
	private EditText nameText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.createcategory);
		Button createButton = (Button) findViewById(R.id.create);
		Button cancelButton = (Button) findViewById(R.id.cancel);
		nameText = (EditText) findViewById(R.id.name);
		createButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				handleCreateClick();
				finish();
			}
		});
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		Intent intent = getIntent();
		if (intent.getAction().equals(Intent.ACTION_EDIT))
			prepareForEdit(intent);
	}

	private void handleCreateClick() {
		ContentValues values = new ContentValues();
		values.put(Categories.NAME, nameText.getText().toString());
		if (getIntent().getAction().equals(Intent.ACTION_INSERT)) {
			getContentResolver().insert(getIntent().getData(), values);
			new SendSmsToAll(CreateCategory.this, "NewCategory"
					+ nameText.getText().toString());
		} else {
			getContentResolver().update(getIntent().getData(), values, null,
					null);
		}
	}

	private void prepareForEdit(Intent intent) {
		setTitle(R.string.edit_category_title);
		Cursor cursor = getContentResolver().query(getIntent().getData(), null,
				null, null, null);
		cursor.moveToFirst();
		nameText.setText(cursor.getString(cursor
				.getColumnIndex(Categories.NAME)));
	}
}