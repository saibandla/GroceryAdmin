package net.schuemie.GroceryList;

import org.xmlpull.v1.XmlPullParser;

import net.schuemie.GroceryList.GroceryListContentProvider.Categories;
import net.schuemie.GroceryList.GroceryListContentProvider.Items;
import android.app.Activity;
import android.content.ContentValues;
import android.content.res.XmlResourceParser;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;

public class RestoreDefaultDatabase extends Activity {

	public static final String RESTORE_DEFAULT = "net.schuemie.GroceryList.action.RESTORE_DEFAULT";
	private static final String TAG = "GroveryListRestoreDefault";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.restoredefault);
		new Thread() {
			@Override
			public void run() {
				getContentResolver().delete(
						GroceryListContentProvider.ITEMS_URI, null, null);
				getContentResolver().delete(
						GroceryListContentProvider.CATEGORIES_URI, null, null);

				XmlResourceParser xmlParser = getResources().getXml(
						R.xml.defaultshoppinglist);
				int eventType;
				long categoryID = -1;
				try {
					eventType = xmlParser.getEventType();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						if (eventType == XmlPullParser.START_TAG) {
							String tagName = xmlParser.getName();
							if (tagName.equals("category")) {

								for (int i = 0; i < xmlParser
										.getAttributeCount(); i++) {
									if (xmlParser.getAttributeName(i).equals(
											"name")) {
										String categoryName = xmlParser
												.getAttributeValue(i);
										ContentValues values = new ContentValues();
										values.put(Categories.NAME,
												categoryName);
										getContentResolver()
												.insert(GroceryListContentProvider.CATEGORIES_URI,
														values);
										// Retrieve the auto-generated ID:
										Cursor cursor = getContentResolver()
												.query(GroceryListContentProvider.CATEGORIES_URI,
														null,
														Categories.NAME
																+ " = '"
																+ categoryName
																+ "'", null,
														null);
										cursor.moveToFirst();
										categoryID = cursor
												.getLong(cursor
														.getColumnIndex(BaseColumns._ID));
										break;
									}
								}
							} else if (tagName.equals("item")) {
								for (int i = 0; i < xmlParser
										.getAttributeCount(); i++) {
									if (xmlParser.getAttributeName(i).equals(
											"name")) {
										String itemName = xmlParser
												.getAttributeValue(i);
										ContentValues values = new ContentValues();
										values.put(Items.NAME, itemName);
										values.put(Items.CATEGORY, categoryID);
										getContentResolver()
												.insert(GroceryListContentProvider.ITEMS_URI,
														values);
										break;
									}
								}
							}
						}
						eventType = xmlParser.next();
					}
				} catch (Exception e) {
					// Log.w(TAG, "Error parsing default shopping list XML.");
				} finally {
					finish();
				}
			}
		}.start();
	}
}
