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
import android.widget.ListView;

import com.example.ngapap.cibodas.Activity.CartActivity;
import com.example.ngapap.cibodas.Activity.NavActivity;
import com.example.ngapap.cibodas.Adapter.AddressReservAdapter;
import com.example.ngapap.cibodas.JSONParser;
import com.example.ngapap.cibodas.Model.Customer;
import com.example.ngapap.cibodas.Model.Reservation;
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

    private Button _btnAddress;
    private Button _btnPayment;
    private Customer customer;
    private ListView _listAddress;
    private AddressReservAdapter adapter;
    private Reservation reservation;
    private int addressPosition;
    int mCurCheckPosition = 0;
    SessionManager sessionManager;
    NetworkUtils networkUtils;
    private FragmentTransaction ft;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_address_reserv, null);

        _btnAddress = (Button) rootView.findViewById(R.id.btn_address);
        _btnPayment = (Button) rootView.findViewById(R.id.btn_payment);
        _listAddress = (ListView) rootView.findViewById(R.id.listAddress);
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

        if (((CartActivity) getActivity()).getReservation() == null) {
            reservation = new Reservation();
        } else {
            reservation = ((CartActivity) getActivity()).getReservation();
            if (reservation.getId_delivery() != null) {
                for (int i = 0; i < customer.getListAddresses().size(); i++) {
                    int reserv_deliv = Integer.parseInt(reservation.getId_delivery());
                    int id_deliv = Integer.parseInt(customer.getListAddresses().get(i).getDelivery_id());
                    if (id_deliv == reserv_deliv) {
                        addressPosition = i;
                        adapter.selectedPosition = addressPosition;
                    }
                }
            }

        }

        adapter = new AddressReservAdapter(getActivity(), customer.getListAddresses());
        _listAddress.setAdapter(adapter);

        ((NavActivity) getActivity()).setListViewHeightBasedOnChildren(_listAddress);
        Log.d("From Fragment Address :", customer.getStreet());

        ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left,
                R.animator.slide_in_left, R.animator.slide_out_left);

        _btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressPosition = adapter.selectedPosition;
                ft.replace(R.id.frame, new NewAddressFragment());
                ft.addToBackStack(AddressReservFragment.class.getName());
                ft.commit();

                Log.d("Fragment Address Pos", String.valueOf(addressPosition));
            }
        });

        _btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addressPosition = adapter.selectedPosition;
                reservation.setId_delivery(customer.getListAddresses().get(addressPosition).getDelivery_id());
                reservation.setAddress(customer.getListAddresses().get(addressPosition));

                CostDelivCheck task = new CostDelivCheck(getActivity(), reservation);
                task.execute();
//                ft.replace(R.id.frame, new PaymentReservFragment());
//                ft.addToBackStack(AddressReservFragment.class.getName());
//                ft.commit();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Alamat Pengiriman");
    }

    private class CostDelivCheck extends AsyncTask<Void, Void, String> {
        Context context;
        Reservation mReservation;
        private ProgressDialog progressDialog;

        public CostDelivCheck(Context context, Reservation reservation) {
            this.context = context;
            this.mReservation = reservation;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.dialog_loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String returnValue = "";
            if (networkUtils.isConnectingToInternet()) {
                try {
                    JSONParser jsonParser = new JSONParser();
                    int destination = mReservation.getAddress().getmCity().getId();
//                    String destination = "74";
                    Log.d("City", String.valueOf(destination));
                    for (int i = 0; i < mReservation.getProducts().size(); i++) {
                        if (mReservation.getProducts().get(i).getCategory_name().equalsIgnoreCase("pertanian")) {
                            int amount = mReservation.getProducts().get(i).getAmount();
                            int weight = amount * 1000;
                            String param = "origin=24&destination=" + destination + "&weight=" + weight + "&courier=jne";
                            Log.d("param", param);
                            String request = jsonParser.checkDelivCost(param);
                            Log.d("CostDelivCheck", request);
                            JSONObject response = new JSONObject(request);
                            int deliv_cost = response.getJSONObject("rajaongkir").getJSONArray("results").
                                    getJSONObject(0).getJSONArray("costs").getJSONObject(0).getJSONArray("cost").getJSONObject(0).
                                    getInt("value");
//                            ((CartActivity) getActivity()).getReservation().getProducts().get(i).setDeliv_cost(deliv_cost);
                            mReservation.getProducts().get(i).setDeliv_cost(deliv_cost);
                            Log.d("deliv cost", String.valueOf(mReservation.getProducts().get(i).getDeliv_cost()));
                        }else{
//                            ((CartActivity) getActivity()).getReservation().getProducts().get(i).setDeliv_cost(0);
                        }
                    }
                    ((CartActivity) getActivity()).setReservation(mReservation);
                    returnValue = "connected";
                } catch (JSONException e) {
                    e.printStackTrace();
                    returnValue = "DBproblem";
                }
            }else{
                returnValue = "notConnected";
            }
            return returnValue;
        }

        @Override
        protected void onPostExecute(String s) {

            progressDialog.dismiss();
            AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog);
            switch (s){
                case "connected":

                    ft.replace(R.id.frame, new DeliveryCostFragment());
                    ft.addToBackStack(AddressReservFragment.class.getName());
                    ft.commit();
                    Log.d("From Update User", s);
                    break;
                case "notConnected":
                    alert.setTitle(getString(R.string.con_problem));
                    alert.setMessage(getString(R.string.serv_problem));
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    break;
                case "DBproblem":
                    Log.d("From Update User", s);
                    alert.setTitle(getString(R.string.db_problem));
                    alert.setMessage(getString(R.string.db_connection));
                    alert.setPositiveButton("OK", null);
                    alert.show();
            }
            super.onPostExecute(s);
        }
    }


    private void dialogAddress() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
        // Setting Dialog Title
        alertDialog.setTitle("Address Has not Been Updated");
        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want to Continue?");
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke YES event
//                _inputStreet.setText(customer.getStreet());
//                _inputZipCode.setText(customer.getZip_code());
//                _inputCity.setText(customer.getCity());
//                _inputProv.setText(customer.getProvince());
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


}
