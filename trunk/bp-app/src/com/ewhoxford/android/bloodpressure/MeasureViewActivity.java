// Name of the package
package com.ewhoxford.android.bloodpressure;

//Import resources

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.SimpleXYSeries.ArrayFormat;
import com.ewhoxford.android.bloodpressure.database.BloodPressureMeasureTable.BPMeasure;
import com.ewhoxford.android.bloodpressure.utils.FileManager;
import com.ewhoxford.android.bloodpressure.utils.ReadCSV;

/**
 * A generic activity for editing a bp measure in a database. This can be used
 * either to simply view a note {@link InteFnt#ACTION_VIEW}, view and edit a
 * note {@link Intent#ACTION_EDIT}
 * 
 * @author mauro
 */
public class MeasureViewActivity extends Activity {
	private static final String TAG = "BloodPressureMeasure";

	/**
	 * Standard projection for the interesting columns of a normal note.
	 */
	private static final String[] PROJECTION = new String[] { BPMeasure._ID, // 0
			BPMeasure.CREATED_DATE,// 1
			BPMeasure.SP, // 2
			BPMeasure.DP, // 3
			BPMeasure.PULSE, // 4
			BPMeasure.NOTE, // 5
	};
	/** The index of the note column */
	private static final int COLUMN_INDEX_NOTE = 5;

	// This is our state data that is stored when freezing.
	private static final String ORIGINAL_CONTENT = "origContent";

	// Identifiers for our menu items.
	private static final int REVERT_ID = Menu.FIRST;
	private static final int DISCARD_ID = Menu.FIRST + 1;
	private static final int DELETE_ID = Menu.FIRST + 2;

	// The different distinct states the activity can be run in.
	private static final int STATE_EDIT = 0;
	private static final int STATE_INSERT = 1;

	MeasureViewActivity measureContext = this;
	// save file option is false
	boolean saveFile = false;
	// plot that shows real time data
	private XYPlot bpMeasureXYPlot;
	// save measure button
	private Button saveButton;
	// delete measure button
	private Button deleteButton;
	// signal processing progress dialog
	ProgressDialog myProgressDialog;
	// discard measure alert dialog
	AlertDialog.Builder builder;
	// discard measure alert dialog
	AlertDialog.Builder saveAlert;
	// Checkbox to save measure csv file
	CheckBox checkBox;
	// user notes

	private int mState;
	private boolean mNoteOnly = false;
	private Uri mUri;
	private Cursor mCursor;
	private EditText mText;
	private String mOriginalContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = getIntent();

		// Do some setup based on the action being performed.

		final String action = intent.getAction();
		if (Intent.ACTION_EDIT.equals(action)) {
			// Requested to edit: set that state, and the data being edited.
			mState = STATE_EDIT;
			mUri = intent.getData();
		} else {
			// Whoops, unknown action! Bail.
			Log.e(TAG, "Unknown action, exiting");
			finish();
			return;
		}

		// Set the layout for this activity. You can find it in
		// res/layout/note_editor.xml
		setContentView(R.layout.measure_view);

		// The text view for our note, identified by its ID in the XML file.
		mText = (EditText) findViewById(R.id.edittext);

		// initialize checkbox variable
		checkBox = (CheckBox) findViewById(R.id.checkbox);

