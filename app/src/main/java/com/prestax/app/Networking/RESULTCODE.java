package com.prestax.app.Networking;

import android.content.Context;

import com.prestax.app.R;

/**
 * Created by jose.sanchez on 17/09/2015.
 */
public class RESULTCODE {

    public static final int SEARCH_FOLIO_SUCCESS            = 20;
    public static final int SEARCH_FOLIO_NOT_FOUND          = 21;
    public static final int UPLOAD_BAUCHER_SUCCESS          = 30;
    public static final int UPLOAD_BAUCHER_FAILED           = 31;

    public static final boolean SUCCESS = true;
    public static final boolean FAILED = false;

    public static String getMessage(Context context, int resultCode){
        switch (resultCode){
            case SEARCH_FOLIO_SUCCESS:
                return context.getString(R.string.login_success);
            case SEARCH_FOLIO_NOT_FOUND:
                return context.getString(R.string.not_found_search);
            case UPLOAD_BAUCHER_SUCCESS:
                return context.getString(R.string.upload_successfull);
            case UPLOAD_BAUCHER_FAILED:
                return context.getString(R.string.error_when_save);
            default:
                return context.getString(R.string.error_unknow);
        }
    }
}
