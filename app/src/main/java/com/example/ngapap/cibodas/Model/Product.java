package com.example.ngapap.cibodas.Model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by user on 24/03/2016.
 */
public class Product implements Serializable {
    private String id;
    private String product_name;
    private String product_rating;
    private String product_description;
    private String stock;
    private String id_category;
    private String category_name;
    private String id_seller;
    private String seller_name;
    private String id_price;
    private int price;
    private int amount;
    private String date;
    private String[] links;

    public Product toProduct(JSONArray jsonArray,int i){
        Product product=new Product();
            try {
                product.setId(jsonArray.getJSONObject(i).getString("id_detail_product").toString());
                product.setProduct_name(jsonArray.getJSONObject(i).getString("product_name").toString());
                product.setProduct_rating(jsonArray.getJSONObject(i).getString("rating").toString());
                product.setProduct_description(jsonArray.getJSONObject(i).getString("description").toString());
                product.setStock(jsonArray.getJSONObject(i).getString("stock").toString());
                product.setId_category(jsonArray.getJSONObject(i).getString("id_category").toString());
                product.setCategory_name(jsonArray.getJSONObject(i).getString("category_name").toString());
                product.setId_seller(jsonArray.getJSONObject(i).getString("id_seller").toString());
                product.setSeller_name(jsonArray.getJSONObject(i).getString("seller_name").toString());
                product.setId_price(jsonArray.getJSONObject(i).getString("id_price").toString());
                product.setPrice(jsonArray.getJSONObject(i).getInt("price"));
                JSONArray jsonLink= new JSONArray(jsonArray.getJSONObject(i).getString("links").toString());
                String[] arrayLink=new String[jsonLink.length()];
                for(int j=0;j<jsonLink.length();j++){
                    arrayLink[j]=jsonLink.getJSONObject(j).getString("link").toString();
                    Log.d("Link :", arrayLink[j]);
                }
                product.setLinks(arrayLink);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return product;
    }

    public static Comparator<Product> RatingComparator = new Comparator<Product>() {
        @Override
        public int compare(Product lhs, Product rhs) {
            int ratingComparison = rhs.getProduct_rating().compareTo(lhs.getProduct_rating());
            return ratingComparison;
        }
    };

    public static Comparator<Product> DescPriceComparator = new Comparator<Product>() {
        @Override
        public int compare(Product lhs, Product rhs) {
            return rhs.getPrice()-lhs.getPrice();
        }
    };


    public static Comparator<Product> AscPriceComparator = new Comparator<Product>() {
        @Override
        public int compare(Product lhs, Product rhs) {
            return lhs.getPrice()-rhs.getPrice();
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_description() {
        return product_description;
    }

    public void setProduct_description(String product_description) {
        this.product_description = product_description;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getId_category() {
        return id_category;
    }

    public void setId_category(String id_category) {
        this.id_category = id_category;
    }

    public String getId_seller() {
        return id_seller;
    }

    public void setId_seller(String id_seller) {
        this.id_seller = id_seller;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String[] getLinks() {
        return links;
    }

    public void setLinks(String[] links) {
        this.links = links;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getProduct_rating() {
        return product_rating;
    }

    public void setProduct_rating(String product_rating) {
        this.product_rating = product_rating;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId_price() {
        return id_price;
    }

    public void setId_price(String id_price) {
        this.id_price = id_price;
    }
}
