package com.prestax.app.Networking;

import android.content.Context;

import com.prestax.app.R;

/**
 * Created by jose.sanchez on 17/09/2015.
 */
public class Messages {

    public static final int OK = 20;
    public static final int ERROR = 2;

    public static String getResponseFromResultCode(Context context, int resultCode){
        switch (resultCode){
            case 21:
                return context.getString(R.string.not_found_search);
            case 30:
                return context.getString(R.string.upload_successfull);
            case 31:
                return context.getString(R.string.error_when_save);
            default:
                return context.getString(R.string.error_unknow);
        }
    }
}
