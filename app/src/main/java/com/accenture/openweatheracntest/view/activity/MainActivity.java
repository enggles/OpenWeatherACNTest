package com.accenture.openweatheracntest.view.activity;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.accenture.openweatheracntest.R;
import com.accenture.openweatheracntest.view.fragment.MainListFragment;

public class MainActivity extends AppCompatActivity {

    private FragmentManager supportFragmentManager;
    private MainListFragment mainListFragment;
    private Boolean listAdded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        supportFragmentManager = getSupportFragmentManager();
        mainListFragment = MainListFragment.newInstance("1", "2");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!listAdded) {
            supportFragmentManager
                    .beginTransaction()
                    .add(R.id.frame_container_list, mainListFragment)
                    .commit();
            listAdded = true;
        }
    }
}
