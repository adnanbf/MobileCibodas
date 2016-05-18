
package com.example.ngapap.cibodas.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ngapap.cibodas.Adapter.ReservationAdapter;
import com.example.ngapap.cibodas.Fragment.PaymentConfirmFragment;
import com.example.ngapap.cibodas.JSONParser;
import com.example.ngapap.cibodas.Model.Customer;
import com.example.ngapap.cibodas.Model.Reservation;
import com.example.ngapap.cibodas.NetworkUtils;
import com.example.ngapap.cibodas.R;
import com.example.ngapap.cibodas.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReservationActivity extends MenuActivity {
    private ArrayList<Reservation> listReservation;
    NetworkUtils networkUtils;
    @Bind(R.id.listReservation)
    ListView _listReservation;
    ReservationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        ButterKnife.bind(this);
        networkUtils = new NetworkUtils(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        listReservation = new ArrayList<>();
        HashMap<String, String> user = session.getUserDetails();
        String data = user.get(SessionManager.KEY_DATA);
        try {
            JSONArray jsonArray = new JSONArray(data);
            Customer customer = new Customer();
            customer = customer.toCustomer(jsonArray);
            GetReservationAsynctask task = new GetReservationAsynctask(ReservationActivity.this);
            task.execute(customer.getId());
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private class GetReservationAsynctask extends AsyncTask<String, String, String> {
        Context context;
        ProgressDialog progressDialog;

        GetReservationAsynctask(Context context) {
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
            String myURL = getString(R.string.base_url) + "reservation/getReservation?id_customer=" + id;
            if (networkUtils.isConnectedToServer(myURL)) {
                JSONParser jsonParser = new JSONParser();
                String jsonString = jsonParser.getJSON(myURL);
                try {
                    JSONArray response = new JSONArray(jsonString);
                    if (response.length() != 0) {
                        for (int i = 0; i < response.length(); i++) {
                            Reservation reservation = new Reservation();
                            reservation=reservation.toReservation(response, i);
                            listReservation.add(reservation);
                            Log.d("Reservation Task ", reservation.getId_reservation());
                        }
                        returnValue ="valid";
                    } else {
                        returnValue = "invalid";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    returnValue = "invalid";
                }
            } else {
                returnValue = "notConnected";
            }
            return returnValue;
        }

        @Override
        protected void onPostExecute(String s) {
            switch (s){
                case "valid":
                    adapter =new ReservationAdapter(context, getListReservation());
                    _listReservation.setAdapter(adapter);
                    _listReservation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Reservation reservation = listReservation.get(position);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("reservation", reservation);
                            android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
                            if(reservation.getStatus()==0){
                                _listReservation.setVisibility(View.GONE);
                                PaymentConfirmFragment paymentConfirmFragment =new PaymentConfirmFragment();
                                paymentConfirmFragment.setArguments(bundle);
                                ft.replace(R.id.frame, paymentConfirmFragment);
                                ft.addToBackStack(ReservationActivity.class.getName());
                                ft.commit();
                            }
                        }
                    });
                    break;
                default:
                    Log.d("Reservationtask post", s);
            }
            progressDialog.dismiss();
            super.onPostExecute(s);
        }
    }


    public ArrayList<Reservation> getListReservation() {
        return listReservation;
    }

    public void setListReservation(ArrayList<Reservation> listReservation) {
        this.listReservation = listReservation;
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            if (getFragmentManager().getBackStackEntryCount() == 1) {
                _listReservation.setVisibility(View.VISIBLE);
            }
        } else {
            super.onBackPressed();
        }
    }


}