		// Help button
		Button deleteButton = (Button) findViewById(R.id.button_delete);
		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				deleteMeasure();

			}
		});

		Button discardButton = (Button) findViewById(R.id.button_discard);

		discardButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				AlertDialog alert = builder.create();
				alert.show();
			}
		});

		builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to discard changes?")
				.setCancelable(false).setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								cancelUpdate();
							}
						}).setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		saveButton = (Button) findViewById(R.id.button_save);
		saveButton.setEnabled(false);

		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				AlertDialog alert = saveAlert.create();
				alert.show();
			}

		});
		saveAlert = new AlertDialog.Builder(this);
		saveAlert.setMessage("save changes?");
		saveAlert.setCancelable(false).setPositiveButton("Yes",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						Long time = System.currentTimeMillis();
						update(mText.getText().toString(), time);
						Intent i = new Intent(measureContext,
								MeasureListActivity.class);
						startActivity(i);

					}
				}).setNegativeButton("No",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		// #### End of Set up click listeners for all the buttons

		// Get the measure!
		mCursor = managedQuery(mUri, PROJECTION, null, null, null);

		if (mCursor.moveToFirst()) {

			String notes;
			long createdDate;
			int sp;
			int dp;
			int pulse;
			int id;
			float aux;
			int notesColumn = mCursor.getColumnIndex(BPMeasure.NOTE);
			int createdDateColumn = mCursor
					.getColumnIndex(BPMeasure.CREATED_DATE);
			int spColumn = mCursor.getColumnIndex(BPMeasure.SP);
			int dpColumn = mCursor.getColumnIndex(BPMeasure.DP);
			int pulseColumn = mCursor.getColumnIndex(BPMeasure.PULSE);
			int idColumn = mCursor.getColumnIndex(BPMeasure._ID);
			int fileExistsColumn = mCursor
					.getColumnIndex(BPMeasure.MEASUREMENT_FILE_EXIST);
			int fileNameColumn = mCursor
					.getColumnIndex(BPMeasure.MEASUREMENT_FILE);
			// Get the field values
			notes = mCursor.getString(notesColumn);
			aux = Float.parseFloat(mCursor.getString(spColumn));
			sp = Math.round(aux);
			aux = Float.parseFloat(mCursor.getString(dpColumn));
			dp = Math.round(aux);
			aux = Float.parseFloat(mCursor.getString(pulseColumn));
			pulse = Math.round(aux);

			// Display notes and blood pressure algorithm result in the
			// Measure layout
			mText.setText(notes);
			ValuesView valuesView = (ValuesView) findViewById(R.id.results);
			valuesView.requestFocus();
			valuesView.setSPressure(sp);
			valuesView.setDPressure(dp);
			valuesView.setPulseRate(pulse);
			valuesView.invalidate();

			boolean fileExists = Boolean.parseBoolean(mCursor
					.getString(fileExistsColumn));
			if (fileExists) {

				String fileName = mCursor.getString(fileNameColumn);
				if (fileName != null) {

					// initialize our XYPlot reference and real time update
					// code:

					// getInstance and position datasets:
					float[][] bloodPressureArray = ReadCSV.readCSV(
							FileManager.DIRECTORY, fileName);

					int l = bloodPressureArray.length;

					List<Number> pressureValues = new ArrayList<Number>();
					int i = 0;
					while (i < l) {
						pressureValues.add(bloodPressureArray[i][1]);
					}
					SimpleXYSeries series = new SimpleXYSeries("Pressure");
					series.setModel(pressureValues, ArrayFormat.Y_VALS_ONLY);

					bpMeasureXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);

					// freeze the range boundaries:
					bpMeasureXYPlot.setRangeBoundaries(0, 300,
							XYPlot.BoundaryMode.FIXED);
					bpMeasureXYPlot.setDomainBoundaries(0, l,
							XYPlot.BoundaryMode.FIXED);
					bpMeasureXYPlot.addSeries(series,
							LineAndPointRenderer.class,
							new LineAndPointFormatter(Color.rgb(100, 100, 200),
									Color.BLACK));
					bpMeasureXYPlot.setDomainStepValue(3);
					bpMeasureXYPlot.setTicksPerRangeLabel(3);
					bpMeasureXYPlot.setDomainLabel("Time (s)");
					bpMeasureXYPlot.getDomainLabelWidget().pack();
					bpMeasureXYPlot.setRangeLabel("Pressure(mmHg)");
					bpMeasureXYPlot.getRangeLabelWidget().pack();
					bpMeasureXYPlot.disableAllMarkup();
				}
			}

		}
		// If an instance of this activity had previously stopped, we can
		// get the original text iMEASUREMENT_FILE_SYNCt started with.
		if (savedInstanceState != null) {
			mOriginalContent = savedInstanceState.getString(ORIGINAL_CONTENT);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		// If we didn't have any trouble retrieving the data, it is now
		// time to get at the stuff.
		if (mCursor != null) {
			// Make sure we are at the one and only row in the cursor.
			mCursor.moveToFirst();

			// Modify our overall title depending on the mode we are running in.
			if (mState == STATE_EDIT) {
				// setTitle(getText(R.string.title_edit));
			} else if (mState == STATE_INSERT) {
				// setTitle(getText(R.string.title_create));
			}

			// This is a little tricky: we may be resumed after previously being
			// paused/stopped. We want to put the new text in the text view,
			// but leave the user where they were (retain the cursor position
			// etc). This version of setText does that for us.
			String note = mCursor.getString(COLUMN_INDEX_NOTE);
			mText.setTextKeepState(note);

			// If we hadn't previously retrieved the original text, do so
			// now. This allows the user to revert their changes.
			if (mOriginalContent == null) {
				mOriginalContent = note;
			}

		} else {
			// setTitle(getText(R.string.error_title));
			// mText.setText(getText(R.string.error_message));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Save away the original text, so we still have it if the activity
		// needs to be killed while paused.
		outState.putString(ORIGINAL_CONTENT, mOriginalContent);
	}

	@Override
	protected void onPause() {
		super.onPause();

		// The user is going somewhere else, so make sure their current
		// changes are safely saved away in the provider. We don't need
		// to do this if only editing.
		if (mCursor != null) {
			String text = mText.getText().toString();
			int length = text.length();

			// If this activity is finished, and there is no text, then we
			// do something a little special: simply delete the note entry.
			// Note that we do this both for editing and inserting... it
			// would be reasonable to only do it when inserting.
			if (isFinishing() && (length == 0) && !mNoteOnly) {
				setResult(RESULT_CANCELED);
				deleteMeasure();

				// Get out updates into the provider.
			} else {
				ContentValues values = new ContentValues();

				// This stuff is only done when working with a full-fledged
				// note.
				if (!mNoteOnly) {
					// Bump the modification time to now.
					// values.put(Notes.MODIFIED_DATE,
					// System.currentTimeMillis());
					// If we are creating a new note, then we want to also
					// create
					// an initial title for it.
					if (mState == STATE_INSERT) {
						String title = text.substring(0, Math.min(30, length));
						if (length > 30) {
							int lastSpace = title.lastIndexOf(' ');
							if (lastSpace > 0) {
								title = title.substring(0, lastSpace);
							}
						}
						// values.put(Notes.TITLE, title);
					}
				}

				// Write our text back into the provider.
				// values.put(Notes.NOTE, text);

				// Commit all of our changes to persistent storage. When the
				// update completes
				// the content provider will notify the cursor of the change,
				// which will
				// cause the UI to be updated.
				getContentResolver().update(mUri, values, null, null);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		// Build the menus that are shown when editing.
		if (mState == STATE_EDIT) {
			menu.add(0, REVERT_ID, 0, R.string.menu_revert).setShortcut('0',
					'r').setIcon(android.R.drawable.ic_menu_revert);
			if (!mNoteOnly) {
				menu.add(0, DELETE_ID, 0, R.string.menu_delete).setShortcut(
						'1', 'd').setIcon(android.R.drawable.ic_menu_delete);
			}

			// Build the menus that are shown when inserting.
		} else {
			menu.add(0, DISCARD_ID, 0, R.string.menu_discard).setShortcut('0',
					'd').setIcon(android.R.drawable.ic_menu_delete);
		}

		// If we are working on a full note, then append to the
		// menu items for any other activities that can do stuff with it
		// as well. This does a query on the system for any activities that
		// implement the ALTERNATIVE_ACTION for our data, adding a menu item
		// for each one that is found.
		if (!mNoteOnly) {
			Intent intent = new Intent(null, getIntent().getData());
			intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
			menu.addIntentOptions(Menu.CATEGORY_ALTERNATIVE, 0, 0,
					new ComponentName(this, MeasureActivity.class), null,
					intent, 0, null);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle all of the possible menu actions.
		switch (item.getItemId()) {
		case DELETE_ID:
			deleteMeasure();
			finish();
			break;
		case DISCARD_ID:
			cancelUpdate();
			break;
		case REVERT_ID:
			cancelUpdate();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Take care of canceling work on a note. Deletes the note if we had created
	 * it, otherwise reverts to the original text.
	 */
	private final void cancelUpdate() {
		if (mCursor != null) {
			if (mState == STATE_EDIT) {
				// Put the original note text back into the database
				mCursor.close();
				mCursor = null;
				ContentValues values = new ContentValues();
				values.put(BPMeasure.NOTE, mOriginalContent);
				getContentResolver().update(mUri, values, null, null);
			}
		}
		setResult(RESULT_CANCELED);
		finish();
	}

	/**
	 * Take care of deleting a note. Simply deletes the entry.
	 */
	private final void deleteMeasure() {
		if (mCursor != null) {
			mCursor.close();
			mCursor = null;
			getContentResolver().delete(mUri, null, null);
			mText.setText("");
		}
	}

	/**
	 * 
	 * @param time
	 * @param note
	 */
	private void update(String note, long time) {

		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(BPMeasure.MODIFIED_DATE, time);
		values.put(BPMeasure.NOTE, note);

		cr.update(mUri, values, null, null);
	}

}
