package com.myapp.reminderapp.alertORToast;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class AlertOrToast {
    Context context;
    public AlertOrToast(Context context) {
        this.context = context;
    }

    public AlertDialog showAlert(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok",null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }

    public void toastMessage(String Message){
        Toast.makeText(context,Message,Toast.LENGTH_SHORT).show();
    }
}
