package com.example.ngapap.cibodas.Model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user on 04/05/2016.
 */
public class Reservation implements Serializable {
    private String id_reservation;
    private String id_customer;
    private String id_delivery;
    private int status;
    private String bank_name;
    private String bank_account;
    private String payment_proof;
    private String created_at;
    private ArrayList<Product> products;
    private Address address;

    public int totalItem(){
        int totalItem = 0;
        for(int i = 0; i<products.size(); i++){
            totalItem += products.get(i).getAmount();
        }
        return totalItem;
    }

    public int subTotalPrice() {
        int subTotal = 0;
        for (int i = 0; i < products.size(); i++) {
            subTotal += products.get(i).getAmount() * products.get(i).getPrice();
        }
        return subTotal;
    }

    public int delivCost() {
        int delivCost = 0;
        for (int i = 0; i < products.size(); i++) {
            delivCost += products.get(i).getDeliv_cost();
        }
        return delivCost;
    }

    public int totalPrice() {
        return delivCost() + subTotalPrice();
    }

    public Reservation toReservation(JSONArray jsonArray, int i) {
        Reservation reservation = new Reservation();
        try {
            reservation.setId_reservation(jsonArray.getJSONObject(i).getString("id_reservation"));
            reservation.setId_customer(jsonArray.getJSONObject(i).getString("id_customer"));
            reservation.setId_delivery(jsonArray.getJSONObject(i).getString("id_delivery"));
            reservation.setBank_name(jsonArray.getJSONObject(i).getString("bank_name"));
            reservation.setBank_account(jsonArray.getJSONObject(i).getString("bank_account"));
            reservation.setStatus(jsonArray.getJSONObject(i).getInt("status"));
            Log.d("reservation model", jsonArray.getJSONObject(i).getString("created_at"));
            reservation.setCreated_at(jsonArray.getJSONObject(i).getString("created_at"));
            ArrayList<Product> tmp = new ArrayList<>();
            JSONArray cart = jsonArray.getJSONObject(i).getJSONArray("carts");
            for (int j = 0; j < cart.length(); j++) {
                Product product = new Product();
                product.setId(cart.getJSONObject(j).getString("ID_DETAIL_PRODUCT"));
                product.setId_price(cart.getJSONObject(j).getString("ID_PRICE"));
                product.setPrice(cart.getJSONObject(j).getInt("PRICE"));
                product.setAmount(cart.getJSONObject(j).getInt("AMOUNT"));
                product.setDate(cart.getJSONObject(j).getString("SCHEDULE"));
                product.setDeliv_cost(cart.getJSONObject(j).getInt("DELIVERY_COST"));
                product.setStatus(cart.getJSONObject(j).getInt("STATUS"));
                product.setProduct_name(cart.getJSONObject(j).getString("PRODUCT_NAME"));
                product.setProduct_rating(cart.getJSONObject(j).getString("RATING"));
                product.setCategory_name(cart.getJSONObject(j).getString("CATEGORY"));
                product.setSeller_name(cart.getJSONObject(j).getString("SELLER"));
                tmp.add(product);
            }
            reservation.setProducts(tmp);
//            if(reservation.getStatus()!=0){
            reservation.setPayment_proof(jsonArray.getJSONObject(i).getString("payment_proof"));
//            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reservation;
    }

    public JSONObject storeReservation() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("id_customer", getId_customer());
            jsonObject.put("id_delivery", getId_delivery());
            jsonObject.put("bank_name", getBank_name());
            jsonObject.put("bank_account", getBank_account());
            for (int i = 0; i < products.size(); i++) {
                JSONObject json = new JSONObject();
                json.put("id_detail_product", products.get(i).getId());
                json.put("id_price", products.get(i).getId_price());
                json.put("amount", products.get(i).getAmount());
                json.put("delivery_cost", products.get(i).getDeliv_cost());
                if (products.get(i).getCategory_name().equalsIgnoreCase("pariwisata")) {
                    json.put("schedule", products.get(i).getDate());
                } else {
                    json.put("schedule", "0000-00-00");
                }
                jsonArray.put(i, json);
            }
            jsonObject.put("products", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public String getId_reservation() {
        return id_reservation;
    }

    public void setId_reservation(String id_reservation) {
        this.id_reservation = id_reservation;
    }

    public String getId_customer() {
        return id_customer;
    }

    public void setId_customer(String id_customer) {
        this.id_customer = id_customer;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getBank_account() {
        return bank_account;
    }

    public void setBank_account(String bank_account) {
        this.bank_account = bank_account;
    }

    public String getPayment_proof() {
        return payment_proof;
    }

    public void setPayment_proof(String payment_proof) {
        this.payment_proof = payment_proof;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getId_delivery() {
        return id_delivery;
    }

    public void setId_delivery(String delivery_id) {
        this.id_delivery = delivery_id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
