package com.example.ahmed.sunshine;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.ahmed.sunshine.data.WeatherContract;
import com.example.ahmed.sunshine.sync.SunshineSyncAdapter;

public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();

    private static final int FORECAST_LOADER = 0;

    private boolean useTodayLayout = true;

    //private String oldLocation;

    //ArrayAdapter<String> arrayAdapter;
    private ForecastAdapter forecastAdapter;

    private ListView listView;

    //    private int selectedPosition = ListView.INVALID_POSITION;
    private int selectedPosition = 0;

    private static final String SELECTED_POSITION_KEY = "selected_position";

    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

// --Commented out by Inspection START (2018-03-08 4:37 AM):
//    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
//    // must change.
//    static final int COL_WEATHER_ID = 0;
// --Commented out by Inspection STOP (2018-03-08 4:37 AM)
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    // --Commented out by Inspection (2018-03-08 4:37 AM):static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    private static final int COL_COORD_LAT = 7;
    private static final int COL_COORD_LONG = 8;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri dateUri);
    }

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

//        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Sunshine", Context.MODE_PRIVATE);

        //String[] arrayOfFakeForecasts = {"Today - Sunny - 88/63", "Tomorrow - Foggy - 70/46", "Weds - Cloudy - 72/63",
        //        "Thurs - Rainy - 64/51", "Fri - Foggy - 70/46", "Sat - Sunny - 76/68"};

        //List<String> fakeForecasts = new ArrayList<>(Arrays.asList(arrayOfFakeForecasts));

        //arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textView, new ArrayList<String>());


        // The CursorAdapter will take data from our cursor and populate the ListView
        // However, we cannot use FLAG_AUTO_REQUERY since it is deprecated, so we will end
        // up with an empty list the first time we run.

        // The CursorAdapter will take data from our cursor and populate the ListView.

        //oldLocation = Utility.getPreferredLocation(getContext());

        // The ForecastAdapter will take data from a source and
        // use it to populate the ListView it's attached to.
        forecastAdapter = new ForecastAdapter(getActivity());
        forecastAdapter.setUseTodayLayout(useTodayLayout);

        listView = (ListView) rootView.findViewById(R.id.listView_forecast);
        listView.setAdapter(forecastAdapter);

        //if (!useTodayLayout)
        //    listView.setItemChecked(0, true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);

                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getContext());

                    ((Callback) getActivity()).onItemSelected(
                            WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting,
                                    cursor.getLong(COL_WEATHER_DATE)
                            )
                    );
                }

                selectedPosition = position;
            }
        });
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                Intent intent = new Intent(getContext(), DetailsActivity.class);
//                intent.putExtra("forecast", arrayAdapter.getItem(i));
//                startActivity(intent);
//            }
//        });

//        if (sharedPreferences.getBoolean("isFirstRun", true)) {
//            updateWeather();
//            //getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
//            sharedPreferences.edit().putBoolean("isFirstRun", false).apply();
//        }

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_POSITION_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            selectedPosition = savedInstanceState.getInt(SELECTED_POSITION_KEY);
        }

        forecastAdapter.setUseTodayLayout(useTodayLayout);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (useTodayLayout)
            listView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
        else
            listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
    }

    //    @Override
//    public void onResume() {
//        super.onResume();
//
//        String location = Utility.getPreferredLocation(getContext());
//
//        // update the location in our second pane using the fragment manager
//        if (location != null && !location.equals(oldLocation)) {
//            onLocationChanged();
//            oldLocation = location;
//        }
//
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_main, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//
//            case R.id.action_refresh:
//                updateWeather();
//                break;
//
//            case R.id.action_map:
//                showPreferredLocationInMap();
//                break;
//
//            case R.id.action_settings:
//                startActivity(new Intent(getContext(), SettingsActivity.class));
//                break;
//        }
//
//        return true;
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.

        outState.putInt(SELECTED_POSITION_KEY, selectedPosition);

        super.onSaveInstanceState(outState);
    }

    // since we read the location when we create the loader, all we need to do is restart things
    void onLocationChanged(boolean firstRun) {

        if (!firstRun)
            updateWeather();

        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);

    }

    public void setUseTodayLayout(boolean useTodayLayout) {

        this.useTodayLayout = useTodayLayout;

        if (forecastAdapter != null)
            forecastAdapter.setUseTodayLayout(useTodayLayout);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                startActivity(new Intent(getContext(), SettingsActivity.class));
                return true;

            case R.id.action_map:
                openPreferredLocationInMap();
                return true;
        }

        //            case R.id.action_refresh:
