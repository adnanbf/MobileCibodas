package com.example.ngapap.cibodas.Activity;

/**
 * Created by user on 03/03/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ngapap.cibodas.JSONParser;
import com.example.ngapap.cibodas.MainActivity;
import com.example.ngapap.cibodas.Model.Customer;
import com.example.ngapap.cibodas.NetworkUtils;
import com.example.ngapap.cibodas.R;
import com.example.ngapap.cibodas.SessionManager;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    CallbackManager callbackManager;
    SessionManager session;
    NetworkUtils networkUtils;
    private String fbEmail;
    private String fbName;

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;
    @Bind(R.id.btn_login_fb) LoginButton _loginFbButton;
    @Bind(R.id.link_forgot_pass) TextView _forgotPassLink;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSDKInitialize();
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        session = new SessionManager(getApplicationContext());
        networkUtils = new NetworkUtils(getApplicationContext());
        _loginFbButton.setReadPermissions("public_profile email");
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    onLoginFailed();
                    return;
                } else {
//                    String[] params= new String[2];
                    String email = _emailText.getText().toString();
                    String pass = _passwordText.getText().toString();
                    LoginAsyncTask task = new LoginAsyncTask(LoginActivity.this);
                    task.execute(email, pass);
                }
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent i = new Intent(getApplicationContext(), SignupActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
//                finish();
            }
        });

        _forgotPassLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ForgetPasswordActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
//                finish();
            }
        });

        getLoginDetails(_loginFbButton);

    }

    //* Class Login Asynctask *\\

    private class LoginAsyncTask extends AsyncTask<String,String,String> {
//        HttpURLConnection httpURLConnection;
        private Context context;
        private ProgressDialog progressDialog;

        LoginAsyncTask(Context context){
            this.context=context;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = new ProgressDialog(context,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.dialog_message_auth));
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            Customer customer=new Customer();
            String email,pass;
            String returnValue="";
            email=params[0].toString();
            pass=params[1].toString();
            JSONObject object = new JSONObject();
            String myURL=getString(R.string.base_url)+"customers/login";
            try {
                if(networkUtils.isConnectedToServer(myURL)){
                    JSONParser jsonParser = new JSONParser();
                    object.put("email",email);
                    object.put("password", pass);
                    String request=jsonParser.postJSON(myURL, object);
                    if(!request.equals("DBproblem")){
                        JSONArray response= new JSONArray(request);
                        if(response.length()>0){
                            customer = customer.toCustomer(response);
                            if(customer.getStatus()==1){
                                session.createLoginSession(customer.getEmail(),customer.createJSONArray().toString());
                                returnValue ="valid";
                            }else{
                                returnValue = "invalid";
                            }
//                    session.createLoginSession(customer.getEmail(),jsonString);
                        }
                    }
                    Log.d("From LoginActivity", request);
                }else{
                    returnValue = "notConnected";
                }
            } catch (JSONException e) {
                e.printStackTrace();
                returnValue="null";
            }
            return returnValue;
        }

        @Override
        protected void onPostExecute(String customer) {
            super.onPostExecute(customer);
            AlertDialog.Builder alert= new AlertDialog.Builder(context,R.style.AppTheme_Dark_Dialog);
            progressDialog.dismiss();
            switch (customer){
                case "null":
                    alert.setTitle("Login Failed");
                    alert.setMessage("Invalid email or password");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    break;
                case "notConnected":
                    alert.setTitle(getString(R.string.con_problem));
                    alert.setMessage(getString(R.string.serv_problem));
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    break;
                case "valid":
                    onLoginSuccess();
                    break;
                case "invalid":
                    alert.setTitle("Login Failed");
                    alert.setMessage("Please Verify Your Account");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    break;
                default:
                    Log.d("LoginAsynctask", customer);
                    alert.setTitle(getString(R.string.db_problem));
                    alert.setMessage(getString(R.string.db_connection));
                    alert.setPositiveButton("OK", null);
                    alert.show();
            }
        }
    }

    //* Facebook Initialize *\\

    public void facebookSDKInitialize() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    protected void getLoginDetails(LoginButton login_button){

        // Callback registration
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    requestDataFB();

                }
            }

            @Override
            public void onCancel() {
                // code for cancellation
            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
            }
        });
    }

    public void requestDataFB() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                JSONObject json = response.getJSONObject();
                try {
                    if (json != null) {
                        String text = "<b>Name :</b> " + json.getString("name") + "<br><br><b>Email :</b> " + json.getString("email") + "<br><br><b>Profile link :</b> " + json.getString("link");
                        System.out.println(json.toString());
                        System.out.println(text);
                        fbEmail=json.getString("email");
                        fbName=json.getString("name");
                        session.createLoginSession(fbEmail, fbName);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
//                        System.out.println(json.getString("email"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,location");
        request.setParameters(parameters);
        request.executeAsync();
    }

    //* Other Method *\\

    private void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    private void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Please Fill Email and Password", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    private boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 8 || password.length() > 25) {
            _passwordText.setError("between 8 and 25 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}
