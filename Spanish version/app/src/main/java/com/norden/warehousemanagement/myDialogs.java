package com.norden.warehousemanagement;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.snackbar.Snackbar;

public class myDialogs {
    public static void showShortSnackbar(View layout, String text) {
        Snackbar snackbar = Snackbar.make(layout, text, Snackbar.LENGTH_LONG);

        // styling for rest of text
        View snackbarView = snackbar.getView();
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        // styling for background of snackbar
        View sbView = snackbarView;
        sbView.setBackgroundColor(Color.parseColor("#2196f3"));

        snackbar.show();
    }

    public static void showShortToast(Context cts, String text) {
        Toast.makeText(cts, text, Toast.LENGTH_SHORT).show();
    }
}
