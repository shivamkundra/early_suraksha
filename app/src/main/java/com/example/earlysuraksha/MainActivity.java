package com.example.earlysuraksha;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.earlysuraksha.retrofit.myservice;
import com.example.earlysuraksha.retrofit.retrofitclient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    public static String input;
    EditText loginemail, loginpassword;
    TextView signuptext;
    Button login;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    myservice client;
    String answer;
    public static boolean loggedin = false;
    public static boolean dekhayanhi = false;


    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginemail = findViewById(R.id.emailidlogin);
        loginpassword = findViewById(R.id.passwordlogin);
        signuptext = findViewById(R.id.signuptext);
        login = findViewById(R.id.loginbutton);

        signuptext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Register.class);
                startActivity(intent);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginuser(loginemail.getText().toString(), loginpassword.getText().toString());

            }
        });
        Intent intent=getIntent();
        String response=intent.getStringExtra("yes");
        String temp="true";
        String temp2=""+response;
        Log.d("highpitchresponse", "mainactivity "+response);
        if(temp.equals(temp2)){
            dekhayanhi=true;
            SharedPreferences.Editor editor = getSharedPreferences("seenstatus", MODE_PRIVATE).edit();
            editor.putBoolean("status", dekhayanhi);
            editor.apply();
            Log.d("highpitch", "onCreate: "+dekhayanhi);
        }
    }
    public void loginuser(String email, String password) {
        Log.d("autologin", "loginuser: "+email+"   "+password);
        client = retrofitclient.getInstance().create(myservice.class);
        client.loginUser(email, password).enqueue(new Callback<JsonObject>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("responsebody", "onResponse: " + response.errorBody());
                if (response.body() == null) {
                    Toast.makeText(MainActivity.this, "Check your credentials or internet connection", Toast.LENGTH_SHORT).show();
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
                    loggedin = true;
                    SharedPreferences.Editor editor = getSharedPreferences("logindetails", MODE_PRIVATE).edit();
                    editor.putString("email", email);
                    editor.putString("password",password);
                    editor.putString("authtoken",input);
                    editor.putBoolean("flagstatus",loggedin);
                    editor.apply();

                    Toast.makeText(MainActivity.this, "login success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(MainActivity.this, "Some error occured", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Wrong credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static boolean getstatus(Context context){
        SharedPreferences prefs = context.getSharedPreferences("seenstatus", MODE_PRIVATE);
        boolean status = prefs.getBoolean("status", false);
        dekhayanhi = status;
        Log.d("highpitch", "getstatus: "+dekhayanhi);
        return dekhayanhi;

    }
    public static boolean getloginflag(){
        return loggedin;
    }


    public static String getauthtoken() {
        Log.d("messageresponse", "getauthtoken: "+input);
        return input;
    }

}