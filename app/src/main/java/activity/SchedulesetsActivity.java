package activity;

import android.app.Activity;
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
import com.tollerpro.sector4dev.tollerprov1.TollerproMainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import helper.SQLiteHandler;

import static android.R.attr.id;
import static android.content.ContentValues.TAG;

/**
 * Created by Sector4 Dev on 8/10/2017.
 */

public class SchedulesetsActivity extends Activity {
    private SQLiteHandler db;
    private ArrayList<String> TimingE=new ArrayList<String>();
    private ArrayList<String> AssignationE=new ArrayList<String>();

    private ArrayList<String> TimingR=new ArrayList<String>();
    private ArrayList<String> AssignationR=new ArrayList<String>();
    private int[] TimingSizes,AssignSizes;

    private boolean timingstatus=false,assignstatus=false,Rtimingstatus=false,Rassignstatus=false;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public void GetExamSchedules(final String email, final String token, final String id, final Context context) {
        RequestQueue requestQueues = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.URL_EXAMSCHEDULESETS+id, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try {
                    JSONObject schdljObj = new JSONObject(response);
                    JSONArray schdljarray= schdljObj.getJSONArray("data");
                    JSONArray timings = schdljarray.getJSONObject(0).getJSONObject("relationships").getJSONObject("examtimings").getJSONArray("data");
                    JSONArray assignations = schdljarray.getJSONObject(0).getJSONObject("relationships").getJSONObject("examassignations").getJSONArray("data");

                    for (int i=0;i<timings.length();i++) {
                        TimingE.add("!");
                        getExamTimings(email, token, timings.getJSONObject(i).getString("id").toString(), context,i,timings.length());

                    }
                    for (int j=0;j<assignations.length();j++) {
                        AssignationE.add("!");
                        getExamAssignations(email, token, assignations.getJSONObject(j).getString("id").toString(), context,j,assignations.length());
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

    public void getExamTimings(final String email, final String token, final String id, final Context context, final int index, final int fullsize){
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
                                            AddingtoDB_timing_assign_Exam(context);
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

    public void getExamAssignations(final String email, final String token, final String id, final Context context, final int index, final int fullsize){
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
                                            AddingtoDB_timing_assign_Exam(context);
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

    public void GetRegularSchedules(final String email, final String token, final String id, final Context context) {
        RequestQueue requestQueues = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.URL_SCHEDULESETS+id, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try {
                    JSONObject schdljObj = new JSONObject(response);
                    JSONArray schdljarray= schdljObj.getJSONArray("data");
                    Log.d("Regular Schedules",schdljarray.toString());
                    TimingSizes=new int[schdljarray.length()];
                    AssignSizes=new int[schdljarray.length()];
                    if (schdljarray.length()>0) {
                        int TfullLength=0,AfullLength=0;
                        for (int sc=0;sc<schdljarray.length();sc++) {
                        //int sc=0;
                        //while (sc<schdljarray.length()){
                            JSONArray timings = schdljarray.getJSONObject(sc).getJSONObject("relationships").getJSONObject("timings").getJSONArray("data");
                            JSONArray assignations = schdljarray.getJSONObject(sc).getJSONObject("relationships").getJSONObject("assignations").getJSONArray("data");
                            Log.d("Schedules",timings.length()+"|"+assignations.length());
                            TimingSizes[sc]=timings.length();
                            AssignSizes[sc]=assignations.length();
                            if (timings.length()>0) {
                                for (int i = 0; i < timings.length(); i++) {
                                    TimingR.add("!");
                                    //Log.d("Timings Length",""+TimingR.size());
                                    getRegularTimings(email, token, timings.getJSONObject(i).getString("id").toString(), context, i+TfullLength, timings.length());
                                    if (i>=timings.length()-1){
                                        TfullLength+=timings.length();
                                        Log.d("Timing Length", String.valueOf(TfullLength));
                                    }
                                }
                                //sc++;
                            }else{
                                Log.d("Timings Array","Empty");
                                //sc=schdljarray.length();
                            }

                            if (assignations.length()>0) {
                                for (int j = 0; j < assignations.length(); j++) {
                                    AssignationR.add("!");
                                    //Log.d("Assignations Length",""+TimingR.size());
                                    getRegularAssignations(email, token, assignations.getJSONObject(j).getString("id").toString(), context, j+AfullLength, assignations.length());
                                    if (j>=assignations.length()-1){
                                        AfullLength+=assignations.length();
                                        Log.d("Assignation Length", String.valueOf(AfullLength));
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

    public void getRegularTimings(final String email, final String token, final String id, final Context context, final int index, final int fullsize){
        RequestQueue requestQueues = Volley.newRequestQueue(context);
        //final String[] thisTime = {""};

        StringRequest EtimingRequest=new StringRequest(Request.Method.GET, AppConfig.URL_TIMINGS+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (response!=null) {
                        JSONObject etimingjObj = new JSONObject(response);
                        String tempTime = etimingjObj.getJSONObject("data").getJSONObject("attributes").getString("time").toString();
                        String[] separated = tempTime.split("T");
                        TimingR.set(index, separated[1]);

                        if (TimingR.size() >= fullsize) {
                            int temp_count=TimingR.size();
                            Log.d("Timings size",temp_count+"");
                            for (int k = 0; k < TimingR.size(); k++) {
                                if (TimingR.get(k)=="!"){
                                    Log.d("Completed","Not Completed Timing");
                                }else
                                {
                                    Log.d("Completed","Yes Completed Timing");
                                    temp_count-=1;
                                    if (temp_count==0) {
                                        timingstatus=true;
                                        if ((timingstatus)&&(assignstatus)){
                                            timingstatus=false;
                                            assignstatus=false;
                                            AddingtoDB_timing_assign_Regular(context);
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

    public void getRegularAssignations(final String email, final String token, final String id, final Context context, final int index, final int fullsize){
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
                                        assignstatus=true;

                                        if ((timingstatus)&&(assignstatus)){
                                            timingstatus=false;
                                            assignstatus=false;
                                            AddingtoDB_timing_assign_Regular(context);
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
    private void AddingtoDB_timing_assign_Exam(Context context){
        SQLiteHandler dbHandler=new SQLiteHandler(context);
        Log.d("SQLite Status","Lets Exam schedules Push to Local Db");
        String tempT="";
        for (int k=0;k<TimingE.size();k++){
            tempT=tempT+"+"+TimingE.get(k).toString();
            //Log.d("Full Time Exam",tempT);
        }
        for (int m=0;m<AssignationE.size();m++) {
            //Log.d("Timings",tempT);
            dbHandler.addExamTimings(AssignationE.get(m).toString(),tempT,"audio");
            if (m==AssignationE.size()-1){
                ExamSetDB=true;
                if ((ExamSetDB)&&(RegularSetDB)){
                    TollerproMainActivity myActivity=new TollerproMainActivity();
                    //myActivity.FetchFromSqlite(context);

                    myActivity.pDialog.dismiss();
                }
            }
        }
    }

    private void AddingtoDB_timing_assign_Regular(Context context){
        SQLiteHandler dbHandler=new SQLiteHandler(context);
        Log.d("SQLite Status","Lets Regular schedules Push to Local Db");
        String tempT="";
        int Tindex=0,Asize=0;
        for (int q=0;q<TimingSizes.length;q++) {

            Log.d("Timing Size", String.valueOf(q));

            if (q==TimingSizes.length-1){
                Log.d("Inside","Inside");
                RegularSetDB=true;
                if ((ExamSetDB)&&(RegularSetDB)){
                    Log.d("SQLITE Fetch","Lets fetch from SQLite");
                    TollerproMainActivity myActivity=new TollerproMainActivity();
                    //myActivity.FetchFromSqlite(context);

                    myActivity.pDialog.dismiss();
                }
            }

            if (TimingSizes[q]>0) {
                for (int k = Tindex; k < (Tindex + TimingSizes[q]); k++) {
                    tempT = tempT + "+" + TimingR.get(k).toString();
                    Log.d("Full Time Regular", tempT);
                    if (k >= TimingSizes[q] - 1) {

                        for (int m = 0; m < AssignSizes[q]; m++) {
                            dbHandler.addRegularTimings(AssignationR.get(m).toString(), tempT, "audio");
                            if (m == AssignSizes[q] - 1) {
                                tempT = "";
                                /*RegularSetDB=true;
                                if ((ExamSetDB)&&(RegularSetDB)){
                                    Log.d("SQLITE Fetch","Lets fetch from SQLite");
                                    TollerproMainActivity myActivity=new TollerproMainActivity();
                                    myActivity.FetchFromSqlite();
                                }*/
                            }
                        }
                    }
                    if (k>=(Tindex + TimingSizes[q])){
                        Tindex = (Tindex + TimingSizes[q]);
                        Log.d("time string", "Emty temp" + Tindex);
                    }
                }
            }


        }

    }

}
