// Name of the package
package com.ewhoxford.android.bloodpressure;

//Import resources

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ewhoxford.android.bloodpressure.database.BloodPressureMeasureTable.BPMeasure;

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
			BPMeasure.MEASUREMENT_FILE_EXIST, // 6
			BPMeasure.MODIFIED_DATE,// 1
	};
	/** The index of the note column */
	private static final int COLUMN_INDEX_NOTE = 5;

	// This is our state data that is stored when freezing.
	private static final String ORIGINAL_CONTENT = "origContent";

	// The different distinct states the activity can be run in.
	private static final int STATE_EDIT = 0;
	private static final int STATE_INSERT = 1;

	MeasureViewActivity measureContext = this;
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
	// delete measure alert dialog
	AlertDialog.Builder deleteAlert;
	// user notes
	TextView textView;
	// has csv file answer
	TextView csvFileAnswerTextView;

	private int mState;
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
		// The text view for our note, identified by its ID in the XML file.
		textView = (TextView) findViewById(R.id.id_date);

		// initialize checkbox variable
		csvFileAnswerTextView = (TextView) findViewById(R.id.answer_csv);

		// Help button
		Button deleteButton = (Button) findViewById(R.id.button_delete);
		deleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AlertDialog alert = deleteAlert.create();
				alert.show();
			}
		});

		deleteAlert = new AlertDialog.Builder(this);
		deleteAlert.setMessage("Are you sure you want to delete measure?")
				.setCancelable(false).setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								deleteMeasure();
								dialog.cancel();
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

		saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Long time = System.currentTimeMillis();
				updateNote(mText.getText().toString(), time);

				AlertDialog alert = saveAlert.create();
				alert.show();
			}

		});
		saveAlert = new AlertDialog.Builder(this);
		saveAlert.setMessage("Changes Saved");
		saveAlert.setCancelable(false).setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						Intent i = new Intent(measureContext,
								MeasureListActivity.class);
						startActivity(i);
					}
				});

		// #### End of Set up click listeners for all the buttons

		// Get the measure!
		mCursor = managedQuery(mUri, PROJECTION, null, null, null);

		if (mCursor.moveToFirst()) {

			String notes;
			long createdDate;
			long modified;
			int sp;
			int dp;
			int pulse;
			int id = 0;
			float aux;
			int notesColumn = mCursor.getColumnIndex(BPMeasure.NOTE);
			int createdDateColumn = mCursor
					.getColumnIndex(BPMeasure.CREATED_DATE);
			int modifiedDateColumn = mCursor
					.getColumnIndex(BPMeasure.MODIFIED_DATE);
			int spColumn = mCursor.getColumnIndex(BPMeasure.SP);
			int dpColumn = mCursor.getColumnIndex(BPMeasure.DP);
			int pulseColumn = mCursor.getColumnIndex(BPMeasure.PULSE);
			int idColumn = mCursor.getColumnIndex(BPMeasure._ID);
			int fileExistsColumn = mCursor
					.getColumnIndex(BPMeasure.MEASUREMENT_FILE_EXIST);

			// Get the field values
			id = mCursor.getInt(idColumn);
			notes = mCursor.getString(notesColumn);
			aux = Float.parseFloat(mCursor.getString(spColumn));
			sp = Math.round(aux);
			aux = Float.parseFloat(mCursor.getString(dpColumn));
			dp = Math.round(aux);
			aux = Float.parseFloat(mCursor.getString(pulseColumn));
			pulse = Math.round(aux);
			createdDate = new Long(mCursor.getString(createdDateColumn));
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
			Date createdResultdate = new Date(createdDate);
			modified = new Long(mCursor.getString(modifiedDateColumn));
			Date modifiedResultdate = new Date(modified);

			// Display notes and blood pressure algorithm result in the
			// Measure layout
			String headerString = "Number:   " + id + "\n";
			headerString += "Created:   " + sdf.format(createdResultdate)
					+ "\n";
			headerString += "Modified: " + sdf.format(modifiedResultdate);
			textView.setText(headerString);
			mText.setText(notes);
			ValuesView valuesView = (ValuesView) findViewById(R.id.results);
			valuesView.requestFocus();
			valuesView.setSPressure(sp);
			valuesView.setDPressure(dp);
			valuesView.setPulseRate(pulse);
			valuesView.invalidate();

			String fileExists = mCursor.getString(fileExistsColumn);
			if (fileExists.equals("1")) {
				csvFileAnswerTextView.setText("Has csv file: Yes");
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
	private void updateNote(String note, long time) {

		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(BPMeasure.MODIFIED_DATE, time);
		values.put(BPMeasure.NOTE, note);
		cr.update(mUri, values, null, null);
	}

}
