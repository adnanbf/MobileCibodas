package com.example.ngapap.cibodas.Model;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by user on 29/05/2016.
 */
public class Province {
    private int province_id;
    private String province;

    public CharSequence[] toArray(ArrayList<Province> listProvince){
        CharSequence[] items = new CharSequence[listProvince.size()];
        for(int i=0;i<listProvince.size();i++){
            items[i] = listProvince.get(i).getProvince();
        }
        return items;
    }

    public Province toProvince(JSONArray jsonArray, int i){
        Province province = new Province();
        try {
            province.setProvince(jsonArray.getJSONObject(i).getString("province"));
            province.setProvince_id(jsonArray.getJSONObject(i).getInt("province_id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return province;
    }

    public int getProvince_id() {
        return province_id;
    }

    public void setProvince_id(int province_id) {
        this.province_id = province_id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
