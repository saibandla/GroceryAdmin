package net.schuemie.GroceryList;

import net.schuemie.GroceryList.GroceryListContentProvider.Categories;
import net.schuemie.GroceryList.GroceryListContentProvider.Items;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;

public class EditCategories extends ListActivity {
	private static final int ITEM_DELETE = 0;
	private static final int ITEM_EDIT = 1;
	public static final String ACTION_CREATE = "create";
	public static final String ACTION_EDIT = "edit";
	private long deleteID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.categoryedit);

		// Tell the list view which view to display when the list is empty
		getListView().setEmptyView(findViewById(R.id.empty));

		// Wire up the create button
		Button add = (Button) findViewById(R.id.create);
		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleCreateClick();
			}
		});
		// Inform the list we provide context menus for items
		getListView().setOnCreateContextMenuListener(this);

		fillList();
	}

	private void handleCreateClick() {
		Intent intent = new Intent(Intent.ACTION_INSERT,
				GroceryListContentProvider.CATEGORIES_URI);
		startActivity(intent);
	}

	private void fillList() {
		Cursor cursor = managedQuery(getIntent().getData(), null, null, null,
				null);
		String[] from = new String[] { Categories.NAME };
		int[] to = new int[] { R.id.name };
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.category, cursor, from, to);
		setListAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		} catch (ClassCastException e) {
			// Log.e(TAG, "bad menuInfo", e);
			return;
		}

		Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
		if (cursor == null) {
			// For some reason the requested item isn't available, do nothing
			return;
		}

		// Setup the menu header
		menu.setHeaderTitle(cursor.getString(cursor.getColumnIndex(Items.NAME)));

		// Add a menu item to delete the note
		menu.add(0, ITEM_DELETE, 0, R.string.delete_category_item);
		menu.add(0, ITEM_EDIT, 0, R.string.edit_category_item);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info;
		try {
			info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			// Log.e(TAG, "bad menuInfo", e);
			return false;
		}

		switch (item.getItemId()) {
		case ITEM_DELETE:
			deleteID = info.id;
			confirmDeleteCategory();
			return true;
		case ITEM_EDIT:
			Uri categoryUri = ContentUris.withAppendedId(
					GroceryListContentProvider.CATEGORIES_URI, info.id);
			Intent intent = new Intent(Intent.ACTION_EDIT, categoryUri);
			startActivity(intent);
			return true;
		}
		return false;
	}

	private void confirmDeleteCategory() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.confirm_category_delete_text)
				.setPositiveButton(R.string.ok_label,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
								Uri categoryUri = ContentUris
										.withAppendedId(
												GroceryListContentProvider.CATEGORIES_URI,
												deleteID);
								getContentResolver().delete(categoryUri, null,
										null);
							}
						})
				.setNegativeButton(R.string.cancel_label,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						})

				.create().show();

	}

}