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

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.LiveFolders;
import android.text.TextUtils;
import android.util.Log;

import com.ewhoxford.android.bloodpressure.BPMeasures.BPMeasure;

/**
 * Provides access to a database of BP Measures. Each BP Measure has an ID, note, SP, DP, Pulse rate,
 *  a creation date and a modified data.
 */
public class BPMeasureProvider extends ContentProvider {

	private static final String TAG = "BPMeasureProvider";

	private static final String DATABASE_NAME = "bp_measures.db";
	private static final int DATABASE_VERSION = 1;
	private static final String MEASURES_TABLE_NAME = "measures";

	private static HashMap<String, String> sBPMeasuresProjectionMap;
	private static HashMap<String, String> sLiveFolderProjectionMap;

	private static final int MEASURES = 1;
	private static final int MEASURE_ID = 2;
	private static final int LIVE_FOLDER_NOTES = 3;

	private static final UriMatcher sUriMatcher;

	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + MEASURES_TABLE_NAME + " ("
					+ BPMeasure._ID + " INTEGER PRIMARY KEY," + BPMeasure.PULSE
					+ " INTEGER," + BPMeasure.SP + " INTEGER," + BPMeasure.DP
					+ " INTEGER," + BPMeasure.NOTE + " INTEGER,"
					+ BPMeasure.CREATED_DATE + " INTEGER,"
					+ BPMeasure.MODIFIED_DATE + " INTEGER" + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}

	private DatabaseHelper mOpenHelper;

	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(MEASURES_TABLE_NAME);

		switch (sUriMatcher.match(uri)) {
		case MEASURES:
			qb.setProjectionMap(sBPMeasuresProjectionMap);
			break;

		case MEASURE_ID:
			qb.setProjectionMap(sBPMeasuresProjectionMap);
			qb.appendWhere(BPMeasure._ID + "=" + uri.getPathSegments().get(1));
			break;

		case LIVE_FOLDER_NOTES:
			qb.setProjectionMap(sLiveFolderProjectionMap);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		// If no sort order is specified use the default
		String orderBy;
		if (TextUtils.isEmpty(sortOrder)) {
			orderBy = BPMeasure.DEFAULT_SORT_ORDER;
		} else {
			orderBy = sortOrder;
		}

		// Get the database and run the query
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor c = qb.query(db, projection, selection, selectionArgs, null,
				null, orderBy);

		// Tell the cursor what uri to watch, so it knows when its source data
		// changes
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case MEASURES:
		case LIVE_FOLDER_NOTES:
			return BPMeasure.CONTENT_TYPE;

		case MEASURE_ID:
			return BPMeasure.CONTENT_ITEM_TYPE;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		// Validate the requested uri
		if (sUriMatcher.match(uri) != MEASURES) {
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}

		Long now = Long.valueOf(System.currentTimeMillis());

		// Make sure that the fields are all set
		if (values.containsKey(BPMeasures.BPMeasure.CREATED_DATE) == false) {
			values.put(BPMeasures.BPMeasure.CREATED_DATE, now);
		}

		if (values.containsKey(BPMeasures.BPMeasure.MODIFIED_DATE) == false) {
			values.put(BPMeasures.BPMeasure.MODIFIED_DATE, now);
		}

		if (values.containsKey(BPMeasures.BPMeasure.DP) == false) {
			values.put(BPMeasures.BPMeasure.DP, 0);
		}
		if (values.containsKey(BPMeasures.BPMeasure.PULSE) == false) {

			values.put(BPMeasures.BPMeasure.PULSE, 0);
		}

		if (values.containsKey(BPMeasures.BPMeasure.SP) == false) {

			values.put(BPMeasures.BPMeasure.SP, 0);
		}

		if (values.containsKey(BPMeasures.BPMeasure.MEASUREMENT_FILE) == false) {

			values.put(BPMeasures.BPMeasure.MEASUREMENT_FILE, "");
		}

		if (values.containsKey(BPMeasures.BPMeasure.NOTE) == false) {
			values.put(BPMeasures.BPMeasure.NOTE, "");
		}

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long rowId = db.insert(MEASURES_TABLE_NAME, BPMeasure.NOTE, values);
		if (rowId > 0) {
			Uri noteUri = ContentUris.withAppendedId(
					BPMeasures.BPMeasure.CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(noteUri, null);
			return noteUri;
		}

		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case MEASURES:
			count = db.delete(MEASURES_TABLE_NAME, where, whereArgs);
			break;

		case MEASURE_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.delete(MEASURES_TABLE_NAME,
					BPMeasure._ID
							+ "="
							+ noteId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count;
		switch (sUriMatcher.match(uri)) {
		case MEASURES:
			count = db.update(MEASURES_TABLE_NAME, values, where, whereArgs);
			break;

		case MEASURE_ID:
			String noteId = uri.getPathSegments().get(1);
			count = db.update(MEASURES_TABLE_NAME, values,
					BPMeasure._ID
							+ "="
							+ noteId
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ')' : ""), whereArgs);
			break;

		default:
			throw new IllegalArgumentException("Unknown URI " + uri);
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(BPMeasures.AUTHORITY, "BPMeasure", MEASURES);
		sUriMatcher.addURI(BPMeasures.AUTHORITY, "BPMeasure/#", MEASURE_ID);
		sUriMatcher.addURI(BPMeasures.AUTHORITY, "live_folders/BPMeasure",
				LIVE_FOLDER_NOTES);

		sBPMeasuresProjectionMap = new HashMap<String, String>();
		sBPMeasuresProjectionMap.put(BPMeasure._ID, BPMeasure._ID);
		sBPMeasuresProjectionMap.put(BPMeasure.SP, BPMeasure.SP);
		sBPMeasuresProjectionMap.put(BPMeasure.DP, BPMeasure.DP);
		sBPMeasuresProjectionMap.put(BPMeasure.PULSE, BPMeasure.PULSE);
		sBPMeasuresProjectionMap.put(BPMeasure.NOTE, BPMeasure.NOTE);
		sBPMeasuresProjectionMap.put(BPMeasure.CREATED_DATE,
				BPMeasure.CREATED_DATE);
		sBPMeasuresProjectionMap.put(BPMeasure.MODIFIED_DATE,
				BPMeasure.MODIFIED_DATE);

		// Support for Live Folders.
		sLiveFolderProjectionMap = new HashMap<String, String>();
		sLiveFolderProjectionMap.put(LiveFolders._ID, BPMeasure._ID + " AS "
				+ LiveFolders._ID);
		sLiveFolderProjectionMap.put(LiveFolders.NAME, BPMeasure.NOTE + " AS "
				+ LiveFolders.NAME);
		// Add more columns here for more robust Live Folders.
	}
}
