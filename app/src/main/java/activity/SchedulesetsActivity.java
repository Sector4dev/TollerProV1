package activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

import app.AppConfig;
import helper.SQLiteHandler;

import static android.content.ContentValues.TAG;

/**
 * Created by Sector4 Dev on 8/10/2017.
 */

public class SchedulesetsActivity extends Activity {
    private SQLiteHandler db;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public void GetSchedules(final String email, final String token, final String id,Context context) {
        RequestQueue requestQueues = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.URL_SCHEDULESETS+id, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                //Log.d("TEST", "Schedule Response: " + response.toString());
                try {
                    JSONObject schdljObj = new JSONObject(response);
                    JSONArray schdljarray= schdljObj.getJSONArray("data");
                    JSONArray timings = schdljarray.getJSONObject(0).getJSONObject("relationships").getJSONObject("timings").getJSONArray("data");
                    Log.d("TEST", "Schedule Response: " + timings.toString());

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
}
