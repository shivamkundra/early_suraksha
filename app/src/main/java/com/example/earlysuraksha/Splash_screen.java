package com.example.earlysuraksha;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.earlysuraksha.retrofit.myservice;
import com.example.earlysuraksha.retrofit.retrofitclient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Splash_screen extends AppCompatActivity {
    boolean hasloggedin;
    myservice client ;
    String answer;
    boolean loggedin;

    String input;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences("logindetails", MODE_PRIVATE);
                String email= prefs.getString("email","");
                String password=prefs.getString("password","");
                String authtoken=prefs.getString("authtoken","");
                Boolean hasloggedin = prefs.getBoolean("flagstatus",false);

                if(hasloggedin){
                    Log.d("autologin", "run: "+"idhar hi to aana tha");
                    Log.d("autologin", "loginuser: 221 "+email+"   "+password);

                    client = retrofitclient.getInstance().create(myservice.class);
                    client.loginUser(email, password).enqueue(new Callback<JsonObject>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            Log.d("responsebody", "onResponse: " + response.body());
                            if (response.body() == null) {
                                Toast.makeText(getApplicationContext(), "Check your credentials or internet connection", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            answer = new Gson().toJson(response.body());
                            JsonParser jsonParser = new JsonParser();
                            JsonObject authtoken = (JsonObject) jsonParser.parse(answer);
                            Log.d("accessobject", "onResponse: " + authtoken.get("authToken"));
                            input = authtoken.get("authToken").toString();
                            Log.d("qwerty1", "newuser: " + input);
                            Log.d("responselogin", "onResponse: " + answer);

                            if (answer.length() > 100) {
                                SharedPreferences.Editor editor = getSharedPreferences("logindetals", MODE_PRIVATE).edit();
                                editor.putString("email", email);
                                editor.putString("password", password);
                                editor.putString("authtoken", input);
                                editor.apply();
                                loggedin = true;
                                Toast.makeText(getApplicationContext(), "login success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                startActivity(intent);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {

                        }
                    });
                }
                else{

                    Intent intent= new Intent(Splash_screen.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                    Log.d("autologin", "run: "+"idhar nhi aana tha ");
                }
            }
        }, 3*1000);

    }
}