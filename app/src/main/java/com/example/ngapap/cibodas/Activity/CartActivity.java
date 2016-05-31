package com.example.ngapap.cibodas.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ngapap.cibodas.Adapter.CartAdapter;
import com.example.ngapap.cibodas.CartArrayList;
import com.example.ngapap.cibodas.Fragment.AddressReservFragment;
import com.example.ngapap.cibodas.Model.Product;
import com.example.ngapap.cibodas.Model.Reservation;
import com.example.ngapap.cibodas.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by user on 24/04/2016.
 */
public class CartActivity extends NavActivity {
    private ListView _listCart;
    private ArrayList<Product> listCart;
    Context context = CartActivity.this;
    CartArrayList cartArrayList;
    CartAdapter adapter;
    @Bind(R.id.textTotalItem)
    TextView _textTotalItem;
    @Bind(R.id.textNullCart)
    TextView _textNullCart;
    @Bind(R.id.buttonConfirmResevation)
    Button _btnConfirm;
    @Bind(R.id.header)
    FrameLayout _header;
    @Bind(R.id.footer)
    LinearLayout _footer;
    @Bind(R.id.textTotalPrice)
    TextView _textTotalPrice;
    SetTotalAsynctask task;
    Calendar myCalendar;
    private Reservation reservation;
    private int addressPosition;
    @Bind(R.id.linearMain)
    LinearLayout _linearMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_cart);
        LayoutInflater inflater = getLayoutInflater();
        LinearLayout container = (LinearLayout) findViewById(R.id.content_frame);
        inflater.inflate(R.layout.activity_cart, container);
        ButterKnife.bind(this);
//        disableNavIcon();
        cartArrayList = new CartArrayList();
        myCalendar = Calendar.getInstance();
        listCart = new ArrayList<>();
        listCart = cartArrayList.getFavorites(getApplicationContext());
        _listCart = (ListView) findViewById(R.id.listCart);
