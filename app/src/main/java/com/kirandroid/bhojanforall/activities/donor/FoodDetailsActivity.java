package com.kirandroid.bhojanforall.activities.donor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.kirandroid.bhojanforall.R;
import com.kirandroid.bhojanforall.modals.Fooddetails;
import com.kirandroid.bhojanforall.utilities.ConstantValues;
import com.kirandroid.bhojanforall.utilities.Dialog;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FoodDetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public Button submit;
    private FirebaseAuth firebaseAuth;
    private TextView userEmail;
    private ImageView foodDetailsPic;
    DatabaseReference reff;
    DatabaseReference ref;
    EditText edtnam, edtphone, spin, edtadd, edtfoodcanfeed;
    Fooddetails fooddetails;
    boolean connected = false;
    FirebaseAuth mAuth;
    String name, place, address, phon, foodno, tim, currdate, codesent;

    private static String TAG = "TOKENS_DATA";

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + ConstantValues.AUTH_KEY_FCM;
    final private String contentType = "application/json";
    String TOPIC;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food__details);
        firebaseAuth = FirebaseAuth.getInstance();

        foodDetailsPic = (ImageView) findViewById(R.id.foodDetailsPic);

        edtnam = (EditText) findViewById(R.id.fooddonname);
        edtphone = (EditText) findViewById(R.id.fooddonphone);
        edtadd = (EditText) findViewById(R.id.fooddonadd);
        edtfoodcanfeed = (EditText) findViewById(R.id.fooddonfeed);
        final Spinner time = findViewById(R.id.foodtime);
        final Spinner spinner = findViewById(R.id.foodspin);
        submit = (Button) findViewById(R.id.fooddonsubmit);

        mAuth = FirebaseAuth.getInstance();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Donate to Poor");
        }

        Glide.with(this).load(R.drawable.donate).into(foodDetailsPic);
        ref = (DatabaseReference) FirebaseDatabase.getInstance().getReference("Volunteers");

        reff = FirebaseDatabase.getInstance().getReference().child("Food_Details");

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/userABC1");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.placetype, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adap = ArrayAdapter.createFromResource(this, R.array.timetaken, android.R.layout.simple_spinner_dropdown_item);
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time.setAdapter(adap);
        time.setOnItemSelectedListener(this);

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/userABC1");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                place = spinner.getSelectedItem().toString();
                name = edtnam.getText().toString().trim();
                phon = edtphone.getText().toString().trim();
                address = edtadd.getText().toString().trim();
                tim = time.getSelectedItem().toString();
                foodno = edtfoodcanfeed.getText().toString().trim();

                Date cd = Calendar.getInstance().getTime();
                System.out.println("Current time => " + cd);
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                currdate = df.format(cd);

                if (name.isEmpty()) {
                    Toast.makeText(FoodDetailsActivity.this, "Please enter Name", Toast.LENGTH_LONG).show();
                } else if (phon.isEmpty()) {
                    Toast.makeText(FoodDetailsActivity.this, "Please enter Phone Number", Toast.LENGTH_LONG).show();
                } else if (phon.length() < 10) {
                    Toast.makeText(FoodDetailsActivity.this, "Phone Number is Invalid", Toast.LENGTH_SHORT).show();
                } else if (address.isEmpty()) {
                    Toast.makeText(FoodDetailsActivity.this, "Please enter Address", Toast.LENGTH_LONG).show();
                } else if (foodno.isEmpty()) {
                    Toast.makeText(FoodDetailsActivity.this, "Please enter Food can feed Number", Toast.LENGTH_SHORT).show();
                } else if (foodno.equals("0")) {
                    Toast.makeText(FoodDetailsActivity.this, "Please enter Number greater than 1", Toast.LENGTH_SHORT).show();
                } else if (place.equals("Choose Type of Place")) {
                    Toast.makeText(FoodDetailsActivity.this, "Please choose Type of Place", Toast.LENGTH_LONG).show();
                } else if (tim.equals("Cooked Before")) {
                    Toast.makeText(FoodDetailsActivity.this, "Please select Time of Period", Toast.LENGTH_LONG).show();
                } else {

                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        sendVerificationCode("+91" + phon);
                        Toast.makeText(FoodDetailsActivity.this, "One Time Password has been sent to Your Mobile Number " + "+91" + phon, Toast.LENGTH_SHORT).show();
                        connected = true;
                    } else {
                        Toast.makeText(FoodDetailsActivity.this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
                        connected = false;
                    }
                }
            }
        });
    }

    public void sendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                reff.push().setValue(new Fooddetails(name, phon, address, place, "", tim, currdate, foodno));
                try {

                    RequestQueue queue = Volley.newRequestQueue(FoodDetailsActivity.this);

                    String url = "https://fcm.googleapis.com/fcm/send";

                    String m = "Mr./Mrs." + name + " is willing to Donate Food from a ";
                    String num = m + place + " which can be fed to " + foodno + " person(s)";
                    String res = num + "\nAddress is " + address;
                    String add = res + "\nFood Cooked Before :" + tim;
                    String fina = add + "\nContact: " + phon;

                    TOPIC = "/topics/donateFood";
                    JSONObject data = new JSONObject();
                    data.put("title", "Food Ready to Donate");
                    data.put("message", fina);
                    Log.e(TAG, "" + data);

                    JSONObject notification_data = new JSONObject();
                    notification_data.put("data", data);
                    notification_data.put("to", TOPIC);

                    Log.e(TAG, "" + notification_data);


                    JsonObjectRequest request = new JsonObjectRequest(url, notification_data, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() {

                            Map<String, String> params = new HashMap<>();
                            params.put("Authorization", serverKey);
                            params.put("Content-Type", contentType);
                            return params;
                        }
                    };

                    queue.add(request);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(FoodDetailsActivity.this, "Food Donation details Submitted Successfully !", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(OTP_ValidationActivity.this, MainActivity.class));
                openDialog();
            } else {
                Toast.makeText(FoodDetailsActivity.this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // [START_EXCLUDE]
                Toast.makeText(FoodDetailsActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            } else if (e instanceof FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // [START_EXCLUDE]
                /*Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                        Snackbar.LENGTH_SHORT).show();*/
                Toast.makeText(FoodDetailsActivity.this, "SMS Quota Exceeded", Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            } else {
                Toast.makeText(FoodDetailsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codesent = s;
            goToNext();
        }
    };

    public void goToNext() {
        Intent intent = new Intent(FoodDetailsActivity.this, OTP_ValidationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("phone", phon);
        bundle.putString("address", address);
        bundle.putString("place", place);
        bundle.putString("foodno", foodno);
        bundle.putString("time", tim);
        bundle.putString("date", currdate);
        bundle.putString("code", codesent);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void openDialog() {
        Dialog d = new Dialog();
        d.show(getSupportFragmentManager(), "Dialog");
    }

}
