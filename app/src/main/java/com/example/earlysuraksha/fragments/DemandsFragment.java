package com.example.earlysuraksha.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.earlysuraksha.R;
//import com.example.earlysuraksha.demands;
import com.example.earlysuraksha.diseaseadapter;
import com.example.earlysuraksha.retrofit.myservice;
import com.example.earlysuraksha.retrofit.retrofitclient;
import com.example.earlysuraksha.selectedDiseaseAdapter;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DemandsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DemandsFragment extends Fragment {
    public static ArrayList<String> disease = new ArrayList<>();
    public static ArrayList<Integer> diseaseimage = new ArrayList<>();
    public static ArrayList<String> selecteddisease = new ArrayList<>();
    public static ArrayList<Integer> selecteddiseaseimage = new ArrayList<>();
    public static selectedDiseaseAdapter adapter;
    public static diseaseadapter ad;
    RecyclerView medicaldetails;
    RecyclerView selectedmedicaldetails;
    TextView remarktextview;
    EditText remarksedittext;
    Button post, send;
    CircleImageView editremarks;
    public static TextView selected;

    myservice client;
    String input;

    public static ArrayList getselecteddiseasename() {
        return selecteddisease;
    }

    public static ArrayList getselecteddiseaseimage() {
        return selecteddiseaseimage;
    }

    public static ArrayList getdiseasename() {
        return disease;
    }

    public static ArrayList getdiseaseimage() {
        return diseaseimage;
    }

    public static void updateadapter() {
        Log.i("diseaseitems", selecteddiseaseimage.toString());
        adapter.notifyDataSetChanged();
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DemandsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DemandsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DemandsFragment newInstance(String param1, String param2) {
        DemandsFragment fragment = new DemandsFragment();
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
        return inflater.inflate(R.layout.fragment_demands, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        remarksedittext=view.findViewById(R.id.remarksedittext);
        remarktextview=view.findViewById(R.id.remarkstextview);
        editremarks=view.findViewById(R.id.editremarks);
        post=view.findViewById(R.id.postremark);
        send=view.findViewById(R.id.senddemands);

        medicaldetails = view.findViewById(R.id.medicaldetails);
        selectedmedicaldetails = view.findViewById(R.id.selectedmedicaldetails);
        disease.add("Food");
        disease.add("Clothing");
        disease.add("Medical Requirements");
        disease.add("Batteries");

        diseaseimage.add(R.drawable.food);
        diseaseimage.add(R.drawable.clothing);
        diseaseimage.add(R.drawable.medicalrequirements);
        diseaseimage.add(R.drawable.batteries);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2, RecyclerView.HORIZONTAL, false);
        medicaldetails.setLayoutManager(gridLayoutManager);
        ad = new diseaseadapter(disease, diseaseimage);
        medicaldetails.setAdapter(ad);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        selectedmedicaldetails.setLayoutManager(linearLayoutManager);
        adapter = new selectedDiseaseAdapter(selecteddisease, selecteddiseaseimage);
        selectedmedicaldetails.setAdapter(adapter);
        medicaldetails.setHasFixedSize(true);
        selected = view.findViewById(R.id.selected);
        selected.setText("Selected:(" + selecteddiseaseimage.size() + ")");


        if(input==null){
            SharedPreferences prefs = getActivity().getSharedPreferences("logindetails", Context.MODE_PRIVATE);
            input = prefs.getString("authtoken", "");
        }

        editremarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remarktextview.setVisibility(View.GONE);
                remarksedittext.setVisibility(View.VISIBLE);
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text= remarksedittext.getText().toString();
                client = retrofitclient.getInstance().create(myservice.class);
                Log.d("messageresponse", "onCreate:main"+input);
                client.sendremarks(""+text,""+input.substring(1,input.length()-1)).enqueue(new Callback<JsonObject>() {

                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        Log.d("messageresponse", "messageresponse " + response.body());
                        if (response.body() == null) {
                            Toast.makeText(getContext(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(getContext(), "Message sent !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getContext(), "request failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}