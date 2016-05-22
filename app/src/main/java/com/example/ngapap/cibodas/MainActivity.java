package com.example.ngapap.cibodas;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ngapap.cibodas.Activity.CatalogActivity;
import com.example.ngapap.cibodas.Activity.NavActivity;
import com.example.ngapap.cibodas.Model.Customer;
import com.facebook.FacebookSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends NavActivity {
//    SessionManager session;
//    MenuActivity resideMenu;
    long lastPress;
    @Bind(R.id.agri_logo)
    ImageView _agriLogo;
    @Bind(R.id.tourism_logo)
    ImageView _tourismLogo;
//    @Bind(R.id.text_email) TextView _textEmail;
//    @Bind(R.id.text_name) TextView _textName;
    Customer customer ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        session = new SessionManager(getApplicationContext());
        networkUtils = new NetworkUtils(getApplicationContext());
        if (session.checkLogin())
            finish();
        HashMap<String, String> user = session.getUserDetails();
        String data = user.get(SessionManager.KEY_DATA);
        customer = new Customer();
        try {
            JSONArray jsonArray = new JSONArray(data);
//            Customer customer = new Customer();
            customer = customer.toCustomer(jsonArray);
            Log.d("Attemp Update Data User",jsonArray.toString());
            UpdateUserAsyncTask task = new UpdateUserAsyncTask(MainActivity.this);
            task.execute(customer.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        _agriLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestCatalogAsynctask task = new RequestCatalogAsynctask(MainActivity.this);
                task.execute("pertanian", customer.getApi_token());
            }
        });
        _tourismLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestCatalogAsynctask task = new RequestCatalogAsynctask(MainActivity.this);
                task.execute("pariwisata", customer.getApi_token());
            }
        });

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPress > 5000) {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_LONG).show();
            lastPress = currentTime;
        } else {
            super.onBackPressed();
        }
    }

    private class UpdateUserAsyncTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;
        private Context context;

        UpdateUserAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
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
            String returnValue = "";
            String id = params[0];
            JSONObject object = new JSONObject();
            String myURL = getString(R.string.base_url) + "customers/maintainLogin";
//            Log.d("From updateUser ",myURL);
            Customer customer = new Customer();
            try {
                if (networkUtils.isConnectedToServer(myURL)) {
                    JSONParser jsonParser = new JSONParser();
                    object.put("id_customer", id);
                    String jsonString = jsonParser.postJSON(myURL, object);
                    JSONArray jsonLogin = new JSONArray(jsonString);
                    if (jsonLogin.length() == 1) {
                        customer = customer.toCustomer(jsonLogin);
                        session.createLoginSession(customer.getEmail(), customer.createJSONArray().toString());
                        returnValue = "valid";
                    } else
                        returnValue = "invalid";
                } else
                    returnValue = "notConnected";

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return returnValue;
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.dismiss();
            switch (s) {
                case "valid":
                    Log.d("From Update User", s);
                    break;
                case "notConnected":
                    AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog);
                    alert.setTitle(getString(R.string.con_problem));
                    alert.setMessage(getString(R.string.serv_problem));
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            startActivity(getIntent());
                        }
                    });
                    alert.show();
                    break;
                default:
                    Log.d("From Update User", s);
            }
            super.onPostExecute(s);
        }
    }

    private class RequestCatalogAsynctask extends AsyncTask<String, String, String> {
        private Context context;
        private ProgressDialog progressDialog;

        //        ArrayList<Product> listCatalog= new ArrayList<Product>();
//        HttpURLConnection httpURLConnection;
        RequestCatalogAsynctask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.dialog_create_con));
            progressDialog.show();
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    RequestCatalogAsynctask.this.cancel(true);
                }
            });
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String category = params[0].toString();
            String api_token = params[1];
            String myURL = getString(R.string.base_url) + "products/catalog?api_token="+api_token+"&catalog=" + category;
            String result;
            if (networkUtils.isConnectedToServer(myURL)) {
                JSONParser jsonParser = new JSONParser();
                result = jsonParser.getJSON(myURL);
            } else {
                result = "notConnected";
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog);
            switch (result) {
                case "notConnected":
                    alert.setTitle(getString(R.string.con_problem));
                    alert.setMessage(getString(R.string.serv_problem));
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    break;
                case "DBproblem":
                    alert.setTitle(getString(R.string.db_problem));
                    alert.setMessage(getString(R.string.db_connection));
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    break;
                default:
                    Intent intent = new Intent(MainActivity.this, CatalogActivity.class);
                    intent.putExtra("catalog", result);
                    startActivity(intent);
            }
            progressDialog.dismiss();
            super.onPostExecute(result);
        }
    }

}
