package com.pduvvur.saveme.contact;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.pduvvur.saveme.R;

public class EditGuardianActivity extends ActionBarActivity
{
    private static final int PICK_CONTACT_REQUEST = 1;
    private String m_contactName;
    private String m_contactNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_guardian);
        ImageButton addFromContactListButton = (ImageButton) findViewById(
                R.id.add_from_contact_list_button);

        addFromContactListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Shows the contact list for the user to pick a contact from.
                pickContact();
            }
        });
    }

    /**
     * Allows the user to pick a contact from the address book
     */
    private void pickContact()
    {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
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
                // columns. We do it in a worker to thread to avoid blocking the UI thread.
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
                            m_contactNumber = cursor.getString(column);
                            // Retrieve the phone number from the NUMBER column
                            column = cursor.getColumnIndex(ContactsContract.Contacts.
                                    DISPLAY_NAME);
                            m_contactName = cursor.getString(column);
                            // TODO: Store the info in the database.
                        } finally {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                    }
                }).start();
            }
        }
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
        }

        return super.onOptionsItemSelected(item);
    }
}
