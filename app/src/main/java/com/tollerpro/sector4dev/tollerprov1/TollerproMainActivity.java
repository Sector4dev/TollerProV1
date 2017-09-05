package com.tollerpro.sector4dev.tollerprov1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import activity.LoginActivity;
import activity.SchedulesetsActivity;
import helper.SQLiteHandler;
import helper.SessionManager;

public class TollerproMainActivity extends Activity implements CompoundButton.OnCheckedChangeListener {
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

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> mDataSet;

    public ProgressDialog pDialog;
    public Context myContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tollerpro_main);

        txtName = (TextView) findViewById(R.id.textViewUsername);
        txtEmail = (TextView) findViewById(R.id.textViewPlace);

        myContext=this.getApplicationContext();

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

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

                                java.text.SimpleDateFormat sdft = new java.text.SimpleDateFormat("hh:mm:ssa");
                                java.text.SimpleDateFormat sdfd = new java.text.SimpleDateFormat("MM-dd-yyyy");
                                java.text.SimpleDateFormat sdfdy = new java.text.SimpleDateFormat("EEEE");

                                String dateStringT = sdft.format(date);
                                String dateStringD = sdfd.format(date);
                                String dateStringDY = sdfdy.format(date);
                                String htmlfullDT="<b>"+dateStringT+"</b><br/>"+"<small>"+dateStringD+"</small><br/><small>"+dateStringDY+"</small>";
                                //Spanned strHtml= Html.fromHtml(htmlfullDT,0);
                                Spanned strHtml;
                                if (Build.VERSION.SDK_INT<=23) {
                                    strHtml = Html.fromHtml(htmlfullDT);
                                }else
                                {
                                    strHtml= Html.fromHtml(htmlfullDT,0);
                                }

                                tdate.setText(strHtml);

                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();

        Intent intent = new Intent(this, LoginActivity.class);
        //Intent intent = this.getIntent();

        FetchFromSqlite(intent);

    }

    @Override
    public void onBackPressed() {

        return;
    }

    public void Restart_Fetch(Intent intent,Context contextS){
        contextS.startActivity(intent);
        //startActivity(intent);
        finish();
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void FetchFromSqlite(Intent intent){
        pDialog.setMessage("Fetching Data ...");
        showDialog();

        SchedulesetsActivity schdlset=new SchedulesetsActivity(this);
        schdlset.RegularSetDB=false;
        schdlset.ExamSetDB=false;

        Date date = new Date();
        String CurrDate = new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
        String CurrDay=new java.text.SimpleDateFormat("EEEE").format(date).toLowerCase();
        Log.d("Current Date",CurrDate+CurrDay);

        db=new SQLiteHandler(this);

        //schdlset.GetSchedules(myemail,myToken,myId,this);
        if ((!db.CheckexamScheduleAvail())&&(!db.CheckregularScheduleAvail())) {
            Log.d("Schedules","Both not exist in db");
            //Checking any exam Timings available
            if (db.CheckexamScheduleAvail()) {
                Log.d("Exam SQLite Status", "Something there in Exam schedules");
                ArrayList<String> Eschedules = db.getExamTimingDetails();
                //Log.d("Exam Schedule", String.valueOf(Eschedules));
                ArrayList<String> Edate = new ArrayList<>();
                ArrayList<String> Etimes = new ArrayList<>();
                ArrayList<String> Eaudio = new ArrayList<>();
                for (int i = 0; i < Eschedules.size(); i++) {
                    String schedule = Eschedules.get(i);
                    String[] SplitTemp = schedule.toString().trim().split("\\|");
                    ;
                    Edate.add(SplitTemp[0]);
                    Etimes.add(SplitTemp[1]);
                    Eaudio.add(SplitTemp[2]);
                    if (i >= Eschedules.size() - 1) {
                        CheckExamDay(CurrDate, Edate, Etimes, Eaudio, CurrDay);
                    }
                }
            } else {
                Log.d("Exam Schedule", "Exam schedules not available,Fetch from Server");
                schdlset.GetExamSchedules(myemail, myToken, myId, pDialog, intent);
            }

            if (db.CheckregularScheduleAvail()) {
                Log.d("Regular SQLite Status", "Regular timings available");
                //ArrayList<String> Rschedules = db.getRegularTimingDetails();
                //Log.d("Regular Schedule", String.valueOf(Rschedules));
            } else {
                Log.d("Regular Schedule", "Regular schedules not available,Fetch from Server");
                schdlset.GetRegularSchedules(myemail, myToken, myId, pDialog, intent);
            }
        }else{
            if (db.CheckexamScheduleAvail()) {
                Log.d("Exam SQLite Status", "Something there in Exam schedules");
                ArrayList<String> Eschedules = db.getExamTimingDetails();
                //Log.d("Exam Schedule", String.valueOf(Eschedules));
                ArrayList<String> Edate = new ArrayList<>();
                ArrayList<String> Etimes = new ArrayList<>();
                ArrayList<String> Eaudio = new ArrayList<>();
                for (int i = 0; i < Eschedules.size(); i++) {
                    String schedule = Eschedules.get(i);
                    String[] SplitTemp = schedule.toString().trim().split("\\|",-1);
                    ;
                    Edate.add(SplitTemp[0]);
                    Etimes.add(SplitTemp[1]);
                    Eaudio.add(SplitTemp[2]);
                    if (i >= Eschedules.size() - 1) {
                        CheckExamDay(CurrDate, Edate, Etimes, Eaudio, CurrDay);
                    }
                }
            }else {

                if (db.CheckregularScheduleAvail()) {
                    Log.d("Regular SQLite Status", "Regular timings available");
                    //ArrayList<String> Rschedules = db.getRegularTimingDetails();
                    CheckRegularday(CurrDay);
                }
            }
        }
        //hideDialog();
    }

    private void InitializeListViewTime(String ListData){
        mRecyclerView=(RecyclerView) findViewById(R.id.TimeRecyclerView);

        /*mDataSet=new ArrayList<>();
        for (int i = 0; i <50 ; i++) {
            mDataSet.add("Card No: #"+i );
        }*/
        String[] Timings=ListData.split("\\+");
        ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(Timings));
        Log.d("Corrects times", String.valueOf(ListData));
        mDataSet=stringList;

        mRecyclerView.setHasFixedSize(true);
        //mAdapter.setHasStableIds(true);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter=new MainAdapter(mDataSet);
        mRecyclerView.setAdapter(mAdapter);
        hideDialog();
    }

    private void CheckExamDay(String curDate,ArrayList<String> dbdate,ArrayList<String> dbtime,ArrayList<String> dbaudio,String curDay){
        for (int p=0;p<dbdate.size();p++){
            if (dbdate.get(p)==curDate){
                Log.d("Today",dbdate.get(p)+"- Yes");
                InitializeListViewTime(dbtime.get(p));
            }else{
                Log.d("Today",dbdate.get(p)+"- No");
                if (p==dbdate.size()-1) {
                    CheckRegularday(curDay);
                }
            }
        }

    }

    private void CheckRegularday(String curDay){
        if (db.CheckregularScheduleAvail()) {
            ArrayList<String> Rschedules = db.getRegularTimingDetails();
            ArrayList<String> Rdate=new ArrayList<>();
            ArrayList<String> Rtimes=new ArrayList<>();
            ArrayList<String> Raudio=new ArrayList<>();
            for (int i=0;i<Rschedules.size();i++){
                String schedule=Rschedules.get(i);
                String[] SplitTemp=schedule.toString().trim().split("\\|");
                Rdate.add(SplitTemp[0]);
                Rtimes.add(SplitTemp[1]);
                Raudio.add(SplitTemp[2]);
                Log.d("Regular Exists",schedule);
                if (i>=Rschedules.size()-1){
                    for (int p=0;p<Rdate.size();p++){
                        if (Rdate.get(p).equals(curDay)){
                            Log.d("Today",Rdate.get(p)+"="+curDay+"-YES");
                            InitializeListViewTime(Rtimes.get(p));
                            p=Rdate.size();

                        }else{
                            Log.d("Today",Rdate.get(p)+"="+curDay+"-NO");
                            hideDialog();
                        }
                    }
                }
            }
        }

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

    //Check if internet is present or not
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    private MediaPlayer mediaPlayer;
    private void playSounds(){
        String localUrl=getApplicationContext().getFilesDir() + File.separator + "furrow.mp3";
        Uri myUri = Uri.parse(localUrl); // initialize Uri here
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), myUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }
    private void MuteSounds(){
        if (mediaPlayer!=null){
            mediaPlayer.setVolume(0,0);
        }
    }
}
