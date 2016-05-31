package com.example.ngapap.cibodas.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.ngapap.cibodas.ImageLoader.ImageLoader;
import com.example.ngapap.cibodas.Model.Address;
import com.example.ngapap.cibodas.R;

import java.util.ArrayList;

/**
 * Created by user on 27/05/2016.
 */
public class AddressReservAdapter extends ArrayAdapter<Address> {
    Context context;
    ArrayList<Address> listAddress;
    ImageLoader imageLoader;
    public static int selectedPosition = 0;
    public AddressReservAdapter(Context context, ArrayList<Address> objects){
        super(context, 0, objects);
        this.context = context;
        this.listAddress = objects;
        imageLoader = new ImageLoader(this.context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        final AddressHolder holder = new AddressHolder();
        if (convertView == null) {
            rowView = inflater.inflate(R.layout.adapter_address, parent, false);
        } else {
            rowView = convertView;
        }
        //set component
        holder.setAddress(listAddress.get(position));
        holder.set_name((TextView) rowView.findViewById(R.id.name));
        holder.set_street((TextView) rowView.findViewById(R.id.street));
        holder.set_region((TextView) rowView.findViewById(R.id.region));
        holder.set_zipCode((TextView) rowView.findViewById(R.id.zip_code));
        holder.set_radioButton((RadioButton) rowView.findViewById(R.id.radioButton));
        holder.set_phone((TextView) rowView.findViewById(R.id.phone));
        holder.setLinearLayout((LinearLayout) rowView.findViewById(R.id.linearAddressHorizontal));

        holder.get_name().setText(listAddress.get(position).getName());
        holder.get_street().setText(listAddress.get(position).getStreet());
        holder.get_region().setText(listAddress.get(position).getmCity().getType()+
                " "+listAddress.get(position).getmCity().getCity()+", "+
                listAddress.get(position).getmProvince().getProvince());
        holder.get_zipCode().setText(listAddress.get(position).getZip_code());
        holder.get_phone().setText(listAddress.get(position).getPhone());
        holder.get_radioButton().setChecked(position == selectedPosition);
        holder.get_radioButton().setTag(position);
        holder.get_radioButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = (Integer) v.getTag();
                Log.d("Address Adapter Pos", String.valueOf(selectedPosition));
                notifyDataSetChanged();
            }
        });
        holder.getLinearLayout().setTag(position);
        holder.getLinearLayout().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = (Integer) v.getTag();
                Log.d("Address Adapter Pos", String.valueOf(selectedPosition));
                notifyDataSetChanged();
            }
        });
//        rowView.setTag(holder);
        return rowView;
    }

    public static class AddressHolder{
        private Address address;
        private LinearLayout linearLayout;
        private RadioButton _radioButton;
        private TextView _name;
        private TextView _street;
        private TextView _region;
        private TextView _zipCode;
        private TextView _phone;
        private int selected;
        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public RadioButton get_radioButton() {
            return _radioButton;
        }

        public void set_radioButton(RadioButton _radioButton) {
            this._radioButton = _radioButton;
        }

        public TextView get_street() {
            return _street;
        }

        public void set_street(TextView _street) {
            this._street = _street;
        }

        public TextView get_region() {
            return _region;
        }

        public void set_region(TextView _region) {
            this._region = _region;
        }


        public TextView get_zipCode() {
            return _zipCode;
        }

        public void set_zipCode(TextView _zipCode) {
            this._zipCode = _zipCode;
        }

        public LinearLayout getLinearLayout() {
            return linearLayout;
        }

        public void setLinearLayout(LinearLayout linearLayout) {
            this.linearLayout = linearLayout;
        }

        public int getSelected() {
            return selected;
        }

        public void setSelected(int selected) {
            this.selected = selected;
        }

        public TextView get_name() {
            return _name;
        }

        public void set_name(TextView _name) {
            this._name = _name;
        }

        public TextView get_phone() {
            return _phone;
        }

        public void set_phone(TextView _phone) {
            this._phone = _phone;
        }
    }

}
