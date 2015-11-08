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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.prestax.app.Fragments.DialogFragmentMessage;
import com.prestax.app.Listeners.ListenerVolleyResponse;
import com.prestax.app.Networking.KEY;
import com.prestax.app.Networking.RESULTCODE;
import com.prestax.app.Networking.RequestType;
import com.prestax.app.Networking.VolleyManager;
import com.prestax.app.R;
import com.prestax.app.Utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
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
                    date_end,
                    picture_name,
                    payment_reference,
                    img_path;

    private TextView txv_folio,
                    txv_customer_name,
                    txv_description,
                    txv_amount,
                    txv_date_pay_or_renovation,
                    txv_percent_to_pay;

    private LinearLayout linear_container_payment_reference;

    private EditText edt_payment_reference;

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
            date_end            = response.getString(KEY.DATE_END);
            payment_reference   = response.getString(KEY.PAYMENT_REFERENCE);

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
        txv_percent_to_pay = (TextView) findViewById(R.id.txv_percent_to_pay);
        linear_container_payment_reference = (LinearLayout) findViewById(R.id.linear_container_payment_reference);
        edt_payment_reference = (EditText) findViewById(R.id.edt_payment_reference);

        txv_folio.setText(folio);
        txv_customer_name.setText(customer_name);
        txv_amount.setText("$ " + money.format(Double.parseDouble(amount)));
        txv_description.setText(description);
        txv_date_pay_or_renovation.setText(setDateToPay());
        txv_percent_to_pay.setText("$ " + money.format(getPercentToPay(type, amount)));

        if (payment_reference != "0"){
            linear_container_payment_reference.setVisibility(View.VISIBLE);
            edt_payment_reference.setText(payment_reference);
        }
    }

    public void exit(View view){
        Intent intent = new Intent(this, SearchFolio.class);
        startActivity(intent);
        finish();
    }

    public void startPaypal(View view){
        Intent paypal = new Intent(this, PayPal.class);
        switch (view.getId()){
            case R.id.btn_paypal_total:
                paypal.putExtra(PayPal.TYPE, PayPal.PAGO_TOTAL);
                startActivity(paypal);
                break;
            case R.id.btn_paypal_month:
                paypal.putExtra(PayPal.TYPE, PayPal.PAGO_MENSUAL);
                startActivity(paypal);
                break;
        }
    }

    public void takeAPicture(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    private Uri setImageUri(){
        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        month++;

        String picture_name = c.get(Calendar.DAY_OF_MONTH) +
                "-" + month +
                "-" + c.get(Calendar.YEAR) +
                "--" + c.get(Calendar.HOUR) +
                ":" + c.get(Calendar.MINUTE) +
                ".jpeg";

        this.picture_name = picture_name.substring(0, picture_name.length() - 5);
        Log.v("picture name", this.picture_name);
        File file= new File(Environment.getExternalStorageDirectory(), picture_name);
        Uri imgUri = Uri.fromFile(file);
        this.img_path = file.getAbsolutePath();
        return imgUri;
    }

    private String setDateToPay(){
        String date_end = this.date_end;
        int day = Integer.parseInt(date_end.substring(0, 2));
        Log.v("day", day + "");

        Calendar date = Calendar.getInstance();
        Calendar current_date = Calendar.getInstance();
        date.set(Calendar.DAY_OF_MONTH, day);

        if(current_date.after(date) | current_date.getTimeInMillis() == date.getTimeInMillis()){
            date.set(Calendar.MONTH, date.get(Calendar.MONTH) + 1);
            date_end = date.get(Calendar.DAY_OF_MONTH) + " de " + getMonth(date.get(Calendar.MONTH)) + " del " + date.get(Calendar.YEAR);

        }else if(current_date.before(date)){
            date_end = date.get(Calendar.DAY_OF_MONTH) + " de " + getMonth(date.get(Calendar.MONTH)) + " del " + date.get(Calendar.YEAR);
        }
        return date_end;
    }

    public double getPercentToPay(String type, String amount){
        int amount_temp = Integer.parseInt(amount);
        double percent = 0;

        if(type.equals("2")){//bimestral
            percent = amount_temp * 0.07;
        }else if(type.equals("1")){//mes
            percent = amount_temp * 0.14;
        }

        return percent;
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 28, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private String getMonth(int month){
        switch (month){
            case Calendar.JANUARY:
                return getString(R.string.january);

            case Calendar.FEBRUARY:
                return getString(R.string.february);

            case Calendar.MARCH:
                return getString(R.string.march);

            case Calendar.APRIL:
                return getString(R.string.april);

            case Calendar.MAY:
                return getString(R.string.may);

            case Calendar.JUNE:
                return getString(R.string.june);

            case Calendar.JULY:
                return getString(R.string.july);

            case Calendar.AUGUST:
                return getString(R.string.august);

            case Calendar.SEPTEMBER:
                return getString(R.string.september);

            case Calendar.OCTOBER:
                return getString(R.string.october);

            case Calendar.NOVEMBER:
                return getString(R.string.november);

            case Calendar.DECEMBER:
                return getString(R.string.december);

            default:
                return "";
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        try {
            if (response.has(KEY.RESULT_CODE)){

                int code = response.getInt(KEY.RESULT_CODE);

                if(code == RESULTCODE.UPLOAD_BAUCHER_SUCCESS){
                    String message = RESULTCODE.getMessage(this, code);
                    DialogFragmentMessage dialog = DialogFragmentMessage.newInstance(message, RESULTCODE.SUCCESS);
                    dialog.show(getFragmentManager(), DialogFragmentMessage.TAG);

                }else if (code == RESULTCODE.UPLOAD_BAUCHER_FAILED){
                    String message = RESULTCODE.getMessage(this, code);
                    DialogFragmentMessage dialog = DialogFragmentMessage.newInstance(message, RESULTCODE.FAILED);
                    dialog.show(getFragmentManager(), DialogFragmentMessage.TAG);
                }
            }else if(response.has(KEY.ERROR)){
                DialogFragmentMessage dialog = DialogFragmentMessage.newInstance(response.getString(KEY.ERROR), RESULTCODE.FAILED);
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

    @Override
    public void onBackPressed(){  }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

}

