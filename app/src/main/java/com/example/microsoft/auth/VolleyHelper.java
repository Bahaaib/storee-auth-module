package com.example.microsoft.auth;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VolleyHelper {

    private final static String API_URL = "https://api.myjson.com/bins/9am92";
    private static String email;
    private static String password;

    private static Context vContext;
    private static RequestQueue requestQueue;
    private static JSONObject userObj;


    public VolleyHelper() {
        //Required Empty Constructor
    }

    public static void volleyInitialize(Context context) {
        vContext = context;
        requestQueue = Volley.newRequestQueue(context);

    }

    public static void loginUser(String email, String password) {
        userObj = new JSONObject();

        try {
            userObj.put("email", email);
            userObj.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void registerUser(String username, String email, String password, String mobile) {
        userObj = new JSONObject();

        try {
            userObj.put("username", username);
            userObj.put("email", email);
            userObj.put("password", password);
            userObj.put("mobile", mobile);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void performRequest() {

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                API_URL, userObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Log.i("APIMessage", "Success!");
                            Log.i("APIMessage", response.toString());


                            // Initialize Gson and start new transaction
                            Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
                                @Override
                                public boolean shouldSkipField(FieldAttributes f) {
                                    return false;
                                }

                                @Override
                                public boolean shouldSkipClass(Class<?> clazz) {
                                    return false;
                                }
                            }).create();

                            JSONArray jsonArray = response.getJSONArray("success");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                UserModel user = gson.fromJson(jsonArray.get(i).toString(), UserModel.class);
                                Log.i("UserLoop", "Inside for loop");

                                Log.i("username", user.getUsername());
                                Log.i("userToken", user.getToken());
                            }
                            //Notify the RecyclerView with the new data..
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }, new Response.ErrorListener()


        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("APIMessage", "Couldn't Reach API");
                Log.i("APIMessage", error.toString());


            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");

                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        requestQueue.add(request);
    }


    public static void loadUser(){
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                API_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            Log.i("apiresponse", response.toString());


                            // Initialize Gson and start new transaction
                            Gson gson = new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
                                @Override
                                public boolean shouldSkipField(FieldAttributes f) {
                                    return false;
                                }

                                @Override
                                public boolean shouldSkipClass(Class<?> clazz) {
                                    return false;
                                }
                            }).create();

                            JSONObject userObj = response.getJSONObject("success");

                            UserModel user = gson.fromJson(userObj.toString(), UserModel.class);
                            Log.i("username", user.getUsername());
                            Log.i("userToken", user.getToken());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()


        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("apiresponse", "Couldn't reach API");

            }

        });
        requestQueue.add(request);
    }
}
