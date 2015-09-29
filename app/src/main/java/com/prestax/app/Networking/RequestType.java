package com.prestax.app.Networking;

/**
 * Created by jose.sanchez on 06/09/2015.
 */
public enum RequestType {
    SEARCH_FOLIO("search"),
    UPLOAD_PICTURE("upload_baucher");

    private final String method;

    RequestType(final String method){
        this.method = method;
    }

    public String getMethod(){
        return method;
    }

}
