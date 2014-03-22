package net.schuemie.GroceryList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.schuemie.GroceryList.GroceryListContentProvider.Categories;
import net.schuemie.GroceryList.GroceryListContentProvider.Items;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

public class GroceryList extends ListActivity {
	private static final int CONTEXTMENU_ITEM_DELETE = 0;
	private static final int CONTEXTMENU_ITEM_EDIT = 1;
	private static final int MENU_ITEM_EDIT_MODE = 0;
	private static final int MENU_ITEM_SHOPPING_MODE = 1;
	private static final int MENU_ITEM_MAINTENANCE = 2;
	private static final int MENU_ITEM_ABOUT = 3;
	private static final int EDIT_MODE = 0;
	private static final int REQUEST_CHECK_OFF = 0;
	private static final String MODE = "mode";
	private static final String CATEGORY = "category";
	private static final int SHOPPING_MODE = 1;
	private static final long ALL_CATEGORIES = -1;
	private static final String PREFERENCE_STARTED = "PREFERENCE_STARTED";
	private Cursor itemsCursor;
	private Cursor categoriesCursor;
	private EditText searchText;
	private int mode;
	private Spinner spinner;
	private long categoryID = ALL_CATEGORIES;
	private List<Long> categoryIDs = new ArrayList<Long>();

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		// Tell the list view which view to display when the list is empty
		getListView().setEmptyView(findViewById(R.id.empty));