//        Log.d("From Cart", g.getData().get(0).getProduct_name());
        if (listCart != null) {
            if (!listCart.isEmpty()) {
                adapter = new CartAdapter(this, listCart);
                _listCart.setAdapter(adapter);
                setListViewHeightBasedOnChildren(_listCart);
                task = new SetTotalAsynctask(context);
                task.execute(listCart);

                _btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(CartActivity.this,ReservationActivity.class);
//                        startActivity(intent);
                        if (validateDate()) {
                            reservation = new Reservation();
                            reservation.setProducts(listCart);
                            AddressReservFragment fragment = new AddressReservFragment();
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_left,
                                    R.animator.slide_in_left, R.animator.slide_out_left);
                            fragmentTransaction.replace(R.id.frame, fragment, AddressReservFragment.class.getName());
                            fragmentTransaction.addToBackStack(CartActivity.class.getName());
                            _linearMain.setVisibility(View.INVISIBLE);
                            disableNavIcon();
                            fragmentTransaction.commit();
                        }
                    }
                });
            } else {
                nullCartVisibility();
            }

        } else {
            nullCartVisibility();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(R.string.shop_cart);
        onNavigationItemSelected(3);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                    disableNavIcon();
//                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                } else {
                    Log.d("from back stack :", "shitstain");
                }
            }
        });
    }

    public void setAmount(View vi) {
        CartAdapter.CartHolder holder = (CartAdapter.CartHolder) vi.getTag();
        final TextView _editAmount = holder.get_editAmount();
        final Product itemToEdit = holder.getProduct();
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompt_amount, null);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String input = userInput.getText().toString();
                                int minInput;
                                if (itemToEdit.getCategory_name().equals("pariwisata")) {
                                    minInput = 1;
                                } else {
                                    minInput = 5;
                                }
                                boolean validate = true;
                                // get user input and set it to result
                                // edit text
                                if (input.isEmpty() || Integer.parseInt(input) < minInput) {
                                    Toast.makeText(getApplicationContext(), "Minimum Pembelian : " + minInput, Toast.LENGTH_SHORT).show();
                                    validate = false;
                                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                } else if (Integer.parseInt(input) > Integer.parseInt(itemToEdit.getStock())) {
                                    Toast.makeText(getApplicationContext(), "Stok Tidak Mencukupi", Toast.LENGTH_SHORT).show();
                                    validate = false;
                                    ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                }
                                if (validate) {
                                    _editAmount.setText(userInput.getText());
                                    itemToEdit.setAmount(Integer.parseInt(userInput.getText().toString()));
                                    cartArrayList.editFavorite(context, itemToEdit);
                                    adapter.notifyDataSetChanged();
                                    task = new SetTotalAsynctask(context);
                                    task.execute(cartArrayList.getFavorites(context));
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private boolean validateDate() {
        boolean validate = true;
        for (int i = 0; i < listCart.size(); i++) {
            if (listCart.get(i).getCategory_name().equals("pariwisata")) {
                String[] cParts = listCart.get(i).getDate().split("-");
                String cYear = cParts[0];
                String cMonth = cParts[1];
                String cDay = cParts[2];
                myCalendar.set(Calendar.YEAR, Integer.parseInt(cYear));
                myCalendar.set(Calendar.MONTH, Integer.parseInt(cMonth));
                myCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(cDay));
                long cDate = myCalendar.getTimeInMillis();
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                long yesterday = cal.getTimeInMillis();
                if (cDate <= yesterday)
                    validate = false;
            }
        }
        return validate;
    }


    public void setDate(View vi) {
        CartAdapter.CartHolder holder = (CartAdapter.CartHolder) vi.getTag();
        final TextView _editDate = holder.get_editDate();
        final Product itemToEdit = holder.getProduct();
        String myFormat = "yyyy-MM-dd";
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        try {
            myCalendar.setTime(sdf.parse(itemToEdit.getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

            // when dialog box is closed, below method will be called.
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                long yesterday = cal.getTimeInMillis();
                cal.add(Calendar.DATE, 30);
                long tomorrow = cal.getTimeInMillis();
                myCalendar.set(Calendar.YEAR, selectedYear);
                myCalendar.set(Calendar.MONTH, selectedMonth);
                myCalendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                long sDate = myCalendar.getTimeInMillis();

                boolean validate = true;
//                myCalendar.add(Calendar.DATE,30);
                if (sDate <= yesterday || sDate >= tomorrow) {
                    validate = false;
                }
                if (validate) {
                    String[] parts = sdf.format(myCalendar.getTime()).split("-");
                    String year = parts[0];
                    String month = parts[1];
                    String day = parts[2];
                    _editDate.setText(day + "-" + month + "-" + year);
                    itemToEdit.setDate(sdf.format(myCalendar.getTime()));
                    cartArrayList.editFavorite(context, itemToEdit);
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Invalid Date", Toast.LENGTH_SHORT).show();
                }

                Log.d("from Datepicker", itemToEdit.getDate());
            }
        };
        DatePickerDialog datePicker = new DatePickerDialog(this,
                datePickerListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    public Reservation getReservation() {
        return reservation;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public int getAddressPosition() {
        return addressPosition;
    }

    public void setAddressPosition(int addressPosition) {
        this.addressPosition = addressPosition;
    }

    private class SetTotalAsynctask extends AsyncTask<ArrayList<Product>, String, int[]> {
        private Context context;
        private ProgressDialog progressDialog;

        SetTotalAsynctask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context,
                    R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected int[] doInBackground(ArrayList<Product>... params) {
            ArrayList<Product> listProduct = params[0];
            int[] array = new int[2];
            for (int i = 0; i < listProduct.size(); i++) {
                array[0] += listProduct.get(i).getAmount();
                int total = listProduct.get(i).getAmount() * listProduct.get(i).getPrice();
                array[1] += total;
            }
            return array;
        }


        @Override
        protected void onPostExecute(int[] ints) {
            progressDialog.dismiss();
            _textTotalItem.setText(String.valueOf(ints[0]) + " Item(s)");
            _textTotalPrice.setText("Rp " + String.valueOf(ints[1]));
            super.onPostExecute(ints);
        }
    }

    private void nullCartVisibility() {
        _textNullCart.setTextSize(30);
        _textNullCart.setTextColor(Color.parseColor("#558B2F"));
        _textNullCart.setVisibility(View.VISIBLE);
        _header.setVisibility(View.INVISIBLE);
        _footer.setVisibility(View.INVISIBLE);
    }

    public void removeCartHandler(View v) {
        final Product itemoToRemove = (Product) v.getTag();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CartActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle("Confirm Remove Item");
        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want remove this?");
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke YES event
                cartArrayList.removeFavorite(getApplicationContext(), itemoToRemove);
                adapter.remove(itemoToRemove);
                adapter.notifyDataSetChanged();
                task = new SetTotalAsynctask(context);
                task.execute(cartArrayList.getFavorites(context));
                if (listCart.isEmpty()) {
                    nullCartVisibility();
                }
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
    public void onBackPressed() {
        final Handler handler = new Handler();
        if (getFragmentManager().getBackStackEntryCount() > 0) {
//            disableNavIcon();
            getFragmentManager().popBackStack();
            if (getFragmentManager().getBackStackEntryCount() == 1) {
                enableNavIcon();
                setTitle(R.string.shop_cart);
                _linearMain.setVisibility(View.VISIBLE);
            }
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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