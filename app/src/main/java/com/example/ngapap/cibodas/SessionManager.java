package com.example.ngapap.cibodas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.ngapap.cibodas.Activity.LoginActivity;

import java.util.HashMap;

/**
 * Created by user on 03/03/2016.
 */
public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE =0;
    private static final String PREFER_NAME = "SessionPref";
    private static final String IS_USER_LOGIN = "IsUserLoggedIn";
    public static final String KEY_DATA = "data";
    public static final String KEY_EMAIL= "email";

    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String email, String data){
        editor.putBoolean(IS_USER_LOGIN,true);
        editor.putString(KEY_EMAIL,email);
        editor.putString(KEY_DATA,data);
        editor.commit();
    }

    public boolean isUserLoggedIn(){
        return pref.getBoolean(IS_USER_LOGIN, false);
    }

    public boolean checkLogin(){
        if(!this.isUserLoggedIn()){
            Intent intent = new Intent(_context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(intent);
            return true;
        }
        return false;
    }

    public HashMap<String,String> getUserDetails(){
        HashMap<String,String> user = new HashMap<String,String>();
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL,null));
        user.put(KEY_DATA, pref.getString(KEY_DATA,null));
        return  user;
    }

    public void logoutUser(){

//        if(AccessToken.getCurrentAccessToken() != null){
//            LoginManager.getInstance().logOut();
//        }
        editor.clear();
        editor.commit();
        Intent intent = new Intent(_context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(intent);

    }

}
