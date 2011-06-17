// Name of the package
package com.ewhoxford.android.bloodpressure;

//Import resources

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ewhoxford.android.bloodpressure.database.BloodPressureMeasureTable.BPMeasure;
import com.ewhoxford.android.bloodpressure.ghealth.auth.AccountChooser;
import com.ewhoxford.android.bloodpressure.ghealth.auth.AuthManager;
import com.ewhoxford.android.bloodpressure.ghealth.gdata.GDataHealthClient;
import com.ewhoxford.android.bloodpressure.ghealth.gdata.HealthClient;
import com.ewhoxford.android.bloodpressure.ghealth.gdata.Result;
import com.ewhoxford.android.bloodpressure.ghealth.gdata.TestResult;
import com.ewhoxford.android.bloodpressure.ghealth.gdata.HealthClient.AuthenticationException;
import com.ewhoxford.android.bloodpressure.ghealth.gdata.HealthClient.InvalidProfileException;
import com.ewhoxford.android.bloodpressure.ghealth.gdata.HealthClient.ServiceException;

/**
 * A generic activity for editing a bp measure in a database. This can be used
 * either to simply view a note {@link InteFnt#ACTION_VIEW}, view and edit a
 * note {@link Intent#ACTION_EDIT}
 * 
 * @author mauro
 */
public class MeasureViewActivity extends Activity {

	/**
	 * Standard projection for the interesting columns of a normal note.
	 */
	private static final String[] PROJECTION = new String[] {
			BPMeasure._ID, // 0
			BPMeasure.CREATED_DATE,// 1
			BPMeasure.SP, // 2
			BPMeasure.DP, // 3
			BPMeasure.PULSE, // 4
			BPMeasure.NOTE, // 5
			BPMeasure.MEASUREMENT_FILE_EXIST, // 6
			BPMeasure.MODIFIED_DATE, BPMeasure.MEASUREMENT_SYNC,
			BPMeasure.PHR_PROVIDER, BPMeasure.PHR_PROVIDER_PROFILE,
			BPMeasure.PHR_PROVIDER_USERNAME };
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
	// measurementExists
	TextView measureSyncTextView;
	// profile sync
	TextView phrProfileTextView;
	// provider sync
	TextView phrProviderTextView;
	// username sync
	TextView phrUsernameTextView;

	private int sp;
	private int dp;
	private int pulse;

	private static final TreeMap<String, String> RESULTS = new TreeMap<String, String>();

	// TODO Load template results instead of map
	static {
		RESULTS.put("Blood glucose", "mg/dL");
		RESULTS.put("Blood pressure", "mmHg");
		RESULTS.put("Body temperature", "degrees Fahrenheit");
		RESULTS.put("Breathing", "breaths/min");
		RESULTS.put("Calories burned", "calories");
		RESULTS.put("Calories consumed", "calories");
		RESULTS.put("Cycling distance", "miles");
		RESULTS.put("Cycling time", "minutes");
		RESULTS.put("Exercise minutes", "minutes");
		RESULTS.put("Heart rate", "bpm");
		RESULTS.put("Height", "in");
		RESULTS.put("Hours slept", "hours");
		RESULTS.put("Peak flow", "liters/sec");
		RESULTS.put("Running distance", "miles");
		RESULTS.put("Running time", "minutes");
		RESULTS.put("Steps taken", "steps");
		RESULTS.put("Swimming distance", "meters");
		RESULTS.put("Swimming time", "minutes");
		RESULTS.put("Vegetable servings", "servings");
		RESULTS.put("Walking distance", "miles");
		RESULTS.put("Walking time", "minutes");
		RESULTS.put("Weight", "lb");
	}
	private int mState;
	private Uri mUri;
	private Cursor mCursor;
	private EditText mText;
	private String mOriginalContent;

	public static final String RESULT_PROPERTY = "result";

	private Date createdResultdate;

	public static final String TAG = "BloodPressureMeasuresList";

	private static final String SERVICE_NAME = HealthClient.H9_SERVICE;

