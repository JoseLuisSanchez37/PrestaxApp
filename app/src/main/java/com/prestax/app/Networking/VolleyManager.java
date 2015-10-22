package com.prestax.app.Networking;

import android.app.Activity;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.prestax.app.Listeners.ListenerVolleyResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by jose.sanchez on 06/09/2015.
 */
public class VolleyManager implements
        Response.Listener<JSONObject>,
        Response.ErrorListener{

    private static final String API = "http://otakulife.co/prestamos/prestamos.php";

    private static VolleyManager volleyManager;
    private Activity activity;
    private RequestType request;
    private ListenerVolleyResponse listener;
    private LoadingDialog progress;

    public static synchronized VolleyManager getInstance(){
        if (volleyManager == null){
            volleyManager = new VolleyManager();
        }
        return volleyManager;
    }

    private VolleyManager(){ }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    public void setListener(ListenerVolleyResponse listener){
        this.listener = listener;
    }

    public void sendRequest(RequestType request, Map<String, String> params){
        this.request = request;
        params.put(KEY.REQUEST, request.getMethod());
        progress = new LoadingDialog(activity);
        progress.show();
        JSONRequest jsonRequest = new JSONRequest(Request.Method.POST, API, params, this, this);
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 3, 0.5f));
        try {
            Log.v("params", jsonRequest.getParams().toString());
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
        VolleyController.getInstance().addToRequestQueue(jsonRequest);
    }

    @Override
    public void onResponse(JSONObject response) {
        progress.dismiss();
        listener.onResponse(response);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        try {
            Log.v("onErrorReponse", error.toString());
            progress.dismiss();
            JSONObject jsonError = new JSONObject();
            jsonError.put(KEY.ERROR, VolleyErrorHelper.getMessage(error, activity));
            listener.onResponse(jsonError);
        } catch (JSONException e) {}
    }

}
