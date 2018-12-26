package com.example.microsoft.auth.Root;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.microsoft.auth.Auth.TokenListener;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VolleyHelper {

    private final static String API_URL = "http://192.168.1.16/ecommerce/public/api/";
    private final static String TYPE_LOGIN = "login";
    private final static String TYPE_REGISTER = "register";


    private static String requestType;
    private static String recentToken;
    private static TokenListener tokenListener;

    private static RequestQueue requestQueue;
    private static JSONObject userObj;


    public VolleyHelper() {
        //Required Empty Constructor
    }


    public static void volleyInitialize(Context context) {
        requestQueue = Volley.newRequestQueue(context);

    }


    public static void setUserToken(String token) {
        recentToken = token;
    }


    public static void loginUser(String email, String password) {
        userObj = new JSONObject();

        try {
            userObj.put("email", email);
            userObj.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setApiType(TYPE_LOGIN);
    }


    public static void registerUser(String username, String email, String password, String mobile) {
        userObj = new JSONObject();

        try {
            userObj.put("name_en", username);
            userObj.put("email", email);
            userObj.put("password", password);
            userObj.put("phone", mobile);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        setApiType(TYPE_REGISTER);

    }


    private static void setApiType(String type) {
        requestType = type;
    }


    private static String getApiUrl() {
        return API_URL + requestType;
    }


    public static void setTokenListener(TokenListener listener) {
        tokenListener = listener;
    }


    public static void performRequest() {

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                getApiUrl(), userObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Log.i("APIMessage", "Success!");
                            Log.i("APIMessage", response.toString());


                            // Initialize Gson and start new transaction
                            Gson gson = new Gson();

                            JSONObject object = response.getJSONObject("success");
                            UserModel user = gson.fromJson(object.toString(), UserModel.class);

                            String userToken = user.getToken();
                            Log.i("username", user.getUsername());
                            Log.i("userToken", user.getToken());

                            //Notify others that Token has been received
                            if (!userToken.isEmpty()) {
                                tokenListener.onTokenReceived(userToken);
                            }else {
                                tokenListener.onTokenError();
                            }

                            //}
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
                Log.i("APIMessage", getApiUrl());

                //Notify others Token has not been received in time..
                tokenListener.onTokenError();


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


    public static void performUserDataRequest() {

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                getApiUrl(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Log.i("APIMessage", "Success!");
                            Log.i("APIMessage", response.toString());


                            // Initialize Gson and start new transaction
                            Gson gson = new Gson();

                            JSONObject object = response.getJSONObject("success");
                            UserModel user = gson.fromJson(object.toString(), UserModel.class);

                            String userToken = user.getToken();
                            Log.i("username", user.getUsername());
                            Log.i("userToken", user.getToken());

                            //Notify others that Token has been received
                            if (!userToken.isEmpty()) {
                                tokenListener.onTokenReceived(userToken);
                            }else {
                                tokenListener.onTokenError();
                            }

                            //}
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
                Log.i("APIMessage", getApiUrl());

                //Notify others Token has not been received in time..
                tokenListener.onTokenError();


            }

        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("Authentication", "Bearer" + recentToken);

                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        requestQueue.add(request);
    }


    public static void loadUser() {
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
