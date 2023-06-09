package com.example.earlysuraksha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.earlysuraksha.retrofit.retrofitclient;
import com.example.earlysuraksha.retrofit.myservice;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    EditText signupemail, signuppassword, username, phonenumber;
    TextView logintext;
    Button signup;
    String emailsignup, passwordsignup, name, phnnumber;
    myservice client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setContentView(R.layout.activity_register);
        signupemail = findViewById(R.id.emailidsignup);
        signuppassword = findViewById(R.id.passwordsignup);
        logintext = findViewById(R.id.logintext);
        signup = findViewById(R.id.signupbutton);
        username = findViewById(R.id.username);
        phonenumber = findViewById(R.id.phonenumber);

        logintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailsignup = signupemail.getText().toString();
                passwordsignup = signuppassword.getText().toString();
                name = username.getText().toString();
                phnnumber = phonenumber.getText().toString();
                if (emailsignup.equals(" ")) {
                    Toast.makeText(Register.this, "Enter a valid email id !!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (passwordsignup.equals(" ")) {
                    Toast.makeText(Register.this, "Password is mandatory !!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (username.equals(" ")) {
                    Toast.makeText(Register.this, "Enter your name !!", Toast.LENGTH_SHORT).show();
                    return;
                }
                registeruser(emailsignup, passwordsignup, name, phnnumber);
            }
        });
    }
    public void registeruser(String emailsignup, String passwordsignup, String name, String phonenumber) {
        client = retrofitclient.getInstance().create(myservice.class);
        client.registerUser(emailsignup, name, passwordsignup, phonenumber).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String answer = new Gson().toJson(response.body());

                Log.d("resbody", "onResponse: " + answer + "\n" + answer.length());

                if (answer.length() > 100) {

                    Toast.makeText(Register.this, "New user created", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(Register.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}