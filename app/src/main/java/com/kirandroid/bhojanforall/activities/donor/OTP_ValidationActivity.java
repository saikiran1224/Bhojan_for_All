package com.kirandroid.bhojanforall.activities.donor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.kirandroid.bhojanforall.R;
import com.kirandroid.bhojanforall.modals.Fooddetails;
import com.kirandroid.bhojanforall.utilities.ConstantValues;
import com.kirandroid.bhojanforall.utilities.Dialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OTP_ValidationActivity extends AppCompatActivity {

    EditText edtotp;
    TextView txttitle;
    Button btnvalidate;
    FirebaseAuth mAuth;
    DatabaseReference reff;
    String name, place, address, phon, foodno, tim, currdate, recvdotp;
    boolean connected = false;

    private static String TAG = "TOKENS_DATA";

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + ConstantValues.AUTH_KEY_FCM;
    final private String contentType = "application/json";
    String TOPIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p__validation);
        this.setTitle("OTP Verification");
        edtotp = (EditText) findViewById(R.id.otp);
        txttitle = (TextView) findViewById(R.id.title);
        btnvalidate = (Button) findViewById(R.id.validate);

        mAuth = FirebaseAuth.getInstance();
        reff = FirebaseDatabase.getInstance().getReference().child("Food_Details");

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        recvdotp = bundle.getString(getString(R.string.code));
        name = bundle.getString(getString(R.string.bun_name));
        place = bundle.getString(getString(R.string.bun_place));
        address = bundle.getString(getString(R.string.bun_address));
        phon = bundle.getString(getString(R.string.bun_phone));
        foodno = bundle.getString(getString(R.string.foodno));
        tim = bundle.getString(getString(R.string.bun_time));
        currdate = bundle.getString(getString(R.string.currentdate));

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/userABC1");

        btnvalidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entered_otp = edtotp.getText().toString().trim();

                if (entered_otp.isEmpty()) {
                    edtotp.setError("Please enter OTP");
                    edtotp.requestFocus();
                } else {
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        connected = true;
                        verifySignInCode(entered_otp);
                    } else {
                        connected = false;
                        Toast.makeText(OTP_ValidationActivity.this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void verifySignInCode(String enteredotp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(recvdotp, enteredotp);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            signInWithPhoneAuthCredential(credential);
            connected = true;
        } else {
            Toast.makeText(this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
        }

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            reff.push().setValue(new Fooddetails(name, phon, address, place, "", tim, currdate, foodno));


                            try {

                                RequestQueue queue = Volley.newRequestQueue(OTP_ValidationActivity.this);

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

                            Toast.makeText(OTP_ValidationActivity.this, "Food Donation details Submitted Successfully !", Toast.LENGTH_SHORT).show();
                            //startActivity(new Intent(OTP_ValidationActivity.this, MainActivity.class));
                            openDialog();
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(OTP_ValidationActivity.this, "Invalid OTP Entered", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    public void openDialog() {
        Dialog d = new Dialog();
        d.show(getSupportFragmentManager(), "Dialog");
    }
}
