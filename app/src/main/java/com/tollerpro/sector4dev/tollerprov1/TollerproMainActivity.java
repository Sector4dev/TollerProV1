package com.tollerpro.sector4dev.tollerprov1;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;

import activity.SchedulesetsActivity;
import helper.SQLiteHandler;
import helper.SessionManager;

public class TollerproMainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    ToggleButton togglemutebttn,toggleamplibttn;

    private TextView txtName;
    private TextView txtEmail;

    private SQLiteHandler db;
    private SessionManager session;

    private RecyclerView pRecyclerView;
    private RecyclerView.LayoutManager pLayoutManager;
    private RecyclerView.Adapter pAdaper;
    private ArrayList<String> pDataset;

    public String myToken;
    public String myemail,myId;
    //private SchedulesetsActivity schdlset;
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

        //String name = user.get("username");
        myemail = user.get("email");
        myToken=user.get("token");
        myId=user.get("dbid");

        // Displaying the user details on the screen
        txtName.setText(myToken);
        txtEmail.setText(myemail);

        togglemutebttn=(ToggleButton)findViewById(R.id.buttonmute);
        togglemutebttn.setOnCheckedChangeListener(this);

        toggleamplibttn=(ToggleButton)findViewById(R.id.buttonamplifier);
        toggleamplibttn.setOnCheckedChangeListener(this);

        //Continous Timer Updation
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                TextView tdate = (TextView) findViewById(R.id.tollertimer);
                                long date = System.currentTimeMillis();
                                //SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ssa\nMM-dd-yyyy\nE");
                                SimpleDateFormat sdft = new SimpleDateFormat("hh:mm:ssa");
                                SimpleDateFormat sdfd = new SimpleDateFormat("MM-dd-yyyy");
                                SimpleDateFormat sdfdy = new SimpleDateFormat("EEEE");
                                //String dateString = sdft.format(date);
                                String dateStringT = sdft.format(date);
                                String dateStringD = sdfd.format(date);
                                String dateStringDY = sdfdy.format(date);
                                String htmlfullDT="<b>"+dateStringT+"</b><br/>"+"<small>"+dateStringD+"</small><br/><small>"+dateStringDY+"</small>";
                                Spanned strHtml= Html.fromHtml(htmlfullDT,0);
                                //tdate.setText(dateString);
                                tdate.setText(strHtml);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

        SchedulesetsActivity schdlset=new SchedulesetsActivity();

        schdlset.GetSchedules(myemail,myToken,myId,this);

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
