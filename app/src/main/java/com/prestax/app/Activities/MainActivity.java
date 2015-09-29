package com.prestax.app.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prestax.app.Fragments.DialogFragmentMessage;
import com.prestax.app.Listeners.ListenerVolleyResponse;
import com.prestax.app.Networking.KEY;
import com.prestax.app.Networking.Messages;
import com.prestax.app.Networking.RequestType;
import com.prestax.app.Networking.VolleyManager;
import com.prestax.app.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements ListenerVolleyResponse{

    private DecimalFormat money = new DecimalFormat("###,###,##0.00");
    private static final int REQUEST_CODE_CAMERA = 1;

    private String folio,
                    customer_name,
                    description,
                    amount,
                    type,
                    date_start,
                    date_end,
                    picture_name,
                    img_path;

    private TextView txv_folio,
                    txv_customer_name,
                    txv_description,
                    txv_amount,
                    txv_date_pay_or_renovation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getData(getIntent().getExtras().getString(KEY.PARAMS));
        initView();
    }

    private void getData(String stringResponse){
        try {
            JSONObject response = new JSONObject(stringResponse);

            folio               = response.getString(KEY.FOLIO);
            customer_name       = response.getString(KEY.NAME);
            description         = response.getString(KEY.DESCRIPTION);
            amount              = response.getString(KEY.AMOUNT);
            type                = response.getString(KEY.TYPE_PROVIDER);
            date_start          = response.getString(KEY.DATE_START);
            date_end            = response.getString(KEY.DATE_END);

        } catch (JSONException e) {
            Log.v("onCreate-->", e.getMessage());
        }
    }

    private void initView(){
        txv_folio = (TextView) findViewById(R.id.txv_folio);
        txv_customer_name = (TextView) findViewById(R.id.txv_customer_name);
        txv_description = (TextView) findViewById(R.id.txv_description);
        txv_amount = (TextView) findViewById(R.id.txv_amount);
        txv_date_pay_or_renovation = (TextView) findViewById(R.id.txv_date_to_pay);

        txv_folio.setText(folio);
        txv_customer_name.setText(customer_name);
        txv_amount.setText("$ "+money.format(Double.parseDouble(amount)));
        txv_description.setText(description);
        txv_date_pay_or_renovation.setText(date_end);

    }

    public void exit(View view){
        Intent intent = new Intent(this, SearchFolio.class);
        startActivity(intent);
        finish();
    }

    public void takeAPicture(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    private Uri setImageUri(){
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        String picture_name = d.getDay() + "_" + d.getMonth() + "_" + d.getYear()
                +"__" + d.getHours() + "_" + d.getMinutes() +".jpeg";

        this.picture_name = picture_name.substring(0, picture_name.length() - 5);
        Log.v("takeAPicture()", this.picture_name);
        File file= new File(Environment.getExternalStorageDirectory(), picture_name);
        Uri imgUri = Uri.fromFile(file);
        this.img_path = file.getAbsolutePath();
        return imgUri;
    }

    private void setData(){
        String date_end = this.date_end;
        Date date = new Date();
        int day = Integer.parseInt(date_end.substring(0, 1));
        int month = Integer.parseInt(date_end.substring(3, 4));
        int year = Integer.parseInt(date_end.substring(5, 6));
        date.setDate(day);
        date.setMonth(month);
        date.setYear(year);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_CANCELED){
            if(requestCode == REQUEST_CODE_CAMERA){
                new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.confirm_send_data))
                    .setMessage(getString(R.string.confirm_send_data_message))
                    .setPositiveButton(getString(R.string.send_data), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            sendRequest(RequestType.UPLOAD_PICTURE, dataToMap());
                        }
                    })
                    .setNegativeButton(getString(R.string.continue_edit), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show();
            }
        }
    }

    private HashMap<String, String> dataToMap(){
        HashMap<String,String> data = new HashMap<>();
        data.put(KEY.FOLIO,         folio);
        data.put(KEY.PICTURE,       encodeToBase64(img_path));
        data.put(KEY.PICTURE_NAME,  picture_name);
        return data;
    }

    public static String encodeToBase64(String img_path){
        Bitmap bitmap = BitmapFactory.decodeFile(img_path);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if (response.has(KEY.RESULT_CODE)){
                if(response.getInt(KEY.RESULT_CODE) == 30){
                    String message = Messages.getResponseFromResultCode(this, response.getInt(KEY.RESULT_CODE));
                    DialogFragmentMessage dialog = DialogFragmentMessage.newInstance(message, Messages.OK);
                    dialog.show(getFragmentManager(), DialogFragmentMessage.TAG);
                }else{
                    String message = Messages.getResponseFromResultCode(this, response.getInt(KEY.RESULT_CODE));
                    DialogFragmentMessage dialog = DialogFragmentMessage.newInstance(message, Messages.ERROR);
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

    @Override
    public void sendRequest(RequestType requestType, Map<String, String> params) {
        VolleyManager.getInstance().setActivity(this);
        VolleyManager.getInstance().setListener(this);
        VolleyManager.getInstance().sendRequest(requestType, params);
    }
}

