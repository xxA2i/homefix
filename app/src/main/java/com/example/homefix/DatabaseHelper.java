package com.example.homefix;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "HomeFix.db";
    private static final int DATABASE_VERSION = 2; // Увеличиваем версию базы данных

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_MASTERS = "masters";
    private static final String COLUMN_MASTER_ID = "id";
    private static final String COLUMN_MASTER_NAME = "name";
    private static final String COLUMN_MASTER_SPECIALTY = "specialty"; // Исправлено с speciality
    private static final String COLUMN_MASTER_RATING = "rating";
    private static final String COLUMN_MASTER_MIN_PRICE = "min_price";
    private static final String COLUMN_MASTER_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT)";

        String CREATE_MASTERS_TABLE = "CREATE TABLE " + TABLE_MASTERS + " (" +
                COLUMN_MASTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MASTER_NAME + " TEXT, " +
                COLUMN_MASTER_SPECIALTY + " TEXT, " +
                COLUMN_MASTER_RATING + " REAL, " +
                COLUMN_MASTER_MIN_PRICE + " REAL, " +
                COLUMN_MASTER_DESCRIPTION + " TEXT)";

        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_MASTERS_TABLE);
        db.execSQL("CREATE INDEX idx_specialty ON " + TABLE_MASTERS + "(" + COLUMN_MASTER_SPECIALTY + ")");
        db.execSQL("CREATE INDEX idx_name ON " + TABLE_MASTERS + "(" + COLUMN_MASTER_NAME + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Переименовываем столбец speciality в specialty
            db.execSQL("ALTER TABLE " + TABLE_MASTERS + " RENAME TO temp_masters");
            db.execSQL("CREATE TABLE " + TABLE_MASTERS + " (" +
                    COLUMN_MASTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MASTER_NAME + " TEXT, " +
                    COLUMN_MASTER_SPECIALTY + " TEXT, " +
                    COLUMN_MASTER_RATING + " REAL, " +
                    COLUMN_MASTER_MIN_PRICE + " REAL, " +
                    COLUMN_MASTER_DESCRIPTION + " TEXT)");
            db.execSQL("INSERT INTO " + TABLE_MASTERS + " (" +
                    COLUMN_MASTER_ID + ", " +
                    COLUMN_MASTER_NAME + ", " +
                    COLUMN_MASTER_SPECIALTY + ", " +
                    COLUMN_MASTER_RATING + ", " +
                    COLUMN_MASTER_MIN_PRICE + ", " +
                    COLUMN_MASTER_DESCRIPTION + ") " +
                    "SELECT " +
                    COLUMN_MASTER_ID + ", " +
                    COLUMN_MASTER_NAME + ", " +
                    "speciality AS " + COLUMN_MASTER_SPECIALTY + ", " +
                    COLUMN_MASTER_RATING + ", " +
                    COLUMN_MASTER_MIN_PRICE + ", " +
                    COLUMN_MASTER_DESCRIPTION + " FROM temp_masters");
            db.execSQL("DROP TABLE temp_masters");
            db.execSQL("CREATE INDEX idx_specialty ON " + TABLE_MASTERS + "(" + COLUMN_MASTER_SPECIALTY + ")");
            db.execSQL("CREATE INDEX idx_name ON " + TABLE_MASTERS + "(" + COLUMN_MASTER_NAME + ")");
        }
    }

    public String addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // Check if username already exists
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERNAME + " = ?", new String[]{username});
            if (cursor != null && cursor.getCount() > 0) {
                cursor.close();
                return "USERNAME_EXISTS";
            }
            if (cursor != null) {
                cursor.close();
            }

            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, username);
            values.put(COLUMN_PASSWORD, password);

            long result = db.insert(TABLE_USERS, null, values);
            return result != -1 ? "SUCCESS" : "ERROR";
        } catch (SQLiteConstraintException e) {
            return "USERNAME_EXISTS";
        } catch (Exception e) {
            return "ERROR";
        } finally {
            db.close();
        }
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                    COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?", new String[]{username, password});
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public boolean addMaster(Master master) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COLUMN_MASTER_NAME, master.getName());
            values.put(COLUMN_MASTER_SPECIALTY, master.getSpecialty());
            values.put(COLUMN_MASTER_RATING, master.getRating());
            values.put(COLUMN_MASTER_MIN_PRICE, master.getMinPrice());
            values.put(COLUMN_MASTER_DESCRIPTION, master.getDescription());

            long result = db.insert(TABLE_MASTERS, null, values);
            return result != -1;
        } finally {
            db.close();
        }
    }

    public List<Master> getAllMasters() {
        List<Master> masters = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_MASTERS, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Master master = new Master(
                            cursor.getInt(0),   // id
                            cursor.getString(1), // name
                            cursor.getString(2), // specialty
                            cursor.getFloat(3), // rating
                            cursor.getFloat(4), // min_price
                            cursor.getString(5)  // description
                    );
                    masters.add(master);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return masters;
    }
}