//                updateWeather();
//                return true;

        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {

        // Using the URI scheme for showing a location found on a map.  This super-handy
        // intent can is detailed in the "Common Intents" page of Android's developer site:
        // http://developer.android.com/guide/components/intents-common.html#Maps
        if (forecastAdapter != null) {

            Cursor cursor = forecastAdapter.getCursor();

            if (cursor != null && cursor.moveToPosition(0)) {

                    String posLat = cursor.getString(COL_COORD_LAT);
                    String posLong = cursor.getString(COL_COORD_LONG);
                    Uri geoLocation = Uri.parse("geo:" + posLat + "," + posLong);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(geoLocation);

                    if (intent.resolveActivity(getContext().getPackageManager()) != null)
                        startActivity(intent);
                    else
                        Log.d(LOG_TAG, "Couldn't call " + geoLocation.toString() + ", no receiving apps installed!");
                }
        }
    }

    //    private void showPreferredLocationInMap() {
//
//        String location = Utility.getPreferredLocation(getContext());
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
//        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
//            startActivity(intent);
//
//        } else {
//            Toast.makeText(getContext(), "Couldn't call " + location + ", no receiving apps installed!", Toast.LENGTH_SHORT).show();
//            Log.d("com.ahmed.sunshine", "Couldn't call " + location + ", no receiving apps installed!");
//        }
//    }

    private void updateWeather() {

//        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask(getContext());
//        fetchWeatherTask.execute(Utility.getPreferredLocation(getContext()));
////////////////////////////////////////
//        Intent intent = new Intent(getContext(), SunshineService.class);
//
//        intent.putExtra(
//                SunshineService.LOCATION_QUERY_EXTRA,
//                Utility.getPreferredLocation(getContext())
//        );
//
//        getContext().startService(intent);
////////////////////////////////////////
//        Intent alarmIntent = new Intent(getActivity(), SunshineService.AlarmReceiver.class);
//        alarmIntent.putExtra(SunshineService.LOCATION_QUERY_EXTRA, Utility.getPreferredLocation(getActivity()));
//
//        //Wrap in a pending intent which only fires once.
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);//getBroadcast(context, 0, i, 0);
//
//        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
//
//        //Set the AlarmManager to wake up the system.
//        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent);

        SunshineSyncAdapter.syncImmediately(getContext());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, filter the query to return weather only for
        // dates after or including today.

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";

        String locationSetting = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        return new CursorLoader(getContext(), weatherForLocationUri, FORECAST_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        forecastAdapter.changeCursor(cursor);
        forecastAdapter.notifyDataSetChanged();

        if (!useTodayLayout) {

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    listView.setItemChecked(selectedPosition, true);
                    listView.smoothScrollToPosition(selectedPosition);

                    Cursor preSelectedItemCursor = (Cursor) listView.getAdapter().getItem(selectedPosition);

                    if (preSelectedItemCursor != null && preSelectedItemCursor.moveToFirst()) {
                        String locationSetting = Utility.getPreferredLocation(getContext());

                        ((Callback) getActivity()).onItemSelected(
                                WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                        locationSetting,
                                        preSelectedItemCursor.getLong(COL_WEATHER_DATE)
                                )
                        );
                    }
                }
            });

//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            });

        }

    }

    //OnDestroyed
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        forecastAdapter.changeCursor(null);
    }

