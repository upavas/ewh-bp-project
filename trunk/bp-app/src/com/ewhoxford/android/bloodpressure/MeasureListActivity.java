/*
 */

package com.ewhoxford.android.bloodpressure;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.ewhoxford.android.bloodpressure.database.BloodPressureMeasureTable.BPMeasure;

/**
 * Displays a list of BP measures. Will display notes from the {@link Uri}
 * provided in the intent if there is one, otherwise defaults to displaying the
 * contents of the {@link NotePadProvider}
 * 
 * @author mauro
 */
public class MeasureListActivity extends ListActivity implements
		SimpleCursorAdapter.ViewBinder {
	private static final String TAG = "BloodPressureMeasuresList";

	// Menu item ids
	public static final int MENU_ITEM_DELETE = Menu.FIRST;
	public static final int MENU_ITEM_INSERT = Menu.FIRST + 1;
	private static final int MENU_ITEM_HELP = Menu.FIRST + 2;
	Context measureListContext = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// The user does not need to hold down the key to use menu shortcuts.
		setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
		setContentView(R.layout.measure_list_item);

		/*
		 * If no data is given in the Intent that started this Activity, then
		 * this Activity was started when the intent filter matched a MAIN
		 * action. We should use the default provider URI.
		 */
		// Gets the intent that started this Activity.
		Intent intent = getIntent();

		// If there is no data associated with the Intent, sets the data to the
		// default URI, which
		// accesses a list of notes.
		if (intent.getData() == null) {
			intent.setData(BPMeasure.CONTENT_URI);
		}

		Button newMeasure = (Button) findViewById(R.id.new_measure_button);
		newMeasure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(measureListContext, MeasureActivity.class);
				startActivity(i);
			}
		});