		// Wire up the clear button
		Button clear = (Button) findViewById(R.id.clear);
		clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchText.setText("");
				updateList();
			}
		});
		clear.setBackgroundResource(android.R.drawable.ic_menu_revert);

		// dont show send button

		View smsbtn = findViewById(R.id.smslay);
		smsbtn.setVisibility(View.GONE);

		// Wire up the create button
		Button add = (Button) findViewById(R.id.create);
		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				handleCreateClick();
			}
		});
		add.setBackgroundResource(android.R.drawable.ic_menu_add);

		// Wire up the search box
		searchText = (EditText) findViewById(R.id.search);
		searchText.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				updateList();
				return false;
			}
		});

		// Wire up the categories box
		spinner = (Spinner) findViewById(R.id.category);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
					long id) {
				long previousID = categoryID;
				categoryID = categoryIDs.get(pos);
				if (previousID != categoryID)
					updateList();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// Do nothing
			}
		});

		// Inform the list we provide context menus for items
		getListView().setOnCreateContextMenuListener(this);

		categoriesCursor = managedQuery(
				GroceryListContentProvider.CATEGORIES_URI, null, null, null,
				null);

		getContentResolver().registerContentObserver(
				GroceryListContentProvider.CATEGORIES_URI, true,
				new CategoryContentObserver(new Handler()));

		updateCategories(savedInstanceState == null ? ALL_CATEGORIES
				: savedInstanceState.getLong(CATEGORY, ALL_CATEGORIES));

		// Set mode
		setMode(savedInstanceState == null ? EDIT_MODE : savedInstanceState
				.getInt(MODE, EDIT_MODE));

		SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
		if (!preferences.getBoolean(PREFERENCE_STARTED, false)) {
			Intent intent = new Intent(RestoreDefaultDatabase.RESTORE_DEFAULT);
			startActivity(intent);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putBoolean(PREFERENCE_STARTED, true);
			editor.commit();
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
				updateCategories(categoryID);
			}
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN)
			if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
				int pos = spinner.getSelectedItemPosition();
				if (pos > 0 && pos != AdapterView.INVALID_POSITION) {
					pos--;
					spinner.setSelection(pos, true);
				}
				getListView().requestFocus();
				return true;
			} else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
				int pos = spinner.getSelectedItemPosition();
				if (pos < spinner.getCount() - 1
						&& pos != AdapterView.INVALID_POSITION) {
					pos++;
					spinner.setSelection(pos, true);
				}
				getListView().requestFocus();
				return true;
			}

		return super.dispatchKeyEvent(event);
	}

	private void updateCategories(long setID) {
		Set<Long> validIDs = null;
		if (mode == SHOPPING_MODE)
			validIDs = fetchValidIDs();

		categoryIDs.clear();
		List<String> categories = new ArrayList<String>();
		int nameColumn = categoriesCursor.getColumnIndex(Categories.NAME);
		int idColumn = categoriesCursor.getColumnIndex(BaseColumns._ID);
		categoriesCursor.moveToFirst();
		if (categoriesCursor.isFirst()) {
			do {
				long id = categoriesCursor.getLong(idColumn);
				if (validIDs == null || validIDs.contains(id)) {
					String category = categoriesCursor.getString(nameColumn);
					categoryIDs.add(id);
					categories.add(category);
				}
			} while (categoriesCursor.moveToNext());
		}
		categories.add(getResources().getString(R.string.all_categories_label));
		categoryIDs.add(ALL_CATEGORIES);
		Spinner spinner = (Spinner) findViewById(R.id.category);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, categories);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);

		int newID = categoryIDs.indexOf(setID);
		if (newID == -1)
			newID = categories.size() - 1;
		spinner.setSelection(newID);
	}

	/**
	 * Generates the set of category IDs of items that are on the list
	 * 
	 * @return The set of category IDs
	 */
	private Set<Long> fetchValidIDs() {
		Set<Long> result = new HashSet<Long>();
		Cursor cursor = getContentResolver().query(
				GroceryListContentProvider.ITEMS_URI, null,
				Items.ONLIST + " = 1", null, null);
		int categoryColumn = cursor.getColumnIndex(Items.CATEGORY);
		cursor.moveToFirst();
		if (cursor.isFirst()) {
			do {
				result.add(cursor.getLong(categoryColumn));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return result;
	}

	private void setMode(int mode) {
		this.mode = mode;
		View searchPanel = findViewById(R.id.searchpanel);

		View smsbtn = findViewById(R.id.smslay);

		switch (mode) {
		case EDIT_MODE:
			setTitle(getString(R.string.app_name) + " - "
					+ getString(R.string.edit_mode_label));
			searchPanel.setVisibility(View.VISIBLE);
			break;
		case SHOPPING_MODE:
			setTitle(getString(R.string.app_name) + " - "
					+ getString(R.string.shopping_mode_label));
			searchPanel.setVisibility(View.GONE);
			smsbtn.setVisibility(View.VISIBLE);
			Log.d("GroceryList", "shopingmodeeeeeeeeeeee");

			Button smsbutton = (Button) findViewById(R.id.sendbtn);

			smsbutton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Log.i("GrocryList", "button clicked");
					Intent intent = new Intent(GroceryList.this, SMS.class);
					startActivity(intent);

				}
			});

			break;
		}
		updateList();
		updateCategories(categoryID);
	}

	private void handleCreateClick() {
		Intent intent = new Intent(Intent.ACTION_INSERT,
				GroceryListContentProvider.ITEMS_URI);
		intent.putExtra("name", searchText.getText().toString());
		if (categoryID != ALL_CATEGORIES)
			intent.putExtra("category", categoryID);
		startActivity(intent);

	}

	private void updateList() {
		String text = searchText.getText().toString();
		String selection;
		if (text.equals(""))
			selection = null;
		else
			selection = GroceryListContentProvider.ITEMS_TABLE_NAME + "."
					+ Items.NAME + " LIKE '%" + text + "%'";

		if (mode == SHOPPING_MODE) {
			if (selection == null)
				selection = "";
			else
				selection += " AND ";
			selection += Items.ONLIST + " = 1";
		}

		if (categoryID != ALL_CATEGORIES) {
			if (selection == null || selection.equals(""))
				selection = "";
			else
				selection += " AND ";
			selection += Items.CATEGORY + " = " + categoryID;
		}

		if (itemsCursor != null) {
			stopManagingCursor(itemsCursor);
			itemsCursor.close();
		}
		Cursor cursor = managedQuery(GroceryListContentProvider.ITEMS_URI,
				null, selection, null, null);
		System.err.println("callingggggggggggggggggggggggggggggg update list");
		CursorAdapter adapter = new GroceryListCursorAdapter(this, cursor);
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
		menu.add(0, CONTEXTMENU_ITEM_DELETE, 0, R.string.delete_item_item);
		menu.add(0, CONTEXTMENU_ITEM_EDIT, 0, R.string.edit_item_item);
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
		case CONTEXTMENU_ITEM_DELETE: {
			Uri itemUri = ContentUris.withAppendedId(
					GroceryListContentProvider.ITEMS_URI, info.id);
			getContentResolver().delete(itemUri, null, null);
			return true;
		}
		case CONTEXTMENU_ITEM_EDIT: {
			Uri itemUri = ContentUris.withAppendedId(
					GroceryListContentProvider.ITEMS_URI, info.id);
			Intent intent = new Intent(Intent.ACTION_EDIT, itemUri);
			startActivity(intent);
			return true;
		}
		}
		return false;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Uri itemUri = ContentUris.withAppendedId(
				GroceryListContentProvider.ITEMS_URI, id);
		Intent intent;
		switch (mode) {
		case EDIT_MODE:
			intent = new Intent(AddToList.MODIFY_ITEM_STATUS, itemUri);
			startActivity(intent);
			break;
		case SHOPPING_MODE:
			intent = new Intent(ConfirmCheck.CHECK_ITEM_OFF, itemUri);
			startActivityForResult(intent, REQUEST_CHECK_OFF);

			/*
			 * ContentValues values = new ContentValues();
			 * values.put(Items.ONLIST, 0); getContentResolver().update(itemUri,
			 * values, null, null); updateCategories(categoryID);
			 */
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == REQUEST_CHECK_OFF && resultCode == RESULT_OK) {
			updateCategories(categoryID);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_ITEM_EDIT_MODE, 0, R.string.edit_mode_label).setIcon(
				android.R.drawable.ic_menu_edit);
		menu.add(0, MENU_ITEM_SHOPPING_MODE, 1, R.string.shopping_mode_label)
				.setIcon(android.R.drawable.ic_menu_directions);
		menu.add(1, MENU_ITEM_MAINTENANCE, 2, R.string.maintenance_label)
				.setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(1, MENU_ITEM_ABOUT, 3, R.string.about_label).setIcon(
				android.R.drawable.ic_menu_info_details);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_EDIT_MODE:
			setMode(EDIT_MODE);
			break;
		case MENU_ITEM_SHOPPING_MODE:
			setMode(SHOPPING_MODE);
			break;
		case MENU_ITEM_MAINTENANCE:
			Intent intent = new Intent(Maintenance.MAINTENANCE);
			startActivity(intent);
			break;
		case MENU_ITEM_ABOUT:
			showAboutBox();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showAboutBox() {
		new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setTitle(
						getResources().getString(R.string.app_name) + " "
								+ getResources().getString(R.string.version))
				.setMessage("Developed By Wincent Technologies India Pvt ltd.")
				.setPositiveButton(R.string.ok_label,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).create().show();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(MODE, mode);
		outState.putLong(CATEGORY, categoryID);
	}
}