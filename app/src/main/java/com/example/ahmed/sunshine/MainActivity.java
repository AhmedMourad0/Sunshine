package com.example.ahmed.sunshine;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.ahmed.sunshine.sync.SunshineSyncAdapter;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback {

    //TODO: use accounts to store user preferences over different devices
    //TODO: change switch preference on color to blue
    //TODO: add setting of how many days he wants to retrieve --> {"cod":"400","message":"cnt from 1 to 17"}
    //http://api.openweathermap.org/data/2.5/forecast/daily?id=357994&mode=json&units=metric&cnt=50&appid=b972b37925957200930b2c12e1f7e3a3

    private static final String DETAILS_FRAGMENT_TAG = "DFTAG";
    // --Commented out by Inspection (2018-03-08 4:37 AM):private final String LOG_TAG = MainActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    private boolean twoPane, isValid;
    private boolean isFirstRun = true;
    private String oldLocation;
    private Boolean oldIsMetric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        oldLocation = Utility.getPreferredLocation(this);
        oldIsMetric = Utility.isMetric(this);

//        CountriesDbHelper countriesDbHelper = new CountriesDbHelper(getApplicationContext());
//        countriesDbHelper.openDataBase();
//
//        String[] countries = countriesDbHelper.getCountryNames();
//        AutoCompleteEditTextPreference.list = countries;
//        Toast.makeText(getApplicationContext(), countries.length + "", Toast.LENGTH_SHORT).show();

        sharedPreferences = getSharedPreferences("SunShineData", Context.MODE_PRIVATE);

        isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);

        setContentView(R.layout.activity_main);

        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            twoPane = true;

            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.

        } else {

            twoPane = false;

            if (getSupportActionBar() != null)
                getSupportActionBar().setElevation(0f);
        }

        if (isFirstRun) {

            displayCountryDialog(AutoCompleteEditTextPreference.prepareCountriesList(getApplicationContext()));

        } else {

            AutoCompleteEditTextPreference.prepareCountriesList(getApplicationContext());

            setUpUi();

            if (savedInstanceState == null && twoPane) {

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailsFragment(), DETAILS_FRAGMENT_TAG)
                        .commit();
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isFirstRun) {

            String location = Utility.getPreferredLocation(this);
            Boolean isMetric = Utility.isMetric(this);

            // update the location in our second pane using the fragment manager
            if ((location != null && !location.equals(oldLocation)) || (isMetric != oldIsMetric)) {

                Fragment mainActivityFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);


                ((ForecastFragment) mainActivityFragment).onLocationChanged(isFirstRun);

                getSupportFragmentManager().beginTransaction()
                        .detach(mainActivityFragment)
                        .attach(mainActivityFragment)
                        .commit();

                if (twoPane)
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.weather_detail_container, new DetailsFragment(), DETAILS_FRAGMENT_TAG)
                            .commit();

                oldLocation = location;
                oldIsMetric = isMetric;

            }
        }
    }
//
//        String location = Utility.getPreferredLocation(getApplicationContext());
//
//        /* Using the URI scheme for showing a location found on a map. This super-handy intent is
//         detailed in the "Common Intents" page of Android's developer site:
//         http://developer.android.com/guide/components/intents-common.html#Maps*/
//
//        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
//                .appendQueryParameter("q", location)
//                .build();
//
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(geoLocation);
//
//        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
//            startActivity(intent);
//
//        } else {
//            Toast.makeText(getApplicationContext(), "Couldn't call " + location + ", no receiving apps installed!", Toast.LENGTH_SHORT).show();
//            Log.d("com.ahmed.sunshine", "Couldn't call " + location + ", no receiving apps installed!");

//    public void updateWeather() {
//        SunshineSyncAdapter.syncImmediately(getApplicationContext());
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        String location = Utility.getPreferredLocation(this);
//        Boolean isMetric = Utility.isMetric(this);
//        // update the location in our second pane using the fragment manager
//
//        if ((location != null && !location.equals(oldLocation)) || (isMetric != oldIsMetric)) {
//
//            ForecastFragment mainActivityFragment = (ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);
//            DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager().findFragmentByTag(DETAILS_FRAGMENT_TAG);
//
//            if (mainActivityFragment != null && detailsFragment != null) {
//
//                if (location != null && !location.equals(oldLocation)) {
//
//                    mainActivityFragment.onLocationChanged();
//
//                    detailsFragment.onLocationChanged(location);
//
//                    oldLocation = location;
//                }
//
//                if (isMetric != oldIsMetric) {
//
//                    mainActivityFragment.onUnitChanged();
//
//                    detailsFragment.onUnitChanged();
//
//                    oldIsMetric = isMetric;
//                }
//            }
//        }
//    }

    private void setUpUi() {
        SunshineSyncAdapter.initializeSyncAdapter(this);

        SunshineSyncAdapter.notifyWeather(getApplicationContext());

        ((ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast)).setUseTodayLayout(!twoPane);
    }

    @Override
    public void onItemSelected(Uri contentUri) {

        if (twoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();

            args.putParcelable(DetailsFragment.DETAIL_URI, contentUri);

            DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, fragment, DETAILS_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class).setData(contentUri);
            startActivity(intent);
        }
    }

    private void displayCountryDialog(final String[] array) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Set your location:");
        builder.setNegativeButton(null, null);

        final AutoCompleteTextView autoCompleteTextView = new AutoCompleteTextView(this);

        autoCompleteTextView.setAdapter(new AutoCompleteCustomArrayAdapter(MainActivity.this, array));

        autoCompleteTextView.setThreshold(1);

        autoCompleteTextView.setDropDownBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.white)));

        FrameLayout frameLayout = new FrameLayout(getApplicationContext());

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(dipToPixels(16), dipToPixels(8), dipToPixels(16), dipToPixels(8));

        autoCompleteTextView.setLayoutParams(layoutParams);

        frameLayout.addView(autoCompleteTextView);

        builder.setView(frameLayout);

        final AlertDialog alertDialog = builder.create();

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);

        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "SET", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PreferenceManager.
                        getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .putString("location", autoCompleteTextView.getText().toString())
                        .apply();

                sharedPreferences.edit().putBoolean("isFirstRun", false).apply();
                isFirstRun = false;

                Fragment mainActivityFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_forecast);

                ((ForecastFragment) mainActivityFragment).onLocationChanged(isFirstRun);

                getSupportFragmentManager().beginTransaction()
                        .detach(mainActivityFragment)
                        .attach(mainActivityFragment)
                        .commit();

                setUpUi();
            }
        });

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                isValid = isValid(array, s.toString());
                validate(alertDialog.getButton(DialogInterface.BUTTON_POSITIVE));
            }
        });

        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValid = isValid(array, autoCompleteTextView.getText().toString());
                validate(alertDialog.getButton(DialogInterface.BUTTON_POSITIVE));
            }
        });

        alertDialog.show();

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColorStateList(getApplicationContext(), R.color.dialog_state_color_selector));
    }

    private boolean isValid(String[] array, String text) {
        return !text.equals("") && Arrays.binarySearch(array, text, String.CASE_INSENSITIVE_ORDER) > 0;
    }

    private void validate(Button yesButton) {
        yesButton.setEnabled(isValid);
    }

    private int dipToPixels(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }
}

