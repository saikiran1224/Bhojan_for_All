
package com.kirandroid.bhojanforall.activities.volunteer;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kirandroid.bhojanforall.R;
import com.kirandroid.bhojanforall.activities.administrator.AdminActivity;
import com.kirandroid.bhojanforall.activities.general.SthreeRaksha;
import com.kirandroid.bhojanforall.modals.Volunteer;
import com.kirandroid.bhojanforall.utilities.ConstantValues;
import com.kirandroid.bhojanforall.utilities.MyAppPrefsManager;

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

import java.util.Objects;

public class VolunteerLoginActivity extends AppCompatActivity {
    public Button b1, b2;
    EditText t1, t2;
    String email, pwd;
    private FirebaseAuth firebaseAuth;
    MyAppPrefsManager myAppPrefsManager;
    private ProgressDialog progressDialog;
    boolean connected = false;
    TextView reset;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth = FirebaseAuth.getInstance();
        myAppPrefsManager = new MyAppPrefsManager(VolunteerLoginActivity.this);
        ConstantValues.internetCheck(VolunteerLoginActivity.this);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are connected to a network
        assert connectivityManager != null;
        connected = Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)).getState() == NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)).getState() == NetworkInfo.State.CONNECTED;
        if (!connected) {
            Toast.makeText(VolunteerLoginActivity.this, "Network Unavailable", Toast.LENGTH_SHORT).show();
        }

        progressDialog = new ProgressDialog(this);
        t1 = (EditText) findViewById(R.id.volunlogemail);
        t2 = (EditText) findViewById(R.id.volunlogpwd);
        b1 = (Button) findViewById(R.id.volunlogsubmit);
        reset = (TextView) findViewById(R.id.volunlogreset);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connected == true) {
                    Validate();
                } else {
                    Toast.makeText(VolunteerLoginActivity.this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
                }

            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Volunteer Login");
        }

        findViewById(R.id.volunlogregister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VolunteerLoginActivity.this, VolunteerRegistrationActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VolunteerLoginActivity.this, ResetPasswordActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
            }
        });
    }

    private void Validate() {
        email = t1.getText().toString().trim();
        pwd = t2.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (email.equals("admin@gmail.com") && pwd.equals("Admin@123")) {
            Intent i = new Intent(VolunteerLoginActivity.this, AdminActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
            progressDialog.dismiss();
        }

        if (email.isEmpty()) {
            t1.setError("Please enter Email ID");
            //Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
        } else if (!email.matches(emailPattern)) {
            t1.setError("Email is Invalid");
        } else if (pwd.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            //t2.setError("Please enter Password");
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference("Volunteers");
            Query query = databaseReference.orderByChild("email").equalTo(email);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            final String category = dataSnapshot1.getValue(Volunteer.class).getCategory();
                            //   Toast.makeText(RecipientLoginActivity.this, "" + category, Toast.LENGTH_SHORT).show();
                            if (category.equals("Volunteer")) {
                                userLogin(email, pwd);
                            }
                        }
                    } else {
                        Toast.makeText(VolunteerLoginActivity.this, "Invalid User !!!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(VolunteerLoginActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    private void userLogin(String email, String pwd) {
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {

                            progressDialog.dismiss();
                            Toast.makeText(VolunteerLoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();

                        } else {
                            myAppPrefsManager.setUserLoggedIn(true);
                            myAppPrefsManager.setUserName(email);

                            // Set isLogged_in of ConstantValues
                            ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();


                            Intent intent = new Intent(VolunteerLoginActivity.this, VolunteerActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            Toast.makeText(VolunteerLoginActivity.this, "Sign In Success", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();


                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alert, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        if (id == R.id.action_alertvollog) {
            Intent emergency = new Intent(VolunteerLoginActivity.this, SthreeRaksha.class);
            emergency.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(emergency);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}