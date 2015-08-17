package com.pduvvur.saveme.guardian;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pduvvur.saveme.R;

public class AddModifyGuardianActivity extends AppCompatActivity
{
    public static final String OLD_GUARDIAN_NAME_KEY = "old_guardian_name";
    public static final String OLD_GUARDIAN_NUM_KEY = "old_guardian_number";

    public static final String NEW_GUARDIAN_NAME_KEY = "new_guardian_name";
    public static final String NEW_GUARDIAN_NUM_KEY = "new_guardian_number";

    public static final String TITLE_KEY = "title";
    public static final String GUARDIAN_INDEX_IN_LIST = "index";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_modify_guardian);

        // Attaching the layout to the toolbar object
        Toolbar toolbar = (Toolbar) findViewById(R.id.mod_tool_bar);
        setSupportActionBar(toolbar);
        String title = getIntent().getStringExtra(TITLE_KEY);
        getSupportActionBar().setTitle(title);

        String oldGuardianName = getIntent().getStringExtra(OLD_GUARDIAN_NAME_KEY);
        final String oldGuardianNumber = getIntent().getStringExtra(OLD_GUARDIAN_NUM_KEY);
        final int guardianIndex = getIntent().getIntExtra(GUARDIAN_INDEX_IN_LIST, -1);

        final EditText guardianNameField = (EditText) findViewById(R.id.guardian_name);
        guardianNameField.setText(oldGuardianName);
        guardianNameField.setSelection(guardianNameField.getText().length());

        final EditText guardianNumField = (EditText) findViewById(R.id.guardian_number);
        guardianNumField.setText(oldGuardianNumber);

        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean errorExists = false;

                String newGuardianName = guardianNameField.getText().toString();
                String newGuardianNumber = guardianNumField.getText().toString();
                if(newGuardianName.trim().length() == 0){
                    errorExists = true;
                    guardianNameField.setError("Name field cannot be empty!");
                }

                if(newGuardianNumber.trim().length() == 0){
                    errorExists = true;
                    guardianNumField.setError("Number field cannot be empty!");
                }

                if(!errorExists) {
                    Intent intent = new Intent();
                    intent.putExtra(NEW_GUARDIAN_NAME_KEY, newGuardianName);
                    intent.putExtra(NEW_GUARDIAN_NUM_KEY, newGuardianNumber);
                    intent.putExtra(OLD_GUARDIAN_NUM_KEY, oldGuardianNumber);
                    intent.putExtra(GUARDIAN_INDEX_IN_LIST, guardianIndex);

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_modify_guardian, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
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
