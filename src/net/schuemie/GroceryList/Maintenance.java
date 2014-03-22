package net.schuemie.GroceryList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class Maintenance extends Activity {
	public static final String MAINTENANCE = "net.schuemie.GroceryList.action.MAINTENANCE";
	private TextView numberOfItems;
	private TextView numberOfCategories;
	private CheckBox checkBox;
	private Cursor itemsCursor;
	private Cursor categoriesCursor;
	private ContentObserver categoriesContentObserver;
	private ContentObserver itemsContentObserver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maintenance);

		Button deleteAllButton = (Button) findViewById(R.id.delete_all);
		Button restoreDefaultButton = (Button) findViewById(R.id.restore_default);
		Button editCategoriesButton = (Button) findViewById(R.id.edit_categories);
		numberOfItems = (TextView) findViewById(R.id.number_items);
		numberOfCategories = (TextView) findViewById(R.id.number_categories);
		checkBox = (CheckBox) findViewById(R.id.performcheck);
		checkBox.setChecked(getPreference());

		deleteAllButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				confirmDeleteAll();
			}
		});

		restoreDefaultButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				confirmRestoreDefault();
			}
		});

		editCategoriesButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				setResult(RESULT_OK);
				handleEditCategoriesClick();
			}
		});

		checkBox.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				changePreference();
			}

		});

		itemsCursor = managedQuery(GroceryListContentProvider.ITEMS_URI, null,
				null, null, null);
		categoriesCursor = managedQuery(
				GroceryListContentProvider.CATEGORIES_URI, null, null, null,
				null);
		categoriesContentObserver = new CategoryContentObserver(new Handler());
		itemsContentObserver = new ItemsContentObserver(new Handler());
		getContentResolver().registerContentObserver(
				GroceryListContentProvider.ITEMS_URI, true,
				itemsContentObserver);
		getContentResolver().registerContentObserver(
				GroceryListContentProvider.CATEGORIES_URI, true,
				categoriesContentObserver);
		numberOfCategories
				.setText(Integer.toString(categoriesCursor.getCount()));
		numberOfItems.setText(Integer.toString(itemsCursor.getCount()));
	}

	public void handleEditCategoriesClick() {
		Intent intent = new Intent(Intent.ACTION_EDIT,
				GroceryListContentProvider.CATEGORIES_URI);
		startActivity(intent);
	}

	private void confirmDeleteAll() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.confirm_delete_all_text)
				.setPositiveButton(R.string.ok_label,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								getContentResolver().delete(
										GroceryListContentProvider.ITEMS_URI,
										null, null);
								getContentResolver()
										.delete(GroceryListContentProvider.CATEGORIES_URI,
												null, null);
							}
						})
				.setNegativeButton(R.string.cancel_label,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).create().show();
	}

	private void confirmRestoreDefault() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.confirm_restore_default_text)
				.setPositiveButton(R.string.ok_label,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Intent intent = new Intent(
										RestoreDefaultDatabase.RESTORE_DEFAULT);
								startActivity(intent);
							}
						})
				.setNegativeButton(R.string.cancel_label,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).create().show();
	}

	@Override
	protected void onDestroy() {
		getContentResolver().unregisterContentObserver(itemsContentObserver);
		getContentResolver().unregisterContentObserver(
				categoriesContentObserver);
		super.onDestroy();
	}

	private class ItemsContentObserver extends ContentObserver {
		public ItemsContentObserver(Handler h) {
			super(h);
		}

		@Override
		public void onChange(boolean selfChange) {
			if (!itemsCursor.isClosed()) {
				itemsCursor.requery();
				numberOfItems.setText(Integer.toString(itemsCursor.getCount()));

			}
		}
	}

	private class CategoryContentObserver extends ContentObserver {
		public CategoryContentObserver(Handler h) {
			super(h);
		}

		@Override
		public void onChange(boolean selfChange) {
			if (!categoriesCursor.isClosed()) {
				categoriesCursor.requery();
				numberOfCategories.setText(Integer.toString(categoriesCursor
						.getCount()));
			}
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
}
