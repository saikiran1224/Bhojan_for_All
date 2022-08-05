package com.kirandroid.bhojanforall.activities.administrator;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kirandroid.bhojanforall.R;
import com.kirandroid.bhojanforall.modals.Volunteer;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AdminActivity extends AppCompatActivity {

    TextView txtName, txtPhone, txtEmail, txtCount, txtNormal, txtHigh;
    Button btnSearch;
    EditText edtPhone;
    String name, email, phone;
    int count;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Administrator");
        }

        txtName = (TextView) findViewById(R.id.admintxtName);
        txtPhone = (TextView) findViewById(R.id.admintxtPhone);
        txtEmail = (TextView) findViewById(R.id.admintxtEmail);
        txtCount = (TextView) findViewById(R.id.admintxtCount);
        txtNormal = (TextView) findViewById(R.id.admintxtNormal);
        txtHigh = (TextView) findViewById(R.id.admintxtHigh);
        edtPhone = (EditText) findViewById(R.id.adminedtPhone);
        btnSearch = (Button) findViewById(R.id.adminbtnSearch);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Volunteers");

        //keep the data in Offline Mode also
        databaseReference.keepSynced(true);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String volPhone = edtPhone.getText().toString().trim();

                if (volPhone.isEmpty()) {
                    edtPhone.setError("Please enter Volunteer Phone Number ");
                } else if (volPhone.length() < 10) {
                    edtPhone.setError("Phone Number is Invalid !");

                } else {
                    //Toast.makeText(AdminActivity.this, ""+volphone, Toast.LENGTH_SHORT).show();

                    Query query = databaseReference.orderByChild("phone").equalTo(volPhone);

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                    Volunteer volunteer = dataSnapshot1.getValue(Volunteer.class);
                                    if (volunteer != null) {
                                        name = volunteer.getName();
                                        email = volunteer.getEmail();
                                        phone = volunteer.getPhone();
                                        count = volunteer.getCount();
                                    } else {
                                        Toast.makeText(AdminActivity.this, "No Data Found !", Toast.LENGTH_SHORT).show();
                                    }

                                }
                                txtName.setText(name);
                                txtPhone.setText(phone);
                                txtEmail.setText(email);
                                txtCount.setText(String.valueOf(count));

                                if (count > 10) {
                                    txtHigh.setVisibility(View.VISIBLE);
                                } else {
                                    txtNormal.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(AdminActivity.this, "No Data Found !", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(AdminActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


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
