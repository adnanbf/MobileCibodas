package com.example.ngapap.cibodas.Activity;

/**
 * Created by user on 03/03/2016.
 */

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ngapap.cibodas.AsyncTask.GetCitiesTask;
import com.example.ngapap.cibodas.AsyncTask.GetProvincesTask;
import com.example.ngapap.cibodas.JSONParser;
import com.example.ngapap.cibodas.Model.City;
import com.example.ngapap.cibodas.Model.Customer;
import com.example.ngapap.cibodas.Model.Province;
import com.example.ngapap.cibodas.NetworkUtils;
import com.example.ngapap.cibodas.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private ArrayList<Province> listProvinces;
    private Province province;
    private City city;
    private ArrayList<City> listCity;
    @Bind(R.id.input_name)
    EditText _nameText;
    @Bind(R.id.input_email)
    EditText _emailText;
    @Bind(R.id.input_street)
    EditText _streetText;
    @Bind(R.id.input_city)
    EditText _cityText;
    @Bind(R.id.input_province)
    EditText _provinceText;
    @Bind(R.id.input_zip_code)
    EditText _zipCodeText;
    @Bind(R.id.input_phone)
    EditText _phoneText;
    @Bind(R.id.radioSex)
    RadioGroup _sexRadioGroup;
    @Bind(R.id.input_password)
    EditText _passwordText;
    @Bind(R.id.input_confirm_password)
    EditText _confirmPassText;
    @Bind(R.id.btn_signup)
    Button _signupButton;
    @Bind(R.id.link_login)
    TextView _loginLink;
    RadioButton _radioSexButton;
    NetworkUtils networkUtils;
    private int selectedProvince = 0;
    private int selectedCity = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        listCity = new ArrayList<>();
        city = new City();
        listProvinces = new ArrayList<>();
        province = new Province();
        new GetProvincesTask(SignupActivity.this, listProvinces).execute();
        networkUtils = new NetworkUtils(getApplicationContext());
        _provinceText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogProvinces(province.toArray(getListProvinces()));
            }
        });
        _cityText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkProv = _provinceText.getText().toString();
                if(checkProv.isEmpty()){
                    Toast.makeText(SignupActivity.this,"Pilih Provinsi Dulu!", Toast.LENGTH_SHORT).show();
                }else{

                    dialogCities(city.toArray(listCity));
                }
            }
        });
        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate()) {
                    onSignupFailed();
                    return;
                } else {
                    String email = _emailText.getText().toString();
                    String name = _nameText.getText().toString();
                    int selectedGender = _sexRadioGroup.getCheckedRadioButtonId();
                    _radioSexButton = (RadioButton) findViewById(selectedGender);
                    String gender = _radioSexButton.getText().toString();
                    String street = _streetText.getText().toString();
                    String mCity = _cityText.getText().toString();
                    String province = _provinceText.getText().toString();
                    String zip = _zipCodeText.getText().toString();
                    String phone = _phoneText.getText().toString();
                    String password = _passwordText.getText().toString();
                    SignupAsyncTask task = new SignupAsyncTask(SignupActivity.this);
                    task.execute(email, name, gender, street, mCity, province, zip, phone, password, String.valueOf(city.getId()));
                }
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                toLogin();
            }
        });
    }

    private void dialogCities(CharSequence[] item){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SignupActivity.this);
        final CharSequence[] items = item;
        builder.setTitle("Select City").setCancelable(false)
                .setSingleChoiceItems(items, selectedCity, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        selectedCity = which;
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selected = listCity.get(selectedCity).getType()+" "+listCity.get(selectedCity).getCity();
                _cityText.setText(selected);
                city = listCity.get(selectedCity);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create();
        builder.show();
    }

    private void dialogProvinces(CharSequence[] item) {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SignupActivity.this);
        final CharSequence[] items = item;
        builder.setTitle("Select Province").setCancelable(false)
                .setSingleChoiceItems(items, selectedProvince, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        selectedProvince = which;
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _provinceText.setText(listProvinces.get(selectedProvince).getProvince());
                province = listProvinces.get(selectedProvince);
                Log.d("dialogprovince", String.valueOf(province.getProvince_id()));
                listCity = new ArrayList<>();
                new GetCitiesTask(SignupActivity.this,province.getProvince_id(), listCity).execute();
                _cityText.setText("");
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create();
        builder.show();
    }

    public ArrayList<Province> getListProvinces() {
        return listProvinces;
    }

    public void setListProvinces(ArrayList<Province> listProvinces) {
        this.listProvinces = listProvinces;
    }

    public ArrayList<City> getListCity() {
        return listCity;
    }

    public void setListCity(ArrayList<City> listCity) {
        this.listCity = listCity;
    }



