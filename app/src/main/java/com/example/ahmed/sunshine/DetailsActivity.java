package com.example.ahmed.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ahmed.sunshine.data.WeatherContract;
import com.example.ahmed.sunshine.sync.SunshineSyncAdapter;

public class DetailsActivity extends AppCompatActivity {

    private String oldLocation;
    private Boolean oldIsMetric;
    private Uri uri;

    private static final String DETAILS_FRAGMENT_TAG = "DETAILSTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        oldLocation = Utility.getPreferredLocation(this);
        oldIsMetric = Utility.isMetric(this);

        uri = getIntent().getData();

        if (savedInstanceState == null) {

            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailsFragment.DETAIL_URI, uri);

            DetailsFragment fragment = new DetailsFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, fragment, DETAILS_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        String location = Utility.getPreferredLocation(this);
        Boolean isMetric = Utility.isMetric(this);

        // update the location in our second pane using the fragment manager
        if ((location != null && !location.equals(oldLocation)) || (isMetric != oldIsMetric)) {

            // replace the uri, since the location has changed

            updateWeather();

            if (uri != null) {

                long date = WeatherContract.WeatherEntry.getDateFromUri(uri);

                //Updated Uri
                uri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(location, date);

                Bundle arguments = new Bundle();
                arguments.putParcelable(DetailsFragment.DETAIL_URI, uri);

                DetailsFragment fragment = new DetailsFragment();
                fragment.setArguments(arguments);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, fragment, DETAILS_FRAGMENT_TAG)
                        .commit();

                oldLocation = location;
                oldIsMetric = isMetric;
            }
        }
    }

    private void updateWeather() {
        SunshineSyncAdapter.syncImmediately(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.details_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

