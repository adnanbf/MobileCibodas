package com.example.ngapap.cibodas.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ngapap.cibodas.ImageLoader.ImageLoader;
import com.example.ngapap.cibodas.Model.Reservation;
import com.example.ngapap.cibodas.R;

import java.util.ArrayList;

/**
 * Created by user on 14/05/2016.
 */
public class ReservationAdapter extends ArrayAdapter<Reservation> {
    Context context;
    ArrayList<Reservation> listReservation;
    ImageLoader imageLoader;

    public ReservationAdapter(Context context, ArrayList<Reservation> objects){
        super(context, 0, objects);
        this.context = context;
        this.listReservation = objects;
        imageLoader = new ImageLoader(this.context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        TextView _noReservation;
        TextView _dateReservation;
        TextView _statReservation;
        if(convertView == null)
        {
            rowView = inflater.inflate(R.layout.adapter_reservation, parent, false);
        }
        else
        {
            rowView = convertView;
        }
        //init
        _noReservation = (TextView) rowView.findViewById(R.id.no_reservation);
        _dateReservation = (TextView) rowView.findViewById(R.id.date_reservation);
        _statReservation = (TextView) rowView.findViewById(R.id.status_reservation);
        //set value
        _noReservation.setText("Reservasi #"+listReservation.get(position).getId_reservation());
        String[] parts =listReservation.get(position).getCreated_at().split("-");
        String year = parts[0];
        String month = parts[1];
        String day = parts[2].substring(0,2);
        String time = parts[2].substring(3,8);
        _dateReservation.setText("Dipesan pada tanggal "+day + "-" + month + "-" + year+" jam "+time);
        int status = listReservation.get(position).getStatus();
        switch (status){
            case 0:
                _statReservation.setText("Pembayaran Belum Dikonfirmasi");
                _statReservation.setTextColor(Color.RED);
                break;
            case 1:
                _statReservation.setText("Pembayaran Sedang Diproses");
        }
        return rowView;
    }
}
