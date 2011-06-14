/*
 */

package com.ewhoxford.android.bloodpressure;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import android.accounts.Account;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentUris;
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
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.ewhoxford.android.bloodpressure.database.BloodPressureMeasureTable.BPMeasure;
import com.ewhoxford.android.bloodpressure.ghealth.auth.AccountChooser;
import com.ewhoxford.android.bloodpressure.ghealth.auth.AuthManager;
import com.ewhoxford.android.bloodpressure.ghealth.gdata.GDataHealthClient;
import com.ewhoxford.android.bloodpressure.ghealth.gdata.HealthClient;
import com.ewhoxford.android.bloodpressure.ghealth.gdata.Result;
import com.ewhoxford.android.bloodpressure.ghealth.gdata.HealthClient.AuthenticationException;
import com.ewhoxford.android.bloodpressure.ghealth.gdata.HealthClient.InvalidProfileException;
import com.ewhoxford.android.bloodpressure.ghealth.gdata.HealthClient.ServiceException;

/**
 * Displays a list of BP measures. Will display notes from the {@link Uri}
 * provided in the intent if there is one, otherwise defaults to displaying the
 * contents of the {@link NotePadProvider}
 * 
 * @author mauro
 */
public class MeasureListActivity extends ListActivity implements
		SimpleCursorAdapter.ViewBinder {
	public static final String TAG = "BloodPressureMeasuresList";

	private static final String SERVICE_NAME = HealthClient.H9_SERVICE;

	private static final int ACTIVITY_AUTHENTICATE = 0;
	// Public so that the AuthManager can start a new get_login activity after
	// the
	// user has authorized the app to access their data.
	public static final int ACTIVITY_GET_LOGIN = 1;

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

	@SuppressWarnings("unchecked")
	private AsyncTask currentTask;

	private static final String PREF_HEALTH_NOTE = "read_note";

	Context measureListContext = this;

	public static final String ACCOUNT_TYPE = "com.google";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// The user does not need to hold down the key to use menu shortcuts.
		setDefaultKeyMode(DEFAULT_KEYS_SHORTCUT);
		setContentView(R.layout.measure_list);
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

		// ListView headerListView = (ListView) findViewById(R.id.header1);
		// // Used to map notes entries from the database to views
		// String[] from = new String[] {};
		// int[] to = new int[] {};
		//
		// SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
		// R.layout.measure_list_header_row, null, from, to);
		// headerListView.setAdapter(adapter);

		// Populate the bp measures list
		populateBPMeasureList();

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

		auth = new AuthManager(this, SERVICE_NAME);

		// If this is the first use, display the requisite Health notice.

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

	private void populateBPMeasureList() {

		Cursor cursor = getBPMeasureList();

		// Used to map notes entries from the database to views
		String[] from = new String[] { BPMeasure._ID, BPMeasure.CREATED_DATE,
				BPMeasure.SP, BPMeasure.DP, BPMeasure.PULSE,
				BPMeasure.PHR_PROVIDER_PROFILE };
		int[] to = new int[] { R.id.id, R.id.createdDate, R.id.sp, R.id.dp,
				R.id.pulse, R.id.sync_profile };

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
				BPMeasure.SP, BPMeasure.DP, BPMeasure.PHR_PROVIDER_PROFILE };// Return
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
					SimpleDateFormat sdf = new SimpleDateFormat(
							"dd/MM/yy HH:mm");
					Date resultdate = new Date(dateStr);

					// TODO correct this space problem between columns
					((TextView) v).setText(sdf.format(resultdate));
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
				case 6:
					String aux12 = cur.getString(columnIndex);
					if (aux12 == null)
						((TextView) v).setText("No");
					if (aux12 == "")
						((TextView) v).setText("No");
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

	/**
	 * Retrieve a list of accounts stored in the phone and display a dialog
	 * allowing the user to choose one.
	 */
	protected void chooseAccount() {
		Log.d(TAG, "Selecting account.");
		AccountChooser accountChooser = new AccountChooser();
		accountChooser.chooseAccount(MeasureListActivity.this,
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
			dialog = ProgressDialog.show(MeasureListActivity.this, "", this
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

							retrieveResults();
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
		// Called after the user has authorized application access to the
		// service.
		case ACTIVITY_GET_LOGIN:
			if (resultCode == RESULT_OK) {
				if (!auth.authResult(resultCode, data)) {
					// Auth token could not be retrieved.
				}
			}
			break;
		}
	}

	/**
	 * Retrieve a list of test results from data base.
	 */
	protected void retrieveResults() {
		if (account == null) {
			chooseAccount();
			return;
		}

		if (profileId == null) {
			chooseProfile();
			return;
		}

		showDialog(DIALOG_PROGRESS);
		currentTask = new RetrieveResultsTask().execute();
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
			displayResults();
		}
	}

	/**
	 * Display results in the main activity's test result list.
	 */
	protected void displayResults() {
		Log.d(TAG, "Displaying test results.");
		// Collect the Tests from the Results and order them chronologically.
		Set<Result> resultSet = new TreeSet<Result>();
		resultSet.addAll(results);
		Result[] items = resultSet.toArray(new Result[resultSet.size()]);

		// Update the list view of the main activity with the list of test
		// results.
		// setListAdapter(new ArrayAdapter<Result>(this,
		// R.layout.main_list_item,
		// items));

		// Display a notice if not results found.
		if (items.length == 0) {
			// Toast
			// .makeText(getApplicationContext(),
			// this.getString(R.string.no_test_results),
			// Toast.LENGTH_LONG).show();
		}
	}
}
