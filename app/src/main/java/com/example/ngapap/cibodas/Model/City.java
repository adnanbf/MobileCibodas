package com.example.ngapap.cibodas.Model;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by user on 29/05/2016.
 */
public class City {
    private int id;
    private  int province_id;
    private String type;
    private String city;
    private String zip_code;

    public CharSequence[] toArray(ArrayList<City> lisCity){
        CharSequence[] items = new CharSequence[lisCity.size()];
        for(int i=0;i<lisCity.size();i++){
            items[i] = lisCity.get(i).getType()+" "+lisCity.get(i).getCity();
        }
        return items;
    }

    public City toCity(JSONArray jsonArray, int i){
        City mCity = new City();
        try {
            mCity.setId(jsonArray.getJSONObject(i).getInt("id"));
            mCity.setProvince_id(jsonArray.getJSONObject(i).getInt("province_id"));
            mCity.setType(jsonArray.getJSONObject(i).getString("type"));
            mCity.setCity(jsonArray.getJSONObject(i).getString("city"));
            mCity.setZip_code(jsonArray.getJSONObject(i).getString("zip_code"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mCity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvince_id() {
        return province_id;
    }

    public void setProvince_id(int province_id) {
        this.province_id = province_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }
}
