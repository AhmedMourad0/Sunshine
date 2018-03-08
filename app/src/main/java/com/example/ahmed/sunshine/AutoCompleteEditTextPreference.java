package com.example.ahmed.sunshine;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AutoCompleteEditTextPreference extends EditTextPreference {

    private static String[] list;
    private boolean isValid = true;

    public AutoCompleteEditTextPreference(Context context) {
        super(context);
    }

    public AutoCompleteEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoCompleteEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * the default EditTextPreference does not make it easy to
     * use an AutoCompleteEditTextPreference field. By overriding this method
     * we perform surgery on it to use the type of edit field that
     * we want.
     */
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        // find the current EditText object
        final EditText editText = (EditText) view.findViewById(android.R.id.edit);
        // copy its layout params
        ViewGroup.LayoutParams params = editText.getLayoutParams();
        ViewGroup vg = (ViewGroup) editText.getParent();
        String curVal = editText.getText().toString();
        // remove it from the existing layout hierarchy
        vg.removeView(editText);

        // construct a new editable autocomplete object with the appropriate params
        // and id that the TextEditPreference is expecting
        mACTV = new AutoCompleteTextView(getContext());
        mACTV.setLayoutParams(params);
        mACTV.setId(android.R.id.edit);
        mACTV.setText(curVal);
        mACTV.setThreshold(1);

        isValid  = isValid(mACTV.getText().toString());

        mACTV.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isValid = isValid(s.toString());
                validate();
            }
        });

        mACTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValid = isValid(mACTV.getText().toString());
                validate();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, list);
        mACTV.setAdapter(adapter);

        // add the new view to the layout
        vg.addView(mACTV);
    }

    private boolean isValid(CharSequence text) {
        return !text.equals("") && Arrays.binarySearch(list, text.toString(), String.CASE_INSENSITIVE_ORDER) > 0;
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);
        mACTV.setSelection(mACTV.getText().length());
    }

    private void validate() {
        Dialog dialog = getDialog();

        if (dialog instanceof AlertDialog) {
            Button btn = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            btn.setEnabled(isValid);
        }
    }

    /**
     * Because the baseclass does not handle this correctly
     * we need to query our injected AutoCompleteTextView for
     * the value to save
     */
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult && mACTV != null) {

            String value = mACTV.getText().toString();

            if (callChangeListener(value))
                setText(value);
        }
    }

    static String[] prepareCountriesList(Context context) {
//
//        BufferedReader bufferedReader = null;
//
//        try {
//
//            bufferedReader = new BufferedReader(new FileReader(context.getCacheDir()+"/cities.txt"));
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        List<String> lines = new ArrayList<>();
//
//        String line;
//
//        try {
//
//            if (bufferedReader != null) {
//                while((line = bufferedReader.readLine()) != null)
//                    lines.add(line);
//
//                bufferedReader.close();
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        Set<String> lines = new HashSet<>();

        try {

            InputStream inputStream = context.getAssets().open("cities.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (!line.equals("-"))
                lines.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        list = lines.toArray(new String[lines.size()]);

//        Arrays.stream(list).distinct();
        Arrays.sort(list, String.CASE_INSENSITIVE_ORDER);

        return list;
    }

    /**
     * again we need to override methods from the base class
     */
    public EditText getEditText() {
        return mACTV;
    }

    private AutoCompleteTextView mACTV = null;
}
