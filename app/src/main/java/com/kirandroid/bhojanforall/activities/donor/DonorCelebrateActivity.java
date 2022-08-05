package com.kirandroid.bhojanforall.activities.donor;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kirandroid.bhojanforall.R;
import com.kirandroid.bhojanforall.modals.Happy;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class DonorCelebrateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText don_name, don_email, don_phone, don_money, don_add, don_date;
    String name, email, phone, money, recip, key, address, date, occasion;
    Button submit;
    String admin_1, admin_2, admin_3, msg;
    DatabaseReference myref;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happy);

        don_name = (EditText) findViewById(R.id.celebdonname);
        final Spinner spinneroccasion = findViewById(R.id.celeboccasionspin);
        don_phone = (EditText) findViewById(R.id.celebdonphone);
        don_email = (EditText) findViewById(R.id.celebdonemail);
        don_add = (EditText) findViewById(R.id.celebdonaddress);
        don_date = (EditText) findViewById(R.id.celebdate);
        final Spinner spinner = findViewById(R.id.celebspin);
        don_money = (EditText) findViewById(R.id.celebdonmoney);
        submit = (Button) findViewById(R.id.celebsubmit);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Celebrate Your Occasion");
        }

        myref = FirebaseDatabase.getInstance().getReference().child("Happy_Moments");

        admin_1 = "9381384234";
        admin_2 = "8639796138";
        admin_3 = "6303149161";

        don_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(DonorCelebrateActivity.this, new DatePickerDialog.OnDateSetListener() {
                    //DatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        don_date.setText("" + (month + 1) + "/" + dayOfMonth + "/" + year);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        final ArrayAdapter<CharSequence> occasionadapter = ArrayAdapter.createFromResource(DonorCelebrateActivity.this, R.array.occasion, android.R.layout.simple_spinner_dropdown_item);
        occasionadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinneroccasion.setAdapter(occasionadapter);
        spinneroccasion.setOnItemSelectedListener(DonorCelebrateActivity.this);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.recipient, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(DonorCelebrateActivity.this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = don_name.getText().toString().trim();
                occasion = spinneroccasion.getSelectedItem().toString();
                email = don_email.getText().toString().trim();
                phone = don_phone.getText().toString().trim();
                money = don_money.getText().toString().trim();
                recip = spinner.getSelectedItem().toString();
                address = don_add.getText().toString().trim();
                date = don_date.getText().toString().trim();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (name.isEmpty()) {
                    don_name.setError("Please enter Name !");
                } else if (occasion.equals("Select Occasion")) {
                    Toast.makeText(DonorCelebrateActivity.this, "Please select Occasion", Toast.LENGTH_SHORT).show();
                } else if (phone.isEmpty()) {
                    don_phone.setError("Please enter Phone Number ");
                } else if (phone.length() < 10) {
                    don_phone.setError("Phone Number is Invalid");
                } else if (email.isEmpty()) {
                    don_email.setError("Please enter Email ID");
                } else if (!email.matches(emailPattern)) {
                    don_email.setError("Email is Invalid");
                } else if (address.isEmpty()) {
                    don_add.setError("Please enter Address ");
                } else if (date.isEmpty()) {
                    Toast.makeText(DonorCelebrateActivity.this, "Please choose Date of Celebration", Toast.LENGTH_SHORT).show();
                } else if (recip.equals("Select Recipient")) {
                    Toast.makeText(DonorCelebrateActivity.this, "Please select Recipient", Toast.LENGTH_SHORT).show();
                } else if (money.isEmpty()) {
                    don_money.setError("Please enter Money");
                } else if (money.equals("0")) {
                    Toast.makeText(DonorCelebrateActivity.this, "Please enter an amount grater than 0", Toast.LENGTH_SHORT).show();
                } else {

                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        Happy happy_1_modal = new Happy(name, occasion, email, phone, money, recip, address, date);
                        myref.push().setValue(happy_1_modal);

                        msg = "Dear Administrator,\n" + name + " is ready to donate a sum of ";
                        String res = msg + "Rs." + money + " to " + recip + " for " + occasion + " occasion";
                        String re = res + ", Please collect Money at " + address;

                        admin_1 = "9381384234";
                        admin_2 = "8639796138";
                        admin_3 = "6303149161";

                        Uri sendSmsTo = Uri.parse("smsto:" + admin_1 + ";" + admin_2 + ";" + admin_3);
                        Intent intent = new Intent(
                                Intent.ACTION_SENDTO, sendSmsTo);
                        intent.putExtra("sms_body", re);
                        startActivity(intent);
                        connected = true;
                    } else {
                        connected = false;
                        Toast.makeText(DonorCelebrateActivity.this, "Internet Unavailable", Toast.LENGTH_LONG).show();
                    }
                    // myref = FirebaseDatabase.getInstance().getReference().child("Happy_Moments").child(key);
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
