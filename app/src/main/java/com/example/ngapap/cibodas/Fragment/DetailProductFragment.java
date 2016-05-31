package com.example.ngapap.cibodas.Fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.ngapap.cibodas.Activity.CartActivity;
import com.example.ngapap.cibodas.CartArrayList;
import com.example.ngapap.cibodas.Model.Product;
import com.example.ngapap.cibodas.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by user on 29/03/2016.
 */
public class DetailProductFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener{
    private SliderLayout mDemoSlider;
    private TextView _product_name;
    private TextView _product_stok;
    private TextView _product_desc;
    private TextView _product_price;
    private TextView _product_type;
    private TextView _seller_name;
    private Button _buttonReserve;
    private Button _buttonAddCart;
    private RatingBar _rtbProduct;
    private Product product;
    CartArrayList cartArrayList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_detailed_product,null);
        _product_name= (TextView) rootView.findViewById(R.id.product_name);
        _product_stok = (TextView) rootView.findViewById(R.id.containerStok);
        _product_desc = (TextView) rootView.findViewById(R.id.containerDeskripsi);
        _product_price = (TextView) rootView.findViewById(R.id.containerPrice);
        _seller_name = (TextView) rootView.findViewById(R.id.sellerName);
        _product_type = (TextView) rootView.findViewById(R.id.producType);
        _buttonReserve = (Button) rootView.findViewById(R.id.buttonReserve);
        _buttonAddCart = (Button) rootView.findViewById(R.id.buttonAddToCart);
        _rtbProduct = (RatingBar) rootView.findViewById(R.id.rtbProductRating);
        mDemoSlider = (SliderLayout) rootView.findViewById(R.id.slider);
//        ((CatalogActivity) getActivity()).getResideMenu().addIgnoredView(mDemoSlider);
        product= (Product) this.getArguments().getSerializable("product");
        HashMap<String,String> url_maps = new HashMap<String, String>();
        if(product.getLinks()!=null){
            for(int i =0; i<product.getLinks().length;i++){
                url_maps.put("image-"+i, product.getLinks()[i]);
            }
            for(String name : url_maps.keySet()){
                TextSliderView textSliderView = new TextSliderView(getActivity().getApplicationContext());
                // initialize a SliderLayout
                textSliderView
                        .description(name)
                        .image(url_maps.get(name))
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(this);

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra",name);

                mDemoSlider.addSlider(textSliderView);
            }
            mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            mDemoSlider.setCustomAnimation(new DescriptionAnimation());
            mDemoSlider.setDuration(4000);
            mDemoSlider.addOnPageChangeListener(this);
        }


        _seller_name.setText(product.getSeller_name());
        _product_name.setText(product.getProduct_name());
        _product_price.setText("Rp " + product.getPrice());
        _rtbProduct.setRating(Float.parseFloat(product.getProduct_rating()));
        if(product.getCategory_name().equalsIgnoreCase("Pertanian")){
            if(product.getStock().equals("0")||Integer.parseInt(product.getStock())<5){
                _product_stok.setText("Out of Stock");
                _product_stok.setTextColor(Color.RED);
                _buttonReserve.setEnabled(false);
                _buttonAddCart.setEnabled(false);
            }
            _product_type.setText(product.getProduct_type());
        }else{
            if(product.getStock().equals("0")){
                _product_stok.setText("Out of Stock");
                _product_stok.setTextColor(Color.RED);
                _buttonReserve.setEnabled(false);
                _buttonAddCart.setEnabled(false);
            }
            _product_type.setVisibility(View.GONE);
        }

        _product_desc.setText(product.getProduct_description());
        cartArrayList = new CartArrayList();

        _buttonReserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("From reserve", product.getProduct_name());
                addToCart();
                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);
            }
        });
        _buttonAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                cartArrayList.destroy(getActivity());
                addToCart();
                Toast.makeText(getActivity(),"Added To Cart",Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(product.getProduct_name());
    }

    public void addToCart(){
        ArrayList<Product> listProduct = cartArrayList.getFavorites(getActivity());
        boolean checkCart = true;
        switch (product.getCategory_name().toUpperCase()){
            case "PARIWISATA":
                product.setAmount(1);
                String myFormat = "yyyy-MM-dd";
                Date cDate = new Date();
                String sDate = new SimpleDateFormat(myFormat).format(cDate);
                product.setDate(sDate);
                break;
            case "PERTANIAN":
                product.setAmount(5);
                break;
            case "PETERNAKAN":
                product.setAmount(1);
                break;
        }

        if (listProduct == null || listProduct.isEmpty()) {
            cartArrayList.addFavorite(getActivity(), product);
        } else {
            for (int i = 0; i < listProduct.size();i++ ) {
                if (listProduct.get(i).getId().equals(product.getId())) {
                    checkCart=false;
                }
            }
            if(checkCart) cartArrayList.addFavorite(getActivity(), product);
        }
    }

    @Override
    public void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        mDemoSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView baseSliderView) {

    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        Log.d("Slider Demo", "Page Changed: " + i);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }
}
