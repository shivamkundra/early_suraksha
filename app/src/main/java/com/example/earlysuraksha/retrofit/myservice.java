package com.example.earlysuraksha.retrofit;

import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface myservice {

    @FormUrlEncoded
    @POST("/api/user/createuser")
    Call<JsonObject> registerUser(@Field("email") String email,
                                  @Field("name") String name,
                                  @Field("password") String password,
                                  @Field("phoneNumber") String phoneNumber);

    @POST("/api/user/login")
    @FormUrlEncoded
    Call<JsonObject> loginUser(@Field("email") String email,
                               @Field("password") String password);

    @FormUrlEncoded
    @POST("/api/user/setLocation")
    Call<JsonObject> setlocation(@Field("lat") String lat,
                                 @Field("long") String longitude,
                                 @Header("auth-token") String authtoken);

    @POST("/api/custom/sendAlert")
    Call<JsonObject> sendalert(@Field("number") String number);

    @POST("/api/user/getuser")
    Call<JsonObject> getuser(@Header("auth-token") String auth);

    @GET("/api/es/isDanger")
    Call<JsonObject> isdanger(@Header("auth-token") String auth);

    @FormUrlEncoded
    @POST("/api/es/sendMessageUser")
    Call<JsonObject> sendremarks(@Field("msg") String message,
                                    @Header("auth-token") String authtoken);

    @FormUrlEncoded
    @POST("/api/user/dearOnes")
    Call<JsonObject> addcontacts(@Field("dearones") List<String> contacts,
                                       @Header("auth-token") String authtoken);


    @GET("/api/user/sendDearOnes")
    Call<JsonObject> sendmessages();
}

