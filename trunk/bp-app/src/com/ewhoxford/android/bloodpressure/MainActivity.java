package com.ewhoxford.android.bloodpressure;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Main Moca activity. When Moca is launched, this activity runs, allowing the user to 
 * either run a procedure, view notifications, or view pending transfers.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    public static final String TAG = MainActivity.class.toString();

    // Option menu codes
    private static final int OPTION_RELOAD_DATABASE = 0;
    private static final int OPTION_SETTINGS = 1;
	private static final int OPTION_SYNC = 2;
	
    // Activity request codes
    public static final int PICK_PROCEDURE = 0;
    public static final int PICK_SAVEDPROCEDURE = 1;
    public static final int PICK_NOTIFICATION = 2;
    public static final int RUN_PROCEDURE = 3;
    public static final int RESUME_PROCEDURE = 4;
    public static final int SETTINGS = 6;
    
    //Alert dialog codes
    private static final int DIALOG_INCORRECT_PASSWORD = 0;
	private static final int DIALOG_NO_CONNECTIVITY = 1;
	private static final int DIALOG_NO_PHONE_NAME = 2;
    
    private MainActivity context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        View openProcedure = findViewById(R.id.moca_main_procedure);
        openProcedure.setOnClickListener(this);

        View viewTransfers = findViewById(R.id.moca_main_transfers);
        viewTransfers.setOnClickListener(this);

        View viewNotifications = findViewById(R.id.moca_main_notifications);
        viewNotifications.setOnClickListener(this);
        
        // start background uploader listener
        try {
        	TelephonyManager telephony = (TelephonyManager)this.getSystemService(this.TELEPHONY_SERVICE); 
        	DataConnectionListener listener = new DataConnectionListener(context);
        	telephony.listen(listener, PhoneStateListener.LISTEN_DATA_CONNECTION_STATE); 
        	ComponentName service = startService(new Intent(MainActivity.this, BackgroundUploader.class));
        	if (service != null) {
        		Log.i(TAG, "Background uploading service started successfully.");
        	} else {
        		Log.e(TAG, "Failed to start background uploader service.");
        	}
        }
        catch (Exception e) {
        	Log.e(TAG, "Exception starting background upload service: " + e.toString());
        }
    }

    private void pickProcedure() {
    	Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(ProcedureSQLFormat.CONTENT_TYPE);
        i.setData(ProcedureSQLFormat.CONTENT_URI);
        startActivityForResult(i, PICK_PROCEDURE);
    }
    
    private void pickSavedProcedure() {
    	Intent i = new Intent(Intent.ACTION_PICK);
    	i.setType(SavedProcedureSQLFormat.CONTENT_TYPE);
    	i.setData(SavedProcedureSQLFormat.CONTENT_URI);
    	startActivityForResult(i, PICK_SAVEDPROCEDURE);
    }
    
    private void pickNotification() {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType(NotificationSQLFormat.CONTENT_TYPE);
        i.setData(NotificationSQLFormat.CONTENT_URI);
        startActivityForResult(i, PICK_NOTIFICATION);
    }
    
    public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.moca_main_procedure:
			pickProcedure();
			break;
		case R.id.moca_main_transfers:
			pickSavedProcedure();
			break;
		case R.id.moca_main_notifications:
			pickNotification();
			break;
		}
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        
        switch (resultCode) {
        case RESULT_CANCELED:
        	Log.i(TAG, "onActivityResult: requestCode=" + requestCode + " resultCode=CANCELLED");
        	if(requestCode == RUN_PROCEDURE) {
        		pickProcedure();
        	} else if(requestCode == RESUME_PROCEDURE) {
        		pickSavedProcedure();
        	} else if(requestCode == SETTINGS) {
        		//Check to make sure there is a phone number entered, otherwise will not connect to MDS
        		String phoneNum = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.PREFERENCE_PHONE_NAME, null);
        		Log.i(TAG, "phoneNum from preferences is: " + phoneNum);
        		if (phoneNum == null || phoneNum.equals("")) {
        			Log.i(TAG, "No phone number entered - showing dialog now");
        			showDialog(DIALOG_NO_PHONE_NAME);
        		}
        		// Check EMR username and password when user changes this preference
        		ContentResolver cr = context.getContentResolver();
        		if (BackgroundUploader.checkConnection(context, cr)) {
        			try {
	        			if (MDSInterface.validateCredentials(context)) {
	        				Log.i(TAG, "Username/Password for EMR correct");
	        				BackgroundUploader.credentialsChanged(true, cr);
	        			}
	        			else {
	        				Log.i(TAG, "In onActivityResult, Validate Credentials returned false - could not validate EMR username/password");
	        				BackgroundUploader.credentialsChanged(false, cr);
	        				showDialog(DIALOG_INCORRECT_PASSWORD);
	        			}
        			} catch (APIException e) {
        				Log.e(TAG, "In onActivityResult, validateCredentials() threw APIException: " + e);
        				showDialog(DIALOG_NO_CONNECTIVITY);
        			}
        		}
        		else {
        			Log.i(TAG, "Cannot validate EMR credentials - no network connectivity!");
        			showDialog(DIALOG_NO_CONNECTIVITY);
        		}
        	}
            break;
        case RESULT_OK:
        	Uri uri = null;
        	if(data != null) {
        		uri = data.getData();
        	}
        	Log.i(TAG, "onActivityResult: requestCode=" + requestCode + " resultCode=OK uri=" + (uri == null ?  "(null)" : uri.toString()));
        	
        	if(requestCode == PICK_PROCEDURE) {
        		assert(uri != null);
        		doPerformProcedure(uri);
        	} else if(requestCode == PICK_SAVEDPROCEDURE) {
        		assert(uri != null);
        		doResumeProcedure(uri);
        	} else if(requestCode == PICK_NOTIFICATION) {
        		assert(uri != null);
        		doShowNotification(uri);
        	}
            break;
        }
    }
   
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_INCORRECT_PASSWORD:
        	return new AlertDialog.Builder(MainActivity.this)
        	.setTitle("Error!")
            .setMessage("OpenMRS username/password incorrect!")
            .setPositiveButton("Change Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	// Dismiss dialog and return to settings                	
                	Intent i = new Intent(Intent.ACTION_PICK);
                    i.setClass(context, Settings.class);
                    startActivityForResult(i, SETTINGS);
                	setResult(RESULT_OK, null);
                	dialog.dismiss();
                }
            })
            .setCancelable(true)
            .setNegativeButton("Cancel", new OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					setResult(RESULT_CANCELED, null);
                	dialog.dismiss();
				}
            })
            .create();
        case DIALOG_NO_CONNECTIVITY:
        	return new AlertDialog.Builder(MainActivity.this)
        	.setTitle("Error!")
            .setMessage("Could not check username/password - no network connection.")
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	// Dismiss dialog and return to settings
                	setResult(RESULT_OK, null);
                	dialog.dismiss();
                }
            })
            .create();
        case DIALOG_NO_PHONE_NAME:
        	return new AlertDialog.Builder(MainActivity.this)
        	.setTitle("Error!")
            .setMessage("Phone Name is blank! Must enter a phone number")
            .setPositiveButton("Change Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	// Dismiss dialog and return to settings                	
                	Intent i = new Intent(Intent.ACTION_PICK);
                    i.setClass(context, Settings.class);
                    startActivityForResult(i, SETTINGS);
                	setResult(RESULT_OK, null);
                	dialog.dismiss();
                }
            })
            .setCancelable(true)
            .setNegativeButton("Cancel", new OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					setResult(RESULT_CANCELED, null);
                	dialog.dismiss();
				}
            })
            .create();
        }
        return null;
    }

    private void doShowNotification(Uri uri) {
    	try {
    		Intent i = new Intent(Intent.ACTION_VIEW, uri);
    		startActivity(i);
    	} catch(Exception e) {
    		Log.e(TAG, "While showing notification " + uri + " an exception occured: " + e.toString());
    	}
    }
    
    private void doResumeProcedure(Uri uri) {
    	try {
    		Intent i = new Intent(Intent.ACTION_VIEW, uri);
    		i.putExtra("savedProcedureUri", uri.toString());
    		startActivityForResult(i, RESUME_PROCEDURE);
    	} catch(Exception e) {
    		Log.e(TAG, "While resuming procedure " + uri + " an exception occured: " + e.toString());
    	}
    }
    
    private void doPerformProcedure(final Uri uri) {
        Log.i(TAG, "doPerformProcedure uri=" + uri.toString());
        try {
        	Intent i = new Intent(Intent.ACTION_VIEW, uri);
    		startActivityForResult(i, RUN_PROCEDURE);
        } catch (Exception e) {
            MocaUtil.errorAlert(this, e.toString());
            Log.e(TAG, "While running procedure " + uri + " an exception occured: " + e.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        menu.add(0, OPTION_RELOAD_DATABASE, 0, "Reload Database");
        menu.add(0, OPTION_SETTINGS, 1, "Settings");
		menu.add(0, OPTION_SYNC, 2, "Sync");
        return true;
    }
    
    private ProgressDialog progressDialog;
    private void doClearDatabase() {
    	if (progressDialog != null) {
    		progressDialog.dismiss();
    		progressDialog = null;
    	}
    	progressDialog = new ProgressDialog(context);
    	progressDialog.setMessage("Clearing Database");
    	progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	progressDialog.show();
    	
    	new Thread() {
    		public void run() {
    			MocaUtil.clearDatabase(context);
    	    	MocaUtil.loadDefaultDatabase(context);		
    	    	if (progressDialog != null) {
    	    		progressDialog.dismiss();
    	    		progressDialog = null;
    	    	}
    		}
    	}.start();
    }
    
    private void doUpdateProcedureDatabase() {
    	if (progressDialog != null) {
    		progressDialog.dismiss();
    		progressDialog = null;
    	}
    	progressDialog = new ProgressDialog(context);
    	progressDialog.setMessage("Updating procedure database.");
    	progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	progressDialog.show();
    	
    	new Thread() {
    		public void run() {
    			try {
    				MocaUtil.updateProcedureDatabase(context, getContentResolver());
    			} catch (APIException e) {
    				// TODO(XXX) check error code
    				Log.e(TAG, "updateProcedureDatabase threw APIException " + e);
    				showDialog(DIALOG_NO_CONNECTIVITY);
    			} finally {
    				if (progressDialog != null) {
        				progressDialog.dismiss();
        				progressDialog = null;
        			}
    			}
    		}
    	}.start();
    }
    
    private void doUpdatePatientDatabase() {
    	if (progressDialog != null) {
    		progressDialog.dismiss();
    		progressDialog = null;
    	}
    	progressDialog = new ProgressDialog(context);
    	progressDialog.setMessage("Updating patient database cache");
    	progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	progressDialog.show();
    	
    	new Thread() {
    		public void run() {
    			try {
    				MDSInterface.updatePatientDatabase(context, getContentResolver());
    			} catch (APIException e) {
    				// TODO(XXX) check error code
    				showDialog(DIALOG_NO_CONNECTIVITY);
    			} finally {
    				if (progressDialog != null) {
        				progressDialog.dismiss();
        				progressDialog = null;
        			}
    			}
    		}
    	}.start();
    }
    
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
        case OPTION_RELOAD_DATABASE:
        	AlertDialog.Builder bldr = new AlertDialog.Builder(this);
        	AlertDialog dialog = bldr.create();
        	dialog.setMessage("Warning: reloading the database will clear all saved procedures and notifications. Proceed?");
        	
        	dialog.setCancelable(true);
        	dialog.setButton("Yes", new OnClickListener() {
        		public void onClick(DialogInterface i, int v) {
        			doClearDatabase();
        		}
        	});
        	dialog.setButton2("No", (OnClickListener)null);
        	dialog.show();
            return true;
        case OPTION_SETTINGS:
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setClass(this, Settings.class);
            startActivityForResult(i, SETTINGS);
            return true;
        case OPTION_SYNC:
        	doUpdatePatientDatabase();
        	doUpdateProcedureDatabase();
			return true;
        }
        
        return false;
    }   
}