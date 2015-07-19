package com.pduvvur.saveme.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by PradeepKumar on 7/12/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    public static final String TABLE_GUARDIANS = "guardians";
    public static final String COLUMN_ID = "ROWID";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_GUARDIAN_NAME = "name";

    private static final String DATABASE_NAME = "guardians.db";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper guardianSQLiteHelperInstance;
    private static final String TAG = "GuardianSQLiteHelper";

    // Use a singleton to ensure there is always only one instance.
    // This helps avoid leaks etc.
    public static synchronized DatabaseHelper getInstance(Context context)
    {
        // Use the application context, which will ensure that we
        // don't accidentally leak an Activity's context.
        if (guardianSQLiteHelperInstance == null) {
            guardianSQLiteHelperInstance = new DatabaseHelper(
                    context.getApplicationContext());
        }
        return guardianSQLiteHelperInstance;
    }

    private DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String create_table = "create table "
                + TABLE_GUARDIANS + "("
                + COLUMN_GUARDIAN_NAME + " text not null, "
                + COLUMN_PHONE_NUMBER + " text primary key);";

        db.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GUARDIANS);
        onCreate(db);
    }
}
