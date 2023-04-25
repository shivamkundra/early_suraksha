package com.example.earlysuraksha.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.earlysuraksha.R;
import com.example.earlysuraksha.retrofit.myservice;
import com.example.earlysuraksha.retrofit.retrofitclient;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DearOnes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DearOnes extends Fragment {
    EditText contact1,contact2,contact3;
    Button save;
    myservice client;
    String input;
    CircleImageView edit1,edit2,edit3;
    TextView contacts1tv,contacts2tv,contacts3tv;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DearOnes() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DearOnes.
     */
    // TODO: Rename and change types and number of parameters
    public static DearOnes newInstance(String param1, String param2) {
        DearOnes fragment = new DearOnes();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_dear_ones, container, false);
        contact1=view.findViewById(R.id.contact1);
        contact2=view.findViewById(R.id.contact2);
        contact3=view.findViewById(R.id.contact3);
        edit1=view.findViewById(R.id.editremarks1);
        edit2=view.findViewById(R.id.editremarks2);
        edit3=view.findViewById(R.id.editremarks3);
        contacts1tv=view.findViewById(R.id.contact1textview);
        contacts2tv=view.findViewById(R.id.contact2textview);
        contacts3tv=view.findViewById(R.id.contact3textview);
        ArrayList<String> contacts=new ArrayList<>();

        if(input==null){
            SharedPreferences prefs= getActivity().getSharedPreferences("logindetails", Context.MODE_PRIVATE);
            input=prefs.getString("authtoken","");
        }
        contacts1tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contacts1tv.setVisibility(View.GONE);
                contact1.setVisibility(View.VISIBLE);
            }
        });
        contacts2tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contacts2tv.setVisibility((View.GONE));
                contact2.setVisibility(View.VISIBLE);
            }
        });
        contacts3tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contacts3tv.setVisibility((View.GONE));
                contact3.setVisibility(View.VISIBLE);
            }
        });
        save=view.findViewById(R.id.savecontacts);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client= retrofitclient.getInstance().create(myservice.class);
                contacts.add(contact1.getText().toString());
                contacts.add(contact2.getText().toString());
                contacts.add(contact3.getText().toString());
                contacts3tv.setVisibility(View.VISIBLE);
                contacts2tv.setVisibility(View.VISIBLE);
                contacts1tv.setVisibility(View.VISIBLE);
                contact1.setVisibility(View.GONE);
                contact2.setVisibility(View.GONE);
                contact3.setVisibility(View.GONE);
                contacts3tv.setText(""+contacts.get(2));
                contacts2tv.setText(""+ contacts.get(1));
                contacts1tv.setText(""+contacts.get(0));

                client.addcontacts(contacts,""+input.substring(1,input.length()-1)).enqueue(new Callback<JsonObject>() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Toast.makeText(getContext(), "contacts saved", Toast.LENGTH_SHORT).show();
                       FragmentManager fragmentManager = getChildFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentContainer, new DashboardFragment());
                        fragmentTransaction.commit();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return view;
    }
}