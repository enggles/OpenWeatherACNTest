package com.accenture.openweatheracntest.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.accenture.openweatheracntest.BuildConfig;
import com.accenture.openweatheracntest.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "OpenWeatherAcn";

    private TextView buildType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        buildType = findViewById(R.id.activity_splash_build);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (BuildConfig.DEBUG) {
            buildType.setText("DEBUG");
        } else {
            buildType.setText("PRODUCTION");
        }

        Observable<? extends Long> timerDisposable = Observable.timer(2000,
                TimeUnit.MILLISECONDS, Schedulers.io());
        timerDisposable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        onNext -> {
                            openMainActivity();
                        }
                );
    }

    private void openMainActivity() {
        Intent intent = new Intent();
        intent.setClassName(getApplicationContext(),
                "com.accenture.openweatheracntest.view.activity.MainActivity");
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
