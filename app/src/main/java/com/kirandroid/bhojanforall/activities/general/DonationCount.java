package com.kirandroid.bhojanforall.activities.general;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.kirandroid.bhojanforall.R;
import com.kirandroid.bhojanforall.utilities.ConstantValues;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class DonationCount extends AppCompatActivity {

    private DatabaseReference myref;
    private EditText edtphone;
    private ImageView doncountPic;
    private Button btnsubmit;
    private String phone;
    boolean connected = false;
    private TextView doncount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_count);
        ConstantValues.internetCheck(DonationCount.this);

        doncountPic = (ImageView) findViewById(R.id.donCountPic);

        Glide.with(this).load(R.drawable.donation_count).into(doncountPic);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are connected to a network
        assert connectivityManager != null;
        connected = Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED;
        if (!connected) {
            Toast.makeText(DonationCount.this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
        }

        edtphone = (EditText) findViewById(R.id.doncountphone);
        btnsubmit = (Button) findViewById(R.id.doncountsubmit);
        doncount = (TextView) findViewById(R.id.doncountdisplay);

        doncount.setVisibility(View.INVISIBLE);

        myref = FirebaseDatabase.getInstance().getReference("Food_Details");

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = edtphone.getText().toString().trim();
                if (phone.isEmpty()) {
                    edtphone.setError("Please enter Phone Number");
                } else if (phone.length() != 10) {
                    edtphone.setError("Invalid Phone Number");
                } else {
                    assert connectivityManager != null;
                    connected = Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                            Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED;
                    if (!connected) {
                        Toast.makeText(DonationCount.this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
                    } else {
                        Query query = myref.orderByChild("phone").equalTo(phone);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    //int  = Integer.toString(dataSnapshot.getChildrenCount())
                                    String don_count = Long.toString(dataSnapshot.getChildrenCount());
                                    doncount.setVisibility(View.VISIBLE);
                                    doncount.setText("Total No. of Donations : " + don_count);
                                    //Toast.makeText(DonationCount.this, "Donations Count : " + don_count, Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(DonationCount.this, "No Donations Found", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(DonationCount.this, "Failed to Load.", Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                }

            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("View your Donations Count");
        }

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
