package com.example.microsoft.auth.Root;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.microsoft.auth.Auth.AuthResponseListener;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VolleyHelper {

    private final static String API_URL = "http://192.168.1.17/ecommerce/public/api/";
    private final static String TYPE_LOGIN = "login";
    private final static String TYPE_REGISTER = "register";
    private final static String TYPE_UPDATE = "update";


    private static String requestType;
    private static String recentToken;
    private static AuthResponseListener authResponseListener;

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


    public static void loginUser(UserModel data) {
        userObj = new JSONObject();

        try {
            userObj.put("email", data.getEmail());
            userObj.put("password", data.getPassword());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setApiType(TYPE_LOGIN);
    }


    public static void registerUser(UserModel data) {
        userObj = new JSONObject();

        try {
            userObj.put("name_en", data.getUsername());
            userObj.put("email", data.getEmail());
            userObj.put("password", data.getPassword());
            userObj.put("phone", data.getMobile());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        setApiType(TYPE_REGISTER);

    }


    public static void changeUserData(UserModel data) {

        if (data != null) {
            userObj = new JSONObject();
            try {
                userObj.put("name_en", data.getUsername());
                userObj.put("phone", data.getMobile());
                userObj.put("gender", data.getGender());
                userObj.put("user_img", data.getImgURL());

                //if user didn't ask password change, exclude from request!
                if (data.getPassword() != null) {
                    userObj.put("password", data.getPassword());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            userObj = null;
        }

        setApiType(TYPE_UPDATE);
    }


    private static void setApiType(String type) {
        requestType = type;
    }


    private static String getApiUrl() {
        return API_URL + requestType;
    }


    public static void setAuthResponseListener(AuthResponseListener listener) {
        authResponseListener = listener;
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
                            if (object != null) {
                                UserModel user = gson.fromJson(object.toString(), UserModel.class);

                                Log.i("username", user.getUsername());
                                Log.i("userToken", user.getToken());

                                //Notify others that Token has been received
                                authResponseListener.onResponseReceived(user);
                            } else {
                                authResponseListener.onResponseError();
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
                authResponseListener.onResponseError();


            }

        }) {
            @Override
            public Map<String, String> getHeaders() {
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

                            if (object != null) {
                                UserModel user = gson.fromJson(object.toString(), UserModel.class);

                                Log.i("username", user.getUsername());
                                Log.i("userToken", user.getToken());

                                //Notify others that Token has been received
                                authResponseListener.onResponseReceived(user);
                            } else {
                                authResponseListener.onResponseError();
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
                authResponseListener.onResponseError();


            }

        }) {
            @Override
            public Map<String, String> getHeaders() {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Authorization", "Bearer " + recentToken);

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
