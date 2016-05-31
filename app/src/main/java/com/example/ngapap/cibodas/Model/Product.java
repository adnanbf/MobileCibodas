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
    private String product_type;
    private int price;
    private int amount;
    private int deliv_cost;
    private int status;
    private String date;
    private String[] links;

    public Product toProduct(JSONArray jsonArray,int i){
        Product product=new Product();
            try {
                product.setId(jsonArray.getJSONObject(i).getString("id_detail_product"));
                product.setProduct_name(jsonArray.getJSONObject(i).getString("product_name"));
                product.setProduct_rating(jsonArray.getJSONObject(i).getString("rating"));
                product.setProduct_description(jsonArray.getJSONObject(i).getString("description"));
                product.setStock(jsonArray.getJSONObject(i).getString("stock"));
                product.setProduct_type(jsonArray.getJSONObject(i).getString("type_product"));
                product.setId_category(jsonArray.getJSONObject(i).getString("id_category"));
                product.setCategory_name(jsonArray.getJSONObject(i).getString("category_name"));
                product.setId_seller(jsonArray.getJSONObject(i).getString("id_seller"));
                product.setSeller_name(jsonArray.getJSONObject(i).getString("seller_name"));
                product.setId_price(jsonArray.getJSONObject(i).getString("id_price"));
                product.setPrice(jsonArray.getJSONObject(i).getInt("price"));
                JSONArray jsonLink= new JSONArray(jsonArray.getJSONObject(i).getString("links"));
                String[] arrayLink=new String[jsonLink.length()];
                for(int j=0;j<jsonLink.length();j++){
                    arrayLink[j]=jsonLink.getJSONObject(j).getString("link");
                    Log.d("Link :", arrayLink[j]);
                    if(j==jsonLink.length()-1){
                        product.setLinks(arrayLink);
                    }
                }
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

    public int getDeliv_cost() {
        return deliv_cost;
    }

    public void setDeliv_cost(int deliv_cost) {
        this.deliv_cost = deliv_cost;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }
}