//    private class GetProvincesTask extends AsyncTask<Void, Void, String> {
//        //        private ArrayList<Province> mList;
//
//    }

    //* Class Signup Asnyctask *\\
    private class SignupAsyncTask extends AsyncTask<String, String, String> {
        private Context context;
        private ProgressDialog progressDialog;

        SignupAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.dialog_create_acc));
            progressDialog.show();
            progressDialog.setCancelable(false);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String returnValue = "";
            String myURL = getString(R.string.base_url) + "customers/registration";
            Customer cust = new Customer();
            cust.setEmail(params[0]);
            cust.setName(params[1]);
            cust.setGender(params[2]);
            cust.setStreet(params[3]);
            cust.setCity(params[4]);
            cust.setProvince(params[5]);
            cust.setZip_code(params[6]);
            cust.setPhone(params[7]);
            cust.setPassword(params[8]);
            JSONObject jsonObject = cust.createJSONObject();
            JSONObject response;
            try {
                jsonObject.put("city_id", params[9]);
                if (networkUtils.isConnectedToServer(myURL)) {
                    JSONParser jsonParser = new JSONParser();
                    String request = jsonParser.postJSON(myURL, jsonObject);
                    Log.d("From Signup Activity: ", request);
                    if (!request.equals("DBproblem")) {
                        response = new JSONObject(request);
                        if (response.getBoolean("Response")) {
                            returnValue = "true";
                        } else {
                            returnValue = "false";
                        }
                    }
                } else
                    returnValue = "notConnected";
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return returnValue;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog);
            switch (s) {
                case "true":
                    alert.setTitle("Register Success");
                    alert.setMessage("Go To Login Page");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onSignupSuccess();
                            toLogin();
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
                case "false":
                    alert.setTitle("Register Failed");
                    alert.setMessage("Email Has Been Used");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onSignupFailed();
                        }
                    });
                    alert.show();
                    break;
                default:
                    alert.setTitle(getString(R.string.db_problem));
                    alert.setMessage(getString(R.string.db_connection));
                    alert.setPositiveButton("OK", null);
                    alert.show();
            }
        }
    }

    //* Other Method  *\\
    private void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    private void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Register Failed", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    private void toLogin() {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    private boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String street = _streetText.getText().toString();
        String zipCode = _zipCodeText.getText().toString();
        String city = _cityText.getText().toString();
        String province = _provinceText.getText().toString();
        String phone = _phoneText.getText().toString();
        String confirmPassword = _confirmPassText.getText().toString();

        if (name.isEmpty() || name.length() < 5) {
            _nameText.setError("at least 5 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 25 || !password.equals(confirmPassword)) {
            _passwordText.setError("Invalid Password");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (confirmPassword.isEmpty() || confirmPassword.length() < 6 || confirmPassword.length() > 25 || !confirmPassword.equals(password)) {
            _confirmPassText.setError("Invalid Password");
            valid = false;
        } else {
            _confirmPassText.setError(null);
        }


        if (street.isEmpty() || street.length() < 10 || street.length() > 50) {
            _streetText.setError("between 10 and 50 alphanumeric characters");
            valid = false;
        } else {
            _streetText.setError(null);
        }

        if (city.isEmpty() || city.length() < 5 || city.length() > 30) {
            _cityText.setError("between 5 and 30 alphanumeric characters");
            valid = false;
        } else {
            _cityText.setError(null);
        }

        if (province.isEmpty() || province.length() < 5 || province.length() > 30) {
            _provinceText.setError("between 5 and 30 alphanumeric characters");
            valid = false;
        } else {
            _provinceText.setError(null);
        }

        if (zipCode.isEmpty() || zipCode.length() < 5) {
            _zipCodeText.setError("enter a valid zip code");
            valid = false;
        } else {
            _zipCodeText.setError(null);
        }

        if (phone.isEmpty() || phone.length() < 8 || phone.length() > 15) {
            _phoneText.setError("enter a phone number");
            valid = false;
        } else {
            _phoneText.setError(null);
        }

        return valid;
    }

    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

}
