package com.toh.weatherboard;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RegistrationOpenHelper extends SQLiteOpenHelper {
	
	//Database name
	public static final String DATABASE_NAME = "REGISTRATION_DB";
	
	//Table names
    public static final String TABLE_SPO2 = "REGISTRATION_TABLE";
    public static final String TABLE_BLOODPRESSURE = "REGISTRATION_TABLE1";
    public static final String TABLE_BLOODSUGAR = "REGISTRATION_TABLE2";
    
    // Database Version
    public static final int VERSION = 1;
    
    //Table column names
    public static final String KEY_ID = "_id";
    public static final String WAVE = "wave";
    public static final String HR = "heartRage";
    public static final String SPO2 = "spo2";
    public static final String HP = "highPressure";
    public static final String LP = "lowPressure";
    public static final String AP = "averagePressure";
    public static final String PP = "pulse";
    public static final String BV = "bloodValue";
    public static final String BT = "bloodTemp";
    public static final String BTM = "bloodTime";
    
    public static final String SPO2_TABLE = "create table " + TABLE_SPO2 + " ("
            + KEY_ID + " integer primary key autoincrement, "
    		+ SPO2 + " text not null);";
    
//    public static final String BLOODPREUSSRE_TABLE = "create table " + TABLE_BLOODPRESSURE + " ("
//            + KEY_ID + " integer primary key autoincrement, "
//            + HP + " text not null, "
//            + LP + " text not null, "
//            + AP + " text not null, "
//            + PP + " text not null );";
// 
//    public static final String BLOODSUGAR_TABLE = "create table " + TABLE_BLOODSUGAR + " ("
//            + KEY_ID + " integer primary key autoincrement, "
//            + BV + " text not null, "
//            + BT + " text not null, "
//            + BTM + " text not null );";
 
    public RegistrationOpenHelper(Context context, String name,
            CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(SPO2_TABLE);
        //db.execSQL(BLOODPREUSSRE_TABLE);
        //db.execSQL(BLOODSUGAR_TABLE);
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    	 Log.w(RegistrationOpenHelper.class.getName(),
                 "Upgrading database from version " + oldVersion + " to "
                     + newVersion + ", which will destroy all old data");
         db.execSQL("DROP TABLE IF EXISTS" + TABLE_SPO2);
         //db.execSQL("DROP TABLE IF EXISTS" + TABLE_BLOODPRESSURE);
         //db.execSQL("DROP TABLE IF EXISTS" + TABLE_BLOODSUGAR);
         onCreate(db);

   
    }

}
