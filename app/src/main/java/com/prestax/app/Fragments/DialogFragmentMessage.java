package com.prestax.app.Fragments;


import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.prestax.app.Networking.RESULTCODE;
import com.prestax.app.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogFragmentMessage extends DialogFragment  implements OnClickListener{

    public static final String TAG = "DialogFragmentMessage";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_TYPE = "type";

    public static DialogFragmentMessage newInstance(String message, boolean type){
        DialogFragmentMessage dialogFragmentMessage = new DialogFragmentMessage();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MESSAGE, message);
        bundle.putBoolean(ARG_TYPE, type);
        dialogFragmentMessage.setArguments(bundle);
        return dialogFragmentMessage;
    }

    public DialogFragmentMessage() {  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        String message = arguments.getString(ARG_MESSAGE);
        boolean type = arguments.getBoolean(ARG_TYPE);

        View view = inflater.inflate(R.layout.fragment_dialog_fragment_message, container, false);
        Button btn_close = (Button) view.findViewById(R.id.btn_close_dialog_message);
        btn_close.setOnClickListener(this);

        TextView txv_message = (TextView) view.findViewById(R.id.txv_message_service);
        ImageView image_response = (ImageView) view.findViewById(R.id.imgv_image_response);

        txv_message.setText(message);
        if (type)
            image_response.setImageResource(R.drawable.ic_ok);
        else
            image_response.setImageResource(R.drawable.ic_warning);

        getDialog().requestWindowFeature(STYLE_NO_TITLE);
        setCancelable(false);

        return view;

    }

    @Override
    public void onClick(View view) {
        dismiss();
    }

}

