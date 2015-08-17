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

    public boolean isOpen()
    {
        return db.isOpen();
    }

    public long addGuardian(Guardian guardian) throws SQLiteException
    {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_GUARDIAN_NAME, guardian.getName()); // Guardian Name
        values.put(DatabaseHelper.COLUMN_PHONE_NUMBER, guardian.getPhoneNumber()); // Guardian Phone
        // Inserting Row
        try {
            return db.insertOrThrow(DatabaseHelper.TABLE_GUARDIANS, null, values);
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

    /**
     *
     * @param guardian The guardian to be deleted.
     *
     * @return number of rows deleted
     */
    public int deleteGuardian(Guardian guardian)
    {
        String whereClause = DatabaseHelper.COLUMN_PHONE_NUMBER + "=?";
        String[] whereArgs = new String[] {guardian.getPhoneNumber()};
        return db.delete(DatabaseHelper.TABLE_GUARDIANS, whereClause, whereArgs);
    }

    public int updateGuardian(String oldNumber, String newNumber, String newName)
    {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_GUARDIAN_NAME, newName); // Guardian Name
        values.put(DatabaseHelper.COLUMN_PHONE_NUMBER, newNumber); // Guardian Phone

        String whereClause = DatabaseHelper.COLUMN_PHONE_NUMBER + "=?";
        String[] whereArgs = new String[] {oldNumber};
        return db.update(DatabaseHelper.TABLE_GUARDIANS, values, whereClause, whereArgs);
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
        Cursor cursor = db.query(DatabaseHelper.TABLE_GUARDIANS, new String[]{
                        DatabaseHelper.COLUMN_GUARDIAN_NAME,
                        DatabaseHelper.COLUMN_PHONE_NUMBER},
                null, null, null, null, null, null);
        Guardian guardian = null;
        if (cursor != null) {
            cursor.moveToFirst();
            guardian = new Guardian(cursor.getString(0),
                    cursor.getString(1));
            cursor.close();
        }
        // return guardian
        return guardian; // TODO possible NULL value?
    }
}
