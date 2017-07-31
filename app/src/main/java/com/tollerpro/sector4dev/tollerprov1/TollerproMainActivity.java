package com.tollerpro.sector4dev.tollerprov1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashMap;

import helper.SQLiteHandler;
import helper.SessionManager;

public class TollerproMainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    ToggleButton togglemutebttn,toggleamplibttn;

    private TextView txtName;
    private TextView txtEmail;

    private SQLiteHandler db;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tollerpro_main);

        txtName = (TextView) findViewById(R.id.textViewUsername);
        txtEmail = (TextView) findViewById(R.id.textViewPlace);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("username");
        String email = user.get("email");

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);

        togglemutebttn=(ToggleButton)findViewById(R.id.buttonmute);
        togglemutebttn.setOnCheckedChangeListener(this);

        toggleamplibttn=(ToggleButton)findViewById(R.id.buttonamplifier);
        toggleamplibttn.setOnCheckedChangeListener(this);
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(TollerproMainActivity.this, activity.LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton compund, boolean isChecked) {
        if (isChecked){
            //Toast.makeText(this,"enabed",Toast.LENGTH_SHORT).show();
        }else{
            //Toast.makeText(this,"disabled",Toast.LENGTH_SHORT).show();
        }
    }
}
