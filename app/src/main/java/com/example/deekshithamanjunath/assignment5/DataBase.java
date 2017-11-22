package com.example.deekshithamanjunath.assignment5;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by deekshithamanjunath on 4/5/17.
 */

public class DataBase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "users";
    private static final String TABLE_NAME = "User_credentials";

    private static final String NICKNAME = "_nickname";
    private static final String CITY = "city";
    private static final String LONGITUDE = "longitude";
    private static final String STATE = "state";
    private static final String YEAR = "year";
    private static final String UID = "id";
    private static final String LATITUDE = "latitude";
    private static final String TIME_STAMP = "time_stamp";
    private static final String COUNTRY = "country";

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " +TABLE_NAME+ "("
                +NICKNAME+ " TEXT PRIMARY KEY NOT NULL,"
                +CITY+ " TEXT,"
                +LONGITUDE+ " REAL NOT NULL,"
                +STATE+ " TEXT NOT NULL,"
                +YEAR+ " INTEGER NOT NULL,"
                +UID+ " INTEGER NOT NULL,"
                +LATITUDE+ " REAL NOT NULL,"
                +TIME_STAMP+ " TEXT NOT NULL,"
                +COUNTRY+ " TEXT NOT NULL);";

        db.execSQL(createTable);
    }

    public boolean addUsers(String nickName, String city, double longi, String state, int year, int id, double lat, String time, String country)
    {
        SQLiteDatabase aDB = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(NICKNAME, nickName);
        contentValues.put(CITY, city);
        contentValues.put(LONGITUDE, longi);
        contentValues.put(STATE, state);
        contentValues.put(YEAR, year);
        contentValues.put(UID, id);
        contentValues.put(LATITUDE, lat);
        contentValues.put(TIME_STAMP, time);
        contentValues.put(COUNTRY, country);

        long result = aDB.insertWithOnConflict(TABLE_NAME,null,contentValues,SQLiteDatabase.CONFLICT_IGNORE);

        if(result == -1)
        {
            return false;
        }
        else {
            return true;
        }
    }

    public Cursor sortData()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " +TABLE_NAME+ " ORDER BY ID DESC;", null);
        return data;
    }

    public Cursor filterYear(int year)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor filteredYearData = db.rawQuery("SELECT * FROM " +TABLE_NAME+ " WHERE YEAR = " +year+" ORDER BY ID DESC;",null);
        return filteredYearData;
    }

    public Cursor filterCountry(String country)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor filteredYearData = db.rawQuery("SELECT * FROM " +TABLE_NAME+ " WHERE COUNTRY = '" +country+"' ORDER BY ID DESC;",null);
        return filteredYearData;
    }

    public Cursor filterCountryState(String country, String state)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor filteredYearData = db.rawQuery("SELECT * FROM " +TABLE_NAME+ " WHERE COUNTRY = '" +country+ "' AND STATE = '" +state+ "' ORDER BY ID DESC;",null);
        return filteredYearData;
    }

    public Cursor filterCountryYear(String country, int year)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor filteredYearData = db.rawQuery("SELECT * FROM " +TABLE_NAME+ " WHERE COUNTRY = '" +country+ "' AND YEAR = " +year+ " ORDER BY ID DESC;",null);
        return filteredYearData;
    }

    public Cursor filterCountryStateYear(String country, String state, int year)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor filteredYearData = db.rawQuery("SELECT * FROM " +TABLE_NAME+ " WHERE COUNTRY = '" +country+ "' AND STATE = '" +state+ "' AND YEAR = " +year+ " ORDER BY ID DESC;",null);
        return filteredYearData;
    }

    public Cursor checkIDs(int firstId, int lastId)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor idsRange = db.rawQuery("SELECT * FROM " +TABLE_NAME+ " WHERE ID < " +lastId+ " AND ID > " +firstId+";",null);
        return idsRange;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String delete = "DELETE * FROM " + TABLE_NAME + ";" ;
        db.execSQL(delete);
        onCreate(db);
    }
}
