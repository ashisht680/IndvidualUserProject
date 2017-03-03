package com.javinindia.individualuser.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.javinindia.individualuser.R;
import com.javinindia.individualuser.fragments.CheckConnectionFragment;
import com.javinindia.individualuser.fragments.HomeFragment;
import com.javinindia.individualuser.fragments.LocationSearchFragment;
import com.javinindia.individualuser.location.NewLoc;
import com.javinindia.individualuser.preference.SharedPreferencesManager;
import com.javinindia.individualuser.utility.CheckConnection;

import java.util.List;


/**
 * Created by Ashish on 12-09-2016.
 */
public class NavigationActivity extends BaseActivity implements LocationSearchFragment.OnCallBackListener,CheckConnectionFragment.OnCallBackInternetListener {
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    protected Context context;
    private GoogleApiClient googleApiClient;
    final static int REQUEST_LOCATION = 199;
    double latitude = 0.0;
    double longitude = 0.0;
    NewLoc gps;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        methodLocation();
        String username = SharedPreferencesManager.getUsername(getApplicationContext());
        if (CheckConnection.haveNetworkConnection(this)) {
            mFragmentManager = getSupportFragmentManager();
            mFragmentTransaction = mFragmentManager.beginTransaction().setCustomAnimations(0, 0, 0, 0);
            mFragmentTransaction.replace(R.id.navigationContainer, new HomeFragment()).commit();
        } else {
            methodCallCheckInternet();
        }
    }

    public void methodCallCheckInternet() {
        CheckConnectionFragment fragment = new CheckConnectionFragment();
        fragment.setMyCallBackInternetListener(this);
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction().setCustomAnimations(0, 0, 0, 0);
        mFragmentTransaction.replace(R.id.navigationContainer, fragment).commit();
    }

    private void methodLocation() {
        if (!hasGPSDevice(NavigationActivity.this)) {
            Toast.makeText(NavigationActivity.this, "Gps not Supported", Toast.LENGTH_SHORT).show();
        }
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(NavigationActivity.this)) {
            Toast.makeText(NavigationActivity.this, "Gps not enabled", Toast.LENGTH_SHORT).show();
            enableLoc();
        } else {
            getLocationMethod();
        }
    }

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    private void getLocationMethod() {

        gps = new NewLoc(getApplicationContext());
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
    }

    private void enableLoc() {

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            getLocationMethod();
                        }

                        @Override
                        public void onConnectionSuspended(int i) {
                            googleApiClient.connect();
                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                        }
                    }).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(NavigationActivity.this, REQUEST_LOCATION);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                    }
                }
            });
        }

    }


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_home;
    }


    @Override
    public void onCallBack(String a) {
        SharedPreferencesManager.setLocation(getApplicationContext(), a);
        Intent refresh = new Intent(this, NavigationActivity.class);
        startActivity(refresh);//Start the same Activity
        finish();
    }


    @Override
    public void OnCallBackInternet() {
        Intent refresh = new Intent(this, LoginActivity.class);
        startActivity(refresh);//Start the same Activity
        finish();
    }
}