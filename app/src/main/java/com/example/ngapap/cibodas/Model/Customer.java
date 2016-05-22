package com.example.ngapap.cibodas.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 14/03/2016.
 */
public class Customer {
    private String id;
    private String user_id;
    private String email;
    private String name;
    private String gender;
    private String street;
    private String city;
    private String province;
    private String zip_code;
    private String password;
    private String phone;
    private String api_token;
    private int status;


    public Customer toCustomer(JSONArray jsonArray){
        Customer cus=new Customer();
        try {
            cus.setId(jsonArray.getJSONObject(0).getString("ID_CUSTOMER").toString());
            cus.setUser_id(jsonArray.getJSONObject(0).getString("ID").toString());
            cus.setName(jsonArray.getJSONObject(0).getString("NAME").toString());
            cus.setEmail(jsonArray.getJSONObject(0).getString("EMAIL").toString());
            cus.setGender(jsonArray.getJSONObject(0).getString("GENDER").toString());
            cus.setStreet(jsonArray.getJSONObject(0).getString("STREET").toString());
            cus.setCity(jsonArray.getJSONObject(0).getString("CITY").toString());
            cus.setProvince(jsonArray.getJSONObject(0).getString("PROVINCE").toString());
            cus.setZip_code(jsonArray.getJSONObject(0).getString("ZIP_CODE").toString());
            cus.setPhone(jsonArray.getJSONObject(0).getString("PHONE").toString());
            cus.setApi_token(jsonArray.getJSONObject(0).getString("API_TOKEN").toString());
            cus.setStatus(jsonArray.getJSONObject(0).getInt("STATUS"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cus;
    }

    public JSONObject createJSONObject(){
        JSONObject json = new JSONObject();
        try {
            json.put("ID_CUSTOMER", getId());
            json.put("ID", getUser_id());
            json.put("EMAIL", getEmail());
            json.put("NAME", getName());
            json.put("GENDER", getGender());
            json.put("STREET", getStreet());
            json.put("CITY", getCity());
            json.put("PROVINCE", getProvince());
            json.put("ZIP_CODE", getZip_code());
            json.put("PASSWORD", getPassword());
            json.put("PHONE", getPhone());
            json.put("STATUS",getStatus());
            json.put("API_TOKEN", getApi_token());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public JSONArray createJSONArray(){
        JSONObject json = new JSONObject();
        JSONArray jason = new JSONArray();
        try {
            json.put("ID_CUSTOMER",getId());
            json.put("ID", getUser_id());
            json.put("EMAIL",getEmail());
            json.put("NAME",getName());
            json.put("GENDER",getGender());
            json.put("STREET", getStreet());
            json.put("CITY", getCity());
            json.put("PROVINCE", getProvince());
            json.put("ZIP_CODE",getZip_code());
//            json.put("PASSWORD",getPassword());
            json.put("PHONE", getPhone());
            json.put("STATUS",getStatus());
            json.put("API_TOKEN", getApi_token());
            jason.put(0,json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jason;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getApi_token() {
        return api_token;
    }

    public void setApi_token(String api_token) {
        this.api_token = api_token;
    }
}
