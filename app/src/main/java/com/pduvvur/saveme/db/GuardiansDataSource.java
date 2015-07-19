package com.pduvvur.saveme.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.pduvvur.saveme.guardian.Guardian;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PradeepKumar on 7/12/2015.
 */
public class GuardiansDataSource
{
    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    private static final String TAG = "GuardiansDataSource";

    public GuardiansDataSource(Context context)
    {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public void open() throws SQLException
    {
        db = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }

    public long addGuardian(Guardian guardian) throws SQLiteException
    {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_GUARDIAN_NAME, guardian.getName()); // Guardian Name
        System.out.println("guardian.getName() = " + guardian.getName());
        values.put(DatabaseHelper.COLUMN_PHONE_NUMBER, guardian.getPhoneNumber()); // Guardian Phone
        System.out.println("guardian.getPhoneNumber() = " + guardian.getPhoneNumber());
        // Inserting Row
        try {
            return db.insertOrThrow(DatabaseHelper.TABLE_GUARDIANS, null, values);
            /*Cursor cursor = db.query(DatabaseHelper.TABLE_GUARDIANS,
                    new String[]{DatabaseHelper.COLUMN_GUARDIAN_NAME,
                            DatabaseHelper.COLUMN_PHONE_NUMBER},
                    DatabaseHelper.COLUMN_ID + " = " + rowId, null,
                    null, null, null);
            cursor.moveToFirst();
            Guardian insertedGuardian = getGuardianFromCursor(cursor);
            cursor.close();
            return insertedGuardian;*/
        } catch(SQLiteConstraintException dupex){
            Log.e(TAG, "Duplicate error inserting guardian with phone" +
                    "number - " + guardian.getPhoneNumber(), dupex);
            throw dupex;
        } catch(SQLiteException sqlex){
            Log.e(TAG, "Error inserting guardian with phone number " +
                    guardian.getPhoneNumber());
            throw sqlex;
        }
    }

    // Getting contacts Count
    public int getGuardianCount()
    {
        String countQuery = "select  * from " + DatabaseHelper.TABLE_GUARDIANS;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();

    }

    public List<Guardian> getAllGuardians()
    {
        List<Guardian> guardians = new ArrayList<Guardian>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_GUARDIANS, new String[]{
                        DatabaseHelper.COLUMN_GUARDIAN_NAME,
                        DatabaseHelper.COLUMN_PHONE_NUMBER},
                null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Guardian guardian = getGuardianFromCursor(cursor);
            guardians.add(guardian);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return guardians;
    }

    private Guardian getGuardianFromCursor(Cursor cursor)
    {
        String guardianName = cursor.getString(0);
        String contactNumber = cursor.getString(1);

        return new Guardian(guardianName, contactNumber);
    }
    public Guardian getGuardian(int id)
    {
        /*Cursor cursor = db.query(TABLE_GUARDIANS, new String[]{COLUMN_ID,
                        COLUMN_GUARDIAN_NAME, COLUMN_PHONE_NUMBER},
                        null, null, null, null, null, null);*/
        Cursor cursor = db.query(DatabaseHelper.TABLE_GUARDIANS, new String[]{
                        DatabaseHelper.COLUMN_GUARDIAN_NAME,
                        DatabaseHelper.COLUMN_PHONE_NUMBER},
                null, null, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
// TODO change index to 1 and 2 ?
        Guardian guardian = new Guardian( cursor.getString(0),
                cursor.getString(1));
        cursor.close();
        // return guardian
        return guardian;
    }
}
