package com.tollerpro.sector4dev.tollerprov1;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.icu.text.SimpleDateFormat;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import activity.LoginActivity;
import activity.SchedulesetsActivity;
import helper.SQLiteHandler;
import helper.SessionManager;

import static com.tollerpro.sector4dev.tollerprov1.R.id.info;

public class TollerproMainActivity extends AppCompatActivity {
    ToggleButton togglemutebttn,toggleamplibttn;

    private TextView txtName;
    private TextView txtEmail;

    private SQLiteHandler db;
    private SessionManager session;

   /*private RecyclerView pRecyclerView;
    private RecyclerView.LayoutManager pLayoutManager;
    private RecyclerView.Adapter pAdaper;
    private ArrayList<String> pDataset;*/

    public String myToken;
    public String myemail,myId,myZone;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<String> mDataSet;

    public ProgressDialog pDialog;
    public Context myContext;

    //private int width,height;
    private PopupWindow popWindow;
    private LayoutInflater layoutInflater;
    private RelativeLayout relativeLayout;
    private Button popB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tollerpro_main);

        mHandler = new MyHandler(this);

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
        myZone=user.get("timezone");

        // Displaying the user details on the screen
        txtName.setText(myToken);
        txtEmail.setText(myemail);

        //Toggling the Mute Button
        togglemutebttn=(ToggleButton)findViewById(R.id.buttonmute);
        togglemutebttn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){
                //Toast.makeText(getApplicationContext(),"Enabled",Toast.LENGTH_SHORT).show();
                MuteSounds();
            }else{
                //Toast.makeText(getApplicationContext(),"Disabled",Toast.LENGTH_SHORT).show();
                UnMuteSounds();
            }
            }
        });

        //Toggling the Amplifier Button
        toggleamplibttn=(ToggleButton)findViewById(R.id.buttonamplifier);
        toggleamplibttn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked){

                if (usbService != null) { // if UsbService was correctly binded, Send data
                    String data = "#ampON$";
                    usbService.write(data.getBytes());
                }
            }else{
                //Toast.makeText(getApplicationContext(),"Off",Toast.LENGTH_SHORT).show();
                if (usbService != null) { // if UsbService was correctly binded, Send data
                    String data = "#ampOFF$";
                    usbService.write(data.getBytes());
                }
            }
            }
        });

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

        /*try {
            TimeConverter("02:30:00","Asia/Kolkata");
        } catch (ParseException e) {
            e.printStackTrace();
        }*/


    }

    /*
     * Notifications from UsbService will be received here.
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private UsbService usbService;
    private String AmpMsg="";
    //private TextView display;
    //private EditText editText;
    private MyHandler mHandler;
    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    private void TimeConverter(String convTime,String timeZone) throws ParseException {
        String dtc = convTime;
        java.text.SimpleDateFormat readDate = new java.text.SimpleDateFormat("HH:mm:ss");
        readDate.setTimeZone(TimeZone.getTimeZone("GMT")); // missing line
        Date date = readDate.parse(dtc);
        java.text.SimpleDateFormat writeDate = new java.text.SimpleDateFormat("HH:mm:ss");
        writeDate.setTimeZone(TimeZone.getTimeZone(timeZone));
        String s = writeDate.format(date);
        Log.d("Locale",s);
    }

    private void ProgressAnimations(ProgressBar curProgress,String fromTime,String toTime){

        ObjectAnimator animationS = ObjectAnimator.ofInt (curProgress, "progress", 0, 100); // see this max value coming back here, we animale towards that value
        animationS.setDuration (GettingProgressDuration(fromTime,toTime)); //in milliseconds
        animationS.setInterpolator (new LinearInterpolator());
        animationS.start ();
    }

    private long GettingProgressDuration(String dateStart,String dateStop){
        //Custom date format
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("HH:mm:ss");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long duration  = d2.getTime() - d1.getTime();
        return Math.abs(duration);
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

        SchedulesetsActivity schdlset=new SchedulesetsActivity(this,myZone);
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
                    String[] SplitTemp = schedule.toString().trim().split("\\|");
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

    private ArrayList<String> TodaysTimings,TodaysAudios;
    private String CurrTime;
    private void InitializeListViewTime(String ListData,String ListAudio){
        mRecyclerView=(RecyclerView) findViewById(R.id.TimeRecyclerView);

        String[] Timings=ListData.split("\\+");
        ArrayList<String> stringList = new ArrayList<String>(Arrays.asList(Timings));
        //Log.d("Corrects times", String.valueOf(ListData));

        String[] Audios=ListAudio.split("\\+");
        ArrayList<String> audioList = new ArrayList<String>(Arrays.asList(Audios));

        mDataSet=stringList;
        TodaysTimings=stringList;
        TodaysAudios=audioList;

        mRecyclerView.setHasFixedSize(true);
        //mAdapter.setHasStableIds(true);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter=new MainAdapter(mDataSet);
        mRecyclerView.setAdapter(mAdapter);
        hideDialog();

        Date date = new Date();
        CurrTime = new java.text.SimpleDateFormat("HH:mm:ss").format(date);
        //Setting up the DAY progress bar
        ProgressBar mProgressBarSmall=(ProgressBar) findViewById(R.id.progressBarDay);
        ProgressAnimations(mProgressBarSmall,CurrTime,stringList.get((stringList.size()-1)));
        //Log.d("ProgressTimes",stringList.get(0)+"|"+stringList.get((stringList.size()-1))+"||"+CurrTime);

        /*//Setting up the PERIOD progress bar
        ProgressBar mProgressBarBig=(ProgressBar) findViewById(R.id.progressBar2);
        ProgressAnimations(mProgressBarBig,CurrTime,stringList.get((stringList.size()-1)));*/
        InitializePeriodProgress();

        //Setting Recyclerview initial scroll position.
        //mRecyclerView.scrollToPosition(1);
    }

    private void InitializePeriodProgress(){
        Date date = new Date();

        //Checking for current Period
        for (int l=0;l<TodaysTimings.size();l++){
            CurrTime = new java.text.SimpleDateFormat("HH:mm:ss").format(date);
            if (GettingMillisecs(CurrTime)<GettingMillisecs(TodaysTimings.get(l))){

                //This is going to be our next time to alert
                //Setting up the PERIOD progress bar
                ProgressBar mProgressBarBig=(ProgressBar) findViewById(R.id.progressBar2);
                ProgressAnimations(mProgressBarBig,CurrTime,TodaysTimings.get(l));

                //1 min=60000 millisec=60 sec
                Alarmed=false;
                CheckingForRing(TodaysTimings.get(l),TodaysAudios.get(l));
                break;
                //l=TodaysTimings.size();
            }
        }
    }
    private boolean Alarmed=true;
    private void CheckingForRing(final String AlertTime,final String AlertAudio){
        Thread tt = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.N)
                            @Override
                            public void run() {
                                if(!Alarmed) {
                                    Date date = new Date();
                                    CurrTime = new java.text.SimpleDateFormat("HH:mm:ss").format(date);
                                    long t1 = GettingMillisecs(CurrTime);
                                    long t2 = GettingMillisecs(AlertTime);
                                    if (t1 == t2) {
                                        HideProgressPopup();
                                        //Log.d("AudioPlay", "yes: "+AlertAudio+"|"+t1+"|"+t2);
                                        playSounds(AlertAudio);
                                        Alarmed = true;
                                        /*InitializePeriodProgress();*/

                                    }else if (t1==(t2-30000)){
                                        Log.d("Alarm Progress","Show 30 Sec Progress");
                                        ShowProgressPopup();
                                    }else{
                                        Log.d("AudioPlay", "No yet: "+AlertAudio+"|"+t1+"|"+t2);
                                        //HideProgressPopup();
                                    }
                                }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        tt.start();
    }

    private long GettingMillisecs(String time){
        //Custom date format
        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("HH:mm:ss");
        Date d1 = null;
        try {
            d1 = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long duration  = d1.getTime();
        return Math.abs(duration);
    }

    private void CheckExamDay(String curDate,ArrayList<String> dbdate,ArrayList<String> dbtime,ArrayList<String> dbaudio,String curDay){
        for (int p=0;p<dbdate.size();p++){
            if (dbdate.get(p)==curDate){
                Log.d("Today",dbdate.get(p)+"- Yes");
                InitializeListViewTime(dbtime.get(p),dbaudio.get(p));
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
                            Log.d("Today",Rdate.get(p)+"="+curDay+"-YES"+"|Audios="+Raudio.get(p));
                            InitializeListViewTime(Rtimes.get(p),Raudio.get(p));
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
    private void playSounds(String fileName){
        String localUrl=getApplicationContext().getFilesDir() + File.separator + fileName;
        Uri myUri = Uri.parse(localUrl); // initialize Uri here
        //final MediaPlayer mediaPlayer;
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(getApplicationContext(), myUri);
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(
                    new MediaPlayer.OnPreparedListener() {
                        public void onPrepared(MediaPlayer player)
                        {
                            mediaPlayer.start();
                            mediaPlayer.setLooping(true);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    InitializePeriodProgress();
                }
            }
        }, 5000);
    }
    private void MuteSounds(){
        if (mediaPlayer!=null){
            mediaPlayer.setVolume(0,0);
        }
    }
    private void UnMuteSounds(){
        if (mediaPlayer!=null){
            mediaPlayer.setVolume(1,1);
        }
    }
    private void StopSounds(){
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    private void ShowProgressPopup(){
        //Toast.makeText(getApplicationContext(),"Showing Popup!",Toast.LENGTH_LONG).show();
        //popB=(Button)findViewById(R.id.buttonPopup);
        relativeLayout=(RelativeLayout)findViewById(R.id.TollerMainLayout);

        DisplayMetrics popDM=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(popDM);
        final int width=popDM.widthPixels;
        final int height=popDM.heightPixels;

        layoutInflater=(LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        ViewGroup container=(ViewGroup)layoutInflater.inflate(R.layout.popup_layout,null);

        popWindow=new PopupWindow(container,(int)(width),(int)(height),false);
        popWindow.showAtLocation(relativeLayout, Gravity.NO_GRAVITY,0,0);
    }
    private void HideProgressPopup(){
        if (popWindow!=null)
        popWindow.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);
    }

    private void HardwareReply(String msg){
        //Toast.makeText(getApplicationContext(),"HW Reply"+msg,Toast.LENGTH_LONG).show();
    }

    /*
     * This handler will be passed to UsbService. Data received from serial port is displayed through this handler
     */
    private static class MyHandler extends Handler {
        private final WeakReference<TollerproMainActivity> mActivity;

        public MyHandler(TollerproMainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    //mActivity.get().display.append(data);
                    mActivity.get().AmpMsg=data;
                    //mActivity.get().HardwareReply(data);
                    break;
            }
        }
    }
}
