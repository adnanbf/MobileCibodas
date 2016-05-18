package com.example.ngapap.cibodas;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ngapap.cibodas.Model.Product;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by user on 24/04/2016.
 */
public class CartArrayList {

    public static final String PREFS_NAME = "PRODUCT_APP";
    public static final String FAVORITES = "Product_Favorite";

    public CartArrayList() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveFavorites(Context context, List<Product> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.commit();
    }

    public void addFavorite(Context context, Product product) {
        List<Product> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<Product>();
        favorites.add(product);
        saveFavorites(context, favorites);
    }

    public void editFavorite(Context context, Product product){
        ArrayList<Product> favorites = getFavorites(context);
        if(favorites != null){
            for (int i = 0; i<favorites.size();i++){
                if(favorites.get(i).getId().equals(product.getId())){
                    favorites.set(i,product);
                    saveFavorites(context, favorites);
                }
            }
        }
    }

    public void removeFavorite(Context context, Product product) {
        ArrayList<Product> favorites = getFavorites(context);
        if (favorites != null) {
            for(int i=0;i<favorites.size();i++){
                if(favorites.get(i).getId().equals(product.getId())){
                    favorites.remove(i);
                    saveFavorites(context, favorites);
                }
            }
        }
    }

    public ArrayList<Product> getFavorites(Context context) {
        SharedPreferences settings;
        List<Product> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            Product[] favoriteItems = gson.fromJson(jsonFavorites,
                    Product[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<Product>(favorites);
        } else
            return null;

        return (ArrayList<Product>) favorites;
    }

    public void destroy(Context context){
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.clear();
        editor.commit();
    }
}

