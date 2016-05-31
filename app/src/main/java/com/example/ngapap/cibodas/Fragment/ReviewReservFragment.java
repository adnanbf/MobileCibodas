package com.example.ngapap.cibodas.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ngapap.cibodas.Activity.CartActivity;
import com.example.ngapap.cibodas.Activity.NavActivity;
import com.example.ngapap.cibodas.Activity.ReservationActivity;
import com.example.ngapap.cibodas.Adapter.ReviewReservAdapter;
import com.example.ngapap.cibodas.CartArrayList;
import com.example.ngapap.cibodas.JSONParser;
import com.example.ngapap.cibodas.Model.Customer;
import com.example.ngapap.cibodas.Model.Product;
import com.example.ngapap.cibodas.Model.Reservation;
import com.example.ngapap.cibodas.NetworkUtils;
import com.example.ngapap.cibodas.R;
import com.example.ngapap.cibodas.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 06/05/2016.
 */
public class ReviewReservFragment extends Fragment {
    private View rootView;
    private ListView _listCatalog;
    private ArrayList<Product> listProduct;
    CartArrayList cartArrayList;
    SessionManager sessionManager;
    NetworkUtils networkUtils;
    Reservation reservation;
    ReviewReservAdapter adapter;
    JSONArray jsonArray;
    Customer customer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_review_reserv, null);
        TextView _textTotalItem;
        TextView _textSubPrice;
        TextView _textCustName;
        TextView _textAddress;
        TextView _textRegion;
        TextView _textZipCode;
        TextView _textEmail;
        TextView _textDelivCost;
        TextView _textTotalPrice;
        Button _btnConfirm;
        //set Component
        cartArrayList = new CartArrayList();

        _listCatalog = (ListView) rootView.findViewById(R.id.listCart);
        sessionManager = new SessionManager(getActivity());
        networkUtils = new NetworkUtils(getActivity());
        HashMap<String, String> user = sessionManager.getUserDetails();
        String data = user.get(SessionManager.KEY_DATA);
        try {
            jsonArray = new JSONArray(data);
            customer = new Customer();
            customer = customer.toCustomer(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        reservation = ((CartActivity) getActivity()).getReservation();
        listProduct = reservation.getProducts();
//        Log.d("from review reserv",reservation.getBank_account());
        _textTotalItem = (TextView) rootView.findViewById(R.id.textTotalItem);
        _textSubPrice = (TextView) rootView.findViewById(R.id.textSubPrice);
        _textDelivCost = (TextView) rootView.findViewById(R.id.textDelivCost);
        _textTotalPrice = (TextView) rootView.findViewById(R.id.textTotalPrice);
        _textCustName = (TextView) rootView.findViewById(R.id.textCustName);
        _textAddress = (TextView) rootView.findViewById(R.id.textAddress);
        _textRegion = (TextView) rootView.findViewById(R.id.textRegion);
        _textZipCode = (TextView) rootView.findViewById(R.id.textZipCode);
        _textEmail = (TextView) rootView.findViewById(R.id.textEmail);
        _btnConfirm = (Button) rootView.findViewById(R.id.buttonConfirmResevation);
        //set Values
        adapter = new ReviewReservAdapter(getActivity(), listProduct);
        _listCatalog.setAdapter(adapter);
        ((NavActivity) getActivity()).setListViewHeightBasedOnChildren(_listCatalog);

        _textTotalItem.setText(String.valueOf(reservation.totalItem()) + " Item(s)");
        _textSubPrice.setText("Rp. " + String.valueOf(reservation.subTotalPrice()));
        _textDelivCost.setText("Rp. "+ String.valueOf(reservation.delivCost()));
        _textTotalPrice.setText("Rp. "+ String.valueOf(reservation.totalPrice()));

        _textAddress.setText(reservation.getAddress().getStreet());
        _textRegion.setText(reservation.getAddress().getmCity().getCity() + ", "+
        reservation.getAddress().getmProvince().getProvince());
        _textZipCode.setText(reservation.getAddress().getZip_code());
//        for (int i = 0; i < customer.getListAddresses().size(); i++) {
//            String id_deliv = customer.getListAddresses().get(i).getDelivery_id();
//            String deliv_reserv = reservation.getId_delivery();
//            if (id_deliv.equals(deliv_reserv)) {
//                _textAddress.setText(customer.getListAddresses().get(i).getStreet());
//                _textRegion.setText(customer.getListAddresses().get(i).getmCity().getCity() + ", " +
//                        customer.getListAddresses().get(i).getmProvince().getProvince());
//                _textZipCode.setText(customer.getListAddresses().get(i).getZip_code());
//            }
//        }
        _textCustName.setText(reservation.getAddress().getName());
        _textEmail.setText(reservation.getAddress().getPhone());
        reservation.setId_customer(customer.getId());
        _btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogReservation();
            }
        });
        return rootView;
    }

    private void dialogReservation() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());
        // Setting Dialog Title
        alertDialog.setTitle("Konfirmasi Reservasi");
        // Setting Dialog Message
        alertDialog.setMessage("Apakah Anda yakin ingin memesan produk ?");
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke YES event
                StoreReservationAsynctask task = new StoreReservationAsynctask(getActivity(), customer.getApi_token());
                task.execute(reservation);
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
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Review Pesanan");
    }



    private class StoreReservationAsynctask extends AsyncTask<Reservation, String, String> {
        Context context;
        ProgressDialog progressDialog;
        String api_token;

        StoreReservationAsynctask(Context context, String api_token) {
            this.api_token = api_token;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Processing...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Reservation... params) {
            String returnValue = "";
            Reservation reservation = params[0];
            Log.d("StoreReservation ", reservation.storeReservation().toString());
            String myURL = getString(R.string.base_url) + "reservation/store?api_token=" + api_token;
            if (networkUtils.isConnectedToServer(myURL)) {
                JSONParser jsonParser = new JSONParser();
                String request = jsonParser.postJSON(myURL, reservation.storeReservation());
                try {
                    JSONObject response = new JSONObject(request);
                    if (response.getBoolean("Response")) {
                        returnValue = "success";
                    } else {
                        Log.d("from StoreReservTask", response.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    returnValue= "DBproblem";
                }

            } else {
                returnValue = "notConnected";
            }
            return returnValue;
        }

        @Override
        protected void onPostExecute(String s) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog);
            switch (s.toUpperCase()) {
                case "SUCCESS":
                    alert.setTitle("Pemesanan Sukses");
                    alert.setMessage("Silahkan Konfirmasi Pembayaran Anda");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getActivity(), ReservationActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            getActivity().finish();
                        }
                    });
                    alert.show();
                    cartArrayList.destroy(getActivity());
                    break;
                case "NOTCONNECTED":
                    alert.setTitle(getString(R.string.con_problem));
                    alert.setMessage(getString(R.string.serv_problem));
                    alert.setPositiveButton("OK", null);
                    alert.show();
                    break;
                default:
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
