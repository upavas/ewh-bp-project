package com.ewhoxford.android.bloodpressure.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.ewhoxford.android.bloodpressure.MeasureActivity;

public class MeasureActivityTest extends
		ActivityInstrumentationTestCase2<MeasureActivity> {

	private MeasureActivity mActivity;
	private Spinner mSpinner;
	private SpinnerAdapter mPlanetData;
	public static final int ADAPTER_COUNT = 9;
	public static final int INITIAL_POSITION = 0;
	public static final int TEST_POSITION = 5;

	private String mSelection;
	private int mPos;

	public MeasureActivityTest(String pkg, Class<MeasureActivity> activityClass) {
		super(pkg, activityClass);

	}

	public MeasureActivityTest() {
		super("com.ewhoxford.android.bloodpressure", MeasureActivity.class);

	}

	// @Override
	// protected void setUp() throws Exception {
	// super.setUp();
	//
	// setActivityInitialTouchMode(false);
	//
	// mActivity = getActivity();
	//
	// mSpinner = () mActivity
	// .findViewById(com.ewhoxford.android.bloodpressure.R.id.);
	//
	// mPlanetData = mSpinner.getAdapter();
	//
	// } //
	//
	// public void testPreConditions() {
	// assertTrue(mSpinner.getOnItemSelectedListener() != null);
	// assertTrue(mPlanetData != null);
	// assertEquals(mPlanetData.getCount(), ADAPTER_COUNT);
	// } // end of testPreConditions() method definition
	//
	// public void testSpinnerUI() {
	//
	// mActivity.runOnUiThread(new Runnable() {
	// public void run() {
	// mSpinner.requestFocus();
	// mSpinner.setSelection(INITIAL_POSITION);
	// } // end of run() method definition
	// } // end of anonymous Runnable object instantiation
	// ); // end of invocation of runOnUiThread
	//
	// this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
	// for (int i = 1; i <= TEST_POSITION; i++) {
	// this.sendKeys(KeyEvent.KEYCODE_DPAD_DOWN);
	// } // end of for loop
	//
	// this.sendKeys(KeyEvent.KEYCODE_DPAD_CENTER);
	//
	// mPos = mSpinner.getSelectedItemPosition();
	// mSelection = (String) mSpinner.getItemAtPosition(mPos);
	// TextView resultView = (TextView) mActivity
	// .findViewById(com.android.example.spinner.R.id.SpinnerResult);
	//
	// String resultText = (String) resultView.getText();
	//
	// assertEquals(resultText, mSelection);
	//
	// } // end of testSpinnerUI() method definition
	//
	// public static final int TEST_STATE_DESTROY_POSITION = 2;
	// public static final String TEST_STATE_DESTROY_SELECTION = "Earth";
	//
	// public void testStateDestroy() {
	// mActivity.setSpinnerPosition(TEST_STATE_DESTROY_POSITION);
	// mActivity.setSpinnerSelection(TEST_STATE_DESTROY_SELECTION);
	// mActivity.finish();
	// mActivity = this.getActivity();
	// int currentPosition = mActivity.getSpinnerPosition();
	// String currentSelection = mActivity.getSpinnerSelection();
	// assertEquals(TEST_STATE_DESTROY_POSITION, currentPosition);
	// assertEquals(TEST_STATE_DESTROY_SELECTION, currentSelection);
	// } // end of testStateDestroy() method definition
	//
	// public static final int TEST_STATE_PAUSE_POSITION = 4;
	// public static final String TEST_STATE_PAUSE_SELECTION = "Jupiter";
	//
	// @UiThreadTest
	// public void testStatePause() {
	// Instrumentation mInstr = this.getInstrumentation();
	// mActivity.setSpinnerPosition(TEST_STATE_PAUSE_POSITION);
	// mActivity.setSpinnerSelection(TEST_STATE_PAUSE_SELECTION);
	// mInstr.callActivityOnPause(mActivity);
	// mActivity.setSpinnerPosition(0);
	// mActivity.setSpinnerSelection("");
	// mInstr.callActivityOnResume(mActivity);
	// int currentPosition = mActivity.getSpinnerPosition();
	// String currentSelection = mActivity.getSpinnerSelection();
	//
	// assertEquals(TEST_STATE_PAUSE_POSITION, currentPosition);
	// assertEquals(TEST_STATE_PAUSE_SELECTION, currentSelection);
	// } // end of testStatePause() method definition

}
