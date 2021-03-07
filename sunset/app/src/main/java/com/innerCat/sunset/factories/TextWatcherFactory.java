package com.innerCat.sunset.factories;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class TextWatcherFactory {
    public static TextWatcher getNonEmptyTextWatcher( EditText input, Button okButton ) {
       return new TextWatcher() {
           @Override
           public void beforeTextChanged( CharSequence s, int start, int count, int after ) {}

           @Override
           public void onTextChanged( CharSequence s, int start, int before, int count ) {
               if (input.getText().toString().trim().length() > 0) {
                   okButton.setEnabled(true);
               } else {
                   okButton.setEnabled(false);
               }
           }

           @Override
           public void afterTextChanged( Editable s ) {}
       };
    }
}
