package com.kirandroid.bhojanforall.activities.recipients;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kirandroid.bhojanforall.R;
import com.kirandroid.bhojanforall.modals.Recipient;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RecipientRegistrationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView signin;
    private EditText name, edtemail, pwd, phone, address;
    Button register;
    DatabaseReference databaseReference;
    Boolean connected;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    String TAG = "TOKENS_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orphange_registration);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(RecipientRegistrationActivity.this);

        name = (EditText) findViewById(R.id.recregname);
        final Spinner spinner = findViewById(R.id.recregspin);
        edtemail = (EditText) findViewById(R.id.recregemail);
        pwd = (EditText) findViewById(R.id.recregpwd);
        phone = (EditText) findViewById(R.id.recregphone);
        address = (EditText) findViewById(R.id.recregaddress);
        register = (Button) findViewById(R.id.recregregister);
        signin = (TextView) findViewById(R.id.recregsignin);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.organization_type, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else {
            connected = false;
            Toast.makeText(RecipientRegistrationActivity.this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Recipient Registration");
        }

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipientRegistrationActivity.this, RecipientLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nam = name.getText().toString().trim();
                String orgtype = spinner.getSelectedItem().toString();
                String email = edtemail.getText().toString().trim();
                String pw = pwd.getText().toString().trim();
                String ph = phone.getText().toString().trim();
                String add = address.getText().toString().trim();
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Organization_Details");

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (nam.isEmpty()) {
                    name.setError("Please enter Organization Name");
                } else if (orgtype.isEmpty()) {
                    Toast.makeText(RecipientRegistrationActivity.this, "Please choose Type !", Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    edtemail.setError("Email ID should not be Empty");
                } else if (!email.matches(emailPattern)) {
                    edtemail.setError("Please enter Valid Email ID");
                } else if (pw.isEmpty()) {
                    pwd.setError("Password should not be Empty");
                } else if (pw.length() < 8) {
                    pwd.setError("Password should be minimum of 8 Characters");
                } else if (ph.isEmpty()) {
                    phone.setError("Please enter Phone Number");
                } else if (ph.length() < 10) {
                    phone.setError("Phone number is invalid !");
                } else if (add.isEmpty()) {
                    address.setError("Please enter Address ");
                } else if (add.length() < 10) {
                    address.setError("Address should be minimum of 10 Characters !");
                } else {
                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        registerUser(nam, orgtype, email, pw, ph, add);
                    } else {
                        Toast.makeText(RecipientRegistrationActivity.this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void registerUser(String nam, String orgtype, String email, String password, String ph, String add) {

        progressDialog.setMessage("Registering, Please Wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        String id = databaseReference.push().getKey();


        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            progressDialog.dismiss();

                            Recipient orphanage_modal = new Recipient(nam, orgtype, email, password, ph, add, "Recipient");
                            databaseReference.child(id).setValue(orphanage_modal);

                            Intent i = new Intent(RecipientRegistrationActivity.this, RecipientLoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            Toast.makeText(RecipientRegistrationActivity.this, "Recipient Registered Successfully!", Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                progressDialog.dismiss();
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                progressDialog.dismiss();
                                Toast.makeText(RecipientRegistrationActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();

                            } catch (FirebaseAuthUserCollisionException existEmail) {
                                progressDialog.dismiss();
                                Toast.makeText(RecipientRegistrationActivity.this, "Recipient Email Id Already Exists", Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(RecipientRegistrationActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
