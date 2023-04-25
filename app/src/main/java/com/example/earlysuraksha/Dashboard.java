package com.example.earlysuraksha;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.earlysuraksha.fragments.DashboardFragment;
import com.example.earlysuraksha.fragments.DearOnes;
import com.example.earlysuraksha.fragments.DemandsFragment;
import com.example.earlysuraksha.fragments.DisasterManagementKitFragment;
import com.example.earlysuraksha.fragments.EmergencyFragment;
import com.example.earlysuraksha.fragments.GameScreenFragment;
import com.example.earlysuraksha.retrofit.myservice;
import com.example.earlysuraksha.retrofit.retrofitclient;
import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 20;
    private static final int MY_BACKGROUND_LOCATION_REQUEST = 25;

    private static final String TAG = "MainActivity";
    NavigationView nav;
    String input,username,email;

    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ImageView shownavigationdrawer;
    FragmentManager fragmentManager;
    FragmentContainerView fragmentContainerView;
    BubbleNavigationLinearView bubbleNavigation;
    myservice client;
    TextView statusmessage, tvusername, tvemail, runtimechanges, severe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        bubbleNavigation = findViewById(R.id.bottom_navigation_view_linear);
        nav = findViewById(R.id.navmenu);
        fragmentContainerView = findViewById(R.id.fragmentContainer);
        nav.bringToFront();
        drawerLayout = findViewById(R.id.drawer);
        toolbar = findViewById(R.id.toolbar);
        shownavigationdrawer = findViewById(R.id.shownavdrawer);
        View hView = nav.getHeaderView(0);
        tvusername = (TextView) hView.findViewById(R.id.username);
        tvemail = (TextView) hView.findViewById(R.id.emailid);
        setSupportActionBar(toolbar);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().hide();
        replaceFragment(new DashboardFragment());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!checkLocationPermission()) {
                requestPermission();
            }
        }
        input = MainActivity.getauthtoken();
        newUser(input);
        bubbleNavigation.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                switch (position) {
                    case (0): {
                        replaceFragment(new DashboardFragment());
                        break;
                    }
                    case (1): {
                        replaceFragment(new GameScreenFragment());
                        break;
                    }

                    case (2): {
                        replaceFragment(new DearOnes());
                        break;
                    }
                    case (3): {
                        replaceFragment(new EmergencyFragment());
                        break;
                    }
                    case (4):
                        replaceFragment(new DemandsFragment());
                        break;
                }

            }
        });
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case (R.id.home):
                        replaceFragment(new DashboardFragment());
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case (R.id.disastermanagementkit):
                        replaceFragment(new DisasterManagementKitFragment());
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case (R.id.precautionarygames):
                        replaceFragment(new GameScreenFragment());
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case (R.id.helplinenumber):
                        replaceFragment(new EmergencyFragment());
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case (R.id.logout):
                        SharedPreferences.Editor editor = getSharedPreferences("logindetails", MODE_PRIVATE).edit();
                        editor.putBoolean("flagstatus", false);
                        editor.putString("authtoken", "");
                        editor.putString("email", "");
                        editor.putString("password", "");
                        editor.apply();
                        Intent intent2 = new Intent(Dashboard.this, MainActivity.class);
                        startActivity(intent2);
                        finish();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                }
                return false;
            }
        });
        shownavigationdrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: " + "Inside Navigation Drawer");
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    // Check for Permission//
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private boolean checkLocationPermission() {
        int result0 = ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION");
        int result1 = ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION");
        int result2 = ContextCompat.checkSelfPermission(this, "android.permission.CALL_PHONE");
        int result3 = ContextCompat.checkSelfPermission(this, "android.permission.READ_SMS");
        int result4 = ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_BACKGROUND_LOCATION");
        int result5 = ContextCompat.checkSelfPermission(this, "android.permission.RECEIVE_SMS");
        int result6 = ContextCompat.checkSelfPermission(this, "android.permission.SEND_SMS");
        return result0 == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED && result4 == PackageManager.PERMISSION_GRANTED && result5 == PackageManager.PERMISSION_GRANTED && result6 == PackageManager.PERMISSION_GRANTED;

    }



    private void newUser(String input) {
        if (input == null) {
            SharedPreferences prefs = getSharedPreferences("logindetails", MODE_PRIVATE);
            input = prefs.getString("authtoken", "");
        }
        Log.d("qwerty", "newuser: " + input.substring(1, input.length() - 1));
        client = retrofitclient.getInstance().create(myservice.class);
        client.getuser("" + input.substring(1, input.length() - 1)).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                String output = new Gson().toJson(response.body());
                JsonParser jsonParser1 = new JsonParser();
                JsonObject user = (JsonObject) jsonParser1.parse(output);

                username = user.get("name").toString().substring(1, user.get("name").toString().length() - 1);
                email = user.get("email").toString().substring(1, user.get("email").toString().length() - 1);

                tvemail.setText(email);
                tvusername.setText(username);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(Dashboard.this, "error occured", Toast.LENGTH_SHORT).show();
                System.out.println("Mai idhar aya hu");
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void requestBackgroundLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                MY_BACKGROUND_LOCATION_REQUEST);
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                if (ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {


                    AlertDialog alertDialog = new AlertDialog.Builder(Dashboard.this).create();
                    alertDialog.setTitle("Background permission");
                    alertDialog.setMessage("This Permission is  required for background location");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Grant background Permission",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    requestBackgroundLocationPermission();
                                    dialog.dismiss();
                                }
                            });

                    alertDialog.show();


                } else if (ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
//                    setWorkManager();
                }
            } else {
//                setWorkManager();/
            }

        } else if (ActivityCompat.checkSelfPermission(Dashboard.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Dashboard.this, Manifest.permission.ACCESS_FINE_LOCATION)) {


                AlertDialog alertDialog = new AlertDialog.Builder(Dashboard.this).create();
                alertDialog.setTitle("ACCESS_FINE_LOCATION");
                alertDialog.setMessage("Location permission required");

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.CALL_PHONE,
                                        "android.permission.READ_SMS",
                                        Manifest.permission.RECEIVE_SMS,
                                        Manifest.permission.SEND_SMS
                                }, PERMISSION_REQUEST_CODE);
                                dialog.dismiss();
                            }
                        });


                alertDialog.show();

            } else {
                ActivityCompat.requestPermissions(Dashboard.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.CALL_PHONE,
                        "android.permission.READ_SMS",
                        Manifest.permission.RECEIVE_SMS,
                        Manifest.permission.SEND_SMS
                }, PERMISSION_REQUEST_CODE);
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean coarseLocation = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean fineLocation = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                boolean callPhone = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                boolean sms = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                boolean smsRecieve = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                boolean smsSend = grantResults[5] == PackageManager.PERMISSION_GRANTED;

                if (coarseLocation && fineLocation && callPhone && sms && smsRecieve && smsSend) {
                    MyReceiver r = new MyReceiver();
                    IntentFilter i = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
                    registerReceiver(r, i);
                    requestBackgroundLocationPermission();
                    Log.d("log inside", "onRequestPermissionsResult: "+"abc");

                } else {
                    Toast.makeText(this, "ACCESS_FINE_LOCATION permission denied", Toast.LENGTH_LONG).show();
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.parse("package:com.example.earlysuraksha")
                        ));

                    }
                }
            }
        } else if (requestCode == MY_BACKGROUND_LOCATION_REQUEST) {

            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                }
            } else {
            }
        }
    }
    private void replaceFragment(Fragment fragment){
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }


}
