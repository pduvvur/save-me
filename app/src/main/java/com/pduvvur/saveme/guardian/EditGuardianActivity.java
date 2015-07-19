package com.pduvvur.saveme.guardian;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.pduvvur.saveme.R;
import com.pduvvur.saveme.db.GuardiansDataSource;

public class EditGuardianActivity extends ActionBarActivity
{
    private static final int PICK_CONTACT_REQUEST = 1;
    private GuardiansDataSource m_guardiansDataSource;
    private Toolbar m_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_guardian);

        m_toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(m_toolbar);

 /*       ImageButton addFromContactListButton = (ImageButton) findViewById(
                R.id.add_from_contact_list_button);

        m_guardiansDataSource = new GuardiansDataSource(this);
        m_guardiansDataSource.open();

        List<Guardian> guardianList = m_guardiansDataSource.getAllGuardians();
        ArrayAdapter<Guardian> adapter = new ArrayAdapter<Guardian>(this,
                android.R.layout.simple_list_item_1, guardianList);
        setListAdapter(adapter);

        addFromContactListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Shows the contact list for the user to pick a contact from.
                pickContact();
            }
        });*/
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
/*        // Check which request it is that we're responding to
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
        }*/
    }

    /**
     * Adds a guardian to the database if the count has not reached maximum
     * which is specified in the config file.
     *
     * @param guardianName Name of the guardian
     * @param contactNumber Contact number of the guardian
     */
    @SuppressWarnings("unchecked")
/*    private void addGuardian(String guardianName, String contactNumber)
    {
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
                        ArrayAdapter<Guardian> adapter = (ArrayAdapter<Guardian>)
                                getListAdapter();
                        if(rowId != -1) {
                            adapter.add(guardian);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            } else {
                // TODO Build alert dialog saying max reached.
            }
        } catch (SQLiteConstraintException e){
            if(e.getMessage().contains("UNIQUE")){
                // TODO Build alert dialog saying guardian already exists.
            }
        } catch (SQLiteException e){
            // TODO Build alert / toast saying unexpected error occurred
        }
    }*/

    @Override
    public void onPause()
    {
//        m_guardiansDataSource.close();
        super.onPause();
    }

    public void onResume()
    {
//        m_guardiansDataSource.open();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_guardian, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_add_guardian){
            Toast.makeText(this, "yayy", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
