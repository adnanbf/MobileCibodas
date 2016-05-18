package com.example.ngapap.cibodas.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.ngapap.cibodas.Activity.CartActivity;
import com.example.ngapap.cibodas.Activity.MenuActivity;
import com.example.ngapap.cibodas.Model.Reservation;
import com.example.ngapap.cibodas.R;
import com.example.ngapap.cibodas.SessionManager;

/**
 * Created by user on 05/05/2016.
 */
public class PaymentReservFragment extends Fragment {
    private EditText _inputBank;
    private EditText _inputAccount;
    private Button _btnConfirm;
    String bankName = "BANK_NAME";
    SessionManager sessionManager;
    Reservation reservation;
    private int selectedBank = -1;
    private FragmentTransaction ft;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            selectedBank = savedInstanceState.getInt("curChoice", -1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_payment_reserv, null);
        //setComponent
        _inputBank = (EditText) rootView.findViewById(R.id.input_bank);
        _inputAccount = (EditText) rootView.findViewById(R.id.input_account);
        _btnConfirm = (Button) rootView.findViewById(R.id.btn_confirm);
        if (savedInstanceState != null) {
            selectedBank = savedInstanceState.getInt("curChoice", -1);
        }
        if (((CartActivity) getActivity()).getReservation() == null) {
            reservation = new Reservation();
        } else {
            reservation = ((CartActivity) getActivity()).getReservation();
            _inputBank.setText(((CartActivity) getActivity()).getReservation().getBank_name());
            _inputAccount.setText(((CartActivity) getActivity()).getReservation().getBank_account());
        }
        _inputAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    ((MenuActivity) getActivity()).hideKeyboard(v);
                }
            }
        });


        //setListener
        _inputBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBank();
            }
        });
        ft = getFragmentManager().beginTransaction();
        _btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    Log.d("from button confirm:", _inputAccount.getText().toString());
                    reservation.setBank_account(_inputAccount.getText().toString());
                    ((CartActivity) getActivity()).setReservation(reservation);
                    ft.replace(R.id.frame, new ReviewReservFragment());
                    ft.addToBackStack(PaymentReservFragment.class.getName());
                    ft.commit();
                }
            }
        });

        return rootView;
    }

    private void dialogBank() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final CharSequence[] items = {"Bank Mandiri", "BCA", "BNI", "BRI"};
        builder.setTitle("Pilih Bank Anda").setCancelable(false)
                .setSingleChoiceItems(items, selectedBank, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        selectedBank = which;
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                int selectedPost = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                switch (selectedBank) {
                    case 0:
                        _inputBank.setText("Bank Mandiri");
                        _inputBank.setFreezesText(true);
                        reservation.setBank_name("Bank Mandiri");
                        bankName = "Bank Mandiri";
                        break;
                    case 1:
                        _inputBank.setText("BCA");
                        _inputBank.setFreezesText(true);
                        reservation.setBank_name("BCA");
                        bankName = "BCA";
                        break;
                    case 2:
                        _inputBank.setText("BNI");
                        _inputBank.setFreezesText(true);
                        reservation.setBank_name("BNI");
                        bankName = "BNI";
                        break;
                    case 3:
                        _inputBank.setText("BRI");
                        _inputBank.setFreezesText(true);
                        reservation.setBank_name("BRI");
                        bankName = "BRI";
                        break;
                    default:
                        Log.d("From PaymentReserv", String.valueOf(selectedBank));
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create();
        builder.show();
    }

    private boolean validate() {
        boolean isValid = true;
        String bankName = _inputBank.getText().toString();
        String bankAcc = _inputAccount.getText().toString();
        if (bankName.isEmpty()) {
            _inputBank.setError("Please Select Your Bank");
            isValid = false;
        } else {
            _inputBank.setError(null);
        }
        if (bankAcc.isEmpty() || bankAcc.length() < 5) {
            _inputAccount.setError("At Least 5 Characters");
            isValid = false;
        } else {
            _inputAccount.setError(null);
        }
        return isValid;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            // Restore last state for checked position.
            selectedBank = savedInstanceState.getInt("curChoice", -1);
            bankName = savedInstanceState.getString("bankName");
            reservation = (Reservation) savedInstanceState.getSerializable("reservation");
            Log.d("From onActivityCreated", bankName);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("curChoice", selectedBank);
        outState.putString("bankName", bankName);
        outState.putSerializable("reservation", reservation);
    }


}
