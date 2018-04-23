package com.accenture.openweatheracntest.view.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accenture.openweatheracntest.R;
import com.accenture.openweatheracntest.common.Globals;
import com.accenture.openweatheracntest.common.api.OWService;
import com.accenture.openweatheracntest.model.Coord;
import com.accenture.openweatheracntest.model.Main;
import com.accenture.openweatheracntest.model.WeatherResponse;
import com.accenture.openweatheracntest.model.Wind;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherDetailActivity extends AppCompatActivity {

    protected WeatherResponse weatherResponse;
    private OWService service;

    private TextView detailName;
    private ImageView detailIcon;

    private TextView temp;
    private TextView pressure;
    private TextView humidity;
    private TextView tempMin;
    private TextView tempMax;

    private TextView lon;
    private TextView lat;

    private TextView speed;
    private TextView deg;

    private MaterialFancyButton refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        Bundle bundle = getIntent().getExtras();
        weatherResponse = bundle.getParcelable("weather");

        detailName = findViewById(R.id.activity_weather_detail_name);
        detailIcon = findViewById(R.id.activity_weather_detail_icon);

        temp = findViewById(R.id.activity_weather_detail_temp);
        pressure = findViewById(R.id.activity_weather_detail_pressure);
        humidity = findViewById(R.id.activity_weather_detail_humidity);
        tempMin = findViewById(R.id.activity_weather_detail_temp_min);
        tempMax = findViewById(R.id.activity_weather_detail_temp_max);

        lon = findViewById(R.id.activity_weather_detail_coords_lon);
        lat = findViewById(R.id.activity_weather_detail_coords_lat);

        speed = findViewById(R.id.activity_weather_detail_wind_speed);
        deg = findViewById(R.id.activity_weather_detail_winds_deg);

        refresh = findViewById(R.id.activity_weather_detail_refresh);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.apiEndpoint)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(OWService.class);
    }

    @Override
    protected void onStart() {
        super.onStart();

        detailName.setText(weatherResponse.getName());

        String iconCode = weatherResponse.getWeather().get(0).getIcon();
        Picasso.get().load("http://openweathermap.org/img/w/" + iconCode + ".png").into(detailIcon);

        updateValues(weatherResponse);

        refresh.setOnClickListener((View v) -> {
            Call<WeatherResponse> weatherResponse = service.weather(
                    this.weatherResponse.getId().toString(),
                    Globals.appId);

            Runnable getWeatherResponse = () -> {
                try {
                    WeatherResponse response = weatherResponse.execute().body();
                    if (response != null) {
                        runOnUiThread(() -> {
                            updateValues(response);
                        });
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            };
            new Thread(getWeatherResponse).start();
        });
    }

    private void updateValues(WeatherResponse weatherResponse) {
        Main main = weatherResponse.getMain();
        temp.setText(main.getTemp().toString());
        pressure.setText(main.getPressure().toString());
        humidity.setText(main.getHumidity().toString());
        tempMin.setText(main.getTempMin().toString());
        tempMax.setText(main.getTempMax().toString());

        Coord coord = weatherResponse.getCoord();
        lon.setText(coord.getLon().toString());
        lat.setText(coord.getLat().toString());

        Wind wind = weatherResponse.getWind();
        speed.setText(wind.getSpeed().toString());
        deg.setText(wind.getDeg().toString());
    }
}
