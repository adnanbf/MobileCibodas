package com.example.ngapap.cibodas.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ngapap.cibodas.MainActivity;
import com.example.ngapap.cibodas.NetworkUtils;
import com.example.ngapap.cibodas.R;
import com.example.ngapap.cibodas.ResideMenu.ResideMenu;
import com.example.ngapap.cibodas.ResideMenu.ResideMenuItem;
import com.example.ngapap.cibodas.SessionManager;

/**
 * Created by user on 26/03/2016.
 */
public class MenuActivity extends AppCompatActivity implements View.OnClickListener {
    private ResideMenu resideMenu;
    private MenuActivity mContext;
    private ResideMenuItem itemHome;
    private ResideMenuItem itemReservation;
    private ResideMenuItem itemCart;
    private ResideMenuItem itemLogout;
    protected SessionManager session;
    protected NetworkUtils networkUtils;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mContext = this;
//        session =  new SessionManager(getApplicationContext());
//        if(session.checkLogin())
//            finish();

        setUpMenu();
//        if( savedInstanceState == null )
//            changeFragment(new HomeFragment());
    }


    private void setUpMenu() {

        // attach to current activity;
        resideMenu = new ResideMenu(this);
        resideMenu.setUse3D(true);
//        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);
//        resideMenu.setMenuListener(menuListener);
        //valid scale factor is between 0.0f and 1.0f. leftmenu'width is 150dip.
        resideMenu.setScaleValue(0.6f);

        // create menu items;
        itemHome = new ResideMenuItem(this, R.drawable.icon_home, "Home");
        itemReservation = new ResideMenuItem(this, R.drawable.icon_profile, "Reservasi");
        itemCart = new ResideMenuItem(this, R.drawable.icon_cart, "Shopping Cart");
        itemLogout = new ResideMenuItem(this, R.drawable.icon_logout, "Logout");

        itemHome.setOnClickListener(this);
        itemReservation.setOnClickListener(this);
        itemCart.setOnClickListener(this);
        itemLogout.setOnClickListener(this);

        resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemReservation, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemCart, ResideMenu.DIRECTION_LEFT);
        resideMenu.addMenuItem(itemLogout, ResideMenu.DIRECTION_LEFT);

        // You can disable a direction by setting ->
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

        findViewById(R.id.title_bar_left_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
            }
        });
//        findViewById(R.id.title_bar_right_menu).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                resideMenu.openMenu(ResideMenu.DIRECTION_RIGHT);
//            }
//        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View view) {

        if (view == itemHome) {
            Toast.makeText(mContext, "Main Menu", Toast.LENGTH_SHORT).show();
            Intent mainmenu = new Intent(getApplicationContext(), MainActivity.class);
            mainmenu.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainmenu);
//            changeFragment(new HomeFragment());
        } else if (view == itemReservation) {
            Toast.makeText(mContext, "Reservation", Toast.LENGTH_SHORT).show();
            Intent reservation= new Intent(getApplicationContext(), ReservationActivity.class);
            startActivity(reservation);
//            changeFragment(new ProfileFragment());
        } else if (view == itemCart) {
            Toast.makeText(mContext, "Shopping Cart", Toast.LENGTH_SHORT).show();
            Intent cartmenu = new Intent(getApplicationContext(), CartActivity.class);
            startActivity(cartmenu);
//            changeFragment(new CalendarFragment());
        } else if (view == itemLogout) {
            Toast.makeText(mContext, "Logged Out", Toast.LENGTH_SHORT).show();
            session.logoutUser();
//            changeFragment(new SettingsFragment());
        }
        resideMenu.closeMenu();
    }

    private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
        @Override
        public void openMenu() {
            Toast.makeText(mContext, "Menu is opened!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void closeMenu() {
            Toast.makeText(mContext, "Menu is closed!", Toast.LENGTH_SHORT).show();
        }
    };

    private void changeFragment(Fragment targetFragment) {
        resideMenu.clearIgnoredViewList();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_fragment, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    // What good method is to access resideMenu？
    public ResideMenu getResideMenu() {
        return resideMenu;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight=0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            view = listAdapter.getView(i, view, listView);

            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        ViewGroup.LayoutParams.MATCH_PARENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight()) * (listAdapter.getCount()));

        listView.setLayoutParams(params);
        listView.requestLayout();

    }

}
