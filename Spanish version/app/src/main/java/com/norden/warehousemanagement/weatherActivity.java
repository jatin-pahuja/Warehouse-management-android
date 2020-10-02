package com.norden.warehousemanagement;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import cz.msebera.android.httpclient.Header;

public class weatherActivity extends AppCompatActivity {

    Button btnSearchWeather;
    EditText edtCityWeather;
    ConstraintLayout constraintLayout;
    TextView tvTempCelsius, tvMaxTemp, tvMinTemp, tvRealFeel, tvDescription, tvCity;
    TextView tvMaxTemp_D, tvMinTemp_D, tvRealFeel_D;
    ImageView ivWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Temps actual");

        // Polítca que vaig aconseguir per a que em deixés mostrar la imatge desde una URL
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        constraintLayout = findViewById(R.id.constraintLayout);
        edtCityWeather = findViewById(R.id.edtCityWeather);
        tvTempCelsius = findViewById(R.id.tvTempCelsius);
        tvMaxTemp = findViewById(R.id.tvMaxTemp);
        tvMinTemp = findViewById(R.id.tvMinTemp);
        tvRealFeel = findViewById(R.id.tvRealFeel);
        tvDescription = findViewById(R.id.tvDescription);
        tvCity = findViewById(R.id.tvCity);
        tvMaxTemp_D = findViewById(R.id.tvMaxTemp_D);
        tvMinTemp_D = findViewById(R.id.tvMinTemp_D);
        tvRealFeel_D = findViewById(R.id.tvRealFeel_D);
        ivWeather = findViewById(R.id.ivWeather);

        // Amagar els TextViews
        hideTextViews();

        // Carregar i mostrar llista de ciutats quan es clica en el EditText i s'introdueix caràcters
        ArrayAdapter<String> adapter = null;
        try {
            adapter = new ArrayAdapter<>(this,android.R.layout.select_dialog_item, AllCities());
            AutoCompleteTextView acTextView = findViewById(R.id.edtCityWeather);
            acTextView.setThreshold(1);
            acTextView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnSearchWeather = (Button) findViewById(R.id.btnSearchWeather);
        btnSearchWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Carregar dades de la API
                chargeWeatherAPI();
            }
        });
    }

    public void chargeWeatherAPI() {
        final ProgressDialog Dialog = new ProgressDialog(this);
        Dialog.setCancelable(false);
        Dialog.setCanceledOnTouchOutside(false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setMaxRetriesAndTimeout(0,10000);

        String City = edtCityWeather.getText().toString();
        String Url = "http://api.openweathermap.org/data/2.5/weather?q=" + City + "&APPID=c56998ed0c8e889a77a182e7b74eade4&lang=ca";

        client.get(this, Url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                Dialog.setMessage("Carregant dades...");
                Dialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Dialog.setMessage("Processant dades...");

                JSONObject weatherData = null;
                String str = new String(responseBody);

                try {
                    weatherData = new JSONObject(str);

                    // Instanciem i omplim un objecte amb les dades rebudes
                    cityWeather cityWeather = new cityWeather(
                            weatherData.getString("name") + ", " + weatherData.getJSONObject("sys").getString("country"),
                            weatherData.getJSONObject("main").getDouble("temp"),
                            weatherData.getJSONObject("main").getDouble("temp_max"),
                            weatherData.getJSONObject("main").getDouble("temp_min"),
                            weatherData.getJSONObject("main").getDouble("feels_like"),
                            weatherData.getJSONArray("weather").getJSONObject(0).getString("description"),
                            weatherData.getJSONArray("weather").getJSONObject(0).getString("icon")
                    );

                    // Mostrem les dades rebudes en pantalla
                    tvCity.setText(cityWeather.getCityName());
                    tvTempCelsius.setText((int)cityWeather.getTemperature()+"°");
                    tvMaxTemp.setText((int)cityWeather.getMaxTemp()+"°");
                    tvMinTemp.setText((int)cityWeather.getMinTemp()+"°");
                    tvRealFeel.setText((int)cityWeather.getRealFeelTemp()+"°");
                    tvDescription.setText(cityWeather.getDescription());

                    // S'obté la imatge a partir de la API enviant la id de la icona
                    ivWeather.setImageBitmap(WeatherIconApi(cityWeather.getWeatherIcon()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Mostrem els TextViews
                revealTextViews();

                constraintLayout.setBackground(getDrawable(R.drawable.weather));

                Dialog.hide();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String str = error.getMessage();
                String message = "No s'ha pogut carregar les dades del servidor. " + str;

                Dialog.hide();

                myDialogs.showShortSnackbar(constraintLayout, message);
            }

        });
    }

    public Bitmap WeatherIconApi(String weatherIconId) {
        URL url = null;
        Bitmap bmp = null;

        try {
            url = new URL("https://openweathermap.org/img/wn/"+ weatherIconId +"@2x.png");
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bmp;
    }

    public ArrayList<String> AllCities() throws JSONException {
        Resources res = getResources();
        InputStream is = res.openRawResource(R.raw.cities);
        Scanner sc = new Scanner(is);
        StringBuilder sb = new StringBuilder();

        while (sc.hasNextLine()) {
            sb.append(sc.nextLine());
        }

        JSONArray jsonArray = new JSONArray(sb.toString());
        ArrayList<String> cities = new ArrayList<String>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = (JSONObject) jsonArray.get(i);
            cities.add(jsonObject.getString("name"));
        }

        return cities;
    }

    public void hideTextViews() {
        tvTempCelsius.setVisibility(View.GONE);
        tvMaxTemp.setVisibility(View.GONE);
        tvMinTemp.setVisibility(View.GONE);
        tvRealFeel.setVisibility(View.GONE);
        tvDescription.setVisibility(View.GONE);
        tvMaxTemp_D.setVisibility(View.GONE);
        tvMinTemp_D.setVisibility(View.GONE);
        tvRealFeel_D .setVisibility(View.GONE);
        ivWeather.setVisibility(View.GONE);
        tvCity.setVisibility(View.GONE);
    }

    public void revealTextViews() {
        tvTempCelsius.setVisibility(View.VISIBLE);
        tvMaxTemp.setVisibility(View.VISIBLE);
        tvMinTemp.setVisibility(View.VISIBLE);
        tvRealFeel.setVisibility(View.VISIBLE);
        tvDescription.setVisibility(View.VISIBLE);
        tvMaxTemp_D.setVisibility(View.VISIBLE);
        tvMinTemp_D.setVisibility(View.VISIBLE);
        tvRealFeel_D .setVisibility(View.VISIBLE);
        ivWeather.setVisibility(View.VISIBLE);
        tvCity.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
