package com.prestax.app.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.prestax.app.Fragments.DialogFragmentMessage;
import com.prestax.app.Listeners.ListenerVolleyResponse;
import com.prestax.app.Networking.KEY;
import com.prestax.app.Networking.Messages;
import com.prestax.app.Networking.RequestType;
import com.prestax.app.Networking.VolleyManager;
import com.prestax.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SearchFolio extends Activity implements ListenerVolleyResponse{

    private EditText edt_folio, edt_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        edt_folio = (EditText) findViewById(R.id.edt_folio);
        edt_password = (EditText) findViewById(R.id.edt_password);

    }

    public void searchFolio(View v){
        if(edt_folio.getText().toString().isEmpty()){
            edt_folio.setError(getString(R.string.empty_field));
            edt_folio.requestFocus();
        }else if(edt_password.getText().toString().isEmpty()){
            edt_password.setError(getString(R.string.empty_field));
            edt_password.requestFocus();
        } else {
            sendRequest(RequestType.SEARCH_FOLIO, new HashMap<String, String>());
        }
    }

    @Override
    public void sendRequest(RequestType requestType, Map<String, String> params) {
        params.put(KEY.FOLIO, edt_folio.getText().toString());
        params.put(KEY.PASSWORD, edt_password.getText().toString());

        VolleyManager.getInstance().setActivity(this);
        VolleyManager.getInstance().setListener(this);
        VolleyManager.getInstance().sendRequest(RequestType.SEARCH_FOLIO, params);
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if (response.has(KEY.RESULT_CODE)){
                if(response.getInt(KEY.RESULT_CODE) == Messages.OK){
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra(KEY.PARAMS, response.toString());
                    startActivity(intent);
                }else{
                    DialogFragmentMessage dialog = DialogFragmentMessage.newInstance(Messages.getResponseFromResultCode(this,
                            response.getInt(KEY.RESULT_CODE)), Messages.ERROR);
                    dialog.show(getFragmentManager(), DialogFragmentMessage.TAG);
                }
            }else if(response.has(KEY.ERROR)){
                DialogFragmentMessage dialog = DialogFragmentMessage.newInstance(response.getString(KEY.ERROR), Messages.ERROR);
                dialog.show(getFragmentManager(), DialogFragmentMessage.TAG);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
