package com.example.raultc.weather;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private TextView textView;

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String...params) {

            URL url = null;
            HttpURLConnection connection = null;

            try {

                url = new URL(params[0]);
                connection = (HttpURLConnection)url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(reader);
                StringBuilder builder = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    builder.append(line);
                }

                return builder.toString();
            }

            catch (MalformedInputException e) {
                e.printStackTrace();
            }

            catch (IOException e) {
                e.printStackTrace();
            }


            return  null;
        }

        protected void onPostExecute (String s) {

            super.onPostExecute(s);
           // Log.d("OnPostExecute", s);

            try {
                JSONObject jsonObject = new JSONObject(s);

                if (jsonObject.length() == 0){
                    Toast.makeText(getApplicationContext(), "no Json", Toast.LENGTH_LONG).show();
                }

                else {

                    if (jsonObject.getInt("cod") == 404) {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }

                    else {
                        String cityName = jsonObject.getString("name");
                        String mainWeather = "";
                        String detailWeather = "";
                        Log.d("Json>>", cityName);
                        JSONArray weatherArray = jsonObject.getJSONArray("weather");

                        for (int i = 0; i < weatherArray.length(); i++) {
                            JSONObject weather = weatherArray.getJSONObject(i);
                            mainWeather = weather.getString("main");
                            detailWeather = weather.getString("description");

                        }

                        textView.setText(cityName + ":\n" + mainWeather + "\n " + detailWeather);

                        adapter.notifyDataSetChanged();
                    }
                }

            }

            catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item);
        textView = (TextView) findViewById(R.id.cityWeather);


    }

    public void clickSearch (View view) {

        EditText input = (EditText) findViewById (R.id.userCity);

        String city = input.getText().toString();
        char[] cityArray = city.toCharArray();

        city = "";

        for (int i = 0; i < cityArray.length; i++) {

            if (cityArray[i] == ' ') {
                city += "%20";
            }

            else {
                city += cityArray[i];
            }
        }

        if (input.length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter a City", Toast.LENGTH_LONG).show();
        }

        else if (input.getText().toString().matches("[0-9]+")) {

            Toast.makeText(getApplicationContext(), "No numbers allowed", Toast.LENGTH_LONG).show();

        }

        else {
            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + city + "&&appid=b1b15e88fa797225412429c1c50c122a");
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}