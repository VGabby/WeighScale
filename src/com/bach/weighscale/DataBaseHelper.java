package com.bach.weighscale;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
	private final Context myContext;

	// The Android's default system path of your application database.
	// private static String DB_PATH = "/data/data/YOUR_PACKAGE/databases/";

	private String DB_PATH; // =
							// myContext.getApplicationContext().getPackageName()+"/databases/";

	/*
	 * private String DB_PATH = "/data/data/" +
	 * mycontext.getApplicationContext().getPackageName() + "/databases/";
	 */

	private static String DB_NAME = "qlbienban.s3db";

	private SQLiteDatabase myDataBase;
	// Bienban Table Colums names
	private static final String KEY_ID = "id";
	private static final String KEY_THOIGIAN = "thoigian";
	private static final String KEY_CAN = "can";
	private static final String KEY_TONGKHOILUONG = "tongkhoiluong";
	private static final String KEY_LAIXE = "laixe";
	private static final String KEY_BIENSOXE = "biensoxe";
	private static final String KEY_DIADIEM = "diadiem";
	private static final String KEY_NGLAMCHUNG = "nglamchung";

	// Bienban table name
	private static final String TABLE_BIENBAN = "bienban";

	/**
	 * Constructor Takes and keeps a reference of the passed context in order to
	 * access to the application assets and resources.
	 * 
	 * @param context
	 */
	@SuppressLint("SdCardPath")
	public DataBaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;
		// DB_PATH = myContext.getApplicationContext().getPackageName()
		// + "/databases/";
		DB_PATH = "/data/data/"
				+ myContext.getApplicationContext().getPackageName()
				+ "/databases/";
	}

	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * */
	public void createDataBase() throws IOException {

		boolean dbExist = checkDataBase();

		if (dbExist) {
			// do nothing - database already exist
		} else {

			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();

			try {

				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase() {

		SQLiteDatabase checkDB = null;

		try {
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e) {

			// database does't exist yet.

		}

		if (checkDB != null) {

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException {

		// Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		// Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		// transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void openDataBase() throws SQLException {

		// Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READONLY);

	}

	@Override
	public synchronized void close() {

		if (myDataBase != null)
			myDataBase.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	// Add your public helper methods to access and get content from the
	// database.
	// You could return cursors by doing "return myDataBase.query(....)" so it'd
	// be easy
	// to you to create adapters for your views.
	// Adding new contact
	void addBienBan(DuLieu BienBan) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_THOIGIAN, BienBan.getThoigian());
		values.put(KEY_CAN, BienBan.getCan());
		values.put(KEY_TONGKHOILUONG, BienBan.getTongkhoiluong());
		values.put(KEY_LAIXE, BienBan.getLaixe());
		values.put(KEY_BIENSOXE, BienBan.getBiensoxe());
		values.put(KEY_DIADIEM, BienBan.getDiadiem());
		values.put(KEY_NGLAMCHUNG, BienBan.getNglamchung());
		// Inserting Row

		db.insert(TABLE_BIENBAN, null, values);
		db.close(); // Closing database connection
	}

	// Getting single contact
	public DuLieu getBienBan(int id) {
		SQLiteDatabase db = this.getReadableDatabase();

		Cursor cursor = db.query(TABLE_BIENBAN, new String[] { KEY_ID,
				KEY_THOIGIAN, KEY_CAN, KEY_TONGKHOILUONG, KEY_LAIXE,
				KEY_BIENSOXE, KEY_DIADIEM, KEY_NGLAMCHUNG }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		DuLieu BienBan = new DuLieu(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getString(2), cursor.getString(3),
				cursor.getString(4), cursor.getString(5), cursor.getString(6),
				cursor.getString(7));
		// return contact
		return BienBan;
	}

	// Getting Bienban Count
	public int getBienbanCount() {
		String countQuery = "SELECT  * FROM " + TABLE_BIENBAN;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		// Cursor cursor = db.rawQuery(
		// "SELECT name FROM sqlite_master WHERE type='table'", null);

		int result = cursor.getCount();
		cursor.close();
		// return count
		return result;
	}
}