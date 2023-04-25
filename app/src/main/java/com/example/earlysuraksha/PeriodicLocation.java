package com.example.earlysuraksha;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.earlysuraksha.retrofit.myservice;
import com.example.earlysuraksha.retrofit.retrofitclient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.time.Duration;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


@RequiresApi(api = Build.VERSION_CODES.O)
public class PeriodicLocation extends Worker {
    public String input;
    private static final String TAG = "MyWorker";
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private Location mLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private Context mContext;
    private LocationCallback mLocationCallback;
    private WorkRequest uploadWorkRequest;
    myservice client;


    public PeriodicLocation(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        input = getInputData().getString("Input");
    }

    @NonNull
    @Override
    public Result doWork() {
        String input = getInputData().getString("Input");
        Log.d(TAG, "doWork: Done");
        Log.d(TAG, "work" + input);
        Log.d(TAG, "onStartJob: STARTING JOB..");

        //setting onetime work request
        Duration duration= Duration.ofSeconds(15);
        uploadWorkRequest = new OneTimeWorkRequest.Builder(UploadWorker.class)
                .setInitialDelay(duration)
                .build();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        };

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            mFusedLocationClient
                    .getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                                LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                                Log.d(TAG, "Location:" + mLocation.getLatitude() + "" + mLocation.getLongitude());
                                assert input != null;
                                setLatLan(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude), input);
                                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                                client.isdanger(""+input.substring(1, input.length() - 1)).enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                        String res=new Gson().toJson(response.body());
                                        JsonParser jsonParser1 = new JsonParser();
                                        JsonObject status = (JsonObject) jsonParser1.parse(res);
                                        String temp=res.substring(7,res.length()-1);
                                        Log.d(TAG, "PeriodicLocation "+res+"idhar  "+temp);
                                        String temp2="true";
                                        if(temp.equals(temp2)){
                                            Log.d(TAG, "onResponse: "+"idhar aaya");
                                            WorkerUtils.makeStatusNotification(getCompleteAddressString(mLocation.getLatitude(),mLocation.getLongitude()),mContext);
                                            //work manager enqueued
                                            WorkManager
                                                    .getInstance(mContext)
                                                    .enqueue(uploadWorkRequest);
                                            client=retrofitclient.getInstance().create(myservice.class);
                                            client.sendmessages().enqueue(new Callback<JsonObject>() {
                                                @Override
                                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                                    Toast.makeText(getApplicationContext(), "messages sent to specified contacts", Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                                    Toast.makeText(getApplicationContext(), "Some error occured", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        else{
                                            Log.d(TAG, "onResponse: "+"idhar nhi aaya");

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<JsonObject> call, Throwable t) {

                                    }
                                });

                            } else {
                                Log.d(TAG, "Failed to get location.");

                            }

                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }

        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, null);
        } catch (SecurityException unlikely) {
            //Utils.setRequestingLocationUpdates(this, false);
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }

        return Result.success();
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strAdd;
    }

    // Setting Longitude and Latitude to server//
    public void setLatLan(String latitude, String longitude, String input) {
        client = retrofitclient.getInstance().create(myservice.class);
        client.setlocation(latitude, longitude, "" + input.substring(1, input.length() - 1)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String answer = new Gson().toJson(response.body());
                Log.d(TAG, "onResponse: " + input.substring(1, input.length() - 1));
                Log.d(TAG, "onResponse: " + answer + "\n" + answer.length());
                if (answer.length() > 100) {
                    Log.d(TAG, "New user created ");

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
//        void sendmessages1(){
//            client=retrofitclient.getInstance().create(myservice.class);
//            client.sendmessages().enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                    Toast.makeText(getApplicationContext(), "messages sent to specified contacts", Toast.LENGTH_SHORT).show();
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//                    Toast.makeText(getApplicationContext(), "Some error occured", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
    }
}


