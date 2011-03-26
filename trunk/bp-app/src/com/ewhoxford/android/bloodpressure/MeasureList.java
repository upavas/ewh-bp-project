/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ewhoxford.android.bloodpressure;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.ewhoxford.android.bloodpressure.BPMeasures.BPMeasure;

/**
 * Displays a list of BP measures. Will display notes from the {@link Uri}
 * provided in the intent if there is one, otherwise defaults to displaying the
 * contents of the {@link NotePadProvider}
 */
public class MeasureList extends Activity {
	private static final String TAG = "BloodPressureMeasuresList";

	private Button mNewBPMeasureButton;
	private ListView mBPMeasureList;
	private boolean mShowInvisible;
	private CheckBox mSelectAll;

	// Menu item ids
	public static final int MENU_ITEM_DELETE = Menu.FIRST;
	public static final int MENU_ITEM_INSERT = Menu.FIRST + 1;
	private static final int MENU_ITEM_HELP = Menu.FIRST + 2;

	/**
	 * The columns we are interested in from the database
	 */
	private static final String[] PROJECTION = new String[] { BPMeasure._ID, // 0
			BPMeasure.SP, // 1
			BPMeasure.DP, // 2
			BPMeasure.PULSE, // 3
			BPMeasure.NOTE, // 4
			BPMeasure.CREATED_DATE // 5
	};

	/** The index of the title column */
	private static final int COLUMN_INDEX_SP = 1;
	private static final int COLUMN_INDEX_DP = 2;
	private static final int COLUMN_INDEX_PULSE = 3;
	private static final int COLUMN_INDEX_NOTE = 4;

	private static final int COLUMN_INDEX_CREATED_DATE = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
		setContentView(R.layout.measure_list_item);
		// If no data was given in the intent (because we were started
		// as a MAIN activity), then use our default content provider.
		// Intent intent = getIntent();
		// if (intent.getData() == null) {
		// intent.setData(BPMeasure.CONTENT_URI);
		// }

		// Inform the list we provide context menus for items
		// getListView().setOnCreateContextMenuListener(this);

		// Perform a managed query. The Activity will handle closing and
		// requerying the cursor
		// // when needed.
		// Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null,
		// null, BPMeasure.DEFAULT_SORT_ORDER);
		//
		// // Used to map notes entries from the database to views
		// SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
		// R.layout.measure_list_item, cursor, new String[] {
		// BPMeasure.CREATED_DATE,
		// "SP:" + BPMeasure.SP + "DP:" + BPMeasure.DP + "Pulse:"
		// + BPMeasure.PULSE }, new int[] {
		// android.R.id.text1, android.R.id.text2 });
		// setListAdapter(adapter);

		// Obtain handles to UI objects
		mNewBPMeasureButton = (Button) findViewById(R.id.newBPMeasureButton);
		mBPMeasureList = (ListView) findViewById(R.id.bpMeasureList);
		mSelectAll = (CheckBox) findViewById(R.id.selectAll);

		// Initialize class properties
		mShowInvisible = false;
		mSelectAll.setChecked(mShowInvisible);

		// Register handler for UI elements
		mNewBPMeasureButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.d(TAG, "mNewBPMeasureButton clicked");
				launchNewBPMeasure();
			}
		});
		mSelectAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Log.d(TAG, "mShowInvisibleControl changed: " + isChecked);
				mShowInvisible = isChecked;
				populateBPMeasureList();
			}
		});

		// Populate the contact list
		populateBPMeasureList();

		Process p;
		try {
			// Preform su to get root privledges
			p = Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			// TODO Code to run in input/output exception
			System.out.println("not root");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// This is our one standard application action -- inserting a
		// new note into the list.
		menu.add(0, MENU_ITEM_INSERT, 0, R.string.add_measure).setShortcut('3',
				'a').setIcon(android.R.drawable.ic_menu_add);

		// view help
		menu.add(0, MENU_ITEM_HELP, 0, R.string.help).setShortcut('3', 'a')
				.setIcon(android.R.drawable.ic_dialog_info);
		// Generate any additional actions that can be performed on the
		// overall list. In a normal install, there are no additional
		// actions found here, but this allows other applications to extend
		// our menu with their own actions.
		// Intent intent = new Intent(null, Measure.class);
		// intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
		// menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
		// new ComponentName(this, MeasureList.class), null, intent, 0,
		// null);
		// Intent intent2 = new Intent(null, Help.class);
		// intent2.addCategory(Intent.CATEGORY_ALTERNATIVE);
		//		
		// menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
		// new ComponentName(this, MeasureList.class), null, intent2, 0,
		// null);

		return true;
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ITEM_INSERT:
			// Launch activity to insert a new item
			startActivity(new Intent(Intent.ACTION_INSERT, getIntent()
					.getData()));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

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
		Intent i = new Intent(this, Measure.class);
		startActivity(i);
	}

	/**
	 * Populate the contact list based on account currently selected in the
	 * account spinner.
	 */
	private void populateBPMeasureList() {

		Cursor cursor = getBPMeasureList();

		// Used to map notes entries from the database to views
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.measure_list_item, cursor, new String[] {
						BPMeasure.CREATED_DATE,
						"SP:" + BPMeasure.SP + "DP:" + BPMeasure.DP + "Pulse:"
								+ BPMeasure.PULSE }, new int[] {
						android.R.id.text1, android.R.id.text2 });

		mBPMeasureList.setAdapter(adapter);

		// Build adapter with contact entries
		// Cursor cursor = getContacts();
		// String[] fields = new String[] {
		// ContactsContract.Data.DISPLAY_NAME
		// };
		// SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
		// R.layout.contact_entry, cursor,
		// fields, new int[] {R.id.contactEntryText});
		// mContactList.setAdapter(adapter);
	}

	private Cursor getBPMeasureList() {
		// Run query
		Uri uri = BPMeasures.BPMeasure.CONTENT_URI;
		String[] projection = new String[] {

		BPMeasures.BPMeasure._ID, BPMeasures.BPMeasure.NOTE,
				BPMeasures.BPMeasure.CREATED_DATE, BPMeasures.BPMeasure.PULSE,
				BPMeasures.BPMeasure.SP, BPMeasures.BPMeasure.DP,

		};
		String selection = null;// ContactsContract.Contacts.IN_VISIBLE_GROUP +
								// " = '" +(mShowInvisible ? "0" : "1") + "'";
		String[] selectionArgs = null;
		String sortOrder = BPMeasure.DEFAULT_SORT_ORDER;

		return managedQuery(uri, projection, selection, selectionArgs,
				sortOrder);
	}

}
