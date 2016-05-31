package com.example.ngapap.cibodas.Fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ngapap.cibodas.Activity.CartActivity;
import com.example.ngapap.cibodas.Activity.NavActivity;
import com.example.ngapap.cibodas.Adapter.AdapterDelivCost;
import com.example.ngapap.cibodas.Model.Reservation;
import com.example.ngapap.cibodas.R;

/**
 * Created by user on 29/05/2016.
 */
public class DeliveryCostFragment extends Fragment {
    private ListView _listView;
    private TextView _holderSubTotal;
    private TextView _holderDelivCost;
    private TextView _holderTotal;
    private Reservation reservation;
    private AdapterDelivCost adapter;
    private Button _btnPayment;
    private FragmentTransaction ft;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_deliv_cost, null);
        _listView = (ListView) rootView.findViewById(R.id.listProduct);
        _holderSubTotal = (TextView) rootView.findViewById(R.id.holderSubTotal);
        _holderDelivCost = (TextView) rootView.findViewById(R.id.holderDelivCost);
        _holderTotal = (TextView) rootView.findViewById(R.id.holderTotal);
        _btnPayment = (Button) rootView.findViewById(R.id.btn_payment);
        reservation = ((CartActivity) getActivity()).getReservation();
        adapter = new AdapterDelivCost(getActivity(), reservation.getProducts());
        _listView.setAdapter(adapter);
        ((NavActivity) getActivity()).setListViewHeightBasedOnChildren(_listView);
        int subTotal = reservation.subTotalPrice();
        int deliveryCost = reservation.delivCost();
        int total = reservation.totalPrice();
        _holderSubTotal.setText("Rp. "+subTotal);
        _holderDelivCost.setText("Rp. "+deliveryCost);
        _holderTotal.setText("Rp. "+total);
        ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left,
                R.animator.slide_in_left, R.animator.slide_out_left);
        Log.d("DelivCOstFragment", String.valueOf(reservation.getProducts().get(0).getDeliv_cost()));
        _btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft.replace(R.id.frame, new PaymentReservFragment());
                ft.addToBackStack(DeliveryCostFragment.class.getName());
                ft.commit();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Biaya Pengiriman");
    }
}
