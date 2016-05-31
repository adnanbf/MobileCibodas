package com.example.ngapap.cibodas.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ngapap.cibodas.ImageLoader.ImageLoader;
import com.example.ngapap.cibodas.Model.Product;
import com.example.ngapap.cibodas.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by user on 24/04/2016.
 */
public class CartAdapter extends ArrayAdapter<Product> {
    Context context;
    ArrayList<Product> listCatalog;
    ImageLoader imageLoader;
    Calendar myCalendar;

    public CartAdapter(Context context, ArrayList<Product> objects) {
        super(context, 0, objects);
        this.context = context;
        this.listCatalog = objects;
        imageLoader = new ImageLoader(this.context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TextView _textJadwal;
        View rowView;
        final CartHolder holder = new CartHolder();
        if (convertView == null) {
            rowView = inflater.inflate(R.layout.adapter_cart, parent, false);
        } else {
            rowView = convertView;
        }
        myCalendar = Calendar.getInstance();
        //set component
        _textJadwal = (TextView) rowView.findViewById(R.id.textJadwal);
        holder.setProduct(listCatalog.get(position));
        holder.set_textProduct((TextView) rowView.findViewById(R.id.product_name));
        holder.set_textCategory((TextView) rowView.findViewById(R.id.category));
        holder.set_imageProduct((ImageView) rowView.findViewById(R.id.list_image));
        holder.set_textRemove((TextView) rowView.findViewById(R.id.text_remove));
        holder.get_textRemove().setTag(holder.getProduct());
        holder.set_editAmount((TextView) rowView.findViewById(R.id.editAmount));
        holder.set_editDate((TextView) rowView.findViewById(R.id.editDate));
        holder.set_textPrice((TextView) rowView.findViewById(R.id.price));
        //set Values
        holder.get_editAmount().setText(String.valueOf(holder.getProduct().getAmount()));
        holder.get_textProduct().setText(holder.getProduct().getProduct_name());
        holder.get_textCategory().setText(holder.getProduct().getCategory_name());
        holder.get_textPrice().setText(String.valueOf(holder.getProduct().getPrice()));

        if (holder.getProduct().getDate() != null) {
            String[] parts = holder.getProduct().getDate().split("-");
            String year = parts[0];
            String month = parts[1];
            String day = parts[2];
            holder.get_editDate().setText(day + "-" + month + "-" + year);
        }

        //In which you need put here
        if (!holder.getProduct().getCategory_name().equalsIgnoreCase("Pariwisata")) {
            _textJadwal.setVisibility(View.INVISIBLE);
            holder.get_editDate().setVisibility(View.INVISIBLE);
        }
        if (listCatalog.get(position).getLinks() != null) {
            Log.d("Link Image :", listCatalog.get(0).getLinks()[0]);
            imageLoader.DisplayImage(listCatalog.get(position).getLinks()[0], holder.get_imageProduct());
        } else {
            holder.get_imageProduct().setImageResource(R.drawable.stub);
        }
        rowView.setTag(holder);
        holder.get_editAmount().setTag(holder);
        holder.get_editDate().setTag(holder);
        return rowView;
    }

    public static class CartHolder {
        private Product product;
        private TextView _textProduct;
        private TextView _textCategory;
        private TextView _textRemove;
        private ImageView _imageProduct;
        private TextView _editAmount;
        private TextView _editDate;
        private TextView _textPrice;

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public TextView get_textProduct() {
            return _textProduct;
        }

        public void set_textProduct(TextView _textProduct) {
            this._textProduct = _textProduct;
        }

        public TextView get_textCategory() {
            return _textCategory;
        }

        public void set_textCategory(TextView _textCategory) {
            this._textCategory = _textCategory;
        }

        public TextView get_textRemove() {
            return _textRemove;
        }

        public void set_textRemove(TextView _textRemove) {
            this._textRemove = _textRemove;
        }

        public ImageView get_imageProduct() {
            return _imageProduct;
        }

        public void set_imageProduct(ImageView _imageProduct) {
            this._imageProduct = _imageProduct;
        }

        public TextView get_editAmount() {
            return _editAmount;
        }

        public void set_editAmount(TextView _editAmount) {
            this._editAmount = _editAmount;
        }

        public TextView get_editDate() {
            return _editDate;
        }

        public void set_editDate(TextView _editDate) {
            this._editDate = _editDate;
        }

        public TextView get_textPrice() {
            return _textPrice;
        }

        public void set_textPrice(TextView _textPrice) {
            this._textPrice = _textPrice;
        }
    }
}
