package com.kirandroid.bhojanforall.activities.volunteer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kirandroid.bhojanforall.R;
import com.kirandroid.bhojanforall.modals.Volunteer;
import com.kirandroid.bhojanforall.utilities.ConstantValues;
import com.kirandroid.bhojanforall.utilities.MyAppPrefsManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class VolunteerProfileActivity extends AppCompatActivity {

    TextView name, phone, email, inc;
    String mail, nam, ph, em;
    int cou;
    DatabaseReference databaseReference;
    MyAppPrefsManager myAppPrefsManager;
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        overridePendingTransition(0, 0);
        ConstantValues.internetCheck(VolunteerProfileActivity.this);
        myAppPrefsManager = new MyAppPrefsManager(VolunteerProfileActivity.this);
        mail = myAppPrefsManager.getUserName();

        name = (TextView) findViewById(R.id.volunprofilename);
        phone = (TextView) findViewById(R.id.volunprofilephone);
        email = (TextView) findViewById(R.id.volunprofileemail);
        inc = (TextView) findViewById(R.id.volunprofilecount);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Volunteer Profile");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Volunteers");
        databaseReference.keepSynced(true);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //connected to network
            Query query = databaseReference.orderByChild("email").equalTo(mail);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            nam = Objects.requireNonNull(dataSnapshot1.getValue(Volunteer.class)).getName();
                            ph = Objects.requireNonNull(dataSnapshot1.getValue(Volunteer.class)).getPhone();
                            em = Objects.requireNonNull(dataSnapshot1.getValue(Volunteer.class)).getEmail();
                            cou = Objects.requireNonNull(dataSnapshot1.getValue(Volunteer.class)).getCount();
                        }
                        name.setText(nam);
                        phone.setText(ph);
                        email.setText(em);
                        inc.setText("Total No. of Deliveries : " + cou);
                    } else {
                        Toast.makeText(VolunteerProfileActivity.this, "Fetching Details....This may take some time!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(VolunteerProfileActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
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