	private static final int ACTIVITY_AUTHENTICATE = 0;
	// Public so that the AuthManager can start a new get_login activity after
	// the
	// user has authorized the app to access their data.
	public static final int ACTIVITY_GET_LOGIN = 1;
	private static final int ACTIVITY_ADD_RESULT = 2;

	private static final int DIALOG_PROFILES = 0;
	private static final int DIALOG_PROGRESS = 1;
	private static final int DIALOG_ERROR = 2;
	private static final int DIALOG_TERMS = 3;
	/**
	 * Service client for send to and retrieving information from Google Health.
	 */
	private final HealthClient client = new GDataHealthClient(SERVICE_NAME);

	private Map<String, String> profiles = new LinkedHashMap<String, String>();
	private String profileId;

	private List<Result> results;

	private AuthManager auth;
	private Account account;
	private Result result;
	@SuppressWarnings("unchecked")
	private AsyncTask currentTask;
	private Builder syncAlert;
	private Builder syncAlert2;

	private static final String PREF_HEALTH_NOTE = "read_note";

	public static final String ACCOUNT_TYPE = "Google Health";
	private static final int ACTIVITY_AUTHENTICATE2 = 4;
	private static final int DIALOG_PROFILES2 = 5;

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

		csvFileAnswerTextView = (TextView) findViewById(R.id.answer_csv);

