package com.toh.weatherboard;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class RegistrationAdapter {

	SQLiteDatabase database_ob;
	RegistrationOpenHelper openHelper_ob;
	Context context;

	public RegistrationAdapter(Context c) {
		context = c;
	}

	public RegistrationAdapter opnToRead() {
		openHelper_ob = new RegistrationOpenHelper(context,
				openHelper_ob.DATABASE_NAME, null, openHelper_ob.VERSION);
		database_ob = openHelper_ob.getReadableDatabase();
		return this;

	}

	public RegistrationAdapter opnToWrite() {
		try {
			Log.d("3333", "#1");

			openHelper_ob = new RegistrationOpenHelper(context,
					openHelper_ob.DATABASE_NAME, null, openHelper_ob.VERSION);
			Log.d("3333", "#2");
			if (openHelper_ob == null) {
				Log.d("3333", "####");
			}
			database_ob = openHelper_ob.getWritableDatabase();
			Log.d("3333", "#3");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this;

	}

	public void Close() {
		database_ob.close();
	}

	public long insertDetails(String spo2) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(openHelper_ob.SPO2, spo2);
		// contentValues.put(openHelper_ob.HR, hr);
		// contentValues.put(openHelper_ob.WAVE, wave);
		opnToWrite();
		long val = database_ob.insert(openHelper_ob.TABLE_SPO2, null,
				contentValues);
		Close();
		return val;
	}

	// public long insertDetails1(String hp, String lp, String ap, String pp) {
	// ContentValues contentValues = new ContentValues();
	// contentValues.put(openHelper_ob.HP, hp);
	// contentValues.put(openHelper_ob.LP, lp);
	// contentValues.put(openHelper_ob.AP, ap);
	// contentValues.put(openHelper_ob.PP, pp);
	// opnToWrite();
	// long val = database_ob.insert(openHelper_ob.TABLE_BLOODPRESSURE, null,
	// contentValues);
	// Close();
	// return val;
	// }
	//
	// public long insertDetails2(String bv, String bt, String btm) {
	// ContentValues contentValues = new ContentValues();
	// contentValues.put(openHelper_ob.BV, bv);
	// contentValues.put(openHelper_ob.BT, bt);
	// contentValues.put(openHelper_ob.BTM, btm);
	// opnToWrite();
	// long val = database_ob.insert(openHelper_ob.TABLE_BLOODSUGAR, null,
	// contentValues);
	// Close();
	// return val;
	// }

	public String queryName() {
		String[] cols = { openHelper_ob.KEY_ID, openHelper_ob.SPO2 };
		opnToWrite();
		Cursor c = database_ob.query(openHelper_ob.TABLE_SPO2, cols, null,
				null, null, null, null);
		int iDate = c.getColumnIndex(openHelper_ob.SPO2);
		String str = null;
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			str = c.getString(iDate);
			Log.e("str", str);
		}
		return str;
	}

	// public Cursor queryName1() {
	// // Log.d("bbb", "#1");
	// String[] cols = { openHelper_ob.KEY_ID, openHelper_ob.HP,
	// openHelper_ob.LP, openHelper_ob.AP, openHelper_ob.PP };
	// opnToWrite();
	// // Log.d("bbb", "#2");
	// Cursor c = database_ob.query(openHelper_ob.TABLE_BLOODPRESSURE, cols,
	// null, null, null, null, null);
	// // Log.d("bbb", "#3");
	// return c;
	// }
	//
	// public Cursor queryName2() {
	// // Log.d("bbb", "#1");
	// String[] cols = { openHelper_ob.KEY_ID, openHelper_ob.BV,
	// openHelper_ob.BT, openHelper_ob.BTM };
	// opnToWrite();
	// // Log.d("bbb", "#2");
	// Cursor c = database_ob.query(openHelper_ob.TABLE_BLOODSUGAR, cols,
	// null, null, null, null, null);
	// // Log.d("bbb", "#3");
	// return c;
	// }

	public Cursor queryAll(int nameId) {
		String[] cols = { openHelper_ob.KEY_ID, openHelper_ob.SPO2 };
		opnToWrite();
		Cursor c = database_ob.query(openHelper_ob.TABLE_SPO2, cols,
				openHelper_ob.KEY_ID + "=" + nameId, null, null, null, null);

		return c;
	}

	// public Cursor queryAll1(int nameId) {
	// String[] cols = { openHelper_ob.KEY_ID, openHelper_ob.HP,
	// openHelper_ob.LP, openHelper_ob.AP, openHelper_ob.PP };
	// opnToWrite();
	// Cursor c = database_ob.query(openHelper_ob.TABLE_BLOODPRESSURE, cols,
	// openHelper_ob.KEY_ID + "=" + nameId, null, null, null, null);
	//
	// return c;
	// }
	//
	// public Cursor queryAll2(int nameId) {
	// String[] cols = { openHelper_ob.KEY_ID, openHelper_ob.BV,
	// openHelper_ob.BT, openHelper_ob.BTM };
	// opnToWrite();
	// Cursor c = database_ob.query(openHelper_ob.TABLE_BLOODSUGAR, cols,
	// openHelper_ob.KEY_ID + "=" + nameId, null, null, null, null);
	//
	// return c;
	// }

	public long updateldetail(int rowId, String spo2) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(openHelper_ob.SPO2, spo2);
		// contentValues.put(openHelper_ob.HR, hr);
		// contentValues.put(openHelper_ob.WAVE, wave);
		opnToWrite();
		long val = database_ob.update(openHelper_ob.TABLE_SPO2, contentValues,
				openHelper_ob.KEY_ID + "=" + rowId, null);
		Close();
		return val;
	}

	// public long updateldetail1(int rowId, String hp, String lp, String ap,
	// String pp) {
	// ContentValues contentValues = new ContentValues();
	// contentValues.put(openHelper_ob.HP, hp);
	// contentValues.put(openHelper_ob.LP, lp);
	// contentValues.put(openHelper_ob.AP, ap);
	// contentValues.put(openHelper_ob.PP, pp);
	// opnToWrite();
	// long val = database_ob.update(openHelper_ob.TABLE_BLOODPRESSURE,
	// contentValues, openHelper_ob.KEY_ID + "=" + rowId, null);
	// Close();
	// return val;
	// }
	//
	// public long updateldetail2(int rowId, String bv, String bt, String btm) {
	// ContentValues contentValues = new ContentValues();
	// contentValues.put(openHelper_ob.BV, bv);
	// contentValues.put(openHelper_ob.BT, bt);
	// contentValues.put(openHelper_ob.BTM, btm);
	// opnToWrite();
	// long val = database_ob.update(openHelper_ob.TABLE_BLOODSUGAR,
	// contentValues, openHelper_ob.KEY_ID + "=" + rowId, null);
	// Close();
	// return val;
	// }

	public int deletOneRecord(int rowId) {
		// TODO Auto-generated method stub
		opnToWrite();
		int val = database_ob.delete(openHelper_ob.TABLE_SPO2,
				openHelper_ob.KEY_ID + "=" + rowId, null);
		Close();
		return val;
	}

	// public int deletOneRecord1(int rowId) {
	// // TODO Auto-generated method stub
	// opnToWrite();
	// int val = database_ob.delete(openHelper_ob.TABLE_BLOODPRESSURE,
	// openHelper_ob.KEY_ID + "=" + rowId, null);
	// Close();
	// return val;
	// }
	//
	// public int deletOneRecord2(int rowId) {
	// // TODO Auto-generated method stub
	// opnToWrite();
	// int val = database_ob.delete(openHelper_ob.TABLE_BLOODSUGAR,
	// openHelper_ob.KEY_ID + "=" + rowId, null);
	// Close();
	// return val;
	// }
}
