package com.example.ngapap.cibodas.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ngapap.cibodas.ImageLoader.ImageLoader;
import com.example.ngapap.cibodas.Model.Product;
import com.example.ngapap.cibodas.R;

import java.util.ArrayList;

/**
 * Created by user on 29/05/2016.
 */
public class AdapterDelivCost extends BaseAdapter {
    Context context;
    ArrayList<Product> listProduct;
    ImageLoader imageLoader;
    public AdapterDelivCost(Context context, ArrayList<Product> listProduct){
        this.context = context;
        this.listProduct = listProduct;
        imageLoader=new ImageLoader(this.context);
    }

    @Override
    public int getCount() {
        return listProduct.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView;
        Product product;
        TextView _sellerName;
        ImageView _listImage;
        TextView _productName;
        TextView _price;
        TextView _category;
        TextView _textJumlah;
        TextView _textJadwal;
        TextView _delivCost;
        if(convertView == null)
        {
            rowView = inflater.inflate(R.layout.adapter_deliv_cost, parent, false);
        }
        else
        {
            rowView = convertView;
        }
        //set Component
        product = listProduct.get(position);
        _sellerName = (TextView) rowView.findViewById(R.id.seller_name);
        _listImage= (ImageView) rowView.findViewById(R.id.list_image);
        _productName = (TextView) rowView.findViewById(R.id.product_name);
        _price = (TextView) rowView.findViewById(R.id.price);
        _category = (TextView) rowView.findViewById(R.id.category);
        _textJumlah = (TextView) rowView.findViewById(R.id.textJumlah);
        _textJadwal = (TextView) rowView.findViewById(R.id.textJadwal);
        _delivCost = (TextView) rowView.findViewById(R.id.delivCost);
        //set Values
        _sellerName.setText(product.getSeller_name());
        if(product.getLinks()!=null){
            imageLoader.DisplayImage(product.getLinks()[0],_listImage);
        }else {
            _listImage.setImageResource(R.drawable.stub);
        }
        _productName.setText(product.getProduct_name());
        int totalPrice = product.getAmount()*product.getPrice();
        _price.setText("Rp "+String.valueOf(totalPrice));
        _category.setText(product.getCategory_name());
        _textJumlah.setText("Quantity : "+String.valueOf(product.getAmount()));
        if(product.getCategory_name().equalsIgnoreCase("Pariwisata")){
            _textJadwal.setText("Schedule :"+product.getDate());
        }else{
            _textJadwal.setVisibility(View.INVISIBLE);
        }
        _delivCost.setText("Rp. "+String.valueOf(product.getDeliv_cost()));

        return  rowView;
    }

}