		measureSyncTextView = (TextView) findViewById(R.id.syncked_phr);
		// profile sync
		phrProfileTextView = (TextView) findViewById(R.id.phr_profile);
		// provider sync
		phrProviderTextView = (TextView) findViewById(R.id.phr_provider);
		// username sync
		phrUsernameTextView = (TextView) findViewById(R.id.phr_username);

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
		deleteAlert.setMessage(
				getResources().getText(R.string.alert_dialog_delete_measure))
				.setCancelable(false).setPositiveButton(
						getResources().getText(R.string.alert_dialog_yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								deleteMeasure();
								dialog.cancel();
								Intent i = new Intent(measureContext,
										MeasureListActivity.class);
								startActivity(i);
							}
						}).setNegativeButton(
						getResources().getText(R.string.alert_dialog_no),
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
		builder.setMessage(
				getResources().getText(R.string.alert_dialog_discard_measure))
				.setCancelable(false).setPositiveButton(
						getResources().getText(R.string.alert_dialog_yes),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								cancelUpdate();
							}
						}).setNegativeButton(
						getResources().getText(R.string.alert_dialog_no),
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
		saveAlert.setMessage(getResources().getText(
				R.string.alert_dialog_changes_saved));
		saveAlert.setCancelable(false).setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						Intent i = new Intent(measureContext,
								MeasureListActivity.class);
						startActivity(i);
					}
				});
		Button syncButton = (Button) findViewById(R.id.button_sync_ghealth);
		syncButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				
				String resultName1 = "Blood pressure";

				TestResult test = new TestResult();
				test.setName(resultName1);
				test.setValue(sp + "/" + dp);
				test.setUnits("mmHg");
				// test.setDate();
				String format = "yyyy-MM-dd";
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				String date = sdf.format(createdResultdate);

				test.setDate(date);
				System.out.println(test.getDate());
				String resultName2 = "Heart rate";
				TestResult test1 = new TestResult();
				test1.setName(resultName2);
				test1.setValue(Integer.toString(pulse));
				test1.setUnits(RESULTS.get(resultName2));
				test1.setDate(date);

				result = new Result();
				result.addTestResult(test);
				result.addTestResult(test1);
				
				if (account == null) {
					chooseAccount2();
					return;
				}
				

				// showDialog(DIALOG_PROGRESS);
				// currentTask = new CreateResultTask().execute(result);

				// Bundle bundle = new Bundle();
				// bundle.putSerializable(MeasureViewActivity.RESULT_PROPERTY,
				// result);
				//
				// Intent intent = new Intent();
				// intent.putExtras(bundle);
				// setResult(RESULT_OK, intent);
				// finish();
				showDialog(DIALOG_PROGRESS);
				currentTask = new CreateResultTask().execute(result);
			}
		});

		syncAlert = new AlertDialog.Builder(this);
		syncAlert.setMessage(getResources().getText(
				R.string.alert_dialog_bp_syncked));
		syncAlert.setCancelable(false).setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						Intent i = new Intent(measureContext,
								MeasureListActivity.class);
						startActivity(i);
					}
				});
		// Configure the buttons
		Button button = (Button) findViewById(R.id.main_accounts);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				chooseAccount();
			}
		});

		button = (Button) findViewById(R.id.main_profiles);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				chooseProfile();
			}
		});

		// #### End of Set up click listeners for all the buttons

		// Get the measure!
		mCursor = managedQuery(mUri, PROJECTION, null, null, null);

		if (mCursor.moveToFirst()) {

			String notes;
			String phrProvider;
			String phrProfile;
			String phrUsername;
			long createdDate;
			long modified;

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
			int measureSyncColumn = mCursor
					.getColumnIndex(BPMeasure.MEASUREMENT_SYNC);
			int phrUsernameColumn = mCursor
					.getColumnIndex(BPMeasure.PHR_PROVIDER_USERNAME);
			int phrProviderColumn = mCursor
					.getColumnIndex(BPMeasure.PHR_PROVIDER);
			int phrProfileColumn = mCursor
					.getColumnIndex(BPMeasure.PHR_PROVIDER_PROFILE);

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
			createdResultdate = new Date(createdDate);
			modified = new Long(mCursor.getString(modifiedDateColumn));
			Date modifiedResultdate = new Date(modified);
			phrProvider = mCursor.getString(phrProviderColumn);
			phrProfile = mCursor.getString(phrProfileColumn);
			phrUsername = mCursor.getString(phrUsernameColumn);

			// Display notes and blood pressure algorithm result in the
			// Measure layout
			String headerString = getResources().getText(R.string.number)
					+ ":   " + id + "\n";
			headerString += getResources().getText(R.string.created_date)
					+ "   " + sdf.format(createdResultdate) + "\n";
			headerString += getResources().getText(R.string.modified_date)
					+ " " + sdf.format(modifiedResultdate);
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
				csvFileAnswerTextView.setText(R.string.has_csv_file_yes);
			}

			String measureSync = mCursor.getString(measureSyncColumn);
			if (measureSync.equals("1")) {
				measureSyncTextView.setText(R.string.syncked);

				phrProfileTextView.setText(getResources().getText(
						R.string.phr_profile)
						+ " " + phrProfile);
				// provider sync
				phrProviderTextView.setText(getResources().getText(
						R.string.phr_provider)
						+ " " + phrProvider);
				// username sync
				phrUsernameTextView.setText(getResources().getText(
						R.string.phr_username)
						+ " " + phrUsername);
				syncButton.setEnabled(false);
				phrProfileTextView.setEnabled(false);
				phrProviderTextView.setEnabled(false);
				phrUsernameTextView.setEnabled(false);
			} else {
				phrProfileTextView.setEnabled(false);
				phrProviderTextView.setEnabled(false);
				phrUsernameTextView.setEnabled(false);
			}

		}

		auth = new AuthManager(this, SERVICE_NAME);

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

	/**
	 * 
	 * @param time
	 */
	private void saveSyncGHealth(String username, String provider,
			String profile, String profileID, long time) {

		ContentResolver cr = getContentResolver();
		ContentValues values = new ContentValues();
		values.put(BPMeasure.MODIFIED_DATE, time);
		values.put(BPMeasure.PHR_PROVIDER, provider);
		values.put(BPMeasure.PHR_PROVIDER_USERNAME, username);
		values.put(BPMeasure.PHR_PROVIDER_PROFILE, profile);
		values.put(BPMeasure.MEASUREMENT_SYNC, 1);
		values.put(BPMeasure.PHR_PROVIDER_PROFILE_ID, profileID);
		cr.update(mUri, values, null, null);
	}

	/**
	 * Retrieve a list of accounts stored in the phone and display a dialog
	 * allowing the user to choose one.
	 */
	protected void chooseAccount() {
		Log.d(TAG, "Selecting account.");
		AccountChooser accountChooser = new AccountChooser();
		accountChooser.chooseAccount(MeasureViewActivity.this,
				new AccountChooser.AccountHandler() {
					@Override
					public void handleAccountSelected(Account account) {
						Log.d(TAG, "Account selected.");
						// The user hit cancel
						if (account == null) {
							return;
						}
						authenticate(account);
					}
				});
	}

	/**
	 * Retrieve a list of accounts stored in the phone and display a dialog
	 * allowing the user to choose one.
	 */
	protected void chooseAccount2() {
		Log.d(TAG, "Selecting account2.");
		AccountChooser accountChooser = new AccountChooser();
		accountChooser.chooseAccount(MeasureViewActivity.this,
				new AccountChooser.AccountHandler() {
					@Override
					public void handleAccountSelected(Account account) {
						Log.d(TAG, "Account selected2.");
						// The user hit cancel
						if (account == null) {
							return;
						}
						authenticate2(account);
					}
				});
	}

	/**
	 * Once an account has been selected, use account credentials to get an
	 * authorization token. If the account has already been authenticated, then
	 * the existing token will be invalidated prior to re-authenticating.
	 * 
	 * @param account
	 *            The {@code Account} to authenticate with.
	 */
	protected void authenticate(Account account) {
		Log.d(TAG, "Authenticating account.");

		this.account = account;

		auth.doLogin(new Runnable() {
			public void run() {
				Log.d(TAG, "User authenticated.");
				onActivityResult(ACTIVITY_AUTHENTICATE, RESULT_OK, null);
			}
		}, account);
	}

	/**
	 * Once an account has been selected, use account credentials to get an
	 * authorization token. If the account has already been authenticated, then
	 * the existing token will be invalidated prior to re-authenticating.
	 * 
	 * @param account
	 *            The {@code Account} to authenticate with.
	 */
	protected void authenticate2(Account account) {
		Log.d(TAG, "Authenticating account.");

		this.account = account;

		auth.doLogin(new Runnable() {
			public void run() {
				Log.d(TAG, "User authenticated.");
				onActivityResult(ACTIVITY_AUTHENTICATE2, RESULT_OK, null);
			}
		}, account);
	}

	/**
	 * Retrieve a list of profiles from Health and display a dialog allowing the
	 * user to select one.
	 */
	protected void chooseProfile() {
		// If the user hasn't selected an account (i.e. they canceled the
		// initial
		// account dialog), have them do so.
		if (account == null) {
			chooseAccount();
			return;
		}

		showDialog(DIALOG_PROGRESS);
		currentTask = new RetrieveProfilesTask().execute();
	}

	/**
	 * Retrieve a list of profiles from Health and display a dialog allowing the
	 * user to select one.
	 */
	protected void chooseProfile2() {
		// If the user hasn't selected an account (i.e. they canceled the
		// initial
		// account dialog), have them do so.
		if (account == null) {
			chooseAccount();
			return;
		}

		showDialog(DIALOG_PROGRESS);
		currentTask = new RetrieveProfilesTask2().execute();
	}

	protected class RetrieveProfilesTask extends AsyncTask<Void, Void, Void> {
		private Exception exception;

		@Override
		protected Void doInBackground(Void... params) {
			Log.d(TAG, "Retreiving profiles.");
			try {
				profiles = client.retrieveProfiles();
			} catch (Exception e) {
				exception = e;
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (exception != null) {
				handleException(exception);
				return;
			}

			Log.d(TAG, "Profiles retrieved.");
			dismissDialog(DIALOG_PROGRESS);
			showDialog(DIALOG_PROFILES);
		}
	}

	protected class RetrieveProfilesTask2 extends AsyncTask<Void, Void, Void> {
		private Exception exception;

		@Override
		protected Void doInBackground(Void... params) {
			Log.d(TAG, "Retreiving profiles.");
			try {
				profiles = client.retrieveProfiles();
			} catch (Exception e) {
				exception = e;
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (exception != null) {
				handleException(exception);
				return;
			}

			Log.d(TAG, "Profiles retrieved.");
			dismissDialog(DIALOG_PROGRESS);
			showDialog(DIALOG_PROFILES2);
		}
	}

	/**
	 * Method processes network connectivity exceptions, which will
	 * re-authenticate the user, re-request a Health profile, or request that
	 * the user check the network connection.
	 * 
	 * @param e
	 *            The network connectivity exception to process, which can be a
	 *            AuthenticationException, InvalidProfileException, or
	 *            ServiceException.
	 */
	protected void handleException(Exception e) {
		if (e instanceof AuthenticationException) {
			Log.w(TAG, "User authentication failed. Re-authenticating.");
			authenticate(account);
		} else if (e instanceof InvalidProfileException) {
			Log.w(TAG, "Profile invalid. Re-retrieving profiles.");
			chooseProfile();
		} else if (e instanceof ServiceException) {
			if (e.getCause() != null) {
				// Likely network connectivity issue.
				Log.e(TAG, "Error connecting to Health service.", e);
			} else {
				ServiceException se = (ServiceException) e;
				Log.e(TAG, "Error connecting to Health service: code="
						+ se.getCode() + ", message=" + e.getMessage()
						+ ", content=" + se.getContent());
			}

			// Remove the progress dialog and display the error.
			dismissDialog(DIALOG_PROGRESS);
			showDialog(DIALOG_ERROR);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		AlertDialog.Builder builder;

		switch (id) {
		case DIALOG_TERMS:
			final SpannableString msg = new SpannableString(this
					.getString(R.string.health_notice));
			Linkify.addLinks(msg, Linkify.WEB_URLS);
			// TODO Make links click-able

			builder = new AlertDialog.Builder(this);
			builder.setTitle("Please note:");
			builder.setMessage(msg);
			builder.setPositiveButton("Close",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Store that the user has read the note.
							Editor e = getPreferences(Context.MODE_PRIVATE)
									.edit();
							e.putBoolean(PREF_HEALTH_NOTE, true);
							e.commit();

							chooseAccount();
						}
					});

			dialog = builder.create();
			break;

		case DIALOG_PROGRESS:
			dialog = ProgressDialog.show(MeasureViewActivity.this, "", this
					.getString(R.string.loading), true);
			dialog.setCancelable(true);
			dialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					currentTask.cancel(true);
				}
			});
			break;

		case DIALOG_PROFILES:
			String[] profileNames = profiles.values().toArray(
					new String[profiles.size()]);

			builder = new AlertDialog.Builder(this);
			builder.setTitle(this.getText(R.string.choose_profile));
			builder.setItems(profileNames,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int i) {
							// Remove the dialog so that it's refreshed with new
							// list items the
							// next time it's displayed since onPrepareDialog
							// cannot change the dialog's
							// list items.
							removeDialog(DIALOG_PROFILES);

							profileId = profiles.keySet().toArray(
									new String[profiles.size()])[i];
							client.setProfileId(profileId);

							Button button = (Button) findViewById(R.id.main_profiles);
							button.setText(profiles.get(profileId));

						}
					});

			dialog = builder.create();
			break;
		case DIALOG_PROFILES2:
			profileNames = profiles.values().toArray(
					new String[profiles.size()]);

			builder = new AlertDialog.Builder(this);
			builder.setTitle(this.getText(R.string.choose_profile));
			builder.setItems(profileNames,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int i) {
							// Remove the dialog so that it's refreshed with new
							// list items the
							// next time it's displayed since onPrepareDialog
							// cannot change the dialog's
							// list items.
							removeDialog(DIALOG_PROFILES);

							profileId = profiles.keySet().toArray(
									new String[profiles.size()])[i];
							client.setProfileId(profileId);

							Button button = (Button) findViewById(R.id.main_profiles);
							button.setText(profiles.get(profileId));
							showDialog(DIALOG_PROGRESS);
							currentTask = new CreateResultTask()
									.execute(result);
						}
					});

			dialog = builder.create();
			break;
		case DIALOG_ERROR:
			builder = new AlertDialog.Builder(this);
			builder.setTitle(this.getText(R.string.connection_error_title));
			builder.setMessage(R.string.connection_error_message);
			builder.setCancelable(true);
			builder.setPositiveButton("Close",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					});

			dialog = builder.create();
			break;

		default:
			dialog = null;
		}

		return dialog;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case ACTIVITY_AUTHENTICATE:
			if (resultCode == RESULT_OK) {
				if (auth.getAuthToken() == null) {
					Log.w(TAG, "User authenticated, but auth token not found.");
					authenticate(account);
				} else {
					Log
							.d(TAG,
									"User authenticated, proceeding with profile selection.");

					Button button = (Button) findViewById(R.id.main_accounts);
					button.setText(account.name);

					client.setAuthToken(auth.getAuthToken());
					chooseProfile();
				}
			}
			break;
		case ACTIVITY_AUTHENTICATE2:
			if (resultCode == RESULT_OK) {
				if (auth.getAuthToken() == null) {
					Log.w(TAG, "User authenticated, but auth token not found.");
					authenticate(account);
				} else {
					Log
							.d(TAG,
									"User authenticated, proceeding with profile selection.");

					Button button = (Button) findViewById(R.id.main_accounts);
					button.setText(account.name);

					client.setAuthToken(auth.getAuthToken());
					chooseProfile2();
				}
			}
			break;
		// Called after the user has authorized application access to the
		// service.
		case ACTIVITY_GET_LOGIN:
			if (resultCode == RESULT_OK) {
				if (!auth.authResult(resultCode, data)) {
					// Auth token could not be retrieved.
				}
			}
			break;

		case ACTIVITY_ADD_RESULT:
			if (resultCode == RESULT_OK) {
				Bundle bundle = data.getExtras();
				Result result = (Result) bundle.get(RESULT_PROPERTY);

				showDialog(DIALOG_PROGRESS);
				currentTask = new CreateResultTask().execute(result);
			}
			break;
		}
	}

	protected class RetrieveResultsTask extends AsyncTask<Void, Void, Void> {
		private Exception exception;

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Log.d(TAG, "Retreiving results.");
				results = client.retrieveResults();
			} catch (Exception e) {
				exception = e;
			}
			return null;
		}

		protected void onPostExecute(Void results) {
			if (exception != null) {
				handleException(exception);
				return;
			}

			Log.d(TAG, "Results retrieved.");
			dismissDialog(DIALOG_PROGRESS);
		
		}
	}

	protected class RetrieveResultsTask2 extends AsyncTask<Void, Void, Void> {
		private Exception exception;

		@Override
		protected Void doInBackground(Void... params) {
			try {
				Log.d(TAG, "Retreiving results.");
				results = client.retrieveResults();
			} catch (Exception e) {
				exception = e;
			}
			return null;
		}

		protected void onPostExecute(Void results) {
			if (exception != null) {
				handleException(exception);
				return;
			}

			Log.d(TAG, "Results retrieved.");
			dismissDialog(DIALOG_PROGRESS);
		
		}
	}
	

	protected class CreateResultTask extends AsyncTask<Result, Void, Void> {
		private Exception exception;

		@Override
		protected Void doInBackground(Result... results) {
			Log.d(TAG, "Creating result.");
			try {
				client.createResult(results[0]);

			} catch (Exception e) {
				exception = e;
			}
			return null;
		}

		protected void onPostExecute(Void results) {
			if (exception != null) {
				handleException(exception);
				return;
			}

			Log.d(TAG, "Results retrieved.");
			dismissDialog(DIALOG_PROGRESS);

			saveSyncGHealth(account.name, ACCOUNT_TYPE,
					profiles.get(profileId), profileId, System
							.currentTimeMillis());

			AlertDialog alert = syncAlert.create();
			alert.show();
		}
	}
}
