package com.example.ngapap.cibodas.Fragment;

import android.app.Fragment;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ngapap.cibodas.Activity.NavActivity;
import com.example.ngapap.cibodas.AsyncTask.GetCitiesTask;
import com.example.ngapap.cibodas.AsyncTask.GetProvincesTask;
import com.example.ngapap.cibodas.JSONParser;
import com.example.ngapap.cibodas.Model.Address;
import com.example.ngapap.cibodas.Model.City;
import com.example.ngapap.cibodas.Model.Customer;
import com.example.ngapap.cibodas.Model.Province;
import com.example.ngapap.cibodas.NetworkUtils;
import com.example.ngapap.cibodas.R;
import com.example.ngapap.cibodas.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 28/05/2016.
 */
public class NewAddressFragment extends Fragment {
    private LinearLayout _mainParent;
    private EditText _inputName;
    private EditText _inputStreet;
    private EditText _inputCity;
    private EditText _inputProvince;
    private EditText _inputZip;
    private EditText _inputPhone;
    private Button _btnAddress;
    private Customer customer;
    private ArrayList<Province> listProvinces;
    private Province province;
    private City city;
    private ArrayList<City> listCity;
    private int selectedProvince = 0;
    private int selectedCity = 0;
    SessionManager sessionManager;
    NetworkUtils networkUtils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_address, null);
        listCity = new ArrayList<>();
        city = new City();
        listProvinces = new ArrayList<>();
        province = new Province();
        new GetProvincesTask(getActivity(), listProvinces).execute();
        sessionManager = new SessionManager(getActivity());
        networkUtils = new NetworkUtils(getActivity());
        _mainParent = (LinearLayout) rootView.findViewById(R.id.main_parent);
        _inputName = (EditText) rootView.findViewById(R.id.input_name);
        _inputStreet = (EditText) rootView.findViewById(R.id.input_street);
        _inputCity = (EditText) rootView.findViewById(R.id.input_city);
        _inputProvince = (EditText) rootView.findViewById(R.id.input_province);
        _inputZip = (EditText) rootView.findViewById(R.id.input_zip_code);
        _inputPhone = (EditText) rootView.findViewById(R.id.input_phone);
        _btnAddress = (Button) rootView.findViewById(R.id.btn_address);
        _inputProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogProvinces(province.toArray(listProvinces));
            }
        });
        _inputCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String checkProv = _inputProvince.getText().toString();
                if(checkProv.isEmpty()){
                    Toast.makeText(getActivity(), "Select Province First", Toast.LENGTH_SHORT).show();
                }else{
                    dialogCities(city.toArray(listCity));
                }
            }
        });
        _mainParent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    ((NavActivity) getActivity()).hideKeyboard(v);
                }
            }
        });

        HashMap<String, String> user = sessionManager.getUserDetails();
        String data = user.get(SessionManager.KEY_DATA);
        customer = new Customer();
        try {
            JSONArray jsonArray = new JSONArray(data);
            customer = customer.toCustomer(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        _btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validate()) {
                    dialogInsertAddress();
                }
            }
        });
        return rootView;
    }

    private void dialogCities(CharSequence[] item){
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
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
                _inputCity.setText(selected);
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
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        final CharSequence[] items = item;
        builder.setTitle("Select Province").setCancelable(false)
                .setSingleChoiceItems(items, selectedProvince, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        selectedProvince = which;
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _inputProvince.setText(listProvinces.get(selectedProvince).getProvince());
                province = listProvinces.get(selectedProvince);
                Log.d("dialogprovince", String.valueOf(province.getProvince_id()));
                listCity = new ArrayList<>();
                new GetCitiesTask(getActivity(), province.getProvince_id(), listCity).execute();
                _inputCity.setText("");
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

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Tambah Alamat Pengiriman");
    }

    private void dialogInsertAddress() {
        final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle("Add New Address");
        dialogBuilder.setMessage("Are You Sure This Address Valid ?");
        dialogBuilder.setPositiveButton("Add Address", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = _inputName.getText().toString();
                String phone = _inputPhone.getText().toString();
                String street = _inputStreet.getText().toString();
                String city_id = String.valueOf(city.getId());
                String zip = _inputZip.getText().toString();
                InsertAddressAsyncTask task = new InsertAddressAsyncTask(getActivity());
                task.execute(customer.getId(), street, city_id, zip, customer.getApi_token(), name, phone);
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.show();

    }

    private boolean validate() {
        boolean isValid = true;
        String street = _inputStreet.getText().toString();
        String city = _inputCity.getText().toString();
        String province = _inputProvince.getText().toString();
        String zipCode = _inputZip.getText().toString();
        String name = _inputName.getText().toString();
        String phone = _inputPhone.getText().toString();

        if (street.isEmpty() || street.length() < 10 || street.length() > 50) {
            _inputStreet.setError("between 10 and 50 alphanumeric characters");
            isValid = false;
        } else {
            _inputStreet.setError(null);
        }

        if (city.isEmpty() || city.length() < 5 || city.length() > 30) {
            _inputCity.setError("between 5 and 30 alphanumeric characters");
            isValid = false;
        } else {
            _inputCity.setError(null);
        }

        if (province.isEmpty() || province.length() < 5 || province.length() > 30) {
            _inputProvince.setError("between 5 and 30 alphanumeric characters");
            isValid = false;
        } else {
            _inputProvince.setError(null);
        }

        if (zipCode.isEmpty() || zipCode.length() < 5) {
            _inputZip.setError("enter a valid zip code");
            isValid = false;
        } else {
            _inputZip.setError(null);
        }

        if (phone.isEmpty() || phone.length() < 8 || phone.length() > 15) {
            _inputPhone.setError("enter a phone number");
            isValid = false;
        } else {
            _inputPhone.setError(null);
        }

        if (name.isEmpty() || name.length() < 5) {
            _inputName.setError("at least 5 characters");
            isValid = false;
        } else {
            _inputName.setError(null);
        }

        return isValid;
    }

    private class InsertAddressAsyncTask extends AsyncTask<String, String, String> {
        private Context context;
        private ProgressDialog progressDialog;

        InsertAddressAsyncTask(Context context) {
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
            String id = params[0];
            String street = params[1];
            String city_id = params[2];
            String zip_code = params[3];
            String api_token = params[4];
            String name = params[5];
            String phone = params[6];
            String returnValue = "";
            JSONObject object = new JSONObject();
            String myURL = getString(R.string.base_url) + "customers/insertAddress?api_token=" + api_token;
            try {
                if (networkUtils.isConnectedToServer(myURL)) {
                    object.put("id_customer", id);
                    object.put("street", street);
                    object.put("city_id", city_id);
                    object.put("zip_code", zip_code);
                    object.put("name", name);
                    object.put("phone", phone);
//                    object.put("api_token", api_token);
                    JSONParser jsonParser = new JSONParser();
                    String request = jsonParser.postJSON(myURL, object);
                    if (!request.equals("DBproblem")) {
                        JSONObject response = new JSONObject(request);
                        String delivery_id = response.getString("delivery_id");
                        Address address = new Address();
                        address.setDelivery_id(delivery_id);
                        address.setStreet(street);
                        address.setmCity(city);
                        address.setmProvince(province);
                        address.setZip_code(zip_code);
                        address.setName(name);
                        address.setPhone(phone);
                        customer.getListAddresses().add(address);
                        sessionManager.createLoginSession(customer.getEmail(), customer.createJSONArray().toString());
                        returnValue = "valid";
                    }
                } else {
                    returnValue = "notConnected";
                }
                return returnValue;
            } catch (JSONException e) {
                e.printStackTrace();
                return returnValue;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog);
            switch (s) {
                case "valid":
                    alert.setTitle("Insert Success");
                    alert.setMessage("Your New Address Has Been Added");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getFragmentManager().popBackStack();

                        }
                    });
                    alert.show();
                    break;
                case "invalid":
                    alert.setTitle("Update Failed");
                    alert.setMessage("Failed to Update Address");
                    alert.setPositiveButton("OK", null);
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
