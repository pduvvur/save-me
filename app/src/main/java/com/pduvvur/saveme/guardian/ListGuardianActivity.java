package com.pduvvur.saveme.guardian;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.pduvvur.saveme.R;
import com.pduvvur.saveme.adapter.GuardianListAdapter;
import com.pduvvur.saveme.db.GuardiansDataSource;

import java.util.List;

public class ListGuardianActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener
{
    private static final int PICK_CONTACT_REQUEST = 1;
    private static final int EDIT_CONTACT_REQUEST = 2;
    private static final int ADD_CONTACT_REQUEST = 3;

    private GuardiansDataSource m_guardiansDataSource;
    private GuardianListAdapter m_adapter;
    List<Guardian> m_guardianList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_guardian);

        // Attaching the layout to the toolbar object
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        m_guardiansDataSource = new GuardiansDataSource(this);
        m_guardiansDataSource.open();

        ListView listView = (ListView) findViewById(R.id.list_guardians);
        // Queries the db to get list of guardians.
        m_guardianList = m_guardiansDataSource.getAllGuardians();
        // Create a new adapter for the listview
        m_adapter = new GuardianListAdapter(this, m_guardianList, ListGuardianActivity.this);
        listView.setAdapter(m_adapter);
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Guardian guardian = m_guardianList.get(position);
        Toast.makeText(this, guardian.toString(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, AddModifyGuardianActivity.class);
        intent.putExtra(AddModifyGuardianActivity.OLD_GUARDIAN_NAME_KEY,
                guardian.getName());
        intent.putExtra(AddModifyGuardianActivity.OLD_GUARDIAN_NUM_KEY,
                guardian.getPhoneNumber());
        intent.putExtra(AddModifyGuardianActivity.TITLE_KEY, "Edit Guardian");
        intent.putExtra(AddModifyGuardianActivity.GUARDIAN_INDEX_IN_LIST, position);

        startActivityForResult(intent, EDIT_CONTACT_REQUEST);
    }

    /**
     * Allows the user to pick a contact from the address book
     */
    private void pickContact()
    {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK,
                Uri.parse("content://contacts"));
        // Show user only contacts with phone numbers
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    /**
     * Process the result from the pickContactIntent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Check which request it is that we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                final Uri contactUri = data.getData();
                // We only need the NUMBER, DISPLAY_NAME columns.
                final String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.Contacts.DISPLAY_NAME};

                // Perform the query on the contact to get the NUMBER and DISPLAY_NAME
                // columns. We do it in a worker thread to avoid blocking the UI thread.
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Cursor cursor = null;
                        try {
                            cursor = getContentResolver()
                                    .query(contactUri, projection, null, null, null);
                            cursor.moveToFirst();

                            // Retrieve the phone number from the NUMBER column
                            int column = cursor.getColumnIndex(ContactsContract.
                                    CommonDataKinds.Phone.NUMBER);
                            String contactNumber  = cursor.getString(column);
                            // Retrieve the contact name from the DISPLAY_NAME column
                            column = cursor.getColumnIndex(ContactsContract.Contacts.
                                    DISPLAY_NAME);
                            String guardianName = cursor.getString(column);
                            addGuardian(guardianName, contactNumber);

                        } finally {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                    }
                }).start();
            }
        } else if (requestCode == EDIT_CONTACT_REQUEST) {
            // Check if it was successful
            if (resultCode == RESULT_OK) {
                String newGuardianName = data.getStringExtra(
                        AddModifyGuardianActivity.NEW_GUARDIAN_NAME_KEY);
                String oldContactNumber = data.getStringExtra(
                        AddModifyGuardianActivity.OLD_GUARDIAN_NUM_KEY);
                String newContactNumber = data.getStringExtra(
                        AddModifyGuardianActivity.NEW_GUARDIAN_NUM_KEY);

                int guardianIndex = data.getIntExtra(AddModifyGuardianActivity.GUARDIAN_INDEX_IN_LIST, -1);
                // TODO call updateGuardian method instead.
                updateGuardian(newGuardianName, oldContactNumber, newContactNumber, guardianIndex);
            }
        } else if (requestCode == ADD_CONTACT_REQUEST) {
            // Check if it was successful
            if(resultCode == RESULT_OK){
                String newGuardianName = data.getStringExtra(
                        AddModifyGuardianActivity.NEW_GUARDIAN_NAME_KEY);
                String newContactNumber = data.getStringExtra(
                        AddModifyGuardianActivity.NEW_GUARDIAN_NUM_KEY);

                addGuardian(newGuardianName, newContactNumber);
            }
        }
    }

    /**
     * Adds a guardian to the database if the count has not reached maximum
     * which is specified in the config file.
     *
     * @param guardianName Name of the guardian
     * @param contactNumber Contact number of the guardian
     */
    @SuppressWarnings("unchecked")
    private void addGuardian(String guardianName, String contactNumber)
    {
        // Check if the database is closed and open it.
        if(!m_guardiansDataSource.isOpen()){
            m_guardiansDataSource.open();
        }
        try {
//            int count = m_guardiansDataSource.getGuardianCount();
            int count = 0;
            // TODO make the count gettable from the config file.
            if(count <= 5) {
                final Guardian guardian = new Guardian(guardianName, contactNumber);
                final long rowId = m_guardiansDataSource.addGuardian(guardian);

                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(rowId != -1) {
                            m_guardianList.add(guardian);
                            m_adapter.notifyDataSetChanged();
                        }
                    }
                });
            } else {
                // TODO Build alert dialog saying max reached. tun on ui thread?
                Toast.makeText(this, "Max limit reached", Toast.LENGTH_LONG).show();
            }
        } catch (SQLiteConstraintException e){
            if(e.getMessage().contains("UNIQUE")){
                // TODO Build alert dialog saying guardian already exists.
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ListGuardianActivity.this, "Already exists", Toast.LENGTH_LONG).show();
                    }
                });
            }
        } catch (SQLiteException e){
            // TODO Build alert / toast saying unexpected error occurred run on ui thread?
            Toast.makeText(this, "Error occurred", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Updates the details of a guardian in the database.
     * @param newGuardianName  New name of the guardian
     * @param oldContactNumber Old contact number of the guardian
     * @param newContactNumber New contact number of the guardian
     * @param guardianIndex    Index of the guardian in the list.
     *
     */
    @SuppressWarnings("unchecked")
    private void updateGuardian(final String newGuardianName, String oldContactNumber,
                                final String newContactNumber, final int guardianIndex)
    {
        // Check if the database is closed and open it.
        if(!m_guardiansDataSource.isOpen()){
            m_guardiansDataSource.open();
        }
        try {
            int count = 0;
            // TODO make the count gettable from the config file.
            if(count <= 5) {
                final int numRowsAffected = m_guardiansDataSource.updateGuardian(oldContactNumber,
                        newContactNumber, newGuardianName);

                if(numRowsAffected == 1){
                    this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Guardian guardian = m_guardianList.get(guardianIndex);
                            guardian.setName(newGuardianName);
                            guardian.setNumber(newContactNumber);
                            m_adapter.notifyDataSetChanged();
                        }
                    });
                }
            } else {
                // TODO Build alert saying more than one row affected
                // This should never happen
                Toast.makeText(this, "More than one row affected", Toast.LENGTH_LONG).show();
            }
        } catch (SQLiteConstraintException e){
            if(e.getMessage().contains("UNIQUE")){
                // TODO Build alert dialog saying guardian already exists.
                Toast.makeText(this, "Already exists", Toast.LENGTH_LONG).show();
            }
        } catch (SQLiteException e){
            // TODO Build alert / toast saying unexpected error occurred
            Toast.makeText(this, "Error occurred", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause()
    {
        m_guardiansDataSource.close();
        super.onPause();
    }

    @Override
    public void onResume()
    {
        m_guardiansDataSource.open();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_guardian, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // TODO change to switch case?
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_add_guardian){
            Intent intent = new Intent(this, AddModifyGuardianActivity.class);
            intent.putExtra(AddModifyGuardianActivity.TITLE_KEY, "Add Guardian");
            startActivityForResult(intent, ADD_CONTACT_REQUEST);
        } else if(id == R.id.action_add_from_contact){
            pickContact();
        }

        return super.onOptionsItemSelected(item);
    }
}
