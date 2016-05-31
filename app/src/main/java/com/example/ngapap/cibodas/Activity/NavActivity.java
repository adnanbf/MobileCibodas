package com.example.ngapap.cibodas.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ngapap.cibodas.MainActivity;
import com.example.ngapap.cibodas.Model.Customer;
import com.example.ngapap.cibodas.NetworkUtils;
import com.example.ngapap.cibodas.R;
import com.example.ngapap.cibodas.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

/**
 * Created by user on 22/05/2016.
 */
public class NavActivity extends AppCompatActivity {
    protected DrawerLayout mDrawer;
    protected Toolbar toolbar;
    protected NavigationView nvDrawer;
    protected ActionBarDrawerToggle drawerToggle;
    protected SessionManager session;
    protected NetworkUtils networkUtils;
    protected Customer customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        // Set a Toolbar to replace the ActionBar.
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        nvDrawer.inflateMenu(R.menu.drawer_view);
        drawerToggle = setupDrawerToggle();
        // Setup drawer view
        mDrawer.addDrawerListener(drawerToggle);
        setupDrawerContent(nvDrawer);

        //init
        session = new SessionManager(getApplicationContext());
        networkUtils = new NetworkUtils(getApplicationContext());
        if (session.checkLogin())
            finish();
        customer = new Customer();
        HashMap<String, String> user = session.getUserDetails();
        String data = user.get(SessionManager.KEY_DATA);
        try {
            JSONArray jsonArray = new JSONArray(data);
            customer = customer.toCustomer(jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }

    protected void onNavigationItemSelected(int i){
        nvDrawer.getMenu().getItem(i).setChecked(true);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }


    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
//        Fragment fragment = null;
//        Class fragmentClass;
        Intent intent;
        switch (menuItem.getItemId()) {
            case R.id.nav_main:
//                fragmentClass = FirstFragment.class;
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Main Menu", Toast.LENGTH_SHORT).show();
                mDrawer.closeDrawers();
                break;
            case R.id.nav_reservation:
//                fragmentClass = SecondFragment.class;
                intent = new Intent(getApplicationContext(), ReservationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Reservation History", Toast.LENGTH_SHORT).show();
                mDrawer.closeDrawers();
                break;
            case R.id.nav_cart:
//                fragmentClass = ThirdFragment.class;
                intent = new Intent(getApplicationContext(), CartActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                mDrawer.closeDrawers();
                Toast.makeText(getApplicationContext(), "Shopping Cart", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_catalog:
                nvDrawer.getMenu().clear();
                nvDrawer.inflateMenu(R.menu.subdrawer_view);
                break;
            case R.id.nav_back:
                nvDrawer.getMenu().clear();
                nvDrawer.inflateMenu(R.menu.drawer_view);
                break;
            case R.id.nav_agri:
                intent = new Intent(this, AgriActivity.class);
//                intent.putExtra("catalogType", "pertanian");
                startActivity(intent);
                mDrawer.closeDrawers();
                break;
            case R.id.nav_tour:
                intent = new Intent(this, TourismActivity.class);
                startActivity(intent);
                mDrawer.closeDrawers();
                break;
            case R.id.nav_farm:
                intent = new Intent(this, FarmActivity.class);
                startActivity(intent);
                mDrawer.closeDrawers();
                break;
            case R.id.nav_logout:
                dialogLogout();
                break;
        }

        try {
//            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }


//        // Insert the fragment by replacing any existing fragment
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
//        setTitle(menuItem.getTitle());
        // Close the navigation drawer

    }

    private void dialogLogout(){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(NavActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Log Out");
        // Setting Dialog Message
        alertDialog.setMessage("Apakah Anda yakin ingin keluar  ?");
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke YES event
                session.logoutUser();
            }
        });
        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(R.string.main_menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        else if(android.R.id.home == id && !drawerToggle.isDrawerIndicatorEnabled()){
//            onBackPressed();
//            return true;
//        }else {
//
//        }
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
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

    protected void disableNavIcon() {
        if (getSupportActionBar() != null) {
            drawerToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            drawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    protected void enableNavIcon() {
        if (getSupportActionBar() != null) {
            drawerToggle.setDrawerIndicatorEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            drawerToggle.syncState();
        }
    }
}