//		ListView headerListView = (ListView) findViewById(R.id.header1);
//		// Used to map notes entries from the database to views
//		String[] from = new String[] {};
//		int[] to = new int[] {};
//
//		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
//				R.layout.measure_list_header_row, null, from, to);
//		headerListView.setAdapter(adapter);

		// Populate the bp measures list
		populateBPMeasureList();

		/*
		 * Sets the callback for context menu activation for the ListView. The
		 * listener is set to be this Activity. The effect is that context menus
		 * are enabled for items in the ListView, and the context menu is
		 * handled by a method in MeasureList.
		 */
		// setOnCreateContextMenuListener(this);
		// ask for root permission since we need the mice raw values
		Process p;
		try {
			// Preform su to get root privledges
			p = Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			// TODO Code to run in input/output exception
			System.out.println("not root");
		}

	}



	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu) {
	// super.onPrepareOptionsMenu(menu);
	// final boolean haveItems = getListAdapter().getCount() > 0;
	//
	// // If there are any notes in the list (which implies that one of
	// // them is selected), then we need to generate the actions that
	// // can be performed on the current selection. This will be a combination
	// // of our own specific actions along with any extensions that can be
	// // found.
	// if (haveItems) {
	// // This is the selected item.
	// Uri uri = ContentUris.withAppendedId(getIntent().getData(),
	// getSelectedItemId());
	//
	// // Build menu... always starts with the EDIT action...
	// Intent[] specifics = new Intent[1];
	// specifics[0] = new Intent(Intent.ACTION_EDIT, uri);
	// MenuItem[] items = new MenuItem[1];
	//
	// // ... is followed by whatever other actions are available...
	// Intent intent = new Intent(null, uri);
	// intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
	// menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0, null,
	// specifics, intent, 0, items);
	//
	// // Give a shortcut to the edit action.
	// if (items[0] != null) {
	// items[0].setShortcut('1', 'e');
	// }
	// } else {
	// menu.removeGroup(Menu.CATEGORY_ALTERNATIVE);
	// }
	//
	// return true;
	// }


	// @Override
	// public void onCreateContextMenu(ContextMenu menu, View view,
	// ContextMenuInfo menuInfo) {
	// AdapterView.AdapterContextMenuInfo info;
	// try {
	// info = (AdapterView.AdapterContextMenuInfo) menuInfo;
	// } catch (ClassCastException e) {
	// Log.e(TAG, "bad menuInfo", e);
	// return;
	// }
	//
	// Cursor cursor = (Cursor) getListAdapter().getItem(info.position);
	// if (cursor == null) {
	// // For some reason the requested item isn't available, do nothing
	// return;
	// }
	//
	// // Setup the menu header
	// menu.setHeaderTitle(cursor.getString(COLUMN_INDEX_CREATED_DATE));
	//
	// // Add a menu item to delete the note
	// menu.add(0, MENU_ITEM_DELETE, 0, R.string.menu_delete);
	// }

	// @Override
	// public boolean onContextItemSelected(MenuItem item) {
	// AdapterView.AdapterContextMenuInfo info;
	// try {
	// info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
	// } catch (ClassCastException e) {
	// Log.e(TAG, "bad menuInfo", e);
	// return false;
	// }
	//
	// switch (item.getItemId()) {
	// case MENU_ITEM_DELETE: {
	// // Delete the note that the context menu is for
	// Uri noteUri = ContentUris.withAppendedId(getIntent().getData(),
	// info.id);
	// getContentResolver().delete(noteUri, null, null);
	// return true;
	// }
	// }
	// return false;
	// }

	// @Override
	// protected void onListItemClick(ListView l, View v, int position, long id)
	// {
	// Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
	//
	// String action = getIntent().getAction();
	// if (Intent.ACTION_PICK.equals(action)
	// || Intent.ACTION_GET_CONTENT.equals(action)) {
	// // The caller is waiting for us to return a note selected by
	// // the user. The have clicked on one, so return it now.
	// setResult(RESULT_OK, new Intent().setData(uri));
	// } else {
	// // Launch activity to view/edit the currently selected item
	// startActivity(new Intent(Intent.ACTION_EDIT, uri));
	// }
	// }

	protected void launchNewBPMeasure() {
		Intent i = new Intent(this, MeasureActivity.class);
		startActivity(i);
	}

	/**
	 * Populate the contact list based on account currently selected in the
	 * account spinner.
	 */
	private void populateBPMeasureList() {

		Cursor cursor = getBPMeasureList();

		// Used to map notes entries from the database to views
		String[] from = new String[] { BPMeasure._ID, BPMeasure.CREATED_DATE,
				BPMeasure.SP, BPMeasure.DP, BPMeasure.PULSE };
		int[] to = new int[] { R.id.id, R.id.createdDate, R.id.sp, R.id.dp,
				R.id.pulse };

		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.measure_list_row, cursor, from, to);

		adapter.setViewBinder(this);

		setListAdapter(adapter);

	}

	/**
	 * 
	 * @return blood pressure measurements
	 */

	private Cursor getBPMeasureList() {

		Uri uri = getIntent().getData();// Use the default content URI for the
		// //
		// provider.
		String[] projection = new String[] {

		BPMeasure._ID, BPMeasure.NOTE, BPMeasure.CREATED_DATE, BPMeasure.PULSE,
				BPMeasure.SP, BPMeasure.DP, BPMeasure.NOTE };// Return
		// the
		// measureId
		// ID,NOTE,Created_date,pulse,sp,dp

		String selection = null;// No where clause, return all records.
		String[] selectionArgs = null;// No where clause, therefore no where
		// column
		// values.
		String sortOrder = BPMeasure.DEFAULT_SORT_ORDER;// Use the default sort
		// order.
		// Run query
		return managedQuery(uri, projection, selection, selectionArgs,
				sortOrder);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);
		String action = getIntent().getAction();
		if (Intent.ACTION_PICK.equals(action)
				|| Intent.ACTION_GET_CONTENT.equals(action)) {
			// The caller is waiting for us to return a note selected by
			// the user. The have clicked on one, so return it now.
			setResult(RESULT_OK, new Intent().setData(uri));
			finish();
		} else {
			// Launch activity to view/edit the currently selected item
			startActivity(new Intent(Intent.ACTION_EDIT, uri));
		}

	}

	@Override
	public boolean setViewValue(View v, Cursor cur, int columnIndex) {
		try {

			if (v instanceof TextView) {
				((TextView) v).setText(cur.getString(columnIndex));
				switch (columnIndex) {
				case 2:
					// Log.i(TAG,
					// "Setting procedure name in SavedProcedureList text");
					long dateStr = new Long(cur.getString(columnIndex));
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
					Date resultdate = new Date(dateStr);
			
					// TODO correct this space problem between columns
					((TextView) v).setText("   " + sdf.format(resultdate)
							+ "   ");
					break;
				case 4:
					// Log.i(TAG,
					// "Setting patient id and name in SavedProcedureList text");
					float aux = Float.parseFloat(cur.getString(columnIndex));
					int sp = Math.round(aux);
					((TextView) v).setText(Integer.toString(sp));
					break;
				case 5:
					float aux1 = Float.parseFloat(cur.getString(columnIndex));
					int dp = Math.round(aux1);
					((TextView) v).setText(Integer.toString(dp));
					break;
				case 3:
					float aux11 = Float.parseFloat(cur.getString(columnIndex));
					int pulse = Math.round(aux11);
					((TextView) v).setText(Integer.toString(pulse));
					break;

				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Exception in setting the text in the list: "
					+ e.toString());
			return false;
		}
		return true;
	}

}
