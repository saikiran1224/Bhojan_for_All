package com.kirandroid.bhojanforall.activities.general;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kirandroid.bhojanforall.R;
import com.kirandroid.bhojanforall.utilities.ConstantValues;


import java.util.Objects;

public class NearbyNGOActivity extends AppCompatActivity {

    EditText edtlocation;
    Button btnsubmit;
    String location;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_n_g_o);
        ConstantValues.internetCheck(NearbyNGOActivity.this);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are connected to a network
        assert connectivityManager != null;
        connected = Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED;
        if (!connected) {
            Toast.makeText(NearbyNGOActivity.this, "Network Unavailable", Toast.LENGTH_SHORT).show();
        }

        edtlocation = (EditText) findViewById(R.id.nearbyngolocation);
        btnsubmit = (Button) findViewById(R.id.nearbyngosubmit);

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = edtlocation.getText().toString().trim();
                if (location.isEmpty()) {
                    edtlocation.setError("Please enter the location");
                } else {
                    assert connectivityManager != null;
                    connected = Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                            Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED;
                    if (!connected) {
                        Toast.makeText(NearbyNGOActivity.this, "Network Unavailable", Toast.LENGTH_SHORT).show();
                    } else {
                        String url = "https://www.google.com/search?q=ngos%20near%20" + location;
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                }
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("View nearby NGOs");
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
