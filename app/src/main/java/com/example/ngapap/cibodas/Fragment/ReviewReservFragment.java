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
import com.example.ngapap.cibodas.Activity.MenuActivity;
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
        TextView _textTotalPrice;
        TextView _textCustName;
        TextView _textAddress;
        TextView _textZipCode;
        TextView _textEmail;
        Button _btnConfirm;
        //set Component
        cartArrayList = new CartArrayList();
        listProduct = cartArrayList.getFavorites(getActivity());
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
//        Log.d("from review reserv",reservation.getBank_account());
        _textTotalItem = (TextView) rootView.findViewById(R.id.textTotalItem);
        _textTotalPrice = (TextView) rootView.findViewById(R.id.textTotalPrice);
        _textCustName = (TextView) rootView.findViewById(R.id.textCustName);
        _textAddress = (TextView) rootView.findViewById(R.id.textAddress);
        _textZipCode = (TextView) rootView.findViewById(R.id.textZipCode);
        _textEmail = (TextView) rootView.findViewById(R.id.textEmail);
        _btnConfirm = (Button) rootView.findViewById(R.id.buttonConfirmResevation);
        //set Values
        adapter = new ReviewReservAdapter(getActivity(), listProduct);
        _listCatalog.setAdapter(adapter);
        MenuActivity.setListViewHeightBasedOnChildren(_listCatalog);
        _textTotalItem.setText(String.valueOf(setTotalItem()) + " Item");
        _textTotalPrice.setText("Rp " + String.valueOf(setTotalPrice()));
        _textCustName.setText(customer.getName());
        _textAddress.setText(customer.getStreet());
        _textZipCode.setText(customer.getZip_code());
        _textEmail.setText(customer.getEmail());
        reservation.setId_customer(customer.getId());
        reservation.setProducts(listProduct);
        _btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreReservationAsynctask task = new StoreReservationAsynctask(getActivity());
                task.execute(reservation);
            }
        });
        return rootView;
    }

    private int setTotalPrice() {
        int total = 0;
        for (int i = 0; i < listProduct.size(); i++) {
            int subTotal = listProduct.get(i).getAmount() * listProduct.get(i).getPrice();
            total += subTotal;
        }
        return total;
    }

    private int setTotalItem() {
        int total = 0;
        for (int i = 0; i < listProduct.size(); i++) {
            total += listProduct.get(i).getAmount();
        }
        return total;
    }

    private class StoreReservationAsynctask extends AsyncTask<Reservation, String, String> {
        Context context;
        ProgressDialog progressDialog;

        StoreReservationAsynctask(Context context) {
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
            String myURL = getString(R.string.base_url) + "reservation/store";
            if (networkUtils.isConnectedToServer(myURL)) {
                JSONParser jsonParser = new JSONParser();
                String request = jsonParser.postJSON(myURL, reservation.storeReservation());
                if (!request.equals("DBproblem")) {
                    try {
                        JSONObject response = new JSONObject(request);
                        if (response.getBoolean("Response")) {
                            returnValue = "success";
                        } else {
                            Log.d("from StoreReservTask", response.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                returnValue = "notConnected";
            }
            return returnValue;
        }

        @Override
        protected void onPostExecute(String s) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.AppTheme_Dark_Dialog);
            switch (s) {
                case "success":
                    alert.setTitle("Reservation Success");
                    alert.setMessage("Please Confirm Your Reservation Payment");
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
                case "notConnected":
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
