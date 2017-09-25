package activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tollerpro.sector4dev.tollerprov1.DownloadTask;
import com.tollerpro.sector4dev.tollerprov1.TollerproMainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import app.AppConfig;
import helper.SQLiteHandler;

import static android.R.attr.id;
import static android.content.ContentValues.TAG;

/**
 * Created by Sector4 Dev on 8/10/2017.
 */

public class SchedulesetsActivity {


    private SQLiteHandler db;
    private ArrayList<String> TimingE=new ArrayList<String>();
    private ArrayList<String> AssignationE=new ArrayList<String>();
    private ArrayList<String> AudiosE=new ArrayList<String>();

    private ArrayList<String> TimingR=new ArrayList<String>();
    private ArrayList<String> AssignationR=new ArrayList<String>();
    private ArrayList<String> AudiosR=new ArrayList<String>();
    private int[] TimingSizes,AssignSizes;

    private boolean timingstatus=false,assignstatus=false,Rtimingstatus=false,Rassignstatus=false,audiostatus=false;

    /*public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }*/
    private Context context;
    private String myZone;
    public SchedulesetsActivity(Context context,String Zone){
        this.context=context;
        this.myZone=Zone;
    }

    public void GetExamSchedules(final String email, final String token, final String id,final ProgressDialog pDialog,final Intent intent) {
        RequestQueue requestQueues = Volley.newRequestQueue(this.context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.URL_EXAMSCHEDULESETS+id, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try {
                    if (response!=null) {
                        JSONObject schdljObj = new JSONObject(response);
                        JSONArray schdljarray = schdljObj.getJSONArray("data");
                        /*Log.d("Exam Schedules",schdljarray.toString());*/

                        JSONArray timings = schdljarray.getJSONObject(0).getJSONObject("relationships").getJSONObject("examtimings").getJSONArray("data");
                        JSONArray assignations = schdljarray.getJSONObject(0).getJSONObject("relationships").getJSONObject("examassignations").getJSONArray("data");
                        if (timings.length() > 0) {
                            //Log.d("Exam Timings",schdljarray.toString());
                            for (int i = 0; i < timings.length(); i++) {
                                TimingE.add("!");
                                getExamTimings(email, token, timings.getJSONObject(i).getString("id").toString(), i, timings.length(), pDialog, intent);

                            }
                            for (int j = 0; j < assignations.length(); j++) {
                                AssignationE.add("!");
                                getExamAssignations(email, token, assignations.getJSONObject(j).getString("id").toString(), j, assignations.length(), pDialog, intent);
                            }
                        } else {
                            Log.d("Exam Schedules", "No Exams schedules!");
                            ExamSetDB = true;
                        }
                    }else {
                        Log.d("Exam Schedules", "No Exams schedules!");
                        ExamSetDB = true;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String>  headers = new HashMap<String, String>();

                headers.put("Authorization","Token token="+token+","+"email="+email);

                return headers;
            }
        };
        requestQueues.add(stringRequest);
    }

    public void getExamTimings(final String email, final String token, final String id, final int index, final int fullsize, final ProgressDialog pDialog,final Intent intent){
        RequestQueue requestQueues = Volley.newRequestQueue(context);
        //final String[] thisTime = {""};

        StringRequest EtimingRequest=new StringRequest(Request.Method.GET, AppConfig.URL_EXAMTIMINGS+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (response!=null) {
                        JSONObject etimingjObj = new JSONObject(response);
                        String tempTime = etimingjObj.getJSONObject("data").getJSONObject("attributes").getString("time").toString();
                        String[] separated = tempTime.split("T");
                        TimingE.set(index, separated[1]);

                        if (TimingE.size() >= fullsize) {
                            int temp_count=TimingE.size();
                            for (int k = 0; k < TimingE.size(); k++) {
                                if (TimingE.get(k)=="!"){
                                    Log.d("Completed","Not Completed Timing");
                                }else
                                {
                                    temp_count-=1;
                                    if (temp_count==0) {
                                        timingstatus=true;
                                        if ((timingstatus)&&(assignstatus)){
                                            timingstatus=false;
                                            assignstatus=false;
                                            AddingtoDB_timing_assign_Exam(pDialog,intent);
                                        }

                                    }
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse (VolleyError volleyError)
                {
                }
            })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String>  headers = new HashMap<String, String>();

                headers.put("Authorization","Token token="+token+","+"email="+email);

                return headers;
            }
        };
        requestQueues.add(EtimingRequest);

    }

    public void getExamAssignations(final String email, final String token, final String id, final int index, final int fullsize,final ProgressDialog pDialog,final Intent intent){
        RequestQueue requestQueues = Volley.newRequestQueue(context);
        //final String[] thisTime = {""};

        StringRequest EassignationRequest=new StringRequest(Request.Method.GET, AppConfig.URL_EXAMASSIGNATION+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (response!=null) {
                        JSONObject etimingjObj = new JSONObject(response);
                        String tempTime = etimingjObj.getJSONObject("data").getJSONObject("attributes").getString("time").toString();
                        String[] separated = tempTime.split("T");
                        AssignationE.set(index, separated[0]);
                        //Log.d("Timings", "ExamAssignationLength: " + AssignationE.size());
                        if (AssignationE.size() >= fullsize) {
                        /*Log.d("Fetch","Assignation Fetching:"+AssignationE.get(0));*/
                            int temp_count=AssignationE.size();
                            for (int k = 0; k < AssignationE.size(); k++) {
                                if (AssignationE.get(k)=="!"){
                                    Log.d("Completed","Not Completed assignation");
                                }else
                                {
                                    temp_count-=1;
                                    if (temp_count==0) {
                                        assignstatus=true;
                                        //Log.d("Completed", "Completed assignation");
                                        if ((timingstatus)&&(assignstatus)){
                                            timingstatus=false;
                                            assignstatus=false;
                                            AddingtoDB_timing_assign_Exam(pDialog,intent);
                                        }
                                    }
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse (VolleyError volleyError)
            {
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String>  headers = new HashMap<String, String>();

                headers.put("Authorization","Token token="+token+","+"email="+email);

                return headers;
            }
        };
        requestQueues.add(EassignationRequest);

    }

    public void GetRegularSchedules(final String email, final String token, final String id, final ProgressDialog pDialog,final Intent intent) {
        RequestQueue requestQueues = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.URL_SCHEDULESETS+id, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try {
                    JSONObject schdljObj = new JSONObject(response);
                    JSONArray schdljarray= schdljObj.getJSONArray("data");
                    //Log.d("Regular Schedules",schdljarray.toString());
                    TimingSizes=new int[schdljarray.length()];
                    AssignSizes=new int[schdljarray.length()];
                    if (schdljarray.length()>0) {
                        int TfullLength=0,AfullLength=0;
                        for (int sc=0;sc<schdljarray.length();sc++) {
                        //int sc=0;
                        //while (sc<schdljarray.length()){
                            JSONArray timings = schdljarray.getJSONObject(sc).getJSONObject("relationships").getJSONObject("timings").getJSONArray("data");
                            JSONArray assignations = schdljarray.getJSONObject(sc).getJSONObject("relationships").getJSONObject("assignations").getJSONArray("data");
                            Log.d("Schedules",timings.length()+"|"+assignations.length()+"|"+timings.toString());
                            TimingSizes[sc]=timings.length();
                            AssignSizes[sc]=assignations.length();
                            if (timings.length()>0) {
                                for (int i = 0; i < timings.length(); i++) {
                                    TimingR.add("!");
                                    AudiosR.add("!");
                                    Log.d("Timings Length",""+(i+TfullLength));
                                    getRegularTimings(email, token, timings.getJSONObject(i).getString("id").toString(), i+TfullLength, timings.length(),pDialog,intent);
                                    if (i==timings.length()-1){
                                        TfullLength+=timings.length();
                                        //Log.d("Timing Length", String.valueOf(TfullLength));
                                    }
                                }
                                //sc++;
                            }else{
                                Log.d("Timings Array","Empty");
                                RegularSetDB=true;
                                //sc=schdljarray.length();
                            }

                            if (assignations.length()>0) {
                                for (int j = 0; j < assignations.length(); j++) {
                                    AssignationR.add("!");
                                    //Log.d("Assignations Length",""+TimingR.size());
                                    getRegularAssignations(email, token, assignations.getJSONObject(j).getString("id").toString(), j+AfullLength, assignations.length(),pDialog,intent);
                                    if (j>=assignations.length()-1){
                                        AfullLength+=assignations.length();
                                        //Log.d("Assignation Length", String.valueOf(AfullLength));
                                    }
                                }
                                //sc++;
                            }else{
                                Log.d("Assignation Array","Empty");
                                //sc=schdljarray.length();
                            }


                        }
                    }else{
                        Log.d("Schedulesets","No schedulesets");
                        //RegularSetDB=true;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError volleyError)
            {
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String>  headers = new HashMap<String, String>();

                headers.put("Authorization","Token token="+token+","+"email="+email);

                return headers;
            }
        };
        requestQueues.add(stringRequest);
    }

    public void getRegularTimings(final String email, final String token, final String id,final int index, final int fullsize,final ProgressDialog pDialog,final Intent intent){
        RequestQueue requestQueues = Volley.newRequestQueue(context);
        //final String[] thisTime = {""};

        StringRequest EtimingRequest=new StringRequest(Request.Method.GET, AppConfig.URL_TIMINGS+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (response!=null) {
                        JSONObject etimingjObj = new JSONObject(response);

                        String tempTime = etimingjObj.getJSONObject("data").getJSONObject("attributes").getString("time").toString();

                        String tempAudio= etimingjObj.getJSONObject("data").getJSONObject("relationships").getJSONObject("audio").getJSONObject("data").getString("id").toString();
                        //Log.d("Timing",tempTime+"|"+index);

                        getAudios(email, token, tempAudio, index, fullsize,pDialog,intent);

                        String[] separated = tempTime.split("T");
                        TimingR.set(index, separated[1]);
                        Log.d("InsertedTime",TimingR.get(index)+"|"+index+"|"+id);

                        if (TimingR.size() >= fullsize) {
                            int temp_count=TimingR.size();
                            //Log.d("Timings size",temp_count+"");
                            for (int k = 0; k < TimingR.size(); k++) {
                                if (TimingR.get(k)=="!"){
                                    Log.d("Completed","Not Completed Timing");
                                }else
                                {
                                    Log.d("Completed","Yes Completed Timing");
                                    temp_count-=1;
                                    if (temp_count==0) {
                                        Log.d("Whole Timing",TimingR.toString());
                                        Rtimingstatus=true;
                                        if ((Rtimingstatus)&&(Rassignstatus)&&(audiostatus)){
                                            Rtimingstatus=false;
                                            Rassignstatus=false;
                                            audiostatus=false;
                                            AddingtoDB_timing_assign_Regular(pDialog,intent);
                                        }
                                    }
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse (VolleyError volleyError)
            {
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String>  headers = new HashMap<String, String>();

                headers.put("Authorization","Token token="+token+","+"email="+email);

                return headers;
            }
        };
        requestQueues.add(EtimingRequest);

    }

    public void getAudios(final String email, final String token, final String id,final int index, final int fullsize,final ProgressDialog pDialog,final Intent intent){
        RequestQueue requestQueues = Volley.newRequestQueue(context);
        //final String[] thisTime = {""};

        StringRequest audioRequest=new StringRequest(Request.Method.GET, AppConfig.URL_AUDIO+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (response!=null) {
                        JSONObject etimingjObj = new JSONObject(response);
                        String tempAudio = etimingjObj.getJSONObject("data").getJSONObject("attributes").getString("fullurl").toString();
                        String tempName=etimingjObj.getJSONObject("data").getJSONObject("attributes").getString("filename").toString();
                        //String[] separated = tempAudio.split("T");
                        AudiosR.set(index, tempAudio+"|"+tempName);
                        //Log.d("Audio file",AudiosR.get(index));

                        if (AudiosR.size() >= fullsize) {
                            int temp_count=AudiosR.size();
                            Log.d("Audios size",temp_count+"");
                            for (int k = 0; k < AudiosR.size(); k++) {
                                if (AudiosR.get(k)=="!"){
                                    Log.d("Completed","Not Completed Audios");
                                }else
                                {
                                    TollerproMainActivity tempMain=new TollerproMainActivity();
                                    String[] seperatedA=AudiosR.get(k).split("\\|");
                                    new DownloadTask(context, seperatedA[0],seperatedA[1]);
                                    /*if (tempMain.isConnectingToInternet())
                                        new DownloadTask(context, seperatedA[0],seperatedA[1]);
                                    else
                                        Log.d("Connection","Connection failed!");*/

                                    Log.d("Completed","Yes Completed Audios");
                                    temp_count-=1;
                                    if (temp_count==0) {
                                        audiostatus=true;
                                        if ((Rtimingstatus)&&(Rassignstatus)&&(audiostatus)){
                                            Rtimingstatus=false;
                                            Rassignstatus=false;
                                            audiostatus=false;
                                            AddingtoDB_timing_assign_Regular(pDialog,intent);
                                        }

                                    }
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse (VolleyError volleyError)
            {
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String>  headers = new HashMap<String, String>();

                headers.put("Authorization","Token token="+token+","+"email="+email);

                return headers;
            }
        };
        requestQueues.add(audioRequest);

    }

    public void getRegularAssignations(final String email, final String token, final String id, final int index, final int fullsize,final ProgressDialog pDialog,final Intent intent){
        RequestQueue requestQueues = Volley.newRequestQueue(context);
        //final String[] thisTime = {""};

        StringRequest EassignationRequest=new StringRequest(Request.Method.GET, AppConfig.URL_ASSIGNATIONS+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (response!=null) {
                        JSONObject etimingjObj = new JSONObject(response);
                        String tempTime = etimingjObj.getJSONObject("data").getJSONObject("attributes").getString("day").toString();
                        String[] separated = tempTime.split("T");
                        AssignationR.set(index, separated[0]);
                        //Log.d("Timings", "ExamAssignationLength: " + AssignationE.size());
                        if (AssignationR.size() >= fullsize) {
                        /*Log.d("Fetch","Assignation Fetching:"+AssignationE.get(0));*/
                            int temp_count=AssignationR.size();
                            for (int k = 0; k < AssignationR.size(); k++) {
                                if (AssignationR.get(k)=="!"){
                                    Log.d("Completed","Not Completed Assignation");
                                }else
                                {
                                    Log.d("Completed", "Yes Completed assignation"+AssignationR.get(k));
                                    temp_count-=1;
                                    if (temp_count==0) {
                                        Rassignstatus=true;

                                        if ((Rtimingstatus)&&(Rassignstatus)&&(audiostatus)){
                                            Rtimingstatus=false;
                                            Rassignstatus=false;
                                            audiostatus=false;
                                            AddingtoDB_timing_assign_Regular(pDialog,intent);
                                        }
                                    }
                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse (VolleyError volleyError)
            {
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String>  headers = new HashMap<String, String>();

                headers.put("Authorization","Token token="+token+","+"email="+email);

                return headers;
            }
        };
        requestQueues.add(EassignationRequest);

    }

    public boolean ExamSetDB=false,RegularSetDB=false;
    private void AddingtoDB_timing_assign_Exam(ProgressDialog pDialog,Intent intent){
        SQLiteHandler dbHandler=new SQLiteHandler(context);
        Log.d("SQLite Status","Lets Exam schedules Push to Local Db");
        String tempT="";
        for (int k=0;k<TimingE.size();k++){
            String[] splitedTime=TimingE.get(k).split("\\.");
            //tempT +=splitedTime[0]+"+";
            String temps="";
            try {
                //Log.d("TimeZone",myZone);
                temps=TimeConverter(splitedTime[0],myZone);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            tempT +=temps+"+";
            //tempT +=TimingE.get(k).toString()+"+";
            //Log.d("Full Time Exam",tempT);
        }
        for (int m=0;m<AssignationE.size();m++) {
            //Log.d("Timings",tempT);
            dbHandler.addExamTimings(AssignationE.get(m).toString(),tempT,"audio");
            if (m==AssignationE.size()-1){
                ExamSetDB=true;
                if ((ExamSetDB)&&(RegularSetDB)){
                    TollerproMainActivity myActivity=new TollerproMainActivity();

                    myActivity.Restart_Fetch(intent,context);

                    pDialog.dismiss();

                    //finish();
                }
            }
        }
    }

    private void AddingtoDB_timing_assign_Regular(ProgressDialog pDialog,Intent intent){
        SQLiteHandler dbHandler=new SQLiteHandler(context);
        //Log.d("SQLite Status","Lets Regular schedules Push to Local Db");
        String tempT="",tempA="";
        int Tindex=0,Aindex=0;
        for (int q=0;q<TimingSizes.length;q++) {
            //Log.d("Timing Size", String.valueOf(q));
            if (q==TimingSizes.length-1){
                //Log.d("Inside","Inside");
                RegularSetDB=true;
                if ((ExamSetDB)&&(RegularSetDB)){
                    //Log.d("SQLITE Fetch","Lets fetch from SQLite");
                    TollerproMainActivity myActivity=new TollerproMainActivity();

                    myActivity.Restart_Fetch(intent,context);

                    pDialog.dismiss();
                   //finish();
                }
            }

            if (TimingSizes[q]>0) {
                int tempTIndex=Tindex;
                for (int k = tempTIndex; k < (tempTIndex + TimingSizes[q]); k++) {
                    String[] splitedTime=TimingR.get(k).split("\\.");
                    String temps="";
                    try {
                        //Log.d("TimeZone",myZone);
                        temps=TimeConverter(splitedTime[0],myZone);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    tempT +=temps+"+";
                    //tempT +=  TimingR.get(k).toString()+"+";//+"+"+tempT;
                    String[] sepA=AudiosR.get(k).split("\\|");
                    tempA +=sepA[1]+"+";//+"+"+tempA;
                    //Log.d("Full Time Regular", tempT+"|TimingSizes"+TimingSizes[q]+"|"+Tindex);
                    if (k >= (tempTIndex + TimingSizes[q]) - 1) {
                        int tempIndex=Aindex;
                        for (int m = tempIndex; m < (AssignSizes[q]+tempIndex); m++) {
                            dbHandler.addRegularTimings(AssignationR.get(m).toString(), tempT, tempA);
                            if (m == AssignSizes[q] - 1) {
                                tempT = "";
                                tempA = "";
                                Aindex += AssignSizes[q];
                                Log.d("AssignIndex", Aindex + "");
                            }
                        }
                        Tindex += TimingSizes[q];
                    }
                    /*if (k==(Tindex + TimingSizes[q])){
                        Tindex = k;
                        //Log.d("time string", "Emty temp" + Tindex);
                    }*/
                }
            }
        }

    }

    private String TimeConverter(String convTime,String timeZone) throws ParseException {
        String dtc = convTime;
        java.text.SimpleDateFormat readDate = new java.text.SimpleDateFormat("HH:mm:ss");
        readDate.setTimeZone(TimeZone.getTimeZone("GMT")); // missing line
        Date date = readDate.parse(dtc);
        java.text.SimpleDateFormat writeDate = new java.text.SimpleDateFormat("HH:mm:ss");
        writeDate.setTimeZone(TimeZone.getTimeZone(timeZone));
        String s = writeDate.format(date);
        Log.d("Locale",s);
        return s;
    }


}
