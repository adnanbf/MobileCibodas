package com.example.ngapap.cibodas.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.ngapap.cibodas.ImageLoader.ImageLoader;
import com.example.ngapap.cibodas.Model.Product;
import com.example.ngapap.cibodas.R;

import java.util.ArrayList;

/**
 * Created by user on 25/03/2016.
 */
public class CatalogContentAdapter extends BaseAdapter {

    Context context;
    ArrayList<Product> listCatalog;
    ImageLoader imageLoader;


    public CatalogContentAdapter(Context context, ArrayList<Product> listCatalog){
        this.context = context;
        this.listCatalog = listCatalog;
        imageLoader=new ImageLoader(this.context);
    }

    @Override
    public int getCount() {
        return listCatalog.size();
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
        TextView _textProduct;
        TextView _textCategory;
        TextView _textPrice;
        ImageView _imageProduct;
        RatingBar _ratingProduct;
        View rowView;
        if(convertView == null)
        {
            rowView = inflater.inflate(R.layout.adapter_catalog, parent, false);
        }
        else
        {
            rowView = convertView;
        }
        _textProduct = (TextView)rowView.findViewById(R.id.product_name);
        _textCategory = (TextView)rowView.findViewById(R.id.category);
        _textPrice = (TextView) rowView.findViewById(R.id.price);
        _imageProduct = (ImageView) rowView.findViewById(R.id.list_image);
        _ratingProduct = (RatingBar)rowView.findViewById(R.id.rtbProductRating);
        //set Values
        _textProduct.setText(listCatalog.get(position).getProduct_name());
        _textPrice.setText("Rp "+listCatalog.get(position).getPrice());
        _textCategory.setText(listCatalog.get(position).getCategory_name());
        _ratingProduct.setRating(Float.parseFloat(listCatalog.get(position).getProduct_rating()));

        if(listCatalog.get(position).getLinks()!=null){
            Log.d("Link Image :", listCatalog.get(0).getLinks()[0]);
            imageLoader.DisplayImage(listCatalog.get(position).getLinks()[0],_imageProduct);
        }else{
            _imageProduct.setImageResource(R.drawable.stub);
        }

        return  rowView;
    }
}
