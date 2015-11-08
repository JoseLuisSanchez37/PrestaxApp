package com.prestax.app.Utils;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by jose.sanchez on 08/11/2015.
 */
public class Util {

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}
