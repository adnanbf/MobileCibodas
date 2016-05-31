package com.example.ngapap.cibodas.Model;

/**
 * Created by user on 27/05/2016.
 */
public class Address  {
    private String delivery_id;
    private String name;
    private String street;
    private String zip_code;
    private String phone;
    private City mCity;
    private Province mProvince;

    public Address(){
        setmCity(new City());
        setmProvince(new Province());
    }


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getDelivery_id() {
        return delivery_id;
    }

    public void setDelivery_id(String delivery_id) {
        this.delivery_id = delivery_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public City getmCity() {
        return mCity;
    }

    public void setmCity(City mCity) {
        this.mCity = mCity;
    }

    public Province getmProvince() {
        return mProvince;
    }

    public void setmProvince(Province mProvince) {
        this.mProvince = mProvince;
    }
}
