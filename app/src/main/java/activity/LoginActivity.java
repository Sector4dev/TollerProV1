package activity;

/**
 * Created by Sector4 Dev on 30-Jul-17.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.tollerpro.sector4dev.tollerprov1.R;
import com.tollerpro.sector4dev.tollerprov1.TollerproMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//import info.androidhive.loginandregistration.R;
import app.AppConfig;
import app.AppController;
import helper.SQLiteHandler;
import helper.SessionManager;

public class LoginActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    //private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        //btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        //db.ResetDB();

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, TollerproMainActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
            String email = inputEmail.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            // Check for empty data in the form
            if (!email.isEmpty() && !password.isEmpty()) {
                // login user
                checkLogin(email, password);
            } else {
                // Prompt user to enter credentials
                Toast.makeText(getApplicationContext(),"Please enter the credentials!", Toast.LENGTH_LONG).show();
            }
            }

        });
    }

    public void onBackPressed() {

        return;
    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
//                    JSONObject jObj2=jObj.getJSONObject("data");
//
//                    String myID = jObj2.getString("id");
//                    JSONObject jObj3=jObj2.getJSONObject("attributes");

                    session.setLogin(true);

                    // Now store the user in SQLite
                    String role = jObj.getString("role");
                    String myID = jObj.getString("user_id");

                    String token = jObj.getString("token");
                    String email = jObj.getString("email");
                    String timezone = jObj.getString("timezone");
                    String username= jObj.getString("username");
                    //String schoolname ="n";
                    //String location ="n";

                    getRegularAssignations(myID,role,token,email,timezone,username);
//                    // Inserting row in users table
//                    db.addUser(token, email, role, myID, timezone,username,schoolname,location);
//
//                    // Launch main activity
//                    Intent intent = new Intent(LoginActivity.this, TollerproMainActivity.class);
//                    startActivity(intent);
//                    finish();
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "Login Error: " +error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user[username]", email);
                params.put("user[password]", password);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(strReq);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void getRegularAssignations(final String id,final String role,final String token,final String email,final String timezone,final String username){
        RequestQueue requestQueues = Volley.newRequestQueue(this);
        //final String[] thisTime = {""};

        StringRequest FulldataRequest=new StringRequest(Request.Method.GET, AppConfig.URL_FULLDATA+id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    if (response!=null) {
                        JSONObject jObj = new JSONObject(response);
                        JSONObject jObj2=jObj.getJSONObject("data");
                        JSONObject jObj3=jObj2.getJSONObject("attributes");

                        String schoolname =jObj3.getString("nameofinstitution");
                        String location =jObj3.getString("location");

                        // Inserting row in users table
                        db.addUser(token, email, role, id, timezone,username,schoolname,location);

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this, TollerproMainActivity.class);
                        startActivity(intent);
                        finish();

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
                volleyError.printStackTrace();
                Toast.makeText(getApplicationContext(), "Login Error: " +volleyError.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String>  headers = new HashMap<String, String>();

                headers.put("Authorization","Token token="+token+","+"username="+username);

                return headers;
            }
        };
        requestQueues.add(FulldataRequest);

    }
}
