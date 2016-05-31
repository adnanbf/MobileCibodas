package com.example.ngapap.cibodas.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.ngapap.cibodas.Adapter.CatalogContentAdapter;
import com.example.ngapap.cibodas.Catalog;
import com.example.ngapap.cibodas.Model.Product;
import com.example.ngapap.cibodas.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by user on 26/05/2016.
 */
public class FarmActivity extends Catalog{
    SearchView searchView;
    @Bind(R.id.list)
    ListView _listView;
    @Bind(R.id.fab)
    FloatingActionButton _fab;
    CatalogContentAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout container = (LinearLayout) findViewById(R.id.content_frame);
        inflater.inflate(R.layout.activity_catalog, container);
        ButterKnife.bind(this);
        setListProduct(new ArrayList<Product>());
        setCatalogType("peternakan");
        String url =getString(R.string.base_url) + "products/catalog?api_token="+customer.getApi_token()+
                "&catalog="+getCatalogType()+"&offset=0";
        loadProduct(FarmActivity.this, url, adapter, _listView, _fab);
        handleIntent(getIntent(), FarmActivity.this, searchView, _listView);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent, FarmActivity.this, searchView, _listView);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
//         Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setFocusable(true);
        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v.findFocus());
                }
            }
        });
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            if (getFragmentManager().getBackStackEntryCount() == 1) {
                enableNavIcon();
                _listView.setVisibility(View.VISIBLE);
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                //do your action here.
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
