package com.example.ngapap.cibodas.Fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.ngapap.cibodas.Activity.MenuActivity;
import com.example.ngapap.cibodas.JSONParser;
import com.example.ngapap.cibodas.Model.Customer;
import com.example.ngapap.cibodas.NetworkUtils;
import com.example.ngapap.cibodas.R;
import com.example.ngapap.cibodas.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by user on 01/05/2016.
 */
public class AddressReservFragment extends Fragment {
    private EditText _inputStreet;
    private EditText _inputZipCode;
    private EditText _inputCity;
    private EditText _inputProv;
    private Button _btnAddress;
    private Button _btnPayment;
    private Customer customer;
    int mCurCheckPosition = 0;
    SessionManager sessionManager;
    NetworkUtils networkUtils;
    private FragmentTransaction ft;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_address_reserv,null);
        _inputStreet = (EditText) rootView.findViewById(R.id.input_street);
        _inputZipCode = (EditText) rootView.findViewById(R.id.input_zip_code);
        _inputCity = (EditText) rootView.findViewById(R.id.input_city);
        _inputProv = (EditText) rootView.findViewById(R.id.input_province);
        _btnAddress = (Button) rootView.findViewById(R.id.btn_address);
        _btnPayment = (Button) rootView.findViewById(R.id.btn_payment);
        sessionManager = new SessionManager(getActivity());
        networkUtils = new NetworkUtils(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetails();
        String data = user.get(SessionManager.KEY_DATA);
        customer = new Customer();
        try {
            JSONArray jsonArray = new JSONArray(data);
            customer = customer.toCustomer(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("From Fragment Address :", customer.getStreet());
        _inputStreet.setText(customer.getStreet());
        _inputStreet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    ((MenuActivity) getActivity()).hideKeyboard(v);
                }
            }
        });
        _inputCity.setText(customer.getCity());
        _inputCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    ((MenuActivity) getActivity()).hideKeyboard(v);
                }
            }
        });
        _inputProv.setText(customer.getProvince());
        _inputProv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    ((MenuActivity) getActivity()).hideKeyboard(v);
                }
            }
        });
        _inputZipCode.setText(customer.getZip_code());
        _inputZipCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    ((MenuActivity) getActivity()).hideKeyboard(v);
                }
            }
        });

        _btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String street =_inputStreet.getText().toString();
                String zip_code = _inputZipCode.getText().toString();
                String city = _inputCity.getText().toString();
                String prov = _inputProv.getText().toString();
                UpdateAddressAsyncTask task = new UpdateAddressAsyncTask(getActivity());
                task.execute(customer.getUser_id(), street, city, prov, zip_code);
            }
        });

         ft = getFragmentManager().beginTransaction();

        _btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!_inputStreet.getText().toString().equals(customer.getStreet()) ||
                        !_inputZipCode.getText().toString().equals(customer.getZip_code()) ||
                        !_inputCity.getText().toString().equals(customer.getCity()) ||
                        !_inputProv.getText().toString().equals(customer.getProvince()) ){
                    dialogAddress();
                } else {
                    ft.replace(R.id.frame, new PaymentReservFragment());
                    ft.addToBackStack(AddressReservFragment.class.getName());
                    ft.commit();

                }
            }
        });
        return rootView;
    }

    private void dialogAddress(){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
        // Setting Dialog Title
        alertDialog.setTitle("Address Has not Been Updated");
        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to Continue?");
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke YES event
                _inputStreet.setText(customer.getStreet());
                _inputZipCode.setText(customer.getZip_code());
                _inputCity.setText(customer.getCity());
                _inputProv.setText(customer.getProvince());
                ft.replace(R.id.frame, new PaymentReservFragment(), "Payment Reserve Fragment");
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", mCurCheckPosition);
    }
//
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            mCurCheckPosition = savedInstanceState.getInt("curChoice", 0);

        }
    }

    private class UpdateAddressAsyncTask extends AsyncTask<String,String,String>{
        private Context context;
        private ProgressDialog progressDialog;
        UpdateAddressAsyncTask(Context context){
            this.context = context;
        }
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.dialog_message_update));
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String id =params[0];
            String street =params[1];
            String city = params[2];
            String prov = params[3];
            String zip_code=params[4];
            String returnValue="";
            JSONObject object = new JSONObject();
            String myURL = getString(R.string.base_url)+"customers/update/address";
            try {
                if(networkUtils.isConnectedToServer(myURL)){
                    object.put("user_id",id);
                    object.put("street",street);
                    object.put("city", city);
                    object.put("province", prov);
                    object.put("zip_code",zip_code);
                    JSONParser jsonParser = new JSONParser();
                    String request =jsonParser.postJSON(myURL,object);
                    if(!request.equals("DBproblem")){
                        JSONObject response = new JSONObject(request);
                        if(response.getBoolean("Response")){
                            customer.setStreet(street);
                            customer.setZip_code(zip_code);
                            sessionManager.createLoginSession(customer.getEmail(), customer.createJSONArray().toString());
                            returnValue= "valid";
                        }else{
                            returnValue="invalid";
                        }
                    }
                }else{
                    returnValue="notConnected";
                }
                return returnValue;
            } catch (JSONException e) {
                e.printStackTrace();
                return returnValue;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            AlertDialog.Builder alert= new AlertDialog.Builder(context,R.style.AppTheme_Dark_Dialog);
            switch (s){
                case "valid":
                    alert.setTitle("Update Success");
                    alert.setMessage("Your Address Has Been Updated");
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    break;
                case "invalid":
                    alert.setTitle("Update Failed");
                    alert.setMessage("Failed to Update Address");
                    alert.setPositiveButton("OK",null);
                    alert.show();
                    break;
                case "notConnected":
                    alert.setTitle(getString(R.string.con_problem));
                    alert.setMessage(getString(R.string.serv_problem));
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    break;
                default:
                    Log.d("Address Reserve", s);
                    alert.setTitle(getString(R.string.db_problem));
                    alert.setMessage(getString(R.string.db_connection));
                    alert.setPositiveButton("OK", null);
                    alert.show();
            }
            progressDialog.dismiss();
            super.onPostExecute(s);
        }
    }
}
