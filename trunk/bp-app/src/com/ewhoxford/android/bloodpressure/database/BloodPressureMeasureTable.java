

package com.ewhoxford.android.bloodpressure.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Convenience definitions for blood pressure provider
* @author mauro
 */
public final class BloodPressureMeasureTable {
	public static final String AUTHORITY = "com.ewhoxford.android.bloodpressure.database.bpmeasureprovider";

	// This class cannot be instantiated
	private BloodPressureMeasureTable() {
	}

	/**
	 * Notes table
	 */
	public static final class BPMeasure implements BaseColumns {
		// This class cannot be instantiated
		private BPMeasure() {
		}

		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/bpmeasure");

		/**
		 * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
		 */
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/vnd.ewhoxford.measure";

		/**
		 * The MIME type of a {@link #CONTENT_URI} sub-directory of a single
		 * note.
		 */
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/vnd.ewhoxford.measure";

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER = "created DESC";

		/**
		 * The pulse of blood pressure
		 * <P>
		 * Type: INTEGER (int)
		 * </P>
		 */
		public static final String PULSE = "pulse";

		/**
		 * The systolic pressure
		 * <P>
		 * Type: INTEGER (int)
		 * </P>
		 */
		public static final String SP = "sp";

		/**
		 * The dystolic pressure
		 * <P>
		 * Type: INTEGER (int)
		 * </P>
		 */
		public static final String DP = "dp";

		/**
		 * The note of measurement
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String NOTE = "note";

		/**
		 * The measurement file
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String MEASUREMENT_FILE = "measurement_file";
		/**
		 * true if measurement file was saved by the user, false if not
		 * <P>
		 * Type: BOOLEAN
		 * </P>
		 */
		public static final String MEASUREMENT_FILE_EXIST = "measurement_file_exist";

		/**
		 * The measurement syncronized with central server
		 * <P>
		 * Type: BOOLEAN
		 * </P>
		 */
		public static final String MEASUREMENT_FILE_SYNC = "measurement_file_sync";

		/**
		 * The timestamp for when the note was created
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String CREATED_DATE = "created";

		/**
		 * The timestamp for when the note was last modified
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String MODIFIED_DATE = "modified";

		// Column indexes int
		public static final int PULSE_COLUMN = 1;
		public static final int SP_COLUMN = 2;
		public static final int DP_COLUMN = 3;
		public static final int NOTE_COLUMN = 4;
		public static final int MEASUREMENT_FILE_COLUMN = 5;
		public static final int MEASUREMENT_FILE_EXIST_COLUMN = 6;
		public static final int MEASUREMENT_FILE_SYNC_COLUMN = 7;
		public static final int CREATED_DATE_COLUMN = 8;
		public static final int MODIFIED_DATE_COLUMN = 9;

	}
}
