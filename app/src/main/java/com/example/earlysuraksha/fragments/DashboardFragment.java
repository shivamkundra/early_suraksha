package com.example.earlysuraksha.fragments;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.earlysuraksha.Dashboard;
import com.example.earlysuraksha.MainActivity;
import com.example.earlysuraksha.PeriodicLocation;
import com.example.earlysuraksha.R;
import com.example.earlysuraksha.retrofit.myservice;
import com.example.earlysuraksha.retrofit.retrofitclient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    public String longitude, latitude;
    LocationRequest locationRequest;
    myservice client;
    LatLng latLng2;
    String input;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallBack;
    SupportMapFragment supportMapFragment;
    TextView statusmessage,runtimechanges, severe;
    ImageView statusimage;
    WorkManager mWorkManager;
    View view;
    LinearLayout headings,headingstatus;
    public static boolean dangerflag=false;
    public static boolean dekha_ya_nhi = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_dashboard, container, false);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map2);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        locationRequest=new LocationRequest();
        locationRequest.setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setInterval(30000);
        locationRequest.setFastestInterval(5000);
        input = MainActivity.getauthtoken();
        headings=view.findViewById(R.id.invisible1);
        headingstatus=view.findViewById(R.id.invisible2);
        statusimage = view.findViewById(R.id.statusimage);
        statusmessage = view.findViewById(R.id.statustext);
        severe = view.findViewById(R.id.severe);
//        if(getActivity().getIntent().has)
        if (getActivity().getIntent().hasExtra("yes")) {
            dekha_ya_nhi = true;
        }
        SharedPreferences pref= getActivity().getSharedPreferences("logindetails", Context.MODE_PRIVATE);
        if(input==null){

            input=pref.getString("authtoken","");
        }
        dangerhaikya(input);
        LocationRealted();
        setWorkManager();
        return view;


    }
    public void setWorkManager() {

        // Create Network constraint
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        Data myData = new Data.Builder()
                .putString("Input", input).build();
        PeriodicWorkRequest periodicSyncDataWork =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            periodicSyncDataWork = new PeriodicWorkRequest.Builder(PeriodicLocation.class, 15, TimeUnit.MINUTES)
                    .addTag(TAG)
                    .setInputData(myData)
                    .setConstraints(constraints)
                    .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                    .build();
        }
        mWorkManager = WorkManager.getInstance(getActivity().getApplicationContext());
        mWorkManager.enqueueUniquePeriodicWork(

                TAG,
                ExistingPeriodicWorkPolicy.KEEP, //Existing Periodic Work policy
                periodicSyncDataWork //work request
        );

        try {
            isWorkScheduled(WorkManager.getInstance(getActivity().getApplication()).getWorkInfosByTag(TAG).get());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
    @SuppressLint("MissingPermission")
    public void LocationRealted() {

        locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());
            }
        };

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);

        updateGps();
    }

    private void updateGps() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if(!task.isSuccessful()){
                        Location location=task.getResult();
                        if (location != null) {
                            supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(GoogleMap googleMap) {
                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    Log.d("getlocation", "onMapReady: " + location.getLatitude() + "  " + location.getLongitude());
                                    latLng2 = latLng;
                                    longitude = String.valueOf(location.getLatitude());
                                    latitude = String.valueOf(location.getLatitude());
                                    setLongitudeAndLatitude(latLng2);
                                    if(input==null) {
                                        SharedPreferences prefs = getActivity().getSharedPreferences("logindetails", Context.MODE_PRIVATE);
                                        input = prefs.getString("authtoken", "");
                                    }
                                    setLatLan(latitude, longitude, input);
                                    MarkerOptions options = new MarkerOptions().position(latLng).title("You are here");
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                    googleMap.addMarker(options);
                                }
                            });
                        }


                    }
                }
            });
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener( new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                            @Override
                            public void onMapReady(GoogleMap googleMap) {
                                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                Log.d("getlocation", "onMapReady: " + location.getLatitude() + "  " + location.getLongitude());
                                latLng2 = latLng;
                                longitude = String.valueOf(location.getLatitude());
                                latitude = String.valueOf(location.getLatitude());
                                setLongitudeAndLatitude(latLng2);
                                if(input==null) {
                                    SharedPreferences prefs = getActivity().getSharedPreferences("logindetails",Context.MODE_PRIVATE );
                                    input = prefs.getString("authtoken", "");
                                }
                                setLatLan(latitude, longitude, input);
                                MarkerOptions options = new MarkerOptions().position(latLng).title("You are here");
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                                googleMap.addMarker(options);
                            }
                        });
                    }
                }
            });
        }
    }
    private void setLongitudeAndLatitude(LatLng latLng2) {
        longitude = String.valueOf(latLng2.longitude);
        latitude = String.valueOf(latLng2.latitude);
        Log.d("stringCheck", "onCreate: " + longitude + "    " + latitude);
    }
    public void setLatLan(String latitude, String longitude, String input) {

        client = retrofitclient.getInstance().create(myservice.class);
        client.setlocation(latitude, longitude, "" + input.substring(1, input.length() - 1)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String answer = new Gson().toJson(response.body());
                Log.d("resbody2", "onResponse: " + input.substring(1, input.length() - 1));
                Log.d("resbody", "onResponse: " + answer + "\n" + answer.length());

                if (answer.length() > 100) {
                    Toast.makeText(getContext(), "New user created", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean isWorkScheduled(List<WorkInfo> workInfos) {
        boolean running = false;
        if (workInfos == null || workInfos.size() == 0) return false;
        for (WorkInfo workStatus : workInfos) {
            running = workStatus.getState() == WorkInfo.State.RUNNING | workStatus.getState() == WorkInfo.State.ENQUEUED;
        }
        return running;
    }
    public boolean dangerhaikya(String input){
        client = retrofitclient.getInstance().create(myservice.class);
        client.isdanger(""+input.substring(1,input.length()-1)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                String res=new Gson().toJson(response.body());
                Log.d("DashboardFragment", "onResponse: "+res);
                String temp=res.substring(7,res.length()-1);
                Log.d("dashboardactivity", "danger hai kya "+res+"   "+temp);

                if(temp.equals("true")){
                    Log.d("dashboardactivity", "onResponse: "+"true kia");
                    statusimage.setImageResource(R.drawable.alert);
                    statusimage.getLayoutParams().height = 100;
                    statusimage.getLayoutParams().width = 100;
                    statusmessage.setText("Alert! Lightning");
                    severe.setText("Severe");
                    headings.setVisibility(View.VISIBLE);
                    headingstatus.setVisibility(View.VISIBLE);

                    dangerflag =true;
                }

            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
        Log.d("dashboardactivity", "dangerhaikya123: "+dangerflag);
        return dangerflag;
    }
}