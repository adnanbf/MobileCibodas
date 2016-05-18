package com.example.ngapap.cibodas.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ngapap.cibodas.JSONParser;
import com.example.ngapap.cibodas.NetworkUtils;
import com.example.ngapap.cibodas.R;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by user on 27/04/2016.
 */
public class ForgetPasswordActivity extends AppCompatActivity {
    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.btn_sendEmail)
    Button _sendEmailButton;
    NetworkUtils networkUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);
        networkUtils = new NetworkUtils(getApplicationContext());
        _sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate()){
                    String email = _emailText.getText().toString();
                    ForgetPasswordAsyncTask task = new ForgetPasswordAsyncTask(ForgetPasswordActivity.this);
                    task.execute(email);
                }else onSendFailed();
            }
        });
    }

    private class ForgetPasswordAsyncTask extends AsyncTask<String,String,String>{
        private Context context;
        private ProgressDialog progressDialog;

        ForgetPasswordAsyncTask(Context context){
            this.context=context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.dialog_message_auth));
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    ForgetPasswordAsyncTask.this.cancel(true);
                }
            });
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            String myURL=getString(R.string.base_url)+"request/password";
            String email = params[0];
            String returnValue = "";
            JSONObject object = new JSONObject();
            JSONParser jsonParser = new JSONParser();
            JSONObject response=null;
            try {
                if(networkUtils.isConnectedToServer(myURL)){
                    object.put("email", email);
                    String jsonString=jsonParser.postJSON(myURL, object);
                    Log.d("ForgetPassword ", jsonString);
                    if(!jsonString.equals("DBproblem")){
                        response = new JSONObject(jsonString);
                        if(response.getBoolean("Response")){
                            returnValue ="valid";
                        }else{
                            returnValue = "invalid";
                        }
                    }
                }else{
                    returnValue = "notConnected";
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return returnValue;
        }
        @Override
        protected void onPostExecute(String s) {
            AlertDialog.Builder alert= new AlertDialog.Builder(context,R.style.AppTheme_Dark_Dialog);
            switch (s){
                case "valid":
                    alert.setTitle("Reset Password Link Has Been Sent");
                    alert.setMessage("Please Check Your Email");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toLogin();
                        }
                    });
                    alert.show();
                    break;
                case "invalid":
                    alert.setTitle("Sending Failed");
                    alert.setMessage("Email is Invalid");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    alert.show();
                    break;
                case "notConnected":
                    alert.setTitle(getString(R.string.con_problem));
                    alert.setMessage(getString(R.string.serv_problem));
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    break;
                default:
                    Log.d("ForgetPassword", s);
                    alert.setTitle(getString(R.string.db_problem));
                    alert.setMessage(getString(R.string.db_connection));
                    alert.setPositiveButton("OK", null);
                    alert.show();
            }
            progressDialog.dismiss();
            super.onPostExecute(s);
        }
    }

    private boolean validate() {
        boolean valid = true;
        String email = _emailText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("Enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        return valid;
    }

    private void onSendFailed() {
        Toast.makeText(getBaseContext(), "Please Fill Email", Toast.LENGTH_LONG).show();
        _sendEmailButton.setEnabled(true);
    }

    private void toLogin(){
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
