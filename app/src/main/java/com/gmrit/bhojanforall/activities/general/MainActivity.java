package com.gmrit.bhojanforall.activities.general;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.gmrit.bhojanforall.R;
import com.gmrit.bhojanforall.activities.donor.DonorCelebrateActivity;
import com.gmrit.bhojanforall.activities.donor.FoodDetailsActivity;
import com.gmrit.bhojanforall.activities.recipients.RecipientLoginActivity;
import com.gmrit.bhojanforall.activities.volunteer.VolunteerActivity;
import com.gmrit.bhojanforall.activities.volunteer.VolunteerLoginActivity;
import com.gmrit.bhojanforall.adapter.BannerAdapter;
import com.gmrit.bhojanforall.modals.Banners;
import com.gmrit.bhojanforall.recyclerView.RecyclerViewGallery;
import com.gmrit.bhojanforall.utilities.ConstantValues;
import com.gmrit.bhojanforall.utilities.MyAppPrefsManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public Button b1, b2;
    private FirebaseAuth firebaseAuth;
    private DrawerLayout drawer;
    public TextView t, textView;
    MyAppPrefsManager myAppPrefsManager;


    boolean doubleBackToExitPressedOnce = false;

    SliderView sliderImage;

    List<Banners> modelList = new ArrayList<>();

    private AdView mAdView;
    private static final String TAG = "MAIN_ACTIVITY";
    InterstitialAd interstitialAd = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, getString(R.string.admob_app_id));


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Bhojan for All");


        drawer = findViewById(R.id.drawer_layout);

        sliderImage = findViewById(R.id.slider_image);


        myAppPrefsManager = new MyAppPrefsManager(this);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        firebaseAuth = FirebaseAuth.getInstance();
        listAllFiles();


        mAdView = new AdView(MainActivity.this);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(getString(R.string.banner_home_footer));

        mAdView = (AdView) findViewById(R.id.adView);


        AdRequest adRequest = new AdRequest.Builder().build();


        interstitialAd = new InterstitialAd(MainActivity.this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen));
        AdRequest adRequest1 = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest1);

        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

            }

            @Override
            public void onAdClosed() {

            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

            }

            @Override
            public void onAdLeftApplication() {

            }


            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("/topics/donateFood")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "getString(R.string.msg_subscribed)";
                        if (!task.isSuccessful()) {
                            msg = "getString(R.string.msg_subscribe_failed)";
                        }

                    }
                });

        findViewById(R.id.volun).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn()) {
                        Intent intent = new Intent(getBaseContext(), VolunteerActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else {

                        Intent intent = new Intent(getBaseContext(), VolunteerLoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.rest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, FoodDetailsActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

    }


    public void listAllFiles() {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("bannerImages");
        StorageReference storageRef1 = storage.getReference();


        storageRef.listAll()
                .addOnSuccessListener(listResult -> {


                    modelList.clear();
                    for (StorageReference item : listResult.getItems()) {
                        // All the items under listRef.

                        // [START download_via_url]
                        storageRef1.child(item.getPath()).getDownloadUrl().addOnSuccessListener(uri -> {

                            modelList.add(new Banners(item.getName(), uri.toString()));
                            BannerAdapter adapter = new BannerAdapter(MainActivity.this, modelList);
                            sliderImage.setSliderAdapter(adapter);
                            sliderImage.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                            sliderImage.setSliderTransformAnimation(SliderAnimations.CUBEINROTATIONTRANSFORMATION);
                            sliderImage.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
                            sliderImage.setIndicatorRadius(5);
                            sliderImage.setIndicatorSelectedColor(Color.WHITE);
                            sliderImage.setIndicatorUnselectedColor(Color.GRAY);
                            sliderImage.startAutoCycle();
                            sliderImage.setOnIndicatorClickListener(position ->
                                    sliderImage.setCurrentPagePosition(position));

                        }).addOnFailureListener(exception -> {
                            // Handle any errors

                        });


                    }


                })
                .addOnFailureListener(e -> {
                    // Uh-oh, an error occurred!

                });


    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_about:
                Intent b = new Intent(MainActivity.this, IssuesActivity.class);
                b.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(b);
                break;
            case R.id.nav_info:
                Intent a = new Intent(MainActivity.this, AboutActivity.class);
                a.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(a);
                break;
            case R.id.nav_gallery:
                Intent g = new Intent(MainActivity.this, RecyclerViewGallery.class);
                g.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(g);
                break;
            case R.id.nav_org:
                Intent intent = new Intent(MainActivity.this, RecipientLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.nav_happy:
                Intent intent1 = new Intent(MainActivity.this, DonorCelebrateActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                break;
            case R.id.nav_doncount:
                Intent intent3 = new Intent(MainActivity.this, DonationCount.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent3);
                break;
            case R.id.nav_nearby:
                Intent intent4 = new Intent(MainActivity.this, NearbyNGOActivity.class);
                intent4.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent4);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {

            if (interstitialAd.isLoaded()) {
                interstitialAd.show();
                interstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        finish();
                    }
                });
            } else {
                super.onBackPressed();
            }
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {


                doubleBackToExitPressedOnce = false;


            }
        }, 2000);
    }


    @Override
    public void onPause() {
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

}
