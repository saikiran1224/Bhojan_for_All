package com.kirandroid.bhojanforall.activities.recipients;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.kirandroid.bhojanforall.modals.Recipient;
import com.kirandroid.bhojanforall.utilities.MyAppPrefsManager;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RecipientLoginActivity extends AppCompatActivity {

    TextView login, reset;
    EditText edtemail, pwd;
    Button submit;
    DatabaseReference databaseReference;
    String dbpass;
    Boolean connected;
    private AdView mAdView;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    MyAppPrefsManager myAppPrefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orphanage_login);

        progressDialog = new ProgressDialog(RecipientLoginActivity.this);
        firebaseAuth = FirebaseAuth.getInstance();

        myAppPrefsManager = new MyAppPrefsManager(this);

        reset = (TextView) findViewById(R.id.reclogreset);

        edtemail = (EditText) findViewById(R.id.reclogemail);
        pwd = (EditText) findViewById(R.id.reclogpwd);
        submit = (Button) findViewById(R.id.reclogbutton);
        login = (TextView) findViewById(R.id.reclogregister);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipientLoginActivity.this, RecipientRegistrationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipientLoginActivity.this, RecipientResetPassword.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else {
            connected = false;
            Toast.makeText(RecipientLoginActivity.this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network

                    final String email = edtemail.getText().toString().trim();
                    final String pw = pwd.getText().toString().trim();

                    if (email.isEmpty()) {
                        edtemail.setError("Please enter Email ID");
                        //Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
                    } else if (pw.isEmpty()) {
                        Toast.makeText(RecipientLoginActivity.this, "Please enter password", Toast.LENGTH_LONG).show();
                        //t2.setError("Please enter Password");
                    } else {

                        //     Toast.makeText(RecipientLoginActivity.this, "" + email, Toast.LENGTH_SHORT).show();
                        databaseReference = FirebaseDatabase.getInstance().getReference("Organization_Details");
                        Query query = databaseReference.orderByChild("email").equalTo(email);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                        final String category = dataSnapshot1.getValue(Recipient.class).getCategory();
                                        //   Toast.makeText(RecipientLoginActivity.this, "" + category, Toast.LENGTH_SHORT).show();
                                        if (category.equals("Recipient")) {
                                            userLogin(email, pw);
                                        }
                                    }
                                } else {
                                    Toast.makeText(RecipientLoginActivity.this, "Invalid User !!!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(RecipientLoginActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                } else {
                    Toast.makeText(RecipientLoginActivity.this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
                }
            }
        });


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Recipient Login");
        }
    }


    private void userLogin(String email, String pw) {
        //Valid Recipient
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {

                            progressDialog.dismiss();
                            Toast.makeText(RecipientLoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();

                        } else {

                            Intent intent = new Intent(RecipientLoginActivity.this, RecipientActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            Toast.makeText(RecipientLoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
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