//    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
//
//        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
//
//        @Override
//        protected String[] doInBackground(String... params) {
//
//            // If there's no zip code, there's nothing to look up.  Verify size of params.
//            if (params.length < 1)
//                return null;
//
//            // These two need to be declared outside the try/catch
//            // so that they can be closed in the finally block.
//            HttpURLConnection urlConnection = null;
//            BufferedReader bufferedReader = null;
//
//            // Will contain the raw JSON response as a string.
//            String forecastJsonStr = null;
//
//            String format = "json";
//            String units = "metric";
//            int numDays = 7;
//
//            try {
//                // Construct the URL for the OpenWeatherMap query
//                // Possible parameters are available at OWM's forecast API page, at
//                // http://openweathermap.org/API#forecast
//
//                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?id=357994&mode=json&units=metric&cnt=7&appid=b972b37925957200930b2c12e1f7e3a3");
//
//                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
//                final String QUERY_PARAM = "id";
//                //final String COUNTRY_PARAM = ",";
//                final String FORMAT_PARAM = "mode";
//                final String UNITS_PARAM = "units";
//                final String DAYS_PARAM = "cnt";
//                final String APPID_PARAM = "APPID";
//
//                //.appendQueryParameter(COUNTRY_PARAM, params[1])
//                Uri uri = Uri.parse(FORECAST_BASE_URL).buildUpon()
//                        .appendQueryParameter(QUERY_PARAM, params[0])
//                        .appendQueryParameter(FORMAT_PARAM, format)
//                        .appendQueryParameter(UNITS_PARAM, units)
//                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
//                        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
//                        .build();
//
//                URL url = new URL(uri.toString());
//
//                Log.v(LOG_TAG, "URI " + uri.toString());
//
//                // Create the request to OpenWeatherMap, and open the connection
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                // Read the input stream into a String
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuilder stringBuilder = new StringBuilder();
//                if (inputStream == null) {
//                    // Nothing to do.
//                    return null;
//                }
//                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = bufferedReader.readLine()) != null) {
//                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                    // But it does make debugging a *lot* easier if you print out the completed
//                    // buffer for debugging.
//                    stringBuilder.append(line).append("\n");
//                }
//
//                if (stringBuilder.length() == 0) {
//                    // Stream was empty.  No point in parsing.
//                    return null;
//                }
//                forecastJsonStr = stringBuilder.toString();
//
//                Log.v(LOG_TAG, "Forecast string: " + forecastJsonStr);
//
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Error ", e);
//                // If the code didn't successfully get the weather data, there's no point in attempting
//                // to parse it.
//                return null;
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (bufferedReader != null) {
//                    try {
//                        bufferedReader.close();
//                    } catch (final IOException e) {
//                        Log.e(LOG_TAG, "Error closing stream", e);
//                    }
//                }
//            }
//
//            try {
//                return getWeatherDataFromJson(forecastJsonStr, numDays);
//
//            } catch (JSONException e) {
//                Log.e(LOG_TAG, e.getMessage(), e);
//                e.printStackTrace();
//            }
//
//            // This will only happen if there was an error getting or parsing the forecast.
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String[] strings) {
//            if (strings != null) {
//                arrayAdapter.clear();
//                for (String dayForecastStr : strings) {
//                    arrayAdapter.add(dayForecastStr);
//                }
//                // New data is back from the server.
//                //arrayAdapter.notifyDataSetChanged(); this method is called internally by the add method to notify the listView
//                //that the data in the adapter has been updated which can be turned off using this method:
//                //arrayAdapter.setNotifyOnChange(false);
//            }
//        }
//
//        /**
//         * Prepare the weather high/lows for presentation.
//         */
//        private String formatHighLows(double high, double low, String unitType) {
//
//            if (unitType.equals("imperial")) {
//                high = (high * 1.8) + 32;
//                low = (low * 1.8) + 32;
//            } else if (!unitType.equals("metric")) {
//                Log.e("com.ahmed.sunshine", "Unit type not found: " + unitType);
//            }
//
//            // For presentation, assume the user doesn't care about tenths of a degree.
//            long roundedHigh = Math.round(high);
//            long roundedLow = Math.round(low);
//
//            return roundedHigh + "/" + roundedLow;
//        }
//
//        /**
//         * Take the String representing the complete forecast in JSON Format and
//         * pull out the data we need to construct the Strings needed for the wireframes.
//         * <p/>
//         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
//         * into an Object hierarchy for us.
//         */
//        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays) throws JSONException {
//
//            //These are the names of the JSON objects that need to be extracted.
//            final String OWM_LIST = "list";
//            final String OWM_WEATHER = "weather";
//            final String OWM_TEMPERATURE = "temp";
//            final String OWM_MAX = "max";
//            final String OWM_MIN = "min";
//            final String OWM_DESCRIPTION = "main";
//
//            JSONObject forecastJson = new JSONObject(forecastJsonStr);
//            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
//
//            // OWM returns daily forecasts based upon the local time of the city that is being
//            // asked for, which means that we need to know the GMT offset to translate this data
//            // properly.
//
//            // Since this data is also sent in-order and the first day is always the
//            // current day, we're going to take advantage of that to get a nice
//            // normalized UTC date for all of our weather.
//
////            Time dayTime = new Time();
////            dayTime.setToNow();
////
////            // we start at the day returned by local time. Otherwise this is a mess.
////            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
////
////            // now we work exclusively in UTC
////            dayTime = new Time();
//
//            String[] resultStrs = new String[numDays];
//
//            String unitType = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("units", "metric");
//
//            for (int i = 0; i < weatherArray.length(); i++) {
//                // For now, using the format "Day, description, hi/low"
//                String day;
//                String description;
//                String highAndLow;
//
//                // Get the JSON object representing the day
//                JSONObject dayForecast = weatherArray.getJSONObject(i);
//
//                //create a Gregorian Calendar, which is in current date
//                GregorianCalendar gregorianCalendar = new GregorianCalendar();
//                //add i dates to current date of calendar
//                gregorianCalendar.add(GregorianCalendar.DATE, i);
//                //get that date, format it, and "save" it on variable day
//                Date time = gregorianCalendar.getTime();
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd", Locale.getDefault());
//                day = simpleDateFormat.format(time);
//
////                // The date/time is returned as a long.  We need to convert that
////                // into something human-readable, since most people won't read "1400356800" as
////                // "this saturday".
////                long dateTime;
////                // Cheating to convert this to UTC time, which is what we want anyhow
////                dateTime = dayTime.setJulianDay(julianStartDay + i);
////                day = getReadableDateString(dateTime);
//
//                // description is in a child array called "weather", which is 1 element long.
//                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
//                description = weatherObject.getString(OWM_DESCRIPTION);
//
//                // Temperatures are in a child object called "temp".  Try not to name variables
//                // "temp" when working with temperature.  It confuses everybody.
//                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
//                double high = temperatureObject.getDouble(OWM_MAX);
//                double low = temperatureObject.getDouble(OWM_MIN);
//
//                highAndLow = formatHighLows(high, low, unitType);
//                resultStrs[i]   = day + " - " + description + " - " + highAndLow;
//            }
//
//            for (String s : resultStrs) {
//                Log.v(LOG_TAG, "Forecast entry: " + s);
//            }
//            return resultStrs;
//        }
//
//        /*
//        private double getMaxTemperatureForDay(String weatherJsonStr, int dayIndex) throws JSONException {
//            JSONObject weatherJsonObject = new JSONObject(weatherJsonStr);
//            JSONArray daysJsonArray = weatherJsonObject.getJSONArray("list");
//            JSONObject dayJsonObject = daysJsonArray.getJSONObject(dayIndex);
//            JSONObject temperatureJsonObject = dayJsonObject.getJSONObject("temp");
//
//            return temperatureJsonObject.getDouble("max");
//        }*/
//
//        /* The date/time conversion code is going to be moved outside the asynctask later,
//        * so for convenience we're breaking it out into its own method now.
//        */
//        /*
//        private String getReadableDateString(long time) {
//            // Because the API returns a unix timestamp (measured in seconds),
//            // it must be converted to milliseconds in order to be converted to valid date.
//
//            Locale defaultLocale = Locale.getDefault();
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd", defaultLocale);
//            return simpleDateFormat.format(time);
//
//        }
//        */
//    }
}
